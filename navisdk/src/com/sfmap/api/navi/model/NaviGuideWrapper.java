package com.sfmap.api.navi.model;

/**
 * 原类名：a
 */
public class NaviGuideWrapper extends com.sfmap.tbt.NaviGuideItem {
	public NaviGuide naviGuide;
	private NaviLatLng naviLatLng;

	public NaviGuideWrapper(com.sfmap.tbt.NaviGuideItem paramNaviGuideItem) {
		this.m_Length = paramNaviGuideItem.m_Length;
		this.m_Icon = paramNaviGuideItem.m_Icon;
		this.m_Name = paramNaviGuideItem.m_Name;
		this.m_UseTime = paramNaviGuideItem.m_UseTime;
		this.naviLatLng = new NaviLatLng(paramNaviGuideItem.m_Latitude,
				paramNaviGuideItem.m_Longitude);
		this.naviGuide = new NaviGuide(this);
	}

	public NaviGuideWrapper(NaviGuideItem paramNaviGuideItem) {
		this.m_Length = paramNaviGuideItem.m_Length;
		this.m_Icon = paramNaviGuideItem.m_Icon;
		this.m_Name = paramNaviGuideItem.m_Name;
		this.m_UseTime = paramNaviGuideItem.m_UseTime;
		this.naviLatLng = new NaviLatLng(paramNaviGuideItem.m_Latitude,
				paramNaviGuideItem.m_Longitude);
		this.naviGuide = new NaviGuide(this);
	}

	public NaviGuideWrapper() {
		this.naviGuide = new NaviGuide();
	}

	public NaviLatLng getNaviLatLng() {
		return this.naviLatLng;
	}
}