package com.yuyi.family.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CommonData implements Serializable {
    private String retCode;
    private String retMessage;
    private String sessionId;
    private long time;//当前时间戳
}
