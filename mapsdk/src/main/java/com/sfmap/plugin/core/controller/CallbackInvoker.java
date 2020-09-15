package com.sfmap.plugin.core.controller;

import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.core.ctx.IMPlugin;

import java.lang.reflect.Method;

/**
 *
 */
/*package*/ class CallbackInvoker extends IMInvoker {
	public CallbackInvoker(IMPlugin plugin, Object controller, Method method) {
		super(plugin, controller, method);
	}

	/**
	 * 处理消息
	 *
	 * @param msg
	 * @param msgCallback
	 * @return 是否处理完成
	 */
	@Override
	public void invoke(final IMPluginMsg msg, final MsgCallback msgCallback) {
		try {
			method.invoke(controller, msg, msgCallback);
		} catch (Throwable ex) {
			if (msgCallback != null) {
				msgCallback.error(ex, false);
			}
		}
	}
}
