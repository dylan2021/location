package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class IntegerLoader implements Loader<Integer> {
	@Override
	public Loader<Integer> newInstance() {
		return new IntegerLoader();
	}

	@Override
	public void setParams(RequestParams params) {

	}

	@Override
	public void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler) {

	}

	@Override
	public Integer load(InputStream in) throws IOException {
		return -1;
	}

	@Override
	public Integer load(URIRequest request) throws IOException {
		return request.getResponseCode();
	}

	@Override
	public byte[] getRawCache() {
		return null;
	}
}
