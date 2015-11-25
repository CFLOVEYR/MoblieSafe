package com.tofirst.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tofirst.mobilesafe.bean.SoftManangerInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by StudyLifetime on 2015/11/22.
 */
public class AppInfos {

    private static final String TAG ="Test" ;

    public static  List<SoftManangerInfo> getAppInfo(Context context) {
        List<SoftManangerInfo> list = new ArrayList<SoftManangerInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo pakageinfo : installedPackages) {
            SoftManangerInfo softinfo = new SoftManangerInfo();
            //获得图片
            Drawable drawable = pakageinfo.applicationInfo.loadIcon(packageManager);
            softinfo.setIcon(drawable);
            //获得包的名字
            CharSequence charSequence = pakageinfo.applicationInfo.loadLabel(packageManager);
//            Log.i(TAG, "-------------"+charSequence.toString());
            softinfo.setName(charSequence.toString());
            //获得包的名字
            String processName = pakageinfo.applicationInfo.processName;
            softinfo.setPakageName(processName);
            //获得资源路径
            String sourceDir = pakageinfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //获得资源的大小
            long length = file.length();
            softinfo.setAppSize(length);
            int flags = pakageinfo.applicationInfo.flags;
            /**
             * 判断是否是系统应用
             */
            if ((flags&ApplicationInfo.FLAG_SYSTEM)!=0) {
                //是系统应用
                softinfo.setIsUserApp(false);
            }else{
                //不是系统应用
                softinfo.setIsUserApp(true);
            }

            /**
             * 判断是否在手机内存
             */
            if ((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0) {
                //存储在Sdcard中
                softinfo.setIsRom(false);
            }else{
                //存储在内存中
                softinfo.setIsRom(true);
            }

            list.add(softinfo);
        }

        return list;
    }


}
