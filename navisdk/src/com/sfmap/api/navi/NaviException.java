package com.sfmap.api.navi;

/**
 * 导航SDK异常类，用于异常信息描述
 */
public class NaviException extends Exception {
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
	public NaviException(String detail) {
		super(detail);
		this.mDetailMessage = detail;
	}

	/**
	 * 构造函数。
	 */
	public NaviException() {
	}

	/**
	 * 获取异常的错误信息详情。
	 * @return 异常的错误信息详情
	 */
	public String getErrorMessage() {
		return this.mDetailMessage;
	}
}