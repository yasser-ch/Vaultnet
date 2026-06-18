package com.example.vaultnet.network;

import com.example.vaultnet.model.Post;
import com.example.vaultnet.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("users/{userId}")
    Call<User> getUser(@Path("userId") int userId);
}