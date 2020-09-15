package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class InputStreamLoader implements Loader<InputStream> {

    @Override
    public Loader<InputStream> newInstance() {
        return new InputStreamLoader();
    }

    @Override
    public void setParams(final RequestParams params) {
    }

    @Override
    public void setProgressCallbackHandler(final ProgressCallbackHandler progressCallbackHandler) {
    }

    @Override
    public InputStream load(final InputStream in) throws IOException {
        return in;
    }

    @Override
    public InputStream load(final URIRequest request) throws IOException {
        return request.getInputStream();
    }

	@Override
	public byte[] getRawCache() {
		return null;
	}
}
