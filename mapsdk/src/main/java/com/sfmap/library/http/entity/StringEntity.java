package com.sfmap.library.http.entity;


import com.sfmap.library.http.ProgressCallbackHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 */
public class StringEntity implements HttpEntity {

    private byte[] content;

    public StringEntity(String str, String charset) throws UnsupportedEncodingException {
        this.content = str.getBytes(charset);
    }

    @Override
    public void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler) {

    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(content);
        out.flush();
    }
}
