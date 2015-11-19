package com.tofirst.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {
	private SharedPreferences mpref;
	private LocationManager lm;
	private Mylisener lisener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mpref = getSharedPreferences("config", MODE_PRIVATE);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// List<String> allProviders = lm.getAllProviders();
		// System.out.println("--"+allProviders);
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// 是否考虑付费
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 良好的信号就可以了
		String bestProvider = lm.getBestProvider(criteria, true);// 参数一是自己添加刷选条件，第二个参数
		lisener = new Mylisener();
		/**
		 * 第一个参数是提供商，第二个参数是间隔时间，第三个是间隔距离，第四个是监听事件
		 */
//		 lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
//		 lisener);
		lm.requestLocationUpdates(bestProvider, 0, 0, lisener);
	}

	class Mylisener implements LocationListener {
		// 地址发生变化
		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();// 经度
			double longitude = location.getLongitude();// 纬度
			System.out.println("---"+latitude+" : "+longitude);
			mpref.edit()
					.putString("location",
							latitude+" : "+longitude).commit();
			System.out.println("--------onLocationChanged"+longitude+"--"+latitude);
			//当获得地址后关掉获取地址的服务
			stopSelf();//关掉服务自身
		}

		// 状态发生变化
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			System.out.println("--------onStatusChanged");
		}

		// 用户打开GPS
		@Override
		public void onProviderEnabled(String provider) {
			System.out.println("--------onProviderEnabled");

		}

		// 用户关闭GPS
		@Override
		public void onProviderDisabled(String provider) {
			System.out.println("--------onProviderDisabled");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//当服务销毁的时候停止获取的LocationMannager
		lm.removeUpdates(lisener);
	}

}
