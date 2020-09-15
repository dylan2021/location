package com.sfmap.map.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sfmap.library.util.ToastHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public abstract class AbstractResponser {

    public static final String ERROR_NETWORK = "请检查网络后重试";
    public static final String PARSE_ERROR = "解析数据失败";
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_NETWORK_FAIL = -1;
    public static final int CODE_PARSER_FAIL = -2;

    public int errorCode = CODE_NETWORK_FAIL;
    public String errorMessage = ERROR_NETWORK;//在没有网络时提示
    public String version = "";
    public long timeStamp = 0l;
    public boolean result = false;
    public JSONObject mDataObject = null;
    private String noticeContent = null;
    private String noticeAction = null;

    public abstract void parser(final byte[] data)
            throws UnsupportedEncodingException, JSONException;

    protected JSONObject parseHeader(byte[] data) {
        if (data == null) {
            errorCode = CODE_NETWORK_FAIL;
            return mDataObject;
        }
        try {
            String mStrData = new String(data, "UTF-8");
            mDataObject = new JSONObject(mStrData);
            version = mDataObject.getString("version");
            result = mDataObject.getBoolean("result");
            errorCode = mDataObject.getInt("code");
            errorMessage = parserMessage(errorMessage, mDataObject);
            timeStamp = mDataObject.getLong("timestamp");
            final JSONArray noticeJA = mDataObject.optJSONArray("_notice_");
            if (noticeJA != null && noticeJA.length() > 0) {
                final JSONObject noticeJO = noticeJA.optJSONObject(0);
                final String style = noticeJO.optString("style", "");
                noticeContent = noticeJO.optString("content");
                noticeAction = noticeJO.optString("action");
                if (style.equalsIgnoreCase("0")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            noticeContent = null;
                            noticeAction = null;
                        }
                    }, 1000);
                } else if (style.equalsIgnoreCase("-1")) {
                } else {
                    noticeContent = null;
                    noticeAction = null;
                }
            }
        } catch (Exception e) {
            result = false;
            errorCode = CODE_PARSER_FAIL;
            errorMessage = PARSE_ERROR;
        }
        errorMessage = getErrorDesc(errorCode);
        return mDataObject;
    }

    public String parserMessage(String errorMessage, JSONObject js) {
        String message = js.optString("message");
        if (!TextUtils.isEmpty(message)) {
            return message;
        }
        return errorMessage;
    }

    public abstract String getErrorDesc(int errorCode);

}

