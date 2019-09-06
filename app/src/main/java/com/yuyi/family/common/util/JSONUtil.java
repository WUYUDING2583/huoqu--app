package com.yuyi.family.common.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class JSONUtil {

    private static Gson gson=new Gson();

    public static String ObjectToJson(Object object){
        return gson.toJson(object);
    }

    public static Object JsonToObject(String json,Class pojoClass){
        return gson.fromJson(json,pojoClass);
    }

    public static Object JsonToObject(String json,Type type){
        return gson.fromJson(json,type);
    }

    public static List JsonToList(String json, Type type){
        return gson.fromJson(json, type);
    }
}