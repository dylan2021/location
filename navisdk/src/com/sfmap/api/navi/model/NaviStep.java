package com.sfmap.api.navi.model;

import com.sfmap.tbt.NaviAction;

import java.util.List;

/**
 * 路段类。
 */
public class NaviStep {
	private int length;
	private int time;
	private int chargeLength;
	private int startIndex;
	private int endIndex;
	private List<NaviLatLng> coords;
	private List<NaviLink> link;
	private NaviAction segNaviAction;

	/**
	 * 获取该路段起点index。
	 * @return 该路段起点index。
	 */
	public int getStartIndex() {
		return this.startIndex;
	}

	/**
	 * 设置该路段起点index。
	 * @param startIndex - 该路段起点index。
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * 获取该路段终点index。
	 * @return 路段终点index。
	 */
	public int getEndIndex() {
		return this.endIndex;
	}

	/**
	 * 设置该路段终点index。
	 * @param endIndex - 路段终点index
	 */
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * 获取该路段长度。
	 * @return 该路段长度。
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * 设置该路段长度。
	 * @param length - 该路段长度
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 获取该路段耗时。
	 * @return 路段耗时。
	 */
	public int getTime() {
		return this.time;
	}

	/**
	 * 设置该路段耗时。
	 * @param time - 路段耗时。
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * 获取该路段收费路段距离。
	 * @return 该路段收费路段距离。
	 */
	public int getChargeLength() {
		return this.chargeLength;
	}

	/**
	 * 设置该路段收费路段距离。
	 * @param chargeLength - 该路段收费路段距离
	 */
	public void setChargeLength(int chargeLength) {
		this.chargeLength = chargeLength;
	}

	/**
	 * 获取该路段坐标点集。
	 * @return 该路段坐标点集。
	 */
	public List<NaviLatLng> getCoords() {
		return this.coords;
	}

	/**
	 * 设置该路段坐标点集。
	 * @param coors - 该路段坐标点集
	 */
	public void setCoords(List<NaviLatLng> coors) {
		this.coords = coors;
	}

	/**
	 * 获取该路段Link集。
	 * @return 该路段Link集。
	 */
	public List<NaviLink> getLinks() {
		return this.link;
	}

	/**
	 * 设置该路段Link集。
	 * @param naviLinks - 该路段Link集。
	 */
	public void setLink(List<NaviLink> naviLinks) {
		this.link = naviLinks;
	}

	/**
	 *   segIndex 导航段编号，从0开始编号
	 * @return 导航动作对象NaviAction
	 *
     */
	public NaviAction getSegNaviAction() {
		return segNaviAction;
	}

	 public void setSegNaviAction(NaviAction segNaviAction) {
		this.segNaviAction = segNaviAction;
	}
}