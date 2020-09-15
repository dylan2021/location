package com.sfmap.tbt;

/**
 * brief 聚合段GroupSegment信息结构体
 */
public class GroupSegment {
	public String m_GroupName; // !< 聚合段名称
	public int m_nSegCount; // !< 包含导航段个数
	public int m_bArrivePass; // !< 是否到达途径地
	public int m_nStartSegId; // !< 起始导航段下标
	public int m_nDistance; // !< 聚合段长度
	public int m_nToll; // !< 聚合段收费金额
	public int m_nStatus; // !< 状态 // 0 未知状态 // 1 畅通状态 // 2 缓行状态 // 3 阻塞严重状态 //4
	// 超级严重阻塞状态
	public int m_nSpeed; // !< 速度
	public int m_bIsSrucial; // !< 重要性 1表示 重要 // 其中重要的，才是 你要展示的
}