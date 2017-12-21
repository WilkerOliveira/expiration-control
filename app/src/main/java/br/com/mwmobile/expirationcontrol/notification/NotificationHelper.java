package br.com.mwmobile.expirationcontrol.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.ui.activities.MainActivity;

/**
 * Notification Helper class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 14/12/2017
 */
public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ONE_ID = "br.com.mwmobile.expirationcontrol.notification.EXPIRATION";
    public static final String CHANNEL_ONE_NAME = "Expiration";
    private final PendingIntent contentPendingIntent;
    private final int NOTIFICATION_ID = 1;
    private NotificationManager notifManager;

    /**
     * Constructor
     *
     * @param base Base context
     */
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }

        //Set up the notification content intent to launch the app when clicked
        Intent intent = new Intent(base, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.contentPendingIntent = PendingIntent.getActivity(base, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

    }

    /**
     * Create de Channels
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, android.app.NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(notificationChannel);
    }

    /**
     * Create the notification and Notify
     */
    public void startNotify() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(getString(R.string.content_title_products_expiring))
                    .setContentText(getString(R.string.content_text_notification))
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.drawable.ic_barcode_scan)
                    .setAutoCancel(true);

            getManager().notify(NOTIFICATION_ID, notificationBuilder.build());
        } else {
            NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(getString(R.string.content_title_products_expiring))
                    .setContentText(getString(R.string.content_text_notification))
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.drawable.ic_barcode_scan)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

            getManager().notify(NOTIFICATION_ID, notificationCompatBuilder.build());
        }
    }

    /**
     * Send your notifications to the NotificationManager system service
     *
     * @return Notification Manager
     */
    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}