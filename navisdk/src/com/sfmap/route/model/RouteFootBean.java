package com.sfmap.route.model;

public class RouteFootBean {


    private String origin;


    private String destination;
    private String waypoints;


    private String routeType;


    private String ver;


    private String No;//IMEI_时间戳


    private String vehicle;


    /**
     * 包名
     */
    private String appPackageName;

    /**
     * sha1
     */
    private String appCerSha1;

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppCerSha1() {
        return appCerSha1;
    }

    public void setAppCerSha1(String appCerSha1) {
        this.appCerSha1 = appCerSha1;
    }


    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
}
