package com.example.ninja;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<User> fullUserList = new ArrayList<>();
    private List<User> otherUsers = new ArrayList<>();

    private TextView firstName, secondName, thirdName;
    private TextView firstPoints, secondPoints, thirdPoints;
    private ImageView profImage1, profImage2, profImage3;


    private FirebaseFirestore db;
    private CollectionReference usersRef;

    public LeaderboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LeaderboardAdapter(otherUsers);
        recyclerView.setAdapter(adapter);

        firstName = view.findViewById(R.id.firstName);
        secondName = view.findViewById(R.id.secondName);
        thirdName = view.findViewById(R.id.thirdName);
        firstPoints = view.findViewById(R.id.firstPoints);
        secondPoints = view.findViewById(R.id.secondPoints);
        thirdPoints = view.findViewById(R.id.thirdPoints);

        profImage1 = view.findViewById(R.id.profImage1);
        profImage2 = view.findViewById(R.id.profImage2);
        profImage3 = view.findViewById(R.id.profImage3);


        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");


        if (fullUserList.size() > 0) {
            User first = fullUserList.get(0);
            firstName.setText(first.getUsername());
            firstPoints.setText(first.getScore() + " միավոր");
            Glide.with(this).load(first.getProfilePhotoUrl()).placeholder(R.drawable.baseline_person).into(profImage1);
        }
        if (fullUserList.size() > 1) {
            User second = fullUserList.get(1);
            secondName.setText(second.getUsername());
            secondPoints.setText(second.getScore() + " միավոր");
            Glide.with(this).load(second.getProfilePhotoUrl()).placeholder(R.drawable.baseline_person).into(profImage2);
        }
        if (fullUserList.size() > 2) {
            User third = fullUserList.get(2);
            thirdName.setText(third.getUsername());
            thirdPoints.setText(third.getScore() + " միավոր");
            Glide.with(this).load(third.getProfilePhotoUrl()).placeholder(R.drawable.baseline_person).into(profImage3);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        fetchScores();
    }

    private void fetchScores() {
        usersRef.orderBy("score", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fullUserList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String uid = document.getId();
                            Long score = document.getLong("score");
                            String username = document.getString("username");
                            String email = document.getString("email");

                            if (score != null) {
                                // Use username if available, otherwise use email, otherwise use UID
                                String displayName = username != null ? username :
                                        (email != null ? email : uid);
                                String photoUrl = document.getString("profilePhotoUrl");
                                Log.d("PHOTO_URL", "Photo URL for " + displayName + ": " + photoUrl);

                                fullUserList.add(new User(displayName, score, photoUrl));
                            }
                        }

                        displayTopThree();
                        displayOtherUsers();
                    } else {
                        Toast.makeText(getContext(), "Failed to load scores", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayTopThree() {
        if (fullUserList.size() > 0) {
            User first = fullUserList.get(0);
            String photoUrl = first.getProfilePhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.baseline_person)
                        .transform(new CircleCrop())
                        .into(profImage1);
            } else {
                profImage1.setImageResource(R.drawable.baseline_person);
            }
            firstName.setText(first.getUsername());
            firstPoints.setText(first.getScore() + " միավոր");
        }

        if (fullUserList.size() > 1) {
            User second = fullUserList.get(1);
            String photoUrl = second.getProfilePhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.baseline_person)
                        .transform(new CircleCrop())
                        .into(profImage2);  // Fixed: was profImage1
            } else {
                profImage2.setImageResource(R.drawable.baseline_person);
            }
            secondName.setText(second.getUsername());
            secondPoints.setText(second.getScore() + " միավոր");
        }

        if (fullUserList.size() > 2) {
            User third = fullUserList.get(2);
            String photoUrl = third.getProfilePhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.baseline_person)
                        .transform(new CircleCrop())
                        .into(profImage3);
            } else {
                profImage3.setImageResource(R.drawable.baseline_person);
            }
            thirdName.setText(third.getUsername());
            thirdPoints.setText(third.getScore() + " միավոր");
        }
    }

    private void displayOtherUsers() {
        otherUsers.clear();
        if (fullUserList.size() > 3) {
            otherUsers.addAll(fullUserList.subList(3, fullUserList.size()));
        }
        adapter.notifyDataSetChanged();
    }
}