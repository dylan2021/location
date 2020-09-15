package com.sfmap.api.navi.model;

import com.sfmap.tbt.TmcBarItem;

/**
 * 前方交通路况信息类，即路况光柱信息类。
 */
public class NaviTrafficStatus {
	private int mStatus;
	private int mLength;

	public NaviTrafficStatus(TmcBarItem paramTmcBarItem) {
		this.mStatus = paramTmcBarItem.m_Status;
		this.mLength = paramTmcBarItem.m_Length;
	}

	/**
	 * 获取交通状态。0未知状态，1畅通，2缓行，3阻塞,4严重拥堵。
	 * @return 返回交通状态。
	 */
	public int getStatus() {
		return this.mStatus;
	}

	void setStatus(int paramInt) {
		this.mStatus = paramInt;
	}

	/**
	 * 返回该交通状态路段的拥堵状态长度。
	 * @return 返回该交通状态路段的长度，单位：米。
	 */
	public int getLength() {
		return this.mLength;
	}

	void setLength(int paramInt) {
		this.mLength = paramInt;
	}
}