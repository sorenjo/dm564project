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

    //public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public User(String id, String name){
        this.id = id;
        this.name = name;
    }
}
