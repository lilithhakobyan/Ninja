package com.example.ninja;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class Kargin_quizzes extends Fragment {

    private TextView usernameTextView;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button karginHaghordum,karginSerial;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kargin_quizzes, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usernameTextView = view.findViewById(R.id.username);
        profileImageView = view.findViewById(R.id.profile_picture);
        karginHaghordum = view.findViewById(R.id.kargin_haxordum_button);
        karginSerial = view.findViewById(R.id.kargin_serial_button);

        loadUserData();
        loadProfilePicture();

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Profile.class);
            startActivity(intent);
        });

        karginHaghordum.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            startActivity(intent);
        });

        karginSerial.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), KarginSerialActivity.class);
            startActivity(intent);
        });



        return view;
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
                    .addOnFailureListener(e -> Log.e("KarginQuizzes", "Failed to load user data: " + e.getMessage()));
        }
    }

    private void loadProfilePicture() {
        if (getActivity() == null) return;

        // First try to load from local storage
        SharedPreferences prefs = getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
        String imagePath = prefs.getString("profile_image_path", null);

        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Glide.with(requireContext())
                        .load(imgFile)
                        .circleCrop()
                        .into(profileImageView);
                return;
            }
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl");
                            if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(profilePhotoUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.baseline_person_24)
                                        .error(R.drawable.baseline_person_24)
                                        .into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("KarginQuizzes", "Error loading profile picture", e);
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfilePicture();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }
}