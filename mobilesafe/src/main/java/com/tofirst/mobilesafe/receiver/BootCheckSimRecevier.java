package com.tofirst.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCheckSimRecevier extends BroadcastReceiver {

	private SharedPreferences pre;

	@Override
	public void onReceive(Context context, Intent intent) {
//		System.out.println("-----手机重启了");
		//判断是否绑定SIM卡
		boolean Simflag=context.getSharedPreferences("config", context.MODE_PRIVATE).getBoolean("Simflag", false);
		pre = context.getSharedPreferences("config", context.MODE_PRIVATE);
		String simSerialNumber =pre.getString("SimNumber", "");
		if (Simflag) {
			//如果手机绑定了就会获取当前手机的sim，看是否发生改变
			TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String simSerialNumber_new = manager.getSimSerialNumber()+"111";
			if (simSerialNumber.equals(simSerialNumber_new)) {
				System.out.println("---SIM没有变更");
			}else{
				System.out.println("---SIM变更，发送报警短信");
				//读取安全手机号码
				String phone=pre.getString("protect_num", "");
				//发送短信
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(phone, null, " your friend simcard has changed", null, null);
				
			}
			
		}
	}

}
