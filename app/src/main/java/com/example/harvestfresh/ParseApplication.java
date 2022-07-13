package com.example.harvestfresh;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    public static final String APP_ID = "JcKwgmM8JL1xrPlL2bvIqjxN7OSjoda3VJJfnA60";
    public static final String CLIENT_ID = "hjhsbj2SC5QL4EbrXvDKBrSMH5NDcMhDgT6aF9o5";
    public static final String SERVER_ADDRESS = "https://parseapi.back4app.com";
    public static final String SENDER_ID = "808974419018";
    public static final String SENDER_TITLE = "GCMSenderId";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(StoreFront.class);
        ParseObject.registerSubclass(Cart.class);
        ParseObject.registerSubclass(ProductListing.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .clientKey(CLIENT_ID)
                .server(SERVER_ADDRESS)
                .build()
        );

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(SENDER_TITLE, SENDER_ID);
        installation.saveInBackground();
    }
}
