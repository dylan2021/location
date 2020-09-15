package com.sfmap.tbt.util;

/**
 * 导航SDK异常类，用于异常信息描述
 */
public class NaviCommException extends Exception {
	/**
	 * 未知的错误。
	 */
	public static final String ERROR_UNKNOWN = "未知的错误";

	/**
	 * 非法参数。
	 */
	public static final String ILLEGAL_ARGUMENT = "非法参数";

	/**
	 * 当前异常的描述信息。
	 */
	private String mDetailMessage = "未知的错误";

	/**
	 * 构造函数。
	 * @param detail - 详细异常描述
	 */
	public NaviCommException(String detail) {
		super(detail);
		this.mDetailMessage = detail;
	}

	/**
	 * 构造函数。
	 */
	public NaviCommException() {
	}

	/**
	 * 获取异常的错误信息详情。
	 * @return 异常的错误信息详情
	 */
	public String getErrorMessage() {
		return this.mDetailMessage;
	}
}