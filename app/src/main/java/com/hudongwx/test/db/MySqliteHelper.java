package com.hudongwx.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hudongwx on 16-11-19.
 */
public class MySqliteHelper extends SQLiteOpenHelper {

    public MySqliteHelper(Context context) {
        super(context, "data", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table bookmarks(" +
                "name varchar not null," +
                "url varchar not null," +
                "date varchar);");
        db.execSQL("create table history(" +
                "name varchar not null," +
                "url varchar not null," +
                "date varchar);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
