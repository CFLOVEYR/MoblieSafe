package com.tofirst.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tofirst.mobilesafe.R;

public class Setup3Activity extends BaseSetupActivity{
	private EditText et;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et = (EditText) findViewById(R.id.et_contact_safe);
	}
	//选择联系人的方法
	public void select_contact(View view){
		startActivityForResult(new Intent(Setup3Activity.this,ReadContactActivity.class),0);
	}
	@Override
	public void showNext() {
		startActivity(new Intent(Setup3Activity.this,Setup4Activity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}
	@Override
	public void showPrevious() {
		startActivity(new Intent(Setup3Activity.this,Setup2Activity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0) {
			String phone=data.getStringExtra("phone");
			et.setText(phone);
			pre.edit().putString("protect_num", phone).commit();
		}
	
	}
}
