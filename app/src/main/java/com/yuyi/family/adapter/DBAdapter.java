package com.yuyi.family.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.reflect.TypeToken;
import com.yuyi.family.common.util.TimeUtil;
import com.yuyi.family.pojo.LocationResult;
import com.yuyi.family.pojo.User;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter {
    private static final String TAG = "DBAdapter";
    private static final String DB_NAME="yuyifamily.db";
    private static final String DB_TABLE_USER="user";
    private static final String DB_TABLE_LOCATION="location";
    private static final int DB_VERSION=1;

    private static final String KEY_USER_PHONE="phone";
    private static final String KEY_USER_NAME="name";
    private static final String KEY_USER_ID="id";
    private static final String KEY_USER_PORTRAIT="protrait";
    private static final String KEY_USER_FAMILYMEMBER="familyMember";

    private static final String KEY_LOCATION_TIME="time";
    private static final String KEY_LOCATION_LAT="latitude";
    private static final String KEY_LOCATION_LNG="longtitude";
    private static final String KEY_LOCATION_ADDRESS="address";
    private static final String KEY_LOCATION_ACCURACY="accuracy";

    private SQLiteDatabase db;
    private Context context;
    private DBAdapter.DBOpenHelper dbOpenHelper;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        //数据库第一次创建时被调用
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+DB_TABLE_USER+" ("+KEY_USER_PHONE+" text PRIMARY KEY ,"+KEY_USER_NAME+" text not null ,"+KEY_USER_ID
                    +" text ,"+KEY_USER_PORTRAIT+" text ,"+KEY_USER_FAMILYMEMBER+" text)");
            db.execSQL("CREATE TABLE "+DB_TABLE_LOCATION+" ("+KEY_LOCATION_TIME+" text PRIMARY KEY ,"+KEY_LOCATION_LAT+" text not null ,"+KEY_LOCATION_LNG
                    +" text not null ,"+KEY_LOCATION_ADDRESS+" text ,"+KEY_LOCATION_ACCURACY+" text)");
        }

        //软件版本号发生改变时调用
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE_LOCATION);
            db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE_USER);
            onCreate(db);
        }
    }

    public DBAdapter(Context context){
        this.context=context;
    }

    public void open() throws SQLiteException {
        dbOpenHelper=new DBAdapter.DBOpenHelper(context,DB_NAME,null,DB_VERSION);
        try{
            db=dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException ex){
            db=dbOpenHelper.getReadableDatabase();
        }
    }

    public void close(){
        if(db!=null){
            db.close();
            db=null;
        }
    }

    public long insertUser(User user){
        ContentValues newValues=new ContentValues();

        newValues.put(KEY_USER_PHONE,user.getPhone());
        newValues.put(KEY_USER_NAME,user.getName());
        newValues.put(KEY_USER_ID,user.getId());
        newValues.put(KEY_USER_PORTRAIT,user.getPortrait());
        newValues.put(KEY_USER_FAMILYMEMBER, JSONUtil.ObjectToJson(user.getFamilyMemberPhone()));
        return db.insert(DB_TABLE_USER,null,newValues);
    }

    public long removeAllUsers(){
        return db.delete(DB_TABLE_USER,null,null);
    }

//    public long updateOneData(User user){
//        ContentValues updateValues=new ContentValues();
//        updateValues.put(KEY_PSW,user.getPassword());
//        return db.update(DB_TABLE,updateValues,KEY_NAME+"="+user.getName(),null);
//    }

    private List<User> convertToUsers(Cursor cursor){
        int resultCouts=cursor.getCount();
        if(resultCouts==0||!cursor.moveToFirst())
            return null;
        List<User> users=new ArrayList<>();
        for(int i=0;i<resultCouts;i++){
            User user=new User();
            user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_USER_PHONE)));
            user.setName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
            user.setFamilyMemberPhone((List<String>) JSONUtil.JsonToList(cursor.getString(cursor.getColumnIndex(KEY_USER_FAMILYMEMBER)),new TypeToken<List<String>>(){}.getType()));
            user.setId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
            user.setPortrait(cursor.getString(cursor.getColumnIndex(KEY_USER_PORTRAIT)));
            users.add(user);
            cursor.moveToNext();
        }
        return users;
    }

    //获取本地存储的用户信息
    public List<User> getUsers(){
        Cursor results=db.query(DB_TABLE_USER,new String[]{KEY_USER_PHONE,KEY_USER_NAME,KEY_USER_ID,KEY_USER_PORTRAIT,KEY_USER_FAMILYMEMBER},null,
                null,null,null,null);
        return convertToUsers(results);
    }

    public long insertLocation(LocationResult location){
        ContentValues newValues=new ContentValues();

        newValues.put(KEY_LOCATION_TIME,location.getTime());
        newValues.put(KEY_LOCATION_LAT,location.getLatitude());
        newValues.put(KEY_LOCATION_LNG,location.getLongtitude());
        newValues.put(KEY_LOCATION_ADDRESS,location.getAddress());
        newValues.put(KEY_LOCATION_ACCURACY,location.getAccuracy());
        return db.insert(DB_TABLE_LOCATION,null,newValues);
    }

    public long removeAllLocations(){
        return db.delete(DB_TABLE_LOCATION,null,null);
    }

//    public long updateOneData(User user){
//        ContentValues updateValues=new ContentValues();
//        updateValues.put(KEY_PSW,user.getPassword());
//        return db.update(DB_TABLE,updateValues,KEY_NAME+"="+user.getName(),null);
//    }

    //根据手机号获取定位
    public List<LocationResult> getLocationsByPhone(String phone){
        Cursor cursor =  db.rawQuery("SELECT * FROM "+DB_TABLE_USER+" WHERE "+KEY_USER_PHONE+" = ?",
                new String[]{phone});
        int resultCouts=cursor.getCount();
        if(resultCouts==0||!cursor.moveToFirst())
            return null;
        List<LocationResult> locations=new ArrayList<>();
        for(int i=0;i<resultCouts;i++){
            LocationResult location=new LocationResult();
            location.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_TIME))));
            location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_LAT))));
            location.setLongtitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_LNG))));
            String address=cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ADDRESS));
            if(!StringUtil.isNotEmpty(address)){
                address="";
            }
            location.setAccuracy(Float.parseFloat(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ACCURACY))));
            location.setAddress(address);
            locations.add(location);
            cursor.moveToNext();
        }
        return locations;
    }

    //获取所有的定位
    public List<LocationResult> getAllLocations(){
        Cursor cursor=getLocations();
        int resultCouts=cursor.getCount();
        if(resultCouts==0||!cursor.moveToFirst())
            return null;
        List<LocationResult> locations=new ArrayList<>();
        for(int i=0;i<resultCouts;i++){
            LocationResult location=new LocationResult();
            location.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_TIME))));
            location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_LAT))));
            location.setLongtitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_LNG))));
            String address=cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ADDRESS));
            if(!StringUtil.isNotEmpty(address)){
                address="";
            }
            location.setAccuracy(Float.parseFloat(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ACCURACY))));
            location.setAddress(address);
            locations.add(location);
            cursor.moveToNext();
        }
        return locations;
    }

    //获取当日定位信息
    public List<LocationResult> getTodayLocations(){
        long time= TimeUtil.timeFormat2TimeStamp(TimeUtil.getTodyFromat()+" 00:00:00");
        List<LocationResult> list=getAllLocations();
        List<LocationResult> result=new ArrayList<>();
        if(null!=list&&list.size()>0) {
            for (LocationResult location : list) {
                if (location.getTime() > time) {
                    result.add(location);
                }
            }
        }
        return result;
    }

    //获取最近一次定位
    public LocationResult getLastLocation(){
        Cursor cursor=getLocations();
        int resultCouts=cursor.getCount();
        if(resultCouts==0||!cursor.moveToFirst())
            return null;
        cursor.moveToLast();
        LocationResult location=new LocationResult();
        location.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_TIME))));
        location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_LAT))));
        location.setLongtitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_LNG))));
        String address=cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ADDRESS));
        if(!StringUtil.isNotEmpty(address)){
            address="";
        }
        location.setAccuracy(Float.parseFloat(cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ACCURACY))));
        location.setAddress(address);
        return location;
    }


    private Cursor getLocations(){
        Cursor results=db.query(DB_TABLE_LOCATION,new String[]{KEY_LOCATION_TIME,KEY_LOCATION_LAT,KEY_LOCATION_LNG,KEY_LOCATION_ADDRESS,KEY_LOCATION_ACCURACY},null,
                null,null,null,null);
        return results;
    }
}
