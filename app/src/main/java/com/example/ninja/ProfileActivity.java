package com.example.ninja;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView btnChangePicture;
    private TextView txtEmail;
    private TextView txtLanguage;
    private List<ProfilePicture> profilePictures = new ArrayList<>();
    private MediaDatabaseHelper dbHelper;

    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize database helper
        dbHelper = new MediaDatabaseHelper(this);

        profileImageView = findViewById(R.id.profileImageView);
        btnChangePicture = findViewById(R.id.btnChangePicture);
        txtEmail = findViewById(R.id.tvEmail);
        txtLanguage = findViewById(R.id.tvLanguageLabel);
        ImageView backBtn = findViewById(R.id.btnBack);

        backBtn.setOnClickListener(v -> finish());

        // Load profile pictures from SQLite
        loadProfilePicturesFromDatabase();
        dbHelper = new MediaDatabaseHelper(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            if (currentUser.getEmail() != null) {
                txtEmail.setText(currentUser.getEmail());
            } else {
                txtEmail.setText("Username: Unknown");
            }

            // Load saved profile picture from Firebase
            userRef.child("profilePhoto").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String pictureId = snapshot.getValue(String.class);
                        if (pictureId != null) {
                            loadProfilePicture(pictureId);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnChangePicture.setOnClickListener(v -> showPictureSelectionDialog());

        // For changing profile picture
        profileImageView.setOnClickListener(v -> showPictureSelectionDialog());    }

//    private void loadProfilePicturesFromDatabase() {
//        profilePictures = dbHelper.getAllProfilePictures();
//        // You might want to add some default pictures if the database is empty
//        if (profilePictures.isEmpty()) {
//            // Add some default pictures
//            dbHelper.addProfilePicture("default", "https://example.com/default.jpg");
//            dbHelper.addProfilePicture("pic1", "https://example.com/profile1.jpg");
//            dbHelper.addProfilePicture("pic2", "https://example.com/profile2.jpg");
//            profilePictures = dbHelper.getAllProfilePictures();
//        }
//    }

    private void loadProfilePicturesFromDatabase() {
        profilePictures = dbHelper.getAllProfilePictures();

        // Add default pictures if database is empty
        if (profilePictures.isEmpty()) {
            dbHelper.addProfilePicture("Monika", "https://i.postimg.cc/bwM6mFJR/image.png");
            dbHelper.addProfilePicture("Vardan", "https://i.postimg.cc/8PP0yv1t/image.png");
            dbHelper.addProfilePicture("Adzik", "https://i.postimg.cc/jdsGG9h6/image.png");
            profilePictures = dbHelper.getAllProfilePictures();
        }
    }

    private void loadProfilePicture(String pictureId) {
        // Find the picture in our list
        for (ProfilePicture picture : profilePictures) {
            if (picture.getId().equals(pictureId)) {
                // Use Glide or Picasso to load the image from URL
                Glide.with(this)
                        .load(picture.getUrl())
                        .placeholder(R.drawable.profile_default)
                        .error(R.drawable.profile_default)
                        .into(profileImageView);
                return;
            }
        }
        // If not found, load default
        profileImageView.setImageResource(R.drawable.profile_default);
    }


    // The dialog method
    private void showPictureSelectionDialog() {
        if (profilePictures.isEmpty()) {
            Toast.makeText(this, "No pictures available", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] items = new CharSequence[profilePictures.size()];
        for (int i = 0; i < profilePictures.size(); i++) {
            items[i] = profilePictures.get(i).getId(); // Or label it nicely
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Profile Picture")
                .setItems(items, (dialog, which) -> {
                    ProfilePicture selected = profilePictures.get(which);

                    // 1. Save selected picture ID to Firebase
                    if (userRef != null) {
                        userRef.child("profilePhoto").setValue(selected.getId());
                    }

                    // 2. Load the image into ImageView
                    Glide.with(ProfileActivity.this)
                            .load(selected.getUrl())
                            .placeholder(R.drawable.profile_default)
                            .error(R.drawable.profile_default)
                            .into(profileImageView);

                    Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                }).show();
    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}