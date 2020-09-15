package com.sfmap.plugin.exception;

/**
 *
 * @date: 2014/10/30
 */
public class IMVerifyException extends RuntimeException {

	private String fileName;

	public IMVerifyException(String fileName) {
		super("verify error:" + fileName);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
