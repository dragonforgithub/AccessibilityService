package com.li.sheldon.wechatredpacket;

import android.accessibilityservice.AccessibilityService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class LooterService extends AccessibilityService {
    String TAG="LooterService";
    /* 微信的包名 */
    static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    /* 拆红包类 */
    static final String WECHAT_RECEIVER_CALSS = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    /* 红包详情类 */
    static final String WECHAT_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    /* 微信主界面或者是聊天界面 */
    static final String WECHAT_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
    //該對象代表了整個窗口視圖的快照
    private AccessibilityNodeInfo mAccessNodeInfo = null;
    private static Handler mHandle;
    private static int autoBack=0;
    private Message msg = new Message();

    public LooterService() {
    }

    @Override
    public void onInterrupt() {

    }

    public void setHandler(Handler handler){
        mHandle=handler;
        Log.v(TAG, "mHandle="+mHandle);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mAccessNodeInfo = event.getSource();
        if(mAccessNodeInfo == null){
            Log.e(TAG,"mAccessNodeInfo is NULL!");
            return;
        }

        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            List<AccessibilityNodeInfo> findList = mAccessNodeInfo.findAccessibilityNodeInfosByText("微信红包");
            if(findList.size() > 0){
                AccessibilityNodeInfo curNodeInfo;
                Log.e(TAG,"TYPE_WINDOW_STATE_CHANGED : (find)");
                Log.i(TAG,"findList.size() = "+findList.size());

                //for(int index=1; index < findList.size(); index++){
                    //curNodeInfo = findList.get(findList.size() - index);
                    curNodeInfo = findList.get(findList.size() - 1); //select the last one
                    if(autoBack == 0){
                        curNodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        autoBack++;
                        /*
                        Log.i(TAG,"class name = " + curNodeInfo.getClassName().toString()
                                        +"isEnabled ="+curNodeInfo.isEnabled());
                        if (curNodeInfo.getClassName().equals(WECHAT_RECEIVER_CALSS) && curNodeInfo.isEnabled())
                        {
                            curNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        */
                    }else{
                        autoBack--;
                    }
                //}
            }
        }

        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            List<AccessibilityNodeInfo> openList = mAccessNodeInfo.findAccessibilityNodeInfosByText("金额");
            if(openList.size() > 0){
                Log.e(TAG,"TYPE_WINDOW_STATE_CHANGED : (open)");
                Log.i(TAG,"openList.size() = "+openList.size());
                msg = mHandle.obtainMessage(HandleMsg.MSG_CLICK);
                mHandle.sendMessage(msg);
                /*
                AccessibilityNodeInfo curNodeInfo;
                for(int index=1; index < 3; index++){
                    curNodeInfo = openList.get(openList.size() - 1);
                    curNodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    curNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                */
            }
        }

        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            List<AccessibilityNodeInfo> backList_1 = mAccessNodeInfo.findAccessibilityNodeInfosByText("查看我的红包记录");
            if(backList_1.size() > 0){
                Log.e(TAG,"TYPE_WINDOW_STATE_CHANGED : (back)");
                Log.i(TAG,"backList.size() = "+backList_1.size());
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }

            List<AccessibilityNodeInfo> backList_2 = mAccessNodeInfo.findAccessibilityNodeInfosByText("红包详情");
            if(backList_2.size() > 0){
                Log.e(TAG,"TYPE_WINDOW_STATE_CHANGED : (back)");
                Log.i(TAG,"backList.size() = "+backList_2.size());
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }

            List<AccessibilityNodeInfo> backList_3 = mAccessNodeInfo.findAccessibilityNodeInfosByText("红包派完了");
            if(backList_3.size() > 0){
                Log.e(TAG,"TYPE_WINDOW_STATE_CHANGED : (back)");
                Log.i(TAG,"backList.size() = "+backList_3.size());
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }
    }

}
