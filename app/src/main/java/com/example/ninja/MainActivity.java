package com.example.ninja;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button karginButton, vitaminButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView usernameTextView;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usernameTextView = findViewById(R.id.username);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        // Get the profile ImageView
        ImageView profileImageView = findViewById(R.id.profile_picture); // Make sure this ID matches your XML

        loadUserData();
        loadProfilePicture(profileImageView); // Add this line to load the profile picture

        loadUserData();

        karginButton = findViewById(R.id.kargin_card_button);
        vitaminButton = findViewById(R.id.vitamin_card_button);

        karginButton.setOnClickListener(v -> openFragment(new Kargin_quizzes()));
        vitaminButton.setOnClickListener(v -> openActivity());

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
        });


        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                showHome();
                return true;
            } else if (itemId == R.id.nav_discover) {
                selectedFragment = new DiscoverFragment();
            } else if (itemId == R.id.nav_leaderboard) {
                selectedFragment = new LeaderboardFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                openFragment(selectedFragment);
            }
            return true;
        });

        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
        String imagePath = prefs.getString("profile_image_path", null);

        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Glide.with(this)
                        .load(imgFile)
                        .circleCrop()
                        .into(profileImageView);
            }
        }


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String lang = prefs.getString("app_language", "hy");
        super.attachBaseContext(LanguageHelper.setLocale(newBase, lang));
    }



    private Context updateLocale(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    private void loadProfilePicture(ImageView profileImageView) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl");
                            if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profilePhotoUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.baseline_person_24) // Your default profile icon
                                        .error(R.drawable.baseline_person_24) // Fallback if loading fails
                                        .into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error loading profile picture", e);
                    });
        }
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

    private void showHome() {
        findViewById(R.id.top_section).setVisibility(View.VISIBLE);
        findViewById(R.id.quizzes_title).setVisibility(View.VISIBLE);
        findViewById(R.id.kargin_card).setVisibility(View.VISIBLE);
        findViewById(R.id.vitamin_card).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);

        getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            showHome();
        } else {
            super.onBackPressed();
        }
    }

    private void openActivity() {

        Intent intent = new Intent(MainActivity.this, VitaminQuizActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop called");
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView profileImageView = findViewById(R.id.profile_picture);
        loadProfilePicture(profileImageView);
    }

}