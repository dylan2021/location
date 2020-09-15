package com.sfmap.api.navi.model;

public class ETATmc {
    /**
     *ak
     */
    private String ak;

    /**
     *导航id
     */
    private String naviId;

    /**
     *任务id
     */
    private String taskId;

    /**
     *线路id
     */
    private String routeId;

    /**
     *出行时间（非必传）
     * 格式yyyymmddHHMM，201710010930表示2017年10月1日9点30分。
     * （发车时间，不传默认请求服务时的系统时间）
     */
    private String planDate;

    /**
     *Linkid列表，多个linkid用|分隔
     */
    private String swids;

    /**
     *司机当前位置经度
     */
    private double startX;

    /**
     *司机当前位置纬度
     */
    private double startY;

    /**
     *坐标系：
     * 1-GCJ02
     * 2-BD09
     * 3-WGS84
     */
    private int cc;
    private String appPackageName;
    private String appCerSha1;

    private int compress;

    private String reqTime;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
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

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getSwid() {
        return swids;
    }

    public void setSwid(String swid) {
        this.swids = swid;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public int getCc() {
        return cc;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

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

    public int getCompress() {
        return compress;
    }

    public void setCompress(int compress) {
        this.compress = compress;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }
}
