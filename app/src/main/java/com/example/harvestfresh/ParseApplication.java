package com.example.harvestfresh;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("JcKwgmM8JL1xrPlL2bvIqjxN7OSjoda3VJJfnA60")
                .clientKey("hjhsbj2SC5QL4EbrXvDKBrSMH5NDcMhDgT6aF9o5")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
