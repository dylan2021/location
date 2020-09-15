package com.sfmap.library.http.entity;

import com.sfmap.library.http.ProgressCallbackHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 */
public class InputStreamEntity implements HttpEntity {

    private InputStream content;

    public InputStreamEntity(InputStream inputStream) throws UnsupportedEncodingException {
        this.content = inputStream;
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
	    int len = 0;
	    byte[] buffer = new byte[1024];
	    try {
		    while ((len = content.read(buffer)) != -1) {
			    out.write(buffer, 0, len);
		    }
		    out.flush();
	    } catch (Throwable ex){
	    } finally {
		    try {
			    content.close();
		    } catch (Throwable ignored) {
		    }
		}
    }
}
