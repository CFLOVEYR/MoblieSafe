package com.tofirst.mobilesafe.bean;

/**
 * Created by StudyLifetime on 2015/11/20.
 */
public class BlackNumberInfo {
    private  String number;
    private String mode;

    public BlackNumberInfo() {
    }
    public BlackNumberInfo(String number, String mode) {
        this.number = number;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "number='" + number + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
