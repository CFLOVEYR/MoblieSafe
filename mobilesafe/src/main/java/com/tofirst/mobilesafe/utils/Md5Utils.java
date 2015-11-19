package com.tofirst.mobilesafe.utils;

import java.security.MessageDigest;

import com.tofirst.mobilesafe.data.NamePace;

public class Md5Utils {
	/**
	 * 数据加密的处理
	 * @param str
	 * @return
	 */
	public static String psdMD5(String str){
		String ss;
		try {
			String token=str+NamePace.MD5ADDSTRING;
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    byte[] md5 = md.digest(token.getBytes());
		    ss=new String(md5);
		} catch (Exception e) {

		    throw new RuntimeException(e);

		}
		System.out.println("---------------"+ss);
		return new String(ss);
	}
}
