package com.example.ninja;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.startButton);

        // Using lambda for cleaner code
        startButton.setOnClickListener(view -> {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        });
    }
}
