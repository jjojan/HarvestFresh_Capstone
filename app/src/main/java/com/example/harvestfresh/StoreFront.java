package com.example.harvestfresh;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("StoreFront")
public class StoreFront extends ParseObject {

    public static final String KEY_NAME = "StoreName";
    public static final String KEY_IMAGE = "StoreImage";
    public static final String KEY_USER = "User";
    public static final String KEY_LOCATION = "StoreLocation";

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseGeoPoint getLocation(){ return getParseGeoPoint(KEY_LOCATION); }

    public void setLocation(ParseGeoPoint location) { put(KEY_LOCATION, location); }
}
