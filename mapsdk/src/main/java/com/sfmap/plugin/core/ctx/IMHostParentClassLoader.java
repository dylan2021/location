package com.sfmap.plugin.core.ctx;

import android.util.Log;
import com.sfmap.plugin.app.IMPageHelper;
import com.sfmap.plugin.core.install.IMInstaller;
import com.sfmap.plugin.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 *
 * @date: 2014/10/30
 */
/*package*/ final class IMHostParentClassLoader extends ClassLoader {

	private static IMHostParentClassLoader instance;
	private static ClassLoader appClassLoader;
	private static ClassLoader bootClassLoader;

	private IMHostParentClassLoader(IMHost host) {
		// class loader结构:
		// bootClassLoader <-- HostParentClassLoader <-- appClassLoader
		ReflectUtil.init();
		appClassLoader = host.getClassLoader();
		Class<?> loaderCls = ClassLoader.class;
		try {
			Field parentField = loaderCls.getDeclaredField("parent");
			parentField.setAccessible(true);
			bootClassLoader = appClassLoader.getParent();
			parentField.set(this, bootClassLoader);
			parentField.set(appClassLoader, this);
		} catch (Throwable e) {
			Log.e("IMHostParentClassLoader", "init app class loader.", e);
		}
	}

	public synchronized static void init(IMHost host) {
		if (instance == null) {
			instance = new IMHostParentClassLoader(host);
		}
	}

	public static ClassLoader getBootClassLoader() {
		return bootClassLoader;
	}

	public static ClassLoader getAppClassLoader() {
		return appClassLoader;
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (IMPageHelper.HOST_ACTIVITY.equals(className)) {
			IMInstaller.waitForInit();
			return IMPageHelper.getTargetActivityClass();
		}

		Class<?> result = ReflectUtil.findClass(appClassLoader, className);
		if (result != null) {
			return result;
		}

		result = findClassFromModules(className);

		if (result == null) {
			throw new ClassNotFoundException(className);
		}

		return result;
	}

	// 从已加载的模块查找类型
	private Class<?> findClassFromModules(String className) {
		if (className.endsWith("Activity")) {
			IMInstaller.waitForInit();
		}
		Class<?> result = null;
		Collection<Module> moduleList = IMInstaller.getLoadedModules();
		if (moduleList != null) {
			for (Module module : moduleList) {
				try {
					// 不要让ModuleClassLoader的findClass查找其他依赖, 最好不要覆盖它.
					result = ((ModuleClassLoader) module.getClassLoader()).loadClass(className, false);
					if (result != null) {
						break;
					}
				} catch (Throwable ignored) {
				}
			}
		}
		return result;
	}
}