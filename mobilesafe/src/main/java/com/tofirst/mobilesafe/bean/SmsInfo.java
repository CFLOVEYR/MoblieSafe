package com.tofirst.mobilesafe.bean;

/**
 * Created by Study on 2015/11/25.
 */
public class SmsInfo {
    //手机号码
    private String number;
    //发送接收的事件
    private String time;
    // 1 表示接收的短信  2 代表发送出去的短信
    private int type;
    //短信内容
    private String body;

    public SmsInfo() {
    }

    public SmsInfo(String number, String time, int type, String body) {

        this.number = number;
        this.time = time;
        this.type = type;
        this.body = body;
    }

    @Override
    public String toString() {
        return "SmsInfo{" +
                "number='" + number + '\'' +
                ", time='" + time + '\'' +
                ", type=" + type +
                ", body='" + body + '\'' +
                '}';
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
