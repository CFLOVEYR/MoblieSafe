package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.db.dao.AddressDao;

public class QueryAddressActivity extends Activity {
	private EditText et;
	private TextView tv;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		et = (EditText) findViewById(R.id.et_query_address);
		tv = (TextView) findViewById(R.id.tv_query_address_result);
		et.addTextChangedListener(new TextLister());
	}

	class TextLister implements TextWatcher {
		// 内容改变之前
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		// 内容改变时候
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// CharSequence可以直接ToString转换为字符串，不能直接强制转换
			String address = AddressDao.getAddress(s.toString());
			tv.setText(address);
		}

		// 内容改变之后
		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	/**
	 * 按钮点击查询调用的方法
	 * 
	 * @param view
	 */
	public void queryAddress(View view) {
		String number = et.getText().toString();
		if (!TextUtils.isEmpty(number)) {
			String address = AddressDao.getAddress(number);
			tv.setText(address);
		} else {
			// 摇晃的方法动画效果调用
			shake();
			//震动效果的调用
			vibrate();
		}

	}

	private void vibrate() {
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(2000);//震动两秒
    	/**
    	 *  new long[]{} 数组中的偶数是歇息，奇数是震动的时间
    	 *  -1表示只执行一次，
    	 *  0表示从头开始循环,1表示下一次从第二个开始循环，2表示第三个依次类推，大于数组长度就越界了
    	 *  
    	 */
//    	vibrator.vibrate(new long[]{500,2000,500,2000}, 0);
    	if (vibrator.hasVibrator()) {
    		tv.setText("亲！查询内容不能为空哟！么么哒！");
		}else{
			//计时器的使用
//			new Timer().schedule(new TimerTask() {
//				
//				@Override
//				public void run() {
//				  	vibrator.cancel();
//				}
//			}, 5000);
		
		}
	}

	private void shake() {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		// 可以自己写插补器来实现动画逻辑
		// shake.setInterpolator(new Interpolator() {
		//
		// @Override
		// public float getInterpolation(float x) {
		// float y=(float) Math.sin(x);
		// return y;
		// }
		// });
		et.startAnimation(shake);

	}

}
