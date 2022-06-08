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

    @Query("SELECT c.seconds AS seconds, c.nanos AS nanos, c.id AS id, userId, postId, comment, u.name AS userName FROM comments c JOIN users u ON c.userId = u.id AND c.postId = :postId")
    List<CommentWithUserName> getFromPost( int postId );

    @Query("SELECT * FROM comments ORDER BY seconds DESC, nanos DESC LIMIT 1")
    long lastComment();

    @Query("SELECT MAX(id+1) FROM comments")
    int nextId();
}
