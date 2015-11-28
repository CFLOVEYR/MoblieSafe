package com.tofirst.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Study on 2015/11/28.
 */
public class SoftLockDBOpenHelper extends SQLiteOpenHelper {
    public SoftLockDBOpenHelper(Context context) {
        super(context, "softlock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table softlock (_id integer primary key" +
                " autoincrement,name varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
