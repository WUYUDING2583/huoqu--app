package com.yuyi.family.test.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TestService1 extends Service {

    private final String TAG="TestService1";

    @Override
    public IBinder onBind(Intent intent){
        System.out.println("onBind方法被调用");
        return null;
    }

    @Override
    public void onCreate(){
        System.out.println("onCreate方法被调用");
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        System.out.println("onStartCommand方法被调用");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        System.out.println("onDestroy方法被调用");
        super.onDestroy();
    }
}
