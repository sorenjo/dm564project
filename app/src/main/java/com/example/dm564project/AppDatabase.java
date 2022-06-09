package com.example.dm564project;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.OffsetDateTime;

@Database(entities = {User.class, Post.class, Reaction.class, Comment.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase AppDB;

    public abstract UserDao userDao();
    public abstract PostDao postDao();
    public abstract ReactionDao reactionDao();
    public abstract CommentDao commentDao();

    /**
     * Gets the existing database instance, otherwise initialize it.
     * @param context The application context.
     * @return The database instance.
     */
    public static AppDatabase getAppDatabase(Context context){
        if (AppDB == null){
            AppDB = Room.databaseBuilder(context, AppDatabase.class, "hoply").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        //AppDB.syncDatabase();
        return AppDB;
    }

    /**
     * Sync all the relations of the database in separate threads.
     */
    public void syncDatabase(){
        //System.out.println("syncing database");
        try {
            Thread t1 = new Thread(AppDB::syncDownUpUser);
            Thread t2 = new Thread(AppDB::syncDownUpPost);
            Thread t3 = new Thread(AppDB::syncDownUpReaction);
            t1.start();
            t2.start();
            t3.start();
            t1.join();
            t2.join();
            t3.join();
        } catch( InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronizes the users relation of this database.
     */
    private void syncDownUpUser(){
        UserDao userDao = userDao();
        try{
            //download users
            getMultiApplyConsume(
                new URL("https://caracal.imada.sdu.dk/app2022/users?stamp=gt." + DBEntity.instant(userDao.latest())),
                User::ofJSONObject,
                userDao::addAll
            );
            //upload users
            userDao.unSynced().stream().map(User::toJSONObject).filter(jsonObject -> {
                boolean success = false;
                try {
                    success = postTo(new URL("https://caracal.imada.sdu.dk/app2022/users"), jsonObject) == HttpURLConnection.HTTP_CREATED;
                } catch(MalformedURLException e) {
                    e.printStackTrace();
                }
                return success;
            })
            // update timestamps of newly uploaded entities in the local database.
            .forEach(jsonObject -> {
                try {
                    getSingleApplyConsume(
                        new URL("https://caracal.imada.sdu.dk/app2022/users?id=eq."+ jsonObject.getString("id")),
                        User::ofJSONObject,
                        userDao::update
                        );
                } catch(Exception e){
                    e.printStackTrace();
                }
            });
        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Synchronizes the posts relation of this database.
     */
    private void syncDownUpPost(){
        PostDao postDao = postDao();
        // download posts
        try{
            getMultiApplyConsume(
                new URL("https://caracal.imada.sdu.dk/app2022/posts?stamp=gt." + DBEntity.instant(postDao.latest())),
                Post::ofJSONObject,
                postDao::addAll
            );
        } catch(Exception e){
            e.printStackTrace();
        }
        //upload posts
        postDao.unSynced().stream().map(Post::toJSONObject).filter(jsonObject -> {
            boolean success = false;
            try{
                success = postTo(new URL("https://caracal.imada.sdu.dk/app2022/posts"), jsonObject) == HttpURLConnection.HTTP_CREATED;
            } catch(Exception e){
                e.printStackTrace();
            }
            return success;
        })
        // update timestamps of newly uploaded entities in the local database.
        .forEach(jsonObject -> {
            try{
                getSingleApplyConsume(
                    new URL("https://caracal.imada.sdu.dk/app2022/posts?id=eq." + jsonObject.getString("id")),
                    Post::ofJSONObject,
                    postDao::update
                );
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * Synchronizes the reactions relation of this database.
     */
    private void syncDownUpReaction(){
        ReactionDao reactionDao = reactionDao();
        //upload reactions
        reactionDao.unSynced().stream().map(Reaction::toJSONObject).filter(jsonObject -> {
            boolean success = false;
            try{
                success = postTo(new URL("https://caracal.imada.sdu.dk/app2022/reactions"), jsonObject) == HttpURLConnection.HTTP_CREATED;
            } catch(Exception e){
                e.printStackTrace();
            }
            return success;
        })
        // update timestamps of newly uploaded entities in the local database.
        .forEach(jsonObject -> {
            try{
                getSingleApplyConsume(
                    new URL("https://caracal.imada.sdu.dk/app2022/reactions?user_id=eq." + jsonObject.getString("user_id") + "&post_id=eq." + jsonObject.getString("post_id")),
                    Reaction::ofJSONObject,
                    reactionDao::update
                );
            } catch(Exception e){
                e.printStackTrace();
            }
        });
        //download reactions
        try{
            getMultiApplyConsume(
                    new URL("https://caracal.imada.sdu.dk/app2022/reactions?stamp=gt." + DBEntity.instant(reactionDao.latest())),
                    Reaction::ofJSONObject,
                    reactionDao::addAll
            );
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Downloads jsonarray from given url, applies the given function on each element, accumulates that to a list then consumes it using the given consumer.
     * @param url The url to download from.
     * @param function The function to apply on each downloaded JSONObject.
     * @param consumer The consumer to consume the elements.
     */
    private <E> void getMultiApplyConsume(URL url, Function<JSONObject, E> function, Consumer<List<E>> consumer){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream is = connection.getInputStream();
            String json = new BufferedReader(new InputStreamReader( new BufferedInputStream(is))).lines().collect(Collectors.joining("\n"));
            is.close();
            connection.disconnect();
            JSONArray jsonArray = new JSONArray(json);
            List<E> elements = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++)
                elements.add(function.apply(jsonArray.getJSONObject(i)));
            consumer.accept(elements);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Gets a the first element from the given url's jsonarray and applies given function before consuming it with the given consumer.
     * @param url The url to download from.
     * @param function The function to apply on the downloaded JSONObject.
     * @param consumer The consumer to consume the element.
     */
    private <E> void getSingleApplyConsume(URL url, Function<JSONObject, E> function, Consumer<E> consumer){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            String json = new BufferedReader(new InputStreamReader( new BufferedInputStream(connection.getInputStream()))).lines().collect(Collectors.joining("\n"));
            JSONObject jsonObject = new JSONArray(json).getJSONObject(0);
            E element = function.apply(jsonObject);
            consumer.accept(element);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Perform a post with the given payload to the given url.
     * Precondition: the url is a http/s url.
     * @return the http response code.
     */
    private int postTo(URL url, JSONObject payload ) {
        int status = 0;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput( true );
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk" );
            connection.setRequestProperty( "Content-Type", "application/json");
            DataOutputStream os = new DataOutputStream( connection.getOutputStream() );
            System.out.println( payload );
            os.write( payload.toString().getBytes() );
            os.flush();
            os.close();
            System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage() );
            status = connection.getResponseCode();
            connection.disconnect();
        } catch ( IOException e ){
            e.printStackTrace();
        }
        return status;
    }
}
