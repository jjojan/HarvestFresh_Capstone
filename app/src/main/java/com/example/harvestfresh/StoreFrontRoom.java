package com.example.harvestfresh;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@Entity
public class StoreFrontRoom {

    @ColumnInfo
    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "store_name")
    public String name;

    @ColumnInfo
    public String image;

    @Ignore
    @ColumnInfo
    public String user;

    @Ignore
    @ColumnInfo
    public String location;

    public StoreFrontRoom () {

    }

    public StoreFrontRoom (StoreFront storeFront){
        this.name = storeFront.getName();
        this.image = storeFront.getImage().getUrl();
        this.id = storeFront.getObjectId();
    }

    public static List<StoreFront> getStoreFrontRooms (List<StoreFrontRoom> storesFromRooms){
      List<StoreFront> storeFronts = new ArrayList<>();
      for (StoreFrontRoom storeFront: storesFromRooms) {
          StoreFront storeFrontRoom = new StoreFront(storeFront);
          storeFronts.add(storeFrontRoom);
          }
      return storeFronts;
    }

    public StoreFront toStoreFront() { return new StoreFront(this); }

}
