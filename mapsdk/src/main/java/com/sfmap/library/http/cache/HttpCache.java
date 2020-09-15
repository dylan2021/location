package com.sfmap.library.http.cache;


import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.utils.LruDiskCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public interface HttpCache {

    boolean supportCache(URIRequest request);

    boolean useCache(HttpCacheEntry cacheEntry, URIRequest request);

    void addHeaders(HttpCacheEntry cacheEntry, URIRequest request);

    void saveCache(URIRequest request, byte[] rawCache, Object memCache);

    HttpCacheEntry getHttpCacheEntry(URIRequest request);

    Object getMemCache(URIRequest request);

    File getDiskCacheFile(HttpCacheEntry cacheEntry) throws IOException;

    InputStream getDiskCache(HttpCacheEntry cacheEntry) throws IOException;

    LruDiskCache.Editor getDiskCacheEditor(URIRequest request) throws IOException;

    void removeCache(HttpCacheEntry cacheEntry) throws IOException;

    void  evictAll();
}
