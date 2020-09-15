package com.sfmap.plugin.core.controller;

import android.content.pm.ActivityInfo;
import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.app.IMPageHelper;
import com.sfmap.plugin.core.ctx.IMPlugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
/*package*/ class GetPageInfoInvoker extends IMInvoker {
	public GetPageInfoInvoker(IMPlugin plugin, Object controller, Method method) {
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

		Map<String, Object> result = new HashMap<String, Object>();
		String className = null;
		try {
			String action = (String) msg.get("action");
			if (action != null) {
				HashMap<String, String> actionMap = plugin.getConfig().getActionMap();
				if (actionMap != null) {
					className = actionMap.get(action);
				} else {
					className = action;
				}
			}

			if (className.charAt(0) == '.') {
				className = plugin.getConfig().getPackageName() + className;
			}

			HashMap<String, ActivityInfo> pageMap = plugin.getConfig().getPageMap();
			ActivityInfo info = pageMap.get(className);
			result.put(IMPageHelper.PAGE_INFO_KEY, info);
			result.put(IMPageHelper.PAGE_CLASS_KEY, plugin.loadClass(className));

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
