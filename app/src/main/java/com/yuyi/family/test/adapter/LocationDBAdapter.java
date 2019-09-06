package com.yuyi.family.test.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuyi.family.common.util.StringUtil;
import com.yuyi.family.pojo.LocationResult;

import java.util.ArrayList;
import java.util.List;

public class LocationDBAdapter {
    private static final String TAG = "LocationDBAdapter";
    private static final String DB_NAME="yuyifamily.db";
    private static final String DB_TABLE="location";
    private static final int DB_VERSION=1;

    private static final String KEY_TIME="time";
    private static final String KEY_LAT="latitude";
    private static final String KEY_LNG="longtitude";
    private static final String KEY_ADDRESS="address";

    private SQLiteDatabase db;
    private Context context;
    private DBOpenHelper dbOpenHelper;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        //数据库第一次创建时被调用
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+DB_TABLE+" ("+KEY_TIME+" text PRIMARY KEY ,"+KEY_LAT+" text not null ,"+KEY_LNG+" text not null ,"+KEY_ADDRESS+" text )");
        }

        //软件版本号发生改变时调用
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
            onCreate(db);
        }
    }

    public LocationDBAdapter(Context context){
        this.context=context;
    }

    public void open() throws SQLiteException {
        dbOpenHelper=new DBOpenHelper(context,DB_NAME,null,DB_VERSION);
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

    public long insert(LocationResult location){
        ContentValues newValues=new ContentValues();

        newValues.put(KEY_TIME,location.getTime());
        newValues.put(KEY_LAT,location.getLatitude());
        newValues.put(KEY_LNG,location.getLongtitude());
        newValues.put(KEY_ADDRESS,location.getAddress());
        return db.insert(DB_TABLE,null,newValues);
    }

    public long deleteAllData(){
        return db.delete(DB_TABLE,null,null);
    }

//    public long updateOneData(User user){
//        ContentValues updateValues=new ContentValues();
//        updateValues.put(KEY_PSW,user.getPassword());
//        return db.update(DB_TABLE,updateValues,KEY_NAME+"="+user.getName(),null);
//    }

    //获取所有的定位
    public List<LocationResult> getAllLocations(){
        Cursor cursor=getLocations();
        int resultCouts=cursor.getCount();
        if(resultCouts==0||!cursor.moveToFirst())
            return null;
        List<LocationResult> locations=new ArrayList<>();
        for(int i=0;i<resultCouts;i++){
            LocationResult location=new LocationResult();
            location.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_TIME))));
            location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))));
            location.setLongtitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LNG))));
            String address=cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
            if(!StringUtil.isNotEmpty(address)){
                address="";
            }
            location.setAddress(address);
            locations.add(location);
            cursor.moveToNext();
        }
        return locations;
    }

    //获取最近一次定位
    public LocationResult getLastLocation(){
        Cursor cursor=getLocations();
        int resultCouts=cursor.getCount();
        if(resultCouts==0||!cursor.moveToFirst())
            return null;
        cursor.moveToLast();
        LocationResult location=new LocationResult();
        location.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_TIME))));
        location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))));
        location.setLongtitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LNG))));
        String address=cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
        if(!StringUtil.isNotEmpty(address)){
            address="";
        }
        location.setAddress(address);
        return location;
    }


    private Cursor getLocations(){
        Cursor results=db.query(DB_TABLE,new String[]{KEY_TIME,KEY_LAT,KEY_LNG,KEY_ADDRESS},null,
                null,null,null,null);
        return results;
    }
}
