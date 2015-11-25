package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.bean.SoftManangerInfo;
import com.tofirst.mobilesafe.engine.AppInfos;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class SoftManngerActivity extends Activity implements View.OnClickListener {
    //系统剩余内存
    @ViewInject(R.id.tv_systemsize)
    private TextView tv_systemsize;
    //外部剩余内存
    @ViewInject(R.id.tv_sdcardsize)
    private TextView tv_sdcardsize;
    @ViewInject(R.id.list_view_mannger)
    private ListView list_view_mannger;
    //展示一共有多少个系统和应用程序的条目
    @ViewInject(R.id.tv_soft_total)
    private TextView tv_soft_total;
    private List<SoftManangerInfo> list;
    private List<SoftManangerInfo> list_user;
    private List<SoftManangerInfo> list_system;
    private String TAG = "Test";
    private PopupWindow popupWindow;
    private SoftManangerInfo clickinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_mannger);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {

        private MyAdapter adapter;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (adapter == null) {
                    adapter = new MyAdapter();
                    list_view_mannger.setAdapter(adapter);
                } else {
                    list_view_mannger.setAdapter(adapter);
                }

            }
        }
    };

    private void initData() {
        list = new ArrayList<SoftManangerInfo>();
        list_user = new ArrayList<SoftManangerInfo>();
        list_system = new ArrayList<SoftManangerInfo>();
        new Thread() {
            @Override
            public void run() {
                list = AppInfos.getAppInfo(SoftManngerActivity.this);
                //区分开用户和系统的app
                for (SoftManangerInfo info : list) {
                    if (info.isUserApp()) {
                        list_user.add(info);
                    } else {
                        list_system.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        //利用Utils包方法findViewById() 需要些的东西
        ViewUtils.inject(this);
        long dataSpace = Environment.getDataDirectory().getFreeSpace();
        long sdcardSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //利用安卓自带的格式化类的方法
        tv_systemsize.setText("系统剩余内存:" + Formatter.formatFileSize(this, dataSpace));
        tv_sdcardsize.setText("Sdcard剩余内存:" + Formatter.formatFileSize(this, sdcardSpace));
        //滚动的时候改变内容
        list_view_mannger.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int first, int i1, int i2) {
                /**
                 *   取消上一次的popupWindow
                 */
                dissmissPoupWindow();
                /**
                 * 可能还没取完呢，需要判断一下都不为空，表示取完了
                 */
                if (list_user != null && list_system != null) {
                    if (first >= list_user.size()) {
                        tv_soft_total.setText("系统程序：(" + list_system.size() + ")个");
                    } else {
                        tv_soft_total.setText("用户程序：(" + list_user.size() + ")个");
                    }
                }

            }
        });
        /**
         *   listview的点击事件
         */
        list_view_mannger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object obj = list_view_mannger.getItemAtPosition(position);
                //取消上一次的PopupWindow
                dissmissPoupWindow();
                /**
                 * PopupWindow的设置
                 */
                if (obj != null) {
                    //PopupWindow里边的内容
                    clickinfo = (SoftManangerInfo) obj;
                    View poupview = View.inflate(SoftManngerActivity.this, R.layout.popup_item_layout, null);
                    LinearLayout ll_popup_uninstall = (LinearLayout) poupview.findViewById(R.id.ll_popup_uninstall);
                    LinearLayout ll_popup_run = (LinearLayout) poupview.findViewById(R.id.ll_popup_run);
                    LinearLayout ll_popup_share = (LinearLayout) poupview.findViewById(R.id.ll_popup_share);
                    ll_popup_uninstall.setOnClickListener(SoftManngerActivity.this);
                    ll_popup_run.setOnClickListener(SoftManngerActivity.this);
                    ll_popup_share.setOnClickListener(SoftManngerActivity.this);

                    //设置透明度动画
                    AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                    aa.setDuration(500);
                    //设置变形
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
                            1.0f, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(500);
                    //组合动画
                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(sa);
                    set.addAnimation(aa);
                    //播放动画
                    poupview.startAnimation(set);
                    popupWindow = new PopupWindow(poupview, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    /**
                     *  必须给PopupWindow设置背景颜色,否则什么也显示不出来
                     */
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    popupWindow.showAtLocation(adapterView, Gravity.LEFT + Gravity.TOP, 50, location[1]);
                }
            }
        });
    }

    //取消特效
    private void dissmissPoupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    //popuWindow的点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //卸载功能的实现
            case R.id.ll_popup_uninstall:
                startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + this.clickinfo.getPakageName())));
                dissmissPoupWindow();
                break;
            //运行功能的实现
            case R.id.ll_popup_run:
                startActivity(getPackageManager().getLaunchIntentForPackage(this.clickinfo.getPakageName()));
                dissmissPoupWindow();
                break;
            //分享功能的事件
            case R.id.ll_popup_share:
                Intent localIntent2 = new Intent("android.intent.action.SEND");
                localIntent2.setType("text/plain");
                localIntent2.putExtra("android.intent.extra.SUBJECT", "f分享");
                localIntent2.putExtra("android.intent.extra.TEXT", "Hi！推荐您使用软件：" + this.clickinfo.getPakageName() + "下载地址:"
                        + "https://play.google.com/store/apps/details?id=" + this.clickinfo.getPakageName());
                startActivity(Intent.createChooser(localIntent2, "分享"));
                dissmissPoupWindow();
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        ViewHolder holder;

        private SoftManangerInfo info;

        @Override
        public int getCount() {
            return list_user.size() + list_system.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            SoftManangerInfo info = null;
            if (position == 0) {
                return null;
            } else if (position < list_user.size() + 1) {
                info = list_user.get(position - 1);
            } else if (position == list_user.size() + 1) {
                return null;
            } else {
                info = list_system.get(position - 2 - list_user.size());
            }

            return info;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View covertview, ViewGroup viewGroup) {

            //特殊条目的判断
            if (position == 0) {
                TextView textView = new TextView(SoftManngerActivity.this);
                textView.setText("用户程序：(" + list_user.size() + ")个");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.BLACK);
                return textView;
            } else if (position < list_user.size() + 1) {
                info = list_user.get(position - 1);
            } else if (position == list_user.size() + 1) {
                TextView textView = new TextView(SoftManngerActivity.this);
                textView.setText("系统程序：(" + list_system.size() + ")个");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.BLACK);
                return textView;
            } else {
                info = list_system.get(position - list_user.size() - 2);
            }


            //对于特殊条目的处理要判断他是不是我们的类型，就是R.layout.soft_manager_item的父控件LinearLayout
            if (covertview != null && covertview instanceof LinearLayout) {
                holder = (ViewHolder) covertview.getTag();
            } else {
                holder = new ViewHolder();
                covertview = View.inflate(SoftManngerActivity.this, R.layout.soft_manager_item, null);
                holder.iv_icon = (ImageView) covertview.findViewById(R.id.iv_soft_mannager);
                holder.tv_name = (TextView) covertview.findViewById(R.id.tv_soft_mannger_name);
                holder.tv_location = (TextView) covertview.findViewById(R.id.tv_soft_location);
                holder.tv_soft_size = (TextView) covertview.findViewById(R.id.tv_soft_size);
                covertview.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getName());
            boolean rom = info.isRom();
            if (rom == true) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("Sdcard内存");
            }
            long appSize = info.getAppSize();
            holder.tv_soft_size.setText(Formatter.formatFileSize(SoftManngerActivity.this, appSize));
            return covertview;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_location;
            TextView tv_soft_size;
        }
    }
}
