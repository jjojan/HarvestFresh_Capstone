package com.example.harvestfresh;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private final int INTENT_CODE = 0;
    private final int NOTIFY_ID = 123;
    private final String CHANNEl_ID = "HarvestFresh";
    private final String NOTIFY_TITLE = "HarvestFresh - Your Cart is Waiting";
    private final String NOTIFY_TEXT = "Your cart has been idle for 20 minutes, if you don't checkout it will be deleted soon.";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                INTENT_CODE,
                i,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEl_ID)
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
