package com.sfmap.route.model;

import com.sfmap.library.model.GeoPoint;

import java.io.Serializable;

public class JamInfo implements Serializable {
    // 拥堵点经度
    public double lon;
    // 拥堵点纬度
    public double lat;
    // 速度
    public int speed;
    public GeoPoint gPoint;
}
