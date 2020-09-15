package com.sfmap.api.navi.model;

public class ETARoute {
    /**
     *授权ak
     */
    private String ak;
    /**
     *目的地网点纬度
     */
    private String y2;
    /**
     *始发网点纬度
     */
    private String y1;
    /**
     *目的地网点经度
     */
    private String x2;
    /**
     *始发网点经度
     */
    private String x1;
    /**
     *目的地网点
     */
    private String destDeptCode;
    /**
     *车牌(长度5-16)
     */
    private String vehicle;
    /**
     *任务Id
     */
    private String taskId;
    /**
     *始发网点
     */
    private String srcDeptCode;
    /**
     *版本号
     */
    private String appVer;
    /**
     *导航id
     */
    private String naviId;

    //以上是必須參數，以下是非必須參數
    /**
     *燃油标准,可用值:0,1,2,3,4,5,6
     */
    private String emitStand;
    /**
     *频次
     */
    private String frequency;
    /**
     *车辆长度
     */
    private String length;
    /**
     *车辆核定载重
     */
    private String mload;
    /**
     *起点终点围栏距离
     */
    private String fencedist;
    /**
     *	通行证多个通行证用“|”分隔
     */
    private String passport;
    /**
     *	终点类型
     */
    private String etype;
    /**
     *	车牌颜色,可用值:1,2,4,8,16
     */
    private String plateColor;
    /**
     *偏航路重算
     */
    private String reRoute;
    /**
     *路况有效时间
     */
    private String rticDur;
    /**
     *能源类型,可用值:1,2,4,8
     */
    private String energy;
    /**
     *	起点类型
     */
    private String stype;
    /**
     *	车辆高度
     */
    private String height;
    /**
     *测试参数
     */
    private String test;
    /**
     *收费信息
     */
    private String tolls;
    /**
     *	司机id
     */
    private String driverId;
    /**
     *导航方向
     */
    private String vehicleDir;
    /**
     *	车辆类型,可用值:1,4,5,6,7,8,9,10
     */
    private String vehicleType;
    /**
     *	车辆总重
     */
    private String weight;
    /**
     *	车辆宽度
     */
    private String width;
    /**
     *	坐标系(1-GCJ02,2-BD09,3-WGS84),可用值:1,2,3
     */
    private String cc;
    /**
     *	车辆轴重
     */
    private String axleWeight;
    /**
     *	车辆轴数
     */
    private String axleNumber;
    /**
     *	出行规划时间（格式yyyymmddHHMM）
     */
    private String planDate;

    private String reqTime;

    private String compress;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getY2() {
        return y2;
    }

    public void setY2(String y2) {
        this.y2 = y2;
    }

    public String getY1() {
        return y1;
    }

    public void setY1(String y1) {
        this.y1 = y1;
    }

    public String getX2() {
        return x2;
    }

    public void setX2(String x2) {
        this.x2 = x2;
    }

    public String getX1() {
        return x1;
    }

    public void setX1(String x1) {
        this.x1 = x1;
    }

    public String getDestDeptCode() {
        return destDeptCode;
    }

    public void setDestDeptCode(String destDeptCode) {
        this.destDeptCode = destDeptCode;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSrcDeptCode() {
        return srcDeptCode;
    }

    public void setSrcDeptCode(String srcDeptCode) {
        this.srcDeptCode = srcDeptCode;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getNaviId() {
        return naviId;
    }

    public void setNaviId(String naviId) {
        this.naviId = naviId;
    }

    public String getEmitStand() {
        return emitStand;
    }

    public void setEmitStand(String emitStand) {
        this.emitStand = emitStand;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMload() {
        return mload;
    }

    public void setMload(String mload) {
        this.mload = mload;
    }

    public String getFencedist() {
        return fencedist;
    }

    public void setFencedist(String fencedist) {
        this.fencedist = fencedist;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(String plateColor) {
        this.plateColor = plateColor;
    }

    public String getReRoute() {
        return reRoute;
    }

    public void setReRoute(String reRoute) {
        this.reRoute = reRoute;
    }

    public String getRticDur() {
        return rticDur;
    }

    public void setRticDur(String rticDur) {
        this.rticDur = rticDur;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTolls() {
        return tolls;
    }

    public void setTolls(String tolls) {
        this.tolls = tolls;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleDir() {
        return vehicleDir;
    }

    public void setVehicleDir(String vehicleDir) {
        this.vehicleDir = vehicleDir;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getAxleWeight() {
        return axleWeight;
    }

    public void setAxleWeight(String axleWeight) {
        this.axleWeight = axleWeight;
    }

    public String getAxleNumber() {
        return axleNumber;
    }

    public void setAxleNumber(String axleNumber) {
        this.axleNumber = axleNumber;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }
}
