package com.tofirst.mobilesafe.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by StudyLifetime on 2015/11/20.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    public List<T> list;
    public Context mContext;

    public MyBaseAdapter(List<T> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public MyBaseAdapter() {
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
