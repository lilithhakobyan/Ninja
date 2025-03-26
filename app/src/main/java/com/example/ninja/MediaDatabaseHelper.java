package com.example.ninja;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MediaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "media.db";
    private static final int DATABASE_VERSION = 2; // Increment version for schema changes

    private static final String TABLE_MEDIA = "media";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_DURATION = "duration"; // Duration in seconds
    private static final String COLUMN_TYPE = "type"; // "audio" or "video"
    private static final String COLUMN_VIDEO_URL = "video_url"; // YouTube URL

    // Create table query
    private static final String CREATE_TABLE_MEDIA =
            "CREATE TABLE " + TABLE_MEDIA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_FILE_PATH + " TEXT, " + // Can be NULL for YouTube videos
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_VIDEO_URL + " TEXT" + ");"; // Store YouTube video URL

    public MediaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEDIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If upgrading, drop old table and create new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        onCreate(db);
    }

    // Method to add media (Local File OR YouTube Video)
    public boolean addMedia(String title, String filePath, int duration, String type, String videoUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_FILE_PATH, filePath); // NULL for YouTube videos
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_VIDEO_URL, videoUrl); // NULL for local files

        long result = db.insert(TABLE_MEDIA, null, values);
        db.close();
        return result != -1;
    }

    // Retrieve all media from the database
    public Cursor getAllMedia() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MEDIA, null);
    }

    // Delete media by ID
    public boolean deleteMedia(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_MEDIA, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }
}