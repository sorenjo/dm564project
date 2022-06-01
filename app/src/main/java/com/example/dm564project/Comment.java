package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments", foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id",
                    childColumns = "userId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Post.class, parentColumns = "id",
                    childColumns = "postId", onDelete = ForeignKey.CASCADE)
})
public class Comment {

    @PrimaryKey
    @NonNull
    public int id;

    public String userId;

    public int postId;

    public String comment;

    public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public Comment(int id, String userId, int postId, String comment, long stamp ){
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.comment = comment;
        this.stamp = stamp;
    }

    public Comment() {
    }
}