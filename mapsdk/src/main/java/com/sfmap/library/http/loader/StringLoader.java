package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.IOUtils;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class StringLoader implements Loader<String> {

	private String charset = HTTP.UTF_8;

	@Override
	public Loader<String> newInstance() {
		return new StringLoader();
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
	public void setProgressCallbackHandler(final ProgressCallbackHandler progressCallbackHandler) {

	}

	@Override
	public String load(final InputStream in) throws IOException {
		rawCache = IOUtils.loadBytesAndUncompress(in);
		return new String(rawCache, charset).trim();
	}

	@Override
	public String load(final URIRequest request) throws IOException {
		InputStream inputStream = null;
		String result = null;
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
