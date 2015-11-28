package com.tofirst.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tofirst.mobilesafe.bean.SoftManangerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Study on 2015/11/28.
 */
public class SoftLockDao  {
    private final SoftLockDBOpenHelper dbOpenHelper;

    public SoftLockDao(Context context) {
        dbOpenHelper = new SoftLockDBOpenHelper(context);
    }

    /**
     *  程序锁添加数据的方法
     * @param name 应用的名称
     * @return
     */
    public void insert(String name){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        long softlock = db.insert("softlock", null, values);
        db.close();
    }

    /**
     *  删除应用的名字
     * @param name
     */
    public void delete(String name){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("softlock","name = ?",new String[]{name});
        db.close();
    }

    /**
     * 查询数据库中是否存在此包名
     * @param name 包名
     * @return
     */
    public boolean query(String name){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select name from softlock where name = ?", new String[]{name});
        if (cursor.moveToNext()) {
        return  true;
        }
        return  false;
    }

    public List<String> queryAll(){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<String> infos=new ArrayList<String>();
        Cursor cursor = db.rawQuery("select * from softlock",null);
        int num=0;
        while (cursor.moveToNext()){
            SoftManangerInfo info=new SoftManangerInfo();
            String appname = cursor.getString(cursor.getColumnIndex("name"));
            infos.add(num,appname);
            num++;
        }
        cursor.close();
        db.close();
        return  infos;
    }

}
