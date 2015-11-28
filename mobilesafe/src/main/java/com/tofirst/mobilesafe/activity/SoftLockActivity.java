package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.fragment.LockFragment;
import com.tofirst.mobilesafe.fragment.UnLockFragment;

public class SoftLockActivity extends FragmentActivity implements View.OnClickListener {
    @ViewInject(R.id.bt_softlock)
    private Button bt_softlock; //未加锁的按钮
    @ViewInject(R.id.bt_softunlock)
    private Button bt_softunlock; //已加锁的按钮
    @ViewInject(R.id.fl_softlock)
    private FrameLayout fl_softlock; //Fragment的布局
    private android.support.v4.app.FragmentManager fragmentManager;
    private LockFragment lock;
    private UnLockFragment unlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_lock);
        ViewUtils.inject(this);
        bt_softlock.setOnClickListener(this);
        bt_softunlock.setOnClickListener(this);
        //碎片FragMent的利用
        /**
         * getSupportFragmentManager 兼容4.0以下的版本
         * getFragmentManager() 不兼容4.0以下的版本
         */
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        lock = new LockFragment();
        unlock = new UnLockFragment();
        //首先显示的是未加锁的Fragment
        transaction.replace(R.id.fl_softlock, unlock).commit();
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (view.getId()){
        case R.id.bt_softunlock:
            bt_softunlock.setBackgroundResource(R.drawable.tab_left_pressed);
            bt_softlock.setBackgroundResource(R.drawable.tab_right_default);
            ft.replace(R.id.fl_softlock,unlock).commit();
            break;
        case R.id.bt_softlock:
            bt_softlock.setBackgroundResource(R.drawable.tab_right_pressed);
            bt_softunlock.setBackgroundResource(R.drawable.tab_left_default);
            ft.replace(R.id.fl_softlock,lock).commit();
            break;
        default:
            break;

    }
    }
}
