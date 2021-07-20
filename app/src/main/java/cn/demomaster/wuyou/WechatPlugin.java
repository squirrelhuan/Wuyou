package cn.demomaster.wuyou;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.MainThread;

import java.util.List;

import cn.demomaster.huan.quickdeveloplibrary.helper.QdThreadHelper;
import cn.demomaster.huan.quickdeveloplibrary.service.AccessibilityHelper;
import cn.demomaster.qdlogger_library.QDLogger;

public class WechatPlugin {
    static AccessibilityService mAccessibilityService;
    static AccessibilityEvent mEvent;

    static boolean isStarted;
    static Handler handler = new Handler(Looper.getMainLooper());
    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int eventType = mEvent.getEventType();
            //if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            //com.tencent.mm.ui.chatting.ChattingUI

            List<AccessibilityNodeInfo> userNameNodeInfoList = mAccessibilityService.getRootInActiveWindow().findAccessibilityNodeInfosByText("juanr");
            if (userNameNodeInfoList != null && userNameNodeInfoList.size() > 0) {
                //if (packageName.equalsIgnoreCase("com.tencent.mm")) {
                    List<AccessibilityNodeInfo> accessibilityNodeInfoList = AccessibilityHelper.findAccessibilityNodeInfosByViewClass(mAccessibilityService.getRootInActiveWindow(), "android.widget.EditText");
                    if (accessibilityNodeInfoList != null) {
                        //Log.e(TAG, "找到输入框控件：" + accessibilityNodeInfoList.size());
                        if (accessibilityNodeInfoList.size() > 0) {
                            //accessibilityNodeInfoList.get(0).setText("1");
                            AccessibilityNodeInfo nodeInfo = accessibilityNodeInfoList.get(0);
                            autoInput(mAccessibilityService.getApplicationContext(), nodeInfo, "不来是吧");
                            List<AccessibilityNodeInfo> accessibilityNodeInfoList1 = mAccessibilityService.getRootInActiveWindow().findAccessibilityNodeInfosByText("发送");
                            if (accessibilityNodeInfoList1 != null && accessibilityNodeInfoList1.size() > 0) {
                                accessibilityNodeInfoList1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
               // }
            }

            QDLogger.d("pak="+packageName);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 3000);
        }
    };
    static String packageName = "";
    static String windowName = "";
    public static void dealViewChanged(AccessibilityService accessibilityService, AccessibilityEvent event) {
        mAccessibilityService = accessibilityService;
        mEvent = event;

        int eventType = event.getEventType();
        packageName = event.getPackageName() == null ? "" : event.getPackageName().toString();
        windowName = event.getClassName() == null ? "" : event.getClassName().toString();
        QDLogger.d("pak="+packageName);
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            //com.tencent.mm.ui.chatting.ChattingUI
            if (packageName.equalsIgnoreCase("com.tencent.mm")) {
                if (!isStarted) {
                    isStarted = true;
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 1000);
                }

               /* List<AccessibilityNodeInfo> accessibilityNodeInfoList = AccessibilityHelper.findAccessibilityNodeInfosByViewClass(mAccessibilityService.getRootInActiveWindow(), "android.widget.EditText");
                if (accessibilityNodeInfoList != null) {
                    //Log.e(TAG, "找到输入框控件：" + accessibilityNodeInfoList.size());
                    if (accessibilityNodeInfoList.size() > 0) {
                        //accessibilityNodeInfoList.get(0).setText("1");
                        AccessibilityNodeInfo nodeInfo = accessibilityNodeInfoList.get(0);
                        autoInput(mAccessibilityService.getApplicationContext(), nodeInfo, "来嘛？");
                        List<AccessibilityNodeInfo> accessibilityNodeInfoList1 = mAccessibilityService.getRootInActiveWindow().findAccessibilityNodeInfosByText("发送");
                        if (accessibilityNodeInfoList1 != null && accessibilityNodeInfoList1.size() > 0) {
                            accessibilityNodeInfoList1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }*/

            } else {
                isStarted = false;
            }
        }
    }

    private static void autoInput(Context context, AccessibilityNodeInfo nodeInfo, String text) {
        if (nodeInfo == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android 5.0 版本及以上：
            QdThreadHelper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    boolean b = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    Log.e("auto", "自动输入:" + text + "," + b);
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

    public static class User {
        String name;
        String[] messages = new String[]{};

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getMessages() {
            return messages;
        }

        public void setMessages(String[] messages) {
            this.messages = messages;
        }
    }
}
