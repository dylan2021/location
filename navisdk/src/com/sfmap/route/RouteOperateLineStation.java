package com.sfmap.route;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.NavigateArrow;
import com.sfmap.api.maps.model.NavigateArrowOptions;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.util.DeviceUtil;
import com.sfmap.library.util.Projection;
import com.sfmap.map.util.Tools;
import com.sfmap.route.model.ICarRouteResult;
import com.sfmap.route.model.NavigationPath;
import com.sfmap.route.model.NavigationResult;
import com.sfmap.route.model.NavigationSection;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;

import java.util.List;

/**
 * 路线规划的Overlay控制（包括在屏幕中预览整条路线、分段预览）
 */
public class RouteOperateLineStation {

    private Context context;
    private MapController mapController;
    private MapView mapView;

    private static int MAX_ZOOM_LEVEL = 19;
    private static int MIN_ZOOM_LEVEL = 3;

    private int screenVerticalOffSet;
    private int screenHoriticalOffSet;

    // overlay的屏幕边距
    private int leftOffSet;
    private int topOffSet;
    private int rightOffSet;
    private int bottomOffSet;

    private Marker circleMar = null;
    public int zoom_status = 0;

    private NavigateArrow navigateArrow;

    public RouteOperateLineStation(Context context, MapController mapController, MapView mapView) {
        this.mapController = mapController;
        this.context = context;
        this.mapView = mapView;
    }

    /**
     * 设置全部显示时，有效的区域距离地图屏幕显示区域左，上，右，下的dip值。
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setScreenDisplayMargin(int left, int top, int right, int bottom) {
        leftOffSet = left;
        topOffSet = top;
        rightOffSet = right;
        bottomOffSet = bottom;
        screenVerticalOffSet = DeviceUtil.dipToPixel(context, topOffSet
                + bottomOffSet);
        screenHoriticalOffSet = DeviceUtil.dipToPixel(context, leftOffSet
                + rightOffSet);
    }

    public void reSetOffSet() {
        this.setScreenDisplayMargin(40, 100, 40, 120);
    }

    /**
     * 在地图上显示整个驾车路线
     *
     * @param mRouteCarResult
     * @param mCurrentItem
     */
    public void getBounds(ICarRouteResult mRouteCarResult, int mCurrentItem) {
        NavigationResult result = mRouteCarResult.getNaviResultData();
        NavigationPath[] navi_paths = result.paths;
        NavigationPath navigationPath = navi_paths[mCurrentItem];
        NavigationSection[] sections = navigationPath.sections;
        int len = sections.length;

        double x1 = 999999999;
        double y1 = 999999999;
        double x2 = -999999999;
        double y2 = -999999999;

        for (int j = 0; j < len; j++) {
            NavigationSection section = sections[j];
            double[] poXs = section.xs;
            double[] poYs = section.ys;
            int lens = poXs.length;
            for (int h = 0; h < lens; h++) {
                x1 = Math.min(x1, poXs[h]);
                y1 = Math.min(y1, poYs[h]);
                x2 = Math.max(x2, poXs[h]);
                y2 = Math.max(y2, poYs[h]);
            }
        }
        Rect rect = new Rect();
        GeoPoint p1 = Projection.LatLongToPixels(y1, x1, Projection.MAXZOOMLEVEL);
        GeoPoint p2 = Projection.LatLongToPixels(y2, x2, Projection.MAXZOOMLEVEL);
        //84坐标系与墨卡托投影最大和最小方向不一致
        rect.set(p1.x, p2.y, p2.x, p1.y);
        // 得到bound
        float destLevel = getZoomLevelByBound(rect);
//        float curMapLevel = mapContainer.getMapController().getCameraPosition().zoom;
        // 经实验,最终的级别减1才能看到完整的路线。
        //01380761 zhangxuewen 在新的UI中，面积有所缩小，因此级别要再缩小1级
        float disLevel = destLevel - 2;
        // 必须先缩放后确定地图中心
        GeoPoint centerP;
        centerP = getCenter(rect, destLevel);
        if (centerP != null) {
//            mapController.setMapCenter(new LatLng(centerP.getLatitude(), centerP.getLongitude()),
//                    disLevel);
            mapController.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centerP.getLatitude(), centerP.getLongitude()),disLevel));

        } else {
            mapController.animateCamera(CameraUpdateFactory.zoomTo(disLevel));
        }
    }

    private float getZoomLevelByBound(Rect bound) {
        float fZoomScale = 1.0f;
        float screenh = this.mapView.getMeasuredHeight();
        float screenw = this.mapView.getMeasuredWidth();
        float viewh = (screenh - screenVerticalOffSet) * fZoomScale;
        float vieww = (screenw - screenHoriticalOffSet) * fZoomScale;
        float hZoomScale = (float) getZoomLevel(viewh, bound.height());
        float wZoomScale = (float) getZoomLevel(vieww, bound.width());
        float destLevel = Math.min(wZoomScale, hZoomScale);
        destLevel = Math.min(MAX_ZOOM_LEVEL,
                Math.max(MIN_ZOOM_LEVEL, destLevel));
        return destLevel;
    }

    private GeoPoint getCenter(Rect bound, float destLevel) {
        float zoomScale = 1.0f;
        float deltaYMargin = DeviceUtil.dipToPixel(context, bottomOffSet - topOffSet) * zoomScale;
        float deltaXMargin = DeviceUtil.dipToPixel(context, rightOffSet - leftOffSet) * zoomScale;

        int newX = (int) (deltaXMargin * Math.pow(2, 19 - destLevel));
        int newY = (int) (deltaYMargin * Math.pow(2, 19 - destLevel));
        GeoPoint p = new GeoPoint(bound.centerX() + newX, bound.centerY() + newY);
        return p;
    }

    private double getZoomLevel(double toSize, double sourceSize) {
        double size_factor = sourceSize / toSize;
        return 20 - Math.log(size_factor) / Math.log(2);
    }

    /**
     * 展示驾车导航焦点
     *
     * @param mRouteCarResult
     */
    public void showFocusStation(ICarRouteResult mRouteCarResult) {
//        int focusNaviPathIndex = mRouteCarResult.getFocusRouteIndex();
        NavigationPath path = mRouteCarResult.getFocusNavigationPath();
        if (path == null)
            return;

//        // 焦点路段
        int focusPath = mRouteCarResult.getFocusStationIndex();
        List<LatLng> arrowPath = Tools.getNaviArrowData(path, focusPath);
        if (arrowPath == null || arrowPath.size() <= 0) {
            return;
        }
//        mapController.setMapCenter(arrowPath.get(0));
        mapController.moveCamera(CameraUpdateFactory.newLatLngZoom(arrowPath.get(0),MAX_ZOOM_LEVEL - 2));

//        NavigationSection[] sections = path.sections;
//        int len = sections.length;
//        if (focusPath <= 0 || (focusPath >= len + 1)) {
//            if (circleMar != null) {
//                circleMar.remove();
//                circleMar = null;
//            }
//            getBounds(mRouteCarResult, focusNaviPathIndex);
//        } else {
//            // 因为要加上起点和终点
//            --focusPath;
//            if (focusPath < 0 || focusPath >= len) {
//                return;
//            }
//            NavigationSection section = sections[focusPath];
//            double[] poXs = section.xs;
//            double[] poYs = section.ys;
////            GeoPoint focusG = new GeoPoint(poXs[0], poYs[0]);
//
//            // 显示光圈
//            if (circleMar == null) {
//                circleMar = mapContainer.getOverlayManager().addMarker(R.drawable.marker_arc,
//                        new LatLng(poYs[0], poXs[0]),0.5f,0.5f);
//            } else {
//                circleMar.setPosition(new LatLng(poYs[0], poXs[0]));
//            }
////            if (focusG != null) {
//                mapContainer.setMapCenter(new LatLng(poYs[0],poXs[0]),MAX_ZOOM_LEVEL);
////            } else {
////                mapContainer.getMapController().animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM_LEVEL));
////            }
//        }
    }
}
