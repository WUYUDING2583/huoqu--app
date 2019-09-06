package com.yuyi.family.test.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.yuyi.family.R;
import com.yuyi.family.activity.RegisterActivity;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.component.BottomDialog;
import com.yuyi.family.component.SelectTimeDialog;
import com.yuyi.family.listener.interfaces.OnQueryInParticularTimeListener;
import com.yuyi.family.test.adapter.SelectTimeAdapter;

import lombok.Data;


public class TestActivity extends AppCompatActivity{

    private Button button,button1;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        context=TestActivity.this;

        button=(Button)findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SelectTimeDialog().show(getSupportFragmentManager(),"tag");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        SelectTimeDialog.setOnQueryInParticularTimeListener(new OnQueryInParticularTimeListener() {
            @Override
            public void query(String date) {
                date+=" 00:00:00";
                ToastUtil.showToast(date,context);
            }

            @Override
            public void query(String date, String time, boolean isEnd) {
                String t=date+" "+time;
                ToastUtil.showToast(t,context);
            }

            @Override
            public void query(String date, String startTime, String endTime) {
                String t1=date+ " "+startTime;
                String t2=date+" "+endTime;
                ToastUtil.showToast(t1+"\n"+t2,context);
            }
        });
    }
}
