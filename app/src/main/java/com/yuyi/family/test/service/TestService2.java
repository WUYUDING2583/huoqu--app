package com.yuyi.family.test.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TestService2 extends Service {
    private final String TAG="TestService2";
    private int count;
    private boolean quit;

    private MyBinder binder=new MyBinder();
    public class MyBinder extends Binder{
        public int getCount(){
            return count;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        System.out.println("onBind方法被调用");
        return binder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("onCreate方法被调用");
        new Thread(){
            public void run (){
                while(!quit){
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        }.start();
    }

    @Override
    public boolean onUnbind(Intent intent){
        System.out.println("onUnbind方法被调用");
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.quit=true;
        System.out.println("onDestroy方法被调用");
    }

    @Override
    public void onRebind(Intent intent){
        System.out.println("onRebind方法被调用");
        super.onRebind(intent);
    }
}
