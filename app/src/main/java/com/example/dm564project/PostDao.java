package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Map;

@Dao
public interface PostDao {
    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Query("SELECT * FROM posts ORDER BY seconds DESC, nanos DESC")
    List<Post> getAll();

    @Query("SELECT * FROM posts ORDER BY seconds DESC, nanos DESC LIMIT 1")
    Post latest();

    @Query("SELECT p.seconds AS seconds, p.nanos AS nanos, p.id AS id, content, p.synced AS synced, u.name AS userName FROM posts p JOIN users u ON p.userId = u.id AND p.id = :pid")
    PostWithUserName findById( int pid );

    @Query("SELECT MAX(id+1) FROM posts")
    int nextId();

    @Query("SELECT * FROM posts WHERE synced = 0")
    List< Post > unSynced();

    @Transaction
    @Query("SELECT p.seconds AS seconds, p.nanos AS nanos, p.id AS id, content, p.synced AS synced, u.name AS userName FROM posts p JOIN users u ON p.userId = u.id ORDER BY seconds DESC, nanos DESC")
    List<PostWithUserName> withUserNames();

    @Transaction
    @Insert
    void addAll(List<Post> posts);
}
