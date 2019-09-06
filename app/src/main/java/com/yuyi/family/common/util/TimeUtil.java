package com.yuyi.family.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**
     * 获取今日日期
     * @return
     */
    public static String getTodyFromat() {
        Date date=new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 获取某日零时的时间戳
     * @param date
     * @return
     */
    public static long getDayStartTimeStamp(String date){
        date+=" 00:00:00";
        return timeFormat2TimeStamp(date);
    }

    /**
     * 获取某日结束时的时间戳
     * @param date
     * @return
     */
    public static long getDayEndTimeStamp(String date){
        date+=" 23:59:59";
        return timeFormat2TimeStamp(date);
    }

    /**
     * 时间戳转字符串
     * @param timeStamp
     * @return
     */
    public static String timeStamp2TimeFormat(long timeStamp){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeStamp);
        String timeFormat = simpleDateFormat.format(date);
        return timeFormat;
    }

    /***
     * 字符串转时间戳
     * @param timeFormat
     * @return
     */
    public static long timeFormat2TimeStamp(String timeFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeStamp=-1;
        try {
            Date date = simpleDateFormat.parse(timeFormat);
            timeStamp = date.getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return timeStamp;
    }
    /**
     * 获取今日零时的时间戳
     * @return
     */
    public static long getTodyTimeStamp() {
        Date date=new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        return timeFormat2TimeStamp(dateFormat.format(date)+" 00:00:00");
    }

    public static String getFormatDate(int year,int month,int day){
        String str=year+"-";
        if(month<10){
            str+="0"+month;
        }else{
            str+=month;
        }
        if(day<10){
            str+="-0"+day;
        }else{
            str+="-"+day;
        }
        return str;
    }

    public static String getFormatTime(int hour,int minute,int second){
        String str = "";
        if(hour<10){
            str+="0"+hour;
        }else{
            str+=hour;
        }
        str+=":";
        if(minute<10){
            str+="0"+minute;
        }else {
            str+=minute;
        }
        str+=":";
        if(second<10){
            str+="0"+second;
        }else{
            str+=second;
        }
        return  str;
    }

    public static String getFormatTime(int hour,int minute){
        String str = "";
        if(hour<10){
            str+="0"+hour;
        }else{
            str+=hour;
        }
        str+=":";
        if(minute<10){
            str+="0"+minute;
        }else {
            str+=minute;
        }
        str+=":00";
        return str;
    }
}
