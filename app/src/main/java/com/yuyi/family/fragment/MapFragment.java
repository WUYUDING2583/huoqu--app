package com.yuyi.family.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.pojo.LocationResult;
import com.yuyi.family.pojo.User;
import com.yuyi.family.listener.interfaces.OnLocationListener;
import com.yuyi.family.service.LocationService;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private Context context;
    private DBAdapter dbAdapter;
    private User currentUser=null;

    private LatLng currentPosition=null,lastPosition=null;//当前定位，上一次定位
    private Marker marker;//标记点
    private MapView mMapView = null;
    private AMap aMap=null;
    private LinearLayout.LayoutParams mParams;
    private RelativeLayout mContainerLayout;
    private float currentZoom=14;//当前地图缩放大小
    private LocationService locationService;//定位服务实例，用于监听监听信息的回调
    private int count=0;//定位次数

    public static MapFragment newInstance()
    {
        MapFragment fragment = new MapFragment();
        fragment.context= ApplicationUtil.getContext();
        fragment.currentUser=ApplicationUtil.getCurrentUser();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        //开启数据库
        try {
            dbAdapter = new DBAdapter(context);
            dbAdapter.open();
        }catch (Exception e){
            e.printStackTrace();
        }
        //创建地图并添加到布局中
        mContainerLayout = (RelativeLayout) view.findViewById(R.id.map_fragment);
        mMapView=new MapView(context);
        mMapView.onCreate(savedInstanceState);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.addView(mMapView, mParams);

        init();
        return view;
    }


    /**
     * 初始化地图及定位
     */
    private void init(){
        //初始化地图控制器对象
        if(aMap==null) {
            aMap = mMapView.getMap();
        }
//        获取当前存储的所有定位信息显示轨迹
        List<LocationResult> historyLocations=dbAdapter.getTodayLocations();
        makePolyline(historyLocations);
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
        //绑定服务
        Intent startLocation=new Intent(context, LocationService.class);
        context.startService(startLocation);
        context.bindService(startLocation,conn,Context.BIND_AUTO_CREATE);
    }

    /**
     * 改变屏幕中心
     * @param position 经纬度
     * @param zoom 缩放比例
     */
    private void changeCenterToPosition(LatLng position,float zoom){
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,zoom));
    }

    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个locationService对象
            locationService=((LocationService.LocationBinder)service).getService();
            //注册回调接收定位信息的变化
            locationService.setOnLocationListener(new OnLocationListener() {
                @Override
                public void onLocation(LocationResult result) {
                    lastPosition=currentPosition;
                    currentPosition=new LatLng(result.getLatitude(),result.getLongtitude());
                    if(lastPosition==null){
                        lastPosition=currentPosition;
                    }
                    if(count==0){
                        changeCenterToPosition(currentPosition,currentZoom);
                    }
//                    System.out.println("count:"+count);
                    count++;
                    makeMarker(currentPosition);
                    if(result.getAccuracy()>500.0||result.getAddress().equals("")){//精度大于500或地址信息为空则舍弃定位数据
//                        System.out.println("舍弃");
                        return;
                    }
                    makePolyline(null);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 显示位置点
     * @param position
     */
    private void makeMarker(LatLng position){
        if(marker!=null){
            marker.destroy();
        }
        //TODO
        marker=aMap.addMarker(new MarkerOptions().position(position).title(currentUser.getName()));
    }

    /**
     * 画出轨迹
     * @param track
     */
    private void makePolyline(List<LocationResult> track){
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
        Polyline polyline =aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(30).color(Color.argb(255, 1, 1, 1)));
    }



    /**
     * 必须重写以下方法
     */
    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}