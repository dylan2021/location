package com.sfmap.plugin.exception;

/**
 *
 * @date: 2014/10/30
 */
public class IMConfigException extends RuntimeException {

	private String fileName;

	public IMConfigException(String fileName, Throwable cause) {
		super("config read error:" + fileName, cause);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
