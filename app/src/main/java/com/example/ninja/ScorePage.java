package com.example.ninja;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import android.content.Intent;

public class ScorePage extends AppCompatActivity {

    TextView total_questions, scoreText, completion, correct, wrong;
    Button homeButton, again;
    CircularProgressIndicator progressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);

        // Initialize your UI elements
        scoreText = findViewById(R.id.scoreText);
        homeButton = findViewById(R.id.btn_home);
        again = findViewById(R.id.btn_play_again);
        completion = findViewById(R.id.completion);
        total_questions = findViewById(R.id.total_questions);
        correct = findViewById(R.id.correct);
        wrong = findViewById(R.id.wrong);
        progressCircle = findViewById(R.id.progress_circle);

        // Get score and totalQuestions from the Intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int wrongAnswers = totalQuestions - score;

        // Display the score
        scoreText.setText(score + "/" + totalQuestions);

        // Calculate progress percentage
        float percentage = (float) score / totalQuestions * 100;
        int roundedProgress = Math.round(percentage);

        // Update completion text
        completion.setText(getString(R.string.score_progress) + " " + roundedProgress + "%");

        // If score is equal to total questions, set progress to 100%
        if (score == totalQuestions) {
            completion.setText(getString(R.string.score_progress) + "100%");
        }

        // Set other text values
        correct.setText(getString(R.string.answered_right) + " " + score);
        wrong.setText(getString(R.string.answered_wrong) + " " + wrongAnswers);
        total_questions.setText(getString(R.string.total)  + " " + totalQuestions);

        // Animate progress
        animateProgress(roundedProgress);

        // Animate text sliding in
        animateTextSlide();

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScorePage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        again.setOnClickListener(v -> {
            Intent intent = new Intent(ScorePage.this, QuizActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void animateProgress(int targetProgress) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetProgress);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            progressCircle.setProgress(progress);
        });
        animator.start();
    }



    private void animateTextSlide() {
        ObjectAnimator slideInScore = ObjectAnimator.ofFloat(scoreText, "translationX", -500f, 0f);  // -500f is off-screen to the left
        slideInScore.setDuration(1000);
        slideInScore.start();
    }


}
