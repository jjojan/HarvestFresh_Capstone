package com.example.harvestfresh.fragments;

import android.database.DataSetObserver;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.harvestfresh.Cart;
import com.example.harvestfresh.CartAdapter;
import com.example.harvestfresh.CartRoom;
import com.example.harvestfresh.CartRoomDao;
import com.example.harvestfresh.HarvestFreshDatabase;
import com.example.harvestfresh.ProductListingAdapter;
import com.example.harvestfresh.R;
import com.example.harvestfresh.StoreFront;
import com.example.harvestfresh.StoreFrontDao;
import com.example.harvestfresh.StoreFrontRoom;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import es.dmoral.toasty.Toasty;

public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";

    private static final int PRODUCT_SIZE = 20;
    private static final int REMOVE_INDEX = 0;
    private static final int POPUP_ZOOM = 0;
    private static final int CART_LIMIT = 20;
    private static final String DELETE_MESSAGE = "Item Deleted!";
    private static final String DATABASE_NAME = "HarvestFreshDatabase";
    private static final String OFFLINE_MESSAGE = "You are offline and cant checkout.";

    private Button btnCheckout;
    private Button btnConfirm;
    private Button btnCancel;
    private TextView tvCartEmpty;
    private TextView tvTotal;
    private RecyclerView rvCart;
    private CartAdapter fragmentAdapter;
    private List<Cart> allCarts;
    private double totalPrice = 0;
    private List<CartRoom> savedCart;
    private CartRoomDao cartDao;
    public FrameLayout flCart;

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
        savedCart = new ArrayList<>();

        allCarts = new ArrayList<>();

        fragmentAdapter = new CartAdapter(getContext(), allCarts, this);
        rvCart.setAdapter(fragmentAdapter);

        HarvestFreshDatabase harvestFreshDatabase = Room.databaseBuilder(getContext().getApplicationContext(),
                HarvestFreshDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        cartDao = harvestFreshDatabase.cartRoomDao();

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rvCart);


        if (isNetworkConnected()) {
            queryCart();
        }
        else {
            Toasty.warning(getContext(), OFFLINE_MESSAGE, Toast.LENGTH_LONG, true).show();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<CartRoom> savedCarts = cartDao.recentCart();
                    List<Cart> cartsFromRoom = CartRoom.getCartRooms(savedCarts);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentAdapter.addAll(cartsFromRoom);
                        }
                    });
                }
            });

        }

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
                if (isNetworkConnected()){
                    cartCheckout();
                }
                else {
                    Toasty.warning(getContext(), OFFLINE_MESSAGE, Toast.LENGTH_LONG, true).show();
                }
            }
        });


    }

    public void getTotal() {
        totalPrice = 0;
        if (isNetworkConnected()) {
            for (Cart cart : allCarts) {
                totalPrice += Double.parseDouble(cart.getPrice().getProductPrice());
            }
            tvTotal.setText(String.format("%.2f", totalPrice));
        }
        else {
            for (CartRoom cartRoom : savedCart) {
                totalPrice += Double.parseDouble(cartRoom.productCost);
            }
            tvTotal.setText(String.format("%.2f", totalPrice));
        }
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
                for(Cart cart: carts) {
                    savedCart.add(cart.toCartRoom());
                }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        cartDao.insertCartRoom(savedCart.toArray(new CartRoom[0]));
                        List<CartRoom> savedStores = cartDao.recentCart();
                    }
                });
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

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}