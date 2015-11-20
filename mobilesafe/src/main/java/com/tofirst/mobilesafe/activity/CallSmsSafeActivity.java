package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

public class CallSmsSafeActivity extends Activity {

    private ListView listView;
    private List<BlackNumberInfo> blacknumber_list;
    private BlackNumberDao dao;
    private LinearLayout ll_pb;
    /**
     * 当前的页码数
     */
    private int currentPageNumber = 0;
    /**
     * 一页有多少条数据
     */
    private final int pageSize = 20;
    /**
     * 一共有多少条数据
     */
    private int pageTotlaSize;
    /**
     * 一共有多少页
     */
    private int pageNum;
    private TextView textView;
    private EditText et_pageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        initUI();
        initClickEvent();
    }

    private void initClickEvent() {
        findViewById(R.id.bt_prePage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPageNumber <=0) {
                    return;
                }
                currentPageNumber--;
                initUI();
            }
        });
        findViewById(R.id.bt_nextPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPageNumber >=(pageNum-1)) {
                    return;
                }
                currentPageNumber++;
                initUI();
            }
        });
        findViewById(R.id.bt_jumpPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = et_pageNum.getText().toString().trim();
                if (!TextUtils.isEmpty(num)) {
                    int inputpage = Integer.parseInt(num);
                    if (inputpage <0 || inputpage > (pageNum-1)) {
                        Toast.makeText(CallSmsSafeActivity.this, "请输入正确数字", Toast.LENGTH_SHORT).show();
                    } else {
                        currentPageNumber = inputpage;
                        initUI();
                    }
                } else {
                    Toast.makeText(CallSmsSafeActivity.this, "请输入正确数字", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

                //缓冲页面开始
                ll_pb.setVisibility(View.GONE);
                textView.setText(currentPageNumber + "/" + pageNum);
                listView.setAdapter(new MyAdapter(blacknumber_list, CallSmsSafeActivity.this));
            }else if(msg.what == 1){
                //缓冲页面开始
                ll_pb.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 初始化显示内容
     */
    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        blacknumber_list = new ArrayList<BlackNumberInfo>();
        listView = (ListView) findViewById(R.id.list_view);
        dao = new BlackNumberDao(CallSmsSafeActivity.this);
        textView = (TextView) findViewById(R.id.tv_pageNum);
        et_pageNum = (EditText) findViewById(R.id.et_pageNum);
        new Thread() {
            @Override
            public void run() {
                super.run();
                handler.sendEmptyMessage(1);
                //子线程获得全部数据
//                blacknumber_list=dao.queryAll();
                //分页加载数据
                blacknumber_list = dao.queryPage(currentPageNumber, pageSize);
                pageTotlaSize = dao.queryCount();
                System.out.println("---"+pageTotlaSize);
                pageNum = pageTotlaSize / pageSize;
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public MyAdapter(List<BlackNumberInfo> list, Context mContext) {
            super(list, mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(CallSmsSafeActivity.this, R.layout.layout_blacknumber_info, null);
                holder.tv_number = (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            holder.tv_number.setText(list.get(position).getNumber());
            String mode = list.get(position).getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("电话");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("短信");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("电话+短信");
            }
            return convertView;
        }

        class ViewHolder {
            TextView tv_number;
            TextView tv_mode;
        }
    }
}
