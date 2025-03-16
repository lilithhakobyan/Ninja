package com.example.ninja;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button karginButton, vitaminButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView usernameTextView;


    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usernameTextView = findViewById(R.id.username);

        loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top_section), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        karginButton = findViewById(R.id.kargin_card_button);
        vitaminButton = findViewById(R.id.vitamin_card_button);

        karginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new Kargin_quizzes());
            }
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            usernameTextView.setText(username);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("MainActivity", "Failed to load user data: " + e.getMessage()));
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        findViewById(R.id.top_section).setVisibility(View.GONE);
        findViewById(R.id.quizzes_title).setVisibility(View.GONE);
        findViewById(R.id.kargin_card).setVisibility(View.GONE);
        findViewById(R.id.vitamin_card).setVisibility(View.GONE);

        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            findViewById(R.id.top_section).setVisibility(View.VISIBLE);
            findViewById(R.id.quizzes_title).setVisibility(View.VISIBLE);
            findViewById(R.id.kargin_card).setVisibility(View.VISIBLE);
            findViewById(R.id.vitamin_card).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
