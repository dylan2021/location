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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.sfmap.library.CookieStore;
import com.sfmap.library.util.DebugLog;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A CookieStore impl, it's save cookie to SharedPreferences.
 *
 */
public class PreferencesCookieStore implements CookieStore {

	private static final String COOKIE_PREFS = "CookiePrefsFile";
	private static final String COOKIE_NAME_STORE = "names";
	private static final String COOKIE_NAME_PREFIX = "cookie_";

	private final ConcurrentHashMap<String, Cookie> cookies;
	private final SharedPreferences cookiePrefs;

	private static PreferencesCookieStore instance;

	private PreferencesCookieStore(Context context) {
		cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
		cookies = new ConcurrentHashMap<String, Cookie>();

		// Load any previously stored cookies into the store
		String storedCookieNames = cookiePrefs.getString(COOKIE_NAME_STORE, null);
		if (storedCookieNames != null) {
			String[] cookieNames = TextUtils.split(storedCookieNames, ",");
			for (String name : cookieNames) {
				String cookieStr = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
				if (cookieStr != null) {
					List<Cookie> cookieList = parseCookies(cookieStr);
					if (cookieList != null && cookieList.size() > 0) {
						cookies.put(name, cookieList.get(0));
					}
				}
			}

			// Clear out expired cookies
			clearExpired();
		}
	}

	public static synchronized PreferencesCookieStore getInstance(Context context) {
		if (instance == null) {
			instance = new PreferencesCookieStore(context);
		}
		return instance;
	}

	@Override
	public void addSetCookie(String setCookie) {
		List<Cookie> cookies = parseCookies(setCookie);
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				addCookie(cookie);
			}
		}
	}

	@Override
	public void addCookie(Cookie cookie) {
		String name = cookie.getName();

		// Save cookie into local store, or remove if expired
		if (!cookie.isExpired()) {
			cookies.put(name, cookie);
		} else {
			cookies.remove(name);
		}

		// Save cookie into persistent store
		SharedPreferences.Editor editor = cookiePrefs.edit();
		editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
		editor.putString(COOKIE_NAME_PREFIX + name, cookie.toString());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	@Override
	public void clear() {
		// Clear cookies from persistent store
		SharedPreferences.Editor editor = cookiePrefs.edit();
		for (String name : cookies.keySet()) {
			editor.remove(COOKIE_NAME_PREFIX + name);
		}
		editor.remove(COOKIE_NAME_STORE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			editor.apply();
		} else {
			editor.commit();
		}

		// Clear cookies from local store
		cookies.clear();
	}

	@Override
	public boolean clearExpired() {
		boolean clearedAny = false;
		SharedPreferences.Editor editor = cookiePrefs.edit();

		Iterator<Map.Entry<String, Cookie>> iterator = cookies.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Cookie> entry = iterator.next();
			String name = entry.getKey();
			Cookie cookie = entry.getValue();
			if (cookie.isExpired()) {

				DebugLog.debug("cookie expired: " + name + "=" + cookie.getValue());

				// Remove the cookie by name
				cookies.remove(name);

				// Clear cookies from persistent store
				editor.remove(COOKIE_NAME_PREFIX + name);

				// We've cleared at least one
				clearedAny = true;
			}
		}

		// Update names in persistent store
		if (clearedAny) {
			editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			} else {
				editor.commit();
			}
		}

		return clearedAny;
	}

	@Override
	public List<Cookie> getCookies() {
		clearExpired();
		return new ArrayList<Cookie>(cookies.values());
	}

	@Override
	public Cookie getCookie(String name) {
		clearExpired();
		return cookies.get(name);
	}

	@Override
	public String getCookie() {
		clearExpired();
		String cookieStr = "";
		List<Cookie> cookies = this.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookieStr += cookie.toElementString();
			}
		}
		DebugLog.debug("cookie: " + cookieStr);
		return cookieStr;
	}

	private static List<Cookie> parseCookies(String setCookie) {
		List<Cookie> result = new ArrayList<Cookie>();
		Date expiryDate = null;
		String domain = null;
		String path = null;
		if (!TextUtils.isEmpty(setCookie)) {
			String[] pairs = setCookie.split(";");
			if (pairs != null && pairs.length > 0) {
				for (String pair : pairs) {
					if (pair.contains("=")) {
						String[] nameValue = pair.split("=");
						if (nameValue.length == 2) {
							String name = nameValue[0].trim();
							String value = nameValue[1].trim();
							if (name.equalsIgnoreCase("expires")) {
								try {
									value = value.replace("-", " ");
									expiryDate = Cookie.DATE_FORMAT.parse(value);
								} catch (ParseException e) {
									DebugLog.debug(e.getMessage());
								}
							} else if (name.equalsIgnoreCase("domain")) {
								domain = value;
							} else if (name.equalsIgnoreCase("path")) {
								path = value;
							} else if (name.equalsIgnoreCase("Max-Age")) {
							} else {
								Cookie cookie = new Cookie();
								cookie.setName(name);
								cookie.setValue(value);
								result.add(cookie);
							}
						}
					}
				}

				if (expiryDate != null || domain != null || path != null) {
					for (Cookie cookie : result) {
						cookie.setExpiryDate(expiryDate);
						cookie.setDomain(domain);
						cookie.setPath(path);
					}
				}
			}
		}
		return result;
	}
}