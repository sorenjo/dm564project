package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert( User user );

    @Delete
    void delete( User user );

    @Update
    void update ( User user );

    @Query( "SELECT * FROM users WHERE id=:uid" )
    User findById( String uid );

    @Query( "SELECT * FROM users" )
    List< User > getAll();

    @Query( "SELECT EXISTS(SELECT * FROM users WHERE id=:uid)" )
    boolean doesExist(String uid);

    @Transaction
    @Query( "SELECT * FROM users" )
    List< UserWithPosts > getUserPosts();

    @Query( "SELECT MAX(stamp) FROM users" )
    long lastUserCreateTime();

    @Query( "SELECT * FROM users WHERE synced = 0" )
    List< User > unSynced();
}