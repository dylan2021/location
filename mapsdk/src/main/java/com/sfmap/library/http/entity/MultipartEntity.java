package com.sfmap.library.http.entity;


import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.utils.KeyValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

/**
 *
 */
public class MultipartEntity implements HttpEntity {
    private static byte[] BOUNDARY_PREFIX_BYTES = "--------7da3d81520810".getBytes();
    private static byte[] END_BYTES = "\r\n".getBytes();
    private static byte[] TWO_DASHES_BYTES = "--".getBytes();
    private byte[] boundaryPostfixBytes;
    private String contentType;
    private String charset;

    private List<KeyValuePair> multipartParams;
    private long total = 0;
    private long current = 0;

    public MultipartEntity(List<KeyValuePair> multipartParams, String charset) {
        this.multipartParams = multipartParams;
        this.charset = charset;
        generateContentType();

        for (KeyValuePair kv : multipartParams) {
            Object value = kv.value;
            if (value instanceof File) {
                total += ((File) value).length();
            } else if (value instanceof InputStream) {
                try {
                    total += ((InputStream) value).available();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (value instanceof byte[]) {
                total += ((byte[]) value).length;
            } else {
                String str = String.valueOf(value);
                total += str.getBytes().length;
            }
        }
    }

    private ProgressCallbackHandler callBackHandler;

    @Override
    public void setProgressCallbackHandler(ProgressCallbackHandler progressCallbackHandler) {
        this.callBackHandler = progressCallbackHandler;
    }

    private void generateContentType() {
        String boundaryPostfix = Double.toHexString(Math.random() * 0xFFFF);
        boundaryPostfixBytes = boundaryPostfix.getBytes();
        contentType = "multipart/form-data; boundary=" + new String(BOUNDARY_PREFIX_BYTES) + boundaryPostfix;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {

        if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
            return;
        }

        for (KeyValuePair kv : multipartParams) {
            writeEntry(out, charset, kv.key, kv.value, boundaryPostfixBytes);
        }
        writeLine(out, TWO_DASHES_BYTES, BOUNDARY_PREFIX_BYTES, boundaryPostfixBytes, TWO_DASHES_BYTES);
        out.flush();

        if (callBackHandler != null) {
            callBackHandler.updateProgress(total, total, true);
        }
    }

    private void writeEntry(OutputStream to, String charset, String k,
                            Object v, byte[] boundaryPostfixBytes) throws IOException {
        writeLine(to, TWO_DASHES_BYTES, BOUNDARY_PREFIX_BYTES,
                boundaryPostfixBytes);
        if (v instanceof File) {
            File file = (File) v;
            String filename = file.getName();
            String contentType = HttpURLConnection.guessContentTypeFromName(filename);
            if (null == contentType) {
                contentType = "application/octet-stream";
            }
            contentType = contentType.replaceFirst("\\/jpg$", "/jpeg");
            writeLine(to, ("Content-Disposition: form-data; name=\"" + k + "\"; filename=\"" + filename + "\"").getBytes());
            writeLine(to, ("Content-Type: " + contentType).getBytes(), END_BYTES);
            writeStreamAndCloseIn(to, new FileInputStream(file));
        } else {
            writeLine(to, ("Content-Disposition: form-data; name=\"" + k + "\"").getBytes(), END_BYTES);
            if (v instanceof InputStream) {
                writeStreamAndCloseIn(to, (InputStream) v);
            } else {
                byte[] vbuffer;
                if (v instanceof byte[]) {
                    vbuffer = (byte[]) v;
                } else {
                    vbuffer = String.valueOf(v).getBytes(charset);
                }
                writeLine(to, vbuffer);
                current += vbuffer.length;
                if (callBackHandler != null) {
                    callBackHandler.updateProgress(total, current, false);
                }
            }
        }
    }

    private void writeLine(OutputStream out, byte[]... bs) throws IOException {
        for (byte[] b : bs) {
            out.write(b);
        }
        out.write(END_BYTES);
    }

    private void writeStreamAndCloseIn(OutputStream out, InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
            current += len;
            if (callBackHandler != null && !callBackHandler.updateProgress(total, current, false)) {
                return;
            }
        }
        in.close();
        out.write(END_BYTES);
    }
}
