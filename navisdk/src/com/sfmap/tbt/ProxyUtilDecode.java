package com.sfmap.tbt;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class ProxyUtilDecode {
	public static java.net.Proxy getProxy(Context paramContext) {
		java.net.Proxy localProxy = null;
		try {
			if (Build.VERSION.SDK_INT >= 11) {
				URI localURI = new URI(AppInfoUtilDecode.proxyURL);
				localProxy = a(paramContext, localURI);
//			} else {
//				localProxy = b(paramContext);
			}
		} catch (URISyntaxException localURISyntaxException) {
			BasicLogHandler.a(localURISyntaxException, "ProxyUtil", "getProxy");
			localURISyntaxException.printStackTrace();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "ProxyUtil", "getProxy");
			localThrowable.printStackTrace();
		}

		return localProxy;
	}

	private static java.net.Proxy b(Context paramContext) {
		Object localObject1 = null;
		int i = -1;
		if (DeviceInfoDecode.j(paramContext) == 0) {
			String str1;
			String str2;
			int k;
			Uri localUri = Uri.parse("content://telephony/carriers/preferapn");
			ContentResolver localContentResolver = paramContext
					.getContentResolver();
			Cursor localCursor = null;
			try {
				localCursor = localContentResolver.query(localUri, null, null,
						null, null);
				if ((localCursor != null) && (localCursor.moveToFirst())) {
					int j = localCursor.getColumnIndex("apn");
					str1 = localCursor.getString(j);
					if (str1 != null) {
						str1 = str1.toLowerCase(Locale.US);
					}

					if ((str1 != null) && (str1.contains("ctwap"))) {
						str2 = a();
						i = b();

						k = 0;
						if ((!(TextUtils.isEmpty(str2)))
								&& (!(str2.equals("null")))) {
							k = 1;
							localObject1 = str2;
						}

						if (k == 0)
							localObject1 = "10.0.0.200";

						if (i == -1)
							i = 80;
					} else if ((str1 != null) && (str1.contains("wap"))) {
						str2 = a();
						i = b();

						k = 0;
						if ((!(TextUtils.isEmpty(str2)))
								&& (!(str2.equals("null")))) {
							k = 1;
							localObject1 = str2;
						}

						if (k == 0)
							localObject1 = "10.0.0.172";

						if (i == -1)
							i = 80;
					}
				}
			} catch (SecurityException localSecurityException) {
				BasicLogHandler.a(localSecurityException, "ProxyUtil", "getHostProxy");
				str1 = DeviceInfoDecode.l(paramContext);
				if (str1 != null) {
					str2 = str1.toLowerCase(Locale.US);

					k = 0;
					String str3 = a();
					i = b();
					if (str2.indexOf("ctwap") != -1) {
						if ((!(TextUtils.isEmpty(str3)))
								&& (!(str3.equals("null")))) {
							k = 1;
							localObject1 = str3;
						}

						if (k == 0)
							localObject1 = "10.0.0.200";

						if (i == -1)
							i = 80;
					} else if (str2.indexOf("wap") != -1) {
						if ((!(TextUtils.isEmpty(str3)))
								&& (!(str3.equals("null")))) {
							k = 1;
							localObject1 = str3;
						}

						if (k == 0)
							localObject1 = "10.0.0.200";

						i = 80;
					}
				}
			} catch (Exception localException) {
				BasicLogHandler.a(localException, "ProxyUtil", "getHostProxy1");

				localException.printStackTrace();
			} finally {
				if (localCursor != null)
					try {
						localCursor.close();
					} catch (Throwable localThrowable) {
						BasicLogHandler.a(localThrowable, "ProxyUtil", "getHostProxy2");

						localThrowable.printStackTrace();
					}
			}

			if (a((String) localObject1, i)) {
				java.net.Proxy localProxy = new java.net.Proxy(
						java.net.Proxy.Type.HTTP,
						InetSocketAddress.createUnresolved(
								(String) localObject1, i));

				return localProxy;
			}
		}

		return ((java.net.Proxy) null);
	}

	private static boolean a(String paramString, int paramInt) {
		return ((paramString != null) && (paramString.length() > 0) && (paramInt != -1));
	}

	private static String a() {
		String str = null;
		try {
			str = android.net.Proxy.getDefaultHost();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();

			BasicLogHandler.a(localThrowable, "ProxyUtil", "getDefHost");
			str = null;
		}
		if (str == null)
			str = "null";

		return str;
	}

	private static java.net.Proxy a(Context paramContext, URI paramURI) {
		if (DeviceInfoDecode.j(paramContext) == 0)
			try {
				ProxySelector localProxySelector = ProxySelector.getDefault();
				java.net.Proxy localProxy = null;
				List localList = null;
				localList = localProxySelector.select(paramURI);
				if ((localList != null) && (!(localList.isEmpty()))) {
					localProxy = (java.net.Proxy) localList.get(0);
					if ((localProxy == null)
							|| (localProxy.type() == java.net.Proxy.Type.DIRECT)) {
						localProxy = null;
					}
				}

				return localProxy;
			} catch (Exception localException) {
				BasicLogHandler.a(localException, "ProxyUtil", "getProxySelectorCfg");

				localException.printStackTrace();
			}

		return null;
	}

	private static int b() {
		int i = -1;
		try {
			i = android.net.Proxy.getDefaultPort();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "ProxyUtil", "getDefPort");
			localThrowable.printStackTrace();
		}
		return i;
	}
}