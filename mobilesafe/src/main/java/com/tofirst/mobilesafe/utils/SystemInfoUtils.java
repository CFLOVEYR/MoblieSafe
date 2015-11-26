package com.tofirst.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *  获取系统信息的类
 */
public class SystemInfoUtils {

    /**
     *     获取正在运行中进程的总个数：
     */
    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }
    /**
     *   获得可用内存的方法
     *
     */
    public  static long getAvailRam(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;//byte 为单位的long类型的可用内存大小}
    }

    /**
     *   获得可用内存的方法
     *   但是在4.0以后才能用,所以用下边的方法兼容性好
     */
    public  static long getTotalRam_new(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;//byte 为单位的long类型的可用内存大小}
    }

    /**
     *     获得总共内存的方法
     */
    public static long getTotalRam(Context context) {
        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));
            // MemTotal: 516452 kB * 1024 = byte
            String line = bufferedReader.readLine();
            StringBuffer buffer = new StringBuffer();
            for (char c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    buffer.append(c);
                }
            }
            return (Integer.parseInt(buffer.toString()) * 1024l);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}

