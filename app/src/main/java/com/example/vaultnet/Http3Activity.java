package com.example.vaultnet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultnet.model.Post;
import com.example.vaultnet.network.ApiService;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Http3Activity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResult = findViewById(R.id.tvResult);
        MaterialButton btnBack   = findViewById(R.id.btnBack);
        MaterialButton btnAction = findViewById(R.id.btnAction);

        ((TextView) findViewById(R.id.tvTitle)).setText("⚡ HTTP/3 QUIC");
        ((TextView) findViewById(R.id.tvDesc)).setText(
                "HTTP/3 utilise QUIC sur UDP — combine transport et sécurité, " +
                        "élimine le head-of-line blocking. " +
                        "Fallback automatique vers HTTP/2 ou HTTP/1.1.");

        btnAction.setText("Tester HTTP/3");
        btnAction.setOnClickListener(v -> testHttp3());
        btnBack.setOnClickListener(v -> finish());

        testHttp3();
    }

    private void testHttp3() {
        tvResult.setText("Test en cours…");

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Arrays.asList(
                        Protocol.HTTP_2,
                        Protocol.HTTP_1_1))
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(ApiService.class).getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                String proto = response.raw().protocol().toString();
                StringBuilder sb = new StringBuilder();
                sb.append("Protocole négocié : ").append(proto).append("\n");
                sb.append("Code : ").append(response.code()).append("\n\n");
                if (response.isSuccessful() && response.body() != null) {
                    sb.append("✅ ").append(response.body().size()).append(" posts reçus\n");
                    sb.append(proto.equals("h3") ? "⚡ HTTP/3 actif" : "↩️ Fallback : " + proto);
                } else {
                    sb.append("❌ Erreur : ").append(response.code());
                }
                tvResult.setText(sb.toString());
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                tvResult.setText("❌ Échec : " + t.getMessage());
            }
        });
    }
}