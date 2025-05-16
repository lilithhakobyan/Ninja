package com.example.ninja;

public class ProfilePicture {
    private String id;
    private String url;

    public ProfilePicture(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
