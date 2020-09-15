package com.sfmap.route.model;

public class RouteCarBean {


    private String X1;
    private String Y1;
    private String X2;
    private String Y2;
    private String Type;
    private int Flag;
    private String Vehicle;
    private String Viapoint;
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

    public String getX1() {
        return X1;
    }

    public void setX1(String x1) {
        X1 = x1;
    }

    public String getX2() {
        return X2;
    }

    public void setX2(String x2) {
        X2 = x2;
    }

    public String getY1() {
        return Y1;
    }

    public void setY1(String y1) {
        Y1 = y1;
    }

    public String getY2() {
        return Y2;
    }

    public void setY2(String y2) {
        Y2 = y2;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public String getVehicle() {
        return Vehicle;
    }

    public void setVehicle(String vehicle) {
        Vehicle = vehicle;
    }

    public String getViapoint() {
        return Viapoint;
    }

    public void setViapoint(String viapoint) {
        Viapoint = viapoint;
    }


}
