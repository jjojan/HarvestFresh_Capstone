package com.example.harvestfresh;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

public class HarvestFreshDatabaseApplication extends Application {
    HarvestFreshDatabase myDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        myDatabase = Room.databaseBuilder(this,
                        HarvestFreshDatabase.class,
                        HarvestFreshDatabase.NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    public HarvestFreshDatabase getMyDatabase() {
        return myDatabase;
    }
}
