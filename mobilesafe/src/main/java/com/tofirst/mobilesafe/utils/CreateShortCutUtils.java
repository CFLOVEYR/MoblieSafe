package com.tofirst.mobilesafe.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import com.tofirst.mobilesafe.R;

/**
 * Created by Study on 2015/11/28.
 */
public class CreateShortCutUtils {
    /**
     * 创建快捷方式的方法
     */
    public static  void CreateShortcut(Context context) {
        Intent intent = new Intent();
        //我要创建快捷图片了
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //我的图片
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(context.getResources(), R.drawable.home_netmanager));
        //一个动作，或者说要干什么
        Intent callIntent = new Intent();
        callIntent.setAction("com.tofirst.mobilesafe.home");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, callIntent);
        context.sendBroadcast(intent);
        /**
         * 快捷方式已经创建,就不必在创建
         */
       SharedPreferencesUtils.putBooleanData(context,"InstalledShortCut",true);
    }


}
