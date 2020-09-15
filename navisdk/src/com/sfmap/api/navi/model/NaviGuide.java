package com.sfmap.api.navi.model;

/**
 * 导航路径信息类。
 */
public class NaviGuide {
	//路口点坐标
	private NaviLatLng coord;
	//
	private int length;
	//驾驶时间，单位为妙
	private int useTime;
	//
	private int icon;
	//
	private String name;

	NaviGuide(NaviGuideWrapper parama) {
		this.length = parama.m_Length;
		this.icon = parama.m_Icon;
		this.name = parama.m_Name;
		this.coord = parama.getNaviLatLng();
		this.useTime = parama.m_UseTime;
	}

	/**
	 * 构造函数。
	 */
	NaviGuide() {
	}

	/**
	 * 获取导航段路口点的坐标。
	 * @return 导航段路口点的坐标。
	 */
	public NaviLatLng getCoord() {
		return this.coord;
	}

	/**
	 * 设置导航段路口点坐标。
	 * @param latLng - 坐标。
	 */
	void setCoord(NaviLatLng latLng) {
		this.coord = latLng;
	}

	/**
	 * 获取导航段长度。
	 * @return 导航段长度， 单位：米
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * 设置导航段长度。
	 * @param length - 长度。
	 */
	void setLength(int length) {
		this.length = length;
	}

	/**
	 * 获取导航段转向类型。
	 * @return 获取导航段转向类型。
	 */
	public int getIconType() {
		return this.icon;
	}

	/**
	 * 设置导航段转向类型。
	 * @param iconType - 转向类型。
	 */
	void setIconType(int iconType) {
		this.icon = iconType;
	}

	/**
	 * 获取导航段名称。
	 * @return 导航段名称。
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 设置导航段名称。
	 * @param name - 导航段名称。
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取导航段时间。
	 * @return 导航段时间，单位为秒。
	 */
	public int getTime() {
		return this.useTime;
	}

	/**
	 * 设置导航段时间。
	 * @param useTime - 导航段时间。
	 */
	void setTime(int useTime) {
		this.useTime = useTime;
	}
}