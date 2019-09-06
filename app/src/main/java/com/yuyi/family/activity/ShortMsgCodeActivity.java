package com.yuyi.family.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyi.family.R;
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.FileUtil;
import com.yuyi.family.pojo.CommonData;
import com.yuyi.family.pojo.User;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.StringUtil;
import com.yuyi.family.common.util.JSONUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortMsgCodeActivity extends AppCompatActivity implements Chronometer.OnChronometerTickListener{

    private String phoneNum,sessionId;
    private List<String> codeList=new ArrayList<>();
    private List<EditText> code=new ArrayList<>();
    private TextView send_phone_num,resend,code_1,code_2;
    private Chronometer chronometer1;
    private LinearLayout resendLayout;
    private Context context;
    private int time=60;
    private DBAdapter dbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_msg_code);
        context=ShortMsgCodeActivity.this;
        Intent intent=getIntent();
        Bundle bd=intent.getExtras();
        phoneNum=bd.getString("phoneNum");
        sessionId=bd.getString("sessionId");
        //开启数据库
        dbAdapter=new DBAdapter(context);
        dbAdapter.open();
        bindView();
        init();
    }

    private void bindView(){
        code.add((EditText)findViewById(R.id.code_one));
        code.add((EditText)findViewById(R.id.code_two));
        code.add((EditText)findViewById(R.id.code_three));
        code.add((EditText)findViewById(R.id.code_four));
        send_phone_num=(TextView)findViewById(R.id.send_phone_num);
        chronometer1=(Chronometer)findViewById(R.id.chronometer);
        resendLayout=(LinearLayout)findViewById(R.id.code_resend);
        code_1=(TextView)findViewById(R.id.code_1);
        code_2=(TextView)findViewById(R.id.code_2);
        resend=(TextView)findViewById(R.id.code_resend_text);
        resend.setVisibility(View.GONE);


        chronometer1.setOnChronometerTickListener(this);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend.setVisibility(View.GONE);
                code_1.setVisibility(View.VISIBLE);
                code_2.setVisibility(View.VISIBLE);
                chronometer1.setVisibility(View.VISIBLE);
                chronometer1.start();
                Map<String,String> out=new HashMap<>();
                out.put("phone",phoneNum);
                out.put("sessionId",sessionId);
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.SENDMSG, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response!=null) {
                                    System.out.println(response);
                                    User user= (User)JSONUtil.JsonToObject(response,User.class);
                                    if(!user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                        Toast.makeText(context,user.getRetMessage(),Toast.LENGTH_LONG).show();
                                    }else{
                                        CommonData data=(CommonData )JSONUtil.JsonToObject(response,CommonData.class);
                                        sessionId=data.getSessionId();
                                        Toast.makeText(context,"短信已重新发送",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(context,"由于某种原因，短信没有发送成功",Toast.LENGTH_LONG).show();
                            }
                        },out);
            }
        });

        for(int index=0;index<code.size();index++){
            final int i=index;
            if(index!=0){
                code.get(index).setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL
                                && event.getAction() == KeyEvent.ACTION_DOWN) {
                            codeList.remove(codeList.size()-1);
                            System.out.println(codeList);
                            String currentCode=code.get(i).getText().toString();
                            if(StringUtil.isNotEmpty(currentCode)){

                            }else {
                                code.get(i - 1).requestFocus();
                                code.get(i - 1).setText("");
                            }
                        }
                        return false;
                    }
                });
            }
            if(index==code.size()-1){
                code.get(index).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String singleCode=code.get(i).getText().toString();
                        if(!StringUtil.isNotEmpty(singleCode)){
                            return;
                        }
                        codeList.add(singleCode);
                        Map<String,String> out=new HashMap<>();
                        out.put("phone",phoneNum);
                        String codeNum="";
                        for(String code:codeList){
                            codeNum+=code;
                        }
                        out.put("code",codeNum);
                        out.put("sessionId",sessionId);
                        HttpUtil.getInstance()
                                .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.VERIFYSMS, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println("in short msg verify");
                                        System.out.println(response);
                                        User user=(User) JSONUtil.JsonToObject(response,User.class);
                                        if(user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                            if(user.isRegister()){
                                                dbAdapter.insertUser(user);
                                                Intent intent=new Intent(context, DrawerActivity.class);
                                                ApplicationUtil.setCurrentUser(user);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }else{
                                                System.out.println("not register");
                                                Intent intent=new Intent(context, RegisterActivity.class);
                                                intent.putExtra("phone",phoneNum);
                                                startActivity(intent);
                                            }
                                        }else{
                                            Toast.makeText(context,user.getRetMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }}, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("verify error");
                                        System.out.println(error.getMessage());
                                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }},out);
                    }
                });
            }else{
                code.get(index).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String singleCode = code.get(i).getText().toString();
                        if(StringUtil.isNotEmpty(singleCode)) {
                            codeList.add(singleCode);
                            code.get(i + 1).requestFocus();
                        }
                    }
                });
            }
        }
    }

    private void init(){
        send_phone_num.setText(phoneNum);
        chronometer1.setText(time+"");
        chronometer1.start();

    }

    @Override
    public void onChronometerTick(Chronometer chronometer){
        time--;
        chronometer.setText(time+"");
        if(time<=0){
            time=60;
            chronometer.stop();
            code_1.setVisibility(View.GONE);
            code_2.setVisibility(View.GONE);
            chronometer.setVisibility(View.GONE);
            resend.setVisibility(View.VISIBLE);
        }
    }
}
