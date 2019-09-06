package com.yuyi.family.listener.interfaces;

public interface OnQueryInParticularTimeListener {
    void query(String date);
    void query(String date,String time,boolean isEnd);
    void query(String date,String startTime,String endTime);
}
