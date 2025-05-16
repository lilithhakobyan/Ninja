package com.example.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView profileImage;
    private TextView profileName;

    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile); // Make sure XML file is named like this

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        editProfileButton = findViewById(R.id.editProfileButton);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String username = snapshot.getString("username");
                        String profileUrl = snapshot.getString("profilePhotoUrl");

                        if (username != null) profileName.setText(username);

                        Glide.with(this)
                                .load(profileUrl)
                                .placeholder(R.drawable.profile_default)
                                .error(R.drawable.profile_default)
                                .into(profileImage);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error (e.g., show default image)
                    profileImage.setImageResource(R.drawable.profile_default);
                });
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, ProfileActivity.class);
            startActivity(intent);
        });

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

    @Override
    protected void onResume() {
        super.onResume();

        loadProfilePicture(profileImage);
    }

}
