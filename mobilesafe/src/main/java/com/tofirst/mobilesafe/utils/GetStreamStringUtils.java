package com.tofirst.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GetStreamStringUtils {
	public static String getStringStream(InputStream in) throws IOException{
		//获得字节缓冲流，获得后再写进去
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		String str=null;
		int len=0;
		byte[] data=new byte[1024];
		while ((len=in.read(data))!=-1) {
			out.write(data,0,len);
		}
		str=out.toString();
		in.close();
		out.close();
		return str;
	}
}
