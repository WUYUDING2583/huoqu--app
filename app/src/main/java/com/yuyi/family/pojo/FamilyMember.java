package com.yuyi.family.pojo;

import lombok.Data;

@Data
public class FamilyMember extends User {
    private String address="";
    private long lastGetLocationTime=0;//上一次获取定位的时间
}
