package com.example.pedidosapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pedidosapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1. Ocultar la barra de estado para pantalla completa (opcional)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // 2. Cargar y ejecutar la animación en el contenedor principal
        View splashContent = findViewById(R.id.splash_content);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        splashContent.startAnimation(fadeIn);

        // 3. Temporizador para pasar al Login
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Transición suave entre actividades
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            finish();
        }, 3000); // 3 segundos para que se aprecie la animación
    }
}