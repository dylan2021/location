package com.sfmap.tbt;

/**
 * brief      用于描述光柱信息的结构体
 */
public class TmcBarItem {
	// ! TMC的状态：0未知状态，1通畅，2缓行，3 阻塞，4 严重阻塞
	public int m_Status;
	// ! TMC长度（单位米）
	public int m_Length;
}