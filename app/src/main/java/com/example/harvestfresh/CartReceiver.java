package com.example.harvestfresh;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CartReceiver extends BroadcastReceiver {
    private final int INTENT_CODE = 0;
    private final int NOTIFY_ID = 123;
    private final String CHANNEL_ID = "HarvestFresh";
    private final String NOTIFY_TITLE = "Your Cart has been Deleted";
    private final String NOTIFY_TEXT = "Your cart has been idle for too long and has been deleted!";

    private CartAdapter cart;

    @Override
    public void onReceive(Context context, Intent intent) {
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
    }
}
