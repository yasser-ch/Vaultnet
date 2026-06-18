package com.example.vaultnet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultnet.model.Post;
import com.example.vaultnet.network.ApiService;
import com.example.vaultnet.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResult = findViewById(R.id.tvResult);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnAction = findViewById(R.id.btnAction);

        ((TextView) findViewById(R.id.tvTitle)).setText("📡 Retrofit + TLS");
        ((TextView) findViewById(R.id.tvDesc)).setText(
                "Retrofit + OkHttp avec ConnectionSpec.MODERN_TLS — " +
                        "force TLS 1.2+ et cipher suites robustes. " +
                        "Récupère les posts depuis jsonplaceholder.typicode.com.");

        btnAction.setText("Charger les posts");
        btnAction.setOnClickListener(v -> loadPosts());
        btnBack.setOnClickListener(v -> finish());

        loadPosts();
    }

    private void loadPosts() {
        tvResult.setText("Chargement…");
        ApiService api = RetrofitClient.create(ApiService.class);
        api.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvResult.setText("Erreur : " + response.code());
                    return;
                }
                List<Post> posts = response.body();
                StringBuilder sb = new StringBuilder();
                sb.append("✅ ").append(posts.size()).append(" posts récupérés\n");
                sb.append("Protocole : ").append(response.raw().protocol()).append("\n\n");
                for (int i = 0; i < Math.min(5, posts.size()); i++) {
                    Post p = posts.get(i);
                    sb.append("• #").append(p.getId()).append(" — ").append(p.getTitle()).append("\n");
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