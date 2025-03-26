package com.example.ninja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ScorePage extends AppCompatActivity {


    TextView total_questions, scoreText, completion, correct, wrong ;
    Button homeButton,leaderboardBtn,again;

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

        // Get score from intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int one_quest_procent = 100/totalQuestions;
        int wrongAnswers = totalQuestions - score;

        // Display the score
        scoreText.setText(score + "/" + totalQuestions);
        completion.setText("Իմ պրոգրեսը " + " " + score*one_quest_procent + "%");
        if(score==totalQuestions){
            completion.setText("Իմ պրոգրեսը 100%");
        }
        correct.setText("Ճիշտ " + " " + score);
        wrong.setText("Սխալ " + " " + wrongAnswers);
        total_questions.setText("Ընդհանուր " + " " + totalQuestions);


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
