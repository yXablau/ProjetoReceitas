package com.unifebe.edu.projetoreceitas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.unifebe.edu.projetoreceitas.R;

public class SplashScreen extends Activity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this, LoginActivity.class); // Vá para Login
            startActivity(i);
            finish(); // Fecha Splash
        }, SPLASH_TIME_OUT);
    }
}
