package com.sfmap.library.http.entity;


import com.sfmap.library.http.ProgressCallbackHandler;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *
 */
public class BodyParamsEntity implements HttpEntity {

    private byte[] content;

    public BodyParamsEntity(List<NameValuePair> params, String charset) throws UnsupportedEncodingException {
        String paramsStr = URLEncodedUtils.format(params, charset);
        this.content = paramsStr.getBytes(charset);
    }

    @Override
    public void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler) {

    }

    @Override
    public String getContentType() {
        return URLEncodedUtils.CONTENT_TYPE;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.content);
        out.flush();
    }
}
