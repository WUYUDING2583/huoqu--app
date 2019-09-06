package com.yuyi.family.common.util;

import android.app.Application;
import android.content.Context;
import android.widget.Button;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yuyi.family.listener.interfaces.OnCurrentUserListener;
import com.yuyi.family.pojo.User;
import com.yuyi.family.service.LocationService;

public class ApplicationUtil extends Application {
    private static Context context;
    private static User currentUser;
    private static LocationService locationService;
    private static String protraitPath="";
    private static String familyMemberPhone="";
    public static OnCurrentUserListener onCurrentUserListener;
    public static void setOnCurrentUserListener(OnCurrentUserListener onCurrentUserListener1){
        onCurrentUserListener=onCurrentUserListener1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        ZXingLibrary.initDisplayOpinion(this);
    }
    public static Context getContext(){
        return context;
    }

    public static void setCurrentUser(User user){
        currentUser=user;
        if(onCurrentUserListener!=null) {
            onCurrentUserListener.onCurrentUserListener(user);
        }
    }

    public static User getCurrentUser(){return currentUser;}

    public static void setFamilyMemberPhone(String phone){
        familyMemberPhone=phone;
    }

    public static String getFamilyMemberPhone(){return familyMemberPhone;}

    public static void switchButtonEnable(Button button){
        button.setEnabled(!button.isEnabled());
    }

}
