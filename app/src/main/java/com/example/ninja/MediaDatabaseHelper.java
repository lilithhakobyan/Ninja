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
    private static final int DATABASE_VERSION = 5; // Incremented version to handle new table

    // Table names
    private static final String TABLE_MEDIA = "media";
    private static final String TABLE_VITAMIN = "vitamin_quiz";
    private static final String TABLE_PROFILE_PICTURES = "profile_pictures";

    // Common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";

    // Media table columns
    private static final String COLUMN_MEDIA_URL = "media_url";
    private static final String COLUMN_MEDIA_TYPE = "media_type";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTION1 = "option1";
    private static final String COLUMN_OPTION2 = "option2";
    private static final String COLUMN_OPTION3 = "option3";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    // Profile pictures table columns
    private static final String COLUMN_PICTURE_URL = "picture_url";
    private static final String COLUMN_PICTURE_ID = "picture_id";

    // Create table statements
    private static final String CREATE_TABLE_MEDIA =
            "CREATE TABLE " + TABLE_MEDIA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_MEDIA_URL + " TEXT NOT NULL, " +
                    COLUMN_MEDIA_TYPE + " TEXT NOT NULL, " +
                    COLUMN_QUESTION + " TEXT NOT NULL, " +
                    COLUMN_OPTION1 + " TEXT NOT NULL, " +
                    COLUMN_OPTION2 + " TEXT NOT NULL, " +
                    COLUMN_OPTION3 + " TEXT NOT NULL, " +
                    COLUMN_CORRECT_ANSWER + " TEXT NOT NULL" + ");";

    private static final String CREATE_TABLE_VITAMIN =
            "CREATE TABLE " + TABLE_VITAMIN + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_MEDIA_URL + " TEXT NOT NULL, " +
                    COLUMN_MEDIA_TYPE + " TEXT NOT NULL, " +
                    COLUMN_QUESTION + " TEXT NOT NULL, " +
                    COLUMN_OPTION1 + " TEXT NOT NULL, " +
                    COLUMN_OPTION2 + " TEXT NOT NULL, " +
                    COLUMN_OPTION3 + " TEXT NOT NULL, " +
                    COLUMN_CORRECT_ANSWER + " TEXT NOT NULL" + ");";

    private static final String CREATE_TABLE_PROFILE_PICTURES =
            "CREATE TABLE " + TABLE_PROFILE_PICTURES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PICTURE_ID + " TEXT NOT NULL, " +
                    COLUMN_PICTURE_URL + " TEXT NOT NULL);";

    public MediaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_VITAMIN);
        db.execSQL(CREATE_TABLE_PROFILE_PICTURES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VITAMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_PICTURES);
        onCreate(db);
    }// Quiz question methods
    public boolean addQuizQuestion(String title, String mediaUrl, String question,
                                   String option1, String option2, String option3,
                                   String correctAnswer, String mediaType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_OPTION1, option1);
        values.put(COLUMN_OPTION2, option2);
        values.put(COLUMN_OPTION3, option3);
        values.put(COLUMN_CORRECT_ANSWER, correctAnswer);

        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            if (mediaType.equals("video")) {
                values.put(COLUMN_MEDIA_TYPE, "video");
                values.put(COLUMN_MEDIA_URL, mediaUrl);
            } else if (mediaType.equals("image")) {
                values.put(COLUMN_MEDIA_TYPE, "image");
                values.put(COLUMN_MEDIA_URL, mediaUrl);
            } else if (mediaType.equals("audio")) {
                values.put(COLUMN_MEDIA_TYPE, "audio");
                values.put(COLUMN_MEDIA_URL, mediaUrl);
            }
        }

        long result = db.insert(TABLE_MEDIA, null, values);
        db.close();
        return result != -1;
    }

    public List<QuizQuestion> getAllQuizQuestions() {
        List<QuizQuestion> quizList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDIA, null);

        if (cursor.moveToFirst()) {
            do {
                QuizQuestion question = new QuizQuestion(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORRECT_ANSWER))
                );
                quizList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return quizList;
    }

    public void clearQuizQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MEDIA);
        db.close();
    }

    public boolean addProfilePicture(String pictureId, String pictureUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PICTURE_ID, pictureId);
        values.put(COLUMN_PICTURE_URL, pictureUrl);

        long result = db.insert(TABLE_PROFILE_PICTURES, null, values);
        db.close();
        return result != -1;
    }



    public List<ProfilePicture> getAllProfilePictures() {
        List<ProfilePicture> pictures = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE_PICTURES, null);

        if (cursor.moveToFirst()) {
            do {
                ProfilePicture picture = new ProfilePicture(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICTURE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICTURE_URL))
                );
                pictures.add(picture);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return pictures;
    }
    public void addProfilePictureIfNotExists(String pictureId, String pictureUrl) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM profile_pictures WHERE picture_id = ?", new String[]{pictureId});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (!exists) {
            addProfilePicture(pictureId, pictureUrl);
        }
    }


}