package com.yuyi.family.test.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuyi.family.test.service.LongRunningService;

public class AlarmReceiver extends BroadcastReceiver {

    private final String ACTION_BOOT="com.yuyi.family.test.longrunningservice";
    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals(ACTION_BOOT)) {
            System.out.println("receive broadcast");
            System.out.println("send intent to start service");
            Intent i = new Intent(context, LongRunningService.class);
            context.startService(i);
        }
    }
}
