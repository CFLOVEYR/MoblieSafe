package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

import com.tofirst.mobilesafe.R;

public class TrafficStatisticActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_statistic);
        // 3g/2g网络上传的总流量，单位为byte
        TrafficStats.getMobileTxBytes();
        // 3g/2g网络下载的总流量,单位为byte
        TrafficStats.getMobileRxBytes();

        // 手机+wifi的下载总流量
        TrafficStats.getTotalRxBytes();
        // 手机+wifi的上传总流量
        TrafficStats.getTotalTxBytes();


//        计算某个应用程序的流量

        //某个用户程序的下载流量
//        TrafficStats.getUidRxBytes(uid);
        //某个应用程序的上传流量
//        TrafficStats.getUidTxBytes(uid)
    }
}
