package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.List;

public class CleanCacheActivity extends Activity {

    private static final int SCANING =0;
    private static final int SHOW_SCAN_INFO =1;
    private static final int SCAN_FINISH =2;
    private ProgressBar progressBar1;
    private TextView tv_status;
    private LinearLayout ll_container;
    private PackageManager pm;
    private CacheInfo cacheInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        initUI();
        new Thread(){
            public void run() {
                pm = getPackageManager();
                List<PackageInfo> packInfos = pm
                        .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                progressBar1.setMax(packInfos.size());
                int progress = 0;
                for (PackageInfo packInfo : packInfos) {
                    String packName = packInfo.packageName;
                    try {
                        //映射得到getPackageSizeInfo的方法,来扫描各个程序的缓存
                        Method method = PackageManager.class.getMethod(
                                "getPackageSizeInfo", String.class,
                                IPackageStatsObserver.class);
                        method.invoke(pm,packName,new MyObserver());
                        //发送正在扫描中的信息..
                        Message msg = Message.obtain();
                        msg.obj= packInfo.applicationInfo.loadLabel(pm).toString();
                        msg.what = SCANING;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    progress ++;
                    progressBar1.setProgress(progress);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //扫描完成发消息
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                handler.sendMessage(msg);
            };
        }.start();
    }
    class CacheInfo{
        Drawable icon;
        String name;
        long size;
        String pakagename;
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANING:
                    String name = (String) msg.obj;
                    tv_status.setText("正在扫描："+name);
                    break;
                case SHOW_SCAN_INFO:
                    cacheInfo = (CacheInfo) msg.obj;
                    View view = View.inflate(getApplicationContext(), R.layout.list_app_cache, null);
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
                    iv_icon.setImageDrawable(cacheInfo.icon);
                    tv_name.setText(cacheInfo.name);
                    tv_cache_size.setText("缓存大小:"+Formatter.formatFileSize(getApplicationContext(), cacheInfo.size));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //跳转到系统页面的对话框
                            showDialogToSystem();
                        }
                    });
                    ll_container.addView(view);
                    break;
                case SCAN_FINISH:
                    tv_status.setText("扫描完成");
                    break;
            }
        };
    };

    private void showDialogToSystem() {
        AlertDialog.Builder builder=new AlertDialog.Builder(CleanCacheActivity.this);
        builder.setTitle("缓存清理");
        builder.setMessage("将跳转到系统页面");
        builder.setPositiveButton("立即清理", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //启动到某个系统应用页面
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory(Intent.CATEGORY_DEFAULT);//有无没影响
                intent.setData(Uri.parse("package:" + cacheInfo.pakagename));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("下次再说",null);
        builder.show();
    }

    private void initUI() {
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        tv_status = (TextView) findViewById(R.id.tv_status);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
    }

    //点击事件-立即清除
    public void cleanAll(View view){
        Method [] methodes = PackageManager.class.getMethods();
        for(Method method : methodes){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(pm, Integer.MAX_VALUE,new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded)
                                throws RemoteException {
                            System.out.println("succeeded="+succeeded);
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return;
            }
        }
    }

    //获取缓存信息的调用方法
    private class MyObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0) {
                try {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);
                    cacheInfo.name = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();
                    cacheInfo.pakagename = pm.getApplicationInfo(pStats.packageName, 0).packageName;
                    cacheInfo.size = cacheSize;
                    //发送进度条发生变化的信息,通知改变UI
                    Message msg = Message.obtain();
                    msg.what = SHOW_SCAN_INFO;
                    msg.obj = cacheInfo;
                    handler.sendMessage(msg);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
