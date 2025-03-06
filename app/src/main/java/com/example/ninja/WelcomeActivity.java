package com.example.ninja;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class WelcomeActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton, nextButton, skipButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    int totalSlides;

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);

            // Show back button only after the first page
            backButton.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);

            // Change Next button text dynamically
            if (position == totalSlides - 1) {
                nextButton.setText("Finish");
            } else {
                nextButton.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if onboarding has been completed before
        SharedPreferences prefs = getSharedPreferences("Onboarding", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("firstTime", true);

        if (!isFirstTime) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_welcome);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);
        slideViewPager = findViewById(R.id.slideViewPager);
        dotIndicator = findViewById(R.id.dotIndicator);

        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);
        totalSlides = viewPagerAdapter.getCount();

        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);

        backButton.setOnClickListener(v -> {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        nextButton.setOnClickListener(v -> {
            int nextItem = getItem(1);
            Log.d("WelcomeActivity", "Next item index: " + nextItem);

            if (nextItem < totalSlides) {
                slideViewPager.setCurrentItem(nextItem, true);
            } else {
                Log.d("WelcomeActivity", "Last slide reached, finishing onboarding.");
                completeOnboarding();
            }
        });


        skipButton.setOnClickListener(v -> completeOnboarding());
    }

    private void completeOnboarding() {
        SharedPreferences.Editor editor = getSharedPreferences("Onboarding", MODE_PRIVATE).edit();
        editor.putBoolean("firstTime", false);
        editor.apply();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    private void setDotIndicator(int position) {
        dots = new TextView[totalSlides];
        dotIndicator.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.blue));
            dotIndicator.addView(dots[i]);
        }

        dots[position].setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}