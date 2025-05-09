package com.example.ninja;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView btnChangePicture;
    private TextView txtEmail;
    private TextView txtLanguage;

    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private int selectedProfileIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        btnChangePicture = findViewById(R.id.btnChangePicture);
        txtEmail = findViewById(R.id.tvEmail);
        txtLanguage = findViewById(R.id.tvLanguageLabel);
        ImageView backBtn = findViewById(R.id.btnBack);

        // Back button to finish activity
        backBtn.setOnClickListener(v -> finish());

        loadProfilePhoto();  // Load the profile picture when activity starts

        profileImageView.setOnClickListener(v -> showImageSelectionDialog());  // Open dialog to change profile image

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Show email or username
            if (currentUser.getEmail() != null) {
                txtEmail.setText(currentUser.getEmail());
            } else {
                txtEmail.setText("Username: Unknown");
            }

            // Load language and profile picture index from database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("profileImageIndex")) {
                            Long index = snapshot.child("profileImageIndex").getValue(Long.class);
                            if (index != null) {
                                selectedProfileIndex = index.intValue();
                                int[] profilePics = {
                                        R.drawable.profile1, R.drawable.profile2
                                };
                                if (selectedProfileIndex >= 0 && selectedProfileIndex < profilePics.length) {
                                    profileImageView.setImageResource(profilePics[selectedProfileIndex]);
                                }
                            }
                        }

                        if (snapshot.hasChild("language")) {
                            String lang = snapshot.child("language").getValue(String.class);
                            if (lang != null) {
                                txtLanguage.setText("Language: " + lang);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnChangePicture.setOnClickListener(v -> showPictureSelectionDialog());  // Open dialog to select a picture
    }

    // Show the image selection dialog when the user taps on the profile picture
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Select Profile Picture")
                .setItems(new CharSequence[]{"Default", "Profile 1", "Profile 2"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    saveProfilePhoto("default");
                                    break;
                                case 1:
                                    saveProfilePhoto("profile1");
                                    break;
                                case 2:
                                    saveProfilePhoto("profile2");
                                    break;
                            }
                        })
                .show();
    }

    // Load the saved profile picture from Firebase
    private void loadProfilePhoto() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid(); // Get the UID of the logged-in user
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            dbRef.child("profilePhoto").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String photoId = dataSnapshot.getValue(String.class); // Get the photo ID
                    if (photoId != null) {
                        setProfileImage(photoId); // Set the image based on saved data
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors (e.g., network issues)
                }
            });
        }
    }

    // Save the profile picture reference in Firebase
    private void saveProfilePhoto(String selectedPhoto) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid(); // Get the current user's UID
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            dbRef.child("profilePhoto").setValue(selectedPhoto); // Save the selected photo reference
        }
    }

    // Set the profile image based on the photo ID
    private void setProfileImage(String photoId) {
        switch (photoId) {
            case "default":
                profileImageView.setImageResource(R.drawable.profile_default);  // Set the default image
                break;
            case "profile1":
                profileImageView.setImageResource(R.drawable.profile1);  // Set profile image 1
                break;
            case "profile2":
                profileImageView.setImageResource(R.drawable.profile2);  // Set profile image 2
                break;
            // Add more cases if you have more profile images
        }
    }

    // Show a dialog to select a profile picture from predefined images
    private void showPictureSelectionDialog() {
        int[] profilePics = {
                R.drawable.profile1, R.drawable.profile2
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(new CharSequence[]{
                "Picture 1", "Picture 2"
        }, (dialog, which) -> {
            selectedProfileIndex = which;
            profileImageView.setImageResource(profilePics[which]);

            if (userRef != null) {
                userRef.child("profileImageIndex").setValue(which)
                        .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile picture updated!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update picture.", Toast.LENGTH_SHORT).show());
            }
        });
        builder.show();
    }
}
