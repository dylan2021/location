package com.sfmap.library.http.url;

import android.text.TextUtils;


import com.sfmap.library.http.HttpMethod;
import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.entity.HttpEntity;
import com.sfmap.library.http.loader.EmptyLoader;
import com.sfmap.library.http.loader.Loader;
import com.sfmap.library.http.loader.LoaderFactory;
import com.sfmap.library.http.params.ParamsProvider;
import com.sfmap.library.http.params.ParamsProviderFactory;
import com.sfmap.library.http.utils.OtherUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 网络请求对象.
 * 实现参数构造, 发送请求和下载数据.
 */
public class URIRequest {
    private final HttpMethod method;
    private RequestParams params;
    private ProgressCallbackHandler progressCallbackHandler;

    private final String uri;
    private URL requestURL;
    private ParamsProvider paramsProvider;
    private final Loader<?> loader;

    private HttpURLConnection connection;
    private ClassLoader callingClassLoader;
    private IOException initException;

    public URIRequest(
            String uri,
            HttpMethod method,
            RequestParams params,
            Class<?> rawResultCls,
            ProgressCallbackHandler progressCallbackHandler,
            ClassLoader callingClassLoader) throws IOException {

        this.uri = uri;
        this.method = method;
        this.params = params;
        this.progressCallbackHandler = progressCallbackHandler;
        this.callingClassLoader = callingClassLoader;
        this.paramsProvider = ParamsProviderFactory.getParamsProvider(uri, params);
        if (rawResultCls != null) {
            this.loader = LoaderFactory.getLoader(rawResultCls, params);
            this.loader.setProgressCallbackHandler(progressCallbackHandler);
        } else {
            this.loader = new EmptyLoader();
        }

        initURL(uri);
    }

    public URIRequest(
            String uri,
            HttpMethod method,
            RequestParams params,
            Class<?> rawResultCls,
            ProgressCallbackHandler progressCallbackHandler) throws IOException {
        this(uri, method, params, rawResultCls, progressCallbackHandler, null);
    }

    private void initURL(String url) throws IOException {
        if (paramsProvider != null) {
            // 初始化 query string 等工作
            try {
                requestURL = paramsProvider.buildURL(url, this);
            } catch (URISyntaxException e) {
                initException = new MalformedURLException(url);
                throw initException;
            }
        }
    }

    private boolean useProxy = true;

    public void error(Throwable th) {
        if (th != null) {
            useProxy = false;
        }
    }

    public void sendRequest() throws IOException {
        if (initException != null) throw initException;
        if (requestURL == null) return;
        // fix: 重新发送请求时可能出现connection未被重新创建
        connection = null;
        OtherUtils.trustAllHttpsURLConnection();
        // 建立连接
        if (useProxy && params != null) {
            Proxy currentProxy = params.getProxy();
            if (currentProxy != null) {
                connection = (HttpURLConnection) requestURL.openConnection(currentProxy);
            }
        }
        if (connection == null) {
            connection = (HttpURLConnection) requestURL.openConnection();
        }

        // 初始化连接参数
        if (params != null) {
            connection.setConnectTimeout(params.getConnectTimeout());
            connection.setReadTimeout(params.getConnectTimeout());
        }
        connection.setInstanceFollowRedirects(true);
        boolean onlyGet = method == HttpMethod.GET;
        if (!onlyGet) {
            connection.setDoOutput(true);
            connection.setRequestMethod(method.toString());
        }
        if (paramsProvider != null) {
            // 设置 header 和默认参数等工作
            paramsProvider.initConnection(connection, params);
        }

        // 上传数据
        if (!onlyGet && params != null) {
            HttpEntity entity = params.getBodyEntity();
            if (entity != null) {
                String contentType = entity.getContentType();
                if (!TextUtils.isEmpty(contentType)) {
                    connection.setRequestProperty("Content-Type", contentType);
                }
                entity.setProgressCallbackHandler(progressCallbackHandler);
                entity.writeTo(connection.getOutputStream());
            }
        }
    }

    public Object loadResult() throws IOException {
        if (paramsProvider != null && connection != null) {
            // 获取返回的header等工作
            paramsProvider.syncHeaders(connection);
        }
        return loader.load(this);
    }

    public void close() {
        if (connection != null) {
            try {
                InputStream in = this.getInputStream();
                if (in != null) {
                    in.close();
                }
            } catch (Throwable e) {
            }
            try {
                OutputStream out = connection.getOutputStream();
                if (out != null) {
                    out.close();
                }
            } catch (Throwable e) {
            }
        }
    }

    public URL getRequestURL() {
        return requestURL;
    }

    private InputStream inputStream = null;

    public InputStream getInputStream() throws IOException {

        if (inputStream == null) {
            if (connection != null) {
                return connection.getInputStream();
            } else if (callingClassLoader != null && uri.startsWith("assets/")) {
                return callingClassLoader.getResourceAsStream(uri);
            } else {
                File file = new File(uri);
                if (file.exists()) {
                    return new FileInputStream(file);
                }
            }
        }

        return inputStream;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestParams getParams() {
        return params;
    }

    public void setParams(RequestParams params) {
        this.params = params;
    }

    public Loader<?> getLoader() {
        return loader;
    }

    public ProgressCallbackHandler getProgressCallbackHandler() {
        return progressCallbackHandler;
    }

    @Override
    public String toString() {
        return requestURL == null ? uri : requestURL.toString();
    }

    public long getContentLength() {
        int result = 0;
        if (connection != null) {
            result = connection.getContentLength();
            if (result < 1) {
                try {
                    result = this.getInputStream().available();
                } catch (IOException e) {
                }
            }
        } else {
            try {
                result = this.getInputStream().available();
            } catch (IOException e) {
            }
        }
        return result;
    }

    public int getResponseCode() throws IOException {
        if (connection != null) {
            return connection.getResponseCode();
        } else {
            if (this.getInputStream() != null) {
                return 200;
            } else {
                return 404;
            }
        }
    }

    public String getResponseFileName() {
        if (connection != null) {
            String disposition = connection.getHeaderField("Content-Disposition");
            if (!TextUtils.isEmpty(disposition)) {
                int startIndex = disposition.indexOf("filename=");
                if (startIndex > 0) {
                    startIndex += 9; // "filename=".length()
                    int endIndex = disposition.indexOf(";", startIndex);
                    if (endIndex < 0) {
                        endIndex = disposition.length();
                    }
                    return disposition.substring(startIndex, endIndex);
                }
            }
        }
        return null;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public boolean isSupportRange() {
        if (connection != null) {
            String ranges = connection.getHeaderField("Accept-Ranges");
            if (ranges != null) {
                return ranges.contains("bytes");
            }
            ranges = connection.getHeaderField("Content-Range");
            if (ranges != null) {
                return ranges.contains("bytes");
            }
        }
        return false;
    }

    public long getExpiration() {
        if (connection != null) {
            return connection.getExpiration();
        } else {
            return -1;
        }
    }

    public String getContentType() {
        if (connection != null) {
            return connection.getContentType();
        } else {
            return "";
        }
    }

    public String getHeaderField(String key) {
        if (connection != null) {
            return connection.getHeaderField(key);
        } else {
            return "";
        }
    }

    public long getLastModified() {
        long lastModified = 0;
        if (connection != null) {
            lastModified = connection.getLastModified();
        }
        if (lastModified == 0) {
            lastModified = System.currentTimeMillis();
        }
        return lastModified;
    }

}
