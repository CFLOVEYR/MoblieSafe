package com.tofirst.mobilesafe.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.bean.ProgressInfo;
import com.tofirst.mobilesafe.engine.ProcessInfos;
import com.tofirst.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

public class ProgressMannagerActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Test";
    @ViewInject(R.id.list_view_process)
    private ListView list_view_process;
    //是否是用户进程
    @ViewInject(R.id.tv_process)
    private TextView tv_process_apptype;
    //总共的进程
    @ViewInject(R.id.tv_progress_runningnum)
    private TextView tv_progress_runningnum;
    //可用/总共的文本
    @ViewInject(R.id.tv_process_totalmemory)
    private TextView tv_process_totalmemory;
    //缓冲载入内容
    @ViewInject(R.id.ll_pb_progress)
    private LinearLayout ll_pb_progress;
    //全选,反选,设置,一键清理
    @ViewInject(R.id.bt_progress_enall)
    private Button bt_progress_enall;
    @ViewInject(R.id.bt_progress_deall)
    private Button bt_progress_deall;
    @ViewInject(R.id.bt_progress_killall)
    private Button bt_progress_killall;
    @ViewInject(R.id.bt_progress_entersetting)
    private Button bt_progress_entersetting;

    private List<ProgressInfo> progress_list;//存储进程信息的集合
    private List<ProgressInfo> progress_list_user;//存储进程信息的集合
    private List<ProgressInfo> progress_list_system;//存储进程信息的集合
    private MyProgessAdapter adapter;//添加的适配器
    private int processCount;
    private String availRam;
    private long availRam_long;
    private PackageManager packageManager;
    private ActivityManager am;
    private List<ProgressInfo> list_delete;
    private int afterProgressNum;
    private int num;
    private String totalRam;
    private long beforeravaiRam;
    private SharedPreferences pre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pb_progress.setVisibility(View.INVISIBLE);
            if (adapter == null) {
                adapter = new MyProgessAdapter();
                list_view_process.setAdapter(adapter);
            } else {
                list_view_process.setAdapter(adapter);
            }

        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                ll_pb_progress.setVisibility(View.VISIBLE);
                progress_list_user = new ArrayList<ProgressInfo>();
                progress_list_system = new ArrayList<ProgressInfo>();
                progress_list = ProcessInfos.getPogressInfos(ProgressMannagerActivity.this);
                for (ProgressInfo info : progress_list) {
                    if (info.isUserApp()) {
                        progress_list_user.add(info);
                    } else {
                        if (pre.getBoolean("showsystemFlag",true)) {
                            progress_list_system.add(info);
                        }
                    }
                }
                Message msg=handler.obtainMessage();
                msg.what=0;
                handler.sendMessageDelayed(msg,2000);
            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_progress);
        ViewUtils.inject(this);
        pre = getSharedPreferences("config", MODE_PRIVATE);
        bt_progress_enall.setOnClickListener(this);
        bt_progress_deall.setOnClickListener(this);
        bt_progress_killall.setOnClickListener(this);
        bt_progress_entersetting.setOnClickListener(this);
        //总共的运行程序数目
        processCount = SystemInfoUtils.getRunningProcessCount(this);
        tv_progress_runningnum.setText("运行中的进程: " + processCount + "个");
        //获得可用内存和总共内存的显示
        availRam_long = SystemInfoUtils.getAvailRam(this);
        availRam = Formatter.formatFileSize(this, availRam_long);
        totalRam = Formatter.formatFileSize(this, SystemInfoUtils.getTotalRam(ProgressMannagerActivity.this));
        tv_process_totalmemory.setText("剩余/总内存: " + availRam + "/" + totalRam);
        /**
         * listview 的滚动事件
         */
        list_view_process.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int first, int i1, int i2) {
                /**
                 * 可能还没取完呢，需要判断一下都不为空，表示取完了
                 */
                if (progress_list_system != null && progress_list_user != null) {
                    if (first >= progress_list_user.size()) {
                        tv_process_apptype.setText("系统进程：(" + progress_list_system.size() + ")个");
                    } else {
                        tv_process_apptype.setText("用户进程：(" + progress_list_user.size() + ")个");
                    }
                }
            }
        });
        /**
         * Listview的点击事件
         */

        list_view_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /**
                 * 这可以得到可以转化为item的javabean对象,方便下边的利用
                 */
                Object obj = list_view_process.getItemAtPosition(position);
                ProgressInfo info = (ProgressInfo) obj;
                if (obj != null) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cb_progress_isuserapp);
                    if (info.getPakageName().equals(getPackageName())) {
                        cb.setChecked(false);
                    }else{
                        if (cb.isChecked()) {
                            cb.setChecked(false);
                            info.setIsChecked(false);
                        } else {
                            cb.setChecked(true);
                            info.setIsChecked(true);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        switch (view.getId()){
            case R.id.bt_progress_enall://全选
                for (ProgressInfo info:progress_list_user) {
                    if (!info.getPakageName().equals(getPackageName())) {
                        if (!info.isChecked()) {
                            info.setIsChecked(true);
                        }
                    }

                }
                for (ProgressInfo info:progress_list_system) {
                    if (!info.getPakageName().equals(getPackageName())) {
                        if (!info.isChecked()) {
                            info.setIsChecked(true);
                        }
                    }
                }
                handler.sendEmptyMessage(0);
                break;
            case R.id.bt_progress_deall://反选
                for (ProgressInfo info:progress_list_user) {
                    if (!info.getPakageName().equals(getPackageName())) {
                        if (info.isChecked()) {
                            info.setIsChecked(false);
                        }else {
                            info.setIsChecked(true);
                        }
                    }
                }
                for (ProgressInfo info:progress_list_system) {
                    if (!info.getPakageName().equals(getPackageName())) {
                        if (info.isChecked()) {
                            info.setIsChecked(false);
                        }else {
                            info.setIsChecked(true);
                        }
                    }
                }
                handler.sendEmptyMessage(0);
                break;
            case R.id.bt_progress_killall://一键清理
                list_delete = new ArrayList<ProgressInfo>();
                /**
                 *
                 *    集合迭代的时候不能删除元素,需要另建一个集合,等迭代完成后再删除集合中的元素
                 *
                 *
                 */
                for (ProgressInfo info:progress_list_user) {
                    if (info.isChecked()) {
                        list_delete.add(info);
                    }
                }
                for (ProgressInfo info:progress_list_system) {
                    if (info.isChecked()) {
                        list_delete.add(info);
                    }
                }
                //删除要显示的内存
                progress_list_user.removeAll(list_delete);
                progress_list_system.removeAll(list_delete);
                //删除后的内存可用内存需要增加,总共的进程个数要减少
                for (ProgressInfo info: list_delete) {
                    beforeravaiRam +=info.getAppSize();
                    am.killBackgroundProcesses(info.getPakageName());
                    num++;
                }
                Log.d(TAG, "杀死了" + list_delete.size() + "个进程,总共释放了" + Formatter.formatFileSize(this, beforeravaiRam) + "内存");
                tv_progress_runningnum.setText("运行中的内存:" + String.valueOf(processCount - num));
                availRam = Formatter.formatFileSize(this, availRam_long + beforeravaiRam);
                tv_process_totalmemory.setText("剩余/总内存: " + availRam + "/" + totalRam);
                handler.sendEmptyMessage(0);
                break;
            case R.id.bt_progress_entersetting://进入设置
            startActivity(new Intent(ProgressMannagerActivity.this,ProgressSettingActivity.class));
                break;
            default:
                break;
        }
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_appname;
        TextView tv_appsize;
        CheckBox cb_isuerapp;
    }

    class MyProgessAdapter extends BaseAdapter {
        /**
         * 类也要声明,需要实例化,不然不能用的
         */
        ViewHolder holder;
        /**
         * 需要声明为成员变量,否则会都是那个图标,不能复用
         */
        ProgressInfo info;

        @Override
        public int getCount() {
            return progress_list_user.size() + progress_list_system.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            /**
             *  如果在点击事件的时候需要用到listview布局中的东西,就需要把这个返回的是具体的东西了
             */
            if (position == 0) {
                return null;
            } else if (position == progress_list_user.size() + 1) {
                return null;
            } else if (position < progress_list_user.size() + 1) {
                return progress_list_user.get(position - 1);
            } else {
                return progress_list_system.get(position - 2 - progress_list_user.size());
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View covertview, ViewGroup viewGroup) {

            /**
             *  特殊条目的判断
             */
            if (position == 0) {
                TextView textView = new TextView(ProgressMannagerActivity.this);
                textView.setBackground(new ColorDrawable(Color.GRAY));
                textView.setTextColor(Color.WHITE);
                textView.setText("用户进程 (" + progress_list_user.size() + ")");
                return textView;
            } else if (position == progress_list_user.size() + 1) {
                TextView textView = new TextView(ProgressMannagerActivity.this);
                textView.setBackground(new ColorDrawable(Color.GRAY));
                textView.setText("系统进程 (" + progress_list_system.size() + ")");
                textView.setTextColor(Color.WHITE);
                return textView;
            }
            if (position < progress_list_user.size() + 1) {

                info = progress_list_user.get(position - 1);
                Log.d(TAG, "--------------------用户" + info.toString());
            } else if (position > progress_list_user.size() + 2) {

                info = progress_list_system.get(position - progress_list_user.size() - 2);
                Log.d(TAG, "--------------------系统" + info.toString());
            }

            if (covertview != null && covertview instanceof RelativeLayout) {
                holder = (ViewHolder) covertview.getTag();

            } else {
                holder = new ViewHolder();
                covertview = View.inflate(ProgressMannagerActivity.this, R.layout.progress_mannager_item, null);
                holder.iv_icon = (ImageView) covertview.findViewById(R.id.iv_progress_icon);
                holder.tv_appname = (TextView) covertview.findViewById(R.id.tv_progress_appname);
                holder.tv_appsize = (TextView) covertview.findViewById(R.id.tv_progress_memory);
                holder.cb_isuerapp = (CheckBox) covertview.findViewById(R.id.cb_progress_isuserapp);
                covertview.setTag(holder);
            }
            Drawable icon = info.getIcon();
            holder.iv_icon.setImageDrawable(icon);
            holder.tv_appname.setText(info.getAppName());
            long appSize = info.getAppSize();
            String size = Formatter.formatFileSize(ProgressMannagerActivity.this, appSize);
            holder.tv_appsize.setText(size);
            //让自己的程序不可选择
            if (info.getPakageName().equals(getPackageName())) {
                holder.cb_isuerapp.setVisibility(View.INVISIBLE);
            }
            holder.cb_isuerapp.setChecked(info.isChecked());
            return covertview;
        }
    }
}
