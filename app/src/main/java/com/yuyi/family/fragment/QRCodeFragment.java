package com.yuyi.family.fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yuyi.family.R;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.pojo.User;

import java.util.Date;
import java.util.regex.Pattern;

public class QRCodeFragment extends Fragment {

    private static final int MAX_BARCODE_FILENAME_LENGTH = 24;
    private static final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final String USE_VCARD_KEY = "USE_VCARD";


    private User currentUser;


    public static QRCodeFragment newInstance()
    {
        QRCodeFragment fragment = new QRCodeFragment();
        fragment.currentUser=ApplicationUtil.getCurrentUser();
        fragment.currentUser.setTime(new Date().getTime());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_code_fragment, container, false);
        ImageView imageView=(ImageView)view.findViewById(R.id.qr_code);
        Bitmap mBitmap = CodeUtils.createImage(JSONUtil.ObjectToJson(currentUser), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageView.setImageBitmap(mBitmap);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        // This assumes the view is full screen, which is a good assumption

    }

    private void showErrorMessage(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationUtil.getContext());
        builder.setMessage(message);
        builder.show();
    }

}
