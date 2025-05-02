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

public class VitaminQuizActivity extends AppCompatActivity {

    private static final String TAG = "VitaminQuizActivity";
    private static final int MAX_QUESTIONS = 8;

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
        setContentView(R.layout.activity_vitamin_quiz);

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
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
    }

    private void checkUserAuthentication() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(VitaminQuizActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void initializeFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupDatabase() {
        dbHelper = new MediaDatabaseHelper(this);
        addVitaminQuizQuestions();
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
        Intent intent = new Intent(VitaminQuizActivity.this, ScorePage.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", MAX_QUESTIONS);
        startActivity(intent);
        finish();
    }

    private void addVitaminQuizQuestions() {
        dbHelper.clearQuizQuestions();

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://www.youtube.com/watch?v=RlEeH5x0mH4",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Ճոճոլ",
                "Կոկո",
                "Ծկուլ",
                "Ճոճոլ",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://i.postimg.cc/ZKsp8STc/Screenshot-2025-04-23-134437.png",
                "Ի՞նչ է կերպարի անունը",
                "Գվեն",
                "Կլեպ",
                "Մեսրոպիկ",
                "Գվեն",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://youtu.be/20DfYWm4cBM",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Սպիտակ արջի հետ բարբա կպնի",
                "Մեսիի հետ կռվցնենք",
                "100$ով ծախենք Բանանցի վրա",
                "100$ով ծախենք Բանանցի վրա",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://youtu.be/nkzGupXXWRk",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Թմրանյութեր օգտագործու՞մ եք",
                "Извините, դուք ով եք",
                "Դուք ապու՞շ եք",
                "Դուք ապու՞շ եք",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://i.postimg.cc/cHFMDws2/Screenshot-2025-04-20-224753.png",
                "Ո՞ր արտահայտությունն է պատկանում տվյալ հերոսին",
                "Դուք բնանկարի՞չ եք",
                "Տուզիկներ",
                "Այ կովերի փոքրիկ կացարան",
                "Այ կովերի փոքրիկ կացարան",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://i.postimg.cc/zGpCDThB/Screenshot-2025-04-20-230053.png",
                "Ո՞ր արտահայտությունն է պատկանում տվյալ հերոսին",
                "Ապերս, խոսքը խոսքի գնդակ չի, որ փոխանցես",
                "Դոբրի, ընգեր ջան",
                "Ջոգում ենք՝ Եվրոպա ա, բան ա",
                "Ապերս, խոսքը խոսքի գնդակ չի, որ փոխանցես",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://i.postimg.cc/XvfWk9QN/Screenshot-2025-04-12-161540.png",
                "Ո՞ր արտահայտությունն է պատկանում տվյալ հերոսին",
                "Բյուրականի 2 հեկտար հողերս գլխիդ",
                "Բա որ գոմի դուռ չի, դու մեջը ինչ գործ ունես",
                "Պայքարի՛ դու քո դեմ",
                "Բա որ գոմի դուռ չի, դու մեջը ինչ գործ ունես",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://youtu.be/n-ymY3plDyQ",
                "Դիտիր տեսանյութը և կռահիր՝ ինչ է ասում հերոսը հետո",
                "Դու մի բզբզա",
                "Դու ինձ մի աչքով մի նայի",
                "Վա՞տ ես արա",
                "Դու մի բզբզա",
                "video"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://i.postimg.cc/fRMV67gF/Screenshot-2025-04-12-184001.png",
                "Ո՞ր արտահայտությունն է պատկանում տվյալ հերոսին",
                "Они обманщики",
                "Вот это вечеринка",
                "Դուք ուղղակի գենիուս եք",
                "Они обманщики",
                "image"
        );

        dbHelper.addQuizQuestion(
                "Vitamin Quiz",
                "https://i.postimg.cc/T1H0PmFg/Screenshot-2025-04-20-135807.png",
                "Ու՞մ հոգեհացն է",
                "Մանյա",
                "Գալյա",
                "Մանուշ",
                "Մանյա",
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

        ans1.setEnabled(false);
        ans2.setEnabled(false);
        ans3.setEnabled(false);

        highlightCorrectAnswer(currentQuestion.getCorrectAnswer());

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

        quizTitle.setText("Question " + (index + 1) + " of " + MAX_QUESTIONS);
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
            String username = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            Map<String, Object> userData = new HashMap<>();
            userData.put("score", score);

            if (username != null && !username.isEmpty()) {
                userData.put("username", username);
            }
            if (email != null && !email.isEmpty()) {
                userData.put("email", email);
            }

            firestore.collection("users")
                    .document(userId)
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Score updated successfully"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating score", e));
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