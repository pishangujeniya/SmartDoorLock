package com.example.pishang.pishanggui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pishang on 17-11-2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Historybase.db";
    public static final String TABLE_NAME = "historytable";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DATETIME";
    public static final String COL_3 = "TASK";

    SQLiteDatabase db;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,DATETIME TEXT,TASK TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE " + TABLE_NAME);
    }

    public boolean insertdata(String datetime, String task) {
        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COL_2, datetime);
        cv.put(COL_3, task);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result < 0) {
            return false;
        } else {
            return true;
        }


    }

    public Cursor getdata() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);

        return res;
    }

    public void deletedata() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
}
