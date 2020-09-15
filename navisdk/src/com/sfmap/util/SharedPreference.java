package com.sfmap.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sfmap.SfNaviSDK;
//import com.sfmap.location.MapLocation;


public class SharedPreference {
    private static final String PREFERRED_KEY_NET_TRACK_INTERVAL_UPDATE_TIME = "preferredNetTrackIntervalUpdateTime";
    private static final String PREFERRED_KEY_NET_TRACK_INTERVAL = "preferredNetTrackInterval";
    private static final String PREFERRED_KEY_ONLINE_CONFIG_URL = "preferredOnlineConfigUrl";
    private static final String PREFERRED_KEY_GPS_MOTIONAL_INTERVAL = "preferredMotionalGpsInterval";
    private static final String PREFERRED_KEY_GPS_STATIONARY_INTERVAL = "preferredStationaryGpsInterval";

    private static final String USER_PREFERENCE_NAME = "preferred";
    private static final String LOC_PREFERENCE_NAME = "loc";
    private static final String UPLOAD_PREFERENCE_NAME = "upload";


    public static String getPreferString(String key) {
        SharedPreferences sp = getSharedPreferences(LOC_PREFERENCE_NAME);
        return sp.getString(key, "");
    }

    public static void setPreferString(String key, String value) {
        SharedPreferences sp = getSharedPreferences(LOC_PREFERENCE_NAME);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setPreferBoolean(String key, boolean value) {
        SharedPreferences settings = getSharedPreferences(LOC_PREFERENCE_NAME);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPreferBoolean(String key) {
        SharedPreferences sp = getSharedPreferences(LOC_PREFERENCE_NAME);
        return sp.getBoolean(key, false);
    }

    public static int getPreferInteger(String key) {
        SharedPreferences sp = getSharedPreferences(LOC_PREFERENCE_NAME);
        return sp.getInt(key, 0);
    }

    public static void setPreferInteger(String key, int value) {
        SharedPreferences settings = getSharedPreferences(LOC_PREFERENCE_NAME);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean hasPreferString(String key) {
        SharedPreferences sp = getSharedPreferences(LOC_PREFERENCE_NAME);
        return sp.contains(key);
    }

    public static void saveUserName(String username, String branchname) {
//        setPreferString(KEY_USER_NAME, username);
//        setPreferString(KEY_BRANCH_NAME, branchname);
    }

    public static void saveDeviceId(String imei) {
//        setPreferString(KEY_IMEI, imei);
    }

//    static void saveLocationInfo(MapLocation location) {
//        setPreferString("longitude", String.valueOf(location.getLongitude()));
//        setPreferString("latitude", String.valueOf(location.getLatitude()));
//        setPreferString("accuracy", String.valueOf(location.getAccuracy()));
//        setPreferString("altitude", String.valueOf(location.getAltitude()));
//        setPreferString("time", String.valueOf(location.getTime()));
//        setPreferString("type", String.valueOf(location.getLocationType()));
//    }

    public static boolean hasLocationInfo() {
        return hasPreferString("longitude") && hasPreferString("latitude") && hasPreferString("accuracy")
                && hasPreferString("time") && hasPreferString("localtime") && hasPreferString("type");
    }

    public static int getUploadInteger(String key) {
        SharedPreferences sp = getSharedPreferences(UPLOAD_PREFERENCE_NAME);
        return sp.getInt(key, 0);
    }

    public static void setUploadInteger(String key, int value) {
        SharedPreferences settings = getSharedPreferences(UPLOAD_PREFERENCE_NAME);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static boolean hasUploadString(String key) {
        SharedPreferences sp = getSharedPreferences(UPLOAD_PREFERENCE_NAME);
        return sp.contains(key);
    }

    public static String getUploadString(String key) {
        SharedPreferences sp = getSharedPreferences(UPLOAD_PREFERENCE_NAME);
        return sp.getString(key, "");
    }

    public static void setUploadString(String key, String value) {
        SharedPreferences sp = getSharedPreferences(UPLOAD_PREFERENCE_NAME);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean hasPreferredNetTractInterval() {
        SharedPreferences sp = getSharedPreferences(USER_PREFERENCE_NAME);
        long lastUpdateTime = sp.getLong(PREFERRED_KEY_NET_TRACK_INTERVAL_UPDATE_TIME, 0);
        return System.currentTimeMillis() - lastUpdateTime <= 3 * 24 * 60 * 60 * 1000;
    }

    public static void setPreferredNetLocationInterval(int netTrackInterval) {
        CommUtil.d(SfNaviSDK.mContext, "SharedPreference",
                String.format("Set preferred net track interval to %s ms", netTrackInterval));
        SharedPreferences sp = getSharedPreferences(USER_PREFERENCE_NAME);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREFERRED_KEY_NET_TRACK_INTERVAL_UPDATE_TIME, System.currentTimeMillis());
        editor.putInt(PREFERRED_KEY_NET_TRACK_INTERVAL, netTrackInterval);
        editor.apply();
    }

    public static int getPreferredNetLocationInterval() {
        SharedPreferences sp = getSharedPreferences(USER_PREFERENCE_NAME);
        return sp.getInt(PREFERRED_KEY_NET_TRACK_INTERVAL, 0);
    }

    public static void resetPreferredNetLocationInterval() {
        getSharedPreferences(USER_PREFERENCE_NAME)
                .edit()
                .remove(PREFERRED_KEY_NET_TRACK_INTERVAL)
                .apply();
    }


    public static void setPreferredConfigUrl(String configUrl) {
        SharedPreferences sp = getSharedPreferences(USER_PREFERENCE_NAME);
        sp.edit()
                .putString(PREFERRED_KEY_ONLINE_CONFIG_URL, configUrl)
                .apply();
    }

    public static String getPreferredConfigUrl() {
        SharedPreferences sp = getSharedPreferences(USER_PREFERENCE_NAME);
        return sp.getString(PREFERRED_KEY_ONLINE_CONFIG_URL, null);
    }

    public static void resetPreferredConfigUrl() {
        getSharedPreferences(USER_PREFERENCE_NAME)
                .edit()
                .remove(PREFERRED_KEY_ONLINE_CONFIG_URL)
                .apply();
    }

    public static int getPreferredGpsMotionalInterval() {
        SharedPreferences sp = getSharedPreferences(USER_PREFERENCE_NAME);
        return sp.getInt(PREFERRED_KEY_GPS_MOTIONAL_INTERVAL, 0);
    }

    public static void setPreferredGpsMotionalInterval(int interval) {
        getSharedPreferences(USER_PREFERENCE_NAME)
                .edit()
                .putInt(PREFERRED_KEY_GPS_MOTIONAL_INTERVAL, interval)
                .apply();
    }

    public static void resetPreferredGpsMotionalInterval() {
        getSharedPreferences(USER_PREFERENCE_NAME)
                .edit()
                .remove(PREFERRED_KEY_GPS_MOTIONAL_INTERVAL)
                .apply();
    }

    public static int getPreferredGpsStationaryInterval() {
        return getSharedPreferences(USER_PREFERENCE_NAME)
                .getInt(PREFERRED_KEY_GPS_STATIONARY_INTERVAL, 0);
    }

    public static void setPreferredGpsStationaryInterval(int interval) {
        getSharedPreferences(USER_PREFERENCE_NAME)
                .edit()
                .putInt(PREFERRED_KEY_GPS_STATIONARY_INTERVAL, interval)
                .apply();
    }

    public static void resetPreferredGpsStationaryInterval() {
        getSharedPreferences(USER_PREFERENCE_NAME)
                .edit()
                .remove(PREFERRED_KEY_GPS_STATIONARY_INTERVAL)
                .apply();
    }


    private static SharedPreferences getSharedPreferences(String name) {
        return SfNaviSDK.mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    }



}
