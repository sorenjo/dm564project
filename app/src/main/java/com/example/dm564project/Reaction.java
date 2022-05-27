package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"user", "post"})
public class Reaction {
    public static int REACTION_DELETED = 0;
    public static int LIKE = 1;
    public static int HATE = 2;
    public static int COULDNT_CARE_LESS = 3;

    public static String[] reactionTexts = {" deleted their reaction on ", " likes ", " hates ", " couldn't care less about "};

    @NonNull
    public String user;

    @NonNull
    public int post;

    public int type;

    //public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public Reaction( String user, int post, int type ){
        this.user = user;
        this.post = post;
        this.type = type;
    }
}
