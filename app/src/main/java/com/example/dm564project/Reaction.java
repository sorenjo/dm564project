package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity(tableName = "reactions", primaryKeys = {"userId", "postId"}, foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id",
                childColumns = "userId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Post.class, parentColumns = "id",
                childColumns = "postId", onDelete = ForeignKey.CASCADE)
})
public class Reaction extends DBEntity {
    public static int REACTION_DELETED = 0;
    public static int LIKE = 1;
    public static int HATE = 2;
    public static int COULDNT_CARE_LESS = 3;

    public static String[] reactionTexts = {" deleted their reaction on ", " likes ", " hates ", " couldn't care less about "};

    @NonNull
    public String userId;

    public int postId;

    public int type;

    public boolean synced;

    /**
     * Constructs a new, empty reaction.
     */
    public Reaction(){
    }

    /**
     * constructs a reaction from given values.
     * @param userId Id of the user creating this reaction.
     * @param postId The id of the post to which this reaction belong.
     * @param synced The synchronization status of this post. Does it exist in the remote database?
     * @param seconds The seconds component of the timestamp in seconds since the java epoch of 1970-01-01T00:00:00Z
     * @param nanos The nanosecond component of the timestamp.
     */
    public Reaction(String userId, int postId, int type, boolean synced, long seconds, int nanos){
        this.userId = userId;
        this.postId = postId;
        this.type = type;
        this.synced = synced;
        this.seconds = seconds;
        this.nanos = nanos;
    }

    /**
     * Returns a reaction represented by the given JSONObject, with synced set to true.
     * @param jsonObject The JSONObject representing a reaction.
     * @return The reaction.
     */
    public static Reaction ofJSONObject(JSONObject jsonObject){
        Reaction reaction = new Reaction();
        try{
            reaction.userId = jsonObject.getString("user_id");
            reaction.postId = jsonObject.getInt("post_id");
            reaction.type = jsonObject.getInt("type");
            Instant instant = OffsetDateTime.parse(jsonObject.getString("stamp")).toInstant();
            reaction.seconds = instant.getEpochSecond();
            reaction.nanos = instant.getNano();
            reaction.synced = true;
        } catch(JSONException e){
            e.printStackTrace();
        }
        return reaction;
    }

    /**
     * Returns the given reaction as JSONObject representation, with only attributes user_id, post_id and type.
     * @param reaction The reaction to convert to JSON.
     * @return A JSONObject representing the given reaction.
     */
    public static JSONObject toJSONObject(Reaction reaction){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", reaction.userId);
            jsonObject.accumulate("post_id", reaction.postId);
            jsonObject.accumulate("type", reaction.type);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Returns a textual representation of this reaction.
     * @return a textual representation of this reaction.
     */
    public String toString(){
        return userId + postId + type + synced + seconds + nanos;
    }
}
