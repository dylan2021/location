package com.sfmap.plugin.exception;

import com.sfmap.plugin.IMPluginMsg;

/**
 *
 *
 */
public class IMMsgRejectException extends Exception {

	private IMPluginMsg msg;

	public IMMsgRejectException(IMPluginMsg msg) {
		super("msg has been rejected");
		this.msg = msg;
	}

	public IMPluginMsg getMsg() {
		return msg;
	}
}
