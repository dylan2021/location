package com.sfmap.plugin;

import com.sfmap.plugin.core.ctx.IMPlugin;

/**
 * 发送消息时, 只有未指定toId时,即toId为空, 才调用这个接口.
 *
 *
 *
 */
public interface IMPluginMsgMatcher {
	/**
	 * 只有未指定toId时,即toId为空, 才调用这个方法.
	 *
	 * @param plugin
	 * @return 是否要将消息发送给这个插件
	 */
	boolean match(IMPlugin plugin);
}
