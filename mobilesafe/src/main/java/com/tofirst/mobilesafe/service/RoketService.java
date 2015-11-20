package com.tofirst.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.activity.RoketBackgroundActivity;

public class RoketService extends Service {
    private WindowManager mWM;
    private View view;
    private WindowManager.LayoutParams params;
    private int winwidth;
    private int winheight;
    private int length;

    public RoketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
public Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 0) {
            params.y=(Integer)msg.obj;
            mWM.updateViewLayout(view, params);
        }
    }
};

    @Override
    public void onCreate() {
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        winheight = mWM.getDefaultDisplay().getHeight();
        winwidth = mWM.getDefaultDisplay().getWidth();
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                /**
                 * FLAG_NOT_TOUCHABLE 表示不能被触摸
                 * 需要注释这句话，不能被触摸。
                 */
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        /**
         * 将重心位置移动到左上方（0,0），而不是默认的位置
         */
        params.gravity = Gravity.TOP + Gravity.LEFT;
        params.format = PixelFormat.TRANSLUCENT;
        /**
         * 需要把他的权限调，TYPE_TOAST只是一个短暂的，需要设置为TYPE_PHONE类型的
         *    需要添加权限
         *     <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
         */
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        view = View.inflate(this, R.layout.layout_roket_model, null);
        //添加帧动画
        ImageView rocketImage = (ImageView)view.findViewById(R.id.imageView);
        rocketImage.setBackgroundResource(R.drawable.rocket_thrust);
        AnimationDrawable rocketAnimation= (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation.start();
        mWM.addView(view, params);
        /**
         *  触发拖动事件
         */
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获得移动后的坐标
                        int endX = (int) motionEvent.getRawX();
                        int endY = (int) motionEvent.getRawY();
                        params.x = endX;
                        params.y = endY;
                        chekparms();
                        //更新内容,而不是添加内容了
                        mWM.updateViewLayout(view, params);

                        break;
                    case MotionEvent.ACTION_UP:
                        //判断发送火箭的逻辑
                        if (params.y > winheight-view.getHeight()-50 && params.x > 160 && params.x < 320) {
                            sentRoket();
                        }
                        //背景
                        //在服务中启动Activity，需要申请一个任务栈来放Activity
                        Intent intent=new Intent(RoketService.this, RoketBackgroundActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }

                return false;//设置为false，表示处理完毕让下边的继续处理
            }
        });
    }

    private void chekparms() {
        if (params.x<0){
            params.x=0;
        }
        if (params.y<0){
            params.y=0;
        }
        if (params.x>winwidth-view.getWidth()){
            params.x=winwidth-view.getWidth();
        }
        if (params.y>winheight-view.getHeight()){
            params.y=winheight-view.getHeight();
        }
    }

    private void sentRoket() {
        params.x=winwidth/2-view.getWidth()/2;
        mWM.updateViewLayout(view,params);
        length = 735;
        new Thread() {
            @Override
            public void run() {

                for (int i = 1; i <=3; i++) {
                    int y = length - length/3 * i;
                    Message msg=handler.obtainMessage();
                    msg.obj=y;
                    msg.what=0;
                    if (y <0) {
                        break;
                    }
                    handler.sendMessage(msg);
                    SystemClock.sleep(100);
                }
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        if (mWM != null && view != null) {
            mWM.removeViewImmediate(view);
            view = null;
            mWM = null;
        }

    }
}
