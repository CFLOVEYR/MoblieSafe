package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.service.RoketService;

public class RoketActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roket);
    }
    /**
     * 启动火箭
     *
     * @param view
     */
    public void start(View view) {
        startService(new Intent(RoketActivity.this, RoketService.class));
    }

    /**
     * 停止火箭
     *
     * @param view
     */
    public void stop(View view) {
        stopService(new Intent(RoketActivity.this, RoketService.class));
    }
}
