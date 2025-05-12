package com.example.ninja;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LeaderboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_host);

        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LeaderboardFragment())
                .commit();
    }
}
