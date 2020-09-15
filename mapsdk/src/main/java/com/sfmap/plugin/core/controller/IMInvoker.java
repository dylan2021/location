package com.sfmap.plugin.core.controller;

import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.core.ctx.IMPlugin;

import java.lang.reflect.Method;

/**
 *
 */
/*package*/ abstract class IMInvoker {

	protected final IMPlugin plugin;
	protected final Object controller;
	protected Method method;

	public IMInvoker(IMPlugin plugin, Object controller, Method method) {
		this.plugin = plugin;
		this.controller = controller;
		this.method = method;
	}

	/**
	 * 处理消息
	 *
	 * @param msg
	 * @param msgCallback
	 * @return 是否处理完成
	 */
	public abstract void invoke(final IMPluginMsg msg, final MsgCallback msgCallback);
}
