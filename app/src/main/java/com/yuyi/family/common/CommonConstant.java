package com.yuyi.family.common;

import android.Manifest;

public class CommonConstant {
    public static class HttpUrl {
//        private static final String root="http://192.168.43.124:8080/Family/";
    private static final String root="http://www.jsjzx.top/Family/";
        public static final String  SENDMSG=root+"SendMSG";
        public static final String VERIFYSMS=root+"VerifySMS";
        public static final String REGISTER=root+"Register";
        public static final String RECODER_GEOLOCATION=root+"RecordGeolocation";
        public static final String ADDFAMILYMEMBER=root+"AddFamilyMember";
        public static final String GETFAMILYMEMBERS=root+"GetFamilyMembers";
        public static final String GETUSER=root+"GetUser";
        public static final String GETFAMILYMEMBERLOCATION=root+"GetFamilyMemberLocation";
    }

    public static class Permission {
        public static String[] PERMISSION_CAMERA={
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.READ_CONTACTS,
        };
        public static String[] PERMISSION_GEOLOCATION={
                //用于进行网络定位
                Manifest.permission.ACCESS_COARSE_LOCATION,
                //用于访问GPS定位-->
                Manifest.permission.ACCESS_FINE_LOCATION,
                //用于获取运营商信息，用于支持提供运营商信息相关的接口-->
                Manifest.permission.ACCESS_NETWORK_STATE,
                //用于访问wifi网络信息，wifi信息会用于进行网络定位-->
                Manifest.permission.ACCESS_WIFI_STATE,
                //用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
                Manifest.permission.CHANGE_WIFI_STATE,
                //用于访问网络，网络定位需要上网-->
                Manifest.permission.INTERNET,
                //用于读取手机当前的状态-->
                Manifest.permission.READ_PHONE_STATE,
                //用于写入缓存数据到扩展存储卡-->
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //用于申请调用A-GPS模块-->
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                //用于申请获取蓝牙信息进行室内定位-->
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
        };
    }

    public class BundleKey{
        public static final String PERSONALMAP="personalMap";
        public static final String CURRENTUSER="currentUser";
        public static final String FAMILYMEMBER="familyMember";
    }
    public static final String SERVER_SUCCESS_CODE="200";
    public static final String SERVER_FAIL_CODE="400";
    public static final String FILE_PATH="huoqu";

    public static final String AMDINPHONE="15868859587";
}
