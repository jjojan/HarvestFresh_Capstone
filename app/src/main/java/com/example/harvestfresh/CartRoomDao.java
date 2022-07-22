package com.example.harvestfresh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartRoomDao {
    @Query("SELECT * FROM CartRoom ORDER BY cartId LIMIT 20")
    List<CartRoom> recentCart();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCartRoom(CartRoom... cartRooms);
}
