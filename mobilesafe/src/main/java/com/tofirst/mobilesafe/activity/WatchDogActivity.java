package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tofirst.mobilesafe.R;

public class WatchDogActivity extends Activity implements View.OnClickListener {
    private static final String TAG ="Test" ;
    @ViewInject(R.id.bt_dog_num0)
    private Button bt_dog_num0;
    @ViewInject(R.id.bt_dog_num1)
    private Button bt_dog_num1;
    @ViewInject(R.id.bt_dog_num2)
    private Button bt_dog_num2;
    @ViewInject(R.id.bt_dog_num3)
    private Button bt_dog_num3;
    @ViewInject(R.id.bt_dog_num4)
    private Button bt_dog_num4;
    @ViewInject(R.id.bt_dog_num5)
    private Button bt_dog_num5;
    @ViewInject(R.id.bt_dog_num6)
    private Button bt_dog_num6;
    @ViewInject(R.id.bt_dog_num7)
    private Button bt_dog_num7;
    @ViewInject(R.id.bt_dog_num8)
    private Button bt_dog_num8;
    @ViewInject(R.id.bt_dog_num9)
    private Button bt_dog_num9;
    @ViewInject(R.id.bt_dog_clean)
    private Button bt_dog_clean;
    @ViewInject(R.id.bt_dog_delete)
    private Button bt_dog_delete;
    @ViewInject(R.id.bt_dog_yes)
    private Button bt_dog_yes;
    @ViewInject(R.id.et_dog)
    private EditText et_dog;
    private StringBuffer sb;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dog);
        ViewUtils.inject(this);
        packageName = getIntent().getStringExtra("packageName");
        sb = new StringBuffer();
        bt_dog_num0.setOnClickListener(this);
        bt_dog_num1.setOnClickListener(this);
        bt_dog_num2.setOnClickListener(this);
        bt_dog_num3.setOnClickListener(this);
        bt_dog_num4.setOnClickListener(this);
        bt_dog_num5.setOnClickListener(this);
        bt_dog_num6.setOnClickListener(this);
        bt_dog_num7.setOnClickListener(this);
        bt_dog_num8.setOnClickListener(this);
        bt_dog_num9.setOnClickListener(this);
        bt_dog_clean.setOnClickListener(this);
        bt_dog_yes.setOnClickListener(this);
        bt_dog_delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_dog_num0:
                sb.append(0);
                break;
            case R.id.bt_dog_num1:
                sb.append(1);
                break;
            case R.id.bt_dog_num2:
                sb.append(2);
                break;
            case R.id.bt_dog_num3:
                sb.append(3);
                break;
            case R.id.bt_dog_num4:
                sb.append(4);
                break;
            case R.id.bt_dog_num5:
                sb.append(5);
                break;
            case R.id.bt_dog_num6:
                sb.append(6);
                break;
            case R.id.bt_dog_num7:
                sb.append(7);
                break;
            case R.id.bt_dog_num8:
                sb.append(8);
                break;
            case R.id.bt_dog_num9:
                sb.append(9);
                break;
            case R.id.bt_dog_clean:
                sb.delete(0,sb.length()-1);
                Log.d(TAG, "---"+sb.toString());
                break;
            case R.id.bt_dog_delete:
                if (sb.length() >0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                break;
            case R.id.bt_dog_yes:
                if ("123".equals(et_dog.getText().toString())) {
                    finish();
                    Intent intent=new Intent();
                    intent.setAction("com.tofirst.mobilesafe.closelock");
                    intent.putExtra("packagename", packageName);
                    sendBroadcast(intent);
                }
                break;
            default:

                break;
        }
        if (sb != null) {
            et_dog.setText(sb.toString());
        }
    }

    /**
     *  返回的时候不让他一闪一闪的
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }
}
