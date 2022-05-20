package com.example.dm564project;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@Database(entities = {User.class, Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase AppDB;

    public abstract UserDao userDao();
    public abstract PostDao postDao();

    public static AppDatabase getAppDatabase(Context context){
        if (AppDB == null){
            AppDB = Room.databaseBuilder(context, AppDatabase.class, "hoply").fallbackToDestructiveMigration().allowMainThreadQueries().build(); // allowMainThreadQueries is probably suboptimal.
            /* try { //TODO ved ikke lige hvornår man bør synche.
                Thread t = new Thread(AppDatabase::syncDatabase); //TODO det her er sikkert på ingen tænkelig måde threadsafe.4
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
        System.out.println("--------------------------------------------------------------------");
        /*
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/users").openConnection();
            connection.setRequestProperty("accept", "application/json");
            String userJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); //TODO det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string.
            JSONArray jsonUserArray = new JSONArray( userJson );

            System.out.println( jsonUserArray.toString( 2 ) );
            System.out.println("der er " + jsonUserArray.length()+ " jsonobjekter i json arrayet");
            UserDao userDao = AppDB.userDao();

            for( int i = 0; i < jsonUserArray.length(); i++){
                JSONObject jsonObject = jsonUserArray.getJSONObject( i );
                User user = new User(jsonObject.get("id").toString(), jsonObject.get("name").toString());
                userDao.insert( user );
            }
    */
        try{
            HttpURLConnection connection2 = (HttpURLConnection) new URL("https://caracal.imada.sdu.dk/app2022/posts").openConnection();
            connection2.setRequestProperty("accept", "application/json");
            String postJson = new BufferedReader( new InputStreamReader( new BufferedInputStream( connection2.getInputStream() ) ) ).lines().collect( Collectors.joining("\n") ); //TODO det er nok ret hukommelsesintensivt at konvertere inputstreamen til en string.
            JSONArray jsonPostArray = new JSONArray( postJson );

            PostDao postDao = AppDB.postDao();

            for( int i = 0; i < jsonPostArray.length(); i++){
                JSONObject jsonObject = jsonPostArray.getJSONObject( i );
                Post post = new Post( Integer.parseInt( jsonObject.get("id").toString() ), jsonObject.get("user_id").toString(), jsonObject.get("content").toString() );
                postDao.insert( post );
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }


    public void populatePosts(){
        Post post = new Post();
        post.content = "noget mere post content";
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
