package com.sfmap.api.navi.enums;

/**
 * 摄像头类型。
 */
public class CameraType {

	/**
	 * 0-测速摄像
	 */
	public static final int CAMERATYPE_SPEED = 0;

	/**
	 * 1-监控摄像
	 */
	public static final int CAMERATYPE_SURVEILLANCE = 1;

	/**
	 * 2-闯红灯拍照
	 */
	public static final int CAMERATYPE_TRAFFICLIGHT = 2;

	/**
	 * 3-违章拍照
	 */
	public static final int CAMERATYPE_BREAKRULE = 3;

	/**
	 * 4-公交专用道摄像头
	 */
	public static final int CAMERATYPE_BUSWAY = 4;

	/**
	 * 5-应急车道拍照
	 */
	public static final int CAMERATYPE_EMERGENCY = 5;

	/**
	 * 6-非机动车道拍照
	 */
	public static final int CAMERATYPE_BICYCLELANE = 6;

	/**
	 * 7-礼让行人
	 */
	public static final int CAMERATYPE_PEDESTRIANS = 7;

	/**
	 * 8-区间起点测速
	 */
	public static final int CAMERATYPE_REGIONSTART = 8;

	/**
	 * 9-区间终点测速
	 */
	public static final int CAMERATYPE_REGIONEND = 9;
}