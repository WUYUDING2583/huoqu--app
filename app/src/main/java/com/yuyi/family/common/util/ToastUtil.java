package com.yuyi.family.common.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(String content, Context context){
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
