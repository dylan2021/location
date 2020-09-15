package com.sfmap.plugin.exception;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @date: 2014/10/30
 */
public class IMInstallException extends Exception {

	private List<String> packageNameList;
	private final List<Throwable> exList = new LinkedList<Throwable>();

	public IMInstallException(String message, Throwable cause) {
		super(message);
		this.addEx(cause);
	}

	public IMInstallException(String message, Throwable cause, String packageName) {
		super(message);
		this.addEx(cause);
		if (packageName != null) {
			packageNameList = new ArrayList<String>(1);
			packageNameList.add(packageName);
		}
	}

	@Override
	public Throwable getCause() {
		return exList.size() > 0 ? exList.get(0) : null;
	}

	public void addPackageName(String packageName) {
		if (packageName != null) {
			if (packageNameList == null) {
				packageNameList = new ArrayList<String>(1);
			}
			packageNameList.add(packageName);
		}
	}

	public void addEx(Throwable ex) {
		if (ex != null) {
			exList.add(ex);
		}
	}

	public List<String> getPackageNameList() {
		return packageNameList;
	}

	public int packageNameListCount() {
		return packageNameList == null ? 0 : packageNameList.size();
	}

	public List<Throwable> getExList() {
		return exList;
	}

	public int exListCount() {
		return exList.size();
	}
}
