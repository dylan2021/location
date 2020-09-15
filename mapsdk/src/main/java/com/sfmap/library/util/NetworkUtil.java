package com.sfmap.library.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络工具类
 */
public class NetworkUtil {

    private static final String NETWORK_WIFI = "wifi";
    private static final String NETWORK_MOBILE = "mobile";

    /** 不知道的类型 */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /** 2g. */
    public static final int NETWORK_CLASS_2_G = 1;
    /** 3g */
    public static final int NETWORK_CLASS_3_G = 2;
    /** 4g */
    public static final int NETWORK_CLASS_4_G = 3;
    /** wifi */
    public static final int NETWORK_CLASS_WIFI = 4;

    /**
     * 获取网络状态
     * @param context   上下文
     * @return 1.2g 2.3g 3.4g 4.wifi
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo == null)
            return NETWORK_CLASS_UNKNOWN;
        if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_CLASS_WIFI;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return getMobileNetType(context);
        }
        return NETWORK_CLASS_UNKNOWN;
    }

    /**
     * 获取运营商名称（有sim卡）
     *
     * @param context   上下文
     * @return          返回运营商名称
     */
    public String getOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            return telephonyManager.getNetworkOperator();// String
        } else {
            return "";
        }
    }

    /**
     * 获取网络类型 wifi or cellular
     *
     * @param context   上下文
     * @return          返回网络类型
     */
    public String getNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        int activNetType = (activeNetInfo == null) ? -1 : activeNetInfo
                .getType();// 0: mobile 1:wifi

        String netType = NETWORK_WIFI;
        if (activNetType == 0) {
            netType = NETWORK_MOBILE;
        }
        return netType;

    }

    /**
     * 获取网络连接状态
     *
     * @param context           上下文
     * @return WI-FI or Mobile
     */
    public static String getNetworkType(Context context) {
        String netType = "";
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoWifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoMobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != infoWifi && infoWifi.isConnected()) {
            netType = NETWORK_WIFI;
        }
        if (null != infoMobile && infoMobile.isConnected()) {
            netType = NETWORK_MOBILE;
        }
        return netType;
    }

    /**
     * 获取网络连接状态
     *
     * @param context       上下文
     * @return              true网络连接否者反之
     */
    public static boolean isNetworkConnected(Context context) {
        int netConnect = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info != null && info.isConnected()) {
            netConnect = 1;
        }
        if (infoM != null && infoM.isConnected()) {
            netConnect = 2;
        }
        return netConnect > 0 ? true : false;
    }

    /**
     * 返回手机类型
     * @param context   上下文
     * @return          手机类型 eg:3g,4g
     */
    public static int getMobileNetType(Context context) {
        TelephonyManager telephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyMgr.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    /**
     * 获取手机网络连接状态：0.无连接,1.wifi,2.2G,3.3G,4.其他
     * @param context   上下文
     * @return          1:wifi,2:2G,3:3G,4:其他
     */
    public static int getCheckNetWork(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) context
                .getSystemService(context.TELEPHONY_SERVICE); // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        if(null == mConnectivity || null == mTelephony) {
            return 0;
        }
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null) { // || !mConnectivity.getBackgroundDataSetting()
            return 0;
        }
        // 判断网络连接类型，只有在2G/3G/wifi里进行一些数据更新。
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else if (netType == ConnectivityManager.TYPE_MOBILE
                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
                && !mTelephony.isNetworkRoaming()) {
            return 3;
        } else if (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS
                || netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
                || netSubtype == TelephonyManager.NETWORK_TYPE_EDGE) {
            return 2;
        } else {
            return 4;
        }
    }
}
