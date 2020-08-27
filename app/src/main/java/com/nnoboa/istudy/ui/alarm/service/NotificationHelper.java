package com.nnoboa.istudy.ui.alarm.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;


public class NotificationHelper extends ContextWrapper {

    public static final String ChannelID = "ChannelId";
    public static final String ChannelName = "Channel Name";
    NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {

        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel
                    notificationChannel =
                    new NotificationChannel(ChannelID, ChannelName, importance);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//            String ringtone = preferences.getString("general_ringtone", null);
//            Log.d("Helper", "Notification " + ringtone);
            AudioAttributes
                    builder =
                    new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
//            notificationChannel.setSound(Uri.parse(ringtone), builder);
            getManager().createNotificationChannel(notificationChannel);
        }

    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), ChannelID)
                .setAutoCancel(false);
    }


}
