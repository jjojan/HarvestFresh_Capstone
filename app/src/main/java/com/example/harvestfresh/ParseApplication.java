package com.example.harvestfresh;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    public static final String APP_ID = "JcKwgmM8JL1xrPlL2bvIqjxN7OSjoda3VJJfnA60";
    public static final String CLIENT_ID = "hjhsbj2SC5QL4EbrXvDKBrSMH5NDcMhDgT6aF9o5";
    public static final String SERVER_ADDRESS = "https://parseapi.back4app.com";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .clientKey(CLIENT_ID)
                .server(SERVER_ADDRESS)
                .build()
        );
    }
}
