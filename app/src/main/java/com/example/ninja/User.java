package com.example.ninja;

public class User {
    private String username;
    private long score;

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
}