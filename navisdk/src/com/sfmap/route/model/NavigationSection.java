package com.sfmap.route.model;

import java.io.Serializable;

public class NavigationSection implements Serializable {
    public int mIndex;
    public byte navigtionAction;
    // 辅助动作
    public byte naviAssiAction;
    public String streetName;
    public int dataLength;
    public int pathlength;
    public int pointNum;
//    public int[] xs;
//    public int[] ys;
    public double[] xs;
    public double[] ys;
    // 道路通行费
    public int tollCost;
    // 收费路段名称
    public String tollPathName;
    // 红绿灯
    public int trafficLights;
}
