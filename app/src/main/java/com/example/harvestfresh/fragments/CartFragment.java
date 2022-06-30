package com.example.harvestfresh.fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.harvestfresh.Cart;
import com.example.harvestfresh.CartAdapter;
import com.example.harvestfresh.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";

    private RecyclerView rvCart;
    private CartAdapter fragmentAdapter;
    private List<Cart> allCarts;

    public CartFragment() {
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rvCart);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        allCarts = new ArrayList<>();
        fragmentAdapter = new CartAdapter(getContext(), allCarts);
        rvCart.setAdapter(fragmentAdapter);

        queryCart();
    }

    private void queryCart() {
        ParseQuery<Cart> query = ParseQuery.getQuery(Cart.class);
        query.setLimit(20);
        query.whereEqualTo(Cart.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Cart>() {
            @Override
            public void done(List<Cart> carts, ParseException e) {
                if (e != null) {
                    return;
                }
                allCarts.addAll(carts);
                fragmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void removeItem() {
        allCarts.remove(0);
    }
}