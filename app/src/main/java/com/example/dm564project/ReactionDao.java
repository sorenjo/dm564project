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
    @Insert(onConflict = OnConflictStrategy.REPLACE)// hvis en bruger reagerer flere gange på en post bliver reaktionen overskrevet.
    void insert( Reaction reaction );

    @Query("SELECT * FROM reactions")
    List<Reaction> getAll();

    @Query("SELECT userId, postId, type, r.synced AS synced, u.name AS userName, r.seconds AS seconds, r.nanos AS nanos FROM reactions r JOIN users u ON r.userId = u.id AND r.postId = :postId")
    List<ReactionWithUserName> getFor(int postId);

    @Query("SELECT * FROM reactions ORDER BY seconds DESC, nanos DESC LIMIT 1")
    Reaction latest();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE) // når vi henter data fra serveren og indsætter i databasen er der nogle gange reaktioner der ikke overholder unique constraints eller foreign key constraints, hvorfor vi bliver nødt til at gøre noget ved dem.
    void addAll(List<Reaction> reactions);

    @Query("SELECT * FROM reactions WHERE synced = 0")
    List<Reaction> unSynced();

    @Update
    void update(Reaction reaction);
}
