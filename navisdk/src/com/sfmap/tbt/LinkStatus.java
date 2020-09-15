package com.sfmap.tbt;

/**
 * brief      用于描述道路状态信息的类
 * details    动态状态m_Status说明如下：
 * * 0   道路状态未知
 * * 1   道路通畅。
 * * 2   道路缓行。
 * * 3   道路阻塞
 * * 4   道路严重阻塞
 */
public class LinkStatus {
	// ! 速度（单位公里每小时）
	public short m_Speed;
	// ! 状态，参见上定义
	public int m_Status;
	// ! 预计通过该路段所需时间，单位：s
	public int m_PassTime;
	// ! 该段Link的长度，单位：m
	public int m_LinkLen;
}