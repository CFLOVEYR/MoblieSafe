package com.tofirst.mobilesafe.receiver;

import com.tofirst.mobilesafe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/**
 * 广播接收者，需要注册权限
 * @author StudyLifetime
 *
 */
public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			String resultData = getResultData();
			String address=AddressDao.getAddress(resultData);
			Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
