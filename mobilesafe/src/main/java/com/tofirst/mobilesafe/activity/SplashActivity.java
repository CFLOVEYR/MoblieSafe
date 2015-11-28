package com.tofirst.mobilesafe.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.service.AddressService;
import com.tofirst.mobilesafe.service.UpdateWidgetService;
import com.tofirst.mobilesafe.utils.CreateShortCutUtils;
import com.tofirst.mobilesafe.utils.GetStreamStringUtils;
import com.tofirst.mobilesafe.utils.SharedPreferencesUtils;

public class SplashActivity extends Activity {
    // 记录时间
    final long startTime = System.currentTimeMillis();
    // 状态码
    protected static final int UPDATE_VERSION_CODE = 1;
    protected static final int URL_ERROR_CODE = 2;
    protected static final int NETL_ERROR_CODE = 3;
    protected static final int JSON_ERROR_CODE = 4;
    protected static final int ENTER_HOME_CODE = 5;
    protected static final int UPDATE_LOADING = 6;
    private TextView tv_vesion;
    private TextView tv_progress;
    private RelativeLayout rl_splash;
    // 服务器的信息
    private String mVersionName;
    private int mVersionCode;
    private String mDesc;
    private String mDownLoadURL;
    // 建立handler
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION_CODE:// 弹出更新对话框
                    showUpdateDialog();
                    break;
                case URL_ERROR_CODE:// url错误
                    Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT)
                            .show();
                    enterHome();
                    break;
                case NETL_ERROR_CODE:// 网络异常
                    Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT)
                            .show();
                    enterHome();
                    break;
                case JSON_ERROR_CODE:// 数据解析异常
                    Toast.makeText(SplashActivity.this, "数据解析异常",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case ENTER_HOME_CODE:// 跳入主页面
                    enterHome();
                    break;
                case UPDATE_LOADING:// 下载进度
                    tv_progress.setVisibility(View.VISIBLE);
                    tv_progress.setText((String) msg.obj);
                    break;
                default:
                    enterHome();
                    break;
            }
        }

        ;
    };
    private SharedPreferences pref;
    private InputStream in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = getSharedPreferences("config", MODE_PRIVATE);
        // 初始化组件
        InitView();
        //创建快捷方式
        boolean installedShortCut = pref.getBoolean("InstalledShortCut", false);
        if (installedShortCut == false) {
            //创建快捷图标
            CreateShortCutUtils.CreateShortcut(this);
        }

        // 初始化服务

        // 渐变的动画
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1f);
        animation.setDuration(1000);// 设置两秒的渐变过程
        rl_splash.startAnimation(animation);

        // 复制数据库到files目录下
        copyDb("address.db");
        // 更新页面的版本内容
        tv_vesion.setText(getVersionName());
        // 检查是否有新的版本
        if (pref.getBoolean("Updateflag", true)) {
            checkVersion();
        } else {
            // 用发送延时消息来处理进入主界面的内容
            handler.sendEmptyMessageDelayed(ENTER_HOME_CODE, 1000);
        }
    }

    /**
     * 初始化服务
     */
    private void initService() {
        boolean addressflag = SharedPreferencesUtils.getBooleanData(SplashActivity.this,"Addressflag",true);
        if (addressflag) {
            startService(new Intent(SplashActivity.this, AddressService.class));
        }
    }




    /**
     * 调到主页面的方法
     */
    protected void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeAcivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 版本更新的对话框的方法
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashActivity.this);
        builder.setTitle("版本" + mVersionName);
        builder.setMessage(mDesc);
        builder.setPositiveButton("立即更新", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        // 用户取消对话框的监听事件
        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    protected void downLoadApk() {
        // 利用第三方包Utils，来处理下载的问题
        HttpUtils http = new HttpUtils();
        // 判断Sdcard是否正常
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String target = Environment.getExternalStorageDirectory()
                    + "/mobilesafe.apk";
            http.download(mDownLoadURL, target, new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    // TODO Auto-generated method stub
                    super.onLoading(total, current, isUploading);
                    Message msg = handler.obtainMessage();
                    msg.obj = "下载进度" + current * 100 / total + "%";
                    msg.what = UPDATE_LOADING;
                    handler.sendMessage(msg);
                    System.out.println("下载进度" + current * 100 / total + "%");
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    // TODO Auto-generated method stub
                    System.out.println("下载失败");
                }

                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    System.out.println("下载成功");
                    // 跳转到系统的下载页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(arg0.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);// 跳转后的监听事件
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "Sdcard异常或者不存在",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 检查是否有新版本的内容的方法
     */
    private void checkVersion() {
        // 模拟器访问本地需要把local换成10.0.2.2才可以访问
        final String path = "http://10.0.2.2:8080/mobilesafe/update.json";
        // final String path="http://localhost:8080/mobilesafe/update.json";
        final Message msg = handler.obtainMessage();
        new Thread() {
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        String str = GetStreamStringUtils.getStringStream(in);
                        // System.out.println("--"+str);
                        JSONObject json = new JSONObject(str);
                        mVersionName = json.getString("versionName");
                        mVersionCode = json.getInt("versionCode");
                        mDesc = json.getString("description");
                        mDownLoadURL = json.getString("downloadURL");
                        // System.out.println("json解析:"+mDesc);
                        // 判断是否有更新，如果服务器的版本号大于当前的版本号，就弹出对话框
                        if (mVersionCode > getVersionCode()) {
                            msg.what = UPDATE_VERSION_CODE;
                        } else {
                            // 限制时间的方法
                            limitTime();
                        }
                    } else {
                        // 如果响应码不为200
                        msg.what = URL_ERROR_CODE;
                    }

                } catch (MalformedURLException e) {
                    // url异常
                    msg.what = URL_ERROR_CODE;
                    e.printStackTrace();
                } catch (IOException e) {
                    // 网络异常
                    msg.what = NETL_ERROR_CODE;
                    e.printStackTrace();
                } catch (JSONException e) {
                    // 解析数据错误
                    msg.what = JSON_ERROR_CODE;
                    e.printStackTrace();
                } finally {
                    // 限制时间的方法
                    limitTime();
                    // 发送消息
                    handler.sendMessage(msg);
                    // 关闭网络连接
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

        }.start();

    }

    /**
     * 限制时间的方法，防止进入主界面太快
     */
    protected void limitTime() {
        // 防止解析速度太快
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if (time < 2000) {
            try {
                Thread.sleep(2000 - time);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // 初始化组件的方法
    private void InitView() {
        tv_vesion = (TextView) findViewById(R.id.tv_version);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
    }

    // 获取版本名字的方法
    private String getVersionName() {
        String versionName = null;
        int versionCode = 0;
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
            // System.out.println("--------------"+versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }

    // 获取版本号的方法
    private int getVersionCode() {
        int versionCode = 0;
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            // System.out.println("--------------"+versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionCode;
    }

    // 返回的事件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            enterHome();
        }
    }

    public void copyDb(String dbname) {
        File destfile = new File(getFilesDir() + "/" + dbname);
        if (destfile.exists()) {
            return;
        }
        System.out.println("---" + getFilesDir());
        FileOutputStream out = null;
        try {
            in = getAssets().open("address.db");
            out = new FileOutputStream(destfile);
            int len = 0;
            byte[] data = new byte[1024];
            while ((len = in.read(data)) != -1) {
                out.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
