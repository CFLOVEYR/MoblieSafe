package com.tofirst.mobilesafe.activity;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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

public class ProgressActivity extends Activity {
    private static final String TAG = "Test";
    @ViewInject(R.id.list_view_process)
    private ListView list_view_process;
    @ViewInject(R.id.tv_process)
    private TextView tv_process_apptype;
    @ViewInject(R.id.tv_progress_runningnum)
    private TextView tv_progress_runningnum;
    @ViewInject(R.id.tv_process_totalmemory)
    private TextView tv_process_totalmemory;
    @ViewInject(R.id.ll_pb_progress)
    private LinearLayout ll_pb_progress;

    private List<ProgressInfo> progress_list;//存储进程信息的集合
    private List<ProgressInfo> progress_list_user;//存储进程信息的集合
    private List<ProgressInfo> progress_list_system;//存储进程信息的集合
    private MyProgessAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                list_view_process.setAdapter( adapter);
            }

        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                ll_pb_progress.setVisibility(View.VISIBLE);
                progress_list_user=new ArrayList<ProgressInfo>();
                progress_list_system=new ArrayList<ProgressInfo>();
                progress_list = ProcessInfos.getPogressInfos(ProgressActivity.this);
                for (ProgressInfo info : progress_list) {
                    if (info.isUserApp()) {
                        progress_list_user.add(info);
                    } else {
                        progress_list_system.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_progress);
        ViewUtils.inject(this);
        //总共的运行程序数目
        int count = SystemInfoUtils.getRunningProcessCount(this);
        tv_progress_runningnum.setText("运行中的进程: " + count + "个");
        //获得可用内存和总共内存的显示
        String availRam = Formatter.formatFileSize(this, SystemInfoUtils.getAvailRam(this));
        String TotalRam = Formatter.formatFileSize(this, SystemInfoUtils.getTotalRam(ProgressActivity.this));
        tv_process_totalmemory.setText("剩余/总内存: " + availRam + "/" + TotalRam);
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
            return progress_list_user.size()+progress_list_system.size()+2;
        }

        @Override
        public Object getItem(int position) {
            return progress_list.get(position);
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
                TextView textView = new TextView(ProgressActivity.this);
                textView.setBackground(new ColorDrawable(Color.GRAY));
                textView.setTextColor(Color.WHITE);
                textView.setText("用户进程 (" + progress_list_user.size() + ")");
                return textView;
            } else if (position == progress_list_user.size() + 1) {
                TextView textView = new TextView(ProgressActivity.this);
                textView.setBackground(new ColorDrawable(Color.GRAY));
                textView.setText("系统进程 (" + progress_list_system.size() + ")");
                textView.setTextColor(Color.WHITE);
                return textView;
            }
            if (position < progress_list_user.size() + 1) {

                info = progress_list_user.get(position - 1);
                Log.d(TAG, "--------------------用户"+info.toString());
            } else if (position > progress_list_user.size() + 2) {

                info = progress_list_system.get(position - progress_list_user.size() - 2);
                Log.d(TAG, "--------------------系统"+info.toString());
            }

            if (covertview != null && covertview instanceof RelativeLayout) {
                holder = (ViewHolder) covertview.getTag();

            } else {
                holder=new ViewHolder();
                covertview = View.inflate(ProgressActivity.this, R.layout.progress_mannager_item, null);
                holder.iv_icon = (ImageView) covertview.findViewById(R.id.iv_progress_icon);
                holder.tv_appname = (TextView)covertview.findViewById(R.id.tv_progress_appname);
                holder.tv_appsize = (TextView) covertview.findViewById(R.id.tv_progress_memory);
                holder.cb_isuerapp = (CheckBox)covertview.findViewById(R.id.cb_progress_isuserapp);
                covertview.setTag(holder);
            }
            Drawable icon = info.getIcon();
            holder.iv_icon.setImageDrawable(icon);
            holder.tv_appname.setText(info.getAppName());
            long appSize = info.getAppSize();
            String size = Formatter.formatFileSize(ProgressActivity.this, appSize);
            holder.tv_appsize.setText(size);
            return covertview;
        }
    }
}
