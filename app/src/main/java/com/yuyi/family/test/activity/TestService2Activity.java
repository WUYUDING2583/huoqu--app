package com.yuyi.family.test.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yuyi.family.R;
import com.yuyi.family.test.service.TestService2;

public class TestService2Activity extends AppCompatActivity {

    private Button bind,cancel,status;

    TestService2.MyBinder binder;
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("------Service Connected-------");
            binder = (TestService2.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("------Service DisConnected-------");
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service2);
        bind=(Button)findViewById(R.id.btnbind);
        cancel=(Button)findViewById(R.id.btncancel);
        status=(Button)findViewById(R.id.btnstatus);
        final Intent intent=new Intent(this, TestService2.class);
        intent.setAction("com.yuyi.family.test.testservice2");
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(intent,conn, Service.BIND_AUTO_CREATE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(conn);
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestService2Activity.this,"Service的count值为:"+binder.getCount(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
