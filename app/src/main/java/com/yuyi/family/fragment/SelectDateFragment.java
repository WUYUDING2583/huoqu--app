package com.yuyi.family.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.yuyi.family.R;
import com.yuyi.family.adapter.NumericWheelAdapter;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.component.WheelView;
import com.yuyi.family.listener.interfaces.OnSelectDateListener;

import java.util.Calendar;
import java.util.Locale;


public class SelectDateFragment extends Fragment {

    public SelectDateFragment(){}
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private Context context;
    private static OnSelectDateListener onSelectDateListener;
    public static void setOnSelectDateListener(OnSelectDateListener onSelectDateListener1){
        onSelectDateListener=onSelectDateListener1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_date_fragment, container, false);
        context= ApplicationUtil.getContext();
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        year = (WheelView) view.findViewById(R.id.date_year);
        initYear();
        month = (WheelView) view.findViewById(R.id.date_month);
        initMonth();
        day = (WheelView) view.findViewById(R.id.date_day);
        initDay(curYear,curMonth);


        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        year.setVisibleItems(7);
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        // 设置监听
        Button ok = (Button) view.findViewById(R.id.date_set);
        Button cancel = (Button) view.findViewById(R.id.date_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = TimeUtil.getFormatDate((year.getCurrentItem()+1950),(month.getCurrentItem()+1),(day.getCurrentItem()+1));
                onSelectDateListener.onSelectDate(str);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    /**
     * 初始化年
     */
    private void initYear() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,1950, 2050);
        numericWheelAdapter.setLabel(" 年");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        year.setViewAdapter(numericWheelAdapter);
        year.setCyclic(true);
    }

    /**
     * 初始化月
     */
    private void initMonth() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,1, 12, "%02d");
        numericWheelAdapter.setLabel(" 月");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
    }

    /**
     * 初始化天
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(context,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(" 日");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        day.setViewAdapter(numericWheelAdapter);
        day.setCyclic(true);
    }

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }
}
