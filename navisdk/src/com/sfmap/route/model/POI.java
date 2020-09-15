package com.sfmap.route.model;

import com.sfmap.library.model.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 *
 */
public interface POI extends Serializable {

    /**
     * 将当前POI转化成指定type类型的POI
     *
     * @param type 要转化的poi类型
     * @return 返回转化后的对象
     */
    public <T extends POI> T as(Class<T> type);

    /**
     * POI id（顺丰地图标准）
     */
    public String getId();

    public void setId(String id);

    /**
     * POI 类型（顺丰地图 标准 ）
     */
    public String getType();

    public void setType(String type);

    /**
     * 获取POI的商业类型
     *
     * @return
     */
    public String getIndustry();

    /**
     * 设置POI的商业类型
     *
     * @param type
     */
    public void setIndustry(String type);

    /**
     * 地理位置信息 对象永远不能为空
     */
    public GeoPoint getPoint();

    public void setPoint(GeoPoint point);

    public String getName();

    public void setName(String name);

    public String getPhone();

    public void setPhone(String phone);

    public String getCityName();

    public void setCityName(String cityName);

    /**
     * 城市区号？
     */
    public String getCityCode();

    public void setCityCode(String cityCode);

    public String getAddr();

    public void setAddr(String addr);

    /**
     * 城市编码（顺丰地图标准）
     */
    public String getAdCode();

    public void setAdCode(String addrCode);

    /**
     * 图标网络地址
     */
    public String getIconURL();

    public void setIconURL(String iconURL);

    /**
     * OverlayMarker  类型 ID，建议用  markerType 代替
     */
    public int getIconId();

    public void setIconId(int iconId);

    /**
     * 与我现在位置的关系，单位米
     */
    public int getDistance();

    public void setDistance(int distance);

    /**
     * 取到达点坐标（入口）
     *
     * @return
     */
    public ArrayList<GeoPoint> getEntranceList();

    /**
     * 设置到达点坐标(入口)
     *
     * @param inList
     */
    public void setEntranceList(ArrayList<GeoPoint> inList);

    /**
     * 设置到达点坐标（出口）
     *
     * @param exitList
     */
    public void setExitList(ArrayList<GeoPoint> exitList);

    /**
     * 取到达点坐标（出口）
     *
     * @return
     */
    public ArrayList<GeoPoint> getExitList();

    /**
     * 取poi扩展信息
     *
     * @return
     */
    public HashMap<String, Serializable> getPoiExtra();

    public POI clone();
}