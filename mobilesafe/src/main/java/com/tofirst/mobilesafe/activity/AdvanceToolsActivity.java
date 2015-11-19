package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tofirst.mobilesafe.R;

public class AdvanceToolsActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advace_tools);
		
	}
	/**
	 * 调到地址查询的界面
	 * @param view
	 */
	public void adressQuery(View view){
		startActivity(new Intent(AdvanceToolsActivity.this,QueryAddressActivity.class));
		finish();
	}
}
