package com.sfmap.library.http.accelerate;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.library.util.AppUtil;
import com.sfmap.plugin.core.ctx.IMPlugin;

import org.apache.http.HttpHost;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 网络请求加速。
 */
public enum MaaManager {

//	INSTANCE;
//
//	AtomicBoolean ENABLE_MAA = new AtomicBoolean(true);
//	private Integer result = null;
//
//	private MaaManager() {
//
//	}
//
//	public void setEnabled(boolean enable) {
//		ENABLE_MAA.set(enable);
//	}
//
//	public boolean getEnabled() {
//		if (AppUtil.hasFroyo())
//			return ENABLE_MAA.get();
//
//		return false;
//	}

//	public void start(Activity activity) {
//		if (ENABLE_MAA.get()) {
//			if (AppUtil.hasFroyo()) {
//				IMPlugin model = IMPlugin.getPlugin(this);
//				if (model != null) {
//					this.result  = com.mato.sdk.proxy.Proxy.start(model.getContext());
//					Log.e("MAA","result:"+result);
//					//removeMatoHandler(parent);
//				}
//			}
//		}
//	}

//	public void stop() {
//		if (ENABLE_MAA.get()) {
//			if (AppUtil.hasFroyo()) {
//				Thread.UncaughtExceptionHandler parent = Thread
//						.getDefaultUncaughtExceptionHandler();
//				com.mato.sdk.proxy.Proxy.stop();
//				//removeMatoHandler(parent);
//			}
//		}
//	}

//	public HttpHost getHttpHost() {
//		HttpHost proxyhost = null;
//		if (ENABLE_MAA.get() && result != null && result.intValue() ==  0) {
//			if (AppUtil.hasFroyo()) {
//				com.mato.sdk.proxy.Address address = getAddress();
//				if (address != null) {
//					String host = address.getHost();
//					int port = address.getPort();
//					if (!TextUtils.isEmpty(host)) {
//						proxyhost = new HttpHost(host, port);
//					}
//				}
//			}
//		}
//		return proxyhost;
//	}

//	private com.mato.sdk.proxy.Address getAddress() {
//		if(result != null && result.intValue() ==  0){
//		try{
//			Class.forName("com.mato.android.matoid.service.mtunnel.HttpHandler").getMethod("c").invoke(null);
//		}catch(UnsatisfiedLinkError e){
//			e.printStackTrace();
//			return null;
//		}catch(Throwable e){
//			e.printStackTrace();
//			return null;
//		}
//		com.mato.sdk.proxy.Address address = com.mato.sdk.proxy.Proxy
//				.getAddress();
//		return address;
//		}else{
//			return null;
//		}
//	}

//	public java.net.Proxy getProxy() {
//		if (ENABLE_MAA.get()&& result != null && result.intValue() ==  0) {
//			if (AppUtil.hasFroyo()) {
//				com.mato.sdk.proxy.Address address = getAddress();
//				if (address != null) {
//					String host = address.getHost();
//					int port = address.getPort();
//					if (!TextUtils.isEmpty(host)) {
//						return new java.net.Proxy(
//								java.net.Proxy.Type.HTTP,
//								new InetSocketAddress(host, port < 0 ? 0 : port));
//					}
//				}
//			}
//		}
//		return null;
//	}
}