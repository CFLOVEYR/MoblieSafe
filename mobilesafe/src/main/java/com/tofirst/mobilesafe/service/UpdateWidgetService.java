package com.tofirst.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.engine.ProcessInfos;
import com.tofirst.mobilesafe.receiver.MyWidgetProvider;
import com.tofirst.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

    private static final String TAG = "Test";
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager awm;
    private InnearScreenBroadCast innearScreenBroadCast;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateTimerWidget();
        innearScreenBroadCast = new InnearScreenBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(innearScreenBroadCast, filter);

    }

    //实现监听开屏关屏广播只能代码注册
    class InnearScreenBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
                Log.d(TAG, "开启屏幕了");
                //计时跟新桌面组件
                updateTimerWidget();
            } else if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
                Log.d(TAG, "关闭屏幕了");
                //停止计时器
                stopTimer();
            }
        }
    }

    private void updateTimerWidget() {
        //创建一个计时器
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "---开始干活了");
                //跟新Widget的逻辑
                //获得Widget的管理器
                awm = AppWidgetManager.getInstance(UpdateWidgetService.this);
                ComponentName componentName = new ComponentName(UpdateWidgetService.this, MyWidgetProvider.class);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                views.setTextViewText(R.id.process_count, "正在运行的软件" + ProcessInfos.getPogressInfos(UpdateWidgetService.this).size());
                views.setTextViewText(R.id.process_memory, "可用内存为" + Formatter.formatFileSize(UpdateWidgetService.this, SystemInfoUtils.getAvailRam(UpdateWidgetService.this)));
                //延期意图发送广播
                Intent intent = new Intent();
                intent.setAction("com.tofirst.killallprocess");
                /**
                 *  最后一个参数是表示,当点击两次的时候,响应最后一次点击事件
                 */
                PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                /**
                 *   为一键清理Button做出响应事件
                 */
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                awm.updateAppWidget(componentName, views);
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //服务关闭的时候关闭计时器
        stopTimer();
        unregisterReceiver(innearScreenBroadCast);
    }

    private void stopTimer() {
        timer.cancel();
        timerTask.cancel();
        timer = null;
        timerTask = null;
    }
}
