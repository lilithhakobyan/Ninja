package com.example.ninja;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MediaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 4; // incremented version

    private static final String TABLE_MEDIA = "media";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_MEDIA_URL = "media_url";  // General media URL column
    private static final String COLUMN_MEDIA_TYPE = "media_type"; // Media type (video, image, audio)
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTION1 = "option1";
    private static final String COLUMN_OPTION2 = "option2";
    private static final String COLUMN_OPTION3 = "option3";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    private static final String TABLE_VITAMIN = "vitamin_quiz";

    // Updated CREATE TABLE without difficulty field and added option2 and option3 columns
    private static final String CREATE_TABLE_MEDIA =
            "CREATE TABLE " + TABLE_MEDIA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_MEDIA_URL + " TEXT NOT NULL, " + // General media URL column
                    COLUMN_MEDIA_TYPE + " TEXT NOT NULL, " +
                    COLUMN_QUESTION + " TEXT NOT NULL, " +
                    COLUMN_OPTION1 + " TEXT NOT NULL, " +
                    COLUMN_OPTION2 + " TEXT NOT NULL, " +  // Added option2 column
                    COLUMN_OPTION3 + " TEXT NOT NULL, " +  // Added option3 column
                    COLUMN_CORRECT_ANSWER + " TEXT NOT NULL" + ");";

    private static final String CREATE_TABLE_VITAMIN =
            "CREATE TABLE " + TABLE_VITAMIN + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_MEDIA_URL + " TEXT NOT NULL, " + // General media URL column
                    COLUMN_MEDIA_TYPE + " TEXT NOT NULL, " +
                    COLUMN_QUESTION + " TEXT NOT NULL, " +
                    COLUMN_OPTION1 + " TEXT NOT NULL, " +
                    COLUMN_OPTION2 + " TEXT NOT NULL, " +  // Added option2 column
                    COLUMN_OPTION3 + " TEXT NOT NULL, " +  // Added option3 column
                    COLUMN_CORRECT_ANSWER + " TEXT NOT NULL" + ");";

    public MediaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_VITAMIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VITAMIN);
        onCreate(db);
    }

    // Method to add quiz question with various media types
    public boolean addQuizQuestion(String title, String mediaUrl, String question,
                                   String option1, String option2, String option3,
                                   String correctAnswer, String mediaType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_OPTION1, option1);
        values.put(COLUMN_OPTION2, option2);  // Add option2 column
        values.put(COLUMN_OPTION3, option3);  // Add option3 column
        values.put(COLUMN_CORRECT_ANSWER, correctAnswer);

        // Ensure mediaUrl is not null or empty based on the mediaType
        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            if (mediaType.equals("video")) {
                values.put(COLUMN_MEDIA_TYPE, "video");
                values.put(COLUMN_MEDIA_URL, mediaUrl);
            } else if (mediaType.equals("image")) {
                values.put(COLUMN_MEDIA_TYPE, "image");
                values.put(COLUMN_MEDIA_URL, mediaUrl); // Using same column for image URL
            } else if (mediaType.equals("audio")) {
                values.put(COLUMN_MEDIA_TYPE, "audio");
                values.put(COLUMN_MEDIA_URL, mediaUrl); // Using same column for audio URL
            }
        }

        long result = db.insert(TABLE_MEDIA, null, values);
        db.close();

        return result != -1;
    }

    // Method to get all quiz questions including media type
    public List<QuizQuestion> getAllQuizQuestions() {
        List<QuizQuestion> quizList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDIA, null);

        if (cursor.moveToFirst()) {
            do {
                QuizQuestion question = new QuizQuestion(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_URL)), // Get media URL
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_TYPE)), // Get media type
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)),  // Retrieve option2
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)),  // Retrieve option3
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORRECT_ANSWER))
                );
                quizList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return quizList;
    }

    // Method to clear all quiz questions
    public void clearQuizQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MEDIA);
        db.close();
    }
}
