package cn.demomaster.wuyou;

import cn.demomaster.huan.quickdeveloplibrary.QDApplication;
import cn.demomaster.huan.quickdeveloplibrary.helper.hook.HandlerHooker;
import cn.demomaster.qdlogger_library.QDLogger;

public class Application extends QDApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        QDLogger.init(this, "/wuyou/");
        //解锁系统隐藏api限制权限以及hook Instrumentation
        HandlerHooker.doHook(this);
    }
}
