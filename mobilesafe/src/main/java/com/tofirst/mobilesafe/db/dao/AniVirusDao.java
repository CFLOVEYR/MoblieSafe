package com.tofirst.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AniVirusDao {
	public static final String PATH1 = "data/data/com.tofirst.mobilesafe/files/antivirus.db";

	public static String getVirusMd5String(String md5) {
		String result=null;
		/**
		 * 这个方法读取数据库只能读取files目录下的内容所以才写死的内容，需要在Spalash页面来赋值内容到files目录下
		 * 第二个参数：当null是默认值，游标工厂 第三个参数：设置为只读的权限，防止修改了原来的文件
		 */
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH1, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
		if (cursor.moveToNext()) {
			result = cursor.getString(cursor.getColumnIndex("desc"));
		}
//		//关闭游标和关闭数据库
		cursor.close();
		db.close();
		return result;
	}

}
