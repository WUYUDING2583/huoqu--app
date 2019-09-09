package com.yuyi.family.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyi.family.R;
import com.yuyi.family.activity.DrawerActivity;
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.FileUtil;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.listener.interfaces.OnFamilyMemberLocationListener;
import com.yuyi.family.listener.interfaces.OnLatestFamilyListener;
import com.yuyi.family.pojo.CommonData;
import com.yuyi.family.pojo.FamilyMember;
import com.yuyi.family.pojo.FamilyMemberLocations;
import com.yuyi.family.pojo.LocationResult;
import com.yuyi.family.listener.interfaces.OnLocationListener;
import com.yuyi.family.pojo.User;
import com.yuyi.family.test.activity.TestAccessbilityServiceActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationService extends Service {

    private Date time;
    private LocationResult result;
    private DBAdapter dbAdapter;
    private int count=0;//定位次数
    private List<LocationResult> locationResultList=new ArrayList<>();//存储定位数据
    private User currentUser;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //更新定位信息的回调接口
    public OnLocationListener onLocationListener;
    //注册回调监听接口，供外部使用
    public void setOnLocationListener(OnLocationListener onLocationListener){
        this.onLocationListener=onLocationListener;
    }
    //更新家人最新信息回调接口
    public OnLatestFamilyListener onLatestFamilyListener;
    public void setOnLatestFamilyListener(OnLatestFamilyListener onLatestFamilyListener){
        this.onLatestFamilyListener=onLatestFamilyListener;
    }

    //更新某一家人当日定位信息接口
    public OnFamilyMemberLocationListener onFamilyMemberLocationListener;
    public void setOnFamilyMemberLocationListener(OnFamilyMemberLocationListener onFamilyMemberLocationListener){
        this.onFamilyMemberLocationListener=onFamilyMemberLocationListener;
    }

    private String familyMemberPhone="";
    /**
     * 增加get方法共activity使用
     * @return 定位结果
     */
    public LocationResult getLocationResult(){
        return result;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("on create");
        try {
            dbAdapter = new DBAdapter(this.getApplicationContext());
            dbAdapter.open();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        System.out.println("on start command");
        currentUser= ApplicationUtil.getCurrentUser();
        startLocation();
        return START_STICKY;
    }

    private void startLocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000*10);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(mLocationOption);
            //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
            mLocationClient.enableBackgroundLocation(2001, buildNotification());
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener(){
        @Override
        public void onLocationChanged(AMapLocation amapLocation){
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    result=new LocationResult();
                    result.setUserPhone(currentUser.getPhone());
                    result.setLatitude(amapLocation.getLatitude());
                    result.setLongtitude(amapLocation.getLongitude());
                    result.setAddress(amapLocation.getAddress());
                    time=new Date();
                    result.setTime(time.getTime());
                    result.setAccuracy(amapLocation.getAccuracy());
//                    System.out.println(new Gson().toJson(result));
                    //定位信息更新时通知调用方
                    onLocationListener.onLocation(result);
                    //存储定位数据，每存储10条数据就发送给后台
                    locationResultList.add(result);
                    count++;
                    if(count>=6){
                        Map<String,String> out=new HashMap<String,String>();
                        out.put("geolocations", JSONUtil.ObjectToJson(locationResultList));
                        HttpUtil.getInstance()
                                .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.RECODER_GEOLOCATION, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println("response in recoder geolocation");
                                        System.out.println(response);
                                        if(response!=null){
                                            CommonData result=(CommonData)JSONUtil.JsonToObject(response,CommonData.class);
                                            if(result.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                                locationResultList.clear();
                                                locationResultList=new ArrayList<>();
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                },out);
                        if(onLatestFamilyListener!=null&&null!=currentUser.getFamilyMembers()) {
                            Map<String, String> out1 = new HashMap<String, String>();
                            out1.put("userPhone", currentUser.getPhone());
                            HttpUtil.getInstance()
                                    .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.GETUSER, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
//                                        System.out.println("in location service get user");
//                                        System.out.println(response);
                                            if (response != null) {
                                                User user = (User) JSONUtil.JsonToObject(response, User.class);
                                                if (user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)) {
                                                    if (null != user.getFamilyMembers() && user.getFamilyMembers().size() > 0) {
                                                        for (int i = 0; i < user.getFamilyMembers().size(); i++) {
                                                            try {
                                                                currentUser.getFamilyMembers().get(i).setName(user.getFamilyMembers().get(i).getName());
                                                                currentUser.getFamilyMembers().get(i).setTime(user.getFamilyMembers().get(i).getTime());
                                                                currentUser.getFamilyMembers().get(i).setAddress(user.getFamilyMembers().get(i).getAddress());
                                                            } catch (Exception e) {//当新增家人时存储家人信息
                                                                e.printStackTrace();
                                                                currentUser.getFamilyMembers().add(user.getFamilyMembers().get(i));
                                                                currentUser.getFamilyMemberPhone().add(user.getFamilyMemberPhone().get(i));
                                                                FileUtil.storeImageFromUrl(currentUser, ApplicationUtil.getContext());
                                                            }
                                                        }
                                                    }
                                                    ApplicationUtil.setCurrentUser(currentUser);
                                                    if (onLatestFamilyListener != null && null != currentUser.getFamilyMembers()) {
                                                        onLatestFamilyListener.onLatestFamily(currentUser.getFamilyMembers());
                                                    }
                                                }
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }, out1);
                        }
                        familyMemberPhone=ApplicationUtil.getFamilyMemberPhone();
                        if(onFamilyMemberLocationListener!=null&&null!=familyMemberPhone&&!familyMemberPhone.equals("")){
                            System.out.println("get family member locations");
                            Map<String,String> out2=new HashMap<>();
                            out2.put("phone",familyMemberPhone);
                            String today=TimeUtil.getTodyFromat();
                            out2.put("startTime", TimeUtil.getDayStartTimeStamp(today)+"");
                            out2.put("endTime",TimeUtil.getDayEndTimeStamp(today)+"");
                            FamilyMember familyMember=ApplicationUtil.getCurrentUser().getFamilyMemberByPhone(familyMemberPhone);
                            if(familyMember!=null) {
                                if(familyMember.getLastGetLocationTime()<TimeUtil.getTodyTimeStamp()){//若上一次获取时间在今天之前则清空本地数据库数据
                                    dbAdapter.removeAllLocations();
                                    out2.put("lastGetLocationsTime", "");
                                }else if (familyMember.getLastGetLocationTime() != 0) {
                                    out2.put("lastGetLocationsTime", familyMember.getLastGetLocationTime() + "");
                                } else {
                                    out2.put("lastGetLocationsTime", "");
                                    dbAdapter.removeAllLocations();
                                }
                            }
                            final String familyMemberPhoneTemp=familyMemberPhone;
                            HttpUtil.getInstance()
                                    .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.GETFAMILYMEMBERLOCATION, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if(response!=null){
                                                FamilyMemberLocations familyMemberLocations=new FamilyMemberLocations();
                                                familyMemberLocations.setFamilyMemberLocations(dbAdapter.getLocationsByPhone(familyMemberPhoneTemp));
                                                FamilyMemberLocations newFamilyMemberLocations=(FamilyMemberLocations) JSONUtil.JsonToObject(response,FamilyMemberLocations.class);
                                                if(newFamilyMemberLocations.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                                    System.out.println("update family member locations");
                                                    //更新上次获取定位数据时间
                                                    User currentUserTemp=ApplicationUtil.getCurrentUser();
                                                    currentUserTemp.getFamilyMemberByPhone(familyMemberPhoneTemp).setLastGetLocationTime(new Date().getTime());
                                                    currentUser=currentUserTemp;
                                                    ApplicationUtil.setCurrentUser(currentUserTemp);
                                                    for(int i=0;i<newFamilyMemberLocations.getFamilyMemberLocations().size();i++){
                                                        dbAdapter.insertLocation(newFamilyMemberLocations.getFamilyMemberLocations().get(i));
                                                        familyMemberLocations.getFamilyMemberLocations().add(newFamilyMemberLocations.getFamilyMemberLocations().get(i));
                                                    }
                                                    if(onFamilyMemberLocationListener!=null) {
                                                        onFamilyMemberLocationListener.onLocation(familyMemberLocations);
                                                    }
                                                }
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    },out2);
                        }
                        count=0;
                    }
                    if(result.getAccuracy()>500.0||result.getAddress().equals("")){//精度大于500或地址信息为空则舍弃定位数据
                        return;
                    }
                    //将定位信息存储到数据库
//                    dbAdapter.insertLocation(result);
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    System.out.println("AmapError location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    public class LocationBinder extends Binder{
        /**
         * 获取当前service实例
         * @return
         */
        public LocationService getService(){
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        System.out.println("on bind");
        return new LocationBinder();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        dbAdapter.close();
    }

    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    @SuppressLint("NewApi")
    private Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        Intent nfIntent = new Intent(this, DrawerActivity.class);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setContentTitle("活趣")
                .setAutoCancel(false)
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }
}
