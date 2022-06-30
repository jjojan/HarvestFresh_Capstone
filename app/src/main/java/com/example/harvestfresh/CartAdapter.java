package com.example.harvestfresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private final List<Cart> carts;

    public CartAdapter(Context context, List<Cart> carts) {
        this.context = context;
        this.carts = carts;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Cart cart = carts.get(position);
        holder.bind(cart);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public void clear() {
        carts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Cart> list) {
        carts.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvProduct;
        private final TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(Cart cart){
            tvPrice.setText(cart.getPrice().getProductPrice());
            tvProduct.setText(cart.getProduct());
        }
    }
}
