package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "posts")
public class Post {
    @PrimaryKey
    @NonNull
    public Integer id;

    public String user_id;

    public String content;

    public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public boolean synced;

    public String toString(){
        return id + user_id + content;
    }

    public Post(int id, String user_id, String content, long stamp, boolean synced ){
        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.stamp = stamp;
        this.synced = synced;
    }
    public Post(){
    }
}
