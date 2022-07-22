package com.example.harvestfresh;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProductListingAdapter extends RecyclerView.Adapter<ProductListingAdapter.ViewHolder> {
    public static final String CART_ADD = "An item has been added to your cart";
    private static final String CHANNEL_ID = "HarvestFresh";
    private static final String CHANNEL_NAME = "HarvestFreshChannel";
    private static final String PUSH_ID = "Reminders";
    private static final String CHANNEL_DESCRIPTION = "Channel for Alarm Manager";
    private static final String PUSH_MESSAGE = "Item has been added to your cart, be sure to checkout soon.";
    private static final String SENDER_ID = "GCMSenderId";
    private static final String SENDER_CODE = "808974419018";
    private static final String CHANNELS = "channels";
    private static final String BUTTON_TEXT = "OK";
    private static final long ALARM_WAITING_PERIOD_MINS = 1;
    private static final long ALARM_WAITING_PERIOD_MILLIS = Duration.ofMinutes(ALARM_WAITING_PERIOD_MINS).toMillis();
    private static final int ALARM_CODE = 0;
    private static final long CART_WAITING_PERIOD_MINS = 50;
    private static final long CART_WAITING_PERIOD_MILLIS = Duration.ofMinutes(CART_WAITING_PERIOD_MINS).toMillis();
    private static final int CART_CODE = 1;

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
                        parent,
                        false);
        createNotificationChannel();
        return new ViewHolder(view);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        CharSequence name = CHANNEL_NAME;
        String description = CHANNEL_DESCRIPTION;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

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
            ibAdd = itemView.findViewById(R.id.ibAdd);
        }

        public void bind(ProductListing product) {
            tvProductPrice.setText(product.getProductPrice());
            tvProductName.setText(product.getProductName());

            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItem();
                    pushCloudReminder();
                }
            });
        }

        private void pushCloudReminder() {
            ArrayList<String> channels = new ArrayList<>();
            channels.add(PUSH_ID);
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put(SENDER_ID, SENDER_CODE);
            installation.put(CHANNELS, channels);
            installation.saveInBackground();
            final HashMap<String, String> params = new HashMap<>();


            ParseCloud.callFunctionInBackground(PUSH_ID, params, new FunctionCallback<Object>() {
                @Override
                public void done(Object response, ParseException exc) {
                    if(exc == null) {
                        // The function executed, but still has to check the response
                        alertDisplayer(PUSH_ID,PUSH_MESSAGE);
                    }
                    else {
                        // Something went wrong
                    }
                }
            });
        }

        private void alertDisplayer(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(BUTTON_TEXT, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog ok = builder.create();
            ok.show();
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

            Toasty.success(context.getApplicationContext(),
                    CART_ADD,
                    Toast.LENGTH_SHORT,
                    true).show();
            cancelAlarm();
            setAlarm(AlarmSwitch.ALARM_RECEIVER, ALARM_CODE, ALARM_WAITING_PERIOD_MILLIS);
            setAlarm(AlarmSwitch.CART_RECEIVER, CART_CODE, CART_WAITING_PERIOD_MILLIS);
        }

        private void cancelAlarm() {
            Intent intent = new Intent(context, AlarmReceiver.class);

            pendingIntent = PendingIntent.getBroadcast(context,
                    ALARM_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager == null) {
                if (context.getSystemService(Context.ALARM_SERVICE) == null) {
                    return;
                }
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);
        }
    }

    private void setAlarm(AlarmSwitch alarmSwitch, int alarmCode, long alarmWaitingPeriodMillis) {
        alarmManager = (AlarmManager) context.
                getSystemService(Context.ALARM_SERVICE);
        Intent intent = null;
        if (alarmSwitch.equals(null)) {
            return;
        } else if (alarmSwitch.equals(AlarmSwitch.ALARM_RECEIVER)) {
            intent = new Intent(context, AlarmReceiver.class);
        } else {
            intent = new Intent(context, CartReceiver.class);
        }

        pendingIntent = PendingIntent.getBroadcast(context,
                alarmCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + alarmWaitingPeriodMillis,
                pendingIntent);

    }

}
