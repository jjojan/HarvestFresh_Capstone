package com.example.harvestfresh.fragments;

import android.database.DataSetObserver;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.harvestfresh.Cart;
import com.example.harvestfresh.CartAdapter;
import com.example.harvestfresh.ProductListingAdapter;
import com.example.harvestfresh.R;
import com.example.harvestfresh.StoreFront;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";

    private static final int PRODUCT_SIZE = 20;
    private static final int REMOVE_INDEX = 0;
    private static final int POPUP_ZOOM = 0;
    private static final int CART_LIMIT = 20;
    private static final String DELETE_MESSAGE = "Item Deleted!";

    private Button btnCheckout;
    private Button btnConfirm;
    private Button btnCancel;
    private TextView tvTotal;
    private RecyclerView rvCart;
    private CartAdapter fragmentAdapter;
    private List<Cart> allCarts;
    private double totalPrice = 0;
    private FrameLayout flCart;

    public CartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCheckout = view.findViewById(R.id.btnCheckout);
        rvCart = view.findViewById(R.id.rvCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        flCart = view.findViewById(R.id.flCartLayout);

        allCarts = new ArrayList<>();

        fragmentAdapter = new CartAdapter(getContext(), allCarts, this);
        rvCart.setAdapter(fragmentAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rvCart);

        queryCart();

        rvCart.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                getTotal();
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartCheckout();
            }
        });
    }

    public void getTotal() {
        totalPrice = 0;
        for (Cart cart: allCarts) {
            totalPrice += Double.parseDouble(cart.getPrice().getProductPrice());
        }
        tvTotal.setText(Double.toString(totalPrice));
    }

    private void queryCart() {
        ParseQuery<Cart> query = ParseQuery.getQuery(Cart.class);
        query.setLimit(CART_LIMIT);
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

    private void cartCheckout() {
        View popupView = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.popup_checkout, null);
        final PopupWindow popupWindow =
                new PopupWindow(popupView,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);

        btnConfirm = popupView.findViewById(R.id.btnConfirm);
        btnCancel = popupView.findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Cart cart : allCarts) {
                    cart.deleteInBackground();
                }
                fragmentAdapter.clear();
                popupWindow.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(popupView, POPUP_ZOOM, POPUP_ZOOM);
    }
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Snackbar snackbar = Snackbar.make(flCart, DELETE_MESSAGE, Snackbar.LENGTH_SHORT);
            snackbar.show();
            int position = viewHolder.getAdapterPosition();
            Cart cart = allCarts.get(position);
            cart.deleteInBackground();
            allCarts.remove(viewHolder.getAdapterPosition());
            fragmentAdapter.notifyDataSetChanged();
        }
    };
}