package com.tofirst.mobilesafe.activity;

import com.tofirst.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	public GestureDetector detector;
	public SharedPreferences pre;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pre = getSharedPreferences("config", MODE_PRIVATE);
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//调到下一个页面的判断,如果距离>200
				if (e2.getRawX() - e1.getRawX() > 150) {
					showPrevious();
					return true;
				}
				//调到上一个页面的判断,如果距离>200
				if (e1.getRawX() - e2.getRawX() > 150) {
					showNext();
					return true;
				}
				if (Math.abs(e2.getRawY()-e1.getRawY())>200) {
					Toast.makeText(BaseSetupActivity.this, "不能这样滑动哟！么么哒！",0).show();
					return true;
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	// 调到下一个页面
	public void next(View view) {
		showNext();
	}



	// 调到下一个页面
	public void previous(View view) {
		showPrevious();
	}
	/**
	 * 子类必须实现的方法
	 */
	public abstract void showPrevious();
	/**
	 * 子类必须实现的方法
	 */
	public  abstract void showNext();
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 把触摸委托给首饰识别器
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
