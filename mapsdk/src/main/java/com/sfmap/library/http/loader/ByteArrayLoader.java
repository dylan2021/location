package com.sfmap.library.http.loader;


import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class ByteArrayLoader implements Loader<byte[]> {

	private byte[] rawCache;

	@Override
	public Loader<byte[]> newInstance() {
		return new ByteArrayLoader();
	}

	@Override
	public void setParams(final RequestParams params) {
	}

	@Override
	public void setProgressCallbackHandler(final ProgressCallbackHandler progressCallbackHandler) {

	}

	@Override
	public byte[] load(final InputStream in) throws IOException {
		return rawCache = IOUtils.loadBytesAndUncompress(in);
	}

	@Override
	public byte[] load(final URIRequest request) throws IOException {
		InputStream inputStream = null;
		byte[] result = null;
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
