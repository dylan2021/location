package com.sfmap.api.navi.enums;

/**
 * 路径规划错误码。
 */
public class PathPlanningErrCode {

	/**
	 * -1 - 本地鉴权失败
	 */
	public static final int LOCAL_AUTH_FAIL = -1;

	/**
	 * 1 - 路径计算成功
	 */
	public static final int SUCCESS_ROUTE = 1;

	/**
	 * 2 - 路径计算错误异常：网络超时或网络失败
	 */
	public static final int ERROR_CONNECTION = 2;

	/**
	 * 3 - 路径计算错误异常：起点错误
	 */
	public static final int ERROR_STARTPOINT = 3;

	/**
	 * 4 - 路径计算错误异常：协议解析错误
	 */
	public static final int ERROR_PROTOCOL = 4;

	/**
	 * 6 - 路径计算错误异常：终点错误
	 */
	public static final int ERROR_ENDPOINT = 6;

	/**
	 * 10 - 路径计算错误异常：起点没有找到道路
	 */
	public static final int ERROR_NOROADFORSTARTPOINT = 10;

	/**
	 * 11 - 路径计算错误异常：终点没有找到道路
	 */
	public static final int ERROR_NOROADFORENDPOINT = 11;

	/**
	 * 12 - 路径计算错误异常：途径点没有找到道路
	 */
	public static final int ERROR_NOROADFORWAYPOINT = 12;

	/**
	 * 13 - 用户key非法或过期
	 */
	public static final int INVALID_USER_KEY = 13;

	/**
	 * 14 - 请求服务不存在
	 */
	public static final int SERVICE_NOT_EXIST = 14;

	/**
	 * 15 - 请求服务响应错误
	 */
	public static final int SERVICE_RESPONSE_ERROR = 15;

	/**
	 * 16 - 无权限访问此服务
	 */
	public static final int INSUFFICIENT_PRIVILEGES = 16;

	/**
	 * 17 - 请求超出配额
	 */
	public static final int OVER_QUOTA = 17;

	/**
	 * 18 - 请求参数非法
	 */
	public static final int INVALID_PARAMS = 18;

	/**
	 * 19 - 未知错误
	 */
	public static final int UNKNOWN_ERROR = 19;
}