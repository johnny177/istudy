package com.nnoboa.istudy.ui.flashcard.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FlashDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "flash.db";
    private static final int DATABASE_VERSION = 1;

    public FlashDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SQL_SET_DB = "CREATE TABLE " + FlashContract.SetEntry.TABLE_NAME + " (" +
                FlashContract.SetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FlashContract.SetEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FlashContract.SetEntry.COLUMN_DESCRIPTION + " TEXT, " +
                FlashContract.SetEntry.COLUMN_STAR + " INTEGER NOT NULL DEFAULT 0, " +
                FlashContract.SetEntry.COLUMN_COUNT + " INTEGER DEFAULT 0, " +
                FlashContract.SetEntry.COLUMN_PROGRESS + " INTEGER DEFAULT 0, " +
                FlashContract.SetEntry.COLUMN_DATE_CREATED + " INTEGER NOT NULL DEFAULT 0 )";
        db.execSQL(CREATE_SQL_SET_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void createSetTable(SQLiteDatabase db, String TABLE_NAME) {
        String CREATE_SET_CARD_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                FlashContract.CardEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FlashContract.CardEntry.SET_ID + " TEXT NOT NULL, " +
                FlashContract.CardEntry.COLUMN_FRONT_TEXT + " TEXT, " +
                FlashContract.CardEntry.COLUMN_BACK_TEXT + " TEXT, " +
                FlashContract.CardEntry.COLUMN_TAG_TEXT + " TEXT NOT NULL, " +
                FlashContract.CardEntry.COLUMN_FRONT_URI + " TEXT, " +
                FlashContract.CardEntry.COLUMN_BACK_URI+ " TEXT )";
        db.execSQL(CREATE_SET_CARD_TABLE);
    }
}
