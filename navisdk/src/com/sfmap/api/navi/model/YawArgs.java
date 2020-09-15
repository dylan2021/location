package com.sfmap.api.navi.model;

import java.util.List;

public class YawArgs {
    //********************必填參數*********************//
    /**
     *	授权ak
     */
    private String ak;

    /**
     *版本号
     */
    private String appVer;

    /**
     *任务Id
     */
    private String taskId;

    /**
     *导航id
     */
    private String naviId;

    /**
     *线路id
     */
    private String routeId;

    /**
     *车牌(长度5-16)
     */
    private String vehicle;

    /**
     *导航方向
     */
    private String vehicleDir;

    /**
     *	出行规划时间（格式yyyymmddHHMM）
     */
    private String planDate;

    private String reqTime;

    private String swids;

    private String srcDeptCode;

    private String destDeptCode;

    private String x2;

    private String y2;

    private String cc;

    private String driverId;

    private String vehicleType;

    private String compress;

    /**
     *轨迹点数组
     */
    private List<Tracks> tracks;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getNaviId() {
        return naviId;
    }

    public void setNaviId(String naviId) {
        this.naviId = naviId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleDir() {
        return vehicleDir;
    }

    public void setVehicleDir(String vehicleDir) {
        this.vehicleDir = vehicleDir;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public List<Tracks> getTracks() {
        return tracks;
    }

    public void setTracks(List<Tracks> tracks) {
        this.tracks = tracks;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public String getSwids() {
        return swids;
    }

    public void setSwids(String swids) {
        this.swids = swids;
    }

    public String getSrcDeptCode() {
        return srcDeptCode;
    }

    public void setSrcDeptCode(String srcDeptCode) {
        this.srcDeptCode = srcDeptCode;
    }

    public String getDestDeptCode() {
        return destDeptCode;
    }

    public void setDestDeptCode(String destDeptCode) {
        this.destDeptCode = destDeptCode;
    }

    public String getX2() {
        return x2;
    }

    public void setX2(String x2) {
        this.x2 = x2;
    }

    public String getY2() {
        return y2;
    }

    public void setY2(String y2) {
        this.y2 = y2;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }
}
