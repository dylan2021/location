package com.sfmap.library.http;



import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.util.DebugLog;

import org.json.JSONException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashSet;

/**
 */
public class HttpRetryHandler {

	private int maxRetryCount;

	private static HashSet<Class<?>> blackList = new HashSet<Class<?>>();

	static {
		blackList.add(MalformedURLException.class);
		blackList.add(URISyntaxException.class);
		blackList.add(NoRouteToHostException.class);
		blackList.add(PortUnreachableException.class);
		blackList.add(ProtocolException.class);
		blackList.add(NullPointerException.class);
		blackList.add(FileNotFoundException.class);
		blackList.add(JSONException.class);
		//blackList.add(SocketTimeoutException.class); // 部分手机获得了错误的网宿代理
		//blackList.add(UnknownHostException.class); // 部分手机获得了错误的网宿代理
	}

	public HttpRetryHandler() {
		this(5);
	}

	public HttpRetryHandler(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	boolean retryRequest(Throwable e, int count, URIRequest request) {

		if (e instanceof ServerException ||
			// wifi连接时没有使用网宿代 , 不retry.
			(e instanceof SocketTimeoutException && NetworkImpl.getInstance().isWifiConnected())) {
			DebugLog.error(e);
			return false;
		}

		if (count > maxRetryCount || request == null) {
			DebugLog.warn("The Max Retry times has been reached!");
			DebugLog.error(e);
			return false;
		}

		if (request.getMethod() == HttpMethod.POST) {
			RequestParams params = request.getParams();
			if (params != null && !params.isMultipart()) {
				params.setMultipart(true);
				return true;
			}
		}

		if (request.getMethod() != HttpMethod.GET) {
			DebugLog.warn("The http method is not HTTP GET! The NetWork operation can not be retried.");
			DebugLog.error(e);
			return false;
		}

		if (blackList.contains(e.getClass())) {
			DebugLog.warn("The NetWork operation can not be retried.");
			DebugLog.error(e);
			return false;
		}

		return true;
	}
}
