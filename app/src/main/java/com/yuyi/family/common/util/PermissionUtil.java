package com.yuyi.family.common.util;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

public class PermissionUtil {
    public static void checkPermission(Activity activity,String[] permissions) {
        //1.检测权限
        for(String permission:permissions){
            int permissionResult = ActivityCompat.checkSelfPermission(activity, permission);
            if (permissionResult != PermissionChecker.PERMISSION_GRANTED) {
                //2.没有权限，弹出对话框申请
                ActivityCompat.requestPermissions(activity,permissions,1);
            }
        }
    }
}
