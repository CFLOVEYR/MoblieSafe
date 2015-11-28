package com.tofirst.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import com.tofirst.mobilesafe.activity.WatchDogActivity;
import com.tofirst.mobilesafe.db.dao.SoftLockDao;

import java.util.List;

public class WatchDogService extends Service {
    private static final String TAG = "Test";
    private boolean flag;
    private ActivityManager am;
    private SoftLockDao dao;

    public WatchDogService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startWatchDog();
    }

    private void startWatchDog() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                flag = true;
                while (flag) {
                    am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    dao = new SoftLockDao(WatchDogService.this);
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(50);
                    String packageName = runningTasks.get(0).topActivity.getPackageName();
                    if (dao.query(packageName)) {
                        Log.d(TAG, "抓住小偷了....");
                        Intent intent = new Intent(WatchDogService.this, WatchDogActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "不在包内....");
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        dao = null;
        am = null;
    }
}
