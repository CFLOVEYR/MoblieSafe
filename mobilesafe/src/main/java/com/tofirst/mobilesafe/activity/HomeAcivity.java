package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tofirst.mobilesafe.R;
import com.tofirst.mobilesafe.data.NamePace;
import com.tofirst.mobilesafe.utils.Md5Utils;

public class HomeAcivity extends Activity {
	protected static final int SETTING_CENTER = 8;// 设置中心
	protected static final int ADVANCED_TOOLS = 7;// 高级工具
	protected static final int CLEAN_CACHE = 6;// 缓存清理
	protected static final int MOBILE_SECURITY = 5;// 手机杀毒
	protected static final int SUMMARY_FLOW = 4;// 流量统计
	protected static final int PROCESS_MANNAGER = 3;// 进程管理
	protected static final int SOFT_MANNAGER = 2;// 软件管理
	protected static final int NET_SAFE = 1;// 通讯卫士
	protected static final int MOBILE_FD = 0;// 手机防盗
	private static boolean set_psd ;

	private GridView gv_home;
	// 定义主页面的内容
	String[] strs_items = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒",
			"缓存清理", "高级工具", "设置中心" };
	// 定义主页面的图标
	int[] icon_items = { R.drawable.home_safe, R.drawable.home_callmsgsafe,
			R.drawable.home_apps, R.drawable.home_taskmanager,
			R.drawable.home_netmanager, R.drawable.home_trojan,
			R.drawable.home_sysoptimize, R.drawable.home_tools,
			R.drawable.home_settings };
	private SharedPreferences pre;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		pre = getSharedPreferences("config", MODE_PRIVATE);
		// 初始化组件
		initView();
		// 添加适配器
		gv_home.setAdapter(new MyAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case MOBILE_FD:
				showPasswordDialog();
					break;
				case NET_SAFE:
					startActivity(new Intent(HomeAcivity.this,
							CallSmsSafeActivity2.class));
					break;
				case SOFT_MANNAGER:
					startActivity(new Intent(HomeAcivity.this,
							SoftManngerActivity.class));
					break;
				case PROCESS_MANNAGER:
					startActivity(new Intent(HomeAcivity.this,
							RoketActivity.class));
					break;
				case SUMMARY_FLOW:
					startActivity(new Intent(HomeAcivity.this,
							SettingActivity.class));
					break;
				case MOBILE_SECURITY:
					startActivity(new Intent(HomeAcivity.this,
							SettingActivity.class));
					break;
				case CLEAN_CACHE:
					startActivity(new Intent(HomeAcivity.this,
							SettingActivity.class));
					break;
				case ADVANCED_TOOLS:
					startActivity(new Intent(HomeAcivity.this,
							AdvanceToolsActivity.class));
					break;
				case SETTING_CENTER:
					startActivity(new Intent(HomeAcivity.this,
							SettingActivity.class));
					break;

				default:
					break;
				}
			}
		});
	}
	/**
	 * 对于密码的对话框处理的方法
	 */
	protected void showPasswordDialog() {
		//判断是否设置过密码，如果为空
		set_psd=pre.getBoolean("set_psd", false);
		if (!set_psd) {
			//弹出设置密码的对话框
			showSetPasswordDialog();
		}else{
			showInputPasswordDialog();
		}
	}
	/**
	 * 弹出输入密码的对话框
	 */
	private void showInputPasswordDialog() {
		AlertDialog.Builder builder=new AlertDialog.Builder(HomeAcivity.this);
		final AlertDialog create = builder.create();
		View view=View.inflate(HomeAcivity.this, R.layout.psd_input_dialog, null);
		final EditText et_psd=(EditText) view.findViewById(R.id.et_input_psd);
		Button button1=(Button) view.findViewById(R.id.bt_psd_input_yes);
		Button button2=(Button) view.findViewById(R.id.bt_psd_input_no);
		final String pre_psd1=pre.getString("password", null);
		//点击确定按钮的点击事件
				button1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String psd = et_psd.getText().toString();
						//如果输入内容为空
						if (!TextUtils.isEmpty(psd)) {
							if (Md5Utils.psdMD5(psd+NamePace.MD5ADDSTRING).equals(pre_psd1)) {
								Toast.makeText(HomeAcivity.this, "登录成功", Toast.LENGTH_SHORT).show();
								//跳转到手机防盗的页面
								Intent intent=new Intent(HomeAcivity.this,LostFindActivity.class);
								startActivity(intent);
							}else {
								Toast.makeText(HomeAcivity.this, "密码错误", Toast.LENGTH_SHORT).show();
							}
						}else {
							Toast.makeText(HomeAcivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
						}
						//关闭对话框
						create.dismiss();
					}
				});
				//点击确定按钮的点击事件
				button2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//关闭对话框
						create.dismiss();
					}
				});
				create.setView(view);
				create.show();
	}
	/**
	 * 设置密码的对话框的处理
	 */
	private void showSetPasswordDialog() {
	
		AlertDialog.Builder builder=new AlertDialog.Builder(HomeAcivity.this);
		final AlertDialog create = builder.create();
		View view=View.inflate(HomeAcivity.this, R.layout.psd_set_dialog, null);
		final EditText et_psd=(EditText) view.findViewById(R.id.et_psd);
		final EditText et_psd_confirm=(EditText) view.findViewById(R.id.et_psd_confirm);
		Button button1=(Button) view.findViewById(R.id.bt_psd_yes);
		Button button2=(Button) view.findViewById(R.id.bt_psd_no);
		//点击确定按钮的点击事件
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String psd = et_psd.getText().toString();
				String psd_confirm = et_psd_confirm.getText().toString();
				//如果输入内容为空
				if (!TextUtils.isEmpty(psd)&&!TextUtils.isEmpty(psd_confirm)) {
					if (psd.equals(psd_confirm)) {
						Toast.makeText(HomeAcivity.this, "设置成功", Toast.LENGTH_SHORT).show();
						//已经设置过了
						set_psd=true;
						//存入文件中
						pre.edit().putBoolean("set_psd", set_psd).commit();
						//对密码的储存
						pre.edit().putString("password", Md5Utils.psdMD5(psd+NamePace.MD5ADDSTRING)).commit();
						//跳转到手机防盗的页面
						Intent intent=new Intent(HomeAcivity.this,LostFindActivity.class);
						startActivity(intent);
					}else {
						Toast.makeText(HomeAcivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(HomeAcivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
				}
				//关闭对话框
				create.dismiss();
			}
		});
		//点击确定按钮的点击事件
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关闭对话框
				create.dismiss();
			}
		});
		create.setView(view);
		create.show();
	}
	/**
	 * 初始化组件
	 */
	private void initView() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return strs_items.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return strs_items[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = View.inflate(HomeAcivity.this, R.layout.home_item, null);
			ImageView iv_item = (ImageView) v.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
			iv_item.setImageResource(icon_items[position]);
			tv_item.setText(strs_items[position]);
			return v;
		}

	}

}
