package com.sfmap.api.navi.model;

import com.sfmap.api.navi.enums.CameraType;
import com.sfmap.api.navi.enums.IconType;
import com.sfmap.tbt.DGNaviInfo;

/**
 * 指引信息类。
 */
public class NaviInfo extends DGNaviInfo {
	private NaviLatLng currentCoord;
	private NaviLatLng cameraCoord;

	/**
	 * 默认构造函数
	 */
	public NaviInfo() {
	}

	/**
	 * 构造函数：根据基类初始化
	 * @param dgNaviInfo - 导航信息
	 */
	public NaviInfo(DGNaviInfo dgNaviInfo) {
		this.m_Type = dgNaviInfo.m_Type;
		this.m_CurRoadName = dgNaviInfo.m_CurRoadName;
		this.m_NextRoadName = dgNaviInfo.m_NextRoadName;
		this.m_SAPADist = dgNaviInfo.m_SAPADist;
		this.m_SAPAType = dgNaviInfo.m_SAPAType;
		this.m_CameraDist = dgNaviInfo.m_CameraDist;
		this.m_CameraType = dgNaviInfo.m_CameraType;
		this.m_CameraSpeed = dgNaviInfo.m_CameraSpeed;
		this.m_RouteRemainDis = dgNaviInfo.m_RouteRemainDis;
		this.m_RouteRemainTime = dgNaviInfo.m_RouteRemainTime;
		this.m_SegRemainDis = dgNaviInfo.m_SegRemainDis;
		this.m_SegRemainTime = dgNaviInfo.m_SegRemainTime;
		this.m_CarDirection = dgNaviInfo.m_CarDirection;
		this.m_CurSegNum = dgNaviInfo.m_CurSegNum;
		this.m_CurLinkNum = dgNaviInfo.m_CurLinkNum;
		this.m_CurPointNum = dgNaviInfo.m_CurPointNum;
		this.m_Icon = dgNaviInfo.m_Icon;
		this.m_LimitedSpeed = dgNaviInfo.m_LimitedSpeed;

		this.m_Latitude = dgNaviInfo.m_Latitude;
		this.m_Longitude = dgNaviInfo.m_Longitude;
		this.m_CameraIndex = dgNaviInfo.m_CameraIndex;


		this.currentCoord = new NaviLatLng(dgNaviInfo.m_Latitude, dgNaviInfo.m_Longitude);
		//this.cameraCoord = new NaviLatLng(dgNaviInfo.cameraLatitude, dgNaviInfo.cameraLongitude);
	}


	/**
	 * 导航信息类型。
	 * @return 1 GPS导航更新, 2 模拟导航更新
	 */
	public int getNaviType() {
		return this.m_Type;
	}

	/**
	 * 设置导航信息类型。
	 * @param naviType - 1 GPS导航更新,2 模拟导航更新
	 */
	public void setNaviType(int naviType) {
		this.m_Type = naviType;
	}

	/**
	 * 获取当前路线名称。
	 * @return 当前道路名称
	 */
	public String getCurrentRoadName() {
		return this.m_CurRoadName;
	}

	/**
	 * 设置当前路名
	 * @param currentRoadName - 当前路名
	 */
	public void setCurrentRoadName(String currentRoadName) {
		this.m_CurRoadName = currentRoadName;
	}

	/**
	 * 获取下条路名
	 * @return 下条路名
	 */
	public String getNextRoadName() {
		return this.m_NextRoadName;
	}

	/**
	 * 设置下条路名
	 * @param nextRoadName - 下条路名
	 */
	public void setNextRoadName(String nextRoadName) {
		this.m_NextRoadName = nextRoadName;
	}

	/**
	 * 获取距服务站距离。
	 * @return 到服务站的距离，单位：米
	 */
	public int getServiceAreaDistance() {
		return this.m_SAPADist;
	}

	/**
	 * 设置服务站距离。
	 * @param serviceAreaDistance - 到服务站的距离
	 */
	public void setServiceAreaDistance(int serviceAreaDistance) {
		this.m_SAPADist = serviceAreaDistance;
	}

	/**
	 * 获取电子眼距离。
	 * @return 电子眼距离，单位：米
	 */
	public int getCameraDistance() {
		return this.m_CameraDist;
	}

	/**
	 * 设置电子眼距离.
	 * @param cameraDistance - 电子眼距离
	 */
	public void setCameraDistance(int cameraDistance) {
		this.m_CameraDist = cameraDistance;
	}

	/**
	 * 获取电子眼类型。
	 * @return 电子眼类型，具体定义参见 {@link CameraType } 定义。
	 */
	public int getCameraType() {
		return this.m_CameraType;
	}

	/**
	 * 设置电子眼类型。
	 * @param cameraType - 电子眼类型，具体定义参见 {@link CameraType } 定义。
	 */
	public void setCameraType(int cameraType) {
		this.m_CameraType = cameraType;
	}

	/**
	 * 获取电子眼限速。
	 * @return 限制速度
	 */
	public int getLimitSpeed() {
		return this.m_CameraSpeed;
	}

	/**
	 * 设置电子眼限速。
	 * @param limitSpeed - 电子眼限速。
	 */
	public void setLimitSpeed(int limitSpeed) {
		this.m_CameraSpeed = limitSpeed;
	}

	/**
	 * 获取导航转向图标。
	 * @return 转向图标，定义见 {@link IconType } 定义。
	 */
	public int getIconType() {
		return this.m_Icon;
	}

	/**
	 * 设置导航转向图标。
	 * @param iconType - 导航转向图标，定义见 {@link IconType } 定义。
	 */
	public void setIconType(int iconType) {
		this.m_Icon = iconType;
	}

	/**
	 * 获取路线剩余距离。
	 * @return 路线剩余距离。
	 */
	public int getPathRetainDistance() {
		return this.m_RouteRemainDis;
	}

	/**
	 * 设置路线剩余距离。
	 * @param pathRetainDistance - 路线剩余距离。
	 */
	public void setPathRetainDistance(int pathRetainDistance) {
		this.m_RouteRemainDis = pathRetainDistance;
	}

	/**
	 * 获取路线剩余时间。
	 * @return 路线剩余时间。
	 */
	public int getPathRetainTime() {
		return this.m_RouteRemainTime;
	}

	/**
	 * 设置路线剩余时间。
	 * @param pathRetainTime - 路线剩余时间。
	 */
	public void setPathRetainTime(int pathRetainTime) {
		this.m_RouteRemainTime = pathRetainTime;
	}

	/**
	 * 获取当前路段剩余距离。
	 * @return 当前路段剩余距离。
	 */
	public int getCurStepRetainDistance() {
		return this.m_SegRemainDis;
	}

	/**
	 * 设置路段剩余距离。
	 * @param curStepRetainDistance - 路段剩余距离。
	 */
	public void setCurStepRetainDistance(int curStepRetainDistance) {
		this.m_SegRemainDis = curStepRetainDistance;
	}

	/**
	 * 获取当前路段剩余时间。
	 * @return 路段剩余时间。
	 */
	public int getCurStepRetainTime() {
		return this.m_SegRemainTime;
	}

	/**
	 * 设置当前路段剩余时间。
	 * @param curStepRetainTime - 路段剩余时间。
	 */
	public void setCurStepRetainTime(int curStepRetainTime) {
		this.m_SegRemainTime = curStepRetainTime;
	}

	/**
	 * 自车方向（单位度），以正北为基准，顺时针增加。
	 * @return 自车方向
	 */
	public int getDirection() {
		return this.m_CarDirection;
	}

	/**
	 * 设置自车角度。
	 * @param direction - 自车角度。
	 */
	public void setDirection(int direction) {
		this.m_CarDirection = direction;
	}

	/**
	 * 获取自车经纬度。
	 * @return 自车经纬度。
	 */
	public NaviLatLng getCoord() {
		return this.currentCoord;
	}

	/**
	 * 设置自车经纬度。
	 * @param latLng - 自车经纬度。
	 */
	public void setCoord(NaviLatLng latLng) {
		this.currentCoord = latLng;
	}

	/**
	 * 获取当前大路段索引。
	 * @return 大路段索引。
	 */
	public int getCurStep() {
		return this.m_CurSegNum;
	}

	/**
	 * 设置当前大路段索引。
	 * @param curStep - 大路段索引
	 */
	public void setCurStep(int curStep) {
		this.m_CurSegNum = curStep;
	}

	/**
	 * 获取自车所在小路段索引。
	 * @return 小路段索引。
	 */
	public int getCurLink() {
		return this.m_CurLinkNum;
	}

	/**
	 * 设置自车所在小路段索引。
	 * @param curLink - 小路段索引。
	 */
	public void setCurLink(int curLink) {
		this.m_CurLinkNum = curLink;
	}

	/**
	 * 获取当前位置前一个形状点索引。
	 * @return 形状点索引。
	 */
	public int getCurPoint() {
		return this.m_CurPointNum;
	}

	/**
	 * 设置当前位置前一个形状点索引。
	 * @param curPoint - 形状点索引。
	 */
	public void setCurPoint(int curPoint) {
		this.m_CurPointNum = curPoint;
	}

	/**
	 * 获取摄像头经纬度。
	 * @return 摄像头经纬度。
	 */
	public NaviLatLng getCameraCoord() {
		return this.cameraCoord;
	}

	/**
	 * 设置摄像头经纬度。
	 * @param cameraCoord - 摄像头经纬度。
	 */
	public void setCameraCoord(NaviLatLng cameraCoord) {
		this.cameraCoord = cameraCoord;
	}

	/**
	 * 设置自车纬度。
	 * @param lat - 纬度。
	 */
	public void setLatitude(double lat) {
		this.m_Latitude = lat;
	}

	/**
	 * 设置自车经度。
	 * @param lon - 经度。
	 */
	public void setLongitude(double lon) {
		this.m_Longitude = lon;
	}
}