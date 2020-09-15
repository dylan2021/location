package com.sfmap.library.http.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.sfmap.library.http.accelerate.MaaManager;
import com.sfmap.plugin.IMPluginManager;


import java.net.InetSocketAddress;

public class ProxyUtil {
//	public static java.net.Proxy getProxy() {
//		Context context = IMPluginManager.getApplication();
//		ConnectivityManager connectivityManager = (ConnectivityManager) context
//			.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//		java.net.Proxy result = null;
//		if (networkInfo != null
//			&& networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//			if (MaaManager.INSTANCE.getEnabled()) {
//				result = MaaManager.INSTANCE.getProxy();
//			}
//
//			if (result == null) {
//				result = getSystemProxy();
//			}
//		}
//		return result;
//	}
//
//	public static java.net.Proxy getSystemProxy() {
//		java.net.Proxy result = null;
//		int port = android.net.Proxy.getDefaultPort();
//		String host = android.net.Proxy.getDefaultHost();
//		if (!TextUtils.isEmpty(host) && port > 0) {
//			result = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(host, port));
//		}
//		return result;
//	}
}