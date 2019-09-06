package com.yuyi.family.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyi.family.R;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.PermissionUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.component.EditTextWithDel;
import com.yuyi.family.pojo.User;
import com.yuyi.family.common.util.ActivityCollector;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity{

    private EditTextWithDel phone;
    private Button sendMsg;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=LoginActivity.this;
        //检查定位权限
        PermissionUtil.checkPermission(this, CommonConstant.Permission.PERMISSION_GEOLOCATION);
        //检查相机权限
        PermissionUtil.checkPermission(this, CommonConstant.Permission.PERMISSION_CAMERA);
        bindViews();
    }

    private void bindViews(){
        phone=(EditTextWithDel)findViewById(R.id.login_phone);
        sendMsg=(Button)findViewById(R.id.sendMessage);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNum=phone.getText().toString();
                String pattern="\\d{11}";
                if(!Pattern.matches(pattern, phoneNum)){
                    phone.setShakeAnimation();
                    ToastUtil.showToast("请输入11位手机号",context);
                    return;
                }
                Map<String,String> out=new HashMap<>();
                out.put("phone",phoneNum);
//                Intent intent=new Intent(context,ShortMsgCodeActivity.class);
//                intent.putExtra("phoneNum",phoneNum);
//                startActivity(intent);

                ApplicationUtil.switchButtonEnable(sendMsg);
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.SENDMSG, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response!=null) {
                                    System.out.println(response);
                                    User user= (User)JSONUtil.JsonToObject(response,User.class);
                                    if(user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                        Intent intent=new Intent(context,ShortMsgCodeActivity.class);
                                        Bundle bd=new Bundle();
                                        bd.putString("phoneNum",phoneNum);
                                        bd.putString("sessionId",user.getSessionId());
                                        intent.putExtras(bd);
                                        startActivity(intent);
                                        ApplicationUtil.switchButtonEnable(sendMsg);
                                    }else{
                                        ApplicationUtil.switchButtonEnable(sendMsg);
                                        ToastUtil.showToast("由于某种原因，短信没有发送成功",context);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ApplicationUtil.switchButtonEnable(sendMsg);
                                error.printStackTrace();
                                ToastUtil.showToast("由于某种原因，短信没有发送成功",context);
//                                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        },out);
            }
        });
    }
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    /**
     * 双击退出应用程序
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                ToastUtil.showToast("再按一次退出程序",context);
                // 利用handler延迟发送更改状态信息
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                ActivityCollector.AppExit(context);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
