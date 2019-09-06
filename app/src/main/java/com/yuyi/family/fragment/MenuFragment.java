package com.yuyi.family.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuyi.family.R;
import com.yuyi.family.activity.LoginActivity;
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.pojo.User;
import com.yuyi.family.activity.DrawerActivity;
import com.yuyi.family.adapter.MenuItemAdapter;
import com.yuyi.family.component.MenuItem;
import com.yuyi.family.service.LocationService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuFragment  extends Fragment {

    private ListView mListView;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private MenuItemAdapter adapter;
    private User currentUser=null;

    private ImageView userPortrait;
    private TextView userName;
    private DBAdapter dbAdapter;
    private Context context;

    public void setCurrentUser(User user){
        this.currentUser=user;
        //显示用户信息
        if(currentUser!=null) {
            userName.setText(currentUser.getName());
            System.out.println("menu set portrait");
            System.out.println(currentUser.getPortrait());
            Glide.with(this).load(new File(currentUser.getPortrait())).into(userPortrait);
            userPortrait.setBackground(getResources().getDrawable(R.drawable.user_portrait));
        }
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View navView = inflater.inflate(R.layout.activity_menu, container, false);
        context= ApplicationUtil.getContext();
        bindView(navView);
        initListView();
        clickEvents();
        return navView;
    }

    private void bindView(View view){
        userName=(TextView)view.findViewById(R.id.current_user_name);
        userPortrait=(ImageView)view.findViewById(R.id.current_user_portrait);
        mListView = (ListView) view.findViewById(R.id.menu_list_view);
        mListView.setDivider(null);
    }

    public void initListView() {
        String[] data_zh = getResources().getStringArray(R.array.menu_zh);
        String[] data_en = getResources().getStringArray(R.array.menu_en);
        for (int i = 0; i < data_zh.length; i++) {
            MenuItem menuItem = new MenuItem(data_zh[i], data_en[i]);
            menuItemList.add(menuItem);
        }
        adapter = new MenuItemAdapter(getActivity(), R.layout.menu_list_item, menuItemList);
        mListView.setAdapter(adapter);
    }

    public void clickEvents() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==3){
                    dbAdapter=new DBAdapter(ApplicationUtil.getContext());
                    dbAdapter.open();
                    dbAdapter.removeAllUsers();
                    dbAdapter.close();
                    Intent intent=new Intent((DrawerActivity)getActivity(), LoginActivity.class);
                    Intent stopLocation=new Intent(context, LocationService.class);
                    context.stopService(stopLocation);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                }
                adapter.changeSelected(position);
                DrawerActivity activity = (DrawerActivity) getActivity();
                DrawerLayout mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawer(Gravity.START);
                activity.switchFragment(position);
            }
        });

    }

}

