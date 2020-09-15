package com.sfmap.api.navi.model;

/**
 * GPS位置信息
 */
public class NaviLocation {
	private float accuracy;
	private double altitude;
	private float bearing;
	private float speed;
	private long time;
	private int matchStatus;
	private NaviLatLng coord;

	/**
	 * 返回位置匹配状态。
	 * @return 0 未匹配到路径上，1 匹配到路径上
	 */
	public int getMatchStatus() {
		return this.matchStatus;
	}

	/**
	 * 设置位置匹配状态。
	 * @param matchStatus - 0 未匹配到路径上，1 匹配到路径上
	 */
	public void setMatchStatus(int matchStatus) {
		this.matchStatus = matchStatus;
	}

	/**
	 * 设置定位精度。
	 * @return 定位精度
	 */
	public float getAccuracy() {
		return this.accuracy;
	}

	/**
	 * 设置定位精度。
	 * @param accuracy - 定位精度
	 */
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * 返回海拔高度。如果返回0.0，说明没有返回海拔高度。
	 * @return 海拔高度。如果返回0.0，说明没有返回海拔高度。
	 */
	public Double getAltitude() {
		return Double.valueOf(this.altitude);
	}

	/**
	 * 设置海拔高度。
	 * @param altitude - 海拔高度。
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * 返回定位方位（方向）。
	 * @return 返回定位方位（方向）。定位方向，指的是相对正北方向的角度。
	 */
	public float getBearing() {
		return this.bearing;
	}

	/**
	 * 设置定位方位（方向）。
	 * @param bearing - 定位方位（方向）
	 */
	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	/**
	 * 返回当前定位点的速度， 如果此位置不具有速度，则返回0.0。
	 * @return 返回当前定位点的速度，单位米/秒。如果此位置不具有速度，则返回0.0。
	 */
	public float getSpeed() {
		return this.speed;
	}

	/**
	 * 设置当前定位点的速度。
	 * @param speed - 当前定位点的速度，单位米/秒。
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * 返回定位时间。定位时间指的是距离1970年 1月 1日 00:00:00 GMT的时间，单位为毫秒。
	 * @return 定位时间。
	 */
	public Long getTime() {
		return Long.valueOf(this.time);
	}

	/**
	 * 设置定位时间。定位时间指的是距离1970年 1月 1日 00:00:00 GMT的时间，单位为毫秒。
	 * @param time - 定位时间。
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * 返回当前位置是否匹配到道路上。
	 * @return 返回当前位置是否匹配到道路上。true，表示已匹配到道路 上；false，表示未匹配。
	 */
	public boolean isMatchNaviPath() {
		return (this.matchStatus != 0);
	}

	/**
	 * 返回当前位置的经纬度坐标。
	 * @return 返回当前位置的经纬度坐标。
	 */
	public NaviLatLng getCoord() {
		return this.coord;
	}

	/**
	 * 设置当前位置的经纬度坐标。
	 * @param latLng - 当前位置的经纬度坐标
	 */
	public void setCoord(NaviLatLng latLng) {
		this.coord = latLng;
	}
}