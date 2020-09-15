package com.sfmap.route;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.sfmap.map.util.CatchExceptionUtil;
import com.sfmap.map.util.MapSharePreference;
import com.sfmap.map.util.MapSharePreference.SharePreferenceKeyEnum;
import com.sfmap.map.util.MapSharePreference.SharePreferenceName;
import com.sfmap.map.util.RoutePathHelper;
import com.sfmap.route.model.ItemKey;
import com.sfmap.route.model.POI;
import com.sfmap.route.model.RouteType;
import com.sfmap.route.util.RouteCalType;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

public class RouteUtil {

    public final static String MY_LOCATION_DES = "我的位置";

    private static final int ANIMATION_DURING = 300;

    /**
     * 给指定字符串，返回特殊字体大小的字符文本
     * @param smallstr
     * @param bigstr
     * @return
     */
    public static SpannableString getSpannableStr(String smallstr, String bigstr) {
        SpannableString sp = new SpannableString(smallstr + bigstr);
        sp.setSpan(new AbsoluteSizeSpan(12, true), 0, smallstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.argb(255, 254, 174, 174)), 0, smallstr.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(15, true), smallstr.length(),
                bigstr.length() + smallstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    public static TranslateAnimation createAnimation() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(ANIMATION_DURING);

        return animation;
    }

    public static String generateResultKey(int fromtoType, POI fromPoi, List<POI> midPOIs,
                                           POI toPOI, String method) {
        StringBuilder sbr = new StringBuilder();
        try {
            sbr.append(fromtoType);
            if (fromPoi.getName().equals(RouteUtil.MY_LOCATION_DES)) {
                sbr.append(fromPoi.getId()).append(getLonLatKey(fromPoi.getPoint().getLatitude(),
                        fromPoi.getPoint().getLongitude()));
            } else {
                sbr.append(fromPoi.getId()).append(fromPoi.getPoint().getLatitude())
                        .append(fromPoi.getPoint().getLongitude());
            }
            if (midPOIs != null && midPOIs.size() > 0) {
                for (POI poi : midPOIs) {
                    if (poi.getName().equals(RouteUtil.MY_LOCATION_DES)) {
                        sbr.append(poi.getId()).append(getLonLatKey(poi.getPoint().getLatitude(),
                                poi.getPoint().getLongitude()));
                    } else {
                        sbr.append(poi.getId()).append(poi.getPoint().getLatitude())
                                .append(poi.getPoint().getLongitude());
                    }
                }
            }
            if (toPOI.getName().equals(RouteUtil.MY_LOCATION_DES)) {
                sbr.append(toPOI.getId()).append(getLonLatKey(toPOI.getPoint().getLatitude(),
                        toPOI.getPoint().getLongitude()));
            } else {
                sbr.append(toPOI.getId()).append(toPOI.getPoint().getLatitude())
                        .append(toPOI.getPoint().getLongitude());
            }

            sbr.append(method);
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return ItemKey.createMD5(sbr.toString());
    }

    private static String getLonLatKey(double lat, double lon) {
        // 我的位置取经纬度后4位（精确到1米），1~10米内可认为我的位置没有变化
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.####");
        return df.format(lat) + "" + df.format(lon);
    }

    /**
     * 取最近一次路线规划方式
     * @return
     */
    public static RouteType getLastRouteType() {
        MapSharePreference mapsharePreference = new MapSharePreference(
                SharePreferenceName.user_route_method_info);
        int type = mapsharePreference.getIntValue(SharePreferenceKeyEnum.last_route_type,
                RouteType.CAR.getValue());
        switch (type) {
            case 0:
                return RouteType.BUS;
            case 1:
                return RouteType.CAR;
            case 2:
                return RouteType.ONFOOTBIKE;
            case 3:
                return RouteType.TRUCK;
            case 4:
                return RouteType.ONFOOT;
        }
        return RouteType.CAR;
    }

    /**
     * 保存最近一次路线规划方式
     *
     * @param type
     */
    public static void saveLastRouteType(RouteType type) {
        MapSharePreference mapsharePreference = new MapSharePreference(
                SharePreferenceName.user_route_method_info);
        mapsharePreference.putIntValue(SharePreferenceKeyEnum.last_route_type, type.getValue());
    }

    /**
     * 取用户最近一次选择的驾车偏好
     */
    public static String getCarUserMethod() {
//        MapSharePreference mapsharePreference = new MapSharePreference(
//                SharePreferenceName.user_route_method_info);
//        String method = mapsharePreference.getStringValue(SharePreferenceKeyEnum.car_method,
//                RouteCalType.CARROUTE_INDEX_4);
//        String method = RoutePathHelper.getCarUserMethod(MapApplication.getContext(), "|"+ RouteCalType.CARROUTE_INDEX_DEFAULT+"|");
        String method = "|"+ RouteCalType.CARROUTE_INDEX_DEFAULT+"|";
        return method;
    }

    /**
     * 保存用户最近一次选中的驾车偏好
     *
     * @param method
     */
    public static void putCarUserMethod(String method) {
        MapSharePreference mapsharePreference = new MapSharePreference(
                SharePreferenceName.user_route_method_info);
        mapsharePreference.putStringValue(SharePreferenceKeyEnum.car_method, method);
    }

    // 32 位 md5
    public static String createMD5(String str) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
        } catch (UnsupportedEncodingException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }

        if (messageDigest == null)
            return "";

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
}
