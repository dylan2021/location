package com.sfmap.library.http.params;

import android.text.TextUtils;


import com.sfmap.library.Callback;
import com.sfmap.library.http.entity.BodyParamsEntity;
import com.sfmap.library.http.entity.HttpEntity;
import com.sfmap.library.http.entity.InputStreamEntity;
import com.sfmap.library.http.entity.MultipartEntity;
import com.sfmap.library.http.entity.StringEntity;
import com.sfmap.library.http.utils.KeyValuePair;
import com.sfmap.library.task.Priority;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 网络请求参数
 * <p/>
 * 主要请求参数包括: Header 参数: 包含在请求头中的参数. Query String参数: 加在url后面的参数, 也称为GET参数. Body
 * 参数: 请求体参数, POST等参数添加在这里. ...
 * <p/>
 * 注意: 如果要下载文件请设置saveFilePath参数, 否则文件将被下载到磁盘缓存位置. 设置了下载断点续传(autoResume),
 * 对同一个url重新下载时, 如果服务器支持RANGE参数, 会自动接着上次的进度下载.
 * <p/>
 */
public class RequestParams {
	private HashMap<String, String> headers;
	private HashMap<String, String> queryStringParams;
	private HashMap<String, String> bodyParams;
	private HashMap<String, Object> fileParams;
	private InputStream inputStream;
	private String bodyContent;

	// 扩展参数
	private String charset = HTTP.UTF_8;
	private Executor executor;
	private Priority priority;
	private Proxy proxy;
	private Callback.CachePolicyCallback.CachePolicy cachePolicy = Callback.CachePolicyCallback.CachePolicy.Any;
	private int connectTimeout = 1000 * 30; // 远距离骑行路径规划请求耗时超过15秒
//	private int connectTimeout = 1000 * 15; // 连接超时时间
	private boolean autoResume = true; // 是否在下载是自动断点续传
	private boolean autoRename = false; // 是否根据头信息自动命名文件
	private int maxRetryCount = 3; // 最大请求错误重试次数
	private String saveFilePath; // 下载文件时文件保存的路径和文件名
	private boolean multipart = false; // 是否强制使用multipart表单
	private String cacheKey;
	private String loadingMessage;

	public RequestParams() {
	}

	public void setCharset(String charset) {
		if (!TextUtils.isEmpty(charset)) {
			this.charset = charset;
		}
	}

	public String getCharset() {
		return charset;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Callback.CachePolicyCallback.CachePolicy getCachePolicy() {
		return cachePolicy;
	}

	public void setCachePolicy(Callback.CachePolicyCallback.CachePolicy cachePolicy) {
		if (cachePolicy != null) {
			this.cachePolicy = cachePolicy;
		}
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		if (connectTimeout > 0) {
			this.connectTimeout = connectTimeout;
		}
	}

	/**
	 * 是否在下载是自动断点续传
	 */
	public boolean isAutoResume() {
		return autoResume;
	}

	/**
	 * 设置是否在下载是自动断点续传
	 *
	 * @param autoResume
	 */
	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	/**
	 * 是否根据头信息自动命名文件
	 */
	public boolean isAutoRename() {
		return autoRename;
	}

	/**
	 * 设置是否根据头信息自动命名文件
	 *
	 * @param autoRename
	 */
	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}

	/**
	 * 获取下载文件时文件保存的路径和文件名
	 */
	public String getSaveFilePath() {
		return saveFilePath;
	}

	/**
	 * 设置下载文件时文件保存的路径和文件名
	 *
	 * @param saveFilePath
	 */
	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}

	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	public void setMaxRetryCount(int maxRetryCount) {
		if (maxRetryCount > 0) {
			this.maxRetryCount = maxRetryCount;
		}
	}

	public boolean isMultipart() {
		return multipart;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getLoadingMessage() {
		return loadingMessage;
	}

	public void setLoadingMessage(String loadingMessage) {
		this.loadingMessage = loadingMessage;
	}

	public void addHeader(String name, String value) {
		if (this.headers == null) {
			this.headers = new HashMap<String, String>();
		}
		this.headers.put(name, value);
	}

	public void setHeader(String name, String value) {
		if (this.headers == null) {
			this.headers = new HashMap<String, String>();
		}
		this.headers.put(name, value);
	}

	public void addQueryStringParameter(String name, String value) {
		if (this.queryStringParams == null) {
			this.queryStringParams = new HashMap<String, String>();
		}
		this.queryStringParams.put(name, value);
	}

	public void addBodyParameter(String name, String value) {
		if (this.bodyParams == null) {
			this.bodyParams = new HashMap<String, String>();
		}
		this.bodyParams.put(name, value);
	}

	public void addBodyParameter(String key, File file) {
		if (this.fileParams == null) {
			this.fileParams = new HashMap<String, Object>();
		}
		this.fileParams.put(key, file);
	}

	public void addBodyParameter(String key, InputStream stream) {
		if (this.fileParams == null) {
			this.fileParams = new HashMap<String, Object>();
		}
		this.fileParams.put(key, stream);
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setBodyContent(String content) {
		this.bodyContent = content;
	}

	public String getBodyContent() {
		return bodyContent;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public HashMap<String, String> getQueryStringParams() {
		if (((fileParams != null && fileParams.size() > 0) ||
			inputStream != null ||
			!TextUtils.isEmpty(bodyContent))
			&& bodyParams != null) {
			if (this.queryStringParams == null) {
				this.queryStringParams = new HashMap<String, String>();
			}
			queryStringParams.putAll(bodyParams);
			bodyParams.clear();
		}
		return queryStringParams;
	}

	public HashMap<String, String> getBodyParams() {
		return bodyParams;
	}

	public HttpEntity getBodyEntity() throws UnsupportedEncodingException {
		HttpEntity result = null;
		if (inputStream != null) {
			result = new InputStreamEntity(inputStream);
			if (bodyParams != null && bodyParams.size() > 0) {
				if (queryStringParams == null) {
					queryStringParams = new HashMap<String, String>();
				}
				for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
					queryStringParams.put(entry.getKey(), entry.getValue());
				}
			}
		} else if (!TextUtils.isEmpty(bodyContent)) {
			result = new StringEntity(bodyContent, charset);
			if (bodyParams != null && bodyParams.size() > 0) {
				if (queryStringParams == null) {
					queryStringParams = new HashMap<String, String>();
				}
				for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
					queryStringParams.put(entry.getKey(), entry.getValue());
				}
			}
		} else if (multipart || (fileParams != null && fileParams.size() > 0)) {
			multipart = true;
			List<KeyValuePair> multipartParams = new ArrayList<KeyValuePair>();
			if (bodyParams != null && bodyParams.size() > 0) {
				for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
					multipartParams.add(new KeyValuePair(entry.getKey(), entry
						.getValue()));
				}
			}
			if (fileParams != null) {
				for (Map.Entry<String, Object> entry : fileParams.entrySet()) {
					Object value = entry.getValue();
					if (value instanceof File) {
						if (!((File) value).exists()) {
							continue;
						}
					}
					multipartParams
						.add(new KeyValuePair(entry.getKey(), value));
				}
			}
			result = new MultipartEntity(multipartParams, charset);
		} else if (bodyParams != null && bodyParams.size() > 0) {
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
				pairList.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
			}
			result = new BodyParamsEntity(pairList, charset);
		}

		return result;
	}
}
