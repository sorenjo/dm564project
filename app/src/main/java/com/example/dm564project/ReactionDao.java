package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)// hvis en bruger reagerer flere gange på post bliver den overskrevet.
    void insert( Reaction reaction );

    @Query("SELECT * FROM reaction")
    List< Reaction > getAll();

    @Query("SELECT * FROM reaction WHERE post=:postId")
    List< Reaction > getFor( int postId );

    @Query("SELECT * FROM reaction ORDER BY seconds DESC, nanos DESC LIMIT 1")
    Reaction latest();

    @Transaction
    @Insert
    void addAll(List<Reaction> reactions);

    @Query("SELECT * FROM reaction WHERE synced = 0")
    List<Reaction> unSynced();

    @Update
    void update(Reaction reaction);
}
