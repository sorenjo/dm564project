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

    @Transaction
    @Query( "SELECT EXISTS(SELECT * FROM users WHERE id=:uid)" )
    boolean doesExist(String uid);

    @Transaction
    @Insert
    void addAll( List< User > users );

    @Query( "SELECT * FROM users ORDER BY seconds DESC, nanos DESC LIMIT 1" )
    User latest();

    @Query( "SELECT * FROM users WHERE synced = 0" )
    List< User > unSynced();
}