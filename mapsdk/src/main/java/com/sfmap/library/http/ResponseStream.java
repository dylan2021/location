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



import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.IOUtils;

import org.apache.http.protocol.HTTP;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 */
public class ResponseStream extends InputStream {

	private InputStream baseStream;
	private URIRequest request;
	private String defaultCharset = HTTP.UTF_8;

	public ResponseStream(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("inputStream may not be null");
		}

		this.baseStream = inputStream;
	}

	private Object _directResult;

	public ResponseStream(Object result) {
		if (result == null) {
			throw new IllegalArgumentException("result may not be null");
		}

		_directResult = result;
	}

	public InputStream getBaseStream() {
		return baseStream;
	}

	public String readString() throws IOException {
		if (_directResult != null) return (String) _directResult;
		if (baseStream == null) return null;
		try {
			String result;
			String charset = null;
			if (request != null) {
				RequestParams params = request.getParams();
				if (params != null) {
					charset = params.getCharset();
				}
			}
			if (charset == null) {
				charset = defaultCharset;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(baseStream, charset));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result = sb.toString().trim();
			_directResult = result;
			return result;
		} finally {
			IOUtils.closeQuietly(baseStream);
		}
	}

	public void readFile(String savePath) throws IOException {
		if (_directResult != null) return;
		if (baseStream == null) return;
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(savePath));
			BufferedInputStream ins = new BufferedInputStream(baseStream);
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = ins.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(baseStream);
		}
	}

	@Override
	public int read() throws IOException {
		if (baseStream == null) return -1;
		return baseStream.read();
	}

	@Override
	public int available() throws IOException {
		if (baseStream == null) return 0;
		return baseStream.available();
	}

	@Override
	public void close() throws IOException {
		if (baseStream == null) return;
		baseStream.close();
	}

	@Override
	public void mark(int readLimit) {
		if (baseStream == null) return;
		baseStream.mark(readLimit);
	}

	@Override
	public boolean markSupported() {
		if (baseStream == null) return false;
		return baseStream.markSupported();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		if (baseStream == null) return -1;
		return baseStream.read(buffer);
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		if (baseStream == null) return -1;
		return baseStream.read(buffer, offset, length);
	}

	@Override
	public synchronized void reset() throws IOException {
		if (baseStream == null) return;
		baseStream.reset();
	}

	@Override
	public long skip(long byteCount) throws IOException {
		if (baseStream == null) return 0;
		return baseStream.skip(byteCount);
	}

	public byte[] readAllBytes() throws IOException {
		if (baseStream == null) return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[512];
		while ((len = baseStream.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		byte[] result = out.toByteArray();
		out.close();
		out = null;
		return result;
	}

	public URIRequest getRequest() {
		return request;
	}

	public void setRequest(URIRequest request) {
		this.request = request;
	}
}
