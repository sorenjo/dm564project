package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity(tableName = "posts", foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id",
                childColumns = "userId", onDelete = ForeignKey.CASCADE)
})
public class Post extends DBEntity {
    @PrimaryKey
    @NonNull
    public Integer id;

    public String userId;

    public String content;

    public boolean synced;

    public Post(int id, String userId, String content, boolean synced, long seconds, int nanos){
        this.id = id;
        this.userId = userId;
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
            post.userId = jsonObject.getString("user_id");
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
            jsonObject.accumulate("user_id", post.userId);
            jsonObject.accumulate("content", post.content);
        } catch(JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String toString(){
        return id + userId + content;
    }
}
