package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.tofirst.mobilesafe.R;

public class RoketBackgroundActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roket_yun);
        ImageView imageView1= (ImageView) findViewById(R.id.iv_roket_yun_bottom);
        ImageView imageView2= (ImageView) findViewById(R.id.iv_roket_yun_top);
        //设置渐变动画
        AlphaAnimation animation=new AlphaAnimation(0,1);
        animation.setDuration(1000);
        imageView1.startAnimation(animation);
        imageView2.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }
}
