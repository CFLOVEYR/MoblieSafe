package com.tofirst.mobilesafe.service;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 获取地址的服务
 * 
 * @author StudyLifetime
 * 
 */
public class AddressService extends Service {
	private SharedPreferences mpre;
	private TelephonyManager tm;
	private MyLisener lisener;
	private OutCallReceiver receiver;
	private IntentFilter filter;
	private WindowManager mWM;
	private View view;
	private int startX;
	private int startY;
	private int winwidth;
	private int height;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("----服务开启了");
		mpre = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		lisener = new MyLisener();
		tm.listen(lisener, PhoneStateListener.LISTEN_CALL_STATE);// 监听打电话的状态

		// 为了方便管理来电和去电，把监听去电和来电写在一起方便管理
		receiver = new OutCallReceiver();
		filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	class MyLisener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("----来电话了......");
				String address = AddressDao.getAddress(incomingNumber);
				mpre.edit().putString("phoneaddress", address).commit();
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG)
				// .show();
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机的状态

				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话是闲置状态
				if (mWM != null && view != null) {
					mWM.removeView(view);
					mWM = null;
					view = null;
				}
				break;

			default:
				break;
			}
		}
	}

	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String resultData = getResultData();
			String address = AddressDao.getAddress(resultData);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消监听，增加程序的可控性
		tm.listen(lisener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
		System.out.println("----来电和去电的服务关闭了");
	}

	// 自定义Toast，修改系统原生的东西
	public void showToast(String address) {
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		winwidth = mWM.getDefaultDisplay().getWidth();
		height = mWM.getDefaultDisplay().getHeight();
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				/**
				 * FLAG_NOT_TOUCHABLE 表示不能被触摸
				 * 需要注释这句话，不能被触摸。
				 */
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		/**
		 * 将重心位置移动到左上方（0,0），而不是默认的位置
		 */
		params.gravity= Gravity.TOP+Gravity.LEFT;
		params.format = PixelFormat.TRANSLUCENT;
		/**
		 * 需要把他的权限调高，TYPE_TOAST只是一个短暂的，需要设置为TYPE_PHONE类型的
		 *    需要添加权限
		 *     <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
		 */
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("Toast");
		final int lastX=mpre.getInt("lastX",0);
		final int lastY=mpre.getInt("lastY",0);
		params.x=lastX;
		params.y=lastY;
		view = View.inflate(this, R.layout.toast_adress_item, null);
		int[] toast_styles = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_adress);
		int style_num = mpre.getInt("Toast_Style", 0);
		view.setBackgroundResource(toast_styles[style_num]);
		tv.setText(address);
		mWM.addView(view, params);
		/**
		 *  触发拖动事件
		 */
		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						//获得移动后的坐标
						int endX = (int) motionEvent.getRawX();
						int endY = (int) motionEvent.getRawY();
						//改变坐标
						params.x = endX;
						params.y = endY;
						if (params.x<0){
							params.x=0;
						}
						if (params.y<0){
							params.y=0;
						}
						if (params.x>winwidth-view.getWidth()){
							params.x=winwidth-view.getWidth();
						}
						if (params.y>height-view.getHeight()){
							params.y=height-view.getHeight();
						}
						//更新内容,而不是添加内容了
						mWM.updateViewLayout(view, params);
						break;
					case MotionEvent.ACTION_UP:
						//保存坐标
						mpre.edit().putInt("lastX", params.x).commit();
						mpre.edit().putInt("lastY", params.y).commit();
						break;
				}

				return false;//设置为false，表示处理完毕让下边的继续处理
			}
		});
	}

}
