package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String id;

    public String name;

    public User(String id, String name){
        this.id = id;
        this.name = name;
    }
}
