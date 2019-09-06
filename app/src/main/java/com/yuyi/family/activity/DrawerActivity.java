package com.yuyi.family.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyi.family.R;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ActivityCollector;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.FileUtil;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.PermissionUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.fragment.QRCodeFragment;
import com.yuyi.family.listener.interfaces.OnCurrentUserListener;
import com.yuyi.family.pojo.User;
import com.yuyi.family.fragment.MenuFragment;
import com.yuyi.family.fragment.MapFragment;
import com.yuyi.family.fragment.FamilyFragment;
import com.yuyi.family.test.fragment.TabFragment4;
import com.yuyi.family.test.fragment.TabFragment5;

import java.util.ArrayList;
import java.util.List;

public class DrawerActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FrameLayout contentFrameLayout;
    private Fragment currentFragment;
    private List<Fragment> tabFragments = new ArrayList<>();
    private Context context;
    private User currentUser=null;
    private FrameLayout menuContent;
    private int isGetPortrait=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_drawer);
        context= DrawerActivity.this;
        //检查定位权限
        PermissionUtil.checkPermission(this, CommonConstant.Permission.PERMISSION_GEOLOCATION);
        //检查相机权限
        PermissionUtil.checkPermission(this, CommonConstant.Permission.PERMISSION_CAMERA);

        currentUser= ApplicationUtil.getCurrentUser();
        int flag=0;
        try {
            Intent intent = getIntent();
            flag = intent.getIntExtra("fromRegister", 0);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("in drawer activity");
        System.out.println(JSONUtil.ObjectToJson(currentUser));

        FragmentManager fragmentManager = getSupportFragmentManager();
        final MenuFragment menuFragment=(MenuFragment)fragmentManager.findFragmentById(R.id.nav_view);
        if(flag==1){
            menuFragment.setCurrentUser(currentUser);
        }else {
            FileUtil.storeImageFromUrl(currentUser, context);
        }

        ApplicationUtil.setOnCurrentUserListener(new OnCurrentUserListener() {
            @Override
            public void onCurrentUserListener(User user) {
//                System.out.println("on current user listener");
                currentUser=user;
//                System.out.println(JSONUtil.ObjectToJson(currentUser));
                int count=1;
                if(null!=currentUser.getFamilyMembers()&&currentUser.getFamilyMembers().size()>0){
                    count+=currentUser.getFamilyMembers().size();
                }
                if(isGetPortrait<count) {
                    menuFragment.setCurrentUser(currentUser);
                }
                isGetPortrait++;
            }
        });

        init();
    }

    private void init(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT); // 菜单滑动时content不被阴影覆盖

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // 不显示程序应用名
        toolbar.bringToFront();
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        final CardView cardView = (CardView) findViewById(R.id.card_view);

//        Bundle personalMap=new Bundle();
//        personalMap.putSerializable(CommonConstant.BundleKey.PERSONALMAP,currentUser);

//        tabFragments.add(new MapFragment().newInstance(personalMap));
        tabFragments.add(new MapFragment().newInstance());
        tabFragments.add(new FamilyFragment());
//        tabFragments.add(new QRCodeFragment().newInstance(personalMap));
        tabFragments.add(new QRCodeFragment().newInstance());
        tabFragments.add(new TabFragment4());
        tabFragments.add(new TabFragment5());

        contentFrameLayout = (FrameLayout) findViewById(R.id.content_view);
        currentFragment = tabFragments.get(0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content_view, currentFragment).commit();
        //添加抽屉菜单fragme
//        transaction.add(R.id.menu_fragment_content,new MenuFragment());


        /**
         * 监听抽屉的滑动事件
         */
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 0.5f + slideOffset * 0.5f;
                mMenu.setAlpha(leftScale);
                mMenu.setScaleX(leftScale);
                mMenu.setScaleY(leftScale);
                mContent.setPivotX(0);
                mContent.setPivotY(mContent.getHeight() * 1 / 2);
                mContent.setScaleX(rightScale);
                mContent.setScaleY(rightScale);
                mContent.setTranslationX(mMenu.getWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                cardView.setRadius(20);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                cardView.setRadius(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    /**
     * 切换主视图的fragment，避免重复实例化加载
     *
     * @param position
     */
    public void switchFragment(int position) {
        Fragment fragment = tabFragments.get(position);
        if (currentFragment != fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (fragment.isAdded()) {
                transaction.hide(currentFragment)
                        .show(fragment)
                        .commit();
            } else {
                transaction.hide(currentFragment)
                        .add(R.id.content_view, fragment)
                        .commit();
            }
            currentFragment = fragment;
        }
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
