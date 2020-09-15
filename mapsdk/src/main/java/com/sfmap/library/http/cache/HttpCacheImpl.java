package com.sfmap.library.http.cache;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;

import com.sfmap.library.Callback;
import com.sfmap.library.http.HttpMethod;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.LruDiskCache;
import com.sfmap.library.http.utils.LruMemoryCache;
import com.sfmap.library.io.StorageFactory;
import com.sfmap.library.io.SQLiteMapper;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.util.DebugLog;
import com.sfmap.plugin.IMPluginManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 */
public class HttpCacheImpl implements HttpCache {

	private final CacheHitProxy cacheHitProxy;

	private static final int VERSION = 20120316;
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50;
	private static final int DEFAULT_DB_CACHE_COUNT = 200;
	private static final int MEM_CACHE_SIZE = 1024 * 1024 * 4;

	private LruDiskCache diskCache;
	private SQLiteMapper<HttpCacheEntry> mapper;
	private LruMemoryCache<String, Object> memoryCache;

	private static HttpCacheImpl instance;

	public static HttpCacheImpl getInstance() {
		if (instance == null) {
			instance = new HttpCacheImpl();
		}
		return instance;
	}

	@SuppressLint("NewApi")
	private HttpCacheImpl() {
		memoryCache = new LruMemoryCache<String, Object>(MEM_CACHE_SIZE) {
			@Override
			public Object get(String key) {
				Object result = super.get(key);
				if (result instanceof SizeableObject) {
					return ((SizeableObject) result).object;
				} else {
					return result;
				}
			}

			@Override
			protected int sizeOf(String key, Object value) {
				int result = 0;
				if (value != null) {
					if (value instanceof SizeableObject) {
						result = ((SizeableObject) value).size;
					} else {
						result = super.sizeOf(key, value);
					}
				}
				return result;
			}
		};
		mapper = StorageFactory.INSTANCE.getSQLiteStorage(HttpCacheEntry.class, IMPluginManager.getApplication());
		cacheHitProxy = new CacheHitProxyImpl(mapper);

		File extCache = null;
		Application application = IMPluginManager.getApplication();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			extCache = application.getExternalCacheDir();
		}
		if (extCache == null) {
			extCache = new File(Environment.getExternalStorageDirectory(),
				"/Android/data/" + application.getPackageName() + "/cache/");
		}
		File cacheDir = new File(extCache, "uio_http_cache");
		if (cacheDir.exists() || cacheDir.mkdirs()) {
			try {
				diskCache = LruDiskCache.open(cacheDir, VERSION, 1, DEFAULT_DISK_CACHE_SIZE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void evictAll(){
		getMemCacheInstance().evictAll();
	}

	@Override
	public boolean supportCache(URIRequest request) {
		RequestParams params = request.getParams();
		if (params != null) {
			if (params.getCachePolicy() == Callback.CachePolicyCallback.CachePolicy.NetworkOnly) {
				return false;
			}
		}
		return request.getMethod() == HttpMethod.GET;
	}

	@Override
	public boolean useCache(HttpCacheEntry cacheEntry, URIRequest request) {
		try {
			if (request != null) {
				int code = request.getResponseCode();
				if (code == 304) {
					return true;
				}
			}
			if (cacheEntry != null) {
				return cacheEntry.ttl > System.currentTimeMillis();
			}
		} catch (Throwable e) {
		}
		return false;
	}

	@Override
	public void addHeaders(HttpCacheEntry cacheEntry, URIRequest request) {
		RequestParams params = request.getParams();
		if (params == null) {
			params = new RequestParams();
			request.setParams(params);
		}
		if (cacheEntry.lastModified > 0) {
			params.addHeader("If-Modified-Since", toGMTString(new Date(cacheEntry.lastModified)));
		}
		if (cacheEntry.etag != null) {
			params.addHeader("If-None-Match", cacheEntry.etag);
		}
	}

	@Override
	public void saveCache(URIRequest request, byte[] rawCache, Object memCache) {
		HttpCacheEntry cacheEntry = this.getHttpCacheEntry(request);
		if (cacheEntry == null) {
			cacheEntry = new HttpCacheEntry();
		}
		GeoPoint point = cacheHitProxy.matchProxy(request);
		if (point != null) {
			cacheEntry.x = point.x;
			cacheEntry.y = point.y;
		}

		cacheEntry.key = cacheHitProxy.generateKey(request);
		cacheEntry.contentLength = (int) request.getContentLength();
		cacheEntry.contentType = request.getContentType();
		cacheEntry.ttl = request.getExpiration();
		cacheEntry.etag = request.getHeaderField("ETag");
		cacheEntry.lastModified = request.getLastModified();
		cacheEntry.lastAccess = System.currentTimeMillis();

		RequestParams params = request.getParams();
		if (params != null) {
			cacheEntry.charset = params.getCharset();
		}
		cacheEntry.responseBody = rawCache;

		if (rawCache != null && !(memCache instanceof Drawable) && !(memCache instanceof Bitmap)) {
			memoryCache.put(cacheEntry.key, new SizeableObject(rawCache.length, memCache));
		}
		if (cacheEntry.ttl <= 0) {
			cacheEntry.responseBody = null;
		}
		try {
			mapper.saveOrUpdate(cacheEntry);
		} catch (Throwable ex) {
			DebugLog.error(ex);
		}
		trimDbCache();
	}

	@Override
	public HttpCacheEntry getHttpCacheEntry(URIRequest request) {
		HttpCacheEntry result = mapper.getByKey("key", cacheHitProxy.generateKey(request));

		if (result == null) {
			GeoPoint point = cacheHitProxy.matchProxy(request);
			if (point != null) {
				result = cacheHitProxy.getBestMatchHttpCacheEntry(request, point);
			}
		}

		if (result != null) {
			result.hit++;
			result.lastAccess = System.currentTimeMillis();
			try {
				mapper.update(result);
			} catch (Throwable ignore) {
			}
		}

		return result;
	}

	@Override
	public Object getMemCache(URIRequest request) {
		String key = cacheHitProxy.generateKey(request);
		if (key != null) {
			// debug
			//if (DebugLog.isDebug()) {
			//	android.util.Log.d("MEM_CACHE", memoryCache.toString());
			//}
			return memoryCache.get(key);
		}
		return null;
	}

	@Override
	public InputStream getDiskCache(HttpCacheEntry cacheEntry) throws IOException {
		if (cacheEntry == null) {
			return null;
		}
		if (cacheEntry.responseBody != null && cacheEntry.responseBody.length > 0) {
			return new ByteArrayInputStream(cacheEntry.responseBody);
		}
		if (diskCache != null) {
			LruDiskCache.Snapshot snapshot = diskCache.get(cacheEntry.key);
			if (snapshot != null) {
				return snapshot.getInputStream(0);
			}
		}
		return null;
	}

	@Override
	public LruDiskCache.Editor getDiskCacheEditor(URIRequest request) throws IOException {
		if (diskCache != null) {
			String key = cacheHitProxy.generateKey(request);
			LruDiskCache.Snapshot snapshot = diskCache.get(key);
			if (snapshot == null) {
				return diskCache.edit(key);
			} else {
				diskCache.remove(key);
				return diskCache.edit(key);
			}
		}
		return null;
	}

	@Override
	public File getDiskCacheFile(HttpCacheEntry cacheEntry) throws IOException {
		if (diskCache != null) {
			return diskCache.getCacheFile(cacheEntry.key, 0);
		}
		return null;
	}

	@Override
	public void removeCache(HttpCacheEntry cacheEntry) throws IOException {
		if (diskCache != null) {
			diskCache.remove(cacheEntry.key);
		}
	}

	private static String toGMTString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, dd MMM y HH:mm:ss 'GMT'", Locale.US);
		TimeZone gmtZone = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(gmtZone);
		GregorianCalendar gc = new GregorianCalendar(gmtZone);
		gc.setTimeInMillis(date.getTime());
		return sdf.format(date);
	}

	private void trimDbCache() {
		try {
			long count = mapper.count(null, "id");
			if (count > DEFAULT_DB_CACHE_COUNT) {
				List<HttpCacheEntry> list = mapper.query("1=1 ORDER BY lastAccess ASC, hit ASC limit " + (count - DEFAULT_DB_CACHE_COUNT));
				for (HttpCacheEntry entry : list) {
					mapper.remove(entry.id);
					if (diskCache != null) {
						diskCache.remove(entry.key);
					}
				}
			}
		} catch (Throwable ignore) {
			DebugLog.debug(ignore.getMessage());
		}
	}

	public LruMemoryCache<String, Object> getMemCacheInstance() {
		return memoryCache;
	}

	private class SizeableObject {
		final int size;
		final Object object;

		public SizeableObject(int size, Object object) {
			this.size = size;
			this.object = object;
		}
	}
}
