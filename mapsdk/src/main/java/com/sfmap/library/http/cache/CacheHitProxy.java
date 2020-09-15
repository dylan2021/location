package com.sfmap.library.http.cache;

import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.model.GeoPoint;

/**
 *
 */
public interface CacheHitProxy {

    GeoPoint matchProxy(URIRequest request);

    String generateKey(URIRequest request);

    HttpCacheEntry getBestMatchHttpCacheEntry(URIRequest request, GeoPoint point);
}
