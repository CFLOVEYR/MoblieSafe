package com.tofirst.mobilesafe.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.bean.SoftManangerInfo;
import com.tofirst.mobilesafe.db.dao.SoftLockDao;
import com.tofirst.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Study on 2015/11/28.
 */
public class UnLockFragment extends Fragment {

    private ListView lv_unlock_fragment;
    private List<SoftManangerInfo> infos;
    private TextView tv_unlock_fragment;
    private MySoftLockAdapter adapter;
    private SoftLockDao dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_unlock_fragment, null);
        lv_unlock_fragment = (ListView) view.findViewById(R.id.lv_unlock_fragment);
        tv_unlock_fragment = (TextView) view.findViewById(R.id.tv_unlock_fragment);
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (adapter == null) {
                adapter = new MySoftLockAdapter();
                lv_unlock_fragment.setAdapter(adapter);
            } else {
                lv_unlock_fragment.setAdapter(adapter);
            }
            tv_unlock_fragment.setText("未加锁程序(" + infos.size() + ")个");
        }
    };

    /**
     * 因为每次切换都需要加载数据,如果是onCreate()里边写的话就不会重复加载了
     */
    @Override
    public void onStart() {
        super.onStart();
        initData();
        lv_unlock_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                dao.insert(infos.get(position).getPakageName());
                /**
                 * 位移动画
                 */
                TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                animation.setDuration(3000);
                view.startAnimation(animation);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        SystemClock.sleep(3000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                infos.remove(infos.get(position));
                                adapter.notifyDataSetInvalidated();
                                handler.sendEmptyMessage(0);
                            }
                        });
                    }
                }.start();
            }
        });
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                dao = new SoftLockDao(getActivity());
                infos = AppInfos.getAppInfo(getActivity());
                List<SoftManangerInfo> delete_infos = new ArrayList<SoftManangerInfo>();
                List<String> list_name = new ArrayList<String>();
                if (dao.queryAll() != null) {
                    list_name = dao.queryAll();
                    for (String pakagename : list_name) {
                        for (SoftManangerInfo info : infos) {
                            if (info.getPakageName().equals(pakagename)) {
                                delete_infos.add(info);
                            }
                        }
                    }
                }
                infos.removeAll(delete_infos);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    class MySoftLockAdapter extends BaseAdapter {
        ViewHolder holder;
        SoftManangerInfo info;

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View covertview, ViewGroup viewGroup) {
            info = infos.get(position);
            if (covertview != null) {
                holder = (ViewHolder) covertview.getTag();
            } else {
                holder = new ViewHolder();
                covertview = View.inflate(getActivity(), R.layout.item_softunlock, null);
                holder.iv_icon = (ImageView) covertview.findViewById(R.id.iv_softlock_icon);
                holder.tv_appname = (TextView) covertview.findViewById(R.id.tv_softlock_appname);
                covertview.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_appname.setText(info.getName());
            return covertview;
        }
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_appname;
    }
}
