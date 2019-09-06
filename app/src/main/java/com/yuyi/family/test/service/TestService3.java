package com.yuyi.family.test.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class TestService3 extends IntentService {

    private final String TAG="IntentService";

    public TestService3(){
        super("TestService3");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        //Intent是从Activity发过来的，携带识别参数，根据参数不同执行不同的任务
        String action=intent.getExtras().getString("param");
        switch (action){
            case "s1":
                System.out.println(TAG+" 启动service1");
                break;
            case "s2":
                System.out.println(TAG+" 启动service2");
                break;
            case "s3":
                System.out.println(TAG+" 启动service3");
                break;
        }
        //让服务休眠2秒
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //重写其他方法,用于查看方法的调用顺序
    @Override
    public IBinder onBind(Intent intent){
        System.out.println(TAG+" onBind");
        return super.onBind(intent);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println(TAG+" onCreate");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        System.out.println(TAG+" onStartCommand");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void setIntentRedelivery(boolean enabled){
        super.setIntentRedelivery(enabled);
        System.out.println(TAG+" setIntentRedelivery");
    }

    @Override
    public void onDestroy(){
        System.out.println(TAG+" onDestroy");
        super.onDestroy();
    }
}
