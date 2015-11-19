package com.tofirst.mobilesafe.view;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.data.NamePace;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingToastStyleItemView extends RelativeLayout {
	private TextView tv1;
	private TextView tv2;
	public String mtitle;

	// 带样式的
	public SettingToastStyleItemView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public void setText(String str) {
		tv2.setText(str);
	}

	public void setTitle(String str) {
		tv1.setText(str);
	}

	private void initView() {
		/**
		 * 第三个参数是把自定义的布局返回给SettingItemView
		 */
		View.inflate(getContext(), R.layout.setting_item_toast_style, this);
		tv1 = (TextView) findViewById(R.id.tv_setting1);
		tv2 = (TextView) findViewById(R.id.tv_setting2);
	}

	// 带属性的
	public SettingToastStyleItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		mtitle = attrs.getAttributeValue(NamePace.NAMESPCE, "title");
	}

	// new 的时候
	public SettingToastStyleItemView(Context context) {
		super(context);
		initView();
	}

}
