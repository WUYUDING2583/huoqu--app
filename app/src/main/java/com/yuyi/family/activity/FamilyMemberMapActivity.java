package com.yuyi.family.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sonnyjack.widget.dragview.SonnyJackDragView;
import com.yuyi.family.R;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.StringUtil;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.component.SelectTimeDialog;
import com.yuyi.family.listener.interfaces.OnFamilyMemberLocationListener;
import com.yuyi.family.listener.interfaces.OnLocationListener;
import com.yuyi.family.listener.interfaces.OnQueryInParticularTimeListener;
import com.yuyi.family.pojo.FamilyMemberLocations;
import com.yuyi.family.pojo.LocationResult;
import com.yuyi.family.pojo.User;
import com.yuyi.family.service.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FamilyMemberMapActivity extends AppCompatActivity {

    private Context context;

    private LatLng currentPosition=null,lastPosition=null;//当前定位，上一次定位
    private Marker marker;//标记点
    private MapView mMapView = null;
    private AMap aMap=null;
    private LinearLayout.LayoutParams mParams;
    private RelativeLayout mContainerLayout;
    private float currentZoom=13;//当前地图缩放大小
    private LocationService locationService;//定位服务实例，用于监听监听信息的回调
    private int count=0;//定位次数

    private FamilyMemberLocations familyMemberLocations;

    private User familyMember;
    private ImageView imageView;
    private SonnyJackDragView sonnyJackDragView=null;

    private OnFamilyMemberLocationListener onFamilyMemberLocationListener=new OnFamilyMemberLocationListener() {
        @Override
        public void onLocation(FamilyMemberLocations location) {
            familyMemberLocations=location;
            if (familyMemberLocations.getFamilyMemberLocations().size() > 0) {
                makePolyline(familyMemberLocations.getFamilyMemberLocations(),false);
                double lat = familyMemberLocations.getFamilyMemberLocations().get(familyMemberLocations.getFamilyMemberLocations().size() - 1).getLatitude();
                double lon = familyMemberLocations.getFamilyMemberLocations().get(familyMemberLocations.getFamilyMemberLocations().size() - 1).getLongtitude();
                changeCenterToPosition(new LatLng(lat, lon), currentZoom);
            } else {
                ToastUtil.showToast("该用户今日暂无定位数据", context);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_map);
        context=FamilyMemberMapActivity.this;

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        familyMember=(User)bundle.getSerializable(CommonConstant.BundleKey.FAMILYMEMBER);

        //获取今日所有定位数据
       getTodayLocations();

        //创建地图并添加到布局中
        mContainerLayout = (RelativeLayout) findViewById(R.id.map_fragment);
        mMapView=new MapView(context);
        mMapView.onCreate(savedInstanceState);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.addView(mMapView, mParams);

        Toolbar toolbar = (Toolbar) findViewById(R.id.family_member_map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // 不显示程序应用名
        toolbar.bringToFront();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.inflateMenu(R.menu.family_map_toolbar_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_search:
                        new SelectTimeDialog().show(getSupportFragmentManager(),"tag");
                        break;
                }
                return false;
            }
        });
        SelectTimeDialog.setOnQueryInParticularTimeListener(new OnQueryInParticularTimeListener() {
            @Override
            public void query(String date) {
                httpQueryLocations(date,"","",familyMember.getPhone());
            }

            @Override
            public void query(String date, String time, boolean isEnd) {
                String t=date+" "+time;
                if(isEnd){
                    httpQueryLocations(date,"",time,familyMember.getPhone());
                }else{
                    httpQueryLocations(date,time,"",familyMember.getPhone());
                }
            }

            @Override
            public void query(String date, String startTime, String endTime) {
                String t1=date+ " "+startTime;
                String t2=date+" "+endTime;
                httpQueryLocations(date,startTime,endTime,familyMember.getPhone());
            }
        });
        init();

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_cancel_black_24dp);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTodayLocations();
                imageView.setVisibility(View.GONE);
                locationService.setOnFamilyMemberLocationListener(onFamilyMemberLocationListener);
            }
        });
        sonnyJackDragView = new SonnyJackDragView.Builder()
                .setActivity(FamilyMemberMapActivity.this)//当前Activity，不可为空
                .setDefaultLeft(30)//初始位置左边距
                .setDefaultTop(getResources().getDisplayMetrics().heightPixels-500)//初始位置上边距
                .setNeedNearEdge(true)//拖动停止后，是否移到边沿
                .setSize(150)//DragView大小
                .setView(imageView)//设置自定义的DragView，切记不可为空
                .build();
    }

    public void httpQueryLocations(String date,String startTime,String endTime,String phone){
        if(locationService!=null) {
            locationService.setOnFamilyMemberLocationListener(null);
        }
        Map<String,String> out=new HashMap<>();
        out.put("phone",phone);
        if(!StringUtil.isNotEmpty(startTime)){
            startTime=" 00:00:00";
        }
        if(!StringUtil.isNotEmpty(endTime)){
            endTime=" 23:59:59";
        }
        out.put("startTime",TimeUtil.timeFormat2TimeStamp(date+" "+startTime)+"");
        out.put("endTime",TimeUtil.timeFormat2TimeStamp(date+" "+endTime)+"");
        HttpUtil.getInstance()
                .sendStringRequest(Request.Method.POST,CommonConstant.HttpUrl.GETFAMILYMEMBERLOCATION, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            FamilyMemberLocations familyMemberLocations=(FamilyMemberLocations)JSONUtil.JsonToObject(response,FamilyMemberLocations.class);
                            if(familyMemberLocations.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                if(familyMemberLocations.getFamilyMemberLocations().size()>0) {
                                    makePolyline(familyMemberLocations.getFamilyMemberLocations(),true);
                                    double lat = familyMemberLocations.getFamilyMemberLocations().get(familyMemberLocations.getFamilyMemberLocations().size() - 1).getLatitude();
                                    double lon = familyMemberLocations.getFamilyMemberLocations().get(familyMemberLocations.getFamilyMemberLocations().size() - 1).getLongtitude();
                                    changeCenterToPosition(new LatLng(lat, lon), currentZoom);
                                    imageView.setVisibility(View.VISIBLE);
                                }else{
                                    ToastUtil.showToast("该用户该时间段暂无定位数据",context);
                                }
                            }else{
                                ToastUtil.showToast("服务器出错",context);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showToast("网络连接错误请检查网络",context);
                    }
                },out);
    }

    private void getTodayLocations(){
        Map<String,String> out=new HashMap<>();
        out.put("phone",familyMember.getPhone());
        String today=TimeUtil.getTodyFromat();
        out.put("startTime", TimeUtil.getDayStartTimeStamp(today)+"");
        out.put("endTime",TimeUtil.getDayEndTimeStamp(today)+"");
        HttpUtil.getInstance()
                .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.GETFAMILYMEMBERLOCATION, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            familyMemberLocations=(FamilyMemberLocations) JSONUtil.JsonToObject(response,FamilyMemberLocations.class);
                            if(familyMemberLocations.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                if(familyMemberLocations.getFamilyMemberLocations().size()>0) {
                                    makePolyline(familyMemberLocations.getFamilyMemberLocations(),true);//描绘轨迹
                                    double lat = familyMemberLocations.getFamilyMemberLocations().get(familyMemberLocations.getFamilyMemberLocations().size() - 1).getLatitude();
                                    double lon = familyMemberLocations.getFamilyMemberLocations().get(familyMemberLocations.getFamilyMemberLocations().size() - 1).getLongtitude();
                                    changeCenterToPosition(new LatLng(lat, lon), currentZoom);//改变中心
                                }else{
                                    ToastUtil.showToast("该用户今日暂无定位数据",context);
                                }
                            }else{
                                ToastUtil.showToast("服务器出错",context);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showToast("服务器出错",context);
                    }
                },out);
    }

    //显示导航栏右侧突变
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.family_map_toolbar_menu,menu);
        return true;
    }

    /**
     * 初始化地图及定位
     */
    private void init(){
        //初始化地图控制器对象
        if(aMap==null) {
            aMap = mMapView.getMap();
        }
        //设置地图状态变化监听器
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                currentZoom=cameraPosition.zoom;
            }
        });
    }

    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个locationService对象
            locationService=((LocationService.LocationBinder)service).getService();
            //注册回调接家人收定位信息的变化
            locationService.setOnFamilyMemberLocationListener(onFamilyMemberLocationListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 改变屏幕中心
     * @param position 经纬度
     * @param zoom 缩放比例
     */
    private void changeCenterToPosition(LatLng position,float zoom){
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,zoom));
    }

    /**
     * 显示位置点
     * @param position
     */
    private void makeMarker(LatLng position){
        if(marker!=null){
            marker.destroy();
        }
        //TODO
        marker=aMap.addMarker(new MarkerOptions().position(position).title(familyMember.getName()));
    }


    /**
     * 画出轨迹并显示移动动画
     * @param track
     */
    private void makePolyline(List<LocationResult> track,boolean setSmooth){
        aMap.clear();
        // 获取轨迹坐标点
        List<LatLng> latLngs = new ArrayList<LatLng>();
        if(track==null) {//单次定位画出轨迹
            latLngs.add(lastPosition);
            latLngs.add(currentPosition);
        }else if(track.size()>0){//画出精度500以下历史轨迹
            for(LocationResult result:track){
                if(result.getAccuracy()<500) {
                    latLngs.add(new LatLng(result.getLatitude(), result.getLongtitude()));
                }
            }
        }
        Polyline polyline =aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(15).color(getResources().getColor(R.color.gray)));
        if(setSmooth){
            setSmoothMaker(latLngs,track.get(0).getTime(),track.get(track.size()-1).getTime());
        }else{
            makeMarker(latLngs.get(latLngs.size()-1));
        }

    }

    private void setSmoothMaker(List<LatLng> points,long startTime,long endTime){
        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
        // 设置滑动的图标
        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.car));


        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);
        // 设置滑动的总时间
        System.out.println((endTime-startTime)/(1000*60*6));
        smoothMarker.setTotalDuration((int)((endTime-startTime)/(1000*60*6)));
        // 开始滑动
        smoothMarker.startSmoothMove();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("绑定服务 in family map");
                System.out.println("start service in init");
                Intent startLocation=new Intent(context, LocationService.class);
                ApplicationUtil.setFamilyMemberPhone(familyMember.getPhone());
                context.bindService(startLocation,conn,Context.BIND_AUTO_CREATE);
                if(locationService!=null) {
                    locationService.setOnFamilyMemberLocationListener(onFamilyMemberLocationListener);
                }
            }
        }, (endTime-startTime)/(60*6));
    }



    /**
     * 必须重写以下方法
     */
    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
        Intent startLocation=new Intent(context, LocationService.class);
        //TODO 给service传值
//        System.out.println("start service in resume");
//        ApplicationUtil.setFamilyMemberPhone(familyMember.getPhone());
//        context.bindService(startLocation,conn,Context.BIND_AUTO_CREATE);
        if(locationService!=null) {
            locationService.setOnFamilyMemberLocationListener(onFamilyMemberLocationListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(locationService!=null) {
            locationService.setOnFamilyMemberLocationListener(null);
        }
//        unbindService(conn);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("on destroy");
        mMapView.onDestroy();
        if(locationService!=null) {
            locationService.setOnFamilyMemberLocationListener(null);
        }
//        unbindService(conn);
    }
}
