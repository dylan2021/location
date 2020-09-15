package com.sfmap.api.navi;

import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.api.navi.model.NaviLaneInfo;
import com.sfmap.api.navi.model.NaviLocation;
import com.sfmap.api.navi.model.NaviServiceFacilityInfo;
import com.sfmap.api.navi.model.NaviTrafficFacilityInfo;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.enums.PathPlanningErrCode;
import com.sfmap.api.navi.enums.VoiceType;

/**
 * 导航事件监听
 */
public abstract interface NaviListener {

	/**
	 * 导航创建失败时的回调函数。
	 */
	public abstract void onInitNaviFailure();

	/**
	 * 导航创建成功时的回调函数。
	 */
	public abstract void onInitNaviSuccess();

	/**
	 * 启动导航后回调函数。
	 * @param type - 导航类型，1：实时导航，2：模拟导航
	 */
	public abstract void onStartNavi(int type);

	/**
	 * 当前方路况光柱信息有更新时回调函数。
	 */
	public abstract void onTrafficStatusUpdate();

	/**
	 * 当GPS位置有更新时的回调函数。
	 * @param location - 当前位置的定位信息。
	 */
	public abstract void onLocationChange(NaviLocation location);

	/**
	 * 导航播报信息回调函数。
	 * @param voiceType - 播报语音类型，包含如开始导航、结束导航、偏航等。参见 {@link VoiceType }。
	 * @param text - 播报文字。
	 */
	public abstract void onGetNavigationText(int voiceType, String text);

	/**
	 * 模拟导航停止后回调函数。
	 */
	public abstract void onEndEmulatorNavi();

	/**
	 * 到达目的地后回调函数。
	 */
	public abstract void onArriveDestination();

	/**
	 * 驾车路径规划成功后的回调函数。
	 */
	public abstract void onCalculateRouteSuccess();

	/**
	 * 驾车路径规划失败后的回调函数。
	 * @param errorInfo - 计算路径的错误码，参见 {@link PathPlanningErrCode }。
	 */
	public abstract void onCalculateRouteFailure(int errorInfo);

	/**
	 * 驾车导航时,出现偏航后需要重新计算路径的回调函数。
	 */
	public abstract void onReCalculateRouteForYaw();

	/**
	 * 驾车导航时，如果前方遇到拥堵时需要重新计算路径的回调。
	 */
	public abstract void onReCalculateRouteForTrafficJam();

	/**
	 * 驾车路径导航到达某个途经点的回调函数。
	 * @param wayID - 到达途径点的编号，标号从1开始，依次累加。到达终点时wayID取值为0。
	 */
	public abstract void onArrivedWayPoint(int wayID);

	/**
	 * 用户手机GPS设置是否开启的回调函数。
	 * @param enabled - true,开启;false,未开启。
	 */
	public abstract void onGpsOpenStatus(boolean enabled);


	/**
	 * 导航引导信息回调 naviinfo 是导航信息类。
	 * @param naviInfo - 导航信息对象。
	 */
	public abstract void onNaviInfoUpdate(NaviInfo naviInfo);


	/**
	 * 摄像头信息更新回调。
	 * @param trafficFacilityInfo - 摄像头信息
	 */
	public abstract void onUpdateTrafficFacility(NaviTrafficFacilityInfo[] trafficFacilityInfo);

	/**
	 * 显示路口放大图回调。
	 * @param naviCross - 路口放大图类，可以获得此路口放大图bitmap
	 */
	public abstract void showCross(NaviCross naviCross);

	/**
	 * 关闭路口放大图回调。
	 */
	public abstract void hideCross();

	/**
	 * 显示车道线信息视图回调。
	 * @param laneInfos - 车道线信息数组，可获得各条道路分别是什么类型，可用于用户使用自己的素材完全自定义显示。
	 * @param laneBackgroundInfo - 车道线背景数据数组
	 * @param laneRecommendedInfo - 车道线推荐数据数组
	 */
	public abstract void showLaneInfo(NaviLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo);

	/**
	 * 关闭车道线信息视图回调。
	 */
	public abstract void hideLaneInfo();

	/**
	 * 多路线算路成功回调。
	 * @param routeIds - 路线id数组
	 */
	public abstract void onCalculateMultipleRoutesSuccess(int[] routeIds);


	/**
	 * 服务区、收费站信息更新通知回调。
	 * @param serviceFacilityInfos 服务区和收费站信息数组
     */
	public abstract void updateServiceFacility(NaviServiceFacilityInfo[] serviceFacilityInfos);



}