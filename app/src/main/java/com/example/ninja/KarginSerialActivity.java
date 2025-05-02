package com.example.ninja;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KarginSerialActivity extends AppCompatActivity {

    TextView quizTitle, questionText;
    Button ans1, ans2, ans3, nextQuestion;
    WebView webView;
    ProgressBar progressBar;
    int score = 0;

    MediaDatabaseHelper dbHelper;
    List<QuizQuestion> quizList;
    List<QuizQuestion> selectedQuestions;
    int currentIndex = 0;
    int maxQuestions = 3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kargin_serial);

        quizTitle = findViewById(R.id.quizTitle);
        questionText = findViewById(R.id.questionText);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        nextQuestion = findViewById(R.id.nextQuestion);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        dbHelper = new MediaDatabaseHelper(this);

        addKarginSerialQuizQuestions();

        quizList = dbHelper.getAllQuizQuestions();
        if (quizList.isEmpty()) {
            Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedQuestions = getUniqueVideoQuestions(quizList, maxQuestions);

        loadQuestion(currentIndex);

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
                Intent intent = new Intent(KarginSerialActivity.this, ScorePage.class);
                intent.putExtra("score", score);
                intent.putExtra("totalQuestions", maxQuestions);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addKarginSerialQuizQuestions() {
        dbHelper.clearQuizQuestions();


        dbHelper.addQuizQuestion(
                "Kargin Serial",
                "https://i.postimg.cc/pdyxN6yV/Screenshot-2025-04-24-213615.png",
                "Ի՞նչ է առաքիչի անունը",
                "Արմեն",
                "Վարուժ",
                "Արամ",
                "Վարուժ",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Kargin Serial",
                "https://i.postimg.cc/pdyxN6yV/Screenshot-2025-04-24-213615.png",
                "Ո՞վ է պատկերված նկարում",
                "Արմեն",
                "Մկրտիչ",
                "Գագիկ",
                "Մկրտիչ",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Kargin Serial",
                "https://i.postimg.cc/pdyxN6yV/Screenshot-2025-04-24-213615.png",
                "Ո՞վ է պատկերված նկարում",
                "Արմեն",
                "Մկրտիչ",
                "Գագիկ",
                "Մկրտիչ",
                "image"
        );


    }

    private List<QuizQuestion> getUniqueVideoQuestions(List<QuizQuestion> quizList, int maxQuestions) {
        List<QuizQuestion> uniqueQuestions = new ArrayList<>();
        Set<String> videoUrls = new HashSet<>();
        Collections.shuffle(quizList);

        for (QuizQuestion question : quizList) {
            String videoUrl = question.getMediaUrl();
            if (!videoUrls.contains(videoUrl)) {
                videoUrls.add(videoUrl);
                uniqueQuestions.add(question);
            }
            if (uniqueQuestions.size() >= maxQuestions) break;
        }

        return uniqueQuestions;
    }

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
                selectedButton.setBackgroundColor(getResources().getColor(R.color.green));
                score++;
            } else {
                selectedButton.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }

        nextQuestion.setEnabled(true);
    }

    private void loadQuestion(int index) {
        Log.d("KarginSerialActivity", "Loading Question: Current Index = " + index);

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

        String mediaUrl = currentQuestion.getMediaUrl();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        if (mediaUrl.contains("youtu.be") || mediaUrl.contains("youtube.com")) {
            String videoId = extractYouTubeVideoId(mediaUrl);
            if (videoId != null) {
                String videoHtml = "<html><body style='margin:0;padding:0;'>"
                        + "<iframe width='100%' height='100%' "
                        + "src='https://www.youtube.com/embed/" + videoId
                        + "?autoplay=1&rel=0&controls=1&modestbranding=1&showinfo=0' "
                        + "frameborder='0' allowfullscreen></iframe></body></html>";
                webView.loadData(videoHtml, "text/html", "utf-8");
                webView.setVisibility(View.VISIBLE);
            } else {
                webView.setVisibility(View.INVISIBLE);
            }
        } else if (mediaUrl.endsWith(".jpg") || mediaUrl.endsWith(".png") || mediaUrl.endsWith(".jpeg") || mediaUrl.endsWith(".webp")) {
            String imageHtml = "<html><body style='margin:0;padding:0;'>"
                    + "<img src='" + mediaUrl + "' width='100%' height='100%' style='object-fit:contain;'/>"
                    + "</body></html>";
            webView.loadData(imageHtml, "text/html", "utf-8");
            webView.setVisibility(View.VISIBLE);
        } else {
            String audioHtml = "<html><body style='margin:0;padding:0;'>"
                    + "<audio controls autoplay style='width:100%;'>"
                    + "<source src='" + mediaUrl + "' type='audio/mpeg'>"
                    + "Your browser does not support the audio element."
                    + "</audio></body></html>";
            webView.loadData(audioHtml, "text/html", "utf-8");
            webView.setVisibility(View.VISIBLE);
        }

        int progress = (int) (((float) (index + 1) / maxQuestions) * 100);
        progressBar.setProgress(progress);
    }

    private String extractYouTubeVideoId(String url) {
        if (url == null || url.isEmpty()) return null;
        String pattern = "(?:youtube\\.com/embed/|youtube\\.com/watch\\?v=|youtu\\.be/)([^?&]*)";
        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = compiledPattern.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}
