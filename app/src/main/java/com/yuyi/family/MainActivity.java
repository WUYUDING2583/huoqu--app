package com.yuyi.family;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yuyi.family.activity.LoginActivity;
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.ImageUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.common.util.UserUtil;
import com.yuyi.family.component.CircleImageView;
import com.yuyi.family.pojo.User;
import com.yuyi.family.activity.DrawerActivity;
import com.yuyi.family.service.LocationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yuweiguocn.lib.squareloading.SquareLoading;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private DBAdapter dbAdapter;
    private User currentUser;
    private SquareLoading loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        loading=(SquareLoading)findViewById(R.id.main_loading);
        loading.setVisibility(View.VISIBLE);
        context=MainActivity.this;
        //开启数据库
        try {
            dbAdapter = new DBAdapter(context);
            dbAdapter.open();
        }catch (Exception e) {
            e.printStackTrace();
        }

        //判断用户是否登录
        try {
            if (!isLogin()) {//未登录跳转至登录页面
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                Map<String,String> out=new HashMap<String,String>();
                out.put("userPhone",currentUser.getPhone());
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.GETUSER, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response!=null){
                                    User user=(User)JSONUtil.JsonToObject(response,User.class);
                                    if(user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                        if(user.isRegister()) {
                                            ApplicationUtil.setCurrentUser(user);
                                            Intent intent = new Intent(context, DrawerActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }else{
                                        ApplicationUtil.setCurrentUser(null);
                                        ToastUtil.showToast("服务器出问题了。。。",MainActivity.this);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                ApplicationUtil.setCurrentUser(null);
                            }
                        },out);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 根据本地数据库获取用户信息
     * 若用户信息非空则代表用户登录
     * 获取用户信息列表最后一个作为当前用户
     * @return
     */
    private boolean isLogin(){
        List<User> userList=dbAdapter.getUsers();
        if(userList!=null&&userList.size()>0){
            currentUser=userList.get(userList.size()-1);
        }
        return currentUser!=null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        System.out.println("on destroy");
//        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//        mMapView.onDestroy();
        dbAdapter.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        System.out.println("on resume");
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
//        mMapView.onResume();
        dbAdapter.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        System.out.println("on pause");
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
//        mMapView.onPause();
        dbAdapter.close();
//        unbindService(conn);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        System.out.println("on saveInstanceState");
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
//        mMapView.onSaveInstanceState(outState);
    }

}
