package com.sfmap.plugin.core.ctx;

import android.content.Context;
import android.view.LayoutInflater;
import com.sfmap.plugin.core.controller.ControllerProxy;
import com.sfmap.plugin.core.install.Config;
import com.sfmap.plugin.core.install.IMInstaller;

/**
 *
 * @date: 2014/10/29
 */
public abstract class IMPlugin {
	private final Config config;
	private final Context context;
	private LayoutInflater layoutInflater;
	protected ControllerProxy controllerProxy;

	/*package*/ IMPlugin(Context context, Config config) {
		this.context = context;
		this.config = config;
	}

	/**
	 * 获取对象或类型所在的插件
	 *
	 * @param obj 对象实例或class
	 * @return 产生这个实例的Plugin
	 */
	public static IMPlugin getPlugin(Object obj) {
		ClassLoader classLoader = null;
		if (obj instanceof Class) {
			classLoader = ((Class<?>) obj).getClassLoader();
		} else {
			classLoader = obj.getClass().getClassLoader();
		}
		if (classLoader instanceof ModuleClassLoader) {
			return ((ModuleClassLoader) classLoader).getModule();
		} else {
			return IMInstaller.getHost();
		}
	}

	public abstract Class<?> loadClass(String name) throws ClassNotFoundException;

	/*package*/
	ClassLoader getClassLoader() {
		return this.context.getClassLoader();
	}

	public Config getConfig() {
		return this.config;
	}

	public Context getContext() {
		return context;
	}

	public LayoutInflater getLayoutInflater() {
		if (layoutInflater == null) {
			LayoutInflater service = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.layoutInflater = service.cloneInContext(context);
		}
		return layoutInflater;
	}

	public ControllerProxy getControllerProxy() {
		return controllerProxy;
	}

	@Override
	public String toString() {
		return config.getPackageName();
	}
}
