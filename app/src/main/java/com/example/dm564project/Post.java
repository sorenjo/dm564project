package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "posts")
public class Post {
    @PrimaryKey
    @NonNull
    public Integer id;

    public String user;

    public String content;

    public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public String toString(){
        return id + user + content;
    }

    public Post(int id, String user, String content, long stamp ){
        this.id = id;
        this.user = user;
        this.content = content;
        this.stamp = stamp;
    }
    public Post(){
    }
}
