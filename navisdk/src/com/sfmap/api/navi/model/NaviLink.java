package com.sfmap.api.navi.model;

import com.sfmap.api.navi.enums.RoadClass;
import com.sfmap.api.navi.enums.RoadType;

import java.util.List;

/**
 * 道路link类
 */
public class NaviLink {
	private List<NaviLatLng> coords;
	private String roadName;
	private int length;
	private int time;
	private int roadClass;
	private boolean trafficLights;
	private int mRoadType;

	/**
	 * 获取该Link道路名称。
	 * @return 道路名称
	 */
	public String getRoadName() {
		return this.roadName;
	}

	/**
	 * 设置该Link道路名称。
	 * @param roadName - 道路名称
	 */
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	/**
	 * 获取该Link道路长度。
	 * @return 道路长度，单位：米
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * 设置该Link道路长度。
	 * @param length - 道路长度，单位：米
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 获取该Linkd道路坐标点集。
	 * @return 坐标点集合
	 */
	public List<NaviLatLng> getCoords() {
		return this.coords;
	}

	/**
	 * 设置该Linkd道路坐标点集。
	 * @param coords - 该Link道路坐标点集
	 */
	public void setCoords(List<NaviLatLng> coords) {
		this.coords = coords;
	}

	/**
	 * 获取该Link道路耗时。
	 * @return 该Link道路耗时
	 */
	public int getTime() {
		return this.time;
	}

	/**
	 * 设置该Link道路耗时。
	 * @param time - 该Link道路耗时
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * 获取该Link道路等级。参见 {@link RoadClass } 定义。
	 * @return 道路等级
	 */
	public int getRoadClass() {
		return this.roadClass;
	}

	/**
	 * 设置该Link道路等级。 道路等级参见 {@link RoadClass } 定义。
	 * @param roadClass - 该Link道路等级
	 */
	public void setRoadClass(int roadClass) {
		this.roadClass = roadClass;
	}

	/**
	 * 获取该Link道路是否有红绿灯。
	 * @return 该Link道路是否有红绿灯，（一个Link最多一个红绿灯）
	 */
	public boolean getTrafficLights() {
		return this.trafficLights;
	}

	/**
	 * 设置该Link道路是否有红绿灯。
	 * @param trafficLights - 该Link道路是否有红绿灯
	 */
	public void setTrafficLights(boolean trafficLights) {
		this.trafficLights = trafficLights;
	}

	/**
	 * 获取该Link道路类型，参见 {@link RoadType } 定义。
	 * @return  该Link道路类型。
	 */
	public int getRoadType() {
		return this.mRoadType;
	}

	/**
	 * 设置该Link道路类型，参见 {@link RoadType } 定义。
	 * @param roadType - 该Link道路类型
	 */
	public void setRoadType(int roadType) {
		this.mRoadType = roadType;
	}
}