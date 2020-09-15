package com.sfmap.api.navi.model;

public class NaviEndInfo {
    private int endType = 1;//导航结束类型 1-司机自行结束导航（卡死）2-sdk侧判断到达终点 3-闪退结束
    private double x;
    private double y;
    /*
     * 坐标系：
     * 1-GCJ02
     * 2-BD09
     * 3-WGS84
     * 输入2或3需进行转换成1后再进行计算
     */
    private int cc;

    public int getEndType() {
        return endType;
    }

    public void setEndType(int endType) {
        this.endType = endType;
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

    public int getCc() {
        return cc;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }
}
