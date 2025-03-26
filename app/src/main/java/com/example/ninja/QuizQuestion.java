package com.example.ninja;

public class QuizQuestion {
    private String videoUrl;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String correctAnswer;

    // Constructor
    public QuizQuestion(String videoUrl, String question, String option1, String option2, String option3, String correctAnswer) {
        this.videoUrl = videoUrl;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.correctAnswer = correctAnswer;
    }

    // Getters and Setters
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
