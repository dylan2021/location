package com.sfmap.plugin.exception;

/**
 *
 * @date: 2014/10/30
 */
public class IMAlreadyLoadedException extends RuntimeException {
	private String packageName;

	public IMAlreadyLoadedException(String packageName) {
		super("already loaded:" + packageName);
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}
}
