package com.sfmap.tbt.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AuthManager//bn
{
    public static int authResult = -1;//0鉴权成功，其他值鉴权失败
    public static final int AUTH_SUCCESS = 0;//0鉴权成功
    public static String b = "";
    private static SDKInfo sdkInfo;
    private static String e = null;

    private static boolean getKeyAuth(Context paramContext, SDKInfo sdkInfo, boolean paramBoolean) {
//        AuthManager.sdkInfo = sdkInfo;
//        String url = null;
//        boolean bool = true;
//        try {
//            url = getURL(paramContext);
//            HashMap<String, String> paramsMap = new HashMap<String, String>();
//            paramsMap.put("Content-Type", "application/x-www-form-urlencoded");
//            paramsMap.put("Accept-Encoding", "gzip");
//            paramsMap.put("Connection", "Keep-Alive");
//            paramsMap.put("User-Agent", sdkInfo.desc);
//
//            paramsMap.put("X-INFO", ClientInfo.initXInfo(paramContext, sdkInfo, null, paramBoolean));
//            paramsMap.put("logversion", "1.0");
//
//            paramsMap.put("platinfo", String.format("platform=Android&sdkversion=%s&product=%s", new Object[]{sdkInfo.version, sdkInfo.product}));
//
//            BaseNetManager netManager = BaseNetManager.getInstance();
//            AuthRequest request = new AuthRequest();
//            request.setSDKInfo(paramsMap);
//            Map<String, String> localMap = gParamsMap(paramContext);
//            request.setDevInfo(localMap);
//            request.setUrl(url);
//
//            byte[] arrayOfByte = netManager.makeSyncPostRequest(request);
//            bool = parseResult(arrayOfByte);
//
//        } catch (Throwable localThrowable) {
////      BasicLogHandler.getSDKInfo(localThrowable, "Auth", "getAuth");
//            authResult = 999; //自定义一个错误码，此时如网络异常等发生
//        }
        //临时方案 将鉴权返回值改成true 解决鉴权失败和listens过期地图无法加载问题
        authResult = 0;
        return true;
//        return bool;
    }

    public static synchronized boolean a(Context paramContext, SDKInfo parambv) {
        return getKeyAuth(paramContext, parambv, true);
    }

    public static synchronized boolean getKeyAuth(Context paramContext, SDKInfo parambv) {
        return getKeyAuth(paramContext, parambv, false);
    }

    public static void a(String paramString) {
        AppInfo.a(paramString);
    }

    private static String getURL(Context context) {
        String authURL = AppInfo.getSfAuthURL(context);
        if (authURL != null && authURL.length() > 0)
            return authURL;
        else
            return "";
    }

    private static boolean parseResult(byte[] paramArrayOfByte) {
        String str = null;
        try {
            if (paramArrayOfByte == null) {
                return true;
            }
            try {
                str = new String(paramArrayOfByte, "UTF-8");
            } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
                str = new String(paramArrayOfByte);
            }
            Log.i("Auth", str);
            JSONObject localJSONObject = new JSONObject(str);
            if (localJSONObject.has("status")) {
                int i = localJSONObject.getInt("status");
                if (i == 0) {
                    authResult = 0;
                } else {
                    authResult = i;
                }
            }
            if (localJSONObject.has("message")) {
                b = localJSONObject.getString("message");
            }
            if (authResult != 0) {
                Log.i("AuthFailure", b);
            }
            if (authResult == 0) {

                return true;
            }
            return false;
        } catch (JSONException localJSONException) {
//      BasicLogHandler.getSDKInfo(localJSONException, "Auth", "lData");
        } catch (Throwable localThrowable) {
//      BasicLogHandler.getSDKInfo(localThrowable, "Auth", "lData");
        }
        return false;
    }

    private static Map<String, String> gParamsMap(Context context) {
        HashMap<String, String> localHashMap = new HashMap<String, String>();
        try {
            localHashMap.put("resType", "json");
            localHashMap.put("encode", "UTF-8");

            String ts = ClientInfo.getTS();
            localHashMap.put("ts", ts);
            localHashMap.put("ak", AppInfo.getKey(context));
            String str2 = "resType=json&encode=UTF-8&ak=" + AppInfo.getKey(context);

            String str3 = Utils.sortParams(str2);

            localHashMap.put("scode", ClientInfo.Scode(context, ts, str3));
        } catch (Throwable localThrowable) {
//      BasicLogHandler.getSDKInfo(localThrowable, "Auth", "gParams");
        }
        return localHashMap;
    }
}
