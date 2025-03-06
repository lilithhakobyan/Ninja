package com.example.ninja;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                boolean isFirstTime = sharedPreferences.getBoolean("FirstTime", true);

                if (isFirstTime) {
                    // Show Welcome Slider if first time
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                } else {
                    // Go directly to MainActivity
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }

                finish();
            }
        }, 3000);
    }
}
