package com.example.pedidosapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosapp.R;
import com.example.pedidosapp.data.AppDatabase;
import com.example.pedidosapp.data.RetrofitClient;
import com.example.pedidosapp.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnSincronizar;
    private PedidoAdapter adapter;
    private List<Pedido> listaPedidos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);

        recyclerView = findViewById(R.id.recyclerView);
        btnSincronizar = findViewById(R.id.btnSincronizar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos iniciales
        cargarPedidosLocales();

        btnSincronizar.setOnClickListener(v -> sincronizarPedidosPendientes());
    }

    private void sincronizarPedidosPendientes() {
        btnSincronizar.setEnabled(false);
        btnSincronizar.setText("Sincronizando...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            String userId = getUsuarioActual();
            // Pasamos el ID al DAO
            List<Pedido> pendientes = AppDatabase.getDatabase(this).pedidoDao().getPedidosPorSincronizar(userId);

            if (pendientes.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "No hay pedidos pendientes", Toast.LENGTH_SHORT).show();
                    btnSincronizar.setEnabled(true);
                    btnSincronizar.setText("Sincronizar Ahora");
                });
                return;
            }

            // Iterar y enviar uno por uno
            for (Pedido p : pendientes) {
                enviarPedidoAPI(p);
            }
        });
    }

    private void enviarPedidoAPI(Pedido pedido) {
        String token = "Bearer " + getToken();

        // Nota: Asegúrate que tu API soporte el objeto Pedido tal cual,
        // a veces hay que convertirlo a un DTO específico según Swagger.
        RetrofitClient.getApiService().crearPedido(token, pedido).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    pedido.setEstado("SINCRONIZADO");
                    pedido.setMensajeError("");
                    actualizarPedidoEnBD(pedido);
                } else {
                    // 1. Capturamos el error
                    String errorMsg = "Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            // Leemos el mensaje que nos manda el servidor (C#)
                            errorMsg += "\nDetalle: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg += "\nError al leer cuerpo: " + e.getMessage();
                    }

                    // 2. Guardamos en BD
                    pedido.setEstado("ERROR");
                    pedido.setMensajeError(errorMsg);
                    actualizarPedidoEnBD(pedido);

                    // 3. ¡AQUÍ ESTÁ LA SOLUCIÓN!
                    // Usamos Log.e para verlo en Android Studio
                    android.util.Log.e("ERROR_API", errorMsg);

                    // 4. Y mostramos una ALERTA en pantalla (que sí se puede leer completa)
                    String finalErrorMsg = errorMsg;
                    runOnUiThread(() -> {
                        new androidx.appcompat.app.AlertDialog.Builder(ListaPedidosActivity.this)
                                .setTitle("Error de Sincronización")
                                .setMessage(finalErrorMsg) // Aquí se verá todo el texto
                                .setPositiveButton("Entendido", null)
                                .show();
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                pedido.setEstado("ERROR");
                pedido.setMensajeError("Fallo Red: " + t.getMessage());
                actualizarPedidoEnBD(pedido);
                runOnUiThread(() -> Toast.makeText(ListaPedidosActivity.this, "Fallo Red: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void actualizarPedidoEnBD(Pedido p) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase.getDatabase(this).pedidoDao().update(p);

            // Recargar lista visualmente al terminar
            cargarPedidosLocales();

            runOnUiThread(() -> {
                btnSincronizar.setEnabled(true);
                btnSincronizar.setText("Sincronizar Ahora");
            });
        });
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getString("auth_token", "");
    }

    // 1. Método para obtener el usuario
    private String getUsuarioActual() {
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("user_email", "invitado");
    }

    // 2. Actualizar carga de lista
    private void cargarPedidosLocales() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String userId = getUsuarioActual();
            // Pasamos el ID al DAO
            listaPedidos = AppDatabase.getDatabase(this).pedidoDao().getAllPedidos(userId);

            runOnUiThread(() -> {
                if (adapter == null) {
                    adapter = new PedidoAdapter(listaPedidos);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.actualizarLista(listaPedidos);
                }
            });
        });
    }
}
