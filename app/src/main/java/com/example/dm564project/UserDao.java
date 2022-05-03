package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id=:uid")
    User findById(String uid);

    @Query("SELECT * FROM users")
    List<User> getAll();
}