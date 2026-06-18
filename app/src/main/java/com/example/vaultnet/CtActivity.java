package com.example.vaultnet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultnet.network.HttpClientConfig;
import com.example.vaultnet.security.CertificateTransparencyChecker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.OkHttpClient;

public class CtActivity extends AppCompatActivity {

    private TextInputEditText etDomain;
    private TextView          tvResult;
    private OkHttpClient      client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ct);

        etDomain = findViewById(R.id.etDomain);
        tvResult = findViewById(R.id.tvResult);
        MaterialButton btnCheck = findViewById(R.id.btnCheck);
        MaterialButton btnBack  = findViewById(R.id.btnBack);

        client = HttpClientConfig.createSecureClient();

        btnCheck.setOnClickListener(v -> check());
        btnBack.setOnClickListener(v -> finish());
    }

    private void check() {
        String domain = etDomain.getText() != null
                ? etDomain.getText().toString().trim() : "";
        if (domain.isEmpty()) { tvResult.setText("Entrez un domaine"); return; }

        tvResult.setText("Vérification en cours…");

        CertificateTransparencyChecker.check(client, domain,
                new CertificateTransparencyChecker.CTCallback() {
                    @Override
                    public void onResult(boolean inCT, String details) {
                        runOnUiThread(() -> tvResult.setText(
                                "Résultat pour : " + domain + "\n\n" +
                                        (inCT ? "✅ Certificat dans les logs CT\n\n"
                                                : "❌ Certificat NON dans les logs CT\n\n") +
                                        details));
                    }
                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> tvResult.setText("❌ " + message));
                    }
                });
    }
}