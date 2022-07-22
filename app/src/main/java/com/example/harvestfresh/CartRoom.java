package com.example.harvestfresh;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class CartRoom {

    @ColumnInfo
    @PrimaryKey
    @NonNull
    public String CartId;

    @ColumnInfo
    public String productName;

    @ColumnInfo
    public String productCost;

    public CartRoom () {

    }

    public CartRoom (Cart cart){
        this.CartId = cart.getObjectId();
        this.productName = cart.getProduct();
        this.productCost = cart.getPrice().getProductPrice();
    }

    public static List<Cart> getCartRooms (List<CartRoom> cartsFromRooms){
        List<Cart> carts = new ArrayList<>();
        for (CartRoom cartRoom: cartsFromRooms) {
            Cart cart = new Cart();
            ProductListing listing = new ProductListing();
            listing.setProductPrice(cartRoom.productCost);
            listing.setProductName(cartRoom.productName);
            cart.setProduct(listing.getProductName());
            cart.setPrice(listing);
            carts.add(cart);
        }
    return carts;
    }

}
