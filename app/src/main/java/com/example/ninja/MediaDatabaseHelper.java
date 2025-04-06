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
    private static final int DATABASE_VERSION = 3;

    // Define the table name and columns for media and quiz questions
    private static final String TABLE_MEDIA = "media";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_VIDEO_URL = "video_url";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTION1 = "option1";
    private static final String COLUMN_OPTION2 = "option2";
    private static final String COLUMN_OPTION3 = "option3";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    private static final String TABLE_VITAMIN = "vitamin_quiz";

    // Create the table query
    private static final String CREATE_TABLE_MEDIA =
            "CREATE TABLE " + TABLE_MEDIA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_VIDEO_URL + " TEXT NOT NULL, " +
                    COLUMN_QUESTION + " TEXT NOT NULL, " +
                    COLUMN_OPTION1 + " TEXT NOT NULL, " +
                    COLUMN_OPTION2 + " TEXT NOT NULL, " +
                    COLUMN_OPTION3 + " TEXT NOT NULL, " +
                    COLUMN_CORRECT_ANSWER + " TEXT NOT NULL" + ");";

    private static final String CREATE_TABLE_VITAMIN =
            "CREATE TABLE " + TABLE_VITAMIN + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_QUESTION + " TEXT NOT NULL, " +
                    COLUMN_OPTION1 + " TEXT NOT NULL, " +
                    COLUMN_OPTION2 + " TEXT NOT NULL, " +
                    COLUMN_OPTION3 + " TEXT NOT NULL, " +
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

    // Insert a quiz question with video
    public boolean addQuizQuestion(String title, String videoUrl, String question, String option1, String option2, String option3, String correctAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_VIDEO_URL, videoUrl);
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_OPTION1, option1);
        values.put(COLUMN_OPTION2, option2);
        values.put(COLUMN_OPTION3, option3);
        values.put(COLUMN_CORRECT_ANSWER, correctAnswer);

        long result = db.insert(TABLE_MEDIA, null, values);
        db.close();
        return result != -1;
    }

    // Get all quiz questions
    public List<QuizQuestion> getAllQuizQuestions() {
        List<QuizQuestion> quizList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDIA, null);

        if (cursor.moveToFirst()) {
            do {
                // Create a QuizQuestion object for each row in the table
                QuizQuestion question = new QuizQuestion(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORRECT_ANSWER))
                );
                quizList.add(question);  // Add the question to the list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return quizList;  // Return the list of all quiz questions
    }

    public void clearQuizQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MEDIA); // Delete all rows from the media table
        db.close();
    }


}
