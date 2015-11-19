package com.tofirst.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	public static final String PATH = "data/data/com.tofirst.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address="未知号码";
		/**
		 * 这个方法读取数据库只能读取files目录下的内容所以才写死的内容，需要在Spalash页面来赋值内容到files目录下
		 * 第二个参数：当null是默认值，游标工厂 第三个参数：设置为只读的权限，防止修改了原来的文件
		 */
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		switch (number.length()) {
		case 3:
		if (number.equals("110")) {
			address="匪警电话";
		}else if (number.equals("120")) {
			address="急救电话";
		}else if (number.equals("119")) {
			address="火警电话";
		}
			break;
		case 4:
			address = "模拟器";
			break;
		case 5:
			address = "客服电话";
			break;
		case 7:
		case 8:
			address = "本地电话";
			break;
		default:
			address = "未知号码";
			// 手机号码规则：1 +(3 4 5 6 7 8) + 9位数字
			if (number.matches("^1[3-8]\\d{9}$")) {
				Cursor cursor = db
						.rawQuery(
								"select location from data2 where id = (select outkey from data1 where id = ?)",
								new String[] { number.substring(0, 7)});
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
					System.out.println("-----"+address);
					cursor.close();
				}
			}
			if (number.startsWith("0") && number.length() > 10) {
				// 因为有些区号是三位，有些事四位，都是0开头的，就不查询0了，从第二位开始

				// 首先查询四位的
				Cursor cursor1 = db.rawQuery(
						"select location from data2 where area = ?",
						new String[] { number.substring(1, 4) });
				if (cursor1.moveToNext()) {
					address = cursor1.getString(0);
					cursor1.close();
				}
				Cursor cursor2 = db.rawQuery(
						"select location from data2 where area = ?",
						new String[] { number.substring(1, 3) });
				if (cursor2.moveToNext()) {
					address = cursor2.getString(0);
					cursor2.close();
				}
			}
			break;
		}

		return address;
	}

}
