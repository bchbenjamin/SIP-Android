package com.sip.guardian.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.sip.guardian.R;
import com.sip.guardian.data.mapper.IncidentMapper;
import com.sip.guardian.data.local.dao.IncidentDao;
import com.sip.guardian.data.remote.websocket.SipWebSocketClient;
import com.sip.guardian.data.remote.websocket.WebSocketEvent;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Foreground service holding the WebSocket while the app is in the foreground /
 * shortly after backgrounding (plan §27). It is deliberately NOT the sole
 * notification mechanism: when the process is killed, notification delivery is a
 * provider concern (local notifications now; FCM can be slotted in behind the
 * NotificationService abstraction) and missed events are reconciled via REST
 * when the app resumes.
 */
@AndroidEntryPoint
public class WebSocketService extends Service {

    public static final String CHANNEL_ID = "sip_events";
    private static final int NOTIFICATION_ID = 1;
    public static final String EXTRA_BASE_URL = "base_url";

    @Inject SipWebSocketClient webSocketClient;
    @Inject IncidentDao incidentDao;
    @Inject IncidentMapper incidentMapper;

    private final SipWebSocketClient.Listener listener = new SipWebSocketClient.Listener() {
        @Override
        public void onEvent(WebSocketEvent event) {
            if (event instanceof WebSocketEvent.OnIncidentReceived) {
                WebSocketEvent.OnIncidentReceived e = (WebSocketEvent.OnIncidentReceived) event;
                incidentDao.upsert(incidentMapper.toEntity(e.incident));
                NotificationService.notifyNewIncident(WebSocketService.this,
                        e.incident.id,
                        e.incident.threat != null ? e.incident.threat.type : "UNKNOWN",
                        e.incident.threat != null ? e.incident.threat.description : "");
            } else if (event instanceof WebSocketEvent.OnSystemAlert) {
                WebSocketEvent.OnSystemAlert e = (WebSocketEvent.OnSystemAlert) event;
                NotificationService.notifySystemAlert(WebSocketService.this,
                        e.level, e.message);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        webSocketClient.addListener(listener);
        startForeground(NOTIFICATION_ID, buildForegroundNotification("Monitoring…"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(EXTRA_BASE_URL)) {
            webSocketClient.connect(intent.getStringExtra(EXTRA_BASE_URL));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        webSocketClient.removeListener(listener);
        webSocketClient.disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private Notification buildForegroundNotification(String text) {
        createChannel();
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("SIP Guardian")
                .setContentText(text)
                .setOngoing(true)
                .build();
    }

    static void createChannel() {
        // no-op pre-O; channel created in NotificationService on first call
    }
}
