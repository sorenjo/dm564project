package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insert(Comment comment);

    @Update
    void update(Comment comment);

    @Query("SELECT * FROM comments")
    List<Comment> getAll();

    @Query("SELECT * FROM comments WHERE postId=:postId")
    List<Comment> getFromPost( int postId );

    @Query("SELECT MAX(stamp) FROM comments")
    long lastComment();

    @Query("SELECT MAX(id+1) FROM comments")
    int nextId();
}
