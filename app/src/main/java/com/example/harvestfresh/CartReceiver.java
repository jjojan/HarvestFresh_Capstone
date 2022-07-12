package com.example.harvestfresh;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CartReceiver extends BroadcastReceiver {
    private final int INTENT_CODE = 0;
    private final int NOTIFY_ID = 123;
    private final int CART_LIMIT = 20;
    private final String CHANNEL_ID = "HarvestFresh";
    private final String NOTIFY_TITLE = "Your Cart has been Deleted";
    private final String NOTIFY_TEXT = "Your cart has been idle for too long and has been deleted!";

    private List<Cart> allCarts;
    private CartAdapter fragmentAdapter;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        allCarts = new ArrayList<>();
//        fragmentAdapter = new CartAdapter(context, allCarts, );
        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                INTENT_CODE,
                i,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.hficon2)
                .setContentTitle(NOTIFY_TITLE)
                .setContentText(NOTIFY_TEXT)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFY_ID, builder.build());
        queryCart();
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
                for (Cart cart : allCarts) {
                    cart.deleteInBackground();
                }
            }
        });
    }
}
