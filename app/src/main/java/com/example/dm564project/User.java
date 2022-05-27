package com.example.dm564project;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "users")
public class User {
    public static User active;

    @PrimaryKey
    @NonNull
    public String id;

    public String name;

    public boolean synced;

    public long stamp; //In milliseconds since the epoch of 1970-01-01T00:00:00Z.

    public User(String id, String name, boolean synced, long stamp ){
        this.id = id;
        this.name = name;
        this.synced = synced;
        this.stamp = stamp;
    }

    public User(){
        id = "anon";
        name = null;
        synced = false;
        stamp = 0;
    }


    public String toString(){
        return id + " " + name + " " + synced + " " + stamp;
    }
}
