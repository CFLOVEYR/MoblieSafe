package com.tofirst.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.tofirst.mobilesafe.R;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb = (CheckBox) findViewById(R.id.ck_setup4);
		boolean cb_Protected = pre.getBoolean("cb_Protected", false);
		if (cb_Protected) {
			cb.setChecked(true);
			cb.setText("防盗保护已经开启");
		} else {
			cb.setChecked(false);
			cb.setText("防盗保护没有开启");
		}
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					pre.edit().putBoolean("cb_Protected", true).commit();
					cb.setText("防盗保护已经开启");
				} else {
					pre.edit().putBoolean("cb_Protected", false).commit();
					cb.setText("防盗保护没有开启");
				}
			}
		});
	}

	@Override
	public void showNext() {
		// 设置向导页完成
		getSharedPreferences("config", MODE_PRIVATE).edit()
				.putBoolean("lost_find_configed", true).commit();
		startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}

	@Override
	public void showPrevious() {
		startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
	}

}
