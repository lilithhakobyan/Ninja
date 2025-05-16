package com.example.ninja;

public class User {
    private String username;
    private long score;
    private String profilePhotoUrl;

    // Constructor
    public User(String username, long score, String profilePhotoUrl) {
        this.username = username;
        this.score = score;
        this.profilePhotoUrl = (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) ?
                profilePhotoUrl : null;
    }

    // Getter
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getUsername() {
        return username;
    }

    public long getScore() {
        return score;
    }
}
