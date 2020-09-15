package com.sfmap.api.navi.model;

/**
 * 定义服务区，收费站等设施信息。
 */
public class NaviServiceFacilityInfo {

    private int remainDist; // /< 到当前车位的剩余距离（单位米）
    private int type; // /< 设施类型 0表示服务区 1表示收费站
    private String name; // /< 名称，Unicode编码方式
    private NaviLatLng coords;// /<设施所在坐标
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 构造函数。
     */
    public NaviServiceFacilityInfo() {
        remainDist = -1;
        type = 0;
        name = "12345";
        coords=new NaviLatLng();
    }

    /**
     * 获得道路设施类型 0表示服务区 1表示收费站。
     * @return 道路设施类型。
     */
    public int getType() {
        return type;
    }

    /**
     * 设置道路设施类型 0表示服务区 1表示收费站。
     * @param type 道路设施类型。
     */
    public void setType(int type){
        this.type = type;
    }

    /**
     * 获得道路设施到当前车位的剩余距离（单位米）
     * @return 距离值。
     */
    public int getRemainDist() {
        return remainDist;
    }

    /**
     * 设置道路设施到当前车位的剩余距离（单位米）
     * @param remainDist 距离值。
     */
    public void setRemainDist(int remainDist){
        this.remainDist = remainDist;
    }

    /**
     * 获得道路设施所在地坐标
     * @return 设施坐标
     */
    public NaviLatLng getCoords(){
        return coords;
    }

    /**
     * 设置道路设施所在地坐标
     * @param coords 道路设施坐标
     */
    public void setCoords(NaviLatLng coords){
        this.coords=coords;
    }

}
