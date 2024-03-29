package com.sfmap.plugin.core.ctx;

import com.sfmap.plugin.core.install.Config;
import com.sfmap.plugin.core.install.IMInstaller;
import dalvik.system.DexClassLoader;

import java.io.File;

/**
 */
/*package*/ final class ModuleClassLoader extends DexClassLoader {

	private Module module;
	private final Config config;
	private final ClassLoader bootClassLoader;
	private final ClassLoader appClassLoader;

	public ModuleClassLoader(File pluginFile, Config config) {
		super(// dexPath
			pluginFile.getAbsolutePath(),
			// optimizedDir
			pluginFile.getParentFile().getAbsolutePath(),
			// libDir
			IMInstaller.getModuleInstallDir(config.getPackageName()).getAbsolutePath(),
			// parent
			IMHostParentClassLoader.getBootClassLoader());
		this.config = config;
		this.bootClassLoader = IMHostParentClassLoader.getBootClassLoader();
		this.appClassLoader = IMHostParentClassLoader.getAppClassLoader();
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, true);
	}

	@Override
	public Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		Class<?> result = super.findLoadedClass(className);

		if (result == null) {

			try {
				result = this.bootClassLoader.loadClass(className);
			} catch (Throwable ignored) {
			}

			if (result == null) {
				try {
					result = this.findClass(className); // 优先查找内部类型
				} catch (Throwable ignored) {
				}

				if (result == null && resolve) {
					try {
						// 从app classLoader查找,
						// 在查找过程中会跳到它的parent:HostParentClassLoader中.
						// HostParentClassLoader#findClass会查找所有加载项
						result = this.appClassLoader.loadClass(className);
					} catch (Throwable ignored) {
					}
				}
			}
		}

		if (result == null) {
			throw new ClassNotFoundException(className);
		}

		return result;
	}

	/**
	 * 修复阿里云v2.1等早期版本findLibrary
	 * 以系统内置为最高优先级加载的问题
	 *
	 * @param name
	 * @return
	 */
	@Override
	public String findLibrary(String name) {
		File libFile = IMInstaller.findLibrary(config.getPackageName(), name);
		if (libFile.exists()) {
			return libFile.getAbsolutePath();
		} else {
			return super.findLibrary(name);
		}
	}

	@Override
	public String toString() {
		return this.config.getPackageName();
	}
}
