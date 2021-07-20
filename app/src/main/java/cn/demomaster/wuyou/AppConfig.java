package cn.demomaster.wuyou;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppConfig {
    private String appName; //包名
    private String className;//控件类型android.widget.TextView
    private String packageName; //包名
    private String activity;       //图标
    private String text;        //应用标签
    private String id;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", activity='" + activity + '\'' +
                ", text='" + text + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}