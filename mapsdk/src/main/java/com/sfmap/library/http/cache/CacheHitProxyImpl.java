package com.sfmap.library.http.cache;

import android.text.TextUtils;


import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.http.url.URIBuilder;
import com.sfmap.library.io.SQLiteMapper;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.util.DebugLog;

import org.apache.http.NameValuePair;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 */
public class CacheHitProxyImpl implements CacheHitProxy {

	private final static HashSet<String> IGNORE_KEYS = new HashSet<String>();
	private final static HashSet<String> LON_KEYS = new HashSet<String>(3);
	private final static HashSet<String> LAT_KEYS = new HashSet<String>(3);
	private final static HashSet<String> LOCATION_KEYS = new HashSet<String>(2);
	private final static HashSet<String> LON_LAT_KEYS = new HashSet<String>(8);

	static {

		LON_KEYS.add("x");
		LON_KEYS.add("lon");
		LON_KEYS.add("longitude");

		LAT_KEYS.add("y");
		LAT_KEYS.add("lat");
		LAT_KEYS.add("latitude");

		LOCATION_KEYS.add("location");
		LOCATION_KEYS.add("user_loc");

		LON_LAT_KEYS.addAll(LON_KEYS);
		LON_LAT_KEYS.addAll(LAT_KEYS);
		LON_LAT_KEYS.addAll(LOCATION_KEYS);

		IGNORE_KEYS.addAll(LON_LAT_KEYS);
		IGNORE_KEYS.add("sign");
		IGNORE_KEYS.add("div");
		IGNORE_KEYS.add("dip");
		IGNORE_KEYS.add("dic");
		IGNORE_KEYS.add("diu");
		IGNORE_KEYS.add("diu2");
		IGNORE_KEYS.add("diu3");
		IGNORE_KEYS.add("cifa");
		IGNORE_KEYS.add("sa");
		IGNORE_KEYS.add("session");
		IGNORE_KEYS.add("stepid");
		IGNORE_KEYS.add("spm");
		IGNORE_KEYS.add("in");
		IGNORE_KEYS.add("ent");
	}

	private SQLiteMapper<HttpCacheEntry> mapper;

	public CacheHitProxyImpl(SQLiteMapper<HttpCacheEntry> mapper) {
		this.mapper = mapper;
	}

	@Override
	public GeoPoint matchProxy(URIRequest request) {
		try {
			RequestParams params = request.getParams();
			if (params == null) {
				return null;
			}
			HashMap<String, String> queryParams = params.getQueryStringParams();
			if (queryParams == null) {
				return null;
			}
			String longitude = null;
			String latitude = null;
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				String name = entry.getKey();
				if (LON_LAT_KEYS.contains(name)) {
					if (LOCATION_KEYS.contains(name)) {
						String user_loc = entry.getValue();
						String[] lon_lat = user_loc.split("%2C");
						if (lon_lat.length == 2) {
							longitude = lon_lat[0];
							latitude = lon_lat[1];
						}
						break;
					} else if (LON_KEYS.contains(name)) {
						longitude = entry.getValue();
						if (latitude != null) {
							break;
						}
					} else if (LAT_KEYS.contains(name)) {
						latitude = entry.getValue();
						if (longitude != null) {
							break;
						}
					}
				}
			}

			if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
				GeoPoint point = new GeoPoint();
				point.setLonLat(Double.parseDouble(longitude), Double.parseDouble(latitude));
				return point;
			}

			return null;
		} catch (Throwable ignore) {
			DebugLog.error(ignore.getMessage(), ignore);
			return null;
		}
	}


	@Override
	public String generateKey(URIRequest request) {
		RequestParams params = request.getParams();
		if (params != null) {
			String cacheKey = params.getCacheKey();
			if (!TextUtils.isEmpty(cacheKey)) {
				return cacheKey;
			}
		}

		URL url = request.getRequestURL();
		if (url == null) {
			return request.toString();
		}

		HashMap<String, String> queryParams = null;
		try {
			URIBuilder builder = new URIBuilder(url.toURI());
			List<NameValuePair> urlParams = builder.getQueryParams();
			if (urlParams != null && urlParams.size() > 0) {
				queryParams = new HashMap<String, String>();
				for (NameValuePair np : urlParams) {
					queryParams.put(np.getName(), np.getValue());
				}
			}
		} catch (Throwable ex) {
			return request.toString();
		}

		if (params != null && params.getQueryStringParams() != null) {
			if (queryParams == null) {
				queryParams = new HashMap<String, String>();
			}
			queryParams.putAll(params.getQueryStringParams());
		}

		String key = request.toString();
		int queryStartIndex = key.indexOf('?');
		if (queryStartIndex > 0) {
			key = key.substring(0, queryStartIndex + 1);
		}

		if (queryParams == null) {
			return key;
		}

		try {
			String longitude = null;
			String latitude = null;
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				String name = entry.getKey();
				if (IGNORE_KEYS.contains(name)) {
					if ((longitude == null || latitude == null) && LON_LAT_KEYS.contains(name)) {
						if (LOCATION_KEYS.contains(name)) {
							String user_loc = entry.getValue();
							String[] lon_lat = user_loc.split("%2C");
							if (lon_lat.length == 2) {
								longitude = lon_lat[0];
								latitude = lon_lat[1];
							}
							break;
						} else if (LON_KEYS.contains(name)) {
							longitude = entry.getValue();
						} else if (LAT_KEYS.contains(name)) {
							latitude = entry.getValue();
						}
					}
				} else {
					key += "&" + name + "=" + entry.getValue();
				}
			}

			// 减小经纬度参数的精度
			// 保留3位小数
			if (longitude != null && latitude != null) {
				int index = longitude.indexOf('.') + 4;
				if (index > 4 && index < longitude.length()) {
					longitude = longitude.substring(0, index);
				}
				index = latitude.indexOf('.') + 4;
				if (index > 4 && index < latitude.length()) {
					latitude = latitude.substring(0, index);
				}
				key += "&lon=" + longitude;
				key += "&lat=" + latitude;
			}

		} catch (Throwable ex) {
			DebugLog.warn(ex);
		}

		return key;
	}

	@Override
	public HttpCacheEntry getBestMatchHttpCacheEntry(URIRequest request, GeoPoint point) {

		GeoPoint maxPoint = new GeoPoint();
		maxPoint.setLonLat(point.getLongitude() + PRECISION, point.getLatitude() + PRECISION);
		GeoPoint minPoint = new GeoPoint();
		minPoint.setLonLat(point.getLongitude() - PRECISION, point.getLatitude() - PRECISION);

		List<HttpCacheEntry> cacheEntryList = mapper.query(
			"key=? AND x>? AND x<? AND y>? AND y<?",
			this.generateKey(request),
			minPoint.x,
			maxPoint.x,
			minPoint.y,
			maxPoint.y);

		if (cacheEntryList != null && cacheEntryList.size() > 0) {
			Collections.sort(cacheEntryList, new DistanceComparator(point));
			return cacheEntryList.get(0);
		}

		return null;
	}

	private final static double PRECISION = 10f / (60 * 60 * 30.87); // 缓存命中精度10米

	private class DistanceComparator implements Comparator<HttpCacheEntry> {

		final GeoPoint point;

		DistanceComparator(GeoPoint point) {
			this.point = point;
		}

		@Override
		public int compare(HttpCacheEntry lhs, HttpCacheEntry rhs) {
			double lDistance = Math.pow(lhs.x - point.x, 2) + Math.pow(lhs.y - point.y, 2);
			double rDistance = Math.pow(rhs.x - point.x, 2) + Math.pow(rhs.y - point.y, 2);
			return (int) (lDistance - rDistance);
		}
	}

}
