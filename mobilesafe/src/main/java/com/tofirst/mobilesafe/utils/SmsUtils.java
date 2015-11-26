package com.tofirst.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.tofirst.mobilesafe.activity.SmsBackupActivity;
import com.tofirst.mobilesafe.bean.SmsInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Study on 2015/11/25.
 */
public class SmsUtils {

    private static final String TAG = "Test";
    public interface SmsCallback{
        public void beforeCallBack(int count);
        public void onCallBack(int num);

    }
    public static List<SmsInfo> getSmsinfos(Context context,SmsCallback callback) {
        List<SmsInfo> smsInfoLists = new ArrayList<SmsInfo>();
        SmsInfo smsInfo;
        /**
         *  拿到手机的短信记录,利用ContentPrivider
         */
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = resolver.query(uri, new String[]{"address", "type", "date", "body"}, null, null, null);
        int num=1;
        /**
         *         获得总共的短信条数,并且给progressDialog设置最大值
         */
        int count=cursor.getCount();
//        progressDialog.setMax(count);

        callback.beforeCallBack(count);
        while (cursor.moveToNext()) {
            smsInfo = new SmsInfo();
            smsInfo.setNumber(cursor.getString(0));
            smsInfo.setType(cursor.getInt(1));
            smsInfo.setTime(cursor.getString(2));
            smsInfo.setBody(cursor.getString(3));
            smsInfoLists.add(smsInfo);
            SystemClock.sleep(1000);
//            progressDialog.setProgress(num++);
            callback.onCallBack(num++);
        }

        Log.d(TAG, smsInfoLists.toString());
        //关闭游标
        cursor.close();
        return smsInfoLists;
    }

    //备份短信的方法
    public static void SmsBackupToSdcard(Context context,SmsCallback callback) {
        List<SmsInfo> smsInfoLists = new ArrayList<SmsInfo>();
        SmsInfo smsInfo;
        //首先要判断Sdcard是否状态良好
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "进入sdcard");
            /**
             * 得到短信内容的集合
             */
            smsInfoLists = getSmsinfos(context,callback);
            /**
             * 备份短信到xml文件中
             *
             */
            XmlSerializer serializer = Xml.newSerializer();
            File file = new File(Environment.getExternalStorageDirectory() + "/sms.xml");
            Log.d(TAG, "file=" + Environment.getExternalStorageDirectory() + "/sms.xml");
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
                //设置序列化器的参数
                serializer.setOutput(os, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "SMS");
                /**
                 *  遍历数组来存储
                 */
                for (SmsInfo info : smsInfoLists) {
                    serializer.startTag(null, "sms");
                    //地址
                    serializer.startTag(null, "address");
                    serializer.text(info.getNumber());
                    serializer.endTag(null, "address");
                    //时间
                    serializer.startTag(null, "date");
                    serializer.text(info.getTime());
                    serializer.endTag(null, "date");
                    //内容
                    serializer.startTag(null, "body");
                    /**
                     *    对数据进行加密处理
                     */
                    serializer.text(new encryptionUtils().encryption(info.getBody()));
                    serializer.endTag(null, "body");
                    //来电还是去电
                    serializer.startTag(null, "type");
                    if (info.getType() == 1) {
                        serializer.text("接收的信息");
                    } else {
                        serializer.text("发送的信息");
                    }
                    serializer.endTag(null, "type");

                    serializer.endTag(null, "sms");

                }
                serializer.endTag(null, "SMS");
                serializer.endDocument();
                Log.d(TAG, "保存成功");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, "么么哒,Sdcard不可用", Toast.LENGTH_SHORT).show();
        }


    }
}
