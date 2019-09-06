package com.yuyi.family.test.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context,"系统开机",Toast.LENGTH_LONG).show();
    }
}
