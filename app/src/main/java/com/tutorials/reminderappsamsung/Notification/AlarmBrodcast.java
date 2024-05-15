package com.tutorials.reminderappsamsung.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.tutorials.reminderappsamsung.R;

public class AlarmBrodcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("event");
        String date = bundle.getString("date") + " " + bundle.getString("time");
        int id = bundle.getInt("idNotification");
        String location = bundle.getString("location");
        String description = bundle.getString("description");


//        Log.v("TAGY1", "ID: " + id);
        //Click on Notification
//        Intent notifyIntent = new Intent(context, NotificationMessage.class);
//        // Set the Activity to start in a new, empty task.
//        notifyIntent.putExtra("message", text);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        // Create the PendingIntent.
//        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
//                context, id, notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // Create an Intent for the activity you want to start.
        Intent resultIntent = new Intent(context, NotificationMessage.class);
        resultIntent.putExtra("message_title", text);
        resultIntent.putExtra("message_datetime", date);
        resultIntent.putExtra("message_location", location);
        resultIntent.putExtra("message_description", description);

        // Create the TaskStackBuilder and add the intent, which inflates the back
        // stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack.
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);



//        Intent intent1 = new Intent(context, NotificationMessage.class);
//        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        //Notification Builder
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_IMMUTABLE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");


        //here we set all the properties for the notification
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        //contentView.setImageViewResource(R.id.icon, R.drawable.baseline_access_alarm_24);
//        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
//        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
        contentView.setTextViewText(R.id.message, text);
        contentView.setTextViewText(R.id.date, date);


        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.reminder);
        mBuilder.setSmallIcon(R.drawable.alarm)
                .setAutoCancel(true)
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setLargeIcon(largeIcon)
                .setContentTitle(text)
                .setContentText(date);
//                .setCustomContentView(contentView);
//                        .setContent(contentView)
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        mBuilder.setContentIntent(resultPendingIntent);

        //we have to create notification channel after api level 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        Notification notification = mBuilder.build();
        notificationManager.notify(id, notification);
    }
}

