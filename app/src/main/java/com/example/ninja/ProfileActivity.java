package com.example.ninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvLanguageLabel;
    private ImageView imgProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentLanguage = "en"; // Default to English

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvLanguageLabel = findViewById(R.id.tvLanguageLabel);
        imgProfile = findViewById(R.id.imgProfile);
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView btnChangePicture = findViewById(R.id.btnChangePicture);
        View optionLanguage = findViewById(R.id.optionLanguage);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Change picture click
        btnChangePicture.setOnClickListener(v -> showImageSelectionDialog());

        // Language option click
        optionLanguage.setOnClickListener(v -> showLanguageSelectionDialog());

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Set email from Firebase Auth
            tvEmail.setText(currentUser.getEmail());

            // Get username from Firestore
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.getString("username");
                        if (username != null) {
                            tvUsername.setText(username);
                        }
                    }
                }
            });
        }

        // Set initial language
        updateLanguageText();
    }

    private void showImageSelectionDialog() {
        // Create an array of drawable resource IDs for profile pictures
        Integer[] profilePics = {
                R.drawable.profile1,
//                R.drawable.profile2,
//                R.drawable.profile3,
//                R.drawable.profile4
        };

        // Convert to CharSequence array for the dialog
        CharSequence[] items = new CharSequence[profilePics.length];
        for (int i = 0; i < profilePics.length; i++) {
            items[i] = "Profile Picture " + (i + 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(items, (dialog, which) -> {
            // Set the selected image to the ImageView
            imgProfile.setImageResource(profilePics[which]);
            Toast.makeText(ProfileActivity.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showLanguageSelectionDialog() {
        ArrayList<LanguageItem> languageList = new ArrayList<>();
        languageList.add(new LanguageItem("English", R.drawable.flag_uk));
        languageList.add(new LanguageItem("Armenian", R.drawable.flag_armenia));

        LanguageAdapter adapter = new LanguageAdapter(this, languageList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");
        builder.setAdapter(adapter, (dialog, which) -> {
            String selectedLanguage = languageList.get(which).getName();
            if (selectedLanguage.equals("English")) {
                currentLanguage = "en";
            } else if (selectedLanguage.equals("Armenian")) {
                currentLanguage = "hy";
            }
            setLocale(currentLanguage);
            updateLanguageText();
        });
        builder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Restart activity to apply language changes
        Intent refresh = new Intent(this, ProfileActivity.class);
        startActivity(refresh);
        finish();
    }

    private void updateLanguageText() {
        if (currentLanguage.equals("en")) {
            tvLanguageLabel.setText("English");
        } else if (currentLanguage.equals("hy")) {
            tvLanguageLabel.setText("Armenian");
        }
    }
}