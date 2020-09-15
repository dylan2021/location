package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.IOUtils;

import org.apache.http.protocol.HTTP;
import org.xidea.el.json.JSONDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 *
 */
public class MapLoader implements Loader<Map<String, Object>> {

	private String charset = HTTP.UTF_8;

	@Override
	public Loader<Map<String, Object>> newInstance() {
		return new MapLoader();
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
	public Map<String, Object> load(final InputStream in) throws IOException {
//		StringBuilder sb = new StringBuilder();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
//		String line = "";
//		while ((line = reader.readLine()) != null) {
//			sb.append(line).append('\n');
//		}
		try {
			rawCache = IOUtils.loadBytesAndUncompress(in);
			return JSONDecoder.decode(new String(rawCache, charset).trim());
		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> load(final URIRequest request) throws IOException {
		InputStream inputStream = null;
		Map<String, Object> result = null;
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
