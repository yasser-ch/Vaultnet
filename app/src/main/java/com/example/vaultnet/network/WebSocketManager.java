package com.example.vaultnet.network;

import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private OkHttpClient client;
    private WebSocket webSocket;
    private final Callback callback;

    public interface Callback {
        void onOpen();
        void onMessage(String text);
        void onClosing(int code, String reason);
        void onClosed(int code, String reason);
        void onFailure(Throwable t);
    }

    public WebSocketManager(Callback callback) {
        this.callback = callback;
        client = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS))
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }

    public void connect(String url) {
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override public void onOpen(WebSocket ws, Response r) {
                Log.d(TAG, "Ouvert");
                if (callback != null) callback.onOpen();
            }
            @Override public void onMessage(WebSocket ws, String text) {
                Log.d(TAG, "Message : " + text);
                if (callback != null) callback.onMessage(text);
            }
            @Override public void onMessage(WebSocket ws, ByteString bytes) {}
            @Override public void onClosing(WebSocket ws, int code, String reason) {
                if (callback != null) callback.onClosing(code, reason);
            }
            @Override public void onClosed(WebSocket ws, int code, String reason) {
                if (callback != null) callback.onClosed(code, reason);
            }
            @Override public void onFailure(WebSocket ws, Throwable t, Response r) {
                Log.e(TAG, "Erreur", t);
                if (callback != null) callback.onFailure(t);
            }
        });
    }

    public boolean send(String message) {
        return webSocket != null && webSocket.send(message);
    }

    public boolean close(int code, String reason) {
        return webSocket != null && webSocket.close(code, reason);
    }
}