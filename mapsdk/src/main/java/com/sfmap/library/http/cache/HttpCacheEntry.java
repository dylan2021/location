package com.sfmap.library.http.cache;

import com.sfmap.library.io.SQLiteMapper.SQLiteEntry;
import com.sfmap.library.io.SQLiteMapper.SQLiteProperty;

import java.util.Arrays;

@SQLiteEntry(version = 5, name = "HTTP_CACHE")
public class HttpCacheEntry {
	/**
	 * 主键
	 */
	@SQLiteProperty("PRIMARY KEY AUTOINCREMENT")
	public long id;
	@SQLiteProperty("UNIQUE NOT NULL")
	public String key;
	@SQLiteProperty
	public int x;
	@SQLiteProperty
	public int y;
	/**
	 * 请求头
	 */
	@SQLiteProperty
	public String requestHeaders;
	/**
	 * 响应头
	 */
	@SQLiteProperty
	public String responseHeaders;
	/**
	 * 请求头
	 */
	@SQLiteProperty
	public byte[] responseBody;
	/**
	 * 内容类型
	 */
	@SQLiteProperty
	public String contentType;
	/**
	 * 字符集
	 */
	@SQLiteProperty
	public String charset;
	/**
	 * 内容长度
	 */
	@SQLiteProperty
	public Integer contentLength;
	/**
	 * time to alive:max-age
	 */
	@SQLiteProperty
	public long ttl;//time to alive:max-age
	/**
	 * 此值一定不能为空，如果后段返回为空，用当前时间代替
	 */
	@SQLiteProperty
	public long lastModified;
	@SQLiteProperty
	public long lastAccess;
	@SQLiteProperty
	public int hit;
	@SQLiteProperty
	public String etag;

	// 临时变量, 不存入数据库.
	public boolean isMemCache = false;

	@Override
	public String toString() {
		return "HttpCacheEntry{" +
				"id=" + id +
				", key='" + key + '\'' +
				", x=" + x +
				", y=" + y +
				", requestHeaders='" + requestHeaders + '\'' +
				", responseHeaders='" + responseHeaders + '\'' +
				", responseBody=" + Arrays.toString(responseBody) +
				", contentType='" + contentType + '\'' +
				", charset='" + charset + '\'' +
				", contentLength=" + contentLength +
				", ttl=" + ttl +
				", lastModified=" + lastModified +
				", lastAccess=" + lastAccess +
				", hit=" + hit +
				", etag='" + etag + '\'' +
				", isMemCache=" + isMemCache +
				'}';
	}
}

