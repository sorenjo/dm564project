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

    public String toString(){
        return id + user + content;
    }

    public Post(int id, String user, String content ){
        this.id = id;
        this.user = user;
        this.content = content;
    }
    public Post(){
    }
}
