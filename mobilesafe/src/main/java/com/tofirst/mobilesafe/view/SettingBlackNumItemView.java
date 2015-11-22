package com.tofirst.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.data.NamePace;

public class SettingBlackNumItemView extends RelativeLayout {
	private CheckBox cb_setting;
	private TextView tv1;
	private TextView tv2;
	public String mtitle;
	public String mDesc_on;
	public String mDesc_off;;

	// 带样式的
	public SettingBlackNumItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public void setText(String str) {
		tv2.setText(str);
	}
	public void setTitle(String str) {
		tv1.setText(str);
	}

	public void setChecked(boolean flag) {
		cb_setting.setChecked(flag);
		if (flag) {
			tv2.setText(mDesc_on);
		}else {
			tv2.setText(mDesc_off);
		}
	}

	// 判断是否点击
	public boolean getcbchecked() {
		return cb_setting.isChecked();
	}

	private void initView() {
		/**
		 * 第三个参数是把自定义的布局返回给SettingItemView
		 */
		View.inflate(getContext(), R.layout.setting_item, this);
		cb_setting = (CheckBox) findViewById(R.id.cb_setting);
		tv1 = (TextView) findViewById(R.id.tv_setting1);
		tv2 = (TextView) findViewById(R.id.tv_setting2);
	}

	// 带属性的
	public SettingBlackNumItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

	mtitle = attrs.getAttributeValue(NamePace.NAMESPCE, "title");
	mDesc_on = attrs.getAttributeValue(NamePace.NAMESPCE, "desc_on");
	mDesc_off = attrs.getAttributeValue(NamePace.NAMESPCE, "desc_off");
	System.out.println("---"+mtitle);
	}

	// new 的时候
	public SettingBlackNumItemView(Context context) {
		super(context);
		initView();
	}

}
