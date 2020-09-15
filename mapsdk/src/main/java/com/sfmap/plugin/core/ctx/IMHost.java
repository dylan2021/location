package com.sfmap.plugin.core.ctx;

import android.app.Application;
import com.sfmap.plugin.core.controller.ControllerProxy;
import com.sfmap.plugin.core.install.Config;

/**
 * @date: 2014/10/29
 */
public final class IMHost extends IMPlugin {

	public IMHost(Application app, Config config) {
		super(app, config);
		IMHostParentClassLoader.init(this);
		this.controllerProxy = new ControllerProxy(this);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return this.getClassLoader().loadClass(name);
	}
}
