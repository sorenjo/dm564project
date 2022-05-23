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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;
import java.time.Instant;

@Database(entities = {User.class, Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase AppDB;

    public abstract UserDao userDao();
    public abstract PostDao postDao();

    public static AppDatabase getAppDatabase(Context context){
        if (AppDB == null){
            AppDB = Room.databaseBuilder(context, AppDatabase.class, "hoply").fallbackToDestructiveMigration().allowMainThreadQueries().build(); // allowMainThreadQueries is probably suboptimal.
            /* try { //TODO ved ikke lige hvornår man bør synche.
                Thread t = new Thread(AppDatabase::syncDatabase);
                t.start();
                t.join();
            } catch( InterruptedException e) {
                e.printStackTrace();
            }*/
        }
        return AppDB;
    }
    /*
     * Downloads users, posts and reactions from remote server and insert into local database.
     */
    public static void  syncDatabase(){
        System.out.println("-----------------------------------DATABASE SYNCHRONIZATION ---------------------------------");
        /*
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/users").openConnection();
            connection.setRequestProperty("accept", "application/json");
            String userJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") );
            JSONArray jsonUserArray = new JSONArray( userJson );

            System.out.println( jsonUserArray.toString( 2 ) );
            System.out.println("der er " + jsonUserArray.length()+ " jsonobjekter i json arrayet");
            UserDao userDao = AppDB.userDao();

            for( int i = 0; i < jsonUserArray.length(); i++){
                JSONObject jsonObject = jsonUserArray.getJSONObject( i );
                User user = new User(jsonObject.get("id").toString(), jsonObject.get("name").toString());
                userDao.insert( user );
            }
           try{
            PostDao postDao = AppDB.postDao();
            HttpURLConnection connection2 = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/posts?stamp=gt." + Instant.ofEpochMilli( postDao.lastPostTime() ).toString() ).openConnection();
            connection2.setRequestProperty("accept", "application/json");
            String postJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection2.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); // det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string. Det ville nok være at foretrække at parse det her som en inputstream.
            JSONArray jsonPostArray = new JSONArray( postJson );

            for( int i = 0; i < jsonPostArray.length(); i++){
                JSONObject jsonObject = jsonPostArray.getJSONObject( i );
                Post post = new Post( Integer.parseInt( jsonObject.get("id").toString() ), jsonObject.get("user_id").toString(), jsonObject.get("content").toString(), Instant.parse( jsonObject.get("stamp").toString() ).toEpochMilli() );
                postDao.insert( post );//TODO måske det her bør nok gøres som én transaction, dvs tilføj en metode addall der indsætter et user[] i én transaction.
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
         */
    }

    /*
     * Upload local information to remote database. TODO
     */
    public void syncDatabaseUp( URL url, JSONObject payload ){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );
            connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk");
            DataOutputStream os = new DataOutputStream( connection.getOutputStream() );
            os.writeBytes( payload.toString() );
            os.flush();
            os.close();
        } catch( Exception e) {
            e.printStackTrace();
            //synch failed
        }
    }

    /*
     * Download remote content to local database. TODO
     */
    public void syncDatabaseDown( URL url, String tableName, long timeSince ) {
        try{
            PostDao postDao = AppDB.postDao();
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/" + tableName + "?stamp=gt." + timeSince ).openConnection();
            connection.setRequestProperty("accept", "application/json");
            String postJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection2.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); // det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string. Det ville nok være at foretrække at parse det her som en inputstream.
            JSONArray jsonPostArray = new JSONArray( postJson );

            for( int i = 0; i < jsonPostArray.length(); i++){
                JSONObject jsonObject = jsonPostArray.getJSONObject( i );
                Post post = new Post( Integer.parseInt( jsonObject.get("id").toString() ), jsonObject.get("user_id").toString(), jsonObject.get("content").toString(), Instant.parse( jsonObject.get("stamp").toString() ).toEpochMilli() );
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
        post.user = "gaben";
        AppDB.postDao().insert(post);

        Post post2 = new Post();
        post.content = "noget mere post content";
        post.id = 1;
        post.user = "gaben";
        AppDB.postDao().insert(post2);

    }
}
