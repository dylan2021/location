package com.sfmap.route.car;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.NavigateArrow;
import com.sfmap.api.maps.model.NavigateArrowOptions;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.util.CalculateUtil;
import com.sfmap.library.util.DeviceUtil;
import com.sfmap.map.core.OverlayMarker;
import com.sfmap.navi.R;
import com.sfmap.map.util.CatchExceptionUtil;
import com.sfmap.map.util.ConfigerHelper;
import com.sfmap.map.util.Tools;
import com.sfmap.route.model.ICarRouteResult;
import com.sfmap.route.model.JamInfo;
import com.sfmap.route.model.LocationCodeResult;
import com.sfmap.route.model.NavigationPath;
import com.sfmap.route.model.NavigationResult;
import com.sfmap.route.model.NavigationSection;
import com.sfmap.route.model.POI;
import com.sfmap.route.model.SegLocationCodeStatus;
import com.sfmap.route.util.RoutePlanUtil;
import com.sfmap.tbt.RestrictionArea;
import com.sfmap.tbt.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.sfmap.map.core.OverlayMarker.MARKER_TEST_CAR_ICON;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_ARROW_ONLY;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_BUS;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_LINK_DOTT;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_TRAFFIC_BAD;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_TRAFFIC_BADER;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_TRAFFIC_GREEN;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_TRAFFIC_NO_DATA;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_TRAFFIC_SLOW;
import static com.sfmap.map.core.OverlayMarker.MARKER_TLINE_WALK;

/**
 * 对驾车线路数据结果的管理 主要是抽取写在 result里的方法
 * 1.包括 添加数据到 地图
 * 2.对数据结果进行再加工，生成应用层需要的数据
 */

public class RouteCarDrawMapLineTools {

    // 线链接的容差
    public static final int LINE_SEG_CAP = 50;
    // 线链接的容差,单位度
    public static final double LINE_SEG_CAP_D = 0.000067;//7.46米;
    // m_colorlineTextureID
    public static final int TLINE_WALK = MARKER_TLINE_WALK;
    // 此纹理id主要用于未知路况的情况
    public static final int TLINE_BUS = MARKER_TLINE_BUS;
    // 超级拥堵
    public static final int TLINE_TRAFFIC_BADER = MARKER_TLINE_TRAFFIC_BADER;
    // 严重拥堵
    public static final int TLINE_TRAFFIC_BAD = MARKER_TLINE_TRAFFIC_BAD;
    // 缓行
    public static final int TLINE_TRAFFIC_SLOW = MARKER_TLINE_TRAFFIC_SLOW;
    // 畅通
    public static final int TLINE_TRAFFIC_GREEN = MARKER_TLINE_TRAFFIC_GREEN;
    // 无实时交通
    public static final int TLINE_TRAFFIC_NO_DATA = MARKER_TLINE_TRAFFIC_NO_DATA;
    // m_arrowlineTextureID
    public static final int TLINE_ARROW = OverlayMarker.MARKER_TLINE_ARROW;
    // m_arrowonlylineTextureID
    public static final int TLINE_ARROW_ONLY = MARKER_TLINE_ARROW_ONLY;
    // m_linkdottTextureID
    public static final int TLINE_LINK_DOTT = MARKER_TLINE_LINK_DOTT;

    private Context context;
    private ICarRouteResult carRouteResult = null;
    private POI fromPOI;
    private MapController mapController;
    private NavigateArrow navigateArrow;
    private int mWidth = 40;

    public RouteCarDrawMapLineTools(Context context, ICarRouteResult carResult,
                                    MapController mapController) {
        this.context = context;
        this.carRouteResult = carResult;
        this.fromPOI = carResult.getFromPOI();
        this.mapController = mapController;
        mWidth = Utils.dp2px(context,12);
    }

    /**
     * 路线结果更改后更新数据
     *
     * @param result
     */
    public void setCarRouteResult(ICarRouteResult result) {
        carRouteResult = result;
    }

    /**
     * 绘制焦点路径
     *
     * @param isBrowserMode
     */
    public void addLineToOverlay(boolean isBrowserMode) {

        if (carRouteResult == null || !carRouteResult.hasData())
            return;

        NavigationPath path = carRouteResult.getFocusNavigationPath();
        if (path == null)
            return;
        final int width = DeviceUtil.dipToPixel(context,
                ConfigerHelper.CAR_ROUTE_WIDTH);
        // 焦点路段
        final int focusPath = carRouteResult.getFocusStationIndex();
        addFocusPathToOverlayEx(path, width, isBrowserMode);
        addArrowToOverlayEx(focusPath);

    }

    /**
     * 绘制多条路径
     *
     * @param isBrowserMode
     */
    public void addLineToOverlays(boolean isBrowserMode) {
        addLineToOverlaysEx(isBrowserMode);
    }

    private void addLineToOverlaysEx(boolean isBrowserMode) {

        if (carRouteResult == null || !carRouteResult.hasData())
            return;

        NavigationResult carNavigationResult = carRouteResult
                .getNaviResultData();

        NavigationPath[] paths = carNavigationResult.paths;

        int navigationPathsLength = paths.length;

        int focusNaviPathIndex = carRouteResult.getFocusRouteIndex();
        int notFocusLineColor = RoutePlanUtil.getLineStateColor(0);
        int width = DeviceUtil
                .dipToPixel(context, ConfigerHelper.CAR_ROUTE_WIDTH);
        if (focusNaviPathIndex > paths.length - 1) { // 防止出现数组越界
            focusNaviPathIndex = paths.length - 1;
        }
        for (int j = 0; j < navigationPathsLength; j++) {
            if (focusNaviPathIndex != j) { // add no focus route
                addLineItemToLineOverlay(paths[j],
                        MARKER_TLINE_TRAFFIC_NO_DATA, notFocusLineColor,
                        width);
            }
        }
//        if (focusNaviPathIndex != paths.length - 1) {
//            addLineItemToLineOverlay(paths[paths.length - 1],
//                    OverlayMarker.MARKER_TLINE_TRAFFIC_NO_DATA, notFocusLineColor, width);
//        }
        addFocusPathToOverlayEx(paths[focusNaviPathIndex], width, isBrowserMode);
        addDestrictMarkerToOverlay(paths[focusNaviPathIndex]);
    }

    // 彩色线
    private void addFocusPathToOverlayEx(NavigationPath carPath, int width, boolean isBrowserMode) {

        if (carPath == null)
            return;
        int pathLength = carPath.pathlength;
        // 路线超过100公里时不显示速度tip
        int showSpeedLength = NavigationPath.MIN_TMC_DISTANCE;
        // 路线最大路况距离限制
        int maxTMCLength = NavigationPath.MAX_TMC_DISTANCE;
        int lineIndex = 0;
        int sectionNum = carPath.sectionNum;

        int stationCount = 0;
        double sx = 0;
        double sy = 0;
        for (int k = 0; k < carPath.sections.length; k++) { //01380761 zhangxuewen 存在无路的可能，因此要遍历到有路的地方
            if (carPath.sections[k].xs.length != 0 && carPath.sections[k].ys.length != 0) {
                sx = carPath.sections[0].xs[0];
                sy = carPath.sections[0].ys[0];
                break;
            }
        }
        // 连接起点和道路 用黑色显示

        double fromX = carRouteResult.getFromPOI().getPoint().getLongitude();
        double fromY = carRouteResult.getFromPOI().getPoint().getLatitude();

//        double fromX = sx;
//        double fromY = sy;
//        GeoPoint startG = new GeoPoint(fromX, fromY);

        //大于容差这认为不相同
        if (Math.abs(fromX - sx) > 0.0000001 || Math.abs(fromY - sy) > 0.0000001) {
            // 添加连线 显示
            lineIndex = addLine(fromX, fromY, sx, sy, width);
        }

        stationCount = 0;
        // 添加起点marker
        addMarker(R.drawable.bubble_start, new LatLng(fromY, fromX));
        stationCount++;

        double[] xs, ys;
        int lineWidth = DeviceUtil.dipToPixel(context,
                ConfigerHelper.CAR_ROUTE_WIDTH);

        NavigationSection section;
        boolean needShowTmc = true;
        List<LatLng> stackGeoPoint = new ArrayList<LatLng>();
        boolean hasDrawMidPoint = false;
        for (int i = 0; i < sectionNum; i++) {
            section = carPath.sections[i];
            xs = section.xs;
            ys = section.ys;

            if (xs.length < 2)
                continue;
            if (pathLength >= maxTMCLength && !isBrowserMode) {
                if (fromPOI != null && needShowTmc) {
                    float dis = CalculateUtil.getDistance(fromY, fromX, ys[0], xs[0]);
                    if (dis >= showSpeedLength) {
                        needShowTmc = false;
                    }
                }

                if (!needShowTmc) { // 距离大于500公里时不显示距离起点在200公里以外的路线的路况
                    int xsLength = xs.length;
                    int j;
                    for (j = 0; j < xsLength - 1; j++) {
                        stackGeoPoint.add(new LatLng(ys[j], xs[j], false));
                    }
                    if (i + 1 < sectionNum && (Math.abs(xs[j] - carPath.sections[i + 1].xs[0]) < LINE_SEG_CAP_D && Math.abs(ys[j] - carPath.sections[i + 1].ys[0]) < LINE_SEG_CAP_D)) {
                        // 不添加最后一个点
                    } else { // 添加最后一个点
                        stackGeoPoint.add(new LatLng(ys[j], xs[j], false));
                    }
                    if (i > 0 && !hasDrawMidPoint) {
                        // 有途经点
                        if (carRouteResult.hasMidPos()) {
                            // 辅助动作为 途经点
                            if (carPath.sections[i - 1].naviAssiAction == 0x23) {
                                hasDrawMidPoint = true;
                                addMidPoiToCache(lineIndex, stationCount++);
                            }
                        }
                    }
                    continue;
                }
            }

            // 添加但不显示
            if (isBrowserMode) {
                lineIndex = addLine(xs, ys, OverlayMarker.MARKER_NOT_SHOW,
                        RoutePlanUtil.getLineStateColor(0), lineWidth);
            }

            // 扩展点
            LatLng[] tailerPoints = null;
            int pointCount = xs.length;
            if (i < sectionNum - 1) {
                tailerPoints = getTailer1Points(xs[pointCount - 1],
                        ys[pointCount - 1], carPath.sections[i + 1].xs,
                        carPath.sections[i + 1].ys, 0);
            }

            // 添加彩线
            addSegLineEx(carPath.location_code_result, i, xs, ys, tailerPoints);

            // 添加TBT提示点
            // String tiptitle = "进入";
            if (i > 0 && !hasDrawMidPoint) {
                // 有途经点
                if (carRouteResult.hasMidPos()) {
                    // 辅助动作为 途经点
                    if (carPath.sections[i - 1].naviAssiAction == 0x23) {
                        addMidPoiToCache(lineIndex, stationCount++);
                        hasDrawMidPoint = true;
                    }
                }
            }
        }

        int size = stackGeoPoint.size();
        if (size > 0) {
            addLine(stackGeoPoint, TLINE_TRAFFIC_GREEN, RoutePlanUtil.getLineStateColor(1), width);
            stackGeoPoint.clear();
        }
        if (!isBrowserMode && pathLength < showSpeedLength
                && !carRouteResult.isNative()) { // 距离小于200公里时才在地图显示速度标签,离线导航
            if (carPath.jamInfo != null && carPath.jamInfo.size() > 0) {
                int nJamCount = carPath.jamInfo.size();
                int[] anthorList = new int[nJamCount];

                JamInfo j0 = carPath.jamInfo.get(0);
                Rect rect = new Rect();
                rect.left = j0.gPoint.x;
                rect.right = j0.gPoint.x;
                rect.top = j0.gPoint.y;
                rect.bottom = j0.gPoint.y;

                for (int t = 1; t < nJamCount; t++) {
                    JamInfo _tmp = carPath.jamInfo.get(t);
                    updateRect(rect, _tmp.gPoint);
                }

                int anthor0 = getAnthorFromRect(rect, j0.gPoint);
                anthorList[0] = anthor0;

                if (nJamCount == 2) {
                    anthorList[1] = revertAnchorEx(anthor0);
                } else if (nJamCount > 2) {
                    JamInfo jamPre = j0;
                    int lastAnthor = anthor0;

                    for (int t = 1; t < nJamCount; t++) {
                        JamInfo _tmp = carPath.jamInfo.get(t);
                        int nNewAnthor = getAnthorFromRect(rect, _tmp.gPoint);
                        if (nNewAnthor == lastAnthor) {
                            nNewAnthor = getAliaedAnthorNextPrefer(
                                    jamPre.gPoint, _tmp.gPoint, lastAnthor);
                        }
                        anthorList[t] = nNewAnthor;
                        lastAnthor = nNewAnthor;
                        jamPre = _tmp;
                    }
                }

            }
        }

        if (!isBrowserMode) {
            List<LatLng> pList = new ArrayList<LatLng>();
            for (int i = 0; i < sectionNum; i++) {
                xs = carPath.sections[i].xs;
                ys = carPath.sections[i].ys;
                if (xs.length < 2)
                    continue;
                for (int j = 0; j < xs.length; j++) {
                    pList.add(new LatLng(ys[j], xs[j], false));
                }
            }
            addLine(pList, TLINE_ARROW_ONLY, 0x188AFF, lineWidth);
            pList.clear();
        }

        linePointsCache.commitCache(RouteCarDrawMapLineTools.this);

        xs = carPath.sections[carPath.sections.length - 1].xs;
        ys = carPath.sections[carPath.sections.length - 1].ys;

//        double toX = carRouteResult.getToPOI().getPoint().getLongitude();
//        double toY = carRouteResult.getToPOI().getPoint().getLatitude();

        double toX = xs[xs.length-1];//用最后一个点当终点
        double toY = ys[ys.length-1];
        GeoPoint endG = new GeoPoint(toX, toY);

        // 连接终点和道路 用黑色显示
        //if (toX != xs[xs.length - 1] || ys[ys.length - 1] != toY) {
        if (Math.abs(toX - xs[xs.length - 1]) > 0.0000001 || Math.abs(ys[ys.length - 1] - toY) > 0.0000001) {
            lineIndex = addLine(xs[xs.length - 1], ys[ys.length - 1], toX, toY, lineWidth);
        }
        // 终点
        addMarker(R.drawable.bubble_end,
                new LatLng(endG.getLatitude(), endG.getLongitude()));
        stationCount++;

        carRouteResult.setStationCount(stationCount);
    }

    /**
     * 根据坐标和当前的象限获取相邻的象限值
     */
    private int getAliaedAnthorNextPrefer(GeoPoint pPre, GeoPoint p, int current) {
        int deltaX = p.x - pPre.x;
        int deltaY = p.y - pPre.y;

        if (Math.abs(deltaY) > Math.abs(deltaX)) {
            switch (current) {
                case 0:
                    return 1;
                case 1:
                    return 0;
                case 2:
                    return 3;
                case 3:
                    return 2;
            }
        } else {
            switch (current) {
                case 0:
                    return 3;
                case 1:
                    return 2;
                case 2:
                    return 1;
                case 3:
                    return 0;
            }
        }

        return 0;
    }

    /**
     * 将象限反转
     *
     * @param anchor
     * @return
     */
    private int revertAnchorEx(int anchor) {
        if (anchor == 0) {
            anchor = 2;
        } else if (anchor == 1) {
            anchor = 3;
        } else if (anchor == 2) {
            anchor = 0;
        } else if (anchor == 3) {
            anchor = 1;
        }
        return anchor;
    }

    /**
     * 返回点在矩形里的象限值
     *
     * @param rect ：矩形
     * @param p    ：点
     * @return 象限值
     */
    private int getAnthorFromRect(Rect rect, GeoPoint p) {
        GeoPoint pTemp = new GeoPoint();
        pTemp.x = (rect.left + rect.right) / 2;
        pTemp.y = (rect.top + rect.bottom) / 2;

        return getMapQuadrantEx(p.x, p.y, pTemp.x, pTemp.y);
    }

    /**
     * 以终点为坐标原点取起点坐标所在的象限，将象限值1、2、3、4转化成地图上对应的象限
     *
     * @return
     */
    private int getMapQuadrantEx(int startX, int startY, int endX, int endY) {
        int x = startX - endX;
        int y = startY - endY;
        if (x > 0 && y >= 0) { // 第一象限
            return 0;
        } else if (x < 0 && y >= 0) { // 第二象限
            return 1;
        } else if (x < 0 && y <= 0) { // 第三象限
            return 2;
        } else if (x > 0 && y <= 0) { // 第四象限
            return 3;
        }
        return 0;
    }

    /**
     * 根据点来更新矩形
     *
     * @param rect ：原来的矩形
     * @param p    ：点
     */
    private void updateRect(Rect rect, GeoPoint p) {
        if (rect.left > p.x) {
            rect.left = p.x;
        }

        if (rect.right < p.x) {
            rect.right = p.x;
        }

        if (rect.top > p.y) {
            rect.top = p.y;
        }

        if (rect.bottom < p.y) {
            rect.bottom = p.y;
        }
    }

    /**
     * 添加驾车规划浏览时的3D箭头数据到ArrowLinerOverlay
     *
     * @param focus_station_index
     */
    public void addArrowToOverlayEx(int focus_station_index) {
        if (navigateArrow != null) navigateArrow.remove();
        if (carRouteResult == null || !carRouteResult.hasData())
            return;

        NavigationPath carPath = carRouteResult.getFocusNavigationPath();

        List<LatLng> arrowPath = Tools.getNaviArrowData(carPath, focus_station_index);
        if (arrowPath == null || arrowPath.size() <= 0)
            return;

        // 绘制3d导航箭头
        NavigateArrowOptions options = new NavigateArrowOptions();
        options.topColor(Color.argb(255, 255, 255, 255));
        options.sideColor(Color.argb(255, 0, 0, 0));
        options.addAll(arrowPath).width(mWidth);
        navigateArrow = mapController.addNavigateArrow(options);
    }

    /**
     * 删除驾车规划浏览时的3D箭头数据
     */
    public void removeArrowToOverlayEx() {
        if (navigateArrow != null) navigateArrow.remove();
    }

    /**
     * 添加所有的点到LineOverlay
     */
    private void addLineItemToLineOverlay(NavigationPath naviPath, int texturedid, int color, int width) {

        List<LatLng> stackPoint = new ArrayList<LatLng>();

        int sectionNum = naviPath.sectionNum;
        double xs[], ys[];
        for (int i = 0; i < sectionNum; i++) {
            xs = naviPath.sections[i].xs;
            ys = naviPath.sections[i].ys;
            int xsLength = xs.length;
            int j = 0;
            for (j = 0; j < xsLength - 1; j++) {
                stackPoint.add(new LatLng(ys[j], xs[j], false));
            }
            if (ys.length < j && xs.length < j) {
                if (i + 1 < sectionNum && naviPath.sections[i + 1].xs.length > 0 && naviPath.sections[i + 1].ys.length > 0 && (Math.abs(xs[j] - naviPath.sections[i + 1].xs[0]) < LINE_SEG_CAP_D && Math.abs(ys[0] - naviPath.sections[i + 1].ys[0]) < LINE_SEG_CAP_D)) {
                    // 不添加最后一个点
                } else { // 添加最后一个点
                    stackPoint.add(new LatLng(ys[j], xs[j], false));
                }
            }
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(stackPoint);
        int resId = getTextureResId(context, texturedid);
        if (resId != -1) {
            polylineOptions.width(mWidth).setCustomTexture(BitmapDescriptorFactory.fromResource(resId)); // 纹理绘制
            mapController.addPolyline(polylineOptions);
        }
        stackPoint.clear();
    }

    private POI createMidPoi(int lineId, int index, POI poi) {
        POI tmp = poi.clone();
        tmp.setIconId(OverlayMarker.MARKER_MID);
        return tmp;
    }

    private void addMidPoiToCache(int lineId, int index) {
        if (!carRouteResult.hasMidPos())
            return;
        List<POI> midPoiList = carRouteResult.getMidPOIs();
        if (midPoiList != null && !midPoiList.isEmpty()) {
            int size = midPoiList.size();
            for (int i = 0; i < size; i++) {
                POI poi = midPoiList.get(i);
                POI tmp = createMidPoi(lineId, index, poi);
                GeoPoint startMID = tmp.getPoint();
                if (size == 1) {
                    addMarker(R.drawable.bubble_midd,
                            new LatLng(startMID.getLatitude(), startMID.getLongitude()));
                } else {
                    addMarker(DeviceUtil.dip2px(context, 45),
                            new LatLng(startMID.getLatitude(), startMID.getLongitude()), String.valueOf(i + 1));
                }
            }
        }
//        if (poiList != null && poiList.size() > 0) {
//            for (POI poi : poiList) {
//                POI tmp = createMidPoi(lineId, index, poi);
//                mapContainer.getOverlayManager().addMarker(R.drawable.bubble_midd,
//                        new LatLng(tmp.getPoint().getLatitude(), tmp.getPoint().getLongitude()));
//            }
//        }
    }

    public class LinePointsCache {
        ArrayList<double[][]> linePointsList = new ArrayList<double[][]>();
        int pointsCount = 0;
        int state = 0;

        public void add(double[] xs, double[] ys, int stat) {
            if (xs == null || ys == null || xs.length != ys.length)
                return;
            double[][] tmp = new double[2][xs.length];
            tmp[0] = xs;
            tmp[1] = ys;
            pointsCount += xs.length;
            linePointsList.add(tmp);
            state = stat;
        }

        public void commitCache(RouteCarDrawMapLineTools lineTools) {
            if (pointsCount == 0)
                return;

            double[] allXs = new double[pointsCount];
            double[] allYs = new double[pointsCount];
            double[] tmpXs, tmpYs;
            int startIndex = 0;
            int tmpLen = 0;
            int lineCount = linePointsList.size();
            for (int i = 0; i < lineCount; i++) {
                tmpXs = linePointsList.get(i)[0];
                tmpYs = linePointsList.get(i)[1];
                tmpLen = tmpXs.length;
                System.arraycopy(tmpXs, 0, allXs, startIndex, tmpLen);
                System.arraycopy(tmpYs, 0, allYs, startIndex, tmpLen);
                startIndex += tmpLen;
            }
            int lineWidth = dipToPixel(context,
                    ConfigerHelper.CAR_ROUTE_WIDTH);
            pointsCount = 0;
            linePointsList.clear();

            if (state == 0) {
                lineTools.addLine(allXs, allYs, TLINE_BUS,
                        RoutePlanUtil.getLineStateColor(state), lineWidth);
            } else {
                lineTools.addLine(allXs, allYs, RoutePlanUtil.getLineStateBitmapIndex(state),
                        RoutePlanUtil.getLineStateColor(state), lineWidth);
            }
        }
    }

    RouteCarDrawMapLineTools.LinePointsCache linePointsCache = new RouteCarDrawMapLineTools.LinePointsCache();

    void addSegLineEx(LocationCodeResult pathCodeRes, int segNum, double[] xs, double[] ys,
                      LatLng[] extendPoints) {

        boolean hasStatus = true;
        SegLocationCodeStatus segStatus = null;
        if (pathCodeRes == null || pathCodeRes.res_hash == null
                || pathCodeRes.res_hash.size() == 0) {
            hasStatus = false;
        } else {
            segStatus = pathCodeRes.res_hash.get(segNum + "");
            if (segStatus == null || segStatus.state == null
                    || segStatus.state.length == 0) {//
                hasStatus = false;
            }
        }

        if (xs == null || xs.length == 0 || ys == null || ys.length == 0)
            return;

        // 扩展点
        int pCount = xs.length;
        double[] extndXs = getDoubleArray(xs, 0, pCount - 1, extendPoints, true);
        double[] extndYs = getDoubleArray(ys, 0, pCount - 1, extendPoints, false);
        pCount = extndXs.length;
        xs = extndXs;
        ys = extndYs;
        if (!hasStatus) { // 没有状态
            linePointsCache.add(extndXs, extndYs, 0);
            return;
        }

        // 添加之前没有状态的线
        linePointsCache.commitCache(RouteCarDrawMapLineTools.this);

        // 有状态
        int startIndex = 0;
        int endIndex = 0;

        int[] statuses = segStatus.state;
        int i = 0;
        for (i = 0; i < statuses.length; i++) {
            int state = statuses[i];
            startIndex = segStatus.startIndex[i];

            if (i == statuses.length - 1) {
                endIndex = pCount - 1;
            } else {
                // 计算彩色线索引
                endIndex = segStatus.endIndex[i];
            }
            int lineWidth = dipToPixel(context,
                    ConfigerHelper.CAR_ROUTE_WIDTH);
            // 一个状态段结束添加彩线
            if (RoutePlanUtil.isColorLineState(state)) {
                addLine(getDoubleArray(xs, startIndex, endIndex, null, true),
                        getDoubleArray(ys, startIndex, endIndex, null, false),
                        RoutePlanUtil.getLineStateBitmapIndex(state), RoutePlanUtil.getLineStateColor(state),
                        lineWidth);
            } else { // 未知路况
                addLine(getDoubleArray(xs, startIndex, endIndex, null, true),
                        getDoubleArray(ys, startIndex, endIndex, null, false),
                        TLINE_BUS, ConfigerHelper.BUS_ROUTE_COLOR,
                        lineWidth);
            }
        }
    }

    // 重合一个点
    LatLng[] getTailer1Points(double tailx, double taily, double[] nxtXs, double[] nxtYs,
                              int startIndex) {

        if (nxtXs == null || nxtYs == null || nxtXs.length - startIndex < 1
                || nxtYs.length - startIndex < 1)
            return null;
//        GeoPoint tailGP = new GeoPoint(tailx, taily);
//        GeoPoint nxtGP = new GeoPoint(nxtXs[startIndex], nxtYs[startIndex]);
        double offsetX = tailx - nxtXs[startIndex];
        double offsetY = taily - nxtYs[startIndex];
        double smallCap = LINE_SEG_CAP_D / 2d;
        if (Math.abs(offsetX) < smallCap && Math.abs(offsetY) < smallCap) {
            return null;
        } else if (Math.abs(offsetX) < LINE_SEG_CAP_D && Math.abs(offsetY) < LINE_SEG_CAP_D) {
            nxtXs[startIndex] = tailx;
            nxtYs[startIndex] = taily;
            return null;
        } else {
            LatLng[] tmps = new LatLng[1];
            tmps[0] = new LatLng(nxtYs[startIndex], nxtXs[startIndex]);
            return tmps;
        }
    }

    private double[] getDoubleArray(double[] data, int start, int end,
                                    LatLng[] extendsPoints, boolean xFlag) {
        if (data == null || data.length == 0)
            return null;
        int len = end - start + 1;
        if (len <= 0)
            return null;

        double[] tmps = null;

        if (extendsPoints == null) {

            try {
                if(len + start > data.length){
                    len = data.length-start;
                }
                tmps = new double[len];
                System.arraycopy(data, start, tmps, 0, len);
            }catch (Exception e){

            }

        } else {

            int extendLen = extendsPoints.length;
            tmps = new double[len + extendLen];
            System.arraycopy(data, start, tmps, 0, len);
            for (int i = 0; i < extendLen; i++) {
                try {
                    if (xFlag)
                        tmps[len + i] = extendsPoints[i].longitude;
                    else
                        tmps[len + i] = extendsPoints[i].latitude;
                } catch (Exception e) {
                    CatchExceptionUtil
                            .normalPrintStackTrace(e);
                }
            }
        }
        return tmps;
    }

    private int addLine(double x0, double y0, double x1, double y1, int lineW) {
        LatLng[] points = new LatLng[2];
        points[0] = new LatLng(y0, x0);
        points[1] = new LatLng(y1, x1);
        int result = -1;
        result = addLine(points, TLINE_LINK_DOTT, lineW);
        points = null;
        return result;
    }

    public int addLine(double[] xs, double[] ys, int texturedid, int color, int width) {
        int result = -1;
        if (xs == null || ys == null) {
            return result;
        }
        int len = xs.length;
        LatLng[] points = new LatLng[len];
        for (int i = 0; i < len; i++) {
            points[i] = new LatLng(ys[i], xs[i], false);
        }
        result = addLine(points, texturedid, width);
        points = null;

        return result;
    }

    public int addLine(final LatLng[] stackPoints, int texturedid, int width) {
        int result = -1;
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(stackPoints);
        int resId = getTextureResId(context, texturedid);
        if (resId != -1) {
            // 纹理绘制
            polylineOptions.width(mWidth).setCustomTexture(BitmapDescriptorFactory.fromResource(resId));
            mapController.addPolyline(polylineOptions);
        }
        result = 0;
        return result;
    }

    public int addLine(final List<LatLng> stackPoints, int texturedid, int color, int width) {
        int result = -1;
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(stackPoints);
        width = mWidth;
        int resId = getTextureResId(context, texturedid);
        if (resId != -1) {
            // 纹理绘制
            polylineOptions.width(width).setCustomTexture(BitmapDescriptorFactory.fromResource(resId));
            mapController.addPolyline(polylineOptions);
        }
        result = 0;
        return result;
    }

    public int getTextureResId(Context context, int TextureId) {
        int resId = -1;
        switch (TextureId) {
            case MARKER_TLINE_ARROW_ONLY:
                resId = R.drawable.map_aolr;
                break;
            case MARKER_TLINE_LINK_DOTT:
                resId = R.drawable.transparent_route;
                break;
            case MARKER_TEST_CAR_ICON:
                resId = R.drawable.bubble_point_red_big;
                break;
            case MARKER_TLINE_WALK:
                resId = R.drawable.routetexture_walk;
                break;
            case MARKER_TLINE_BUS: // 此处用于未知路况的绘制
//                resId = R.drawable.routetexture_no;
                resId = R.drawable.routetexture_green;
                break;
//            case MARKER_TLINE_ROUTE_OTHER:
//                resId = R.drawable.routetexture_no;
//                break;
            case MARKER_TLINE_TRAFFIC_BAD:
                resId = R.drawable.routetexture_bad;
                break;
            case MARKER_TLINE_TRAFFIC_BADER:
                resId = R.drawable.routetexture_grayred;
                break;
            case MARKER_TLINE_TRAFFIC_SLOW:
                resId = R.drawable.routetexture_slow;
                break;
            case MARKER_TLINE_TRAFFIC_GREEN:
//                resId = R.drawable.routetexture_green;
                resId = R.drawable.routetexture_green;
                break;
            case MARKER_TLINE_TRAFFIC_NO_DATA: // 路径的默认纹理(不绘制tmc)
                resId = R.drawable.routetexture_no;
                break;
        }
        return resId;
    }

    List<Marker> markers = new ArrayList<>();
    public void addDestrictMarkerToOverlay(NavigationPath path){
        removeAllMarkers();
        RestrictionArea[] restrictionAreas = path.getRestrictionAreas();
        if(null == restrictionAreas){
            return;
        }
        for(RestrictionArea restrictionArea : restrictionAreas){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(restrictionArea.x,restrictionArea.y));
            markerOptions.title("限制");
            markerOptions.anchor(0.5f,0.5f);
            markerOptions.icon(getIcon(restrictionArea.bAvoided,restrictionArea.btType));
            Marker marker = mapController.addMarker(markerOptions);
            marker.setObject(restrictionArea);
            markers.add(marker);
        }
    }

    private BitmapDescriptor getIcon(boolean bAvoided, int btType){
        BitmapDescriptor bitmapDescriptor = null;
        switch (btType){
            case 0:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.b__poi);
                break;
            case 1:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.truck_navi_warn_height);
                break;
            case 2:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.truck_navi_warn_weight);
                break;
            case 3:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.truck_navi_warn_width);
                break;
            case 4:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.truck_navi_warn_cross);
                break;
            case 5:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.truck_navi_warn_line);
                break;
            default:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.b_poi_h);
                break;
        }
        return bitmapDescriptor;
    }

    private void removeAllMarkers() {
        if(markers.isEmpty()){
            return;
        }
        for(Marker marker : markers){
            marker.remove();
        }
        markers.clear();
    }


    /**
     * 在地图上增加单点marker,icon内文本按传入参数绘制
     *
     * @param iconSize
     * @param latLng
     */
    public Marker addMarker(int iconSize, LatLng latLng,String text) {
        Marker marker = null;
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng).setAddByAnimation(true);

        markerOption.draggable(true);
        Bitmap bitmap = getNumberBitmap(iconSize,text);
        markerOption.icon(
                BitmapDescriptorFactory.fromBitmap(bitmap));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(false);
        marker = mapController.addMarker(markerOption);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return marker;
    }

    public Bitmap getNumberBitmap(int iconSize, String number) {
        Bitmap loc = BitmapFactory.decodeResource(((Activity)context).getResources(), R.drawable.box_location_default);
        Bitmap bitmap = Bitmap.createBitmap(loc.getWidth(), loc.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        // draw background
        canvas.drawBitmap(loc, 0, 0, paint);
        // draw text
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setTextSize(iconSize * 0.4f);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2 - 10;
        canvas.drawText(number, rect.centerX(), baseline, paint);
        return bitmap;
    }

    /**
     * 添加单个marker
     *
     * @param bitmap
     * @param latLng
     * @return
     */
    public Marker addMarker(Bitmap bitmap, LatLng latLng) {
        Marker marker = null;
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng).setAddByAnimation(true);

        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(false);
        marker = mapController.addMarker(markerOption);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return marker;
    }

    /**
     * 在地图上增加单点marker
     *
     * @param iconID
     * @param latLng
     */
    public Marker addMarker(int iconID, LatLng latLng) {
        Marker marker = null;
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng).setAddByAnimation(false);

        markerOption.draggable(true);
        Bitmap bitmap = BitmapFactory
                .decodeResource(context.getResources(), iconID);
        markerOption.icon(
                BitmapDescriptorFactory.fromBitmap(bitmap));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(false);
        marker = mapController.addMarker(markerOption);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return marker;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context  上下文
     * @param dipValue dp大小
     * @return px大小
     */
    public int dipToPixel(Context context, int dipValue) {
        if (context == null) {
            return dipValue; // 原值返回
        }
        try {
            float pixelFloat = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                            .getResources().getDisplayMetrics());
            return (int) pixelFloat;
        } catch (Exception e) {

        }
        return dipValue;
    }
}
