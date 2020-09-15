package com.sfmap.api.navi.model;

import com.sfmap.tbt.TmcBarItem;

/**
 * 原类名：e
 */
public class TrafficStatus extends TmcBarItem {
	public NaviTrafficStatus a;

	public TrafficStatus(TmcBarItem paramTmcBarItem) {
		this.m_Status = paramTmcBarItem.m_Status;
		this.m_Length = paramTmcBarItem.m_Length;
		this.a = new NaviTrafficStatus(paramTmcBarItem);
	}
}