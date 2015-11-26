package com.tofirst.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.bean.ProgressInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Study on 2015/11/26.
 */
public class ProcessInfos {

    public static List<ProgressInfo> getPogressInfos(Context context) {
        //用来存储对象的
        List<ProgressInfo> infos = new ArrayList<ProgressInfo>();
        //获得Activity管理者
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        //获取正运行的程序
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        // 得到包管理器,方便下边得到运行程序的图标和名字和是否是系统app还是用户app
        PackageManager pm = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {

            ProgressInfo progressInfo = new ProgressInfo();
            // 注意导包android.os.Debug.MemoryInfo
            Debug.MemoryInfo[] memoryInfo = am
                    .getProcessMemoryInfo(new int[]{info.pid});
            //进程在虚拟机的大小
            // memoryInfo[1].dalvikPrivateDirty;
            //进程在本地运行的大小-jni调用的C代码运行的大小
            // memoryInfo[1].nativePrivateDirty;
            // 单位是kb
            int memory = memoryInfo[0].getTotalPrivateDirty();
            // 内存占用大小
            long memorySize = memory * 1024L;
            progressInfo.setAppSize(memorySize);
            // 包名、进程名
            String packageName = info.processName;
            progressInfo.setPakageName(packageName);
            try {
                /**
                 * 通过PackageMannager来管理用户的获得程序的图标,名字,是否是系统app的判断
                 */
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                // 图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                progressInfo.setIcon(icon);
                // 软件名称
                String name = packageInfo.applicationInfo.loadLabel(pm).toString();
                progressInfo.setAppName(name);
                int falgs = packageInfo.applicationInfo.flags;

                if ((falgs & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    // 系统进程
                    progressInfo.setIsUserApp(false);
                } else {
                    // 用户进程
                    progressInfo.setIsUserApp(true);
                }


            } catch (PackageManager.NameNotFoundException e) {
                /**
                 *   因为有三个进程是系统的进程,没有名称和图片,报空指针异常
                 *
                 *   给赋值两个值
                 */

                progressInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                progressInfo.setAppName("系统非常重要的应用");
                e.printStackTrace();
            }
            infos.add(progressInfo);
        }
        return infos;
    }
}
