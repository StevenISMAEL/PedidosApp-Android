package com.example.pedidosapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pedidosapp.R;
import com.example.pedidosapp.data.AppDatabase;
import com.example.pedidosapp.data.RetrofitClient;
import com.example.pedidosapp.data.ExportUtils;
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
    private Button btnSincronizar, btnExportar, btnCerrarSesion;
    private PedidoAdapter adapter;
    private List<Pedido> listaPedidos = new ArrayList<>();

    private int pedidosPendientesDeProcesar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);

        // 1. Vincular Vistas
        recyclerView = findViewById(R.id.recyclerView);
        btnSincronizar = findViewById(R.id.btnSincronizar);
        btnExportar = findViewById(R.id.btnExportarCSV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Cargar la lista
        cargarPedidosLocales();

        // 3. Acciones de Botones
        btnSincronizar.setOnClickListener(v -> sincronizarPedidosPendientes());

        // LÓGICA DEL BOTÓN EXPORTAR
        btnExportar.setOnClickListener(v -> {
            if (listaPedidos != null && !listaPedidos.isEmpty()) {
                ExportUtils.exportarPedidosACV(this, listaPedidos);
                Toast.makeText(this, "Generando archivo CSV...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPedidosLocales() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Obtenemos el usuario actual para filtrar (Bonus Multi-usuario)
            String userEmail = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("user_email", "");
            listaPedidos = AppDatabase.getDatabase(this).pedidoDao().getAllPedidos(userEmail);

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

    private void sincronizarPedidosPendientes() {
        btnSincronizar.setEnabled(false);
        btnSincronizar.setText("Sincronizando...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String userEmail = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("user_email", "");
            List<Pedido> pendientes = AppDatabase.getDatabase(this).pedidoDao().getPedidosPorSincronizar(userEmail);

            if (pendientes.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "¡Todo al día!", Toast.LENGTH_SHORT).show();
                    resetBotonsincronizar();
                });
                return;
            }

            pedidosPendientesDeProcesar = pendientes.size();
            for (Pedido p : pendientes) {
                enviarPedidoAPI(p);
            }
        });
    }

    private void enviarPedidoAPI(Pedido pedido) {
        String token = "Bearer " + getToken();
        RetrofitClient.getApiService().crearPedido(token, pedido).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    pedido.setEstado("SINCRONIZADO");
                    pedido.setMensajeError("");
                    actualizarPedidoEnBD(pedido);
                } else {
                    pedido.setEstado("ERROR");
                    pedido.setMensajeError("Error: " + response.code());
                    actualizarPedidoEnBD(pedido);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                pedido.setEstado("ERROR");
                pedido.setMensajeError("Red: " + t.getMessage());
                actualizarPedidoEnBD(pedido);
            }
        });
    }

    private void actualizarPedidoEnBD(Pedido p) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase.getDatabase(this).pedidoDao().update(p);
            pedidosPendientesDeProcesar--;
            if (pedidosPendientesDeProcesar <= 0) {
                cargarPedidosLocales();
                runOnUiThread(() -> resetBotonsincronizar());
            }
        });
    }

    private void resetBotonsincronizar() {
        btnSincronizar.setEnabled(true);
        btnSincronizar.setText("☁️ Sincronizar Pendientes");
    }

    private String getToken() {
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("auth_token", "");
    }
}