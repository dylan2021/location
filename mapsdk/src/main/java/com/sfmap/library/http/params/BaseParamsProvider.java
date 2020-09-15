package com.sfmap.library.http.params;

import com.sfmap.library.CookieStore;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.loader.FileLoader;
import com.sfmap.library.http.utils.ProxyUtil;
import com.sfmap.library.http.url.URIBuilder;
import com.sfmap.library.http.PreferencesCookieStore;
import com.sfmap.library.task.Priority;
import com.sfmap.plugin.IMPluginManager;
import org.apache.http.NameValuePair;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BaseParamsProvider implements ParamsProvider {

	private static CookieStore cookieStore;

	public BaseParamsProvider() {
		if (cookieStore == null) {
			cookieStore = PreferencesCookieStore.getInstance(IMPluginManager.getApplication());
		}
	}

	@Override
	public ParamsProvider newInstance() {
		return new BaseParamsProvider();
	}

	@Override
	public boolean match(final String url, final RequestParams params) {
		return url.startsWith("http://") || url.startsWith("https://");  // match http or https.
	}

	@Override
	public URL buildURL(final String url, final URIRequest request) throws MalformedURLException, URISyntaxException {
		RequestParams params = request.getParams();
		if (params == null) {
			params = new RequestParams();
			request.setParams(params);
		}

		// 优化参数
//		params.setProxy(ProxyUtil.getProxy());
		if (request.getLoader() instanceof FileLoader) {
			params.setPriority(Priority.BG_NORMAL);
		}

		// build url with query string
		URIBuilder uriBuilder = new URIBuilder(url);

		HashMap<String, String> queryStringParams = params.getQueryStringParams();
		if (queryStringParams != null) {
			for (Map.Entry<String, String> entry : queryStringParams.entrySet()) {
				uriBuilder.addParameter(entry.getKey(), entry.getValue());
			}
		}
		String urlStr = uriBuilder.buildString(params.getCharset());
		String qStr = "";
		List<NameValuePair> queryParams = uriBuilder.getQueryParams();

		return new URL(urlStr + qStr);
	}

	@Override
	public void initConnection(final HttpURLConnection conn, final RequestParams params) {
		// 设置请求cookie
		conn.setRequestProperty("Cookie", cookieStore.getCookie());
		conn.setRequestProperty("User-Agent", "sfmap-android");

		// 设置header
		if (params != null) {
			HashMap<String, String> headers = params.getHeaders();
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	@Override
	public void syncHeaders(HttpURLConnection conn) {
		// 获取cookie和session
		String key = null;
		String setCookie = null;
		for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
			if (key.equalsIgnoreCase("Set-Cookie")) {
				setCookie = conn.getHeaderField(i);
				cookieStore.addSetCookie(setCookie);
			}
		}
	}
}
