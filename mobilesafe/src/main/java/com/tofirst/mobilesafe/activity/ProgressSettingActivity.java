package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.view.SettingUpdateItemView;

public class ProgressSettingActivity extends Activity {
    private SettingUpdateItemView siv;
    private SharedPreferences pref;
    private RelativeLayout rl_setting;
    private CheckBox cb_progress_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_setting);
        cb_progress_setting= (CheckBox) findViewById(R.id.cb_progress_setting);
        rl_setting= (RelativeLayout) findViewById(R.id.rl_progress_setting);
        siv = (SettingUpdateItemView) findViewById(R.id.rl_setting);
        pref=getSharedPreferences("config",MODE_PRIVATE);
        // 初始化自动更新属性
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
                } else {
                    pref.edit().putBoolean("Cleanflag", true).commit();
                    siv.setChecked(true);
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
    }


}
