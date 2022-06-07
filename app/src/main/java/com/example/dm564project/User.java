package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

@Entity(tableName = "users")
public class User extends DBEntity {
    public static User active; // holds the active user.

    @PrimaryKey
    @NonNull
    public String id;

    public String name;

    public boolean synced;


    /**
     * constructs a user from given values.
     * @param id The user's id.
     * @param name The user's name.
     * @param synced The synchronization status of this user. Does it exist in the remote database?
     * @param seconds The seconds component of the timestamp in seconds since the java epoch of 1970-01-01T00:00:00Z
     * @param nanos The nanosecond component of the timestamp.
     */
    public User(String id, String name, boolean synced, long seconds, int nanos){
        this.id = id;
        this.name = name;
        this.synced = synced;
        this.seconds = seconds;
        this.nanos = nanos;
    }

    /**
     * Constructs a new anonymous user.
     */
    public User(){
        id = "anon";
        name = null;
        synced = false;
        seconds = 0;
        nanos = 0;
    }

    /**
     * Returns a user represented by the given JSONObject, with synced set to true.
     * @param jsonObject The JSONObject representing a user.
     * @return The user.
     */
    public static User ofJSONObject(JSONObject jsonObject){
        User user = new User();
        try {
            user.id = jsonObject.getString("id");
            user.name = jsonObject.getString("name");
            Instant instant = OffsetDateTime.parse(jsonObject.getString("stamp")).toInstant();
            user.seconds = instant.getEpochSecond();
            user.nanos = instant.getNano();
            user.synced = true;
        } catch(JSONException e ){
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Returns the given user as JSONObject representation, with only attributes id and name.
     * @param user The user to convert to JSON.
     * @return A JSONObject representing the given user.
     */
    public static JSONObject toJSONObject(User user){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("id", user.id);
            jsonObject.accumulate("name", user.name);
        } catch( JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Returns a textual representation of this post.
     * @return a textual representation of this post.
     */
    public String toString(){
        return id + " " + name + " " + synced + " " + seconds + " " + nanos;
    }
}
