package com.example.harvestfresh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvestfresh.fragments.CartFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private static final int ALARM_CODE = 0;
    private static final String DELETE_MESSAGE = "Item Deleted!";

    private final Context context;
    public final List<Cart> carts;
    private List<Cart> allCarts;
    private CartAdapter fragmentAdapter;
    private CartFragment cartFragment;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private FrameLayout flCart;

    public CartAdapter(Context context, List<Cart> carts, CartFragment cartFragment) {
        this.context = context;
        this.carts = carts;
        this.cartFragment = cartFragment;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_cart, parent, false);
        //attachToRoot set to false so AdapterView is not sent to parent
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

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    public void clear() {
        carts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Cart> list) {
        carts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvProduct;
        private final TextView tvPrice;
        private ImageButton ibRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ibRemove = itemView.findViewById(R.id.ibRemove);
        }

        public void bind(Cart cart) {
            tvPrice.setText(cart.getPrice().getProductPrice());
            tvProduct.setText(cart.getProduct());

            ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem();
                }
            });
        }

        private void removeItem() {
            int position = getAdapterPosition();
            Cart cart = carts.get(position);
            carts.remove(position);
            cart.deleteInBackground();
            notifyItemRemoved(position);
            notifyDataSetChanged();
            Snackbar snackbar = Snackbar.make(cartFragment.flCart, DELETE_MESSAGE, Snackbar.LENGTH_SHORT);
            snackbar.show();
            cancelAlarm();
        }

        private void cancelAlarm() {
            Intent intent = new Intent(context, AlarmReceiver.class);

            pendingIntent = PendingIntent.getBroadcast(context,
                    ALARM_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager == null) {
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);
        }

    }
}
