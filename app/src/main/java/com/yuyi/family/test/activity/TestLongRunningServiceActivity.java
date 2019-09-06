package com.yuyi.family.test.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.yuyi.family.R;
import com.yuyi.family.test.broadcast.AlarmReceiver;

public class TestLongRunningServiceActivity extends AppCompatActivity {

    private LocalBroadcastManager localBroadcastManager;//本地广播
    private AlarmReceiver alarmReceiver;
    private IntentFilter intentFilter;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localBroadcastManager=LocalBroadcastManager.getInstance(this);

        //初始化广播接收者，设置过滤器
        alarmReceiver=new AlarmReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.yuyi.family.test.longrunningservice");
        localBroadcastManager.registerReceiver(alarmReceiver,intentFilter);

        //发送广播
        Intent intent=new Intent("com.yuyi.family.test.longrunningservice");
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(alarmReceiver);
    }
}
