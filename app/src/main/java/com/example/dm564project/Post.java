package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;


@Entity(tableName = "posts")
public class Post extends DBEntity{
    @PrimaryKey
    @NonNull
    public Integer id;

    public String user_id;

    public String content;

    //public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.



    public boolean synced;

    public Post(int id, String user_id, String content, boolean synced, long seconds, int nanos){
        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.seconds = seconds;
        this.nanos = nanos;
        this.synced = synced;
    }
    public Post(){
    }

    public static Post ofJSONObject(JSONObject jsonObject){
        Post post = new Post();
        try {
            post.id = jsonObject.getInt("id");
            post.user_id = jsonObject.getString("user_id");
            post.content = jsonObject.getString("content");
            Instant instant = OffsetDateTime.parse(jsonObject.getString("stamp")).toInstant();
            post.seconds = instant.getEpochSecond();
            post.nanos = instant.getNano();
            post.synced = true;
        } catch(JSONException e){
            e.printStackTrace();
        }
        return post;
    }

    public static JSONObject toJSONObject(Post post){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.accumulate("id", post.id);
            jsonObject.accumulate("user_id", post.user_id);
            jsonObject.accumulate("content", post.content);
        } catch(JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String toString(){
        return id + user_id + content;
    }
}
