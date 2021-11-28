package com.example.wordbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordsDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "wordbookdb";
    private final static int DATABASE_VERSION = 1;
    //建表SQL
    private final static String SQL_CREATE_DATABASE = "CREATE TABLE "
            + Words.Word.TABLE_NAME + " ("
            + Words.Word.COLUMN_NAME_WORD +" string"+" PRIMARY KEY" + ","
            + Words.Word.COLUMN_NAME_MEANING + " string" + ","
            + Words.Word.COLUMN_NAME_SIMPLE + " string" + ","
            + Words.Word.COLUMN_NAME_SMEANING + " string" + " )";
    //删表SQL
    private final static String SQL_DELETE_DATABASE = "DROP TABLE IF EXISTS " + Words.Word.TABLE_NAME;

    public WordsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_DATABASE);
        onCreate(sqLiteDatabase);
    }
}
