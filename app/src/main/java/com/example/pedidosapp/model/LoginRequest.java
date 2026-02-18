package com.example.pedidosapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    // Usamos @SerializedName para asegurarnos que el JSON
    // viaje exactamente con la palabra "email" que pide el servidor
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}