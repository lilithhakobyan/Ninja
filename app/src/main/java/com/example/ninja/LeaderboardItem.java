package com.example.ninja;

public class LeaderboardItem {
    private String userId;
    private String username;
    private int score;

    public LeaderboardItem(String userId, String username, int score) {
        this.userId = userId;
        this.username = username;
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

