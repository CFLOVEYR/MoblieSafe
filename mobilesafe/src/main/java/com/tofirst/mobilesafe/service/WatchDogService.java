package com.tofirst.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import com.tofirst.mobilesafe.activity.WatchDogActivity;
import com.tofirst.mobilesafe.db.dao.SoftLockDao;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService extends Service {
    private static final String TAG = "Test";
    private boolean flag;
    private ActivityManager am;
    private SoftLockDao dao;
    private IntentFilter filter;
    private CloseLockReceiver receiver;
    private String packageName_rececever;
    private Intent intent;
    private List<String> pakagenames;
    private LockDogContentObserver observer;
    private ContentResolver contentResolver;

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
        dao = new SoftLockDao(WatchDogService.this);
        pakagenames=new ArrayList<String>();
        intent = new Intent(WatchDogService.this, WatchDogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //注册广播
        receiver = new CloseLockReceiver();
        filter = new IntentFilter();
        filter.addAction("com.tofirst.mobilesafe.closelock");
        registerReceiver(receiver, filter);
        //注册一个广播观察者
        observer = new LockDogContentObserver(new Handler());
        Uri parse = Uri.parse("content://com.tofirst.mobilesafe.lockobserver");
        contentResolver = getContentResolver();
        contentResolver.registerContentObserver(parse, true, observer);

        /**
         *  看萌狗加速,让他在内存中查询,加快查询速度10倍
         */
        pakagenames = dao.queryAll();
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
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(50);
                    String packageName = runningTasks.get(0).topActivity.getPackageName();
//                    if (dao.query(packageName)) {
                    if(pakagenames.contains(packageName)){
                        Log.d(TAG, "------"+packageName_rececever);
                        if (packageName.equals(packageName_rececever)) {

                        }else {
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    } else {
//                        Log.d(TAG, "不在包内....");
                    }
                    //防止查询太快
                    SystemClock.sleep(100);
                }
            }
        }.start();
    }

    /**
     *  避免重复查询的bug
     */
class CloseLockReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
//        flag=false;
//        Log.d(TAG, "--------CloseLockReceiver");
        packageName_rececever = intent.getStringExtra("packagename");
//        Log.d(TAG, "----------------"+packageName_rececever);
    }
}

    /**
     *  监听数据变化的观察者
     */
    class LockDogContentObserver extends ContentObserver{

        public LockDogContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //跟新数据
            pakagenames = dao.queryAll();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        dao = null;
        am = null;
        //关闭广播
        unregisterReceiver(receiver);
        //关闭观察者
        contentResolver.unregisterContentObserver(observer);
    }
}
