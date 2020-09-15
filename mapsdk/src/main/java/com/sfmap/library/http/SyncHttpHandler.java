/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sfmap.library.http;

import android.text.TextUtils;

import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 同步网络请求的主要流程控制
 */
public class SyncHttpHandler {

    private URIRequest request;
    private final HttpRetryHandler retryHandler = new HttpRetryHandler();

    public SyncHttpHandler(String url, HttpMethod method, RequestParams params, ClassLoader callingClassLoader) throws IOException {
        this.request = new URIRequest(url, method, params, null, null, callingClassLoader);

        if (params != null) {
            this.retryHandler.setMaxRetryCount(params.getMaxRetryCount());
        }
    }

    public ResponseStream sendRequest() throws IOException {
        ResponseStream result = null;

        // 从网络获取数据
        boolean retry = true;
        int retriedCount = 0;
        Throwable exception2 = null;
        while (retry) {

            // 发送请求
            try {
                // 断点续下支持
                RequestParams params = request.getParams();
                if (params != null && params.isAutoResume()) {
                    String saveFilePath = params.getSaveFilePath();
                    if (!TextUtils.isEmpty(saveFilePath)) {
                        File file = new File(saveFilePath);
                        if (file.exists()) {
                            params.setHeader("RANGE", "bytes=" + file.length() + "-");
                        }
                    }
                }

                if(NetworkImpl.globalRequestCallback != null) {
                    NetworkImpl.globalRequestCallback.callback(null);
                }
                request.sendRequest();

                exception2 = null;
            } catch (Throwable e) {
                exception2 = e;
                retry = retryHandler.retryRequest(e, ++retriedCount, request);
                continue;
            }

            // 接收数据
            try {
                InputStream inputStream = request.getInputStream();
                if (inputStream != null) {
                    result = new ResponseStream(inputStream);
                }
                break;
            } catch (Throwable e) {
                exception2 = e;
                retry = retryHandler.retryRequest(e, ++retriedCount, request);
                continue;
            }
        }

        if (result == null && exception2 != null) {
            if (exception2 instanceof IOException) {
                throw (IOException) exception2;
            } else {
                throw new IOException(exception2.getMessage());
            }
        }

        return result;
    }
}
