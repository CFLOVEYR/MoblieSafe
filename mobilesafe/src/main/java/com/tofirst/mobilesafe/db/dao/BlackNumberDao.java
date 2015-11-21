package com.tofirst.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.tofirst.mobilesafe.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StudyLifetime on 2015/11/20.
 */
public class BlackNumberDao {

    private final BlackNumberDBOpenHelper dbOpenHelper;

    public BlackNumberDao(Context context) {
        dbOpenHelper = new BlackNumberDBOpenHelper(context);

    }

    /**
     * 添加数据的方法
     * @param number
     * @param mode
     * @return 是否成功 true成功，false失败
     */
    public boolean insert(String number,String mode){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values =new ContentValues();
        values.put("number",number);
        values.put("mode", mode);
        long blacknumber = db.insert("blacknumber", null, values);
        db.close();
        if (blacknumber == -1) {
            return  false;
        }
        return  true;
    }
    /**
     * 删除数据的方法
     * @param number
     * @return  是否成功 true成功，false失败
     */
    public boolean delete(String number){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        int blacknumber = db.delete("blacknumber", "number = ?", new String[]{number});
        db.close();
        if (blacknumber == 0) {
            return  false;
        }
        return  true;
    }
    /**
     * 更新数据的方法
     * @param number 要更改的号码
     * @param mode  更改的模式
     * @return 是否成功 true成功，false失败
     */
    public boolean update(String number,String mode){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values =new ContentValues();
        values.put("mode", mode);
        int update = db.update("blacknumber", values, "number = ?", new String[]{number});
        db.close();
        return  true;
    }
    /**
     * 查询号码模式的方法
     * @param number
     * @return 是否成功 true成功，false失败
     */
    public String query(String number){
        String mode=null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode=cursor.getString(0);
        }
        db.close();
        cursor.close();
        return  mode;
    }
    /**
     * 查询号码是否存在的方法
     * @param number
     * @return 是否成功 true成功，false失败
     */
    public boolean queryExist(String number){
        boolean result=false;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
           result=true;
        }
        db.close();
        cursor.close();
        return  result;
    }
    /**
     * 查询所有数据的方法
     * @return 是否成功 true成功，false失败
     */
    public List<BlackNumberInfo> queryAll(){
        List<BlackNumberInfo> list=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber", null);
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            list.add(info);
        }
        db.close();
        cursor.close();
        SystemClock.sleep(3000);
        return  list;
    }

    /**
     * 分页查询数据
     * @param pageNumber
     * @param pageSize
     * limit 限制一次取多少个数据
     * offset 表示从哪个数据开始取
     * @return 要查询的数据
     */
    public List<BlackNumberInfo> queryPage(int pageNumber,int pageSize){
        List<BlackNumberInfo> list=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(pageSize),String.valueOf(pageSize*pageNumber)});
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            list.add(info);
        }
        cursor.close();
        db.close();
//        SystemClock.sleep(3000);
        return  list;
    }

    /**
     * 查询一共有多少条数据的方法
     * @return 返回的是数据的数目
     */
    public int queryCount(){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int num=cursor.getInt(0);
        cursor.close();
        db.close();
        return  num;
    }

    /**
     *  分批加载查询数据的方法
     * @param maxCount  每次查询多少条数据
     * @param startIndex 每次查询的起始位置
     * @return
     */
    public List<BlackNumberInfo> queryBatches(int maxCount,int startIndex){
        List<BlackNumberInfo> list=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            list.add(info);
        }
        cursor.close();
        db.close();
//        SystemClock.sleep(3000);
        return  list;
    }
}
