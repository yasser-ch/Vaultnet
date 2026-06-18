package com.example.vaultnet;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultnet.network.WebSocketManager;
import com.google.android.material.button.MaterialButton;

public class WebSocketActivity extends AppCompatActivity implements WebSocketManager.Callback {

    private static final String WS_URL = "wss://echo.websocket.org";

    private TextView          tvStatus, tvResult;
    private EditText          etMessage;
    private MaterialButton    btnConnect, btnSend, btnBack;
    private WebSocketManager  manager;
    private StringBuilder     log = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);

        tvStatus  = findViewById(R.id.tvStatus);
        tvResult  = findViewById(R.id.tvResult);
        etMessage = findViewById(R.id.etMessage);
        btnConnect = findViewById(R.id.btnConnect);
        btnSend    = findViewById(R.id.btnSend);
        btnBack    = findViewById(R.id.btnBack);

        manager = new WebSocketManager(this);

        btnConnect.setOnClickListener(v -> {
            tvStatus.setText("Connexion…");
            manager.connect(WS_URL);
        });

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (msg.isEmpty()) return;
            boolean sent = manager.send(msg);
            if (sent) {
                appendLog("📤 Envoyé : " + msg);
                etMessage.setText("");
            } else {
                Toast.makeText(this, "Connecte-toi d'abord", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
        btnSend.setEnabled(false);
    }

    private void appendLog(String line) {
        log.insert(0, line + "\n");
        tvResult.setText(log.toString());
    }

    @Override public void onOpen() {
        runOnUiThread(() -> {
            tvStatus.setText("🟢 Connecté");
            tvStatus.setTextColor(getColor(R.color.teal_accent));
            btnSend.setEnabled(true);
            btnConnect.setEnabled(false);
            appendLog("✅ WebSocket ouvert sur " + WS_URL);
        });
    }

    @Override public void onMessage(String text) {
        runOnUiThread(() -> appendLog("📩 Reçu : " + text));
    }

    @Override public void onClosing(int code, String reason) {
        runOnUiThread(() -> tvStatus.setText("Fermeture…"));
    }

    @Override public void onClosed(int code, String reason) {
        runOnUiThread(() -> {
            tvStatus.setText("🔴 Déconnecté");
            tvStatus.setTextColor(getColor(R.color.danger));
            btnSend.setEnabled(false);
            btnConnect.setEnabled(true);
            appendLog("❌ Fermé : " + code + " " + reason);
        });
    }

    @Override public void onFailure(Throwable t) {
        runOnUiThread(() -> {
            tvStatus.setText("⚠️ Erreur");
            tvStatus.setTextColor(getColor(R.color.warning));
            btnSend.setEnabled(false);
            btnConnect.setEnabled(true);
            appendLog("⚠️ Erreur : " + t.getMessage());
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (manager != null) manager.close(1000, "Activité fermée");
    }
}