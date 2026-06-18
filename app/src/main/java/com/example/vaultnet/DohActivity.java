package com.example.vaultnet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultnet.network.DohClientConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.Dns;
import okhttp3.OkHttpClient;

public class DohActivity extends AppCompatActivity {

    private TextInputEditText etDomain;
    private TextView          tvResult;
    private OkHttpClient      dohClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doh);

        etDomain  = findViewById(R.id.etDomain);
        tvResult  = findViewById(R.id.tvResult);
        MaterialButton btnResolve = findViewById(R.id.btnResolve);
        MaterialButton btnBack    = findViewById(R.id.btnBack);

        dohClient = DohClientConfig.createDohClient();

        btnResolve.setOnClickListener(v -> resolve());
        btnBack.setOnClickListener(v -> finish());
    }

    private void resolve() {
        String domain = etDomain.getText() != null
                ? etDomain.getText().toString().trim() : "";
        if (domain.isEmpty()) { tvResult.setText("Entrez un domaine"); return; }

        tvResult.setText("Résolution en cours…");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // DNS système
                long t0 = System.currentTimeMillis();
                List<InetAddress> sys = Dns.SYSTEM.lookup(domain);
                long sysMs = System.currentTimeMillis() - t0;

                // DNS over HTTPS
                long t1 = System.currentTimeMillis();
                List<InetAddress> doh = dohClient.dns().lookup(domain);
                long dohMs = System.currentTimeMillis() - t1;

                StringBuilder sb = new StringBuilder();
                sb.append("🔍 Domaine : ").append(domain).append("\n\n");
                sb.append("DNS Système (").append(sysMs).append("ms) :\n");
                for (InetAddress a : sys) sb.append("  • ").append(a.getHostAddress()).append("\n");
                sb.append("\nDNS-over-HTTPS (").append(dohMs).append("ms) :\n");
                for (InetAddress a : doh) sb.append("  • ").append(a.getHostAddress()).append("\n");
                sb.append("\n🛡️ Requêtes DNS chiffrées via dns.google");

                runOnUiThread(() -> tvResult.setText(sb.toString()));

            } catch (UnknownHostException e) {
                runOnUiThread(() -> tvResult.setText("❌ Erreur : " + e.getMessage()));
            }
        });
    }
}