package com.example.dm564project;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase AppDB;

    public abstract UserDao userDao();

    public static synchronized AppDatabase getAppDatabase(Context context){
        if (AppDB == null){
            AppDB = Room.databaseBuilder(context, AppDatabase.class, "hoply").fallbackToDestructiveMigration().build();
        }
        return AppDB;
    }
}