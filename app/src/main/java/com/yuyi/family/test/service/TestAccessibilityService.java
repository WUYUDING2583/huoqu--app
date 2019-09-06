package com.yuyi.family.test.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.yuyi.family.R;
import com.yuyi.family.test.activity.TestAccessbilityServiceActivity;

import java.util.ArrayList;
import java.util.List;

public class TestAccessibilityService extends AccessibilityService {

    private static final String TAG = "TestAccessibilityService";
    List<AccessibilityNodeInfo> pageRoot=new ArrayList<AccessibilityNodeInfo>() ;
    List<AccessibilityNodeInfo> pageRootView ;
    int index=0,oldIndex,timer=0;
    private boolean hasReadList=false;//判断是否已经读取当前页面的items
    private AccessibilityNodeInfo article;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        Intent nfIntent = new Intent(this,TestAccessbilityServiceActivity.class);
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext())
                .setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("正在运行...");// 设置上下文内容
//                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(1, notification);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onServiceConnected(){
        super.onServiceConnected();
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        //配置监听的事件类型为界面变化|点击事件
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED|AccessibilityEvent.TYPE_VIEW_SCROLLED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16) {
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        }
        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {//界面变化事件
                AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();//界面的根节点
                if (rootNodeInfo == null) {
                    return;
                }
                System.out.println("界面变化");
                pageRoot = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/a4h");
                if (pageRoot.size() != 0) {
                    System.out.println("在首页");
                    if(hasReadList){
                        pageRootView= rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/s_");
                        System.out.println("获取首页根节点");
                        System.out.println("size of pageRootView:"+pageRootView.size());
                        if(pageRootView!=null&&pageRootView.size()>0) {
                            hasReadList=false;
//                            pageRootView.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/lf").get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            sleep(3000);
                        }
                    }
                    List<AccessibilityNodeInfo> items = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/aga");
                    if (items != null && items.size() > 0) {
                        for (; index < items.size(); index++) {
                            System.out.println(index + " " + items.get(index).getParent().getParent().getParent().getChild(0).getText());
//                            if(items.get(index).getParent().getParent().getParent().findAccessibilityNodeInfosByText("小视频")!=null&&
//                                    items.get(index).getParent().getParent().getParent().findAccessibilityNodeInfosByText("小视频").size()>0){
//                                System.out.println("跳过小视频");
//                                continue;
//                            }
                            items.get(index).getParent().getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            //此处停顿500ms避免循环进入下一个
                           sleep(500);
                        }
                        hasReadList=true;
                        index=0;
                        //sleep(3000);
                    }
                }else {
                    pageRootView=null;
                    article=null;
                    List<AccessibilityNodeInfo> articles = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/b0a");
                    List<AccessibilityNodeInfo> images = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/ix");
                    List<AccessibilityNodeInfo> video = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/ax");
                    if(video!=null&&video.size()!=0){
                        System.out.println("在视频");
                        AccessibilityNodeInfo back= rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/p_").get(0);
                        try{
                            Thread.sleep(40*1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        back.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else if (articles != null && articles.size() > 0) {
                        System.out.println("在文章页面");
                        article=articles.get(0).getChild(0).getChild(0);
                            while (timer < 30) {
                                try {
                                    Thread.sleep(5000);
                                    article.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                    timer += 5;
                                    System.out.println("timer:" + timer);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            timer = 0;

                       System.out.println("文章返回");
                        rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/gu").get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        sleep(800);
                    }else if(images!=null&&images.size()>0){
                        System.out.println("在图片");
                        rootNodeInfo.findAccessibilityNodeInfosByViewId("com.jifen.qukan:id/iy").get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void sleep(int time){
        try{
            Thread.sleep(time);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {
        System.out.println("服务中断");
    }

    @Override
    public void onDestroy(){
        System.out.println("service销毁");
        stopForeground(true);
        Intent intent=new Intent("android.accessibilityservice.AccessibilityService");
        startService(intent);
        super.onDestroy();
    }
}
