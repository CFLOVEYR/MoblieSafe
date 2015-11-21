package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.adapter.MyBaseAdapter;
import com.tofirst.mobilesafe.bean.BlackNumberInfo;
import com.tofirst.mobilesafe.db.dao.BlackNumberDao;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class CallSmsSafeActivity2 extends Activity {

    private static final String TAG = "Test";
    private ListView listView;
    private List<BlackNumberInfo> blacknumber_list = null;
    private BlackNumberDao dao;
    private LinearLayout ll_pb;
    private  TextView textView_add;
    /**
     * 每次从哪一条数据开始取
     */
    private int startIndex = 0;
    /**
     * 一共有多少条数据
     */
    private int pageTotlaSize;
    /**
     * 每次总共取多少条数据
     */
    private int maxCount = 20;

    private TextView textView;
    private MyBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe2);
        initUI();
        initData();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

                //缓冲页面开始
                ll_pb.setVisibility(View.GONE);
                if (adapter == null) {
                    adapter = new MyAdapter(blacknumber_list, CallSmsSafeActivity2.this);
                    listView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

            } else if (msg.what == 1) {
                //缓冲页面开始
                ll_pb.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 初始化显示内容
     */
    private void initUI() {
        listView = (ListView) findViewById(R.id.list_view);
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        dao = new BlackNumberDao(CallSmsSafeActivity2.this);
        textView = (TextView) findViewById(R.id.tv_pageNum);
        blacknumber_list = new ArrayList<BlackNumberInfo>();
        textView_add= (TextView) findViewById(R.id.bt_black_number_add);
        textView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBlackNumDialog();
            }
        });
        //分页添加的逻辑
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {

                switch (state) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        Log.i(TAG, "滚动到了----------------------------------------------" + lastVisiblePosition);
                        int total = blacknumber_list.size();
                        //如果移动到最后一条数据
                        if (lastVisiblePosition == total - 1) {
                            startIndex += maxCount;
                            if (startIndex > pageTotlaSize) {
                                return;
                            } else {
                                initData();
                            }
                        }
                        break;

                }
            }

            //滚动的时候调用的方法
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void showBlackNumDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(CallSmsSafeActivity2.this);
        final AlertDialog dialog = builder.create();
        View view=View.inflate(CallSmsSafeActivity2.this,R.layout.item_blacknum_dialog,null);
        final EditText et_input_blacknumber = (EditText) view.findViewById(R.id.et_input_blacknumber);
        final CheckBox cb_number_sms = (CheckBox) view.findViewById(R.id.cb_number_sms);
        final CheckBox cb_number_phone = (CheckBox) view.findViewById(R.id.cb_number_phone);
        Button bt_dialog_nagtibe = (Button) view.findViewById(R.id.bt_dialog_nagtibe);//取消的按钮
        Button bt_dialog_positive = (Button) view.findViewById(R.id.bt_dialog_positive);//确定的按钮
        dialog.setView(view);
        dialog.show();
        /**
         *  确定按钮的点击事件
         */

        bt_dialog_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String number=et_input_blacknumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(CallSmsSafeActivity2.this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
                }
                String mode = null;
                if (cb_number_sms.isChecked() && cb_number_phone.isChecked()) {
                    mode = "3";
                } else if (cb_number_phone.isChecked() && !cb_number_sms.isChecked()) {
                    mode = "1";
                } else if (!cb_number_phone.isChecked() && cb_number_sms.isChecked()) {
                    mode = "2";
                }else {
                    Toast.makeText(CallSmsSafeActivity2.this, "请选择一个拉黑的选择", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean insert = dao.insert(number, mode);
                BlackNumberInfo blackinfo=new BlackNumberInfo();
                blackinfo.setNumber(number);
                blackinfo.setMode(mode);
                blacknumber_list.add(blackinfo);
                handler.sendEmptyMessage(0);
                if (insert) {
                    Log.i(TAG, "添加成功");
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG, "添加失败");
                }


            }
        });
        bt_dialog_nagtibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
                /**
                 * 分批加载数据放到集合中
                 */
//                blacknumber_list=dao.queryAll();
                if (blacknumber_list == null) {
                    blacknumber_list = dao.queryBatches(maxCount, startIndex);
                    Log.i(TAG, "集合的长度为-------------------------------------------------------" + blacknumber_list.size());
                } else {
                    blacknumber_list.addAll(dao.queryBatches(maxCount, startIndex));
                    Log.i(TAG, "集合的长度为---------------------------------------------------------" + blacknumber_list.size());
                }
                /**
                 * 总共有多少条数据
                 */
                pageTotlaSize = dao.queryCount();
                Log.i(TAG, "run() called with: " + pageTotlaSize);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public MyAdapter(List<BlackNumberInfo> list, Context mContext) {
            super(list, mContext);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(CallSmsSafeActivity2.this, R.layout.layout_blacknumber_info, null);
                holder.tv_number = (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete_blacknumber);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            //设置号码
            String num=list.get(position).getNumber();
            if (num != null) {
                holder.tv_number.setText(num);
            }
            String mode = list.get(position).getMode();
            //设置拦截模式
            if (mode != null) {
                if (mode.equals("1")) {
                    holder.tv_mode.setText("电话");
                } else if (mode.equals("2")) {
                    holder.tv_mode.setText("短信");
                } else if (mode.equals("3")) {
                    holder.tv_mode.setText("电话+短信");
                }
            }
            //删除数据的事件
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //获得要删除的号码
                    String delete_number = list.get(position).getNumber();
                    //数据库删除数据
                    dao.delete(delete_number);
                    //集合中删除这条数据
                    list.remove(position);
                    //通知适配器刷新
                    adapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv_number;
            TextView tv_mode;
            ImageView iv_delete;
        }
    }
}
