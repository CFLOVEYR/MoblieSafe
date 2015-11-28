package com.tofirst.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Study on 2015/11/28.
 */
public class SharedPreferencesUtils {
    public static  void putBooleanData(Context context,String str,boolean flag){
        SharedPreferences pre = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        pre.edit().putBoolean(str,flag).commit();
    }
    public static  boolean getBooleanData(Context context,String str,boolean flag){
        SharedPreferences pre = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return pre.getBoolean(str,flag);
    }
}
