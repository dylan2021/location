package com.sfmap.plugin.core.controller;

import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.app.IMPageHelper;
import com.sfmap.plugin.core.ctx.IMPlugin;
import com.sfmap.plugin.core.install.Config;
import com.sfmap.plugin.exception.ControllerInvokeException;
import com.sfmap.plugin.exception.ControllerNotFoundException;
import com.sfmap.plugin.task.TaskManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
final public class ControllerProxy {

	//wenbaolin 2018.11.07
	// 由于会编译多个不同applicationId的APP，但是类所在的包名没有变化，这里固定写死
	private final String strPkg = "com.sfmap.map";
	private static final String CONTROLLER_CLS_NAME = "IMController";
	private static final String SYNC_CMD_SUFFIX = "Sync";
	private final Object controller;
	private final ConcurrentHashMap<String, IMInvoker> invokerMap = new ConcurrentHashMap<String, IMInvoker>();

	private final IMPlugin plugin;

	public ControllerProxy(final IMPlugin plugin) {
		this.plugin = plugin;
		Config config = plugin.getConfig();
		String pkg = strPkg;//config.getPackageName();
		String ctrlClsName = pkg + "." + CONTROLLER_CLS_NAME;
		try {
			Class<?> ctrlCls = plugin.loadClass(ctrlClsName);
			controller = ctrlCls.newInstance();
		} catch (Throwable ex) {
			throw new ControllerNotFoundException(ctrlClsName, ex);
		}

		resolveInvoker();
	}

	/**
	 * @param msg
	 * @param msgCallback
	 */
	public void dispatchMsg(final IMPluginMsg msg, final MsgCallback msgCallback) {
		final IMInvoker invoker = invokerMap.get(msg.getCmd());
		if (invoker != null) {
			msg.setHandled(true); // 被成功dispatch的消息, handled默认值改为true.
			if (invoker instanceof AsyncInvoker) {
				invoker.invoke(msg, msgCallback);
			} else {
				TaskManager.post(new Runnable() {
					@Override
					public void run() {
						invoker.invoke(msg, msgCallback);
					}
				});
			}
		} else {
			ControllerInvokeException ex = new ControllerInvokeException(
				"not found " + controller.getClass().getName() + "#" + msg.getCmd());
			if (msgCallback != null) {
				msgCallback.error(ex, false);
			}
		}
	}

	private void resolveInvoker() {
		Method[] declaredMethods = controller.getClass().getDeclaredMethods();
		invokerMap.put(IMPageHelper.PAGE_INFO_CMD,
			new GetPageInfoInvoker(plugin, controller, null));
		if (declaredMethods != null) {
			for (Method method : declaredMethods) {
				if (!Modifier.isPublic(method.getModifiers())) {
					continue;
				}
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes != null && paramTypes.length > 0) {
					if (IMPluginMsg.class.equals(paramTypes[0])) {
						String cmd = method.getName();
						IMInvoker invoker = null;
						switch (paramTypes.length) {
							case 1: {
								if (cmd.endsWith(SYNC_CMD_SUFFIX)) {
									cmd = cmd.substring(0, cmd.length() - 4);
									invoker = new SyncInvoker(plugin, controller, method);
								} else {
									invoker = new AsyncInvoker(plugin, controller, method);
								}
								break;
							}
							case 2: {
								if (MsgCallback.class.equals(paramTypes[1])) {
									invoker = new CallbackInvoker(plugin, controller, method);
								}
								break;
							}
							default:
								break;
						}
						if (invoker != null) {
							invokerMap.put(cmd, invoker);
						}
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return plugin.toString();
	}
}
