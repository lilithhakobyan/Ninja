package com.example.ninja;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Kargin_quizzes extends Fragment {

    private TextView usernameTextView;

    public Kargin_quizzes() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kargin_quizzes, container, false);

        usernameTextView = view.findViewById(R.id.username);

        if (getArguments() != null) {
            String username = getArguments().getString("username", "Guest");
            usernameTextView.setText(username);
        }

        return view;
    }
}
