package com.example.dm564project;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    public String id;

    public String name;
    public Date timestamp;
}
