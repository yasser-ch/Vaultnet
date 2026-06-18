package com.example.vaultnet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultnet.network.DohClientConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FinalActivity extends AppCompatActivity {

    private TextInputEditText etUrl;
    private TextView          tvResult;
    private OkHttpClient      secureClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        etUrl    = findViewById(R.id.etUrl);
        tvResult = findViewById(R.id.tvResult);
        MaterialButton btnExecute = findViewById(R.id.btnExecute);
        MaterialButton btnBack    = findViewById(R.id.btnBack);

        secureClient = DohClientConfig.createDohClient();
        tvResult.setText("✅ Client initialisé\n• TLS moderne\n• DNS-over-HTTPS (Google)\n• Certificate Pinning (via network_security_config)");

        btnExecute.setOnClickListener(v -> execute());
        btnBack.setOnClickListener(v -> finish());
    }

    private void execute() {
        String url = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
        if (url.isEmpty()) url = "https://jsonplaceholder.typicode.com/posts/1";

        tvResult.setText("Requête en cours…");
        final String finalUrl = url;

        secureClient.newCall(new Request.Builder().url(finalUrl).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> tvResult.setText("❌ Échec : " + e.getMessage()));
                    }
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body() != null
                                ? response.body().string() : "vide";
                        String proto = response.protocol().toString();
                        int code = response.code();
                        runOnUiThread(() -> tvResult.setText(
                                "URL : " + finalUrl + "\n\n" +
                                        "Protocole : " + proto + "\n" +
                                        "Code : " + code + "\n\n" +
                                        "🛡️ Sécurité appliquée :\n" +
                                        "  • TLS moderne\n" +
                                        "  • DNS-over-HTTPS\n" +
                                        "  • Certificate Pinning\n\n" +
                                        "Réponse :\n" + body.substring(0, Math.min(300, body.length()))));
                    }
                });
    }
}