package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {
    @Insert
    void insert(Post post);

    @Query("SELECT * FROM posts")
    List<Post> getAll();

    @Query("SELECT MAX(stamp) FROM posts")
    long lastPostTime();
}
