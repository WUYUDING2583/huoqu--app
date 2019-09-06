package com.yuyi.family.component;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.yuyi.family.R;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.StringUtil;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.listener.interfaces.OnQueryInParticularTimeListener;
import com.yuyi.family.listener.interfaces.OnSelectDateListener;
import com.yuyi.family.listener.interfaces.OnSelectEndTimeListener;
import com.yuyi.family.listener.interfaces.OnSelectStartTimeListener;
import com.yuyi.family.test.adapter.SelectTimeAdapter;
import com.yuyi.family.fragment.SelectDateFragment;
import com.yuyi.family.fragment.SelectEndTimeFragment;
import com.yuyi.family.fragment.SelectStartTimeFragment;

public class SelectTimeDialog extends DialogFragment implements ViewPager.OnPageChangeListener {


    private SelectTimeAdapter selectTimeAdapter;
    private ViewPager vpager;
    private BubbleNavigationConstraintView tabNavigation;
    private Button query;
    private TextView selectDate,selectStartTime,selectEndTime;
    private Context context;
    private static OnQueryInParticularTimeListener onQueryInParticularTimeListener;
    public static void setOnQueryInParticularTimeListener(OnQueryInParticularTimeListener onQueryInParticularTimeListener1){
        onQueryInParticularTimeListener=onQueryInParticularTimeListener1;
    }

    public SelectTimeDialog(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_time, container, false);
        context=ApplicationUtil.getContext();
        selectTimeAdapter=new SelectTimeAdapter(getChildFragmentManager());
        tabNavigation=(BubbleNavigationConstraintView)view.findViewById(R.id.select_time_navigation);

        vpager = (ViewPager)view. findViewById(R.id.select_time_vpager);
        vpager.setAdapter(selectTimeAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
        tabNavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                vpager.setCurrentItem(position);
            }
        });

        selectDate=(TextView)view.findViewById(R.id.select_date);
        selectStartTime=(TextView)view.findViewById(R.id.select_start_time);
        selectEndTime=(TextView)view.findViewById(R.id.select_end_time);
        query=(Button)view.findViewById(R.id.query_particular_time_locations);

        SelectDateFragment.setOnSelectDateListener(new OnSelectDateListener() {
            @Override
            public void onSelectDate(String date) {
                selectDate.setText(date);
                vpager.setCurrentItem(1);

            }
        });

        SelectStartTimeFragment.setOnSelectStartTimeListener(new OnSelectStartTimeListener() {
            @Override
            public void onSelectStartTime(String startTime) {
                selectStartTime.setText(startTime);
                vpager.setCurrentItem(2);
            }
        });

        SelectEndTimeFragment.setOnSelectEndTimeListener(new OnSelectEndTimeListener() {
            @Override
            public void onSelectEndTime(String endTime) {
                if(StringUtil.isNotEmpty(selectStartTime.getText().toString())) {
                    long startTime = TimeUtil.timeFormat2TimeStamp(selectDate.getText().toString() + " " + selectStartTime.getText().toString());
                    long end=TimeUtil.timeFormat2TimeStamp(selectDate.getText().toString() + " " + endTime);
                    if(startTime>end){
                        ToastUtil.showToast("开始时间不能大于结束时间哦",context);
                        return;
                    }
                }
                selectEndTime.setText(endTime);
            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=selectDate.getText().toString();
                String startTime=selectStartTime.getText().toString();
                String endTime=selectEndTime.getText().toString();
                if(!StringUtil.isNotEmpty(date)){
                    ToastUtil.showToast("日期是必选项",context);
                    return;
                }
                if(StringUtil.isNotEmpty(startTime)){
                    if(StringUtil.isNotEmpty(endTime)){
                        onQueryInParticularTimeListener.query(date,startTime,endTime);
                    }else{
                        onQueryInParticularTimeListener.query(date,startTime,false);
                    }
                }else{
                    if(StringUtil.isNotEmpty(endTime)){
                        onQueryInParticularTimeListener.query(date,endTime,true);
                    }else{
                        onQueryInParticularTimeListener.query(date);
                    }
                }
                getDialog().cancel();
            }

        });

        setViewLocation();
        getDialog().setCanceledOnTouchOutside(true);//外部点击取消
        return view;
    }



    /**
     * 设置dialog位于屏幕底部
     */
    private void setViewLocation(){
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        window.setWindowAnimations(R.style.ActionSheetDialogStyle);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = height;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        getDialog().onWindowAttributesChanged(lp);
    }


    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case 0:
                    tabNavigation.setCurrentActiveItem(0);
                    break;
                case 1:
                    tabNavigation.setCurrentActiveItem(1);
                    break;
                case 2:
                    tabNavigation.setCurrentActiveItem(2);
                    break;
            }
        }
    }
}
