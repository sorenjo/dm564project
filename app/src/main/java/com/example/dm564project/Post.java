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

    /**
     * constructs a Post from given values.
     * @param id Unique id for this post.
     * @param userId The id of the user creating this post.
     * @param synced The synchronization status of this post. Does it exist in the remote database?
     * @param seconds The seconds component of the timestamp in seconds since the java epoch of 1970-01-01T00:00:00Z
     * @param nanos The nanosecond component of the timestamp.
     */
    public Post(int id, String userId, String content, boolean synced, long seconds, int nanos){
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.seconds = seconds;
        this.nanos = nanos;
        this.synced = synced;
    }

    /**
     * Constructs a new empty Post.
     */
    public Post(){
    }

    /**
     * Returns a post represented by the given JSONObject, with synced set to true.
     * @param jsonObject The JSONObject representing a post.
     * @return The post.
     */
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
    /**
     * Returns the given post as JSONObject representation, with only attributes id, user_id and content.
     * @param post The post to convert to JSON.
     * @return A JSONObject representing the given post.
     */
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

    /**
     * Returns a textual representation of this post.
     * @return a textual representation of this post.
     */
    public String toString(){
        return id + userId + content;
    }
}
