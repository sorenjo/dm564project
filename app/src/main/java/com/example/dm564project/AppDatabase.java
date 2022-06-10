package com.example.dm564project;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.stream.Stream;

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
        //download users
        try{
            List<User> users = JSONObjectStream(getJSONArray(new URL("https://caracal.imada.sdu.dk/app2022/users?stamp=gt." + DBEntity.instant(userDao.latest()))))
                .map(User::ofJSONObject)
                .collect(Collectors.toList());
            userDao.addAll(users);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //upload users
        userDao.unSynced().stream()
            .map(User::toJSONObject)
            .map( jsonObject -> {
                JSONObject returnJson = new JSONObject();
                try {
                    returnJson = postTo(new URL("https://caracal.imada.sdu.dk/app2022/users"), jsonObject);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return returnJson;
            })
            .map(User::ofJSONObject)
            .forEach(userDao::update);
    }


    /**
     * Synchronizes the posts relation of this database.
     */
    private void syncDownUpPost(){
        PostDao postDao = postDao();
        // download posts
        try{
            List<Post> posts = JSONObjectStream(getJSONArray(new URL("https://caracal.imada.sdu.dk/app2022/posts?stamp=gt." + DBEntity.instant(postDao.latest()))))
                .map(Post::ofJSONObject)
                .collect(Collectors.toList());
            postDao.addAll(posts);
        } catch(Exception e){
            e.printStackTrace();
        }
        //upload posts
        postDao.unSynced().stream()
            .map(Post::toJSONObject)
            .map(jsonObject -> {
                JSONObject returnJson = new JSONObject();
                try {
                    returnJson = postTo(new URL("https://caracal.imada.sdu.dk/app2022/posts"), jsonObject);
                } catch(MalformedURLException e){
                    e.printStackTrace();
                }
                return returnJson;
            })
            .map(Post::ofJSONObject)
            .forEach(postDao::update);
    }

    /**
     * Synchronizes the reactions relation of this database.
     */
    private void syncDownUpReaction() {
        ReactionDao reactionDao = reactionDao();
        //upload reactions
        reactionDao.unSynced().stream()
            .map(Reaction::toJSONObject)
            .map(jsonObject -> {
                JSONObject returnJson = new JSONObject();
                try{
                    returnJson = postTo(new URL("https://caracal.imada.sdu.dk/app2022/reactions"), jsonObject);
                } catch(MalformedURLException e){
                    e.printStackTrace();
                }
                return  returnJson;
            })
            .map(Reaction::ofJSONObject)
            .forEach(reactionDao::update);
        //download reactions
        try{
            List<Reaction> reactions = JSONObjectStream(getJSONArray(new URL("https://caracal.imada.sdu.dk/app2022/reactions?stamp=gt." + DBEntity.instant(reactionDao.latest()))))
                .map(Reaction::ofJSONObject)
                .collect(Collectors.toList());
            reactionDao.addAll(reactions);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Parses the http get response of the given url as a jsonarray and returns it.
     * @param url The url to perform the post to.
     * @return The jsonarray.
     */
    private JSONArray getJSONArray(URL url){
        JSONArray jsonArray = new JSONArray();
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            String json = new BufferedReader(new InputStreamReader( new BufferedInputStream(connection.getInputStream()))).lines().collect(Collectors.joining("\n"));
            jsonArray = new JSONArray(json);
        } catch(Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * Perform a post with the given payload to the given url.
     * Precondition: the url is a http/s url.
     * @return If successful the newly uploaded object, else an empty JSONObject.
     */
    private JSONObject postTo(URL url, JSONObject payload ) {
        JSONObject jsonObject = new JSONObject();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput( true );
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk" );
            connection.setRequestProperty( "Content-Type", "application/json");
            connection.setRequestProperty("Prefer", "return=representation"); // gets the newly uploaded object
            connection.setRequestProperty("accept", "application/json");
            DataOutputStream os = new DataOutputStream( connection.getOutputStream() );
            os.write( payload.toString().getBytes() );
            os.flush();
            String json = new BufferedReader(new InputStreamReader( new BufferedInputStream(connection.getInputStream()))).lines().collect(Collectors.joining("\n"));
            System.out.println(json);
            jsonObject = new JSONArray(json).getJSONObject(0);
            os.close();
            connection.disconnect();
        } catch ( Exception e ){
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Convert a JSONArray to a Stream<JSONObject>
     * @param jsonArray The jsonarray to convert from.
     * @return The stream of jsonobjects.
     */
    private Stream<JSONObject> JSONObjectStream(JSONArray jsonArray){
        Stream.Builder<JSONObject> streamBuilder = Stream.builder();
        try {
            for (int i = 0; i < jsonArray.length(); i++)
                streamBuilder.accept(jsonArray.getJSONObject(i));
        } catch(JSONException e){
            e.printStackTrace();
        }
        return streamBuilder.build();
    }
}