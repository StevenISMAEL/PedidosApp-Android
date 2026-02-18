package com.example.pedidosapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pedidosapp.R;
import java.io.File;

public class DetallePedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        // 1. Vincular vistas
        TextView tvCliente = findViewById(R.id.tvDetalleCliente);
        TextView tvTelefono = findViewById(R.id.tvDetalleTelefono);
        TextView tvDireccion = findViewById(R.id.tvDetalleDireccion);
        TextView tvDetalle = findViewById(R.id.tvDetallePedido);
        TextView tvPago = findViewById(R.id.tvDetallePago);
        TextView tvGPS = findViewById(R.id.tvDetalleGPS);
        TextView tvFecha = findViewById(R.id.tvDetalleFecha);
        TextView tvEstado = findViewById(R.id.tvDetalleEstado);
        TextView tvError = findViewById(R.id.tvDetalleError);
        ImageView imgFoto = findViewById(R.id.imgDetalleFoto);

        // 2. Recibir datos del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tvCliente.setText(extras.getString("nombre"));
            tvTelefono.setText(extras.getString("telefono"));
            tvDireccion.setText(extras.getString("direccion"));
            tvDetalle.setText(extras.getString("detalle"));
            tvPago.setText(extras.getString("pago"));
            tvFecha.setText(extras.getString("fecha"));

            double lat = extras.getDouble("lat");
            double lon = extras.getDouble("lon");
            tvGPS.setText("Lat: " + lat + "\nLon: " + lon);

            String estado = extras.getString("estado");
            tvEstado.setText(estado);

            // Colores según estado
            if ("SINCRONIZADO".equals(estado)) {
                tvEstado.setTextColor(Color.parseColor("#388E3C")); // Verde
            } else if ("ERROR".equals(estado)) {
                tvEstado.setTextColor(Color.RED);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Error: " + extras.getString("error"));
            } else {
                tvEstado.setTextColor(Color.parseColor("#F57C00")); // Naranja
            }

            // Cargar Foto
            String fotoPath = extras.getString("foto");
            if (fotoPath != null && !fotoPath.isEmpty()) {
                File imgFile = new File(fotoPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgFoto.setImageBitmap(myBitmap);
                }
            }
        }
    }
}