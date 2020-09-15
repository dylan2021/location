package com.sfmap.tbt;

public class Camera {
	/**
	 * 电子眼类型，0 为测速摄像头，1为监控摄像头
	 */
	public int m_CameraType;

	/**
	 * 电子眼限速，若无限速信息则为0
	 */
	public int m_CameraSpeed;

	/**
	 * 电子眼所在位置的经度
	 */
	public double m_Longitude;

	/**
	 * 电子眼所在位置的纬度
	 */
	public double m_Latitude;

}