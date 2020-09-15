package com.sfmap.api.navi.model;

import com.sfmap.tbt.TrafficFacilityInfo;
import com.sfmap.api.navi.enums.CameraType;

/**
 * 道路设施信息。
 */
public class NaviTrafficFacilityInfo extends TrafficFacilityInfo {
	protected NaviLatLng latLng;
	/**
	 * 构造函数
	 * @param trafficFacilityInfo - 道路设施信息
	 */
	public NaviTrafficFacilityInfo(TrafficFacilityInfo trafficFacilityInfo) {
		this.broadcastType = trafficFacilityInfo.getBroardcastType();
		this.longitude = trafficFacilityInfo.getLongitude();
		this.latitude = trafficFacilityInfo.getLatitude();
		latLng= new NaviLatLng(latitude,longitude);
		this.distance = trafficFacilityInfo.getDistance();
		this.limitSpeed = trafficFacilityInfo.getLimitSpeed();
	}

	/**
	 * 获取摄像头限速速度。
	 * @return 限速速度，单位：公里/小时。
	 */
	public int getLimitSpeed() {
		return this.limitSpeed;
	}

	/**
	 * 设置摄像头限速速度。
	 * @param limitSpeed - 摄像头限速。
	 */
	public void setLimitSpeed(int limitSpeed) {
		this.limitSpeed = limitSpeed;
	}


	/**
	 * 获取摄像头坐标
	 * @return 摄像头坐标
	 */
	public NaviLatLng getCoords(){
		return latLng;
	}
	/**
	 * 设置摄像头坐标
	 * @param coords - 摄像头坐标。
	 */
	public void setCoords(NaviLatLng coords){
		this.latLng=coords;
	}
	/**
	 * 获取摄像头距离。
	 * @return 摄像头距离。
	 */
	public int getDistance() {
		return this.distance;
	}

	/**
	 * 设置摄像头距离。
	 * @param diatance - 摄像头距离。
	 */
	public void setDistance(int diatance) {
		this.distance = diatance;
	}

	/**
	 * 获取摄像头类型。参见 {@link CameraType } 定义。
	 * @return 摄像头类型。
	 */
	public int getBroardcastType() {
		return this.broadcastType;
	}

	/**
	 * 设置摄像头类型。
	 * @param boardcastType - 摄像头类型。参见 {@link CameraType } 定义。
	 */
	public void setBroardcastType(int boardcastType) {
		this.broadcastType = boardcastType;
	}

}