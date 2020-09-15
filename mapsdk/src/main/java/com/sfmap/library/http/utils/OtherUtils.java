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

package com.sfmap.library.http.utils;

import android.text.TextUtils;


import com.sfmap.library.util.DebugLog;

import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 */
public class OtherUtils {
	private OtherUtils() {
	}



	private static SSLSocketFactory sslSocketFactory;

	public static void trustAllHttpsURLConnection() {
		// Create a trust manager that does not validate certificate chains
		if (sslSocketFactory == null) {
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs,
				                               String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs,
				                               String authType) {
				}
			}};
			try {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, trustAllCerts, null);
				sslSocketFactory = sslContext.getSocketFactory();
			} catch (Throwable e) {
				DebugLog.error(e.getMessage(), e);
			}
		}

		if (sslSocketFactory != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
			HttpsURLConnection
				.setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		}
	}

	/**
	 * 判断选择的通讯录号码是否是手机号码
	 *
	 * @param num
	 * @return
	 */
	public static boolean isMobileContact(String num) {
		if (TextUtils.isEmpty(num))
			return false;
		if (num.length() < 11) {
			return false;
		} else {
			num = num.replace(" ", "");
			num = num.replace("-", "");
			StringBuilder sb = new StringBuilder();

			for (int i = num.length() - 1; i >= 0; i--) {
				try {
					sb.append(num.charAt(i));
				} catch (Exception e) {
					return false;
				}
			}
			if (sb.length() == 11) {
				Pattern pattern = Pattern.compile("^\\d{10}(1)$");
				return pattern.matcher(sb.toString()).matches();
			} else {
				return false;
			}
		}
	}

	/**
	 * 去除电话号码的空格和“-”,但并不做是否是电话号码的判断
	 *
	 * @param num
	 * @return
	 */
	public static String trimMobile(String num) {
		num = num.replace(" ", "");
		num = num.replace("-", "");
		return num;
	}

	/**
	 * 这里客户端只做最基本的判断，我设定为以“1”开头的11位号码
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {
		if (TextUtils.isEmpty(mobiles))
			return false;
		Pattern p = Pattern.compile("^(1)\\d{10}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	public static boolean isVerfyNum(String verify) {
		if (TextUtils.isEmpty(verify))
			return false;
		Pattern p = Pattern.compile("^\\d{4}$");
		Matcher m = p.matcher(verify);

		return m.matches();
	}

	private static Pattern httpPattern = Pattern.compile("(http|https):\\/\\/([\\w.]+\\/?)\\S*");

	public static boolean isHttpProtocol(String url) {
		Matcher matcher = httpPattern.matcher(url);
		return matcher.matches();
	}

}
