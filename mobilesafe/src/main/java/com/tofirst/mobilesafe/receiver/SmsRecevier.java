package com.tofirst.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.service.LocationService;

public class SmsRecevier extends BroadcastReceiver {

	private SharedPreferences mpref;
	private ComponentName mDeviceAdminSample;
	DevicePolicyManager mDPM;
	Context context;
	Intent intent;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;
		// 远程锁屏
		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
		mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);// 设备组件
		mpref = context.getSharedPreferences("config", context.MODE_PRIVATE);
		// 获取数据
		Bundle bundle = intent.getExtras();
		// 获取数组
		Object[] objects = (Object[]) bundle.get("pdus");
		for (Object object : objects) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			sms.getOriginatingAddress();// 获得手机号码
			String content = sms.getMessageBody();// 获得手机内容
			System.out.println("---" + content);
			// 接收到不同信息的处理方式
			if ("#*alarm*#".equals(content)) {
				System.out.println("-----开始单曲循环");
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1f, 1f);// 设置左右声道
				player.setLooping(true);// 设置单曲循环
				player.start();
			} else if ("#*location*#".equals(content)) {
				context.startService(new Intent(context, LocationService.class));
				String location = mpref.getString("location",
						"watting for getting location");
				System.out.println("-----" + location);
			} else if ("#*lockscreen*#".equals(content)) {
				System.out.println("---------------33333333333333");
				if (isActiveAdmin()) {
					System.out.println("---------------1111111111111111");
					mDPM.lockNow();
					// mDPM.resetPassword("123", 0);
				} else {
					System.out.println("---------------2222222222222222");
					Intent intent1 = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mDeviceAdminSample);
					intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"hahahahahah");
					intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //
					context.startActivity(intent1);
				}
			}

			else if ("#*wipedata*#".equals(content)) {
				if (isActiveAdmin()) {
					mDPM.wipeData(0);// 0表示恢复出厂设置，WIPE_EXTERNAL_STORAGE表示什么也清除了
					// mDPM.resetPassword("123", 0);
				} else {
					Intent intent1 = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mDeviceAdminSample);
					intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"hahahahahah");
					intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //
					context.startActivity(intent1);
				}
			}
			abortBroadcast();// 拦截短信，不让小偷知道
		}

	}

	private boolean isActiveAdmin() {
		return mDPM.isAdminActive(mDeviceAdminSample);
	}
}
