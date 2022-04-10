package com.example.damxat.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Constants {
    public static final String SERVER_KEY = "AAAAxcmZg1Q:APA91bH2kzRgqAapeYOF9nt7SkJj4wNSgzhkITDHJTUliPY49okV5nISmelR4UEJuiwxuxSWGwTuug7weg72q6LXJWA6rq1ggW7BtCxvsWaInHXoMMvgvB-JCRjyHG8EH6gqhEQ3aoEr";
    public static final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String CONTENT_TYPE = "application/json";
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
