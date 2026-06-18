package com.example.vaultnet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnRetrofit).setOnClickListener(v ->
                startActivity(new Intent(this, RetrofitActivity.class)));

        findViewById(R.id.btnHttp3).setOnClickListener(v ->
                startActivity(new Intent(this, Http3Activity.class)));

        findViewById(R.id.btnWebSocket).setOnClickListener(v ->
                startActivity(new Intent(this, WebSocketActivity.class)));

        findViewById(R.id.btnMtls).setOnClickListener(v ->
                startActivity(new Intent(this, MtlsActivity.class)));

        findViewById(R.id.btnDoh).setOnClickListener(v ->
                startActivity(new Intent(this, DohActivity.class)));

        findViewById(R.id.btnCt).setOnClickListener(v ->
                startActivity(new Intent(this, CtActivity.class)));

        findViewById(R.id.btnFinal).setOnClickListener(v ->
                startActivity(new Intent(this, FinalActivity.class)));
    }
}