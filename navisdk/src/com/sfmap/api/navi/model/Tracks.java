package com.sfmap.api.navi.model;

public class Tracks {

    //********************必填參數*********************//
    /**
     *UNIX时间戳(秒，10位数字)
     */
    private long time;

    /**
     *经度
     */
    private double x;

    /**
     *纬度
     */
    private double y;

    /**
     *定位类型（1-GPS，2-前次，5-WIFI，6-基站，8-离线）,可用值:1,2,5,6,8
     */
    private int type;


    //********************選填參數*********************//
    /**
     *	坐标系(1-GCJ02,2-BD09,3-WGS84),可用值:1,2,3
     */
    private int coordinate;

    /**
     *	定位精度
     */
    private float accuracy;

    /**
     *速度 km/h
     */
    private float speed;

    /**
     *方位角
     */
    private double azimuth;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(int coordinate) {
        this.coordinate = coordinate;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }
}
