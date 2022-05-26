package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {
    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Query("SELECT * FROM posts")
    List<Post> getAll();

    @Query("SELECT MAX(stamp) FROM posts")
    long lastPostTime();

    @Query("SELECT * FROM posts WHERE id=:pid")
    Post findById( int pid );

    @Query("SELECT MAX(id+1) FROM posts")
    int nextId();

    @Query("SELECT * FROM posts WHERE synced = 0")
    List< Post > unSynced();
}
