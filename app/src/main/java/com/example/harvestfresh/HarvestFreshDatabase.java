package com.example.harvestfresh;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StoreFrontRoom.class, CartRoom.class}, version = 5)
public abstract class HarvestFreshDatabase extends RoomDatabase {

    public abstract StoreFrontDao storeFrontDao();

    public abstract CartRoomDao cartRoomDao();

    public static final String NAME = "HarvestFreshDatabase";

}
