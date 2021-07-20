package cn.demomaster.wuyou;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.demomaster.huan.quickdeveloplibrary.base.activity.QDActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.BatteryOptimizationsHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.QDSharedPreferences;
import cn.demomaster.huan.quickdeveloplibrary.helper.QdThreadHelper;
import cn.demomaster.huan.quickdeveloplibrary.service.AccessibilityHelper;
import cn.demomaster.huan.quickdeveloplibrary.service.QDAccessibilityService;
import cn.demomaster.huan.quickdeveloplibrary.util.QDFileUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.floatview.FloatingMenuService;
import cn.demomaster.huan.quickdeveloplibrary.view.floatview.HierarchyFlotingService;
import cn.demomaster.huan.quickdeveloplibrary.view.floatview.ServiceHelper;
import cn.demomaster.huan.quickdeveloplibrary.widget.button.ToggleButton;
import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.quickpermission_library.PermissionHelper;
import cn.demomaster.quicksticker_annotations.BindView;
import cn.demomaster.quicksticker_annotations.QuickStickerBinder;
import cn.demomaster.wuyou.adapter.RecycleAdapter;
import cn.demomaster.wuyou.model.AdsModel;

public class MainActivity extends QDActivity {
    RecyclerView recyDrag;
    List<AppInfo> appInfos;
    List<AppConfig> appConfigs;
    private LinearLayoutManager linearLayoutManager;
    private RecycleAdapter adapter;
    ToggleButton toggleButton_state, toggleButton_hierarchy, toggleButton_floating_menu;
    CheckBox cbox_ads, cbox_hierarchy, cbox_menu;
    Button btn_window_open, btn_log, btn_ads_record;

    @BindView(R.id.btn_floating_05)
    Button btn_floating_05;
    @BindView(R.id.btn_floating_06)
    Button btn_floating_06;
    String HIERARCHY_ENABLE_SP = "HIERARCHY_ENABLE_SP";
    String FLOATING_MENU_ENABLE_SP = "FLOATING_MENU_ENABLE_SP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuickStickerBinder.getInstance().bind(this);
        cbox_menu = findViewById(R.id.cbox_menu);
        cbox_menu.setChecked(QDSharedPreferences.getInstance().getBoolean(FLOATING_MENU_ENABLE_SP, false));
        if (cbox_menu.isChecked()) {
            openFloatMenu();
        }
        cbox_menu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QDSharedPreferences.getInstance().putBoolean(FLOATING_MENU_ENABLE_SP, isChecked);
                if (isChecked) {
                    openFloatMenu();
                } else {
                    ServiceHelper.dissmissWindow(FloatingMenuService.class);
                }
            }
        });

        cbox_hierarchy = findViewById(R.id.cbox_hierarchy);
        cbox_hierarchy.setChecked(QDSharedPreferences.getInstance().getBoolean(HIERARCHY_ENABLE_SP, false));
        cbox_hierarchy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QDSharedPreferences.getInstance().putBoolean(HIERARCHY_ENABLE_SP, isChecked);
                if (isChecked) {
                    if (AccessibilityHelper.getService() != null) {
                        ServiceHelper.showWindow(mContext, HierarchyFlotingService.class);
                    } else if (!AccessibilityHelper.isEnable(mContext, QDAccessibilityService.class)) {
                        //跳转系统自带界面 辅助功能界面
                        QDAccessibilityService.startSettintActivity(mContext);
                    }
                } else {
                    ServiceHelper.dissmissWindow(HierarchyFlotingService.class);
                }
            }
        });
        cbox_ads = findViewById(R.id.cbox_ads);
        cbox_ads.setChecked(AccessibilityHelper.isEnable(mContext, QDAccessibilityService.class));
        cbox_ads.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!AccessibilityHelper.isEnable(mContext, QDAccessibilityService.class)) {
                        //跳转系统自带界面 辅助功能界面
                        QDAccessibilityService.startSettintActivity(mContext);
                    }
                }
            }
        });

        btn_ads_record = findViewById(R.id.btn_ads_record);
        btn_ads_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AdsActivity.class);
            }
        });
        btn_log = findViewById(R.id.btn_log);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LogActivity.class);
            }
        });
        btn_window_open = findViewById(R.id.btn_window_open);
        btn_window_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.getInstance().requestPermission(mContext, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS, Manifest.permission.SYSTEM_ALERT_WINDOW}, new PermissionHelper.PermissionListener() {
                    @Override
                    public void onPassed() {
                        btn_window_open.setVisibility(View.GONE);
                    }

                    @Override
                    public void onRefused() {
                        Toast.makeText(mContext, "拒绝", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btn_floating_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    BatteryOptimizationsHelper.requestIgnoreBatteryOptimizations(MainActivity.this);
                }
            }
        });
        btn_floating_06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryOptimizationsHelper.requestAutoStartService(MainActivity.this);
            }
        });

        AccessibilityHelper.registerAccessibilityEventListener(1024, new QDAccessibilityService.OnAccessibilityListener() {
            @Override
            public void onServiceConnected(QDAccessibilityService qdAccessibilityService) {
                PermissionHelper.getInstance().requestPermission(mContext, new String[]{
                        Manifest.permission.SYSTEM_ALERT_WINDOW}, new PermissionHelper.PermissionListener() {
                    @Override
                    public void onPassed() {
                        if (QDSharedPreferences.getInstance().getBoolean(HIERARCHY_ENABLE_SP, false)) {
                            ServiceHelper.showWindow(mContext, HierarchyFlotingService.class);
                        }
                    }

                    @Override
                    public void onRefused() {
                    }
                });
            }

            @Override
            public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent event) {
                dealViewChanged(accessibilityService, event);
            }

            @Override
            public void onServiceDestroy() {
                ServiceHelper.dissmissWindow(HierarchyFlotingService.class);
            }
        });

        initConfig();
        QDLogger.e("appConfigs:" + appConfigs.size());
        appInfos = GetAppList(this);
        recyDrag = findViewById(R.id.recy_drag);
        //这里使用线性布局像listview那样展示列表,第二个参数可以改为 HORIZONTAL实现水平展示
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        //使用网格布局展示
        //recyDrag.setLayoutManager(new GridLayoutManager(this, 4));
        recyDrag.setLayoutManager(linearLayoutManager);
        adapter = new RecycleAdapter(this, appInfos, appConfigs);
        //设置分割线使用的divider
        recyDrag.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                //startApp(appInfos.get(position).getPackageName());
            }

            @Override
            public void onLongClick(View v, int position) {
                // if (!appInfos.get(position).isSystem())
                //showMenu(v, appInfos.get(position).getPackageName());
            }
        });
        recyDrag.setAdapter(adapter);
    }

    private void initConfig() {
        appConfigs = new ArrayList<>();
       /* String str1 = JSON.toJSONString(appConfigs);
        QDLogger.e(str1);*/
        String str = QDFileUtil.getFromAssets(this, "config/apps");

        String str1 = QDSharedPreferences.getInstance().getString(accessibilityConfig, str);
        List<AppConfig> appConfigs1 = JSON.parseArray(str1, AppConfig.class);
        List<AppConfig> appConfigs2 = JSON.parseArray(str, AppConfig.class);
        LinkedHashMap<String, AppConfig> linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < appConfigs1.size(); i++) {
            linkedHashMap.put(appConfigs1.get(i).toString(), appConfigs1.get(i));
        }
        for (int i = 0; i < appConfigs2.size(); i++) {
            linkedHashMap.put(appConfigs2.get(i).toString(), appConfigs2.get(i));
        }

        //TODO
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            appConfigs.add((AppConfig) entry.getValue());
        }
    }

    private void addConfig() {
    }

    private void openFloatMenu() {
        PermissionHelper.getInstance().requestPermission(mContext, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, new PermissionHelper.PermissionListener() {
            @Override
            public void onPassed() {
                ServiceHelper.showWindow(mContext, FloatingMenuService.class);
            }

            @Override
            public void onRefused() {
                Toast.makeText(MainActivity.this, "onRefused", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean ok = PermissionHelper.getInstance().getPermissionStatus(mContext, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW});
        if (ok) {
            btn_window_open.setVisibility(View.GONE);
        } else {
            btn_window_open.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取app目录
     *
     * @param context
     * @return
     */
    public static List<AppInfo> GetAppList(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : activities) {
            String packName = info.activityInfo.packageName;
            if (packName.equals(context.getPackageName())) {
                continue;
            }
            AppInfo mInfo = new AppInfo();
            // mInfo.setIco(info.activityInfo.applicationInfo.loadIcon(pm));
            // mInfo.setName(info.activityInfo.applicationInfo.loadLabel(pm).toString());
            //info.activityInfo.applicationInfo.category
            mInfo.setProcessName(info.activityInfo.processName);
            mInfo.setName(info.loadLabel(pm).toString());
            mInfo.setIco(info.loadIcon(pm));
            mInfo.setPackageName(packName);
            //判断是否为非系统预装的应用程序
            if ((info.activityInfo.applicationInfo.flags & info.activityInfo.applicationInfo.FLAG_SYSTEM) <= 0) {
                mInfo.setSystem(false);
            }
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(packName,
                    info.activityInfo.name));
            launchIntent.setFlags(info.activityInfo.flags);
            mInfo.setIntent(launchIntent);
            list.add(mInfo);
        }
        return list;
    }

    String activityName;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void dealViewChanged(AccessibilityService accessibilityService, AccessibilityEvent event) {
        int eventType = event.getEventType();

        String packageName = event.getPackageName() == null ? "" : event.getPackageName().toString();
        String windowName = event.getClassName() == null ? "" : event.getClassName().toString();
        WechatPlugin.dealViewChanged(accessibilityService,event);
        if(eventType==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        if (packageName.equalsIgnoreCase("com.trustin.mobile.mobile_agent")||packageName.equalsIgnoreCase(this.getPackageName())) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = AccessibilityHelper.findAccessibilityNodeInfosByViewClass(accessibilityService.getRootInActiveWindow(), "android.widget.EditText");
            if (accessibilityNodeInfoList != null) {
                Log.e(TAG, "找到输入框控件：" + accessibilityNodeInfoList.size());
                if (accessibilityNodeInfoList.size() > 0) {
                    //accessibilityNodeInfoList.get(0).setText("1");
                    autoInput(this,accessibilityNodeInfoList.get(0),"11134");
                }
            }
        }

        //activityName = getCurrentActivityName(mContext);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (windowName.startsWith(packageName)) {
                    activityName = windowName;
                }
                Log.e(TAG, "窗口切换:" + activityName);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return;
        }
        if (event.getPackageName() != null) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            String idName = (nodeInfo != null) ? nodeInfo.getViewIdResourceName() : "";
            Log.i(TAG, "应用名：" + getAppName(packageName) + ",包名：" + packageName + ",页面：" + windowName + ",id:" + idName + ",eventType=" + eventType);
            if (nodeInfo != null) {
                List<AccessibilityNodeInfo> views = null;
                List<AppConfig> appConfigs = getAppConfigList(packageName, activityName);
                W:
                for (AppConfig config : appConfigs) {
                    views = findAccessibilityNodeInfosByConfig(accessibilityService.getRootInActiveWindow(), config);
                    if (views != null) {
                        if (!TextUtils.isEmpty(config.getClassName())) {
                            views = removeClassName(views, config.getClassName());
                        }
                        break W;
                    }
                }
                boolean b = false;
                if (views != null) {
                    nodeInfo.recycle();
                    clickButton(event, views);
                    b = true;
                }
                CharSequence text = nodeInfo.getText();
                if (!b && !TextUtils.isEmpty(text) && nodeInfo.getText().toString().contains("跳过")) {
                    addAds(packageName,activityName, nodeInfo);
                }
            }
        }

        /*switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                String text1 = event.getText().toString();
                Log.e(TAG, event.getClassName()+",TYPE_VIEW_SELECTED="+text1);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String text = event.getText().toString();
                Log.e(TAG, event.getClassName()+","+text);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.i(TAG, "捕获到点击事件:"+event.getPackageName()+"."+event.getText());
               *//* AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    // 查找text为Test!的控件
                    List<AccessibilityNodeInfo> button = nodeInfo.findAccessibilityNodeInfosByText("Test!");
                    nodeInfo.recycle();
                    for (AccessibilityNodeInfo item : button) {
                        Log.i(TAG, "long-click button!");
                        // 执行长按操作
                        item.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                    }
                }*//*
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //获取类名
                String name = event.getClassName().toString();
                //com.ss.android.article.news
                if(!TextUtils.isEmpty(name)&&name.equals("com.tencent.qqmusic.activity.AppStarterActivity")){
                    Log.e(TAG, "qq音乐");
                    AccessibilityNodeInfo nodeInfo = accessibilityService.getRootInActiveWindow();
                    QDAccessibilityService. parseNodeInfo(accessibilityService);
                    if (nodeInfo != null) {
                        // 查找text为Test!的控件
                        List<AccessibilityNodeInfo> button = nodeInfo.findAccessibilityNodeInfosByText("跳过");
                        nodeInfo.recycle();
                        for (AccessibilityNodeInfo item : button) {
                            Log.i(TAG, "点击");
                            // 执行长按操作
                            item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                       *//* for (AccessibilityNodeInfo item : button) {
                            Log.i(TAG, "long-click button!");
                            // 执行长按操作
                            item.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                        }*//*
                    }
                }else {
                    Log.e(TAG, "窗口切换" + name);
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                Log.e(TAG, "文字改变" + event.getClassName());
                break;
        }*/
    }

    private void autoInput(Context context, AccessibilityNodeInfo nodeInfo, String text) {
        if(nodeInfo==null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android 5.0 版本及以上：
            QdThreadHelper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "android");
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    boolean b = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    Log.e("auto","自动输入:"+text+","+b);
                    //nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//Android 4.3 版本及以上：
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);
                CharSequence txt = nodeInfo.getText();
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, text.length());
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            }
    }

    private AccessibilityNodeInfo getRootNode(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.getParent() != null) {
            return getRootNode(nodeInfo.getParent());
        } else {
            return nodeInfo;
        }
    }

    public static String getCurrentActivityName(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
      /*  for(ActivityManager.RunningTaskInfo runningTaskInfo : taskInfo){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                String topActivity = runningTaskInfo.topActivity.getClassName();
               Log.e(TAG, "topActivity=" + topActivity);
               //return topActivity;
            }
        }*/
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getClassName();
    }

    private String getAppName(String packageName) {
        for (AppInfo appInfo : appInfos) {
            if (appInfo.getPackageName().equals(packageName)) {
                return appInfo.getName();
            }
        }
        return "";
    }

    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByConfig(AccessibilityNodeInfo nodeInfo, AppConfig config) {
        List<AccessibilityNodeInfo> views;
        if (TextUtils.isEmpty(config.getActivity()) || !activityName.equals(config.getActivity())) {
            return null;
        }
        if (!TextUtils.isEmpty(config.getId()) && !config.getId().equals("null")) {
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = findAccessibilityNodeInfosByViewId(nodeInfo, config.getId());
            if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() > 0) {
                return accessibilityNodeInfoList;
            }
            //Log.e(TAG, "自身id:" + nodeInfo.getViewIdResourceName());
        }
        if (!TextUtils.isEmpty(config.getText())) {
            return nodeInfo.findAccessibilityNodeInfosByText(config.getText());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfo nodeInfo, String id) {
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (id.equals(nodeInfo.getViewIdResourceName())) {
            if (nodeInfoList == null) {
                nodeInfoList = new ArrayList<>();
            }
            nodeInfoList.add(nodeInfo);
        }
        return nodeInfoList;
    }

    /**
     * 根据包名和activity名筛选，配置文件
     *
     * @param packageName
     * @param activityName
     * @return
     */
    private List<AppConfig> getAppConfigList(String packageName, String activityName) {
        List<AppConfig> appConfigList = new ArrayList<>();
        for (AppConfig config : appConfigs) {
            if (config.getPackageName().equals(packageName)) {
                if (TextUtils.isEmpty(activityName)) {
                    if (!TextUtils.isEmpty(config.getActivity()) && config.getActivity().equals(activityName)) {
                        appConfigList.add(config);
                    }
                } else {
                    appConfigList.add(config);
                }
            }
        }
        return appConfigList;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private List<AccessibilityNodeInfo> removeClassName(List<AccessibilityNodeInfo> views, String className) {
        for (AccessibilityNodeInfo accessibilityNodeInfo : views) {
            if (TextUtils.isEmpty(accessibilityNodeInfo.getClassName()) || !className.equals(accessibilityNodeInfo.getClassName())) {
                views.remove(accessibilityNodeInfo);
                return removeClassName(views, className);
            }
        }
        return views;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private List<AccessibilityNodeInfo> removeNullId(List<AccessibilityNodeInfo> views) {
        for (AccessibilityNodeInfo accessibilityNodeInfo : views) {
            if (!TextUtils.isEmpty(accessibilityNodeInfo.getViewIdResourceName())) {
                views.remove(accessibilityNodeInfo);
                return removeNullId(views);
            }
        }
        return views;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clickButton(AccessibilityEvent event, List<AccessibilityNodeInfo> button) {
        if (button != null) {
            for (AccessibilityNodeInfo item : button) {
                String name2 = event.getClassName().toString();
                //Log.i(TAG,  "eventType="+eventType+",packages:" + item.getPackageName() + ",name:" + item.getClassName()+",text:"+event.getText());
                Log.i(TAG, "找到控件[" + item.getText() + "]:" + item.getViewIdResourceName() + ",enable:" + item.isEnabled() + ",isClickable:" + item.isClickable() + ",类型:" + name2);
                AccessibilityNodeInfo nodeInfo = findClickableView(item);
                // 执行长按操作
                if (nodeInfo != null) {
                    addLog(nodeInfo);
                    Log.i(TAG, "点击[" + item.getText() + "]:" + item.getViewIdResourceName());
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    Log.i(TAG, "未找到可点击按钮");
                }
            }
        }
    }

    public static String accessibilityLog = "accessibilityLog";
    public static String accessibilityAds = "accessibilityAds";
    public static String accessibilityConfig = "accessibilityConfig";

    /**
     * @param nodeInfo
     */
    private void addLog(AccessibilityNodeInfo nodeInfo) {
        String str = QDSharedPreferences.getInstance().getString(accessibilityLog, null);
        List<LogBean> logBeans;
        if (TextUtils.isEmpty(str)) {
            logBeans = new ArrayList<>();
        } else {
            logBeans = JSON.parseArray(str, LogBean.class);
        }
        LogBean logBean = new LogBean();
        logBean.setAppName(getAppName(nodeInfo.getPackageName().toString()));
        logBean.setTime(System.currentTimeMillis());
        logBeans.add(logBean);
        QDSharedPreferences.getInstance().putString(accessibilityLog, JSON.toJSONString(logBeans));
    }

    /**
     * @param packageName
     * @param activityName
     * @param text
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void addAds(String packageName, String activityName, AccessibilityNodeInfo nodeInfo) {// AccessibilityNodeInfo nodeInfo
        String str = QDSharedPreferences.getInstance().getString(accessibilityAds, null);
        List<AdsModel> adsModels;
        if (TextUtils.isEmpty(str)) {
            adsModels = new ArrayList<>();
        } else {
            adsModels = JSON.parseArray(str, AdsModel.class);
        }
        AdsModel adsModel = new AdsModel();
        adsModel.setPackageName(packageName);
        adsModel.setAppName(getAppName(packageName));
        adsModel.setActivityName(activityName);
        adsModel.setText(nodeInfo.getText().toString());
        adsModel.setId(nodeInfo.getViewIdResourceName());
        adsModel.setTime(System.currentTimeMillis());
        QDLogger.i("疑似广告:"+JSON.toJSONString(adsModel));
        adsModels.add(adsModel);
        QDSharedPreferences.getInstance().putString(accessibilityAds, JSON.toJSONString(adsModels));
    }

    private AccessibilityNodeInfo findClickableView(AccessibilityNodeInfo item) {
        if (item == null) {
            return null;
        }
        if (item.isClickable() && item.isEnabled()) {
            return item;
        } else {
            AccessibilityNodeInfo nodeInfo = item.getParent();
            if (nodeInfo != null) {
                if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                    return nodeInfo;
                } else {
                    return findClickableView(nodeInfo.getParent());
                }
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuickStickerBinder.getInstance().unBind(this);
    }
}