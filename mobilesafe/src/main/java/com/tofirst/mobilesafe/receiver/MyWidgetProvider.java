package com.tofirst.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tofirst.mobilesafe.service.UpdateWidgetService;

/**
 * Created by Study on 2015/11/27.
 */
public class MyWidgetProvider extends AppWidgetProvider {
    private static final String TAG ="MyWidgetProvider" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "-------------------------onReceive");
        super.onReceive(context,intent);
    }

    /**
     * 当半个小时一更新的时候,开始组件清理
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "-------------------------onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent intent=new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.d(TAG, "-------------------------onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "-------------------------onDeleted");
        super.onDeleted(context, appWidgetIds);

    }

    /**
     * 当第一个桌面组件创建的时候开始服务
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "-------------------------onEnabled");
        super.onEnabled(context);
        Intent intent=new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    /**
     * 当最后一个桌面组件删除的时候,停止应用
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "-------------------------onDisabled");
        super.onDisabled(context);
        Intent intent=new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
    }
}
