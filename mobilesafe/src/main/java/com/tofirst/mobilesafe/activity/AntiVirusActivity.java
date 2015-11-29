package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.db.dao.AniVirusDao;
import com.tofirst.mobilesafe.utils.Md5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntiVirusActivity extends Activity {

    private static final String TAG = "Test";
    private ImageView iv_scan;
    private ProgressBar progressBar1;
    private PackageManager pm;

    private TextView tv_virus_status;
    private LinearLayout ll_virus_show;
    public static final int SCANING = 0;
    public static final int SCAN_FINISH = 1;
    private ScrollView sl_virus;
    private List<ScanInfo> list_virus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCANING://扫描文件的操作
                    TextView textView = new TextView(AntiVirusActivity.this);
                    ScanInfo info = (ScanInfo) msg.obj;
                    tv_virus_status.setText("正在扫描" + info.appName);
                    if (info.isVirus) {
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒" + info.appName);
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全" + info.appName);
                    }
                    ll_virus_show.addView(textView);
                    break;
                case SCAN_FINISH://完成扫描后的操作
                    iv_scan.clearAnimation();//停止扫描动画
                    tv_virus_status.setText("扫描完毕");
                    if (list_virus.size()>0) {
                        //卸载程序
                        AlertDialog.Builder builder = new AlertDialog.Builder(AntiVirusActivity.this);
                        builder.setTitle("发现病毒");
                        builder.setMessage("发现病毒" + list_virus.size() + "个,请及时处理,以免危害你的人身健康");
                        builder.setPositiveButton("立即处理", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (ScanInfo info1 : list_virus) {
                                    startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + info1.pakagename)));
                                }
                            }
                        });
                        builder.setNegativeButton("下次再说", null);
                        builder.show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        list_virus = new ArrayList<ScanInfo>();
        //查询没卸载干净的的签名信息
        pm = getPackageManager();
        new Thread() {
            @Override
            public void run() {
                ScanInfo scanInfo = new ScanInfo();
                int progress = 0;
                Random random = new Random();
                SystemClock.sleep(2000);
                List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES + PackageManager.GET_SIGNATURES);
                for (PackageInfo info : packages) {
                    String processName = info.packageName;
                    scanInfo.pakagename = processName;
                    String appname = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.appName = appname;
                    String signature = info.signatures[0].toCharsString();
                    //设置进度条的最大值
                    progressBar1.setMax(packages.size());
                    //将签名转化为MD5
                    String md5 = Md5Utils.psdMD5(signature);
                    String result = AniVirusDao.getVirusMd5String(md5);
                    if (result == null) {
                        scanInfo.isVirus = false;
                        scanInfo.desc = null;
                    } else {
                        scanInfo.isVirus = true;
                        scanInfo.desc = result;
                        list_virus.add(scanInfo);
                    }
                    //进度条发生变化
                    progress++;
                    progressBar1.setProgress(progress);
                    //模拟扫描一个程序的时间
                    try {
                        Thread.sleep(50 + random.nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.what = SCANING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
                //扫描完成
                Message message = Message.obtain();
                message.what = SCAN_FINISH;
                handler.sendMessage(message);
            }
        }.start();


    }

    private void initUI() {
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        tv_virus_status = (TextView) findViewById(R.id.tv_virus_status);
        ll_virus_show = (LinearLayout) findViewById(R.id.ll_virus_show);
        sl_virus = (ScrollView) findViewById(R.id.sl_virus);
        /**
         *  设置滚动条自动下滑的功能
         */
        sl_virus.fullScroll(ScrollView.FOCUS_DOWN);
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        //不停的旋转
        ra.setRepeatCount(RotateAnimation.INFINITE);
        iv_scan.startAnimation(ra);
        //进度条的不断变化
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        //欺骗不懂Android的老百姓
        tv_virus_status.setText("初始化八核引擎中...");
    }

    class ScanInfo {
        boolean isVirus;
        String desc;
        String pakagename;
        String appName;
    }

}
