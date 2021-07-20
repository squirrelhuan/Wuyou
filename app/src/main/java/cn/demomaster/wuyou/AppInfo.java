package cn.demomaster.wuyou;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private String packageName; //包名
    private Drawable ico;       //图标
    private String Name;        //应用标签
    private Intent intent;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity

    private String processName;
    private boolean isSystem = true;
    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIco() {
        return ico;
    }

    public void setIco(Drawable ico) {
        this.ico = ico;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}