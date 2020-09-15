package com.sfmap.route.model;

import android.content.Intent;

import com.sfmap.library.model.GeoPoint;

import java.util.List;

/**
 * 一条 驾车规划的结果
 * <p>
 * 一条 NavigationResult
 */
public interface ICarRouteResult extends IRouteResultData {

    /**
     * 设置 数据 不包括 起点终点终点 方法 等数据
     */
    public void setNaviResultData(POI fromPOI, POI toPOI,
                                  NavigationResult result, String method);

    public NavigationResult getNaviResultData();

    public String getFromCityCode();

    public String getToCityCode();

    /**
     * parse data and initialize this object
     *
     * @param data
     * @param startOffset
     * @param size
     */
    public boolean parseData(byte[] data, int startOffset, int size);

    /**
     * 解析原始导航数据（不包含头信息及打车信息）
     *
     * @param tbtData
     * @return
     */
    public boolean parseTBTData(byte[] tbtData);

    //

    /**
     * 获取当前线路索引
     *
     * @return 有数据默认为0，无数据为-1
     */
    public int getFocusRouteIndex();

    /**
     * 设置当前线路索引
     *
     * @param index
     */
    public void setFocusRouteIndex(int index);

    /**
     * 获取当前线路的当前站点索引
     *
     * @return
     */
    public int getFocusStationIndex();

    /**
     * 设置当前线路的当前站点索引
     *
     * @param focus
     */
    public void setFocusStationIndex(int focus);

    /**
     * 获取当前route的station总数
     *
     * @return
     */
    public int getStationsCount();

    public void setStationCount(int count);

    /**
     * 设置中间点
     */
    public void setMidPOIs(List<POI> poiList);

    /**
     * 获取中间点
     *
     * @return
     */
    public List<POI> getMidPOIs();

    public List<POI> getShareMidPOIs();

    public boolean hasMidPos();

    /**
     * 获取要分享的途经点
     *
     * @return
     */
    public GeoPoint getShareMidPos();

    /**
     * 获取要分享的起点
     *
     * @return
     */
    public GeoPoint getShareStartPos();

    /**
     * 获取要分享的终点
     *
     * @return
     */
    public GeoPoint getShareEndPos();

    /**
     * 设置要分享的途经点
     *
     * @return
     */
    public void setShareMidPos(GeoPoint gp);

    /**
     * 设置要分享的起点
     *
     * @return
     */
    public void setShareStartPos(GeoPoint gp);

    /**
     * 设置要分享的终点
     *
     * @return
     */
    public void setShareEndPos(GeoPoint gp);


    /**
     * @return
     */
    public NavigationPath getFocusNavigationPath();

    public NavigationPath getNavigationPath(int index);


    public byte[] getBackUpTbtData();

    public void setGotoNaviDlgIndex(int gotoNaviDlgIndex);

    public int getGotoNaviDlgIndex();

    public boolean isNative();

    public void setNative(boolean aNative);

    public boolean isOfflineNavi();

    public void setOfflineNavi(boolean bOfflineNavi);

    public String genMethodStr(int pathIndex);

    /**
     * 当前驾车结果是否需要提示步行建议
     *
     * @return
     */
    public boolean isSuggestOnfoot();

    /**
     * 设置当前驾车结果是否需要提示步行建议
     *
     * @return
     */
    public void setSuggestOnfoot(boolean suggestOnfoot);

    public void setArgIntent(Intent intent);

    public Intent getArgIntent();

}
