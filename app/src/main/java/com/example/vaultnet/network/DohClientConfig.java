package com.example.vaultnet.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;
import okhttp3.logging.HttpLoggingInterceptor;

public class DohClientConfig {

    public static OkHttpClient createDohClient() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient bootstrap = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS))
                .addInterceptor(logger)
                .build();

        DnsOverHttps doh = new DnsOverHttps.Builder()
                .client(bootstrap)
                .url(HttpUrl.get("https://dns.google/dns-query"))
                .bootstrapDnsHosts(ip("8.8.8.8"), ip("8.8.4.4"))
                .build();

        return new OkHttpClient.Builder()
                .dns(doh)
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS))
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build();
    }

    private static InetAddress ip(String ip) {
        try { return InetAddress.getByName(ip); }
        catch (UnknownHostException e) { throw new RuntimeException(e); }
    }
}