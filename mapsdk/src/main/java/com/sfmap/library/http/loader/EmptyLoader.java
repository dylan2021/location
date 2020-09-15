package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class EmptyLoader implements Loader<Object> {

    @Override
    public Loader<Object> newInstance() {
	return new EmptyLoader();
    }

    @Override
    public void setParams(RequestParams params) {

    }

    @Override
    public void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler) {

    }

    @Override
    public Object load(InputStream in) throws IOException {
	return new Object();
    }

    @Override
    public Object load(URIRequest request) throws IOException {
	return new Object();
    }

	@Override
	public byte[] getRawCache() {
		return null;
	}
}
