package com.yuyi.family.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuyi.family.R;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.pojo.LocationResult;

import java.util.LinkedList;


public class LocationAdapter extends BaseAdapter {

    private LinkedList<LocationResult> mData;
    private Context mContext;

    public LocationAdapter(LinkedList<LocationResult> mData, Context mContext){
        this.mData=mData;
        this.mContext=mContext;
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView= LayoutInflater.from(mContext).inflate(R.layout.test_address_list_item,parent,false);
        TextView time,lat,lon,address;
        time=(TextView)convertView.findViewById(R.id.time);
        lat=(TextView)convertView.findViewById(R.id.lat);
        lon=(TextView)convertView.findViewById(R.id.lon);
        address=(TextView)convertView.findViewById(R.id.address);
        String timeFormat= TimeUtil.timeStamp2TimeFormat(mData.get(position).getTime());
        time.setText("时间："+timeFormat);
        lat.setText("纬度："+mData.get(position).getLatitude());
        lon.setText("经度："+mData.get(position).getLongtitude());
        address.setText("地址："+mData.get(position).getAddress());
        return convertView;
    }
}
