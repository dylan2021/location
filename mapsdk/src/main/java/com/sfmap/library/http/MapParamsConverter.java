package com.sfmap.library.http;



import com.sfmap.library.Callback;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.util.DebugLog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 */
public class MapParamsConverter {

	@SuppressWarnings("rawtypes")
	public static RequestParams map2Params(Callback<?> callback, Map<String, Object> map, boolean isGetMethod) {
		RequestParams params = new RequestParams();

		if (callback != null) {
			if (callback instanceof Callback.CachePolicyCallback) {
				Callback.CachePolicyCallback cacheCallback = (Callback.CachePolicyCallback) callback;
				params.setCachePolicy(cacheCallback.getCachePolicy());
				params.setCacheKey(cacheCallback.getCacheKey());
			}
			if (callback instanceof Callback.ProgressCallback) {
				params.setSaveFilePath(((Callback.ProgressCallback) callback).getSavePath());
			}

			if (callback instanceof Callback.RequestExecutor) {
				params.setExecutor(((Callback.RequestExecutor) callback).getExecutor());
			}
			if (callback instanceof Callback.RequestPriority) {
				params.setPriority(((Callback.RequestPriority) callback).getPriority());
			}
		}

		if (map == null || map.size() == 0) return params;

		if (isGetMethod) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (key != null && value != null) {
					params.addQueryStringParameter(key, value.toString());
				}
			}
		} else {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (key != null && value != null) {
					if (value instanceof File) {
						params.addBodyParameter(key, (File) value);
					} else if (value instanceof InputStream) {
						params.addBodyParameter(key, (InputStream) value);
					} else if (value instanceof byte[]) {
						byte[] data = (byte[]) value;
						params.addBodyParameter(key, new ByteArrayInputStream(data));
					} else {
						params.addBodyParameter(key, String.valueOf(value));
					}
				} else if (key == null) {
					if (value instanceof InputStream) {
						params.setInputStream((InputStream) value);
					} else if (value instanceof File) {
						try {
							params.setInputStream(new FileInputStream((File) value));
						} catch (FileNotFoundException e) {
							DebugLog.error(e);
						}
					} else {
						params.setBodyContent(String.valueOf(value));
					}
				}
			}
		}

		return params;
	}
}
