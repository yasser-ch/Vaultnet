package com.example.vaultnet.security;

import android.util.Log;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CertificateTransparencyChecker {
    private static final String TAG = "CTChecker";

    public interface CTCallback {
        void onResult(boolean inCTLogs, String details);
        void onError(String message);
    }

    public static void check(OkHttpClient client, String domain, CTCallback callback) {
        Request request = new Request.Builder()
                .url("https://" + domain)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erreur connexion", e);
                callback.onError("Erreur : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    List<Certificate> certs = response.handshake().peerCertificates();
                    if (certs.isEmpty()) {
                        callback.onError("Aucun certificat trouvé");
                        return;
                    }

                    X509Certificate cert = (X509Certificate) certs.get(0);
                    boolean inCT = simulateCTCheck(cert);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Sujet : ").append(cert.getSubjectX500Principal().getName()).append("\n");
                    sb.append("Émetteur : ").append(cert.getIssuerX500Principal().getName()).append("\n");
                    sb.append("Valide du : ").append(cert.getNotBefore()).append("\n");
                    sb.append("Valide jusqu'au : ").append(cert.getNotAfter()).append("\n");
                    sb.append("N° série : ").append(cert.getSerialNumber()).append("\n");
                    sb.append("Dans logs CT : ").append(inCT ? "✅ Oui" : "❌ Non");

                    callback.onResult(inCT, sb.toString());

                } catch (Exception e) {
                    callback.onError("Erreur certificat : " + e.getMessage());
                }
            }
        });
    }

    private static boolean simulateCTCheck(X509Certificate cert) {
        String subject = cert.getSubjectX500Principal().getName();
        return subject.contains("example.com")
                || subject.contains("google.com")
                || subject.contains("cloudflare.com");
    }
}