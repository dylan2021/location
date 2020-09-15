package com.sfmap.plugin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.sfmap.plugin.core.controller.ControllerProxy;
import com.sfmap.plugin.core.ctx.ContextProxy;
import com.sfmap.plugin.core.ctx.IMHost;
import com.sfmap.plugin.core.ctx.IMPlugin;
import com.sfmap.plugin.core.ctx.Module;
import com.sfmap.plugin.core.install.IMInstaller;
import com.sfmap.plugin.exception.IMMsgRejectException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 */
public final class IMPluginManager {

	private static IMApplication app;
	private final static Object msgQueueLock = new Object();
	private final static int MSG_QUEUE_MAX_COUNT = 100;
	private static LinkedList<MsgQueueItem> msgQueue = new LinkedList<MsgQueueItem>();

	private IMPluginManager() {
	}

	/**
	 * 只能从HostApplication初始化
	 *
	 * @param app
	 */
	static void init(final IMApplication app) {
		IMPluginManager.app = app;
		IMInstaller.initHost(new IMPluginManager());
	}

	public static Application getApplication() {
		return app;
	}

	/**
	 * 获取对象或类型所在插件的包名
	 *
	 * @param obj 对象实例或class
	 * @return
	 */
	public static String getPackageName(Object obj) {
		return IMPlugin.getPlugin(obj).getConfig().getPackageName();
	}

	/**
	 * 为插件中的AlertDialog生成context.
	 *
	 * @param activity
	 * @param obj
	 * @return
	 */
	public static Context contextForAlertDialog(Activity activity, Object obj) {
		return new ContextProxy(activity, obj);
	}

	/**
	 * 发送消息, 消息由controller去处理.
	 */
	public static void sendMsg(final IMPluginMsg msg, final MsgCallback msgCallback) {
		sendMsgInternal(msg, msgCallback, null, false);
	}

	/**
	 * 发送消息, 消息由controller去处理.
	 *
	 * @param msg
	 * @param msgCallback
	 * @param modules     如果没有targetPackage, 发给指定的modules集合, modules为null时发给已加载的所有插件.
	 * @param fromQueue   是否是队列中未处理的消息.
	 */
	private static void sendMsgInternal(final IMPluginMsg msg, final MsgCallback msgCallback, List<Module> modules, boolean fromQueue) {
		boolean needResend = true;
		String pkg = msg.getTargetPackage();
		if (pkg != null && pkg.length() > 0) { // 发送给指定插件
			IMPlugin toPlugin = IMInstaller.getLoadedPlugin(pkg);
			if (toPlugin != null) {
				ControllerProxy controller = toPlugin.getControllerProxy();
				if (controller != null) {
					needResend = false;
					controller.dispatchMsg(msg, msgCallback);
				}
			}
		} else { // 发送给自定义匹配插件
			if (modules == null) {
				IMHost host = IMInstaller.getHost();
				if (msg.match(host)) {
					ControllerProxy controller = host.getControllerProxy();
					if (controller != null) {
						needResend = false;
						controller.dispatchMsg(msg, msgCallback);
					}
				}
			}

			Collection<Module> moduleList = modules == null ? IMInstaller.getLoadedModules() : modules;
			if (moduleList != null) {
				for (Module module : moduleList) {
					if (msg.match(module)) {
						ControllerProxy controller = module.getControllerProxy();
						if (controller != null) {
							needResend = false;
							controller.dispatchMsg(msg, msgCallback);
						}
					}
				}
			}
		}

		if (!fromQueue && needResend) {
			// 加入消息队列等待加载事件回调再执行
			synchronized (msgQueueLock) {
				msgQueue.addLast(new MsgQueueItem(msg, msgCallback));
				if (msgQueue.size() > MSG_QUEUE_MAX_COUNT) {
					msgQueue.removeFirst();
				}
			}
			if (pkg != null && pkg.length() > 0) {
				IMInstaller.loadModule(pkg, null);
			}
		}
	}

	public boolean isDebug() {
		return app.isDebug();
	}

	/**
	 * 宿主初始化完成回调
	 * <p/>
	 * 如果宿主初始化报错, 则在onPluginsLoadError方法之后回调.
	 */
	public void onHostInitialised() {
		app.onHostInitialised();
	}

	/**
	 * 仅被异步安装或加载过程回调
	 */
	public void onModulesLoaded(List<Module> modules) {
		app.onModulesLoaded(modules);
		Iterator<MsgQueueItem> iterator = msgQueue.iterator();
		while (iterator.hasNext()) {
			MsgQueueItem item = iterator.next();
			if (!item.sendAgain()) {
				synchronized (msgQueueLock) {
					iterator.remove();
				}
				// 未处理的消息移除后通知发送者
				if (!item.msg.isHandled() && item.msgCallback != null) {
					item.msgCallback.error(new IMMsgRejectException(item.msg), false);
				}
			} else {
				sendMsgInternal(item.msg, item.msgCallback, modules, true);
			}
		}
	}

	/**
	 * 仅被异步安装或加载过程回调
	 */
	public void onPluginsLoadError(Throwable ex, boolean isCallbackError) {
		app.onPluginsLoadError(ex, isCallbackError);
	}

	private static class MsgQueueItem {
		IMPluginMsg msg;
		MsgCallback msgCallback;
		int sendCount = 0;
		static final int SEND_MAX_COUNT = 5;

		public MsgQueueItem(IMPluginMsg msg, MsgCallback msgCallback) {
			this.msg = msg;
			this.msgCallback = msgCallback;
		}

		public boolean sendAgain() {
			sendCount++;
			return !msg.isHandled() && sendCount < SEND_MAX_COUNT;
		}
	}
}
