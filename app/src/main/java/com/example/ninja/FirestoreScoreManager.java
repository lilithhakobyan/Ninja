//package com.example.ninja;
//import android.util.Log;
//
//import com.google.android.material.color.utilities.Score;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//import com.example.ninja.User;
//
//public class FirestoreScoreManager {
//
//    private static final String TAG = "FirestoreScoreManager";
//
//    public void saveScore(String username, int score) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Using the username to create a document for each user
//        db.collection("leaderboard")
//                .document(username)  // Create a document using the username
//                .set(new Score(score))  // Save the score
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("Firestore", "Score saved successfully.");
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Firestore", "Error saving score: ", e);
//                });
//    }
//
//
//}
