package com.tofirst.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.tofirst.mobilesafe.R;

public class ReadContactActivity extends Activity {
	private ListView lv;
	List<HashMap<String, String>> list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_contacts);
		lv = (ListView) findViewById(R.id.lv);
		list = new ArrayList<HashMap<String, String>>();
		lv.setAdapter(new SimpleAdapter(this, list, R.layout.list_contact_item,
				new String[] { "phone", "name" }, new int[] { R.id.phone,
						R.id.name }));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = list.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);// 将数据放到结果中
				finish();
			}
		});
		// 读取联系人的方法
		ReadContacts();

	}

	private void ReadContacts() {
		ContentResolver resolver = getContentResolver();
		// 思路分析
		// 首先读取raw_contacts中的contact_id
		// 然后在data表中通过contact_id,获得name，address,phone
		// 再然后通过mimetypes表来查询获得的是什么类型

		// 首先读取raw_contacts中的contact_id
		Uri rawContactUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri rawDataUri = Uri.parse("content://com.android.contacts/data");
		Cursor rawCursor = resolver.query(rawContactUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawCursor != null) {
			while (rawCursor.moveToNext()) {
				String contact_id = rawCursor.getString(0);
				// 然后在data表中通过contact_id,获得name，address,phone
				Cursor dataCursor = resolver.query(rawDataUri, new String[] {
						"data1", "mimetype" }, "contact_id=?",
						new String[] { contact_id }, null);
				if (dataCursor != null) {
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(dataCursor
								.getColumnIndex("data1"));
						String mimetype = dataCursor.getString(dataCursor
								.getColumnIndex("mimetype"));
						// System.out.println("--"+contact_id+"---"+data1+"---"+mimetype);
						HashMap<String, String> map = new HashMap<String, String>();
						// 再然后通过mimetypes表来查询获得的是什么类型
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}

						list.add(map);
					}
					// 关闭游标
					dataCursor.close();
				}
			}
			rawCursor.close();// 关闭游标
		}

	}
}
