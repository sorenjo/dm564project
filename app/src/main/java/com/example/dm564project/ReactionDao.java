package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)// hvis en bruger reagerer flere gange på post bliver den overskrevet.
    void insert( Reaction reaction );

    @Query("SELECT * FROM reaction")
    List< Reaction > getAll();

    @Query("SELECT * FROM reaction WHERE post=:postId")
    List< Reaction > getFor( int postId );
}
