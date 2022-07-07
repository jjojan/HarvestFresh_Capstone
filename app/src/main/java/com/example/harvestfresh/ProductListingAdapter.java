package com.example.harvestfresh;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
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
    private static final String CHANNEL_ID = "HarvestFresh";
    private static final String CHANNEL_NAME = "HarvestFreshChannel";
    private static final String CHANNEL_DESCRIPTION = "Channel for Alarm Manager";
    private static final long ALARM_TIME = 60000;
    private static final int ALARM_CODE = 0;
    private static final int FLAG_CODE = 0;

    private final Context context;

    public final List<ProductListing> products;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    public ProductListingAdapter(Context context, List<ProductListing> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product,
                        parent, false);
        createNotificationChannel();
        return new ViewHolder(view);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

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
            setAlarm();
        }

        private void setAlarm() {
            alarmManager = (AlarmManager) context.
                    getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmReceiver.class);

            pendingIntent = PendingIntent.getBroadcast(context,
                    ALARM_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE);

            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            ALARM_TIME, pendingIntent);
        }

    }
}
