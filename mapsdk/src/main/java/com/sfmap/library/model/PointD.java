package com.sfmap.library.model;


/**
 * 基本坐标模型
 */
public class PointD {
    /**
     * 经纬度对应的x
     */
    public double x;
    /**
     * 经纬度对应的Y
     */
    public double y;
    /**
     * 构造函数
     * @param x x坐标
     * @param y y坐标
     */
    public PointD(double x, double y) {
	this.x = x;
	this.y = y;
    }

    /**
     * 构造器
     */
    public PointD() {
    }
}


