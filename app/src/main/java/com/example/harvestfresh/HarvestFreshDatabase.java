package com.example.harvestfresh;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StoreFrontRoom.class}, version = 3)
public abstract class HarvestFreshDatabase extends RoomDatabase {

    public abstract StoreFrontDao storeFrontDao();

    public static final String NAME = "HarvestFreshDatabase";

}
