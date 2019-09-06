package com.yuyi.family.test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yuyi.family.R;
import com.yuyi.family.pojo.LocationResult;
import com.yuyi.family.test.pojo.WeatherResult;
import com.yuyi.family.test.pojo.WeatherRoot;
import com.yuyi.family.common.util.HttpUtil;
import com.yuyi.family.common.util.JSONUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestHttpActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_http);
//        testStringRequest();
//        testJsonRequest();
        LocationResult params=new LocationResult();
        params.setAddress("asdfasdfa");
        Map<String,String> out=new HashMap<>();
        out.put("1", JSONUtil.ObjectToJson(params));
        HttpUtil.getInstance()
                .sendStringRequest("http://192.168.43.123:8080/Family/servlet/HelloWorld", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
//        HttpUtil.getInstance()
//                .sendStringRequest(Request.Method.POST, HttpUrlInstance.REGISTER,new Response.Listener<String>(){
//                    @Override
//                    public void onResponse(String response){
//                        System.out.println();
//                    }
//                },new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//                        error.printStackTrace();
//                        System.out.println("error:"+error.getMessage());
//                    }
//                },out);
    }

    private void testStringRequest() {
        String url = "http://api.k780.com/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response:", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
            }
        }
        );
        queue.add(stringRequest);
    }

    private void testJsonRequest() {
        String url = "http://api.k780.com/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                WeatherRoot weatherList = new Gson().fromJson(response.toString(), WeatherRoot.class);
                if(weatherList != null) {
                    for(WeatherResult r : weatherList.getResult()) {
                        Log.d("##result##", "city:"+r.getCitynm() + "weather:"+ r.getWeather() +"\n");
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }
}
