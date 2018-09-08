package com.balanstudios.einar.workouttimer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class BaseApp extends Application {

    public static final String NOTIF_CHANNEL = "notifChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIF_CHANNEL,
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows timer while not in app");


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
