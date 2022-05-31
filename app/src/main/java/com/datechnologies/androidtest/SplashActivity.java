package com.datechnologies.androidtest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = new Intent(this, MainActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 3000);
    }
}