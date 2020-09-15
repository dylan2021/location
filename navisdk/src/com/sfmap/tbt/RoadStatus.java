package com.sfmap.tbt;

public class RoadStatus {
	/********************************************************************
	 * 动态状态m_Status说明如下： 0 道路状态未知 1 道路通畅。 2 道路缓行。 3 道路阻塞严重。
	 ********************************************************************/
	public short m_Speed; // 速度（单位公里每小时）
	public int m_Status; // 状态，参见上定义
	public int m_PassTime;
	// ! 通过该路段所延误时间，单位：s
	public int m_DelayTime;
}