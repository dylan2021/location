package com.sfmap.plugin.core.install;

import com.sfmap.plugin.core.ctx.Module;

/**
 *
 * @date: 2014/11/10
 */
public interface LoadCallback {

	void callback(Module module);

	void error(Throwable ex, boolean isCallbackError);
}
