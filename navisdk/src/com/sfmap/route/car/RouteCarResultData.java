package com.sfmap.route.car;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.navi.Navi;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.api.navi.model.NaviLink;
import com.sfmap.api.navi.model.NaviPath;
import com.sfmap.api.navi.model.NaviStep;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.util.CalculateUtil;
import com.sfmap.library.util.Projection;
import com.sfmap.map.util.CatchExceptionUtil;
import com.sfmap.route.model.GroupNavigationSection;
import com.sfmap.route.model.ICarRouteResult;
import com.sfmap.route.model.LocationCodeResult;
import com.sfmap.route.model.NavigationPath;
import com.sfmap.route.model.NavigationResult;
import com.sfmap.route.model.NavigationSection;
import com.sfmap.route.model.POI;
import com.sfmap.route.model.SegLocationCodeStatus;
import com.sfmap.route.util.RouteCalType;
import com.sfmap.tbt.AvoidJamArea;
import com.sfmap.tbt.GroupSegment;
import com.sfmap.tbt.JamInfo;
import com.sfmap.tbt.LinkStatus;
import com.sfmap.tbt.NaviAction;
import com.sfmap.tbt.RestrictionArea;
import com.sfmap.tbt.RouteIncident;
import com.sfmap.tbt.util.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * ICarRouteResult{@link ICarRouteResult} 接口的实现和序列化存储的实现,
 * 使用的时候只要从数据中心获取响应的接口,不需要手动去实例化
 */
public class RouteCarResultData implements ICarRouteResult {

    private static final String TAG = RouteCarResultData.class.getSimpleName();
    // 默认显示三种出行方式
    private int titleIcons = 7;
    private Navi sfNavi;

    NavigationResult carPathResult;

    private POI fromPoi = null;
    private POI toPoi = null;
    private List<POI> midPoiList = null;

    private POI share_from_poi = null;
    private POI share_to_poi = null;
    private List<POI> share_mid_poi = null;
    // 要分享的终点
    private GeoPoint shareEndGp;
    // 要分享的起点
    private GeoPoint shareStartGp = null;

    private String method_;

    private int focusStationIndex = 0;

    private int focusRouteIndex = 0;

    private int stationsCount = 0;

    private byte[] backUpData;

    private int gotoNaviDlgIndex = 0;

    private String key;

    private boolean suggestOnFoot = false;
    // default net
    private boolean isNative = false;

    private boolean isOfflineNavi = false;

    private Intent intent;

    private double zxx;
    private double zyy;

    public RouteCarResultData(Context context) {
        sfNavi = Navi.getInstance(context);
    }

    @Override
    public Class<RouteCarResultData> getClassType() {
        return RouteCarResultData.class;

    }

    @Override
    public void saveData() {

    }

    @Override
    public void restoreData() {

    }

    @Override
    public boolean hasData() {
        if (carPathResult == null || carPathResult.paths == null
                || carPathResult.paths.length == 0 || fromPoi == null
                || toPoi == null)
            return false;
        return true;
    }

    @Override
    public void setNaviResultData(POI fromPOI, POI toPOI, NavigationResult result, String method) {
        fromPoi = fromPOI;
        toPoi = toPOI;

        carPathResult = result;
        method_ = method;
    }

    @Override
    public NavigationResult getNaviResultData() {
        return carPathResult;
    }

    @Override
    public void reset() {
        carPathResult = null;

        fromPoi = null;
        toPoi = null;
        midPoiList = null;
        method_ = RouteCalType.CARROUTE_INDEX_1;

        backUpData = null;

        focusStationIndex = 0;
        focusRouteIndex = 0;
        stationsCount = 0;

    }

    @Override
    public boolean parseData(byte[] data, int startOffset, int size) {
        return parseDataEx21Version(data, startOffset, size);
    }

    @Override
    public boolean parseTBTData(byte[] tbtData) {
        NavigationResult naviResult = new NavigationResult();
        parsePathDataEx(naviResult, tbtData);
        carPathResult = naviResult;
        isNative = false;
        return true;
    }

    @Override
    public String getFromCityCode() {
        if (fromPoi != null)
            return fromPoi.getCityCode();
        return null;
    }

    @Override
    public String getToCityCode() {
        if (toPoi != null)
            return toPoi.getCityCode();
        return null;
    }

    @Override
    public int getFocusRouteIndex() {
        return focusRouteIndex;
    }

    @Override
    public void setFocusRouteIndex(int index) {
        focusRouteIndex = index;
    }

    @Override
    public int getFocusStationIndex() {
        return focusStationIndex;
    }

    @Override
    public void setFocusStationIndex(int focus) {
        focusStationIndex = focus;

    }

    @Override
    public int getStationsCount() {
        return stationsCount;
    }

    @Override
    public void setStationCount(int count) {
        stationsCount = count;
    }

    @Override
    public void setFromPOI(POI fromPOI) {
        fromPoi = fromPOI;

    }

    @Override
    public POI getFromPOI() {
        return fromPoi;
    }

    @Override
    public void setMidPOIs(List<POI> poiList) {
        this.midPoiList = poiList;
    }

    @Override
    public List<POI> getMidPOIs() {
        return this.midPoiList;
    }

    @Override
    public void setToPOI(POI toPOI) {
        toPoi = toPOI;
    }

    @Override
    public POI getToPOI() {
        return toPoi;
    }

    @Override
    public POI getShareFromPOI() {
        if (share_from_poi == null)
            share_from_poi = fromPoi.clone();
        return share_from_poi;
    }

    @Override
    public List<POI> getShareMidPOIs() {
        if (midPoiList != null && share_mid_poi == null) {
            share_mid_poi = new ArrayList<POI>();
            for (POI poi : midPoiList) {
                share_mid_poi.add(poi.clone());
            }
        }
        return share_mid_poi;
    }

    @Override
    public POI getShareToPOI() {
        if (share_to_poi == null)
            share_to_poi = toPoi.clone();
        return share_to_poi;
    }

    @Override
    public void setShareMidPos(GeoPoint gp) {
    }

    @Override
    public void setShareStartPos(GeoPoint gp) {
        shareStartGp = gp;
    }

    @Override
    public void setShareEndPos(GeoPoint gp) {
        shareEndGp = gp;
    }

    @Override
    public GeoPoint getShareMidPos() {
        return null;
    }

    @Override
    public GeoPoint getShareStartPos() {
        POI start = getShareFromPOI();
        if (shareStartGp == null && start != null) {
            shareStartGp = start.getPoint();
        }
        return shareStartGp;
    }

    @Override
    public GeoPoint getShareEndPos() {
        POI end = getShareToPOI();
        if (shareEndGp == null && end != null) {
            shareEndGp = end.getPoint();
        }
        return shareEndGp;
    }

    @Override
    public void setMethod(String m) {
        method_ = m;

    }

    @Override
    public String getMethod() {
        return method_;
    }

    @Override
    public boolean hasMidPos() {
        if (this.midPoiList != null && this.midPoiList.size() > 0)
            return true;
        return false;
    }

    @Override
    public NavigationPath getFocusNavigationPath() {
        return getNavigationPath(focusRouteIndex);
    }

    @Override
    public NavigationPath getNavigationPath(int index) {
        if (carPathResult == null)
            return null;

        NavigationPath[] tmps = carPathResult.paths;

        if (index < 0 || tmps == null || index >= tmps.length
                || tmps.length == 0) {
            return null;
        }

        return tmps[index];
    }

    @Override
    public byte[] getBackUpTbtData() {
        return backUpData;
    }

    private boolean parseDataEx21Version(byte[] data, int startOffset, int size) {
        NavigationResult naviResult = new NavigationResult();
        if(null == data){
            if (!parsePathDataEx(naviResult)) {
                return false;
            }
        }else {
            if (!parsePathDataEx(naviResult, data)) {
                return false;
            }
        }
        carPathResult = naviResult;
        isNative = false;
        return true;
    }

    /**
     * Decoder2.2版本的解析
     *
     * @param naviResult
     * @param data
     * @return
     */
    private boolean parsePathDataEx(NavigationResult naviResult, byte[] data) {
        if (data == null || data.length == 0) return false;
        int flag = RouteCalType.getNaviFlags(getMethod());
        int calcType = RouteCalType.getNaviType(getMethod());
        int status = sfNavi.pushRouteData(calcType, flag, data, data.length);
        if (status <= 0) {
            return false;
        }
        int pathArray[] = sfNavi.getAllRouteID();

        backUpPushTbtData(data);
        naviResult.startX = fromPoi.getPoint().x;
        naviResult.startY = fromPoi.getPoint().y;

        naviResult.endX = toPoi.getPoint().x;
        naviResult.endY = toPoi.getPoint().y;
        // 获得路径个数
        int pathCount = pathArray.length;

        naviResult.pathNum = pathCount;
        naviResult.paths = new NavigationPath[pathCount];
        NavigationPath tmpPath;
        NavigationSection tmpSection;
        for (int i = 0; i < pathCount; i++) {
            // 选择当前路径
            sfNavi.selectRouteId(pathArray[i]);
            tmpPath = new NavigationPath();
            NaviPath naviPath = sfNavi.getNaviPath();
            // 字节流长度 没用 不解析了
            tmpPath.dataLength = 0;
            tmpPath.restrictionAreas = sfNavi.getRestrictionInfo();
            tmpPath.tollCost = sfNavi.getSegTollCost();
            // 当前路径长度
            tmpPath.pathlength = sfNavi.getRouteLength();
            // 整个路径行驶的时间
            tmpPath.costTime = sfNavi.getNaviPath().getAllTime();

            // 当前路径的策略
            tmpPath.pathStrategy = sfNavi.getNaviPath().getStrategy();
            // 导航段个数

            int secCount = sfNavi.getNaviPath().getStepsCount();

            tmpPath.sectionNum = secCount;
            tmpPath.sections = new NavigationSection[secCount];
            tmpPath.limitRoadFlag = naviPath.getByPassLinitedRoad();
            tmpPath.tmcTime = naviPath.getDiffToTMCRoute();
            GroupSegment[] groupSegment = naviPath.getGroupSegmentList();
            if (groupSegment != null) {
                int groupSegmentNum = groupSegment.length;
                int n = 0;
                while (n < groupSegmentNum) {
                    GroupSegment gsObj = groupSegment[n];
                    if (gsObj != null) {

                        GroupNavigationSection groupSection = new GroupNavigationSection();
                        groupSection.index = n;
                        if (TextUtils.isEmpty(gsObj.m_GroupName)) {
                            groupSection.groupName = "无名道路";
                        } else {
                            groupSection.groupName = toDBC(gsObj.m_GroupName);
                        }
                        groupSection.segCount = gsObj.m_nSegCount;
                        groupSection.startSegId = gsObj.m_nStartSegId;
                        groupSection.distance = gsObj.m_nDistance;
                        groupSection.toll = gsObj.m_nToll;
                        groupSection.status = gsObj.m_nStatus;
                        groupSection.speed = gsObj.m_nSpeed;
                        groupSection.isSrucial = gsObj.m_bIsSrucial;
                        tmpPath.groupNaviSectionList.add(groupSection);
                    }
                    n++;
                }
            }
            JamInfo[] jInfo = naviPath.getJamInfoList();
            if (jInfo != null) {
                int jInfoLength = jInfo.length;
                int k = 0;
                while (k < jInfoLength) {
                    if (jInfo[k] != null) {
                        com.sfmap.route.model.JamInfo tmpJinfo = new com.sfmap.route.model.JamInfo();
                        GeoPoint p = Projection.LatLongToPixels(
                                jInfo[k].lat, jInfo[k].lon, 20);
                        tmpJinfo.gPoint = new GeoPoint(p.x, p.y);
                        tmpJinfo.speed = jInfo[k].speed;
                        tmpJinfo.lon = jInfo[k].lon;
                        tmpJinfo.lat = jInfo[k].lat;
                        tmpPath.jamInfo.add(tmpJinfo);
                    }
                    k++;
                }
            }
            int actionNavi;
            double lat;
            double lon;
            tmpPath.location_code_result = new LocationCodeResult();

            for (int j = 0; j < secCount; j++) {
                NaviStep sfNaviStep = naviPath.getSteps().get(j);
                tmpSection = new NavigationSection();
                tmpSection.mIndex = j;
                // 字节流长度 没用 不解析了
                tmpSection.dataLength = 0;
                // 名称
                if (!sfNaviStep.getLinks().isEmpty()) {
                    tmpSection.streetName = toDBC(sfNaviStep.getLinks().get(0).getRoadName());
                } else {
                    tmpSection.streetName = "无可通行路";
                }
                tmpSection.pathlength = sfNaviStep.getLength();
                try {
//			没有用到,暂不解析
//		    tmpSection.tollCost = sfNaviStep.get(j);
//		    tmpSection.tollPathName = toDBC(sfNavi
//			    .getSegTollPathName(j));
                    // 累加计算出过路费
//                    tmpPath.tollCost = sfNavi.getNaviPath().getTollCost();
                    int link = sfNaviStep.getLinks().size();
                    if (link > 0) {
                        for (int k = 0; k < link; k++) {
                            NaviLink naviLink = sfNaviStep.getLinks().get(k);
                            int traffic = naviLink.getTrafficLights() ? 1 : 0;
                            if (traffic > 0) {
                                tmpSection.trafficLights += traffic;
                            }
                        }
                        tmpPath.trafficNum += tmpSection.trafficLights;
                    }
                } catch (Exception e) {
                    CatchExceptionUtil
                            .normalPrintStackTrace(e);
                }
                NaviAction naviAction = sfNaviStep.getSegNaviAction();
                // 动作
                if (naviAction != null) {
                    actionNavi = naviAction.m_MainAction;
                    tmpSection.navigtionAction = (byte) (actionNavi);
                    if (hasMidPos()) {
                        // 途经点 获取辅助动作
                        tmpSection.naviAssiAction = (byte) (naviAction.m_AssitAction);
                    }
                }


                // 统计link里将要插值点的个数
//                int linkNum = sfNaviStep.getLinks().size();
//                // 需要插值点的个数
//                int insertPtNum = 0;
                // 生成缓存坐标数组
                List<NaviLatLng> stepCoorList = sfNaviStep.getCoords();
                int finalCoorsCount = stepCoorList.size();
                tmpSection.pointNum = finalCoorsCount;
                tmpSection.xs = new double[finalCoorsCount];
                tmpSection.ys = new double[finalCoorsCount];

                for (int k = 0; k < finalCoorsCount; k++) {
                    lat = stepCoorList.get(k).getLatitude();
                    lon = stepCoorList.get(k).getLongitude();
                    tmpSection.xs[k] = lon;
                    tmpSection.ys[k] = lat;
//                    GeoPoint p = Projection.LatLongToPixels(lat, lon, Projection.MAXZOOMLEVEL);
//                    tmpSection.xs[k] = p.x;
//                    tmpSection.ys[k] = p.y;
                }

                tmpPath.sections[j] = tmpSection;
                int secLineStatusCount = sfNavi.getLinkStatusCount(j, 0);//01380761 zhangxuewen 20190104每个导航段上的实时路况数量，第二个参数已废弃，随意传
                // 创建实时交通段的记录
                SegLocationCodeStatus tempState = new SegLocationCodeStatus(secLineStatusCount);
                int pos = 0;

                double chafenx = 0;
                double chafeny = 0;
                for (int linkIndex = 0; linkIndex < secLineStatusCount; linkIndex++) {//遍历每个导航段的每个实时路况，得到每个实时路况的具体属性值
                    LinkStatus linkStatus = sfNavi.getLinkStatus(j, 0, linkIndex);//第二个参数可以随意传
                    if (null == linkStatus) {
                        continue;
                    }
//                    MyLog.d("segindex:" +j + "  linkIndex:"+linkIndex+"   length:"+linkStatus.m_LinkLen + "   m_Status:"+linkStatus.m_Status);
                    tempState.length[linkIndex] = linkStatus.m_LinkLen;
                    tempState.time[linkIndex] = linkStatus.m_PassTime;
                    tempState.state[linkIndex] = linkStatus.m_Status;
                    tempState.startIndex[linkIndex] = pos;

                    if(linkIndex == secLineStatusCount - 1){
                        tempState.endIndex[linkIndex] = stepCoorList.size() - 1;
                        break;
                    }
                    int clipLength = linkStatus.m_LinkLen;
                    //计算插值点
                    int remainClipLength = clipLength;
                    for (int p = pos; p < stepCoorList.size(); p++) {

//                        if (第p个点的距离第一个点的长 >= linkStatus.m_LinkLen) {
                        double beginx = stepCoorList.get(p).getLongitude();
                        double beginy = stepCoorList.get(p).getLatitude();
                        if (p == stepCoorList.size() - 1) {
                            break;
                        }
                        double endx = stepCoorList.get(p + 1).getLongitude();
                        double endy = stepCoorList.get(p + 1).getLatitude();
                        int distance = (int)(0.5+CalculateUtil.getDistance(beginy,
                                beginx, endy, endx));
                        if (distance >= remainClipLength) {
//                            MyLog.d("distance：" + distance + "remainClipLength====：" + remainClipLength);
                            double clipx = beginx + (endx - beginx) * remainClipLength / distance;
                            double clipy = beginy + (endy - beginy) * remainClipLength / distance;

                            if(!isSamePositionByValue(chafeny,chafenx,clipy,clipx,0.000000001)){
//                                MyLog.d("位置：" + p + "插值y：" + clipy + "插值x：" + clipx);
                                stepCoorList.add(p, new NaviLatLng(clipy, clipx));
                            }
                            chafenx = clipx;
                            chafeny = clipy;
                            tempState.endIndex[linkIndex] = p;
                            pos = p;
                            break;
                        } else {
                            remainClipLength -= distance;
                        }
                    }
                }
                tmpPath.location_code_result.res_hash.put(j + "", tempState);//保存每个导航段的路况数据到一个map


                if (tmpPath.groupNaviSectionList != null
                        && tmpPath.groupNaviSectionList.size() > 0) {
                    for (GroupNavigationSection group : tmpPath.groupNaviSectionList) {
                        if (j < (group.startSegId + group.segCount)) {
                            group.sectionList.add(tmpSection);
                            group.trafficLights += tmpSection.trafficLights;
                            break;
                        }
                    }
                }
            }

            AvoidJamArea aObj = naviPath.getAvoidJamArea();

            if (aObj != null) {
                tmpPath.avoidJamAreaStr = aObj.getAvoidJamDesc();
            }

            RouteIncident[] rIncident = naviPath.getRouteIncident();
            // 获得选中路径上的事件信息
            if (rIncident != null && rIncident.length > 0) {

                int size = rIncident.length;

                for (int k = 0; k < size; k++) {
                    RouteIncident rObj = rIncident[k];
                    if (rObj != null && !TextUtils.isEmpty(rObj.title)) {
                        tmpPath.incidentStr = rObj.getIncidentDesc();
                        break;
                    }
                }
            }

            naviResult.paths[i] = tmpPath;
        }
        return true;
    }

//    private GeoPoint getGeoPoint(com.ishowchina.tbt.GeoPoint gp) {


    private void backUpPushTbtData(byte[] data) {
        backUpData = data;
    }

    @Override
    public void setGotoNaviDlgIndex(int gotoNaviDlgIndex) {
        this.gotoNaviDlgIndex = gotoNaviDlgIndex;
    }

    @Override
    public int getGotoNaviDlgIndex() {
        return gotoNaviDlgIndex;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    public boolean isOfflineNavi() {
        return isOfflineNavi;
    }

    public void setOfflineNavi(boolean bOfflineNavi) {
        this.isOfflineNavi = bOfflineNavi;
    }

    @Override
    public String genMethodStr(int pathIndex) {
        String method = "";
        if (method.equals(CarRouteTask.CARROUTE_INDEX_1)) { // 对于推荐方案要根据解析结果判断标签
            NavigationPath path = getNavigationPath(pathIndex);
            int tag = pathIndex;
            if (path != null) {
                tag = path.pathStrategy;
            }
            switch (tag) {
                case 11:
                    return CarRouteTask.CARROUTE_INDEX_STR_0;
                case 1:
                    return CarRouteTask.CARROUTE_INDEX_STR_2;
                case 0:
                    return CarRouteTask.CARROUTE_INDEX_STR_4;
                default:
                    return CarRouteTask.CARROUTE_INDEX_STR_8;
            }
        } else if (method.equals(CarRouteTask.CARROUTE_INDEX_0)) { // 0 躲避拥堵
            return "高速优先";
        } else if (method.equals(CarRouteTask.CARROUTE_INDEX_8)) { // 1 不走高速
            return "不走高速";
        } else if (method.equals(CarRouteTask.CARROUTE_INDEX_4)) {// 2 避免收费
            return "费用优先";
        } else if (method.equals(CarRouteTask.CARROUTE_INDEX_2)) {
            return "躲避拥堵";
        } else {
            return "";
        }
    }

    @Override
    public boolean isSuggestOnfoot() {
        return suggestOnFoot;
    }

    @Override
    public void setSuggestOnfoot(boolean suggestOnfoot) {
        this.suggestOnFoot = suggestOnfoot;
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        return returnString;
    }

    //    }
//	return new GeoPoint(p.x, p.y);
//		VirtualEarthProjection.MAXZOOMLEVEL);
//	Point p = VirtualEarthProjection.LatLongToPixels(lat, lon,
//	double lon = gp[0].m_Lon;
//	double lat = gp[0].m_Lat;
//	    return null;
//	if (gp == null || gp.length <= 0)
//    private GeoPoint getGeoPoint(com.ishowchina.tbt.GeoPoint gp[]) {
//
//    }
//	return new GeoPoint(p.x, p.y);
//		VirtualEarthProjection.MAXZOOMLEVEL);
//	Point p = VirtualEarthProjection.LatLongToPixels(lat, lon,
//	double lon = gp.m_Lon;
//	double lat = gp.m_Lat;
//	    return null;
//	if (gp == null)

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean aNative) {
        this.isNative = aNative;
    }

    @Override
    public void setArgIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public Intent getArgIntent() {
        return intent;
    }


    /**
     * Decoder2.2版本的解析
     *
     * @param naviResult
     * @param
     * @return
     */
    private boolean parsePathDataEx(NavigationResult naviResult) {

        int pathArray[] = sfNavi.getAllRouteID();

        naviResult.startX = fromPoi.getPoint().x;
        naviResult.startY = fromPoi.getPoint().y;

        naviResult.endX = toPoi.getPoint().x;
        naviResult.endY = toPoi.getPoint().y;
        // 获得路径个数
        int pathCount = pathArray.length;

        naviResult.pathNum = pathCount;
        naviResult.paths = new NavigationPath[pathCount];
        NavigationPath tmpPath;
        NavigationSection tmpSection;
        for (int i = 0; i < pathCount; i++) {
            // 选择当前路径
            sfNavi.selectRouteId(pathArray[i]);
            tmpPath = new NavigationPath();
            NaviPath naviPath = sfNavi.getNaviPath();
            Log.d(TAG,"sfNavi.GetRouteUID:"+ sfNavi.GetRouteUID(i));
            // 字节流长度 没用 不解析了
            tmpPath.dataLength = 0;
            tmpPath.restrictionAreas = sfNavi.getRestrictionInfo();
            tmpPath.tollCost = sfNavi.getSegTollCost();
            Log.i(TAG,"SegTollCost:"+sfNavi.getSegTollCost());
            // 当前路径长度
            tmpPath.pathlength = sfNavi.getRouteLength();
            // 整个路径行驶的时间
            tmpPath.costTime = sfNavi.getNaviPath().getAllTime();

            // 当前路径的策略
            tmpPath.pathStrategy = sfNavi.getNaviPath().getStrategy();
            // 导航段个数

            int secCount = sfNavi.getNaviPath().getStepsCount();

            tmpPath.sectionNum = secCount;
            tmpPath.sections = new NavigationSection[secCount];
            tmpPath.limitRoadFlag = naviPath.getByPassLinitedRoad();
            tmpPath.tmcTime = naviPath.getDiffToTMCRoute();
            GroupSegment[] groupSegment = naviPath.getGroupSegmentList();
            if (groupSegment != null) {
                int groupSegmentNum = groupSegment.length;
                int n = 0;
                while (n < groupSegmentNum) {
                    GroupSegment gsObj = groupSegment[n];
                    if (gsObj != null) {

                        GroupNavigationSection groupSection = new GroupNavigationSection();
                        groupSection.index = n;
                        if (TextUtils.isEmpty(gsObj.m_GroupName)) {
                            groupSection.groupName = "无名道路";
                        } else {
                            groupSection.groupName = toDBC(gsObj.m_GroupName);
                        }
                        groupSection.segCount = gsObj.m_nSegCount;
                        groupSection.startSegId = gsObj.m_nStartSegId;
                        groupSection.distance = gsObj.m_nDistance;
                        groupSection.toll = gsObj.m_nToll;
                        groupSection.status = gsObj.m_nStatus;
                        groupSection.speed = gsObj.m_nSpeed;
                        groupSection.isSrucial = gsObj.m_bIsSrucial;
                        tmpPath.groupNaviSectionList.add(groupSection);
                    }
                    n++;
                }
            }
            JamInfo[] jInfo = naviPath.getJamInfoList();
            if (jInfo != null) {
                int jInfoLength = jInfo.length;
                int k = 0;
                while (k < jInfoLength) {
                    if (jInfo[k] != null) {
                        com.sfmap.route.model.JamInfo tmpJinfo = new com.sfmap.route.model.JamInfo();
                        GeoPoint p = Projection.LatLongToPixels(
                                jInfo[k].lat, jInfo[k].lon, 20);
                        tmpJinfo.gPoint = new GeoPoint(p.x, p.y);
                        tmpJinfo.speed = jInfo[k].speed;
                        tmpJinfo.lon = jInfo[k].lon;
                        tmpJinfo.lat = jInfo[k].lat;
                        tmpPath.jamInfo.add(tmpJinfo);
                    }
                    k++;
                }
            }
            int actionNavi;
            double lat;
            double lon;
            tmpPath.location_code_result = new LocationCodeResult();

            for (int j = 0; j < secCount; j++) {
                NaviStep sfNaviStep = naviPath.getSteps().get(j);
                tmpSection = new NavigationSection();
                tmpSection.mIndex = j;
                // 字节流长度 没用 不解析了
                tmpSection.dataLength = 0;
                // 名称
                if (!sfNaviStep.getLinks().isEmpty()) {
                    tmpSection.streetName = toDBC(sfNaviStep.getLinks().get(0).getRoadName());
                } else {
                    tmpSection.streetName = "无可通行路";
                }
                tmpSection.pathlength = sfNaviStep.getLength();
                try {
                    int link = sfNaviStep.getLinks().size();
                    if (link > 0) {
                        for (int k = 0; k < link; k++) {
                            NaviLink naviLink = sfNaviStep.getLinks().get(k);
                            int traffic = naviLink.getTrafficLights() ? 1 : 0;
                            if (traffic > 0) {
                                tmpSection.trafficLights += traffic;
                            }
                        }
                        tmpPath.trafficNum += tmpSection.trafficLights;
                    }
                } catch (Exception e) {
                    CatchExceptionUtil
                            .normalPrintStackTrace(e);
                }
                NaviAction naviAction = sfNaviStep.getSegNaviAction();
                // 动作
                if (naviAction != null) {
                    actionNavi = naviAction.m_MainAction;
                    tmpSection.navigtionAction = (byte) (actionNavi);
                    if (hasMidPos()) {
                        // 途经点 获取辅助动作
                        tmpSection.naviAssiAction = (byte) (naviAction.m_AssitAction);
                    }
                }
                // 生成缓存坐标数组
                List<NaviLatLng> stepCoorList = sfNaviStep.getCoords();
                int finalCoorsCount = stepCoorList.size();
                tmpSection.pointNum = finalCoorsCount;
                tmpSection.xs = new double[finalCoorsCount];
                tmpSection.ys = new double[finalCoorsCount];

                for (int k = 0; k < finalCoorsCount; k++) {
                    lat = stepCoorList.get(k).getLatitude();
                    lon = stepCoorList.get(k).getLongitude();
                    tmpSection.xs[k] = lon;
                    tmpSection.ys[k] = lat;
                }

                tmpPath.sections[j] = tmpSection;
                int secLineStatusCount = sfNavi.getLinkStatusCount(j, 0);//01380761 zhangxuewen 20190104每个导航段上的实时路况数量，第二个参数已废弃，随意传
                // 创建实时交通段的记录
                SegLocationCodeStatus tempState = new SegLocationCodeStatus(secLineStatusCount);
                int pos = 0;

                double chafenx = 0;
                double chafeny = 0;
                for (int linkIndex = 0; linkIndex < secLineStatusCount; linkIndex++) {//遍历每个导航段的每个实时路况，得到每个实时路况的具体属性值
                    LinkStatus linkStatus = sfNavi.getLinkStatus(j, 0, linkIndex);//第二个参数可以随意传
                    if (null == linkStatus) {
                        continue;
                    }
                    tempState.length[linkIndex] = linkStatus.m_LinkLen;
                    tempState.time[linkIndex] = linkStatus.m_PassTime;
                    tempState.state[linkIndex] = linkStatus.m_Status;
                    tempState.startIndex[linkIndex] = pos;
                    if(linkIndex == secLineStatusCount - 1){
                        tempState.endIndex[linkIndex] = stepCoorList.size() - 1;
                        break;
                    }
                    int clipLength = linkStatus.m_LinkLen;
                    //计算插值点
                    int remainClipLength = clipLength;
                    for (int p = pos; p < stepCoorList.size(); p++) {

                        double beginx = stepCoorList.get(p).getLongitude();
                        double beginy = stepCoorList.get(p).getLatitude();
                        if (p == stepCoorList.size() - 1) {
                            break;
                        }
                        double endx = stepCoorList.get(p + 1).getLongitude();
                        double endy = stepCoorList.get(p + 1).getLatitude();
                        int distance = (int)(0.5+CalculateUtil.getDistance(beginy,
                                beginx, endy, endx));
                        if (distance >= remainClipLength) {
                            double clipx = beginx + (endx - beginx) * remainClipLength / distance;
                            double clipy = beginy + (endy - beginy) * remainClipLength / distance;

                            if(!isSamePositionByValue(chafeny,chafenx,clipy,clipx,0.000001)){
                                stepCoorList.add(p, new NaviLatLng(clipy, clipx));
                            }
                            chafenx = clipx;
                            chafeny = clipy;
                            tempState.endIndex[linkIndex] = p;
                            pos = p;
                            break;
                        } else {
                            remainClipLength -= distance;
                        }
                    }
                }
                tmpPath.location_code_result.res_hash.put(j + "", tempState);//保存每个导航段的路况数据到一个map


                if (tmpPath.groupNaviSectionList != null
                        && tmpPath.groupNaviSectionList.size() > 0) {
                    for (GroupNavigationSection group : tmpPath.groupNaviSectionList) {
                        if (j < (group.startSegId + group.segCount)) {
                            group.sectionList.add(tmpSection);
                            group.trafficLights += tmpSection.trafficLights;
                            break;
                        }
                    }
                }
            }

            AvoidJamArea aObj = naviPath.getAvoidJamArea();

            if (aObj != null) {
                tmpPath.avoidJamAreaStr = aObj.getAvoidJamDesc();
            }

            RouteIncident[] rIncident = naviPath.getRouteIncident();
            // 获得选中路径上的事件信息
            if (rIncident != null && rIncident.length > 0) {

                int size = rIncident.length;

                for (int k = 0; k < size; k++) {
                    RouteIncident rObj = rIncident[k];
                    if (rObj != null && !TextUtils.isEmpty(rObj.title)) {
                        tmpPath.incidentStr = rObj.getIncidentDesc();
                        break;
                    }
                }
            }

            naviResult.paths[i] = tmpPath;
        }
        return true;
    }

    /**
     * 根据传入的两个坐标值来判断两个位置是否是同一个位置
     *
     * @param value 容差值，两个位置的经纬度相差绝对值在容差之内就认为是同一个位置
     * @return
     */
    public boolean isSamePositionByValue(double lat1, double longitude1, double lat2, double longitude2, double value) {
        boolean result = false;
        if (Math.abs(lat1 - lat2) < value && Math.abs(longitude1 - longitude2) < value) {
            return true;
        }
        return result;
    }
}
