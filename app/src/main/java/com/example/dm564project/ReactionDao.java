package com.example.dm564project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface ReactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)// hvis en bruger reagerer flere gange på post bliver den overskrevet.
    void insert( Reaction reaction );

}
