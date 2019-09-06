package com.yuyi.family.test.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.Toast;

import com.yuyi.family.MainActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private final String BOOT_COMPLETE="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent){
        if(BOOT_COMPLETE.equals(intent.getAction())){
            System.out.println("系统开机");
            Toast.makeText(context,"启动",Toast.LENGTH_LONG).show();
            Intent i=new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
