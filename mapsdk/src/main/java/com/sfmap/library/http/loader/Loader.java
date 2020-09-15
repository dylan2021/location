package com.sfmap.library.http.loader;



import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public interface Loader<T> {

    Loader<T> newInstance();

    void setParams(final RequestParams params);

    void setProgressCallbackHandler(final ProgressCallbackHandler progressCallbackHandler);

    T load(final InputStream in) throws IOException;

    T load(final URIRequest request) throws IOException;

    byte[] getRawCache();
}
