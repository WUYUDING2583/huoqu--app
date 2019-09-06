package com.yuyi.family.test.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WeatherResult implements Serializable {
    private String weaid;

    private String week;

    private String cityno;

    private String citynm;

    private String cityid;

    private String uptime;

    private String temperature;

    private String humidity;

    private String aqi;

    private String weather;

    private String weather_icon;

    private String wind;

    private String winp;

    private String temp;

    private String weatid;

    private String windid;

    private String winpid;

    private String weather_iconid;
}
