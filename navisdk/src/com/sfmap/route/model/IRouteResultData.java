package com.sfmap.route.model;

/**
 * 路线类的结果集接口
 * 
 */
public interface IRouteResultData extends IResultData {
    /**
     * 设置起点poi
     * 
     * @param fromPOI
     *            poi对象
     */
    public void setFromPOI(POI fromPOI);

    /**
     * 取起点poi
     * 
     * @return 起点poi对象
     */
    public POI getFromPOI();

    /**
     * 设置分享用的起点poi（根据当前起点poi创建一份拷贝）
     * 
     * @return 分享用的起点poi
     */
    public POI getShareFromPOI();

    /**
     * 设置终点poi
     * 
     * @param toPOI
     *            poi对象
     */
    public void setToPOI(POI toPOI);

    /**
     * 取终点poi
     * 
     * @return
     */
    public POI getToPOI();

    /**
     * 取分享用的终点poi对象（根据当前终点poi创建一份拷贝）
     * 
     * @return
     */
    public POI getShareToPOI();

    /**
     * 当前路线偏好方式
     * 
     * @return
     */
    public String getMethod();

    /**
     * 取当前路线偏好
     * 
     * @param method
     */
    public void setMethod(String method);

}
