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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.OffsetDateTime;

@Database(entities = {User.class, Post.class, Reaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase AppDB;

    public abstract UserDao userDao();
    public abstract PostDao postDao();
    public abstract ReactionDao reactionDao();

    public static AppDatabase getAppDatabase(Context context){
        if (AppDB == null){
            AppDB = Room.databaseBuilder(context, AppDatabase.class, "hoply").fallbackToDestructiveMigration().allowMainThreadQueries().build(); // allowMainThreadQueries er sandsynligvis ret dumt.
        }
        AppDB.syncDatabase();
        return AppDB;
    }

    /*
     * Sync database TODO: hvad sker der hvis man syncer mens man læser fra databasen?
     */
    public synchronized void syncDatabase(){
        try {
            Thread t1 = new Thread(AppDB::syncUserRelation);
            Thread t2 = new Thread(AppDB::syncPostRelation);
            t1.start();
            t2.start();
            t1.join();
            t2.join();
        } catch( InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * Sync user relation of local database.
     */
    public void syncUserRelation() {
        UserDao userDao = userDao();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/users?stamp=gt." + Instant.ofEpochMilli(userDao.lastUserCreateTime())).openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream is = connection.getInputStream();
            String userJson = new BufferedReader(new InputStreamReader(new BufferedInputStream(is))).lines().collect(Collectors.joining("\n"));
            is.close();

            JSONArray jsonUserArray = new JSONArray(userJson);

            System.out.println(jsonUserArray.toString(2));
            //System.out.println("der er " + jsonUserArray.length()+ " jsonobjekter i json arrayet");

            for (int i = 0; i < jsonUserArray.length(); i++) {
                JSONObject jsonObject = jsonUserArray.getJSONObject(i);
                User user = new User(jsonObject.get("id").toString(), jsonObject.get("name").toString(), true, OffsetDateTime.parse(jsonObject.get("stamp").toString()).toInstant().toEpochMilli());
                userDao.insert(user); // burde nok gøres som én transaction.
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            //synch failed
        }

        //userDao.getAll().forEach( System.out::println );

        List< User > usersToSync = userDao.unSynced();
        System.out.println("user upload sync");
        usersToSync.forEach(System.out::println);

        try {
            URL url = new URL("https://caracal.imada.sdu.dk/app2022/users");
            for (int i = 0; i < usersToSync.size(); i++) {
                User user = usersToSync.get(i);
                JSONObject userJson = new JSONObject();
                userJson.accumulate("id", user.id);
                userJson.accumulate("name", user.name);
                if (syncDatabaseUp(url, userJson)) {
                    user.synced = true;
                    userDao.update(user);
                }
                System.out.println(user.id + " " + user.name + " " + user.synced);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void syncPostRelation(){
        PostDao postDao = postDao();
        try{
            System.out.println( Instant.ofEpochMilli( postDao.lastPostTime() ) );
            HttpURLConnection connection2 = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/posts?stamp=gt." + Instant.ofEpochMilli( postDao.lastPostTime() ) ).openConnection();
            connection2.setRequestProperty("accept", "application/json");
            String postJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection2.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); // det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string. Det ville nok være at foretrække at parse det her som en inputstream.
            JSONArray jsonPostArray = new JSONArray( postJson );

            for( int i = 0; i < jsonPostArray.length(); i++){
                JSONObject jsonObject = jsonPostArray.getJSONObject( i );
                Post post = new Post( Integer.parseInt( jsonObject.get("id").toString() ), jsonObject.get("user_id").toString(), jsonObject.get("content").toString(), OffsetDateTime.parse( jsonObject.get("stamp").toString() ).toInstant().toEpochMilli(), true );
                postDao.insert( post );//TODO måske det her bør nok gøres som én transaction, dvs tilføj en metode addall der indsætter et user[] i én transaction.
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

    /*
     * Upload local information to remote database. TODO
     */
    public boolean syncDatabaseUp( URL url, JSONObject payload ) {
        boolean success = false;
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
            if ( connection.getResponseCode() == HttpURLConnection.HTTP_OK )
                success = true;
            connection.disconnect();
        } catch ( IOException e ){
            e.printStackTrace();
        }
        return success;
    }

    /*
     * Download remote content to local database. TODO
     */
    public void syncDatabaseDown( URL url, String tableName, long timeSince ) {
        try{
            PostDao postDao = AppDB.postDao();
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/" + tableName + "?stamp=gt." + timeSince ).openConnection();
            connection.setRequestProperty("accept", "application/json");
            String postJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); // det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string. Det ville nok være at foretrække at parse det her som en inputstream.
            JSONArray jsonPostArray = new JSONArray( postJson );

            for( int i = 0; i < jsonPostArray.length(); i++){
                JSONObject jsonObject = jsonPostArray.getJSONObject( i );
                Post post = new Post( Integer.parseInt( jsonObject.get("id").toString() ), jsonObject.get("user_id").toString(), jsonObject.get("content").toString(), Instant.parse( jsonObject.get("stamp").toString() ).toEpochMilli(), true );
                postDao.insert( post );//TODO måske det her bør nok gøres som én transaction, dvs tilføj en metode addall der indsætter et user[] i én transaction.
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void populatePosts(){
        Post post = new Post();
        post.content = "noget post content";
        post.id = 2;
        post.user_id = "gaben";
        AppDB.postDao().insert(post);

        Post post2 = new Post();
        post.content = "noget mere post content";
        post.id = 1;
        post.user_id = "gaben";
        AppDB.postDao().insert(post2);

    }
}
