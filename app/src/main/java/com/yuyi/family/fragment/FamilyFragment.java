package com.yuyi.family.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yuyi.family.R;
import com.yuyi.family.activity.FamilyMemberMapActivity;
import com.yuyi.family.adapter.FamilyAdapter;
import com.yuyi.family.common.CommonConstant;
import com.yuyi.family.common.util.ApplicationUtil;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.ImageUtil;
import com.yuyi.family.common.util.JSONUtil;
import com.yuyi.family.common.util.ToastUtil;
import com.yuyi.family.listener.interfaces.OnLatestFamilyListener;
import com.yuyi.family.pojo.CommonData;
import com.yuyi.family.pojo.FamilyMember;
import com.yuyi.family.pojo.User;
import com.yuyi.family.service.LocationService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.github.yuweiguocn.lib.squareloading.SquareLoading;


public class FamilyFragment extends Fragment {

    private static final int REQUEST_CODE        = 113;//扫码跳转界面
    private static final int REQUEST_IMAGE       = 102;//调用系统API打开图库
    /**
     * 请求CAMERA权限码
     */
    public static final  int REQUEST_CAMERA_PERM = 100;

    private List<FamilyMember> familyMembers = new LinkedList<>();
    private FamilyAdapter familyAdapter=null;
    private ListView listView;
    private ImageButton add;
    private Context context;
    private SquareLoading loading;
    private User currentUser;
    private TextView txt_empty;
    private LocationService locationService;
//    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.family_fragment, container, false);
        txt_empty = (TextView) view.findViewById(R.id.txt_empty);
        listView=(ListView) view.findViewById(R.id.family_member_list);
        add=(ImageButton)view.findViewById(R.id.add_family_button);
        add.bringToFront();
        loading=(SquareLoading)view.findViewById(R.id.family_loading);
        hideLoading();
        currentUser=ApplicationUtil.getCurrentUser();

        context=ApplicationUtil.getContext();

        init();
        clickEvent();
        return view;
    }


    private void showLoading(){
        listView.setVisibility(View.GONE);
        txt_empty.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        loading.bringToFront();
    }

    private void hideLoading(){
        listView.setVisibility(View.VISIBLE);
        txt_empty.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        add.bringToFront();
        loading.setVisibility(View.GONE);
    }
    private void init(){
       if(null==currentUser.getFamilyMemberPhone()||currentUser.getFamilyMemberPhone().size()==0){
            txt_empty.setText("您还没有添加任何家人，请点击右上角添加哦");
            listView.setEmptyView(txt_empty);
        }else{
            for(int i=0;i<currentUser.getFamilyMembers().size();i++){
                familyMembers.add(currentUser.getFamilyMembers().get(i));
            }
        }
        familyAdapter=new FamilyAdapter((LinkedList<FamilyMember>)familyMembers, ApplicationUtil.getContext());
        listView.setAdapter(familyAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        Intent intent=new Intent(context,LocationService.class);
        context.bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }

    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个locationService对象
            locationService=((LocationService.LocationBinder)service).getService();
            //注册回调接收家人信息的变化
            locationService.setOnLatestFamilyListener(new OnLatestFamilyListener() {
                @Override
                public void onLatestFamily(List<FamilyMember> familyMembers1) {
//                    System.out.println("on latest family update");
//                    System.out.println(JSONUtil.ObjectToJson(familyMembers1));
                    if(familyMembers1.size()>0) {
                        familyMembers.clear();
                        for (int i = 0; i < familyMembers1.size(); i++) {
                            familyMembers.add(familyMembers1.get(i));
                        }
                        familyAdapter.update((LinkedList<FamilyMember>)familyMembers);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 点击事件
     */
    private void clickEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User familyMember=(User)familyAdapter.getItem(position);
                Intent intent=new Intent(context, FamilyMemberMapActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(CommonConstant.BundleKey.FAMILYMEMBER,familyMember);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    System.out.println("onActivityResult: " + result);
                    showLoading();
                    try {
                        Map<String, String> out = new HashMap<String, String>();
                        out.put("currentUserPhone", currentUser.getPhone());
                        User familyMember = (User) JSONUtil.JsonToObject(result,new TypeToken<User>() {}.getType());
                        out.put("familyMemberPhone", familyMember.getPhone());
                        HttpUtil.getInstance()
                                .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.ADDFAMILYMEMBER, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideLoading();
                                        if(response!=null){
                                            System.out.println("in add family");
                                            System.out.println(response);
                                            User data=(User)JSONUtil.JsonToObject(response,User.class);
                                            if(data.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                                ToastUtil.showToast("添加家人成功",context);
                                                ApplicationUtil.setCurrentUser(data);
                                            }else{
                                                ToastUtil.showToast(data.getRetMessage(),context);
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        hideLoading();
                                        ToastUtil.showToast(error.getMessage(),context);
                                    }
                                }, out);
                    }catch (Exception e){
                        e.printStackTrace();
                        ToastUtil.showToast("添加家人失败，二维码不正确",context);
                        hideLoading();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showToast( "解析二维码失败" , context);
                }
            }
        } else if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(context, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            showLoading();
                            try {
                                Map<String, String> out = new HashMap<String, String>();
                                out.put("currentUserPhone", currentUser.getPhone());
                                User familyMember = (User) JSONUtil.JsonToList(result, User.class);
                                out.put("familyMemberPhone", familyMember.getPhone());
                                HttpUtil.getInstance()
                                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.ADDFAMILYMEMBER, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                hideLoading();
                                                if(response!=null){
                                                    User data=(User)JSONUtil.JsonToList(response,User.class);
                                                    if(data.getRetCode().equals(CommonConstant.SERVER_SUCCESS_CODE)){
                                                        ToastUtil.showToast("添加家人成功",context);
                                                    }
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                hideLoading();
                                                ToastUtil.showToast(error.getMessage(),context);
                                            }
                                        }, out);
                            }catch (Exception e){
                                e.printStackTrace();
                                ToastUtil.showToast("添加家人失败，二维码不正确",context);
                                hideLoading();
                            }
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            ToastUtil.showToast( "解析二维码失败" , context);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CAMERA_PERM) {
            ToastUtil.showToast( "从设置页面返回..." , context);
        }


    }
}
