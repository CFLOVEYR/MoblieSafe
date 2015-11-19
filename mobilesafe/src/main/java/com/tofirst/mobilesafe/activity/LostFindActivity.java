package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;

public class LostFindActivity extends Activity {
	private SharedPreferences pre;
	private boolean lost_find_configed;
	private TextView tv_protect_num;
	private ImageView iv_lock_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pre = getSharedPreferences("config", MODE_PRIVATE);
		lost_find_configed = pre.getBoolean("lost_find_configed", false);
		if (lost_find_configed) {
			setContentView(R.layout.activity_lost_find);
			tv_protect_num = (TextView) findViewById(R.id.tv_protect_num);
			iv_lock_img = (ImageView) findViewById(R.id.iv_lock_img);
			tv_protect_num.setText(pre.getString("protect_num", "暂无设置"));
			if (pre.getBoolean("cb_Protected", false)) {
				iv_lock_img.setImageResource(R.drawable.lock);
			}else{
				iv_lock_img.setImageResource(R.drawable.unlock);
			}
		}else{
			//进入设置向导页面
			startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
			finish();
		}
	}

	//重新进入设置向导的页面
	public void reSetup(View view){
		startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
		finish();
	}
}
