package com.yuyi.family.test.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.yuyi.family.R;
import com.yuyi.family.pojo.LocationResult;
import com.yuyi.family.test.adapter.LocationDBAdapter;
import com.yuyi.family.listener.interfaces.OnLocationListener;
import com.yuyi.family.service.LocationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestAMapActivity extends AppCompatActivity implements View.OnClickListener{

    private MapView mMapView = null;
    private AMap aMap=null;
    private LinearLayout.LayoutParams mParams;
    private RelativeLayout mContainerLayout;
    private Button button;
    private Date time;
    private LatLng currentPosition=new LatLng(30.318483,120.061488),lastPosition=null;//当前定位，上一次定位
    private Marker marker;//标记点
    private LocationDBAdapter locationDBAdapter;//数据库实例
    private Context context;
    private float currentZoom=14;//当前地图缩放大小
    private LocationService locationService;//定位服务实例，用于监听监听信息的回调
    private int count=0;//定位次数


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_amap);
        context=TestAMapActivity.this;
        //初始化数据库对象
        locationDBAdapter=new LocationDBAdapter(context);
        //开启数据库
        locationDBAdapter.open();

        mContainerLayout = (RelativeLayout) findViewById(R.id.activity_main);

        mMapView=new MapView(this);
        mMapView.onCreate(savedInstanceState);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.addView(mMapView, mParams);

        bindView();

        init();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.changePosition:
                //变更当前页面中心点到指定经纬度
                changeCenterToPosition(currentPosition,currentZoom);
                break;
        }
    }

    private void bindView(){
        button=(Button)findViewById(R.id.changePosition);
        button.bringToFront();//将控件放在其他控件上方

        button.setOnClickListener(this);
    }

    //改变屏幕中心
    private void changeCenterToPosition(LatLng position,float zoom){
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,zoom));
    }

    //初始化地图及定位
    private void init(){
        //初始化地图控制器对象
        if(aMap==null) {
            aMap = mMapView.getMap();
        }
        //获取当前存储的所有定位信息显示轨迹
        List<LocationResult> historyLocations=locationDBAdapter.getAllLocations();
        List<LocationResult> list1=new ArrayList<>();
        List<LocationResult> list2=new ArrayList<>();
        for(int i=0;i<1000;i++)
            list1.add(historyLocations.get(i));
        for(int i=1001;i<historyLocations.size();i++)
            list2.add(historyLocations.get(i));
        makePolyline(list1,1);
        makePolyline(list2,2);
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
//        //绑定服务
//        Intent startLocation=new Intent(this, LocationService.class);
//        startService(startLocation);
//        bindService(startLocation,conn,Context.BIND_AUTO_CREATE);
//        makePolyline();
    }

//    ServiceConnection conn=new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //返回一个locationService对象
//            locationService=((LocationService.LocationBinder)service).getService();
//            //注册回调接收定位信息的变化
//            locationService.setOnLocationListener(new OnLocationListener() {
//                @Override
//                public void onLocation(LocationResult result) {
//                    lastPosition=currentPosition;
//                    currentPosition=new LatLng(result.getLatitude(),result.getLongtitude());
//                    if(lastPosition==null){
//                        lastPosition=currentPosition;
//                    }
//                    makeMarker(currentPosition);
//                    makePolyline(null);
//                    count++;
//                    button.setText(count+"");
//                }
//            });
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    //画出轨迹并显示移动动画
    private void makePolyline(List<LocationResult> track,int flag){
        // 获取轨迹坐标点
        List<LatLng> latLngs = new ArrayList<LatLng>();
        if(track==null) {//单次定位画出轨迹
            latLngs.add(lastPosition);
            latLngs.add(currentPosition);
        }else if(track.size()>0){//画出历史轨迹
            for(LocationResult result:track){
                latLngs.add(new LatLng(result.getLatitude(),result.getLongtitude()));
            }
        }
//        List<LocationResult> locations=locationDBAdapter.getLocations();
        if(flag==1) {
            Polyline polyline = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(30).color(Color.argb(255, 1, 1, 1)));
        }else{
            Polyline polyline = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 123, 1, 1)));
        }
//        LatLngBounds bounds = new LatLngBounds(latLngs.get(0), latLngs.get(latLngs.size() - 2));
//        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
//        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
//        // 设置滑动的图标
//        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background));
//
//        LatLng drivePoint = latLngs.get(0);
//        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(latLngs, drivePoint);
//        latLngs.set(pair.first, drivePoint);
//        List<LatLng> subList = latLngs.subList(pair.first, latLngs.size());
//
//        // 设置滑动的轨迹左边点
//        smoothMarker.setPoints(subList);
//        // 设置滑动的总时间
//        smoothMarker.setTotalDuration(5);
//        // 开始滑动
//        smoothMarker.startSmoothMove();
    }

    //显示位置点
    private void makeMarker(LatLng position){
        if(marker!=null){
            marker.destroy();
        }
        marker=aMap.addMarker(new MarkerOptions().position(position).title("北京").snippet("DefaultMarker"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        locationDBAdapter.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        locationDBAdapter.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        locationDBAdapter.close();
//        unbindService(conn);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
