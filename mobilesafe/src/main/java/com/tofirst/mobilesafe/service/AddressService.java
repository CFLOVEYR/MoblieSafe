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
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		/**
		 * 将重心位置移动到左上方（0,0），而不是默认的位置
		 */
		params.gravity= Gravity.TOP+Gravity.LEFT;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
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
		int style_num=mpre.getInt("Toast_Style", 0);
		view.setBackgroundResource(toast_styles[style_num]);
		tv.setText(address);
		mWM.addView(view, params);
	}

}
