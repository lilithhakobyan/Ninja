    package com.example.ninja;

    import android.content.DialogInterface;
    import android.os.Bundle;
    import android.util.Log;
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
    import com.google.firebase.firestore.FirebaseFirestore;

    import java.util.ArrayList;
    import java.util.List;

    public class ProfileActivity extends AppCompatActivity {

        private ImageView profileImageView;
        private TextView btnChangePicture;
        private TextView txtEmail;
        private TextView txtUsername;
        private List<ProfilePicture> profilePictures = new ArrayList<>();
        private MediaDatabaseHelper dbHelper;

        private FirebaseUser currentUser;
        private DatabaseReference userRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            dbHelper = new MediaDatabaseHelper(this);

            profileImageView = findViewById(R.id.profileImageView);
            btnChangePicture = findViewById(R.id.btnChangePicture);
            txtEmail = findViewById(R.id.tvEmail);
            txtUsername = findViewById(R.id.tvUsername);
            ImageView backBtn = findViewById(R.id.btnBack);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();


            backBtn.setOnClickListener(v -> finish());

            loadProfilePicturesFromDatabase();

            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                if (currentUser.getEmail() != null) {
                    txtEmail.setText(currentUser.getEmail());
                } else {
                    txtEmail.setText("Username: Unknown");
                }

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
                Log.d("ProfilePictures", "Loaded: " + profilePictures.size());
//////////////////////////////////////
                firestore.collection("users").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String url = documentSnapshot.getString("profilePhotoUrl");
                                String username = documentSnapshot.getString("username");

                                if (url != null && !url.isEmpty()) {
                                    Glide.with(ProfileActivity.this)
                                            .load(url)
                                            .placeholder(R.drawable.profile_default)
                                            .error(R.drawable.profile_default)
                                            .into(profileImageView);
                                }

                                if (username != null && !username.isEmpty()) {
                                    txtUsername.setText(username);
                                } else {
                                    txtUsername.setText("No username");
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to load profile info", e));


            }

            btnChangePicture.setOnClickListener(v -> showPictureSelectionDialog());

            profileImageView.setOnClickListener(v -> showPictureSelectionDialog());    }
        private void loadProfilePicturesFromDatabase() {
            profilePictures = dbHelper.getAllProfilePictures();

            if (profilePictures.isEmpty()) {
                dbHelper.addProfilePicture("Monika", "https://i.postimg.cc/bwM6mFJR/image.png");
                dbHelper.addProfilePicture("Vardan", "https://i.postimg.cc/8PP0yv1t/image.png");
                dbHelper.addProfilePicture("Adzik", "https://i.postimg.cc/jdsGG9h6/image.png");
                dbHelper.addProfilePicture("Adzik2", "https://i.postimg.cc/Px6vM0fX/image.png");
                dbHelper.addProfilePicture("Seda", "https://i.postimg.cc/JhW3FwNM/image.png");
                profilePictures = dbHelper.getAllProfilePictures();
            }
        }

        private void loadProfilePicture(String pictureId) {
            for (ProfilePicture picture : profilePictures) {
                if (picture.getId().equals(pictureId)) {
                    Glide.with(this)
                            .load(picture.getUrl())
                            .placeholder(R.drawable.profile_default)
                            .error(R.drawable.profile_default)
                            .into(profileImageView);
                    return;
                }
            }
            profileImageView.setImageResource(R.drawable.profile_default);
        }

        private void showPictureSelectionDialog() {
            if (profilePictures.isEmpty()) {
                Toast.makeText(this, "No pictures available", Toast.LENGTH_SHORT).show();
                return;
            }

            CharSequence[] items = new CharSequence[profilePictures.size()];
            for (int i = 0; i < profilePictures.size(); i++) {
                items[i] = profilePictures.get(i).getId(); // Can be changed to label if needed
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Profile Picture")
                    .setItems(items, (dialog, which) -> {
                        ProfilePicture selected = profilePictures.get(which);

                        if (userRef != null) {
                            userRef.child("profilePhoto").setValue(selected.getId());
                        }

                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("users").document(userId)
                                    .update("profilePhotoUrl", selected.getUrl())
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("Firestore", "Profile picture URL saved to Firestore"))
                                    .addOnFailureListener(e ->
                                            Log.e("Firestore", "Error saving profile picture URL", e));
                        }

                        Glide.with(ProfileActivity.this)
                                .load(selected.getUrl())
                                .placeholder(R.drawable.profile_default)
                                .error(R.drawable.profile_default)
                                .into(profileImageView);

                        Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                    }).show();
        }
    }