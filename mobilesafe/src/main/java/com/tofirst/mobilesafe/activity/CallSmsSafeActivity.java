package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        initUI();
        
    }
private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what==0){
            //缓冲页面开始
            ll_pb.setVisibility(View.GONE);
            listView.setAdapter(new MyAdapter(blacknumber_list,CallSmsSafeActivity.this));
        }
    }
};
    /**
     * 初始化显示内容
     *
     */
    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        blacknumber_list = new ArrayList<BlackNumberInfo>();
        listView = (ListView) findViewById(R.id.list_view);
        dao = new BlackNumberDao(CallSmsSafeActivity.this);
        new Thread(){
            @Override
            public void run() {
                super.run();
                //缓冲页面开始
                ll_pb.setVisibility(View.VISIBLE);
                //子线程获得数据
                blacknumber_list=dao.queryAll();
                handler.sendEmptyMessage(0);
            }
        }.start();

    }
    class MyAdapter extends MyBaseAdapter<BlackNumberInfo>{
        public MyAdapter(List<BlackNumberInfo> list, Context mContext) {
            super(list, mContext);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(CallSmsSafeActivity.this,R.layout.layout_blacknumber_info,null);
                holder.tv_number= (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tv_mode= (TextView) convertView.findViewById(R.id.tv_black_mode);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            holder.tv_number.setText(list.get(position).getNumber());
            String mode=list.get(position).getMode();
            if (mode.equals("1")){
                holder.tv_mode.setText("电话");
            }else if(mode.equals("2")){
                holder.tv_mode.setText("短信");
            }else if(mode.equals("3")){
                holder.tv_mode.setText("电话+短信");
            }
            return convertView;
        }
        class ViewHolder{
            TextView tv_number;
            TextView tv_mode;
        }
    }
}
