package com.tofirst.mobilesafe.fragment;

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
public class LockFragment extends Fragment {

    private TextView tv_lock_fragment;
    private ListView lv_lock_fragment;
    private List<SoftManangerInfo> infos;
    private SoftLockDao dao;
    private MySoftUnLockAdapter adapter;
    private List<SoftManangerInfo> lock_infos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_lock_fragment, null);
        lv_lock_fragment = (ListView) view.findViewById(R.id.lv_lock_fragment);
        tv_lock_fragment = (TextView) view.findViewById(R.id.tv_lock_fragment);
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (adapter == null) {
                adapter = new MySoftUnLockAdapter();
                lv_lock_fragment.setAdapter(adapter);
            } else {
                lv_lock_fragment.setAdapter(adapter);
            }
            tv_lock_fragment.setText("未加锁程序(" + lock_infos.size() + ")个");
        }
    };

    /**
     * 因为每次切换都需要加载数据,如果是onCreate()里边写的话就不会重复加载了
     */
    @Override
    public void onStart() {
        super.onStart();
        initData();
        lv_lock_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                dao = new SoftLockDao(getActivity());
                dao.delete(lock_infos.get(position).getPakageName());
                /**
                 * 位移动画
                 */
                TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
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
                                lock_infos.remove(lock_infos.get(position));
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
                super.run();
                dao = new SoftLockDao(getActivity());
                dao = new SoftLockDao(getActivity());
                infos = AppInfos.getAppInfo(getActivity());
                lock_infos = new ArrayList<SoftManangerInfo>();
                List<String> list_name = new ArrayList<String>();
                if (dao.queryAll() != null) {
                    list_name = dao.queryAll();
                    for (String pakagename : list_name) {
                        for (SoftManangerInfo info : infos) {
                            if (info.getPakageName().equals(pakagename)) {
                                lock_infos.add(info);
                            }
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    class MySoftUnLockAdapter extends BaseAdapter {
        ViewHolder holder;
        SoftManangerInfo info;

        @Override
        public int getCount() {
            return lock_infos.size();
        }

        @Override
        public Object getItem(int position) {
            return lock_infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View covertview, ViewGroup viewGroup) {
            info = lock_infos.get(position);
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
