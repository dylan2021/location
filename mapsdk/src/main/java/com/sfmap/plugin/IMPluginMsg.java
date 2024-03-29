package com.sfmap.plugin;

import com.sfmap.plugin.core.ctx.IMPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @date: 2014/10/29
 */
public final class IMPluginMsg {
	private String targetPackage;
	private final String cmd;
	private Map<String, Object> data;
	private boolean handled = false;
	private IMPluginMsgMatcher msgMatcher;

	public IMPluginMsg(String targetPackage, String cmd) {
		this(targetPackage, cmd, null);
	}

	public IMPluginMsg(String targetPackage, String cmd, Map<String, Object> data) {
		this.targetPackage = targetPackage;
		this.cmd = cmd;
		this.data = data;
	}

	/**
	 * 只有未指定toId时,即toId为空, 才调用这个方法.
	 *
	 * @param plugin
	 * @return 是否要将消息发送给这个插件
	 */
	public boolean match(IMPlugin plugin) {
		if (msgMatcher != null) {
			return msgMatcher.match(plugin);
		}
		return true;
	}

	public void put(String key, Object value) {
		if (data == null) {
			data = new HashMap<String, Object>();
		}

		data.put(key, value);
	}

	public Object get(String key) {
		if (data != null) {
			return data.get(key);
		}
		return null;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public String getCmd() {
		return cmd;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public boolean isHandled() {
		return handled;
	}

	public void setHandled(boolean handled) {
		this.handled = handled;
	}

	public IMPluginMsgMatcher getMsgMatcher() {
		return msgMatcher;
	}

	public void setMsgMatcher(IMPluginMsgMatcher msgMatcher) {
		this.msgMatcher = msgMatcher;
	}
}
