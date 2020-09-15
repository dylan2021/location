package com.sfmap.library.http.loader;

import android.text.TextUtils;


import com.sfmap.library.http.ProgressCallbackHandler;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.cache.HttpCache;
import com.sfmap.library.http.cache.HttpCacheImpl;
import com.sfmap.library.http.utils.IOUtils;
import com.sfmap.library.http.utils.LruDiskCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class FileLoader implements Loader<File> {

	private String saveFilePath;
	private boolean isAutoResume;
	private boolean isAutoRename;
	private long contentLength;
	private String responseFileName;
	private long ttl;

	private URIRequest request;
	private static final HttpCache sCache = HttpCacheImpl.getInstance();

	@Override
	public Loader<File> newInstance() {
		return new FileLoader();
	}

	@Override
	public void setParams(final RequestParams params) {
		if (params != null) {
			saveFilePath = params.getSaveFilePath();
			isAutoResume = params.isAutoResume();
			isAutoRename = params.isAutoRename();
		}
	}

	private ProgressCallbackHandler callBackHandler;

	@Override
	public void setProgressCallbackHandler(final ProgressCallbackHandler progressCallbackHandler) {
		this.callBackHandler = progressCallbackHandler;
	}

	@Override
	public File load(final InputStream in) throws IOException {

		if (saveFilePath == null) { // 保存路径为空, 存入磁盘缓存.
			return load2DiskCache(in);
		}

		File targetFile = new File(saveFilePath);

		if (!targetFile.exists()) {
			File dir = targetFile.getParentFile();
			if (dir.exists() || dir.mkdirs()) {
				targetFile.createNewFile();
			}
		}

		long current = 0;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			FileOutputStream fileOutputStream = null;
			if (isAutoResume) {
				current = targetFile.length();
				fileOutputStream = new FileOutputStream(saveFilePath, true);
			} else {
				fileOutputStream = new FileOutputStream(saveFilePath);
			}
			long total = contentLength + current;
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(fileOutputStream);

			if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
				return targetFile;
			}

			byte[] tmp = new byte[4096];
			int len;
			while ((len = bis.read(tmp)) != -1) {
				bos.write(tmp, 0, len);
				current += len;
				if (callBackHandler != null) {
					if (!callBackHandler.updateProgress(total, current, false)) {
						return targetFile;
					}
				}
			}
			bos.flush();
			if (callBackHandler != null) {
				callBackHandler.updateProgress(total, current, true);
			}
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bos);
		}

		if (targetFile.exists() && isAutoRename && !TextUtils.isEmpty(responseFileName)) {
			File newFile = new File(targetFile.getParent(), responseFileName);
			while (newFile.exists()) {
				newFile = new File(targetFile.getParent(), System.currentTimeMillis() + responseFileName);
			}
			return targetFile.renameTo(newFile) ? newFile : targetFile;
		} else {
			return targetFile;
		}
	}

	private File load2DiskCache(final InputStream in) throws IOException {

		long current = 0;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		LruDiskCache.Editor editor = null;
		try {
			editor = sCache.getDiskCacheEditor(request);
			if (editor == null) return null;

			File tempFile = editor.getDirtyFile(0);
			if (tempFile != null) {
				current = tempFile.length();
			}
			editor.setEntryExpiryTimestamp(ttl);
			long total = isAutoResume ? contentLength + current : contentLength;
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(editor.newOutputStream(0, isAutoResume));

			if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
				editor.abort();
				return null;
			}

			byte[] tmp = new byte[4096];
			int len;
			while ((len = bis.read(tmp)) != -1) {
				bos.write(tmp, 0, len);
				current += len;
				if (callBackHandler != null) {
					if (!callBackHandler.updateProgress(total, current, false)) {
						editor.abort();
						return null;
					}
				}
			}
			bos.flush();
			editor.commit();
			if (callBackHandler != null) {
				callBackHandler.updateProgress(total, current, true);
			}
			return editor.getCacheFile(0);
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bos);
		}
	}

	@Override
	public File load(final URIRequest request) throws IOException {
		this.request = request;
		InputStream inputStream = null;
		File result = null;
		try {
			contentLength = request.getContentLength();
			int code = request.getResponseCode();
			if (code == 400 || code == 416) {
				if (saveFilePath != null) {
					result = new File(saveFilePath);
				} else {
					LruDiskCache.Editor editor = sCache.getDiskCacheEditor(request);
					if (editor != null) {
						result = editor.getDirtyFile(0);
						if (result.exists()) {
							editor.commit();
						}
						result = editor.getCacheFile(0);
					}
				}
				if (result != null && result.exists()) {
					return result;
				}
			}
			if (isAutoRename) {
				responseFileName = request.getResponseFileName();
			}
			if (isAutoResume) {
				isAutoResume = request.isSupportRange();
			}
			ttl = getExpiry(request, request.getParams());
			inputStream = request.getInputStream();
			result = this.load(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return result;
	}

	@Override
	public byte[] getRawCache() {
		return null;
	}

	private static long getExpiry(final URIRequest request, RequestParams params) {
		/*long ttl = request.getExpiration();
		if (ttl < System.currentTimeMillis()) {
			ttl = Integer.MAX_VALUE;
		}*/
		return Integer.MAX_VALUE;//ttl;
	}
}
