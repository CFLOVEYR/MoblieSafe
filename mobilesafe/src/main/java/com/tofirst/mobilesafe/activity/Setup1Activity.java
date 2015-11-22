package com.tofirst.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.tofirst.mobilesafe.R;
public class Setup1Activity extends BaseSetupActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	
	

	@Override
	public void showNext() {
		startActivity(new Intent(Setup1Activity.this,Setup2Activity.class));
		finish();	
		//两个界面切换的动画
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}


	@Override
	public void showPrevious() {
		
	}



}
