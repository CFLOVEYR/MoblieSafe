package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.service.AddressService;
import com.tofirst.mobilesafe.service.CallSmsSafeService;
import com.tofirst.mobilesafe.utils.ServiceStatusUtils;
import com.tofirst.mobilesafe.view.SettingAddressItemView;
import com.tofirst.mobilesafe.view.SettingBlackNumItemView;
import com.tofirst.mobilesafe.view.SettingToastLocationItemView;
import com.tofirst.mobilesafe.view.SettingToastStyleItemView;
import com.tofirst.mobilesafe.view.SettingUpdateItemView;

public class SettingActivity extends Activity {
	private SettingUpdateItemView siv;
	private SettingAddressItemView sav;
	private SettingToastStyleItemView stsv;
	private SettingToastLocationItemView stlv;
	private SettingBlackNumItemView sbv;
	private SharedPreferences pref;
	private String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pref = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_setting);
		// 初始化组件
		initView();
		// 点击事件
		clickEvent();
	}

	private void clickEvent() {
		/**
		 * 自动更新的点击事件
		 */
		siv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否选到
				if (pref.getBoolean("Updateflag", true)) {
					pref.edit().putBoolean("Updateflag", false).commit();
					siv.setChecked(false);
				} else {
					pref.edit().putBoolean("Updateflag", true).commit();
					siv.setChecked(true);
				}
			}
		});
		/**
		 * 归属地查询与否的点击事件
		 */
		sav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否选到
				if (pref.getBoolean("Addressflag", true)) {
					pref.edit().putBoolean("Addressflag", false).commit();
					sav.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));
				} else {
					pref.edit().putBoolean("Addressflag", true).commit();
					sav.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));
				}
			}
		});

		/**
		 * Toast样式风格的点击事件
		 */
		stsv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 弹出对话框
				showSingleDialog();
			}
		});


		/**
		 * Toast位置的点击事件
		 */
		stlv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 弹出对话框
				startActivity(new Intent(SettingActivity.this, ToastLocationActivity.class));
			}
		});


		/**
		 * 黑名单的点击事件
		 */
		sbv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否选到
				if (pref.getBoolean("BlackNumflag", true)) {
					pref.edit().putBoolean("BlackNumflag", false).commit();
					sbv.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							CallSmsSafeService.class));
				} else {
					pref.edit().putBoolean("BlackNumflag", true).commit();
					sbv.setChecked(true);
					startService(new Intent(SettingActivity.this,
							CallSmsSafeService.class));
				}
			}
		});
	}

	protected void showSingleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SettingActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("归属地提示框风格");
		builder.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						pref.edit().putInt("Toast_Style", which).commit();
						stsv.setText(items[which]);
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/**
	 * 布局的初始化
	 */
	private void initView() {
		InitUpdateView();
		InitAddressView();
		InitToastStyleView();
		InitToastLocationView();
		InitBlackNumView();
	}
	private void InitToastLocationView() {
		// 自定义样式的初始化
		stlv = (SettingToastLocationItemView) findViewById(R.id.rl_setting_toast_location);
		// 初始化自动更新属性
		stlv.setTitle(stlv.mtitle);
		stlv.setText("设置归属地提示框的显示位置");
	}

	private void InitToastStyleView() {
		// 自定义样式的初始化
		stsv = (SettingToastStyleItemView) findViewById(R.id.rl_setting_toast_style);
		// 初始化自动更新属性
		stsv.setTitle(stsv.mtitle);
		int num=pref.getInt("Toast_Style", 0);
		stsv.setText(items[num]);
	}

	private void InitAddressView() {
		sav = (SettingAddressItemView) findViewById(R.id.rl_setting_address);
		// 查看进程是否被360，管家类恶意停止
		String serviceName = "com.tofirst.mobilesafe.service.AddressService";
		boolean checkServiceRunning = ServiceStatusUtils.checkServiceRunning(
				this, serviceName);
		if (checkServiceRunning) {
			pref.edit().putBoolean("Addressflag", true).commit();
		} else {
			pref.edit().putBoolean("Addressflag", false).commit();
		}
		// 初始化归属地属性
		sav.setTitle(sav.mtitle);
		if (pref.getBoolean("Addressflag", true)) {
			sav.setChecked(true);
			sav.setText(siv.mDesc_on);
			// startService(new Intent(SettingActivity.this,
			// AddressService.class));
		} else {
			sav.setChecked(false);
			sav.setText(siv.mDesc_off);
			// stopService(new Intent(SettingActivity.this,
			// AddressService.class));
		}

	}

	private void InitUpdateView() {
		siv = (SettingUpdateItemView) findViewById(R.id.rl_setting);
		// 初始化自动更新属性
		siv.setTitle(siv.mtitle);
		if (pref.getBoolean("Updateflag", true)) {
			siv.setChecked(true);
			siv.setText(siv.mDesc_on);
		} else {
			siv.setChecked(false);
			siv.setText(siv.mDesc_off);
		}

	}

	private void InitBlackNumView() {
		// 查看进程是否被360，管家类恶意停止
		String serviceName = "com.tofirst.mobilesafe.service.CallSmsSafeService";
		boolean checkServiceRunning = ServiceStatusUtils.checkServiceRunning(
				this, serviceName);
		if (checkServiceRunning) {
			pref.edit().putBoolean("BlackNumflag", true).commit();
		} else {
			pref.edit().putBoolean("BlackNumflag", false).commit();
		}
		sbv = (SettingBlackNumItemView) findViewById(R.id.rl_setting_blackNum);
		// 初始化自动更新属性
		sbv.setTitle(sbv.mtitle);
		if (pref.getBoolean("BlackNumflag", true)) {
			sbv.setChecked(true);
			sbv.setText(sbv.mDesc_on);
		} else {
			sbv.setChecked(false);
			sbv.setText(sbv.mDesc_off);
		}

	}
}
