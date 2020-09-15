package com.sfmap.api.navi.enums;

/**
 * 无目的地播报类型
 */
public class AimLessMode {
	/**
	 * 0 - 无目的地播报不播
	 */
	public static int NONE_DETECTED = 0;

	/**
	 * 1 - 无目的地播报只播报电子眼
	 */
	public static int CAMERA_DETECTED = 1;

	/**
	 * 2 - 无目的地播报只播报特殊路段
	 */
	public static int SPECIALROAD_DETECTED = 2;

	/**
	 * 3 - 无目的地播报 播报电子眼和特殊路段
	 */
	public static int CAMERA_AND_SPECIALROAD_DETECTED = 3;
}