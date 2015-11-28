package com.tofirst.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by StudyLifetime on 2015/11/20.
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "mobilesafe.db ", null, 1);
    }

    /**
     * 创建表
     *
     * @param sqLiteDatabase blacknumber 表名
     *                       number 拦截的号码
     *                       mode 拦截模式：短信，电话，短信+电话
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table blacknumber (_id integer primary key" +
                " autoincrement,number varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
