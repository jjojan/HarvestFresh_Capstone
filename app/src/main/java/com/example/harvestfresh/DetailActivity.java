package com.example.harvestfresh;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity {

    private static int PRODUCT_LIMIT = 20;
    private static String OFFLINE_MESSAGE = "You are offline and cannot buy any products.";

    private RecyclerView rvProduct;
    private ProductListingAdapter fragmentAdapter;
    private List<ProductListing> allProducts;

    StoreFront store;
    TextView tvStoreName;
    ImageView ivStoreImage;
    TextView tvEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvStoreName = (TextView) findViewById(R.id.tvStoreName);
        ivStoreImage = (ImageView) findViewById(R.id.ivStoreImage);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);

        store = (StoreFront) Parcels.unwrap(getIntent().getParcelableExtra(StoreFront.class.getSimpleName()));

        tvStoreName.setText(store.getName());

        ParseFile photo = store.getImage();
        if (photo != null) {
            Glide.with(this).load(photo.getUrl()).into(ivStoreImage);
        }

        rvProduct = findViewById(R.id.rvProducts);
        rvProduct.setLayoutManager(new LinearLayoutManager(this));

        allProducts = new ArrayList<>();
        fragmentAdapter = new ProductListingAdapter(this, allProducts);
        rvProduct.setAdapter(fragmentAdapter);

        if (isNetworkConnected() == false) {
            Toasty.warning(getApplicationContext(), OFFLINE_MESSAGE, Toast.LENGTH_SHORT, true).show();
        }
        queryProducts();
    }

    private void queryProducts() {
        ParseQuery<ProductListing> query = ParseQuery.getQuery(ProductListing.class);
        query.setLimit(PRODUCT_LIMIT);
        query.whereEqualTo(ProductListing.KEY_USER, store.getUser());

        query.findInBackground(new FindCallback<ProductListing>() {
            @Override
            public void done(List<ProductListing> products, ParseException e) {
                if (e != null) {
                    return;
                }
                allProducts.addAll(products);
                fragmentAdapter.notifyDataSetChanged();
                if (allProducts.size() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
