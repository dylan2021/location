package com.sfmap.api.navi.model;

import java.util.List;

/**
 * 避让区域类
 */

public class NaviAvoidBound {
    private List<NaviLatLng> coords;

    /**
     * 获取避让区域范围坐标点集合
     * @return 坐标点集合。
     */
    public List<NaviLatLng> getCoords() {
        return coords;
    }

    /**
     * 设置避让区域范围坐标点集合,一个避让区域至少需要设置3个范围坐标点,最多设置16个范围坐标点
     * @param coords 避让区域范围坐标集合
     */
    public void setCoords(List<NaviLatLng> coords) {
        this.coords = coords;
    }
}
