package com.example.b2m.Helper;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.b2m.R;

public class NotificationHelper extends ContextWrapper {

    private static final String B2M_CHANNEL_ID = "com.example.b2m.bring2me";
    private static final String B2M_CHANNEL_NAME = "BRING2ME";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)//esta funcion finciona para android superior a oreo
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel b2mChannel = new NotificationChannel(B2M_CHANNEL_ID,
                B2M_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        b2mChannel.enableLights(true);
        b2mChannel.enableVibration(true);
        b2mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(b2mChannel);
    }

    public NotificationManager getManager() {
        if (manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getB2MChannelNotification(String title,
                                                                      String body,
                                                                      PendingIntent contentIntent,
                                                                      Uri soundUri)
    {
        return new android.app.Notification.Builder(getApplicationContext(),B2M_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

}
