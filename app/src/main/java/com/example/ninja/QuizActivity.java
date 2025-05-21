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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final int MAX_QUESTIONS = 7;

    private TextView quizTitle, questionText;
    private Button ans1, ans2, ans3, nextQuestion;
    private WebView webView;
    private ProgressBar progressBar;
    private int score = 0;

    private MediaDatabaseHelper dbHelper;
    private List<QuizQuestion> quizList;
    private List<QuizQuestion> selectedQuestions;
    private int currentIndex = 0;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();
        checkUserAuthentication();
        initializeFirestore();
        setupDatabase();
        setupQuestionSelection();
        setupAnswerListeners();
    }

    private void initializeViews() {
        quizTitle = findViewById(R.id.quizTitle);
        questionText = findViewById(R.id.questionText);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        nextQuestion = findViewById(R.id.nextQuestion);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        // Configure WebView
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
    }

    private void checkUserAuthentication() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(QuizActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void initializeFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupDatabase() {
        dbHelper = new MediaDatabaseHelper(this);
        addQuizQuestions(); // Initialize with default questions
        quizList = dbHelper.getAllQuizQuestions();

        if (quizList.isEmpty()) {
            Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupQuestionSelection() {
        selectedQuestions = getUniqueVideoQuestions(quizList, MAX_QUESTIONS);
        loadQuestion(currentIndex);
    }

    private void setupAnswerListeners() {
        View.OnClickListener answerClickListener = v -> {
            Button selectedButton = (Button) v;
            checkAnswer(selectedButton.getText().toString());
            nextQuestion.setEnabled(true);
        };

        ans1.setOnClickListener(answerClickListener);
        ans2.setOnClickListener(answerClickListener);
        ans3.setOnClickListener(answerClickListener);

        nextQuestion.setOnClickListener(v -> handleNextQuestion());
    }

    private void handleNextQuestion() {
        if (currentIndex < MAX_QUESTIONS - 1) {
            currentIndex++;
            loadQuestion(currentIndex);
        } else {
            saveScoreToFirestore();
            navigateToScorePage();
        }
    }

    private void navigateToScorePage() {
        Intent intent = new Intent(QuizActivity.this, ScorePage.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", MAX_QUESTIONS);
        startActivity(intent);
        finish();
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

        dbHelper.addQuizQuestion(
                "Haghordum Quiz",
                "https://i.postimg.cc/d359F6mp/Screenshot-2025-05-20-125547.png",
                "Ո՞ր արտահայտությունն է պատկանում տվյալ հերոսին",
                "Ընդե մի տեսակ հաճելի լաուրա կա",
                "Արա պադվատիտ չանես, այ լակոտ",
                "Ես ձեր անասուն ցավը տանեմ",
                "Ես ձեր անասուն ցավը տանեմ",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Haghordum Quiz",
                "https://youtu.be/Wo6ttiBi5mg",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Ես ինչ իմանամ",
                "Էն վախտ փող պետք չէր",
                "Հերս ասում էր, չէի լսում",
                "Էն վախտ փող պետք չէր",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Haghordum Quiz",
                "https://i.postimg.cc/m27JHzNn/Screenshot-2025-05-20-120720.png",
                "Ո՞ր արտահայտությունն է պատկանում տվյալ հերոսին",
                "Տիկին, դուք սխալվել եք",
                "Հլը նորից կրկնի",
                "Ասում եմ չէ,նովու",
                "Տիկին, դուք սխալվել եք",
                "image"
        );


    }

    private List<QuizQuestion> getUniqueVideoQuestions(List<QuizQuestion> quizList, int maxQuestions) {
        List<QuizQuestion> uniqueQuestions = new ArrayList<>();
        Set<String> mediaUrls = new HashSet<>();

        Collections.shuffle(quizList);

        Collections.shuffle(quizList);

        for (QuizQuestion question : quizList) {
            String mediaUrl = question.getMediaUrl();
            if (!mediaUrls.contains(mediaUrl)) {
                mediaUrls.add(mediaUrl);
                uniqueQuestions.add(question);
                if (uniqueQuestions.size() >= maxQuestions) break;
            }
        }
        return uniqueQuestions;
    }




    private void checkAnswer(String selectedAnswer) {
        QuizQuestion currentQuestion = selectedQuestions.get(currentIndex);

        // Disable all answer buttons
        ans1.setEnabled(false);
        ans2.setEnabled(false);
        ans3.setEnabled(false);

        // Highlight correct answer
        highlightCorrectAnswer(currentQuestion.getCorrectAnswer());

        // Check if selected answer is correct
        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            score++;
            highlightSelectedAnswer(selectedAnswer, R.color.green);
        } else {
            highlightSelectedAnswer(selectedAnswer, R.color.red);
        }
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (ans1.getText().toString().equals(correctAnswer)) {
            ans1.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (ans2.getText().toString().equals(correctAnswer)) {
            ans2.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (ans3.getText().toString().equals(correctAnswer)) {
            ans3.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void highlightSelectedAnswer(String selectedAnswer, int colorRes) {
        if (ans1.getText().toString().equals(selectedAnswer)) {
            ans1.setBackgroundColor(getResources().getColor(colorRes));
        } else if (ans2.getText().toString().equals(selectedAnswer)) {
            ans2.setBackgroundColor(getResources().getColor(colorRes));
        } else if (ans3.getText().toString().equals(selectedAnswer)) {
            ans3.setBackgroundColor(getResources().getColor(colorRes));
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadQuestion(int index) {
        QuizQuestion currentQuestion = selectedQuestions.get(index);

        quizTitle.setText("Հարց  " + (index + 1));
        questionText.setText(currentQuestion.getQuestion());
        ans1.setText(currentQuestion.getOption1());
        ans2.setText(currentQuestion.getOption2());
        ans3.setText(currentQuestion.getOption3());

        resetAnswerButtons();
        loadMedia(currentQuestion.getMediaUrl(), currentQuestion.getMediaType());
        updateProgress(index);
    }

    private void resetAnswerButtons() {
        int bgColor = getResources().getColor(R.color.bg);
        ans1.setBackgroundColor(bgColor);
        ans2.setBackgroundColor(bgColor);
        ans3.setBackgroundColor(bgColor);

        ans1.setEnabled(true);
        ans2.setEnabled(true);
        ans3.setEnabled(true);

        nextQuestion.setEnabled(false);
    }

    private void loadMedia(String mediaUrl, String mediaType) {
        if (mediaUrl == null || mediaUrl.isEmpty()) {
            webView.setVisibility(View.GONE);
            return;
        }

        webView.setVisibility(View.VISIBLE);

        if (mediaType.equals("video")) {
            String videoId = extractYouTubeVideoId(mediaUrl);
            if (videoId != null) {
                String videoHtml = "<html><body style='margin:0;padding:0;'>" +
                        "<iframe width='100%' height='100%' " +
                        "src='https://www.youtube.com/embed/" + videoId +
                        "?autoplay=1&rel=0&controls=1&modestbranding=1' " +
                        "frameborder='0' allowfullscreen></iframe></body></html>";
                webView.loadData(videoHtml, "text/html", "utf-8");
            } else {
                Log.w(TAG, "Invalid YouTube URL: " + mediaUrl);
                webView.setVisibility(View.GONE);
            }
        } else if (mediaType.equals("image")) {
            String imageHtml = "<html><body style='margin:0;padding:0;'>" +
                    "<img src='" + mediaUrl + "' style='width:100%;height:100%;object-fit:contain;'/>" +
                    "</body></html>";
            webView.loadData(imageHtml, "text/html", "utf-8");
        } else {
            webView.setVisibility(View.GONE);
        }
    }

    private void updateProgress(int index) {
        int progress = (int) (((float) (index + 1) / MAX_QUESTIONS) * 100);
        progressBar.setProgress(progress);
    }

    private String extractYouTubeVideoId(String url) {
        if (url == null) return null;

        try {
            if (url.contains("youtu.be/")) {
                return url.substring(url.lastIndexOf("/") + 1).split("\\?")[0];
            } else if (url.contains("watch?v=")) {
                return url.substring(url.indexOf("v=") + 2).split("&")[0];
            } else if (url.contains("youtube.com/embed/")) {
                return url.substring(url.lastIndexOf("/") + 1).split("\\?")[0];
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract video ID", e);
        }
        return null;
    }

    private void saveScoreToFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // First get the current score from Firestore
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            long currentTotalScore = documentSnapshot.contains("score") ?
                                    documentSnapshot.getLong("score") : 0;

                            long newTotalScore = currentTotalScore + score;

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("score", newTotalScore);

                            firestore.collection("users").document(userId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Score updated successfully");
                                        navigateToScorePage();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating score", e);
                                        navigateToScorePage();
                                    });
                        } else {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("score", score);

                            firestore.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Score created successfully");
                                        navigateToScorePage();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error creating score", e);
                                        navigateToScorePage();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error getting current score", e);
                        navigateToScorePage(); // Still navigate even if get fails
                    });
        } else {
            navigateToScorePage();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}