package com.tofirst.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class KillAllProcessReceiver extends BroadcastReceiver {
    public KillAllProcessReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for ( ActivityManager.RunningAppProcessInfo info:runningAppProcesses) {
                am.killBackgroundProcesses(info.processName);
        }
    }
}
