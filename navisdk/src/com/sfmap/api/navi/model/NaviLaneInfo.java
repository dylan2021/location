package com.sfmap.api.navi.model;

/**
 * 车道线信息类。
 */
public class NaviLaneInfo {
	/**
	 * 车道线ID
	 */
	int laneTypeId;

	/**
	 * 构造函数。
	 */
	public NaviLaneInfo() {
		this.laneTypeId = 0;
	}

	/**
	 * 获取车道是否被推荐。
	 * @return 推荐返回true；否则，返回false
	 */
	public boolean isRecommended() {
		String str = getLaneTypeIdHexString();
		return (!(str.endsWith("F")));
	}

	/**
	 * 获取车道线信息的类型ID。
	 * @return 车道线信息的类型ID
	 */
	public int getLaneTypeId() {
		return this.laneTypeId;
	}

	/**
	 * 设置车道线信息的类型ID。
	 * @param laneTypeId - 车道线信息的类型ID
	 */
	public void setLaneTypeId(int laneTypeId) {
		this.laneTypeId = laneTypeId;
	}

	/**
	 * 获取车道线类型的字符串。
	 * @return 车道线类型的字符串
	 */
	public String getLaneTypeIdHexString() {
		return String.format("%1$02X", new Object[] { Integer.valueOf(this.laneTypeId) });
	}
}