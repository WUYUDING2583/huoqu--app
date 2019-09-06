package com.yuyi.family.common.util;

import android.net.Uri;
import android.os.Environment;
import android.view.View;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.TakePhotoOptions;

import java.io.File;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.devio.org
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public class CustomHelper {
    private View rootView;

    public static CustomHelper of(View rootView) {
        return new CustomHelper(rootView);
    }

    private CustomHelper(View rootView) {
        this.rootView = rootView;
    }

    public void onClick(String type, TakePhoto takePhoto) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        switch (type) {
            case "takePhoto":
                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                break;
            case "selectPhoto":
                int limit = 1;//最多选择多少张图片
                if (limit > 1) {
                    takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                    return;
                }
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                break;
            default:
                break;
        }
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //使用自带相册？
        builder.setWithOwnGallery(false);
//        纠正照片角度？
//        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 10240;//大小不超过
        int width = 200;//大小不超过多宽
        int height = 200;//大小不超过多高
        boolean showProgressBar = true;//是否显示压缩进度条
        boolean enableRawFile = true;//压缩后是否保存原图
        CompressConfig config;
        //压缩工具：自带
        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();

        //压缩工具：鲁班
//        LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
//        config = CompressConfig.ofLuban(option);
//        config.enableReserveRaw(enableRawFile);

        takePhoto.onEnableCompress(config, showProgressBar);
    }

    private CropOptions getCropOptions() {
        int height = 200;//高
        int width = 200;//宽
        boolean withWonCrop = true;//压缩工具是否为第三方
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }
}
