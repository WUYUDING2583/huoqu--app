package com.yuyi.family.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuyi.family.R;
import com.yuyi.family.common.util.FileUtil;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.component.CircleImageView;
import com.yuyi.family.pojo.FamilyMember;

import org.w3c.dom.Text;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by Jay on 2015/9/18 0018.
 */
public class FamilyAdapter extends BaseAdapter {

    private LinkedList<FamilyMember> mData;
    private Context mContext;

    public FamilyAdapter(LinkedList<FamilyMember> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.family_member_item,parent,false);
        ImageView portrait = (ImageView) convertView.findViewById(R.id.family_portrait);
        TextView latestAddress = (TextView) convertView.findViewById(R.id.family_latest_address);
        TextView latestTime = (TextView) convertView.findViewById(R.id.family_latest_time);
        TextView name=(TextView)convertView.findViewById(R.id.family_member_name);
        name.setText(mData.get(position).getName());
        portrait.setImageBitmap(FileUtil.FileToBitmap(mData.get(position).getPortrait()));
        latestAddress.setText(mData.get(position).getAddress());
        latestTime.setText(TimeUtil.timeStamp2TimeFormat(mData.get(position).getTime()));
        return convertView;
    }

    public void update(LinkedList<FamilyMember> mData){
        if(mData!=null){
            this.mData=mData;
        }
        notifyDataSetChanged();
    }
}