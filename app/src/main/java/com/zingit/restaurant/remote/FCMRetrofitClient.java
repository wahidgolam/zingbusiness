package com.zingit.restaurant.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMRetrofitClient {
    public static Retrofit instance;
    private OkHttpClient.Builder builder = new OkHttpClient.Builder();

    public static Retrofit getRetrofitInstance(){
        Gson gson = new GsonBuilder().setLenient().create();
        return instance == null ? new Retrofit.Builder()
                .baseUrl("https://us-central1-zing-user.cloudfunctions.net/sendFCM/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build(): instance;
    }
}
