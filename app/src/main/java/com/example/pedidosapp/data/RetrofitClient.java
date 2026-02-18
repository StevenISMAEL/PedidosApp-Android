package com.example.pedidosapp.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Recuerda cambiar esto por la URL de tu profesor
    private static final String BASE_URL = "https://apienterprice-cyeda5bcabgwfzep.canadacentral-01.azurewebsites.net/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}