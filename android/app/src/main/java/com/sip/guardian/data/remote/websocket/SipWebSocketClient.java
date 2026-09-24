package com.sip.guardian.data.remote.websocket;

import android.os.Handler;
import android.os.Looper;

import com.sip.guardian.data.local.SecureTokenStore;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Persistent WebSocket to the gateway with exponential-backoff reconnect
 * (1s -> 2s -> 4s -> ... -> 30s cap) per plan §10/§27.
 *
 * Auth: token is sent as a Sec-WebSocket-Protocol-style handshake header
 * ("Authorization: Bearer ...") instead of a URL query param, per plan §14.
 */
@Singleton
public class SipWebSocketClient {

    public interface Listener {
        void onEvent(WebSocketEvent event);
    }

    private static final long MIN_BACKOFF_MS = 1_000;
    private static final long MAX_BACKOFF_MS = 30_000;
    private static final long BACKOFF_MULTIPLIER = 2;

    private final OkHttpClient okHttpClient;
    private final SecureTokenStore tokenStore;
    private final WebSocketMessageParser parser;
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean stopped = new AtomicBoolean(true);

    private volatile WebSocket webSocket;
    private volatile long backoffMs = MIN_BACKOFF_MS;

    @Inject
    public SipWebSocketClient(OkHttpClient okHttpClient,
                              SecureTokenStore tokenStore,
                              WebSocketMessageParser parser) {
        this.okHttpClient = okHttpClient;
        this.tokenStore = tokenStore;
        this.parser = parser;
    }

    public void addListener(Listener listener) { listeners.add(listener); }
    public void removeListener(Listener listener) { listeners.remove(listener); }

    public synchronized void connect(String baseUrl) {
        stopped.set(false);
        openSocket(baseUrl);
    }

    public synchronized void disconnect() {
        stopped.set(true);
        handler.removeCallbacksAndMessages(null);
        if (webSocket != null) {
            webSocket.close(1000, "client disconnect");
            webSocket = null;
        }
        emit(new WebSocketEvent.OnConnectionStateChanged(
                WebSocketEvent.ConnectionState.DISCONNECTED, "manual"));
    }

    public boolean send(String json) {
        WebSocket ws = webSocket;
        return ws != null && ws.send(json);
    }

    private void openSocket(String baseUrl) {
        String token = tokenStore.getAccessToken();
        if (token == null) {
            scheduleReconnect(baseUrl);
            return;
        }

        // ws(s)://host/ws/events derived from the REST base URL; token via header, NOT URL.
        String wsUrl = baseUrl.replace("https://", "wss://")
                              .replace("http://", "ws://");
        if (!wsUrl.endsWith("/")) wsUrl += "/";
        wsUrl += "ws/events";

        Request request = new Request.Builder()
                .url(wsUrl)
                .header("Authorization", "Bearer " + token)
                .build();

        emit(new WebSocketEvent.OnConnectionStateChanged(
                WebSocketEvent.ConnectionState.CONNECTING, null));

        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                backoffMs = MIN_BACKOFF_MS;
                subscribe(webSocket, Set.of("incidents", "nodes"));
                emit(new WebSocketEvent.OnConnectionStateChanged(
                        WebSocketEvent.ConnectionState.CONNECTED, null));
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                WebSocketEvent event = parser.parse(text);
                if (event != null) emit(event);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                onMessage(webSocket, bytes.utf8());
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                emit(new WebSocketEvent.OnConnectionStateChanged(
                        WebSocketEvent.ConnectionState.FAILED,
                        t == null ? "unknown" : t.getMessage()));
                scheduleReconnect(baseUrl);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                if (!stopped.get()) {
                    emit(new WebSocketEvent.OnConnectionStateChanged(
                            WebSocketEvent.ConnectionState.DISCONNECTED, reason));
                    scheduleReconnect(baseUrl);
                }
            }
        });
    }

    private void subscribe(WebSocket ws, Set<String> channels) {
        ws.send("{\"type\":\"SUBSCRIBE\",\"payload\":{\"channels\":["
                + joinQuoted(channels) + "]}}");
    }

    private static String joinQuoted(Set<String> values) {
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (sb.length() > 0) sb.append(',');
            sb.append('"').append(v).append('"');
        }
        return sb.toString();
    }

    private void scheduleReconnect(String baseUrl) {
        if (stopped.get()) return;
        final long delay = backoffMs;
        backoffMs = Math.min(backoffMs * BACKOFF_MULTIPLIER, MAX_BACKOFF_MS);
        handler.postDelayed(() -> {
            if (!stopped.get()) openSocket(baseUrl);
        }, TimeUnit.MILLISECONDS.toMillis(delay));
    }

    private void emit(WebSocketEvent event) {
        for (Listener l : listeners) l.onEvent(event);
    }
}
