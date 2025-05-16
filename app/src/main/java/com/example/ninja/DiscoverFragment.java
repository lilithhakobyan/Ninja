package com.example.ninja;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;


public class DiscoverFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView usernameTextView;
    private ImageView profileImageView; // Add this field



    Button gol, kartoshka,dzmeruk,tupoy,bordo,AzatGoti,AnkochHyur,Bodybuilder,KisabacDebatner,Nkarich,Oligarkh,AramMP3, Avtovtar;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        gol = view.findViewById(R.id.yt_gol);
        kartoshka = view.findViewById(R.id.yt_kartoshka);
        dzmeruk = view.findViewById(R.id.yt_dzmeruk);
        tupoy = view.findViewById(R.id.yt_tupoy);
        bordo = view.findViewById(R.id.yt_bordo);
        AzatGoti = view.findViewById(R.id.yt_azat_goti);
        AnkochHyur = view.findViewById(R.id.yt_ankoch_hyur);
        Bodybuilder = view.findViewById(R.id.yt_bodybuilder);
        KisabacDebatner = view.findViewById(R.id.yt_kisabac_debatner);
        Nkarich = view.findViewById(R.id.yt_nkarich);
        Oligarkh = view.findViewById(R.id.yt_oligarkh);
        AramMP3 = view.findViewById(R.id.yt_arammp3);
        Avtovtar = view.findViewById(R.id.yt_avtovtar);

        usernameTextView = view.findViewById(R.id.username);
        profileImageView = view.findViewById(R.id.profile_picture); 


        gol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://youtu.be/QVR7aa5cb5I?si=pu92EV2JuxEvV5y_";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        kartoshka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://youtu.be/H_AWoRuyy5U?si=9dQY8YgZJLtdB9-r";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        dzmeruk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://youtu.be/FcoYpp8_PM0?si=SJ_7a7UdYi3nZL0d";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        tupoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://youtu.be/YPieQWuJJFg?si=_OnS3DhMyKW6a2pa";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        bordo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://youtu.be/SWF0HecSwYA?si=BYm8Mh3XwOBO0IHL";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        AzatGoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=FunYRHM9gSo";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        AnkochHyur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=7wX5kpqd__8";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        Bodybuilder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=uPhe18RgSwg&t=301s";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        KisabacDebatner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=rcHuvKCl3dw&t=162s";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        Nkarich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=vERdMdDiGw8";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        Oligarkh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=w0QsGeqGsVg";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        AramMP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=PoiyM-GimNU";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        Avtovtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "https://www.youtube.com/watch?v=b9Z-A6g8jZU";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(intent);
            }
        });

        loadUserData();
        loadProfilePicture();

        return view;

    }

    private void loadProfilePicture() {
        if (!isAdded() || getActivity() == null) {
            return; // Don't load if fragment isn't attached
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (isAdded() && getActivity() != null && documentSnapshot.exists()) {
                            String profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl");
                            if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                                Glide.with(requireActivity()) // Use requireActivity()
                                        .load(profilePhotoUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.baseline_person_24)
                                        .error(R.drawable.baseline_person_24)
                                        .into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            Log.e("DiscoverFragment", "Failed to load profile picture", e);
                        }
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
                    .addOnFailureListener(e -> Log.e("DiscoverFragment", "Failed to load user data: " + e.getMessage()));
        }
    }

    // Optional: Refresh when fragment becomes visible again
    @Override
    public void onResume() {
        super.onResume();
        loadProfilePicture();
    }

}
