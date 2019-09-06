package com.yuyi.family.common.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.pojo.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

    public static Bitmap GetImageFromUrl(String imageurl){
        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap=BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap FileToBitmap(String path){
        Bitmap bitmap=null;
        try {
            FileInputStream fis = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(fis);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String BitmapToBase64(Bitmap bitmap){
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush(); baos.close(); byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush(); baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap Base64ToBitmap(String base64String){
        Bitmap bitmap=null;
        try {
            byte[] bitmapArray = Base64.decode(base64String, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存图片到本地相册
     * @param context
     * @param bmp
     */
    public static String saveImageToGallery(Context context, Bitmap bmp, User user) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), CommonConstant.FILE_PATH);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = user.getPhone() + ".jpg";
        String path="";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            path=appDir.getPath()+"/"+fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
        return path;
    }

    public static void storeImageFromUrl(final User user,final Context context){
        HttpUtil.getInstance()
                .sendImageRequest(user.getPortrait(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        String path = FileUtil.saveImageToGallery(context, response, user);
                        System.out.println("current user image path:"+path);
                        User currentUser=ApplicationUtil.getCurrentUser();
                        currentUser.setPortrait(path);
                        ApplicationUtil.setCurrentUser(currentUser);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        if(null!=user.getFamilyMembers()&&user.getFamilyMembers().size()>0) {
            for (int i = 0; i < user.getFamilyMembers().size(); i++) {
                final int index = i;
                HttpUtil.getInstance()
                        .sendImageRequest(user.getFamilyMembers().get(i).getPortrait(), new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                String path = FileUtil.saveImageToGallery(context, response, user.getFamilyMembers().get(index));
                                System.out.println("family user image path:" + path);
                                User currentUser = ApplicationUtil.getCurrentUser();
                                currentUser.getFamilyMembers().get(index).setPortrait(path);
                                ApplicationUtil.setCurrentUser(currentUser);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
            }
        }
    }
}
