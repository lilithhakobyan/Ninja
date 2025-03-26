package com.example.ninja;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizActivity extends AppCompatActivity {

    TextView quizTitle;
    View progressBar1, progressBar2;
    Button ans1, ans2, ans3, nextQuestion;
    WebView webView;  // Declare the WebView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quizTitle = findViewById(R.id.quizTitle);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        webView = findViewById(R.id.webView);  // Initialize the WebView

        MediaDatabaseHelper dbHelper = new MediaDatabaseHelper(this);

        boolean isInserted = dbHelper.addMedia(
                "Quiz Video",
                null,   // No local file
                0,      // Duration unknown
                "video",
                "https://www.youtube.com/embed/8IWPuf_03aw?rel=0"
        );

        if (isInserted) {
            Toast.makeText(this, "YouTube video saved!", Toast.LENGTH_SHORT).show();
        }

        // Set up WebView to play the YouTube video in embedded mode without related videos
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  // Enable JavaScript for YouTube video playback
        // Embed YouTube URL with rel=0 to prevent related videos at the end
        webView.loadUrl("https://www.youtube.com/embed/8IWPuf_03aw?rel=0");  // Embed URL with rel=0 to disable recommendations
    }
}
