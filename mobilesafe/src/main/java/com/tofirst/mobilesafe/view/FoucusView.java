package com.tofirst.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

public class FoucusView extends View {
	//带样式的重写方法
	public FoucusView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	//带属性的重写方法
	public FoucusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	//用代码new对象的时候实现这个方法
	public FoucusView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	//调用这个方法的时候会判断是否焦点在这个组件上，返回true就在这个组件上的焦点
//	@Override
//	@ExportedProperty(category = "focus")
//	public boolean isFocused() {
//		// TODO Auto-generated method stub
////		return super.isFocused();
//		return false;
//	}
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
	

}
