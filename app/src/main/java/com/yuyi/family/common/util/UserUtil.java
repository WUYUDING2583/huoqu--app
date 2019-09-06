package com.yuyi.family.common.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.pojo.User;

import java.util.HashMap;
import java.util.Map;

public class UserUtil {
    private DBAdapter dbAdapter;
    private Context context;

    public static void getUserFromServer(User user){
        Map<String,String> out=new HashMap<String,String>();
        out.put("userPhone",user.getPhone());
        HttpUtil.getInstance()
                .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.GETUSER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            User user=(User)JSONUtil.JsonToObject(response,User.class);
                            if(user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                ApplicationUtil.setCurrentUser(user);
                            }else{
                                ApplicationUtil.setCurrentUser(null);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ApplicationUtil.setCurrentUser(null);
                    }
                },out);
    }
}
