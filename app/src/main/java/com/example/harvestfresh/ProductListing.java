package com.example.harvestfresh;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ProductListing")
public class ProductListing extends ParseObject {
    public static final String KEY_NAME = "ProductName";
    public static final String KEY_COST = "ProductPrice";
    public static final String KEY_IMAGE = "ProductImage";
    public static final String KEY_USER = "ProductOwner";

    public String getProductName() {
        return getString(KEY_NAME);
    }

    public void setProductName(String productName) {
        put(KEY_NAME, productName);
    }

    public String getProductPrice() {
        return getString(KEY_COST);
    }

    public void setProductPrice(String price) {
        put(KEY_COST, price);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }


}
