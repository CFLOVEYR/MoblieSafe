package com.tofirst.mobilesafe.engine;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Study on 2015/11/26.
 */
public class ProcessInfos {

    private static final String TAG ="Test1" ;

    public static List<TaskInfo> getPogressInfos(Context context) {
        //用来存储对象的
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        //获得Activity管理者
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        //获得包管理者
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info :processInfos) {
            TaskInfo taskinfo = new TaskInfo();
            //得到的是包名
            String pakageName = info.processName;
            taskinfo.setPakageName(pakageName);
            //获得应用进程的大小
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid})[0];
            long memory = memoryInfo.getTotalPrivateDirty()*1024;
            taskinfo.setAppSize(memory);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(pakageName, 0);
                //获得图片
                Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
                taskinfo.setIcon(drawable);
                //获得应用的名称
                String name = packageInfo.applicationInfo.loadLabel(pm).toString();
                taskinfo.setAppName(name);
                //判断是否是系统app还是用户app
                int flags = packageInfo.applicationInfo.flags;
                if ((flags& ApplicationInfo.FLAG_SYSTEM)== 0) {
                    //用户程序
                    taskinfo.setIsUserApp(true);
                }else{
                    //系统程序
                    taskinfo.setIsUserApp(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                /**
                 *  有三个系统的进程,没有图标和应用名字
                 */
                taskinfo.setAppName(pakageName);
                taskinfo.setIcon(context.getResources().getDrawable(R.drawable.defalt_imprort));
            }
//            Log.d(TAG, "---info"+taskinfo.getAppName());
            //添加到集合中
            taskInfos.add(taskinfo);
//            Log.e(TAG, "---集合长度为"+taskInfos.size());
        }

        return taskInfos;
    }
}
