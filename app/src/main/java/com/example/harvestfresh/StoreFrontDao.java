package com.example.harvestfresh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StoreFrontDao {

    @Query("SELECT * FROM StoreFrontRoom ORDER BY id LIMIT 20")
    List<StoreFrontRoom> recentStores();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreFront(StoreFrontRoom... storeFrontRooms);

    @Query("SELECT * FROM StoreFrontRoom WHERE store_name LIKE '%' || :search || '%'")
    public List<StoreFrontRoom> findStorewithName(String search);

}
