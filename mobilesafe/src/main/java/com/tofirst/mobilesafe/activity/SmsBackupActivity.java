package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.bean.SmsInfo;
import com.tofirst.mobilesafe.utils.SmsUtils;
import java.util.List;



public class SmsBackupActivity extends Activity {

    private List<SmsInfo> smsInfoLists;
    private ProgressDialog pd;
    private  ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_backup);
        progressBar= (ProgressBar) findViewById(R.id.pb_sms);
    }



    public void smsBackupToSdcardRL(View view){
        /**
         *   备份短信应该放到工具类里边,方便复用
         */
        //首先要拿到短信,然后封装起来一个bean类中
        //然后备份在utils里边保存到list<Smsinfo>中
        //然后在吧这个信息保存到xml文件中,把文件放到sdcard中
        pd = new ProgressDialog(this);
        pd.setMessage("请稍等片刻,正在加载中...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        new Thread(){
            @Override
            public void run() {
                SmsUtils.SmsBackupToSdcard(SmsBackupActivity.this, new SmsUtils.SmsCallback(){

                    @Override
                    public void beforeCallBack(int count) {
                        pd.setMax(count);
                        progressBar.setMax(count);
                    }

                    @Override
                    public void onCallBack(int num) {
                        pd.setProgress(num);
                        progressBar.setProgress(num);
                    }
                });
                pd.dismiss();
            }
        }.start();

    }


}
