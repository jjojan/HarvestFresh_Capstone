package com.example.harvestfresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductListingAdapter extends RecyclerView.Adapter<ProductListingAdapter.ViewHolder> {
    private final Context context;
    private final List<ProductListing> products;

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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvProductPrice;
        private final TextView tvProductName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductPrice = itemView.findViewById(R.id.tvPrice);
            tvProductName = itemView.findViewById(R.id.tvProduct);
        }

        public void bind(ProductListing product) {
            tvProductPrice.setText(product.getProductPrice());
            tvProductName.setText(product.getProductName());
        }
    }
}
