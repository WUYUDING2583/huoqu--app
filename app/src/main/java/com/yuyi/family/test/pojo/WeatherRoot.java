package com.yuyi.family.test.pojo;

import java.io.Serializable;
import java.util.List;

public class WeatherRoot implements Serializable {
    private String success;

    private List<WeatherResult> result ;

    public void setSuccess(String success){
        this.success = success;
    }
    public String getSuccess(){
        return this.success;
    }
    public void setResult(List<WeatherResult> result){
        this.result = result;
    }
    public List<WeatherResult> getResult(){
        return this.result;
    }
}
