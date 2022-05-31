package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity(primaryKeys = {"user", "post"})
public class Reaction extends DBEntity {
    public static int REACTION_DELETED = 0;
    public static int LIKE = 1;
    public static int HATE = 2;
    public static int COULDNT_CARE_LESS = 3;

    public static String[] reactionTexts = {" deleted their reaction on ", " likes ", " hates ", " couldn't care less about "};

    @NonNull
    public String user;

     public int post;

    public int type;

    public boolean synced;

    //public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public long seconds; // Creation time of this user in seconds since the epoch of 1970-01-01T00:00:00Z

    public int nanos; // The nanosecond component of the creation time. Always between 0 and 999 999 999.

    public Reaction(){
    }

    public Reaction( String user, int post, int type, boolean synced ){
        this.user = user;
        this.post = post;
        this.type = type;
        this.synced = synced;
    }

    public static Reaction ofJSONObject(JSONObject jsonObject){
        Reaction reaction = new Reaction();
        try{
            reaction.user = jsonObject.getString("user_id");
            reaction.post = jsonObject.getInt("post_id");
            reaction.type = jsonObject.getInt("type");
            Instant instant = OffsetDateTime.parse(jsonObject.getString("stamp")).toInstant();
            reaction.seconds = instant.getEpochSecond();
            reaction.seconds = instant.getNano();
            reaction.synced = true;
        } catch(JSONException e){
            e.printStackTrace();
        }
        return reaction;
    }

    public static JSONObject toJSONObject(Reaction reaction){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", reaction.user);
            jsonObject.accumulate("post_id", reaction.post);
            jsonObject.accumulate("type", reaction.type);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
