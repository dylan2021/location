package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.IOUtils;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class JSONObjectLoader implements Loader<JSONObject> {

	private byte[] rawCache;
	private String charset = HTTP.UTF_8;

	@Override
	public Loader<JSONObject> newInstance() {
		return new JSONObjectLoader();
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

	@Override
	public JSONObject load(final InputStream in) throws IOException {
//		StringBuilder sb = new StringBuilder();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
//		String line = "";
//		while ((line = reader.readLine()) != null) {
//			sb.append(line).append('\n');
//		}
		try {
			rawCache = IOUtils.loadBytesAndUncompress(in);
			return new JSONObject(new String(rawCache, charset).trim());
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public JSONObject load(final URIRequest request) throws IOException {
		InputStream inputStream = null;
		JSONObject result = null;
		try {
			inputStream = request.getInputStream();
			result = this.load(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return result;
	}

	@Override
	public byte[] getRawCache() {
		return rawCache;
	}
}
