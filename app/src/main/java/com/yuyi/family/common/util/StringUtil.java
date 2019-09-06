package com.yuyi.family.common.util;

public class StringUtil {

    public static boolean isNotEmpty(String str){
        if(str!=null&&!str.equals("")) {
            return true;
        }
        return false;
    }
}
