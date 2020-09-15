package com.sfmap.tbt;

import java.net.Proxy;
import java.util.Map;

/**
 * 原类名：ay
 */
public abstract class Request {
	int connectTimeout;
	int readTimeout;
	Proxy proxy;

	public Request() {
		this.connectTimeout = 20000;

		this.readTimeout = 20000;

		this.proxy = null;
	}

	public abstract Map<String, String> getAuthHead();

	public abstract Map<String, String> getHttpHead();

	public abstract String getURL();

	public abstract String getRh();

	public final void setConnectTimeOut(int paramInt) {
		this.connectTimeout = paramInt;
	}

	public final void setReadTimeOut(int paramInt) {
		this.readTimeout = paramInt;
	}

	public byte[] d() {
		return null;
	}

	public final void setProxy(Proxy paramProxy) {
		this.proxy = paramProxy;
	}
}