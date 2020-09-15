package com.sfmap.tbt;

import java.io.Serializable;

/**
 * brief      导航 统计 信息结构体
 */
public class NaviStaticInfo implements Serializable {
	public int m_nStartSecond; // !< 导航开始时间，表示时间为当前天的第几秒，如36000表示１０点
	public int m_nEstimateTime; // !< 估算的行驶用时，不考虑偏航的影响，单位：秒
	public int m_nEstimateDist; // !< 估算的行驶里程，不考虑偏航的影响，单位：米
	public int m_nDrivenTime; // !< 实际的行驶用时，包括中途停车时间在内
	public int m_nDrivenDist; // !< 实际的行驶里程
	public int m_nAverageSpeed; // !< 平均速度, 实际的行驶里程／实际的行驶用时, 单位：公里／小时
	public int m_nHighestSpeed; // !< 最高速度, 单位：公里／小时
	public int m_nOverspeedCount; // !< 电子眼播报的超速次数，关闭电子眼播报则没有计数
	public int m_nRerouteCount; // !< 偏航次数
	public int m_nBrakesCount; // !< 急刹车次数
	public int m_nSlowTime; // !< 等待及拥堵时间 , 单位：秒
}