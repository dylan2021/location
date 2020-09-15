package com.sfmap.tbt;

import android.content.Context;
import android.util.Log;

import com.sfmap.tbt.util.AppInfo;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthDecode {
	public static int authResult = -1;
	public static String authResultInfo = "";
	private static ProductInfoDecode productInfo;
//	private static String authServerURL = "http://apiinit.amap.com/v3/log/init";
	private static String authServerURL = "";
	//private static String e = null;

	private static boolean checkAuth(Context paramContext, ProductInfoDecode paramp,
									 boolean paramBoolean) {
		/*productInfo = paramp;
		String str = null;
		boolean bool = true;
		try {
			str = getAuthServerURL();

			HashMap localHashMap = new HashMap();
			localHashMap.put("Content-Type","application/x-www-form-urlencoded");
			localHashMap.put("Accept-Encoding", "gzip");
			localHashMap.put("Connection", "Keep-Alive");
			localHashMap.put("User-Agent", productInfo.b);
			if (paramBoolean) {
				localHashMap.put("X-INFO", CInfoDecode.initXInfo(paramContext, productInfo, null));
			} else {
				localHashMap.put("X-INFO", CInfoDecode.rsaInfo(paramContext, productInfo, null));
			}
			localHashMap.put("platinfo", String.format("platform=Android&sdkversion=%s&product=%s", new Object[] {productInfo.sdkversion, productInfo.product }));
			localHashMap.put("logversion", "2.0");

			BaseNetManagerDecode localau = BaseNetManagerDecode.getInstance();
			AuthRequest localr = new AuthRequest();
			localr.setProxy(ProxyUtilDecode.getProxy(paramContext));
			localr.setAuthHead(localHashMap);
			Map localMap = getDefalutHeadParms(paramContext);
			localr.setHttpHead(localMap);
			localr.setURL(str);

			byte[] arrayOfByte = localau.a(localr);
			bool = checkServerAuthResult(arrayOfByte);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "Auth", "getAuth");
			localThrowable.printStackTrace();
		}
		return bool;*/

		return true; //屏蔽掉网络鉴权
	}

	public static synchronized boolean verifyProductInfo(Context paramContext, ProductInfoDecode paramp) {
		return checkAuth(paramContext, paramp, false);
	}

	public static void setAuthServerURL(String serverURL) {
		authServerURL = serverURL;
	}

	private static String getAuthServerURL() {
		return authServerURL;
	}

	private static boolean checkServerAuthResult(byte[] paramArrayOfByte) {
		String str = null;
		try {
			if (paramArrayOfByte == null)
				return true;

			str = new String(paramArrayOfByte);
			JSONObject localJSONObject = new JSONObject(str);
			if (localJSONObject.has("status")) {
				int i = localJSONObject.getInt("status");
				if (i == 1)
					authResult = 1;
				else if (i == 0)
					authResult = 0;
			}
//			authResult = 1;//强制授权
			if (localJSONObject.has("info"))
				authResultInfo = localJSONObject.getString("info");

			if (authResult == 0) {
				Log.i("AuthFailure", authResultInfo);
			}



			return (authResult == 1);
		} catch (JSONException localJSONException) {
			BasicLogHandler.a(localJSONException, "Auth", "lData");
			localJSONException.printStackTrace();
		} catch (Exception localException) {
			BasicLogHandler.a(localException, "Auth", "lData");
			localException.printStackTrace();
		}
		return false;
	}

	private static Map<String, String> getDefalutHeadParms(Context paramContext) {
		HashMap localHashMap = new HashMap();
		try {
			localHashMap.put("resType", "json");
			localHashMap.put("encode", "UTF-8");

			String str1 = CInfoDecode.getTS();
			localHashMap.put("ts", str1);
			localHashMap.put("key", AppInfo.getSystemAk(paramContext));
			String str2 = "resType=json&encode=UTF-8&key=" + AppInfo.getSystemAk(paramContext);

			String str3 = Utils.sortParameter(str2);
			localHashMap.put("scode", CInfoDecode.Scode(paramContext, str1, str3));
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "Auth", "gParams");
			localThrowable.printStackTrace();
		}
		return localHashMap;
	}
}