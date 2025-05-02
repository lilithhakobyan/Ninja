package com.example.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Kargin_quizzes extends Fragment {

    private TextView usernameTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    Button karginHaghordum, karginSerial;


    public Kargin_quizzes() {

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
        karginHaghordum = view.findViewById(R.id.kargin_haxordum_button);
        karginSerial = view.findViewById(R.id.kargin_serial_button);


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
                    .addOnFailureListener(e -> Log.e("MainActivity", "Failed to load user data: " + e.getMessage()));
        }
    }
}
