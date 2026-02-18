package com.example.pedidosapp.data;

// IMPORTS CORREGIDOS
import com.example.pedidosapp.model.LoginRequest;
import com.example.pedidosapp.model.LoginResponse;
import com.example.pedidosapp.model.Pedido;
import com.example.pedidosapp.model.RegisterRequest;




import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/ApiAuth/Login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/Orders")
    Call<Void> crearPedido(@Header("Authorization") String token, @Body Pedido pedido);

    @POST("api/ApiAuth/Register")
    Call<Void> register(@Body RegisterRequest request);
}