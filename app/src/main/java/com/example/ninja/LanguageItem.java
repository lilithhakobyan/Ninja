package com.example.ninja;

public class LanguageItem {
    private String name;
    private int flagResId;

    public LanguageItem(String name, int flagResId) {
        this.name = name;
        this.flagResId = flagResId;
    }

    public String getName() {
        return name;
    }

    public int getFlagResId() {
        return flagResId;
    }
}