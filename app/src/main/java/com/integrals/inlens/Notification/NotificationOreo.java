package com.integrals.inlens.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.integrals.inlens.R;


public class NotificationOreo extends ContextWrapper {

    private static final String Channel_ID = "123";
    private static final String Channel_NAME = "uploadNotification";
    private NotificationManager manager;


    public NotificationOreo(Context base) {
        super(base);
        CreateChannel();
    }

    private void CreateChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(Channel_ID,Channel_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setImportance(NotificationManager.IMPORTANCE_MIN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {

        if(manager==null)
        {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificationBuilder(String title, String body)
    {
        return new Notification.Builder(getApplicationContext(),Channel_ID).setContentText(body)
                .setContentTitle(title)
                .setProgress(100,0,true)
                .setSmallIcon(R.drawable.ic_notification).setOnlyAlertOnce(true);
    }
}
