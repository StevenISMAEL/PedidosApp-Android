package com.example.pedidosapp.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    // Usamos @SerializedName para coincidir con el RegisterViewModel de C#
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // AGREGADO: Campo requerido por tu API
    @SerializedName("confirmPassword")
    private String confirmPassword;

    // Constructor: Recibe los 3 datos básicos y duplica la contraseña para confirmPassword
    public RegisterRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = password; // Truco: Asignamos la misma contraseña automáticamente para cumplir con la API
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}