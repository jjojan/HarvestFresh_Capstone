package com.example.harvestfresh;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Cart")
public class Cart extends ParseObject {
    public static final String KEY_NAME = "Product";
    public static final String KEY_COST = "Cost";
    public static final String KEY_USER = "CartOwner";

    public String getProduct() {
        return getString(KEY_NAME);
    }

    public void setProduct(String product) {
        put(KEY_NAME, product);
    }

    public ProductListing getPrice() {
        try {
            return getParseObject(KEY_COST).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPrice(ProductListing price) {
        put(KEY_COST, price);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

}
