package com.tofirst.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.view.SettingUpdateItemView;

public class Setup2Activity extends BaseSetupActivity {
	private SettingUpdateItemView siv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		// 初始化组件
		initView();
		siv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否选到
				if (pre.getBoolean("Simflag", true)) {
					siv.setChecked(false);
					pre.edit().putBoolean("Simflag", false).commit();
				} else {
					siv.setChecked(true);
					pre.edit().putBoolean("Simflag", true).commit();
					//如果原先没有绑定开始绑定的时候获取SIM卡的序列号
					TelephonyManager  manager=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = manager.getSimSerialNumber();
					pre.edit().putString("SimNumber", simSerialNumber).commit();
				}
			}
		});
	}
	protected void checkedBang() {
		AlertDialog.Builder builder=new AlertDialog.Builder(Setup2Activity.this);
		builder.setTitle("下一步操作需要绑定手机");
		builder.setMessage("SIM不绑定无法保证您的手机安全,请您慎重考虑");
		builder.setPositiveButton("立即绑定", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				siv.setChecked(true);
				pre.edit().putBoolean("Simflag", true).commit();
				//如果原先没有绑定开始绑定的时候获取SIM卡的序列号
				TelephonyManager  manager=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				String simSerialNumber = manager.getSimSerialNumber();
				pre.edit().putString("SimNumber", simSerialNumber).commit();
			}
		});
	   builder.setNegativeButton("狠心不绑定", new android.content.DialogInterface.OnClickListener(){
		   public void onClick(DialogInterface dialog, int which) {
			   startActivity(new Intent(Setup2Activity.this,LostFindActivity.class));
		   };
	   });
	   builder.show();
	}
	//页面组件的初始化
	private void initView() {
		siv = (SettingUpdateItemView) findViewById(R.id.rl_setting);
		// 初始化属性
		siv.setTitle(siv.mtitle);
		if (pre.getBoolean("Simflag", false)) {
			siv.setChecked(true);
			siv.setText(siv.mDesc_on);
		} else {
			siv.setChecked(false);
			siv.setText(siv.mDesc_off);
		}
	}

	@Override
	public void showNext() {
		if (pre.getBoolean("Simflag", false)) {
			startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
			finish();
			// 两个界面切换的动画
			overridePendingTransition(R.anim.next_in, R.anim.next_out);
		}else{
		      //需要绑定才能进行下一步
			checkedBang();
		}

	}

	@Override
	public void showPrevious() {
		startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
	}
}
