package com.example.pedidosapp.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.example.pedidosapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapaRutaActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double destLat, destLon;
    private String cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_ruta);

        // Recibir destino
        destLat = getIntent().getDoubleExtra("lat", 0);
        destLon = getIntent().getDoubleExtra("lon", 0);
        cliente = getIntent().getStringExtra("cliente");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destino = new LatLng(destLat, destLon);
        // Punto simulado de origen (podrías usar la ubicación real del GPS aquí)
        LatLng origenSimulado = new LatLng(destLat - 0.005, destLon - 0.005);

        // Marcador Destino
        mMap.addMarker(new MarkerOptions().position(destino).title("Entrega: " + cliente));

        // DIBUJAR RUTA (Línea entre puntos)
        mMap.addPolyline(new PolylineOptions()
                .add(origenSimulado, destino)
                .width(10)
                .color(Color.BLUE)
                .geodesic(true));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 15));
    }
}