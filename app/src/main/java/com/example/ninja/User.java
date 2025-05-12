package com.example.ninja;

public class User {
    private String username;
    private long score;

    // Required for Firebase deserialization
    public User() {
    }

    public User(String username, long score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public long getScore() {
        return score;
    }

    // Optional: setters, only if you want to update values after creation
    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
