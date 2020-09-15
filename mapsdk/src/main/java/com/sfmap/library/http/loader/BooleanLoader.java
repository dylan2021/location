package com.sfmap.library.http.loader;

import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class BooleanLoader implements Loader<Boolean> {
    @Override
    public Loader<Boolean> newInstance() {
        return new BooleanLoader();
    }

    @Override
    public void setParams(final RequestParams params) {

    }

    @Override
    public void setProgressCallbackHandler(final ProgressCallbackHandler progressCallbackHandler) {

    }

    @Override
    public Boolean load(final InputStream in) throws IOException {
        return false;
    }

    @Override
    public Boolean load(final URIRequest request) throws IOException {
        return request.getResponseCode() < 300;
    }

	@Override
	public byte[] getRawCache() {
		return null;
	}
}
