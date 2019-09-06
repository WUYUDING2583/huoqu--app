package com.yuyi.family.pojo;

import lombok.Data;

@Data
public class LocationResult extends CommonData {
    private String userPhone;//用户手机号
    private double latitude;//纬度
    private double longtitude;//经度
    private double altitude;//海拔
    private float accuracy;//精度
    private float speed;//速度
    private float bearing;//方向角
    private String buildingId;//室内定位建筑物Id
    private String floor;//室内定位楼层
    private String address;//地址描述
    private String country;//国家
    private String province;//省
    private String city;//城市
    private String district;//城区
    private String street;//街道
    private String streetNum;//街道门牌号
    private String cityCode;//城市编码
    private String adCode;//区域编码
    private String poiName;//当前位置POI名称
    private String aoiName;//当前位置所处AOI名称
    private int gpsAccuracyStatus;//设备当前 GPS 状态
    private int locationType;//定位来源
    private String locationDetail;//定位信息描述
    private String errorInfo;//定位错误信息描述
    private String errorCode;//定位错误码
}
