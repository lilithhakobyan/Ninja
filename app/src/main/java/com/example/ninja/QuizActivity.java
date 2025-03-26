package com.example.ninja;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    TextView quizTitle, questionText;
    Button ans1, ans2, ans3, nextQuestion;
    WebView webView;
    ProgressBar progressBar;

    MediaDatabaseHelper dbHelper;
    List<QuizQuestion> quizList;
    int currentIndex = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizTitle = findViewById(R.id.quizTitle);
        questionText = findViewById(R.id.questionText);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        nextQuestion = findViewById(R.id.nextQuestion);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar1);

        dbHelper = new MediaDatabaseHelper(this);

        // Insert 3 quiz questions into the database
        addQuizQuestions();

        // Fetch all quiz questions from the database
        quizList = dbHelper.getAllQuizQuestions();
        if (quizList.isEmpty()) {
            Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show();
            return; // Stop if no data is found
        } else {
            Collections.shuffle(quizList); // Shuffle the questions
            loadQuestion(currentIndex);    // Load the first question
        }

        // Handle answer clicks
        View.OnClickListener answerClickListener = v -> {
            checkAnswer(((Button) v).getText().toString());
            nextQuestion.setEnabled(true);  // Enable next button once answer is selected
        };

        ans1.setOnClickListener(answerClickListener);
        ans2.setOnClickListener(answerClickListener);
        ans3.setOnClickListener(answerClickListener);

        // Next question button
        nextQuestion.setOnClickListener(v -> {
            Log.d("QuizActivity", "Before Next Button: Current Index = " + currentIndex);

            if (currentIndex < quizList.size() - 1) {
                currentIndex++;
                Log.d("QuizActivity", "After Next Button: Current Index = " + currentIndex);
                loadQuestion(currentIndex);
            } else {
                // Stop the quiz when all questions are completed
                Toast.makeText(QuizActivity.this, "Quiz Complete!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    // Load the question, video, and options
    private void loadQuestion(int index) {
        // Log the current index when loading a question
        Log.d("QuizActivity", "Loading Question: Current Index = " + index);

        if (index >= 0 && index < quizList.size()) {
            QuizQuestion currentQuestion = quizList.get(index);

            quizTitle.setText("Question " + (index + 1));
            questionText.setText(currentQuestion.getQuestion());
            ans1.setText(currentQuestion.getOption1());
            ans2.setText(currentQuestion.getOption2());
            ans3.setText(currentQuestion.getOption3());

            // Only load video if it's not the third question
            if (index < 2) { // Allow videos for the first 2 questions (index 0 and 1)
                String videoUrl = currentQuestion.getVideoUrl() + "?autoplay=1";  // Autoplay enabled

                // Enable JavaScript and allow mixed content
                webView.setWebViewClient(new WebViewClient());
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setAllowContentAccess(true);
                webSettings.setAllowFileAccess(true);
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);  // Allow mixed content
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

                webView.loadUrl(videoUrl);  // Display the video with autoplay
                webView.setVisibility(View.VISIBLE); // Ensure WebView is visible for the video
            } else {
                // After the third question, hide the WebView (no more videos)
                webView.setVisibility(View.INVISIBLE); // Hide WebView after third question
                webView.loadUrl("about:blank"); // Stop the video from playing
            }

            // Update progress bar
            int progress = (int) (((float) (index + 1) / quizList.size()) * 100);
            progressBar.setProgress(progress);
        }
    }

    // Check if the selected answer is correct
    private void checkAnswer(String selectedAnswer) {
        QuizQuestion currentQuestion = quizList.get(currentIndex);
        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong! Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add 3 quiz questions into the database
    private void addQuizQuestions() {


        // Add the first quiz question
        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtube.com/embed/8IWPuf_03aw",  // YouTube video URL
                "What is the capital of France?",
                "Berlin",
                "Madrid",
                "Paris",
                "Paris"  // Correct answer
        );

        // Add the second quiz question
        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtube.com/embed/rg0fiGZbtMY",  // YouTube video URL
                "What is the capital of Japan?",
                "Beijing",
                "Seoul",
                "Tokyo",
                "Tokyo"  // Correct answer
        );

        // Add the third quiz question
        dbHelper.addQuizQuestion(
                "Math Quiz",
                "https://www.youtube.com/embed/mKp2EHzZpAw",  // YouTube video URL
                "What is 2 + 2?",
                "3",
                "4",
                "5",
                "4"  // Correct answer
        );
    }
}
