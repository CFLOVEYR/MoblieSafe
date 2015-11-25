package com.tofirst.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by StudyLifetime on 2015/11/22.
 */
public class SoftManangerInfo {
    /**
     * 图标
     */
    private Drawable icon;
    /**
     * 应用的名字
     */
    private String name;

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    /**
     * 应用的大小
     */
    private long appSize;
    /**
     * 包的名称
     */
    private String pakageName;
    /**
     * 是否是用户应用
     * true 用户
     * false 系统
     */
    private boolean isUserApp;

    public SoftManangerInfo(Drawable icon, String name, long appSize, String pakageName, boolean isUserApp, boolean isRom) {
        this.icon = icon;
        this.name = name;
        this.appSize = appSize;
        this.pakageName = pakageName;
        this.isUserApp = isUserApp;
        this.isRom = isRom;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    @Override
    public String toString() {
        return "SoftManangerInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", appSize=" + appSize +
                ", pakageName='" + pakageName + '\'' +
                ", isUserApp=" + isUserApp +
                ", isRom=" + isRom +
                '}';
    }

    /**
     * 是否在手机内存
     * true 手机内存
     * false Sdcard
     */
    private boolean isRom;

    public SoftManangerInfo() {
    }


    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPakageName() {
        return pakageName;
    }

    public void setPakageName(String pakageName) {
        this.pakageName = pakageName;
    }


    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

}
