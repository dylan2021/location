package com.sfmap.library.http.entity;


import com.sfmap.library.http.ProgressCallbackHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public interface HttpEntity {

    void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler);

    String getContentType();

    void writeTo(final OutputStream out) throws IOException;
}
