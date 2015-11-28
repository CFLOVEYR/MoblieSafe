package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.service.CallSmsSafeService;
import com.tofirst.mobilesafe.service.CleanTaskService;
import com.tofirst.mobilesafe.utils.ServiceStatusUtils;
import com.tofirst.mobilesafe.view.SettingCleanTaskItemView;
import com.tofirst.mobilesafe.view.SettingUpdateItemView;

public class ProgressSettingActivity extends Activity {
    private SettingUpdateItemView siv;
    private SharedPreferences pref;
    private RelativeLayout rl_setting;
    private CheckBox cb_progress_setting;
    private TextView tv_cleantime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_setting);
        cb_progress_setting= (CheckBox) findViewById(R.id.cb_progress_setting);
        rl_setting= (RelativeLayout) findViewById(R.id.rl_progress_setting);
        siv = (SettingUpdateItemView) findViewById(R.id.rl_setting);
        TextView tv_cleantime = (TextView) findViewById(R.id.tv_cleantime);
        pref=getSharedPreferences("config",MODE_PRIVATE);
        // 查看进程是否被360，管家类恶意停止
        String serviceName = "com.tofirst.mobilesafe.service.CleanTaskService";
        boolean checkServiceRunning = ServiceStatusUtils.checkServiceRunning(
                ProgressSettingActivity.this, serviceName);
        if(checkServiceRunning) {
            pref.edit().putBoolean("Cleanflag", true).commit();
        } else {
            pref.edit().putBoolean("Cleanflag", false).commit();
        }
        // 初始化关屏清理属性
        siv.setTitle(siv.mtitle);
        if (pref.getBoolean("Cleanflag", false)) {
            siv.setChecked(true);
            siv.setText(siv.mDesc_on);
        } else {
            siv.setChecked(false);
            siv.setText(siv.mDesc_off);
        }

        siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断是否选到
                if (pref.getBoolean("Cleanflag", false)) {
                    pref.edit().putBoolean("Cleanflag", false).commit();
                    siv.setChecked(false);
                    stopService(new Intent(ProgressSettingActivity.this,
                            CleanTaskService.class));
                } else {
                    pref.edit().putBoolean("Cleanflag", true).commit();
                    siv.setChecked(true);
                    startService(new Intent(ProgressSettingActivity.this,
                            CleanTaskService.class));
                }
            }
        });
        //初始化checkbox
        if (pref.getBoolean("showsystemFlag",true)) {
            cb_progress_setting.setChecked(true);
        }else {
            cb_progress_setting.setChecked(false);
        }
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_progress_setting.isChecked()) {
                    cb_progress_setting.setChecked(false);
                    pref.edit().putBoolean("showsystemFlag",false).commit();
                }else{
                    cb_progress_setting.setChecked(true);
                    pref.edit().putBoolean("showsystemFlag",true).commit();
                }
            }
        });


        //初始化checkbox
     tv_cleantime.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             showSingleDialog();
         }
     });
    }

    private void showSingleDialog() {
        final long[] time={5000,6000*60*30,6000*60*60};
        String[] desc={"5秒","半小时","一小时"};
        AlertDialog.Builder builder=new AlertDialog.Builder(ProgressSettingActivity.this);
        builder.setMessage("需要在设置中心设置开启清理功能才可以");
        builder.setSingleChoiceItems(desc, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pref.edit().putLong("count",time[i]).commit();

            }
        });
        builder.show();
    }


}
