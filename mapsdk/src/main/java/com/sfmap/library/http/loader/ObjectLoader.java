package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.url.URLBuilder;
import com.sfmap.library.http.utils.IOUtils;
import com.sfmap.library.util.DebugLog;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 *
 */
public class ObjectLoader<T> implements Loader<T> {

	private String charset = HTTP.UTF_8;

	private Class<T> objectType;

	public ObjectLoader(Class<T> objectType) {
		this.objectType = objectType;
	}

	@Override
	public Loader<T> newInstance() {
		return new ObjectLoader<T>(objectType);
	}

	@Override
	public void setParams(final RequestParams params) {
		if (params != null) {
			String charset = params.getCharset();
			if (charset != null) {
				this.charset = charset;
			}
		}
	}

	@Override
	public void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler) {

	}

	@SuppressWarnings("static-access")
	@Override
	public T load(final InputStream in) throws IOException {
		Object result = null;
		try {
			rawCache = IOUtils.loadBytesAndUncompress(in);
			JSONObject rawData = new JSONObject(new String(rawCache, charset).trim());

			if (objectType.equals(Boolean.class)) {
				result = rawData.optBoolean("result", false);
			} else {
				JSONObject retObject = null;
				URLBuilder.ResultParser<?> parser = null;
				// 尝试Result类型的默认ResultProperty.
				URLBuilder.ResultProperty propertyAnn = objectType.getAnnotation(URLBuilder.ResultProperty.class);
				if (propertyAnn != null) {
					// property
					String property = propertyAnn.value();
					if (property.length() > 0) {
						retObject = rawData.optJSONObject(property);
					} else {
						retObject = rawData;
					}

					// parser
					Class<?> parserType = propertyAnn.parser();
					if (parserType.equals(URLBuilder.DefaultResultParser.class)) {
						parser = new URLBuilder.DefaultResultParser(objectType);
					} else {
						parser = (URLBuilder.ResultParser<?>) parserType.newInstance();
					}
				} else {
					retObject = rawData;
					parser = new URLBuilder.DefaultResultParser(objectType);
				}
				result = parser.parse(retObject);
			}
		} catch (Throwable e) {
			DebugLog.error(e);
		}
		return result == null ? null : (T) result;
	}

	@Override
	public T load(final URIRequest request) throws IOException {
		InputStream inputStream = null;
		T result = null;
		try {
			inputStream = request.getInputStream();
			result = this.load(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return result;
	}

	private byte[] rawCache;

	@Override
	public byte[] getRawCache() {
		return rawCache;
	}
}