package com.sfmap.plugin.core.controller;

import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.core.ctx.IMPlugin;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 */
/*package*/ class SyncInvoker extends IMInvoker {

	public SyncInvoker(IMPlugin plugin, Object controller, Method method) {
		super(plugin, controller, method);
	}

	/**
	 * 处理消息
	 *
	 * @param msg
	 * @param msgCallback
	 * @return 是否处理完成
	 */
	public void invoke(final IMPluginMsg msg, final MsgCallback msgCallback) {

		Map<String, Object> result = null;
		try {
			result = (Map<String, Object>) method.invoke(controller, msg);

			if (msgCallback != null) {
				try {
					msgCallback.callback(result);
				} catch (Throwable ex) {
					msgCallback.error(ex, true);
				}
			}
		} catch (Throwable ex) {
			if (msgCallback != null) {
				msgCallback.error(ex, false);
			}
		}
	}
}
