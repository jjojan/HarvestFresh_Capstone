package com.example.harvestfresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseUser;

import java.util.List;

public class ProductListingAdapter extends RecyclerView.Adapter<ProductListingAdapter.ViewHolder> {
    public static final String CART_ADD = "An Item has been added";

    private final Context context;

    public final List<ProductListing> products;

    public ProductListingAdapter(Context context, List<ProductListing> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListingAdapter.ViewHolder holder, int position) {
        ProductListing product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void clear() {
        products.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ProductListing> list) {
        products.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvProductPrice;
        private final TextView tvProductName;
        private final ImageButton ibAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductPrice = itemView.findViewById(R.id.tvPrice);
            tvProductName = itemView.findViewById(R.id.tvProduct);
            ibAdd = itemView.findViewById(R.id.ibRemove);

        }

        public void bind(ProductListing product) {
            tvProductPrice.setText(product.getProductPrice());
            tvProductName.setText(product.getProductName());

            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItem();
                }
            });
        }

        private void addItem() {
            Cart cart = new Cart();
            int position = getAdapterPosition();
            ProductListing product = products.get(position);
            cart.setProduct(product.getProductName());
            cart.setPrice(product);
            cart.setUser(ParseUser.getCurrentUser());
            cart.saveInBackground();
            notifyDataSetChanged();

            Toast.makeText(context.getApplicationContext(),
                    CART_ADD,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
