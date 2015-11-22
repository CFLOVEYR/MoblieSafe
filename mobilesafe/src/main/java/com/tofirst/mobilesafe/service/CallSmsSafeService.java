package com.tofirst.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.tofirst.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;
import java.util.Observer;

/**
 * 版权所有：版权归北京Tofirst公司所有
 *
 * @author Mr.Y    E-mail: CFLOVEYR@163.com
 *         <p/>
 *         <p/>
 *         此类的作用： 用来拦截黑名单的中的电话和短信
 *         <p/>
 *         <p/>
 *         修改历史：
 */
public class CallSmsSafeService extends Service {

    private static final String TAG ="Test" ;
    private SmsResever resever;
    private MyLisener lisener;
    private TelephonyManager tm;
    private ContentResolver resolver;
    private Uri url;
    private MyContentObserver observer;

    public CallSmsSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        /**
         * 拦截电话的的逻辑
         */
        /**
         * 1 首先拿到TelephonyManager
         * 2 TelephonyManager监听接电话的状态
         * 3.通过状态来进行自己的逻辑
         */
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        lisener = new MyLisener();
        tm.listen(lisener, PhoneStateListener.LISTEN_CALL_STATE);// 监听打电话的状态
        /**
         * 拦截短信的逻辑
         */
        //首先要有一个广播来接收来短信的广播，自定义一个
        resever = new SmsResever();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //设置是第一个接收短信
        filter.setPriority(Integer.MAX_VALUE);
        //手动注册广播
        registerReceiver(resever, filter);

    }
    class MyLisener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    BlackNumberDao dao = new BlackNumberDao(CallSmsSafeService.this);
                    String mode = dao.query(incomingNumber);
                    if (mode != null) {
                        if (mode.equals("1")||mode.equals("3")) {
                            //注册一个内容观察者
                            resolver = getContentResolver();
                            url = Uri.parse("content://call_log/calls");
                            observer = new MyContentObserver(new Handler(),
                                    incomingNumber);
                            resolver.registerContentObserver(url, true, observer);
                            Log.i(TAG, "开始删除通话记录，和拦截电话");
                            //拦截电话
                            enCall();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机的状态

                    break;
                case TelephonyManager.CALL_STATE_IDLE:// 电话是闲置状态

                    break;

                default:
                    break;
            }
        }
    }
    //内容观察者
    private class MyContentObserver extends ContentObserver {
        private String incomingNumber;
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //删除呼叫记录
            deleteBlackInfo(incomingNumber);
        }
    }
    //删除通话记录
    private void deleteBlackInfo(String num) {
        resolver.delete(url, "number=?", new String[]{num});
    }
    //挂掉电话
    private void enCall() {

        try {
            //得到ServiceManager的字节码
            Class clazz  = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            //得到字节码的方法
            Method method =clazz.getDeclaredMethod("getService", String.class);
            //调用方法得到远程服务代理类
            IBinder binder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            Log.i(TAG, "-------------------enCall"+binder);
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            boolean flag = telephony.endCall();//挂断电话
            Log.i(TAG, "----------------------"+flag);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class SmsResever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "短信来了");
            /**
             * 获取短信内容的方法
             */
            // 首先获取数据
            Bundle bundle = intent.getExtras();
            // 然后获取数组
            Object[] objects = (Object[]) bundle.get("pdus");
            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String phonenumber = sms.getOriginatingAddress();// 获得手机号码
                Log.i(TAG, "这个号码----------"+phonenumber);
                if (phonenumber != null) {
                    BlackNumberDao dao = new BlackNumberDao(CallSmsSafeService.this);
                    boolean queryExist = dao.queryExist(phonenumber);
                    String mode = dao.query(phonenumber);
                    //判断这个号码是否在黑名单中
                    if(queryExist){
                        if(mode.equals("2")||mode.equals("3")){
                            Log.i(TAG, "这个号码存在，开始拦截");
                            //拦截短信
                            abortBroadcast();// 拦截短信，不让小偷知道
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //关闭广播
        unregisterReceiver(resever);
        //关闭监听,增减程序的可控性
        tm.listen(lisener,PhoneStateListener.LISTEN_NONE);
        //关闭监听
        resolver.unregisterContentObserver(observer);
    }
}
