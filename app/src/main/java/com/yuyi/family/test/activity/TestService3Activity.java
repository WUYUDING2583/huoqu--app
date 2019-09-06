package com.yuyi.family.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yuyi.family.R;
import com.yuyi.family.test.service.TestService3;
import com.yuyi.family.test.service.TestService4;

public class TestService3Activity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service3);

        Intent it1=new Intent(this,TestService3.class);
        Bundle b1=new Bundle();
        b1.putString("param","s1");
        it1.putExtras(b1);

        Intent it2=new Intent(this,TestService3.class);
        Bundle b2=new Bundle();
        b2.putString("param","s2");
        it2.putExtras(b2);

        Intent it3=new Intent(this,TestService3.class);
        Bundle b3=new Bundle();
        b3.putString("param","s3");
        it3.putExtras(b3);

        Intent it4=new Intent(this, TestService4.class);

        //接着启动多次IntentService,每次启动,都会新建一个工作线程
        //但始终只有一个IntentService实例
        startService(it1);
        startService(it2);
        startService(it3);
        startService(it4);
    }
}
