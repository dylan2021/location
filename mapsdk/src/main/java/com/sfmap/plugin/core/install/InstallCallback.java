package com.sfmap.plugin.core.install;

import com.sfmap.plugin.core.ctx.Module;

/**
 *
 * @date: 2014/11/10
 */
public interface InstallCallback {

	void callback(Module module, boolean needRestart);

	void error(Throwable ex, boolean isCallbackError);
}
