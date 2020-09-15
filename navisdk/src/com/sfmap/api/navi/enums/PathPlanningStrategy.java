package com.sfmap.api.navi.enums;

/**
 * 算路策略类型。
 */
public class PathPlanningStrategy {
	/**
	 * 0 - 费用优先（不走收费路段的最快道路）。
	 */
	public static int DRIVING_SAVE_MONEY = 0;
	/**
	 * 1 - 躲避拥堵
	 */
	public static int DRIVING_ON_TRAFFIC = 1;
	/**
	 * 2 - 国道优先。
	 */
	public static int DRIVING_ON_NATIONALWAY = 2;

	/**
	 * 4 - 省道优先。
	 */
	public static int DRIVING_ON_PROVINCIALWAY = 4;

	/**
	 * 5 - 不走高速。
	 */
	public static int DRIVING_AVOID_CONGESTION = 5;

	/**
	 * 6 - 多策略算路（同时返回速度优先、费用优先、距离优先的路径各一条）。
	 */
	public static int DRIVING_MULTIPLE_ROUTES = 6;

	/**
	 * 10 - 不走快速路（不走快速路，不包含高速路）。
	 */
	public static int DRIVING_NO_EXPRESS_WAYS = 10;

	/**
	 * 11 - 速度优先（快速路优先）。
	 */
	public static int DRIVING_DEFAULT = 11;

	/**
	 * 12 - 距离优先（路径最短）。
	 */
	public static int DRIVING_SHORT_DISTANCE = 12;

	/**
	 * 0 - 本地算路策略 最优策略
	 */
	public static int LOCAL_DRIVING_DEFAULT = 0;

	/**
	 * 1 - 本地算路策略 高速优先
	 */
	public static int LOCAL_DRIVING_HIGH_SPEED = 1;

	/**
	 * 2 - 本地算路策略 距离优先
	 */
	public static int LOCAL_DRIVING_SHORT_DISTANCE = 2;

	/**
	 * 3 - 本地算路策略 普通路优先
	 */
	public static int LOCAL_DRIVING_NORMAL_WAY = 3;
}