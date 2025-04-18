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
    List<QuizQuestion> selectedQuestions;
    int currentIndex = 0;
    int maxQuestions = 6; // Limit to 6 questions

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
        progressBar = findViewById(R.id.progressBar);

        dbHelper = new MediaDatabaseHelper(this);

        addQuizQuestions();

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
                Intent intent = new Intent(QuizActivity.this, ScorePage.class);
                intent.putExtra("score", score);
                intent.putExtra("totalQuestions", maxQuestions);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addQuizQuestions() {
        dbHelper.clearQuizQuestions();

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/l8FL_MJmyJ0?rel=0",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Տրեներ,բայց ես նորմալ հայերեն հասկանում եմ",
                "Էն ժամանակ, ոնց որ, սենց չոտկի չէիր ասում",
                "Ապե, դու լուրջ ես ասու՞մ",
                "Տրեներ,բայց ես նորմալ հայերեն հասկանում եմ",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/D4s5P4VqLf0?rel=0",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Ապեր, ասում եմ՝ շինանյութի խանութ ա։",
                "Էս մեր անուշ ախպերն էլ, էն մյուս կեսն ա ուզում",
                "Էս էլ էտ քյալն ա",
                "Էս մեր անուշ ախպերն էլ, էն մյուս կեսն ա ուզում",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/6FjkW7G4bTg?rel=0",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Հա բա,նախարար մարդ եմ",
                "Ապեր ջան կարող ա ես գիժ եմ,բայց ես տուպոյ չեմ",
                "Ոչինչ,կմեծանաս ինքդ կհասկանաս",
                "Ապեր ջան կարող ա ես գիժ եմ,բայց ես տուպոյ չեմ",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/mKp2EHzZpAw?rel=0",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Բա ինչի՞ ա պետք որ",
                "Պետք ա` գնա առ",
                "Ձեզի ջինըս պե՞տք ա",
                "Բա ինչի՞ ա պետք որ",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Geography Quiz",
                "https://www.youtu.be/ZeY5l_HzkzA?rel=0",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Պոպոք ու սմետան",
                "Արա չես ջոգում որ ձեն չենք հանում ուրեմն կարտոշկա ա",
                "Արա մի հատ ոտերդ քաշի,ապուշ լակոտ",
                "Արա չես ջոգում որ ձեն չենք հանում ուրեմն կարտոշկա ա",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Math Quiz",
                "https://www.youtu.be/OuZlhjRBXzc?rel=0",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "5 անգամ հրավիրել են մարդիկ,արդեն ամոթ ա",
                "Շատ ճիշտ նկատեցիք,այո",
                "Ապեր ստեղ պահի,աչքիս սխալ մարշուտկա եմ նստել",
                "Ապեր ստեղ պահի,աչքիս սխալ մարշուտկա եմ նստել",
                "video"
        );
    }

    private List<QuizQuestion> getUniqueVideoQuestions(List<QuizQuestion> quizList, int maxQuestions) {
        List<QuizQuestion> uniqueQuestions = new ArrayList<>();
        Set<String> videoUrls = new HashSet<>();
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

        String videoId = extractYouTubeVideoId(currentQuestion.getMediaUrl());
        if (videoId != null) {
            String videoHtml = "<html><body style='margin:0;padding:0;'>" +
                    "<iframe width='100%' height='100%' " +
                    "src='https://www.youtube.com/embed/" + videoId +
                    "?autoplay=1&rel=0&controls=1&modestbranding=1' " +
                    "frameborder='0' allowfullscreen></iframe></body></html>";

            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadData(videoHtml, "text/html", "utf-8");
        }

        nextQuestion.setEnabled(false);
        progressBar.setProgress((int) (((float) (index + 1) / maxQuestions) * 100));
    }

    private String extractYouTubeVideoId(String videoUrl) {
        try {
            if (videoUrl.contains("youtu.be/")) {
                return videoUrl.substring(videoUrl.lastIndexOf("/") + 1).split("\\?")[0];
            } else if (videoUrl.contains("watch?v=")) {
                return videoUrl.substring(videoUrl.indexOf("v=") + 2).split("&")[0];
            }
        } catch (Exception e) {
            Log.e("QuizActivity", "Failed to extract video ID: " + e.getMessage());
        }
        return null;
    }
}