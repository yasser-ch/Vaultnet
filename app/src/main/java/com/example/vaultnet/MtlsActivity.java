package com.example.vaultnet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MtlsActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResult = findViewById(R.id.tvResult);
        MaterialButton btnBack   = findViewById(R.id.btnBack);
        MaterialButton btnAction = findViewById(R.id.btnAction);

        ((TextView) findViewById(R.id.tvTitle)).setText("🔐 Mutual TLS (mTLS)");
        ((TextView) findViewById(R.id.tvDesc)).setText(
                "mTLS = authentification mutuelle client + serveur via certificats X.509.\n\n" +
                        "Le client présente son certificat (PKCS12) stocké dans les assets.\n" +
                        "Le serveur vérifie ce certificat avant d'accepter la connexion.\n\n" +
                        "En production : générer les certificats avec OpenSSL et les placer dans assets/.");

        btnAction.setText("Voir la configuration");
        btnAction.setOnClickListener(v -> showConfig());
        btnBack.setOnClickListener(v -> finish());

        showConfig();
    }

    private void showConfig() {
        tvResult.setText(
                "━━ Configuration mTLS ━━\n\n" +
                        "1. Générer les certificats (OpenSSL) :\n" +
                        "   openssl genrsa -out ca.key 2048\n" +
                        "   openssl req -new -x509 -key ca.key -out ca.crt\n" +
                        "   openssl genrsa -out client.key 2048\n" +
                        "   openssl pkcs12 -export -out client.p12 \\\n" +
                        "     -inkey client.key -in client.crt\n\n" +
                        "2. Placer client.p12 dans app/src/main/assets/\n\n" +
                        "3. Charger dans OkHttp :\n" +
                        "   KeyStore ks = KeyStore.getInstance(\"PKCS12\")\n" +
                        "   ks.load(assets.open(\"client.p12\"), pwd)\n" +
                        "   SSLContext ssl = SSLContext.getInstance(\"TLS\")\n" +
                        "   ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null)\n" +
                        "   client.sslSocketFactory(ssl.getSocketFactory(), x509tm)\n\n" +
                        "✅ Chaque requête présente le certificat client automatiquement."
        );
    }
}