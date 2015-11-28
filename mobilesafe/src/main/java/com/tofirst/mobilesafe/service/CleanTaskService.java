package com.tofirst.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.tofirst.mobilesafe.bean.TaskInfo;
import com.tofirst.mobilesafe.engine.ProcessInfos;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CleanTaskService extends Service {

    private static final String TAG ="Test" ;
    private ScreenResever resever;

    public CleanTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        long count=getSharedPreferences("config",MODE_PRIVATE).getLong("count",2000);
        Timer timer = new Timer();
       TimerTask task= new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "计时器开始干活去...");
            }
        };
        timer.schedule(task,2000,count);
        resever = new ScreenResever();
        IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(resever,filter);
    }
    class  ScreenResever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<TaskInfo> pogressInfos = ProcessInfos.getPogressInfos(getApplicationContext());
            for(TaskInfo info:pogressInfos){
                if (!info.getPakageName().equals(getPackageName())) {
                    am.killBackgroundProcesses(info.getPakageName());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        unregisterReceiver(resever);
    }
}
