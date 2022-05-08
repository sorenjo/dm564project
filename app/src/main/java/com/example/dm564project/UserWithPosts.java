package com.example.dm564project;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;


public class UserWithPosts {
    @Embedded public User user;
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    public List<Post> posts;
}
