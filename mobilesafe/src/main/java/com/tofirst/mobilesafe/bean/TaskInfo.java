package com.tofirst.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Study on 2015/11/26.
 */
public class TaskInfo {
    private Drawable icon;
    private String appName;
    private String pakageName;
    private boolean isUserApp;
    private long appSize;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "ProgressInfo{" +
                "icon=" + icon +
                ", appName='" + appName + '\'' +
                ", pakageName='" + pakageName + '\'' +
                ", isUserApp=" + isUserApp +
                ", appSize=" + appSize +
                ", isChecked=" + isChecked +
                '}';
    }

    public TaskInfo(Drawable icon, String appName, String pakageName, boolean isUserApp, long appSize, boolean isChecked) {
        this.icon = icon;
        this.appName = appName;
        this.pakageName = pakageName;
        this.isUserApp = isUserApp;
        this.appSize = appSize;
        this.isChecked = isChecked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public TaskInfo() {

    }


}
