package com.yuyi.family.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.yuyi.family.R;
import com.yuyi.family.adapter.DBAdapter;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.component.BottomDialog;
import com.yuyi.family.component.CircleImageView;
import com.yuyi.family.component.EditTextWithDel;
import com.yuyi.family.pojo.User;
import com.yuyi.family.common.util.CustomHelper;
import com.yuyi.family.common.util.FileUtil;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.StringUtil;
import com.yuyi.family.common.util.ToastUtil;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends TakePhotoActivity implements View.OnClickListener{

    private Context context;
    private String phoneNum,localImagePath="";
    private EditTextWithDel name;//id;
    private Button register,imageButton;
    private DBAdapter dbAdapter;
    private CircleImageView image;//头像

    private CustomHelper customHelper;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_register, null);
        setContentView(contentView);
        customHelper = CustomHelper.of(contentView);
        context=RegisterActivity.this;
        Intent intent=getIntent();
        phoneNum=intent.getStringExtra("phone");
        dbAdapter=new DBAdapter(context);
        dbAdapter.open();
        bindView();
    }

    private void bindView(){
        name=(EditTextWithDel)findViewById(R.id.register_name);
//        id=(EditTextWithDel)findViewById(R.id.register_id);
        register=(Button)findViewById(R.id.register_button);
        image=(CircleImageView)findViewById(R.id.register_image);
        imageButton=(Button)findViewById(R.id.register_image_button);

        image.setVisibility(View.GONE);

        image.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.register_button:
                String nameText=name.getText().toString();
//                String idText=id.getText().toString();
                if(nameText.equals("")) {
                    ToastUtil.showToast("真实姓名不能为空",context);
                    name.setShakeAnimation();
                    return;
                }
                if(!StringUtil.isNotEmpty(localImagePath)){
                    ToastUtil.showToast("请选择头像",context);
                    return;
                }
//                if(idText.equals("")){
//                    Toast.makeText(context,"身份证号不能为空",Toast.LENGTH_LONG).show();
//                    id.setShakeAnimation();
//                }
//                String pattern="(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
//                if(!Pattern.matches(pattern, idText)){
//                    id.setShakeAnimation();
//                    Toast.makeText(context,"身份证格式错误",Toast.LENGTH_LONG).show();
//                    return;
//                }
                User user=new User();
                user.setId("");
                user.setName(nameText);
                user.setPhone(phoneNum);
                //将拍照获取的图片存储到本地
                Bitmap bitmap=FileUtil.FileToBitmap(localImagePath);
                final String path=FileUtil.saveImageToGallery(context,bitmap,user);
                user.setPortrait(FileUtil.BitmapToBase64(bitmap));
                System.out.println("register base64");
                System.out.println(user.getPortrait());
                Map<String,String> out=new HashMap<String,String>();
                out.put("userInfo", JSONUtil.ObjectToJson(user));
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.REGISTER, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response!=null){
                                    User user=(User)JSONUtil.JsonToObject(response,User.class);
                                    if(user.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                        ToastUtil.showToast(user.getRetMessage(),context);
                                        user.setPortrait(path);
                                        System.out.println("path:"+path);
                                        user.setRegister(true);
                                        dbAdapter.insertUser(user);
                                        ApplicationUtil.setCurrentUser(user);
                                        Intent intent=new Intent(context, DrawerActivity.class);
                                        intent.putExtra("fromRegister",1);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }else{
                                        ToastUtil.showToast(user.getRetMessage(),context);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ToastUtil.showToast(error.getMessage(),context);
                            }
                        },out);
                break;
            case R.id.register_image:
            case R.id.register_image_button:
                new BottomDialog(RegisterActivity.this){
                    @Override
                    public void btnPickByTake(){
                        customHelper.onClick("takePhoto",getTakePhoto());
//                        ChooseImage = true;
                        //拍照
                        //点击拍照时做的事
                    }
                    @Override
                    public void btnPickBySelect() {
                        customHelper.onClick("selectPhoto",getTakePhoto());
//                        ChooseImage = true;
                        //相册
                        //点击相册时做的事
                    }

                }.show();
                break;
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        imageButton.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        Glide.with(this).load(new File(result.getImages().get(0).getCompressPath())).into(image);
        localImagePath=result.getImages().get(0).getCompressPath();
    }
}
