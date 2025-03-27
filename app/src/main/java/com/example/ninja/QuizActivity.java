//package com.example.ninja;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Collections;
//import java.util.List;
//
//public class QuizActivity extends AppCompatActivity {
//
//    TextView quizTitle, questionText;
//    Button ans1, ans2, ans3, nextQuestion;
//    WebView webView;
//    ProgressBar progressBar;
//    int score = 0;  // Tracks correct answers
//
//
//    MediaDatabaseHelper dbHelper;
//    List<QuizQuestion> quizList;
//    int currentIndex = 0;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_quiz);
//
//        quizTitle = findViewById(R.id.quizTitle);
//        questionText = findViewById(R.id.questionText);
//        ans1 = findViewById(R.id.ans1);
//        ans2 = findViewById(R.id.ans2);
//        ans3 = findViewById(R.id.ans3);
//        nextQuestion = findViewById(R.id.nextQuestion);
//        webView = findViewById(R.id.webView);
//        progressBar = findViewById(R.id.progressBar1);
//
//        dbHelper = new MediaDatabaseHelper(this);
//
//        // Insert 3 quiz questions into the database
//        addQuizQuestions();
//
//        // Fetch all quiz questions from the database
//        quizList = dbHelper.getAllQuizQuestions();
//        if (quizList.isEmpty()) {
//            Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show();
//            return; // Stop if no data is found
//        } else {
//            Collections.shuffle(quizList); // Shuffle the questions
//            loadQuestion(currentIndex);    // Load the first question
//        }
//
//        // Handle answer clicks
//        View.OnClickListener answerClickListener = v -> {
//            checkAnswer(((Button) v).getText().toString());
//            nextQuestion.setEnabled(true);  // Enable next button once answer is selected
//        };
//
//        ans1.setOnClickListener(answerClickListener);
//        ans2.setOnClickListener(answerClickListener);
//        ans3.setOnClickListener(answerClickListener);
//
//
//        nextQuestion.setOnClickListener(v -> {
//            Log.d("QuizActivity", "Before Next Button: Current Index = " + currentIndex);
//
//            if (currentIndex < quizList.size() - 1) {
//                currentIndex++;
//                Log.d("QuizActivity", "After Next Button: Current Index = " + currentIndex);
//                loadQuestion(currentIndex);
//            } else {
//                // Move to ScorePage after the last question
//                Intent intent = new Intent(QuizActivity.this, ScorePage.class);
//                intent.putExtra("score", score); // Pass the final score
//                intent.putExtra("totalQuestions", quizList.size());
//                startActivity(intent);
//                finish();
//            }
//        });
//
//
//    }
//
//    private void checkAnswer(String selectedAnswer) {
//        QuizQuestion currentQuestion = quizList.get(currentIndex);
//
//        Button selectedButton = null;
//        if (ans1.getText().toString().equals(selectedAnswer)) {
//            selectedButton = ans1;
//        } else if (ans2.getText().toString().equals(selectedAnswer)) {
//            selectedButton = ans2;
//        } else if (ans3.getText().toString().equals(selectedAnswer)) {
//            selectedButton = ans3;
//        }
//
//        ans1.setEnabled(false);
//        ans2.setEnabled(false);
//        ans3.setEnabled(false);
//
//        if (selectedButton != null) {
//            if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
//                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
//                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
//                score++; // Increase score for correct answers
//            } else {
//                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
//                Toast.makeText(this, "Wrong! Try Again.", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        nextQuestion.setEnabled(true);
//    }
//
//
//
//    private void loadQuestion(int index) {
//        Log.d("QuizActivity", "Loading Question: Current Index = " + index);
//
//        if (index >= 0 && index < quizList.size()) {
//            QuizQuestion currentQuestion = quizList.get(index);
//
//            quizTitle.setText("Question " + (index + 1));
//            questionText.setText(currentQuestion.getQuestion());
//            ans1.setText(currentQuestion.getOption1());
//            ans2.setText(currentQuestion.getOption2());
//            ans3.setText(currentQuestion.getOption3());
//
//            // Reset button colors and re-enable them
//            ans1.setBackgroundColor(getResources().getColor(R.color.bg));
//            ans2.setBackgroundColor(getResources().getColor(R.color.bg));
//            ans3.setBackgroundColor(getResources().getColor(R.color.bg));
//
//            ans1.setEnabled(true);
//            ans2.setEnabled(true);
//            ans3.setEnabled(true);
//
//            // Video Handling (First two questions only)
//            if (index < 2) { // Allow videos for the first 2 questions
//                String videoId = extractYouTubeVideoId(currentQuestion.getVideoUrl());
//
//                if (videoId != null) {
//                    String videoHtml = "<html><body style='margin:0;padding:0;'>"
//                            + "<iframe width='100%' height='100%' "
//                            + "src='https://www.youtube.com/embed/" + videoId
//                            + "?autoplay=1&rel=0&controls=1&modestbranding=1' "
//                            + "frameborder='0' allowfullscreen></iframe></body></html>";
//
//                    webView.getSettings().setJavaScriptEnabled(true);
//                    webView.setWebViewClient(new WebViewClient());
//                    webView.loadData(videoHtml, "text/html", "utf-8");
//                    webView.setVisibility(View.VISIBLE);
//                } else {
//                    Log.e("QuizActivity", "Invalid video URL: " + currentQuestion.getVideoUrl());
//                    webView.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                webView.setVisibility(View.INVISIBLE);
//                webView.loadUrl("about:blank"); // Clear WebView
//            }
//
//            // Update progress bar
//            int progress = (int) (((float) (index + 1) / quizList.size()) * 100);
//            progressBar.setProgress(progress);
//        }
//    }
//
//
//    private String extractYouTubeVideoId(String url) {
//        if (url == null || url.isEmpty()) return null;
//
//        String videoId = null;
//        String pattern = "(?:youtube\\.com/embed/|youtube\\.com/watch\\?v=|youtu\\.be/)([^?&]*)";
//
//        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher matcher = compiledPattern.matcher(url);
//
//        if (matcher.find()) {
//            videoId = matcher.group(1); // Extract video ID
//        }
//
//        return videoId;
//    }
//
//
//    private void addQuizQuestions() {
//
//
//        // Add the first quiz question
//        dbHelper.addQuizQuestion(
//                "Geography Quiz",
//                "https://www.youtube.com/embed/8IWPuf_03aw?rel=0",  // YouTube video URL
//                "What is the capital of France?",
//                "Berlin",
//                "Madrid",
//                "Paris",
//                "Paris"  // Correct answer
//        );
//
//        // Add the second quiz question
//        dbHelper.addQuizQuestion(
//                "Geography Quiz",
//                "https://www.youtube.com/embed/rg0fiGZbtMY?rel=0",  // YouTube video URL
//                "What is the capital of Japan?",
//                "Beijing",
//                "Seoul",
//                "Tokyo",
//                "Tokyo"  // Correct answer
//        );
//
//        // Add the third quiz question
//        dbHelper.addQuizQuestion(
//                "Math Quiz",
//                "https://www.youtube.com/embed/mKp2EHzZpAw?rel=0",  // YouTube video URL
//                "What is 2 + 2?",
//                "3",
//                "4",
//                "5",
//                "4"  // Correct answer
//        );
//    }
//}

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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizActivity extends AppCompatActivity {

    TextView quizTitle, questionText;
    Button ans1, ans2, ans3, nextQuestion;
    WebView webView;
    ProgressBar progressBar;
    int score = 0;

    MediaDatabaseHelper dbHelper;
    List<QuizQuestion> quizList;
    List<QuizQuestion> selectedQuestions; // Only 3 unique questions
    int currentIndex = 0;
    int maxQuestions = 6; // Limit to 3 questions

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

        // Add quiz questions (call this method to add questions to the database)
        addQuizQuestions();

        // Fetch all quiz questions from the database
        quizList = dbHelper.getAllQuizQuestions();
        if (quizList.isEmpty()) {
            Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter to remove duplicate video URLs
        selectedQuestions = getUniqueVideoQuestions(quizList, maxQuestions);

        loadQuestion(currentIndex);

        // Handle answer clicks
        View.OnClickListener answerClickListener = v -> {
            checkAnswer(((Button) v).getText().toString());
            nextQuestion.setEnabled(true);
        };

        ans1.setOnClickListener(answerClickListener);
        ans2.setOnClickListener(answerClickListener);
        ans3.setOnClickListener(answerClickListener);

        nextQuestion.setOnClickListener(v -> {
            if (currentIndex < maxQuestions - 1) {
                currentIndex++;
                loadQuestion(currentIndex);
            } else {
                // Move to ScorePage after the last question
                Intent intent = new Intent(QuizActivity.this, ScorePage.class);
                intent.putExtra("score", score);
                intent.putExtra("totalQuestions", maxQuestions);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to add quiz questions to the database
    private void addQuizQuestions() {
        dbHelper.clearQuizQuestions();
        // Add the first quiz question
        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/l8FL_MJmyJ0?rel=0", // YouTube video URL
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Տրեներ,բայց ես նորմալ հայերեն հասկանում եմ",
                "Էն ժամանակ, ոնց որ, սենց չոտկի չէիր ասում",
                "Ապե, դու լուրջ ես ասու՞մ",
                "Տրեներ,բայց ես նորմալ հայերեն հասկանում եմ" // Correct answer
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/D4s5P4VqLf0?rel=0", // YouTube video URL
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Ապեր, ասում եմ՝ շինանյութի խանութ ա։",
                "Էս մեր անուշ ախպերն էլ, էն մյուս կեսն ա ուզում",
                "Էս էլ էտ քյալն ա",
                "Էս մեր անուշ ախպերն էլ, էն մյուս կեսն ա ուզում" // Correct answer
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/6FjkW7G4bTg?rel=0", // YouTube video URL
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Հա բա,նախարար մարդ եմ",
                "Ապեր ջան կարող ա ես գիժ եմ,բայց ես տուպոյ չեմ",
                "Ոչինչ,կմեծանաս ինքդ կհասկանաս",
                "Ապեր ջան կարող ա ես գիժ եմ,բայց ես տուպոյ չեմ" // Correct answer
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/mKp2EHzZpAw?rel=0", // YouTube video URL
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Բա ինչի՞ ա պետք որ",
                "Պետք ա` գնա առ",
                "Ձեզի ջինըս պե՞տք ա",
                "Բա ինչի՞ ա պետք որ" // Correct answer
        );

        // Add the second quiz question
        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/ZeY5l_HzkzA?rel=0", // YouTube video URL
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Պոպոք ու սմետան",
                "Արա չես ջոգում որ ձեն չենք հանում ուրեմն կարտոշկա ա",
                "Արա մի հատ ոտերդ քաշի,ապուշ լակոտ",
                "Արա չես ջոգում որ ձեն չենք հանում ուրեմն կարտոշկա ա" // Correct answer
        );

        // Add the third quiz question
        dbHelper.addQuizQuestion(
                "Math Quiz",
                "https://www.youtu.be/OuZlhjRBXzc?rel=0", // YouTube video URL
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "5 անգամ հրավիրել են մարդիկ,արդեն ամոթ ա",
                "Շատ ճիշտ նկատեցիք,այո",
                "Ապեր ստեղ պահի,աչքիս սխալ մարշուտկա եմ նստել",
                "Ապեր ստեղ պահի,աչքիս սխալ մարշուտկա եմ նստել" // Correct answer
        );
    }

    // Method to filter out questions with duplicate video URLs
    private List<QuizQuestion> getUniqueVideoQuestions(List<QuizQuestion> quizList, int maxQuestions) {
        List<QuizQuestion> uniqueQuestions = new ArrayList<>();
        Set<String> videoUrls = new HashSet<>();  // Set to track unique video URLs

        for (QuizQuestion question : quizList) {
            String videoUrl = question.getVideoUrl();
            if (!videoUrls.contains(videoUrl)) {
                videoUrls.add(videoUrl);
                uniqueQuestions.add(question);
            }
            if (uniqueQuestions.size() >= maxQuestions) {
                break;  // Stop once we've selected the required number of questions
            }
        }

        return uniqueQuestions;
    }

    // Method to check the selected answer
    private void checkAnswer(String selectedAnswer) {
        QuizQuestion currentQuestion = selectedQuestions.get(currentIndex);

        Button selectedButton = null;
        if (ans1.getText().toString().equals(selectedAnswer)) selectedButton = ans1;
        else if (ans2.getText().toString().equals(selectedAnswer)) selectedButton = ans2;
        else if (ans3.getText().toString().equals(selectedAnswer)) selectedButton = ans3;

        ans1.setEnabled(false);
        ans2.setEnabled(false);
        ans3.setEnabled(false);

        if (selectedButton != null) {
            if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                score++;
            } else {
                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            }
        }

        nextQuestion.setEnabled(true);
    }

    // Method to load the current question
    private void loadQuestion(int index) {
        Log.d("QuizActivity", "Loading Question: Current Index = " + index);

        QuizQuestion currentQuestion = selectedQuestions.get(index);

        quizTitle.setText("Question " + (index + 1));
        questionText.setText(currentQuestion.getQuestion());
        ans1.setText(currentQuestion.getOption1());
        ans2.setText(currentQuestion.getOption2());
        ans3.setText(currentQuestion.getOption3());

        ans1.setBackgroundColor(getResources().getColor(R.color.bg));
        ans2.setBackgroundColor(getResources().getColor(R.color.bg));
        ans3.setBackgroundColor(getResources().getColor(R.color.bg));

        ans1.setEnabled(true);
        ans2.setEnabled(true);
        ans3.setEnabled(true);

        // Video Handling (Unique video per question)
        String videoId = extractYouTubeVideoId(currentQuestion.getVideoUrl());
//        if (videoId != null) {
//            String videoHtml = "<html><body style='margin:0;padding:0;'>"
//                    + "<iframe width='100%' height='100%' "
//                    + "src='https://www.youtube.com/embed/" + videoId
//                    + "?autoplay=1&rel=0&controls=1&modestbranding=1' "
//                    + "frameborder='0' allowfullscreen></iframe></body></html>";
//
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.setWebViewClient(new WebViewClient());
//            webView.loadData(videoHtml, "text/html", "utf-8");
//            webView.setVisibility(View.VISIBLE);
//        } else {
//            webView.setVisibility(View.INVISIBLE);
//            webView.loadUrl("about:blank");
//        }
        if (videoId != null) {
            String videoHtml = "<html><body style='margin:0;padding:0;'>"
                    + "<iframe width='100%' height='100%' "
                    + "src='https://www.youtube.com/embed/" + videoId
                    + "?autoplay=1&rel=0&controls=1&modestbranding=1&showinfo=0' "
                    + "frameborder='0' allowfullscreen></iframe></body></html>";

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadData(videoHtml, "text/html", "utf-8");
            webView.setVisibility(View.VISIBLE);
        } else {
            Log.e("QuizActivity", "Invalid video URL: " + currentQuestion.getVideoUrl());
            webView.setVisibility(View.INVISIBLE);
        }


        int progress = (int) (((float) (index + 1) / maxQuestions) * 100);
        progressBar.setProgress(progress);
    }

    // Method to extract YouTube video ID
    private String extractYouTubeVideoId(String url) {
        if (url == null || url.isEmpty()) return null;
        String pattern = "(?:youtube\\.com/embed/|youtube\\.com/watch\\?v=|youtu\\.be/)([^?&]*)";
        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = compiledPattern.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}
