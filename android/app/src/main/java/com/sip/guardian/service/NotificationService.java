package com.sip.guardian.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sip.guardian.R;

/**
 * Local-notification abstraction (plan §27 open question).
 * A push provider (FCM) can be added behind this same entry point later
 * without touching incident/WebSocket architecture.
 */
public final class NotificationService {

    private static final String CHANNEL_ID = "sip_events";
    private static final String CHANNEL_ALERTS = "sip_alerts";
    private static int nextId = 100;

    private NotificationService() {}

    public static void notifyNewIncident(Context context, String incidentId,
                                         String threatType, String description) {
        ensureChannels(context);
        Intent open = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent pi = PendingIntent.getActivity(context, incidentId.hashCode(),
                open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Threat detected: " + threatType)
                .setContentText(description == null ? "" : description)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi);
        manager(context).notify(nextId++, builder.build());
    }

    public static void notifySystemAlert(Context context, String level, String message) {
        ensureChannels(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ALERTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("SIP " + (level == null ? "ALERT" : level))
                .setContentText(message == null ? "" : message)
                .setAutoCancel(true);
        manager(context).notify(nextId++, builder.build());
    }

    private static NotificationManager manager(Context context) {
        return context.getSystemService(NotificationManager.class);
    }

    private static void ensureChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = manager(context);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Incidents",
                NotificationManager.IMPORTANCE_HIGH));
        nm.createNotificationChannel(new NotificationChannel(CHANNEL_ALERTS, "System alerts",
                NotificationManager.IMPORTANCE_DEFAULT));
    }
}
