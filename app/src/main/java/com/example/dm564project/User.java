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
    public static User active;

    @PrimaryKey
    @NonNull
    public String id;

    public String name;

    public boolean synced;

    public User(String id, String name, boolean synced, long seconds, int nanos){
        this.id = id;
        this.name = name;
        this.synced = synced;
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public User(){
        id = "anon";
        name = null;
        synced = false;
        seconds = 0;
        nanos = 0;
    }

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

    public String toString(){
        return id + " " + name + " " + synced + " " + seconds + " " + nanos;
    }
}
