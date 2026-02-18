package com.example.pedidosapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pedidosapp.MainActivity;
import com.example.pedidosapp.R;
import com.example.pedidosapp.data.RetrofitClient;
import com.example.pedidosapp.model.LoginRequest;
import com.example.pedidosapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername; // Aquí el usuario escribirá su email
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Auto-login: Si ya hay token, pasamos directo
        if (getToken() != null) {
            irAMainActivity();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Listener para ir a la pantalla de registro
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Configurar hint para que el usuario sepa que debe ingresar un correo
        etUsername.setHint("Correo electrónico (ej: user@test.com)");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bloquear botón para evitar doble clic
        btnLogin.setEnabled(false);
        btnLogin.setText("Conectando...");

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("INICIAR SESIÓN");

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    // Guardar token y email
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("auth_token", token)
                            .putString("user_email", email)
                            .apply();

                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    irAMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Datos incorrectos o usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("INICIAR SESIÓN");
                Toast.makeText(LoginActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getToken() {
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("auth_token", null);
    }

    private void irAMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
