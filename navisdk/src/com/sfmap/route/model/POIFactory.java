package com.sfmap.route.model;

import com.sfmap.library.model.GeoPoint;

public class POIFactory {
    /**
     * 创建一个基础poi
     *
     * @param name  poi名称
     * @param point 坐标信息
     * @return
     */
    public static POI createPOI(String name, GeoPoint point) {
        POI poi = createNewPOI();
        poi.setPoint(point);
        poi.setName(name);
        return poi;
    }

    /**
     * 创建一个基础poi
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T extends POI> T createPOI(Class<T> type) {

        POI poi = createNewPOI();
        GeoPoint point = new GeoPoint();
        poi.setPoint(point);
        return poi.as(type);
    }

    /**
     * 创建一个基础POI，不含名称和坐标信息
     *
     * @return
     */
    public static POI createPOI() {
        POI poi = createNewPOI();
        GeoPoint point = new GeoPoint();
        poi.setPoint(point);
        return poi;
    }

    private static POI createNewPOI() {
        return new POIBase();
    }
}
