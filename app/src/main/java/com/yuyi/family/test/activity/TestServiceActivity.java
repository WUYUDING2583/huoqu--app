package com.yuyi.family.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yuyi.family.R;
import com.yuyi.family.test.service.TestService1;

public class TestServiceActivity extends AppCompatActivity{

    private Button start,stop;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service1);

        start=(Button)findViewById(R.id.start);
        stop=(Button)findViewById(R.id.stop);

        //创建启动Service的Intent,以及Intent属性
        //调用service需得显示调用如下所示，设置包名无用
        final Intent intent = new Intent(this, TestService1.class);
//        intent.setAction("com.yuyi.family.test.testservice1");
//        intent.setPackage("com.yuyi.family.test.service");
        //为两个按钮设置点击事件,分别是启动与停止service
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click start");
                startService(intent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click stop");
                stopService(intent);

            }
        });
    }

}
