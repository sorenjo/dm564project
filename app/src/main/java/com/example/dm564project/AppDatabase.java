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

@Database(entities = {User.class, Post.class, Reaction.class, Comment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase AppDB;

    public abstract UserDao userDao();
    public abstract PostDao postDao();
    public abstract ReactionDao reactionDao();
    public abstract CommentDao commentDao();

    public static AppDatabase getAppDatabase(Context context){
        if (AppDB == null){
            AppDB = Room.databaseBuilder(context, AppDatabase.class, "hoply").fallbackToDestructiveMigration().allowMainThreadQueries().build(); // allowMainThreadQueries er sandsynligvis ret dumt.
        }
        //AppDB.syncDatabase();
        return AppDB;
    }

    /*
     * Sync database
     */
    public void syncDatabase(){
        System.out.println("syncing database");
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

    /*
     * Synchronizes the user relation of this database.
     */
    private void syncDownUpUser(){
        System.out.println("---------------------------- user sync --------------------");
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

    private void syncDownUpReaction(){
        ReactionDao reactionDao = reactionDao();
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
    }

    /*
     * Sync user relation of local database.
     *
    private void syncUserRelation() {
        UserDao userDao = userDao();
        try { // download users
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/users?stamp=gt." + Instant.ofEpochMilli(userDao.lastUserCreateTime() ) ).openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream is = connection.getInputStream();
            String userJson = new BufferedReader(new InputStreamReader(new BufferedInputStream(is))).lines().collect(Collectors.joining("\n"));
            is.close();

            JSONArray jsonUserArray = new JSONArray(userJson);

            System.out.println(jsonUserArray.toString(2));
            //System.out.println("der er " + jsonUserArray.length()+ " jsonobjekter i json arrayet");

            List< User > users = new ArrayList<>();

            for (int i = 0; i < jsonUserArray.length(); i++) {
                JSONObject jsonObject = jsonUserArray.getJSONObject(i);
                User user = new User(jsonObject.get("id").toString(), jsonObject.get("name").toString(), true, OffsetDateTime.parse(jsonObject.get("stamp").toString()).toInstant().toEpochMilli() );
                users.add( user );
            }
            userDao.addAll( users );
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            //synch failed
        }

        // upload users

        List< User > usersToSync = userDao.unSynced();
        try {
            URL url = new URL( "https://caracal.imada.sdu.dk/app2022/users" );
            for (int i = 0; i < usersToSync.size(); i++) {
                User user = usersToSync.get(i);
                JSONObject userJson = new JSONObject();
                userJson.accumulate("id", user.id);
                userJson.accumulate("name", user.name);
                if ( syncDatabaseUp( url, userJson ) ) {
                    // Nu hvor useren er blevet indsat på serveren har den også fået et stamp som nu downloades og indsættes i den lokale database.
                    try{
                        URL downURL = new URL( "https://caracal.imada.sdu.dk/app2022/users?id=eq." + user.id );
                        HttpURLConnection connection = (HttpURLConnection) downURL.openConnection();
                        connection.setRequestProperty( "accept", "application/json" );
                        String downUser = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") );
                        JSONObject downUserJson = new JSONArray( downUser ).getJSONObject( 0 ); // siden id er unikt ved vi at vi får et jsonarray af længde 1, hvorfor det ligger på indeks 0.
                        user.stamp = OffsetDateTime.parse( downUserJson.get( "stamp" ).toString() ).toInstant().toEpochMilli();
                    } catch( IOException e ){
                        e.printStackTrace();
                    }
                    user.synced = true;
                    userDao.update( user );
                }
                System.out.println(user.id + " " + user.name + " " + user.synced);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void syncPostRelation(){
        PostDao postDao = postDao();
        try{
            //System.out.println( Instant.ofEpochMilli( postDao.lastPostTime() ) );
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/posts?stamp=gt." + Instant.ofEpochMilli( postDao.lastPostTime() ) ).openConnection();
            connection.setRequestProperty("accept", "application/json");
            String postJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); // det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string. Det ville nok være at foretrække at parse det her som en inputstream.
            JSONArray jsonPostArray = new JSONArray( postJson );

            for( int i = 0; i < jsonPostArray.length(); i++){
                JSONObject jsonObject = jsonPostArray.getJSONObject( i );
                Post post = new Post( Integer.parseInt( jsonObject.get("id").toString() ), jsonObject.get("user_id").toString(), jsonObject.get("content").toString(), true, OffsetDateTime.parse( jsonObject.get("stamp").toString() ).toInstant().toEpochMilli());
                postDao.insert( post );//TODO måske det her bør nok gøres som én transaction, dvs tilføj en metode addall der indsætter et post[] i én transaction.
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
        List< Post > postsToSync = postDao.unSynced();
        System.out.println("post upload sync");
        postsToSync.forEach( System.out::println );

        try {
            URL url = new URL("https://caracal.imada.sdu.dk/app2022/posts");
            for (int i = 0; i < postsToSync.size(); i++) {
                Post post = postsToSync.get(i);
                JSONObject postJson = new JSONObject();
                postJson.accumulate("id", post.id);
                postJson.accumulate("user_id", post.user_id);
                postJson.accumulate("content", post.content);
                if (syncDatabaseUp(url, postJson)) {
                    post.synced = true;
                    postDao.update(post);
                }
                System.out.println(post.id + " " + post.content + " " + post.synced);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */

    /*
     * Downloads jsonarray from given url, applies the given function on each element, accumulates that to a list then consumes it using the given consumer.
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

    /*
     * Gets a the first element from the given url's jsonarray and applies given function before consuming it with the given consumer.
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

    public void populatePosts(){
        Post post = new Post();
        post.content = "noget post content";
        post.id = 2;
        post.userId = "gaben";
        AppDB.postDao().insert(post);

        Post post2 = new Post();
        post.content = "noget mere post content";
        post.id = 1;
        post.userId = "gaben";
        AppDB.postDao().insert(post2);

    }
}
