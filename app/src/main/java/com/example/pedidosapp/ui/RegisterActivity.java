package com.example.pedidosapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pedidosapp.R;
import com.example.pedidosapp.data.RetrofitClient;
import com.example.pedidosapp.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Vincular vistas
        etName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Botón Registrar
        btnRegister.setOnClickListener(v -> registrarUsuario());

        // Botón "Volver al Login"
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Validaciones básicas antes de enviar
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bloquear botón para evitar clics dobles
        btnRegister.setEnabled(false);
        btnRegister.setText("Procesando registro...");

        // Usamos el modelo del Canvas (que ya incluye confirmPassword automáticamente)
        RegisterRequest request = new RegisterRequest(name, email, pass);

        // Llamada a tu API de Azure
        RetrofitClient.getApiService().register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("REGISTRARME");

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "¡Registro exitoso! Ya puede iniciar sesión.", Toast.LENGTH_LONG).show();
                    finish(); // Regresa al Login
                } else {
                    // SI FALLA: Capturamos el error detallado como hicimos en la lista
                    StringBuilder errorMsg = new StringBuilder("Código de error: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String detalle = response.errorBody().string();
                            errorMsg.append("\n\nDetalle del Servidor:\n").append(detalle);
                            // También lo mandamos al Logcat para verlo en Android Studio
                            Log.e("REGISTRO_ERROR", "Error " + response.code() + ": " + detalle);
                        }
                    } catch (Exception e) {
                        errorMsg.append("\nError al leer el cuerpo de la respuesta.");
                    }

                    // Mostramos la ALERTA que no se quita hasta que el usuario le de click
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("No se pudo crear la cuenta")
                            .setMessage(errorMsg.toString())
                            .setPositiveButton("Entendido", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("REGISTRARME");

                // Error de red (sin internet o servidor apagado)
                Log.e("REGISTRO_FAIL", t.getMessage());

                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Fallo de Conexión")
                        .setMessage("No se pudo contactar con el servidor.\n\nError: " + t.getMessage())
                        .setPositiveButton("Cerrar", null)
                        .show();
            }
        });
    }
}