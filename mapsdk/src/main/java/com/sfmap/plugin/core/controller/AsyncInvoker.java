package com.sfmap.plugin.core.controller;

import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.core.ctx.IMPlugin;
import com.sfmap.plugin.task.Task;
import com.sfmap.plugin.task.TaskManager;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @date: 2014/11/13
 */
/*package*/ class AsyncInvoker extends IMInvoker {
	public AsyncInvoker(IMPlugin plugin, Object controller, Method method) {
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

		TaskManager.start(new Task<Map<String, Object>>() {
			@Override
			protected Map<String, Object> doBackground() throws Exception {
				return (Map<String, Object>) method.invoke(controller, msg);
			}

			@Override
			protected void onFinished(Map<String, Object> result) {
				if (msgCallback != null) {
					try {
						msgCallback.callback(result);
					} catch (Throwable ex) {
						msgCallback.error(ex, true);
					}
				}
			}

			@Override
			protected void onError(Throwable ex, boolean isCallbackError) {
				if (msgCallback != null) {
					msgCallback.error(ex, false);
				}
			}
		});
	}
}
