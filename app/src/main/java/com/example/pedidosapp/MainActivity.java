package com.example.pedidosapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pedidosapp.R;
import com.example.pedidosapp.data.AppDatabase;
import com.example.pedidosapp.model.Pedido;
// --- IMPORTANTE: IMPORTAR LAS ACTIVIDADES DE LA CARPETA UI ---
import com.example.pedidosapp.ui.LoginActivity;
import com.example.pedidosapp.ui.ListaPedidosActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etTelefono, etDireccion, etDetalle;
    private RadioGroup rgPago;
    private RadioButton rbEfectivo;
    private TextView tvUbicacion;
    private ImageView imgPreview;

    // Botones
    private Button btnFoto, btnGuardar, btnScanQR, btnVerLista, btnGps;
    private Button btnCerrarSesion;

    private double currentLat = 0.0;
    private double currentLong = 0.0;
    private String currentFotoPath = "";

    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissions();

        // Listeners
        btnFoto.setOnClickListener(v -> dispatchTakePictureIntent());

        btnGps.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Actualizando ubicación...", Toast.LENGTH_SHORT).show();
            obtenerUbicacion();
        });

        btnGuardar.setOnClickListener(v -> guardarPedidoEnSQLite());
        btnScanQR.setOnClickListener(v -> escanearCodigoQR());

        btnVerLista.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListaPedidosActivity.class));
        });

        // NUEVA LÓGICA DE CIERRE DE SESIÓN
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        obtenerUbicacion();
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etDetalle = findViewById(R.id.etDetalle);
        rgPago = findViewById(R.id.rgPago);
        rbEfectivo = findViewById(R.id.rbEfectivo);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        imgPreview = findViewById(R.id.imgPreview);

        btnFoto = findViewById(R.id.btnFoto);
        btnGps = findViewById(R.id.btnGps);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnVerLista = findViewById(R.id.btnVerLista);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionMain);
    }

    // --- LÓGICA DE CIERRE DE SESIÓN ---
    private void cerrarSesion() {
        // 1. Borrar Token
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // 2. Ir al Login
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ... (El resto de métodos: QR, GPS, Cámara, SQLite siguen igual) ...

    private void escanearCodigoQR() {
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this, options);
        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String rawValue = barcode.getRawValue();
                    if (rawValue != null) processQR(rawValue);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error Scanner: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void processQR(String contenido) {
        try {
            String[] partes = contenido.split("\\|");
            if (partes.length >= 2) {
                String parteDatos = partes[0];
                String parteDir = partes[1];

                int inicioNombre = parteDatos.indexOf("CLIENTE ") + 8;
                int finNombre = parteDatos.indexOf(" TEL");
                String nombre = parteDatos.substring(inicioNombre, finNombre).trim();
                String telefono = parteDatos.substring(finNombre + 4).trim();
                String direccion = parteDir.replace("DIR=", "").trim();

                etNombre.setText(nombre);
                etTelefono.setText(telefono);
                etDireccion.setText(direccion);
                Toast.makeText(this, "Datos cargados del QR", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Formato QR incorrecto", Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLat = location.getLatitude();
                                currentLong = location.getLongitude();
                                String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                tvUbicacion.setText("📍 Lat: " + currentLat + "\n📍 Lon: " + currentLong + "\n(Actualizado: " + hora + ")");
                            } else {
                                tvUbicacion.setText("⚠️ GPS sin señal.\n(Abre Google Maps para activar)");
                            }
                        }
                    });
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (Exception e) { }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgPreview.setImageBitmap(imageBitmap);
            currentFotoPath = guardarImagenLocalmente(imageBitmap);
        }
    }

    private String guardarImagenLocalmente(Bitmap bitmap) {
        File filesDir = getFilesDir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File imageFile = new File(filesDir, "JPEG_" + timeStamp + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) { return ""; }
    }

    private void guardarPedidoEnSQLite() {
        String nombre = etNombre.getText().toString();
        String telefono = etTelefono.getText().toString();
        String direccion = etDireccion.getText().toString();
        String detalle = etDetalle.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Faltan datos (Nombre/Teléfono)", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoPago = rbEfectivo.isChecked() ? "Efectivo" : "Transferencia";
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // 1. RECUPERAR EL EMAIL DEL USUARIO ACTUAL
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String usuarioActual = prefs.getString("user_email", "invitado");

        // 2. CREAR PEDIDO CON ESE ID
        Pedido nuevoPedido = new Pedido(
                usuarioActual, // <--- Nuevo primer parámetro
                nombre, telefono, direccion, detalle, tipoPago,
                currentFotoPath, currentLat, currentLong, fecha
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase.getDatabase(getApplicationContext()).pedidoDao().insert(nuevoPedido);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "✅ Pedido Guardado", Toast.LENGTH_LONG).show();
                limpiarFormulario();
            });
        });
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etTelefono.setText("");
        etDireccion.setText("");
        etDetalle.setText("");
        imgPreview.setImageResource(0);
        currentFotoPath = "";
        rbEfectivo.setChecked(true);
        tvUbicacion.setText("GPS: Esperando...");
        currentLat = 0;
        currentLong = 0;
    }
}