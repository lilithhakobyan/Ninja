package com.example.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ScorePage extends AppCompatActivity {

    TextView total_questions, scoreText, completion, correct, wrong;
    Button homeButton, leaderboardBtn, again;
    CircularProgressIndicator progressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);

        scoreText = findViewById(R.id.scoreText);
        homeButton = findViewById(R.id.btn_home);
        leaderboardBtn = findViewById(R.id.btn_leaderboard);
        again = findViewById(R.id.btn_play_again);
        completion = findViewById(R.id.completion);
        total_questions = findViewById(R.id.total_questions);
        correct = findViewById(R.id.correct);
        wrong = findViewById(R.id.wrong);

        // Reference to the CircularProgressIndicator
        progressCircle = findViewById(R.id.progress_circle);

        // Get score from intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int wrongAnswers = totalQuestions - score;

        // Display the score
        scoreText.setText(score + "/" + totalQuestions);

        // Calculate the progress percentage
        float percentage = (float) score / totalQuestions * 100;
        int roundedProgress = Math.round(percentage);

        // Update completion text
        completion.setText("Իմ պրոգրեսը " + " " + roundedProgress + "%");

        // If score is equal to total questions, set progress to 100%
        if (score == totalQuestions) {
            completion.setText("Իմ պրոգրեսը 100%");
        }

        // Set other text values
        correct.setText("Ճիշտ " + " " + score);
        wrong.setText("Սխալ " + " " + wrongAnswers);
        total_questions.setText("Ընդհանուր " + " " + totalQuestions);

        // Update the CircularProgressIndicator
        progressCircle.setProgress(roundedProgress);

        // Button to return to main activity
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScorePage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        leaderboardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ScorePage.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        again.setOnClickListener(v -> {
            Intent intent = new Intent(ScorePage.this, QuizActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
