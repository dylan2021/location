package com.sfmap.map.util;


import android.app.Activity;
import android.location.Location;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.model.PointD;
import com.sfmap.library.util.Projection;
import com.sfmap.map.util.CatchExceptionUtil;
import com.sfmap.map.util.MapSharePreference;
import com.sfmap.navi.R;
import com.sfmap.route.model.NavigationPath;
import com.sfmap.route.model.NavigationSection;
import com.sfmap.api.maps.model.LatLng;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 导航工具类
 */
public class Tools {
    public static String TAG = "Tools";
    /**
     * 适配文本大小
     *
     * @param activity
     * @param strFrom1
     * @param strTo1
     * @param strFrom2
     * @param size1
     * @param size2
     * @return
     */
    public static SpannableString getSpanableSpan(Activity activity, String strFrom1,
                                                  String strTo1, String strFrom2, int size1, int size2) {
        if (TextUtils.isEmpty(strFrom1) || TextUtils.isEmpty(strTo1)
                || TextUtils.isEmpty(strFrom2))
            return null;

        SpannableString wordtoSpan = new SpannableString(strFrom1 + strTo1
                + strFrom2);

        int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
        int start = 0;
        int end = strFrom1.length();
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size1), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(0xFF002456), start, end,
                flag);

        start = end;
        end += strTo1.length();
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size2), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(0xFFFF0000), start, end,
                flag);

        start = end;
        end += strFrom2.length();

        wordtoSpan.setSpan(new AbsoluteSizeSpan(size1), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(0xFF002456), start, end,
                flag);
        return wordtoSpan;
    }

    /**
     * 根据Key值返回对应的Int变量
     *
     * @param key
     * @param defValue
     * @return
     */
    public static int getValueFromKey(MapSharePreference.SharePreferenceKeyEnum key, int defValue) {
        MapSharePreference ishowSetSharedPreferences = new MapSharePreference(MapSharePreference.SharePreferenceName.SharedPreferences);
        return ishowSetSharedPreferences.getIntValue(key, defValue);
    }

    /**
     * 获取key
     *
     * @param key
     * @return
     */
    public static boolean getValueFromKey(MapSharePreference.SharePreferenceKeyEnum key, boolean defaultValue) {
        MapSharePreference ishowSetSharedPreferences = new MapSharePreference(MapSharePreference.SharePreferenceName.SharedPreferences);
        return ishowSetSharedPreferences.getBooleanValue(key, defaultValue);
    }

    /**
     * 返回指示箭头数据
     *
     * @param carPath
     * @param focus_station_index
     * @return
     */
    public static List<LatLng> getNaviArrowData(NavigationPath carPath,
                                                int focus_station_index) {

        try {
            // --focus_station_index;
            --focus_station_index;

            if (focus_station_index < 0
                    || focus_station_index >= carPath.sections.length) {
                return null;
            }
            List<LatLng> naviArrowStack = new ArrayList<LatLng>();
            NavigationSection naviSection = carPath.sections[focus_station_index];
            if (naviSection == null)
                return null;
            // 获取当前路径的段数
            int segNum = carPath.sections.length;
            if (segNum < 1)
                return null;

            naviArrowStack.clear();
            if (focus_station_index >= segNum - 1)
                return null;
            GeoPoint p = Projection.LatLongToPixels(naviSection.ys[0], naviSection.xs[0], Projection.MAXZOOMLEVEL);
            float meterPerPixel20 = computeMeterPerPixel(p.x, p.y);
            int leftPixel = (int) (50 / meterPerPixel20);
            int rightPixel = (int) (50 / meterPerPixel20);
            int wholeLength = 0;
            int navix = 0, naviy = 0;

            int m_Num = naviSection.xs.length;
            for (int i = m_Num - 1; i > 0; i--) {
                GeoPoint p0 = Projection.LatLongToPixels(naviSection.ys[i], naviSection.xs[i], Projection.MAXZOOMLEVEL);
                GeoPoint p1 = Projection.LatLongToPixels(naviSection.ys[i - 1], naviSection.xs[i - 1], Projection.MAXZOOMLEVEL);
                int x0 = p0.x;
                int y0 = p0.y;
                int x1 = p1.x;
                int y1 = p1.y;

                if (i == m_Num - 1) {
                    navix = x0;
                    naviy = y0;
                    naviArrowStack.add(0, new LatLng(naviSection.ys[i], naviSection.xs[i]));
                    Log.i(TAG,"naviArrowStack.add1 latitude="+"   leftPixel="+leftPixel+"   wholeLength="+wholeLength);
                }

                int dist = (int) (Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)));

                if ((wholeLength + dist) < leftPixel) {
                    naviArrowStack.add(0, new LatLng(naviSection.ys[i - 1], naviSection.xs[i - 1]));
                    Log.i(TAG,"naviArrowStack.add2 latitude="+"  dist="+dist+"   leftPixel="+leftPixel+"   wholeLength="+wholeLength);
                    wholeLength += dist;
                } else {
                    // 截取线段上的点
                    GeoPoint gp = getDistancePoint(x0, y0, x1, y1, dist + wholeLength - leftPixel);
                    naviArrowStack.add(0, new LatLng(gp.getLatitude(),gp.getLongitude()));
                    Log.i(TAG,"naviArrowStack.add3 latitude="+"  dist="+dist+"   leftPixel="+leftPixel+"   wholeLength="+wholeLength);
                    break;
                }
            }

            // 导航点后面取1000个像素点 pixel20
//            int rightLength = rightPixel;
            wholeLength = 0;
            if (focus_station_index < segNum - 1) {
                naviSection = carPath.sections[focus_station_index + 1];
                // naviSection = carPath.sections[focus_station_index];
                int lastCoordSize = naviSection.xs.length;
                for (int i = 0; i < lastCoordSize - 1; i++) {
                    GeoPoint p0 = Projection.LatLongToPixels(naviSection.ys[i], naviSection.xs[i], Projection.MAXZOOMLEVEL);
                    GeoPoint p1 = Projection.LatLongToPixels(naviSection.ys[i + 1], naviSection.xs[i + 1], Projection.MAXZOOMLEVEL);
                    int x0 = p0.x;
                    int y0 = p0.y;
                    int x1 = p1.x;
                    int y1 = p1.y;
                    if (i == 0) {
                        if (navix != x0 || naviy != y0)
                            naviArrowStack.add(new LatLng(naviSection.ys[i], naviSection.xs[i]));
                        Log.i(TAG,"naviArrowStack.add4 latitude="+"   rightPixel="+rightPixel+"   wholeLength="+wholeLength);
                    }

                    int dist = (int) (Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)));
                    GeoPoint gp;
                    if ((wholeLength + dist) < rightPixel) {
                        if (i == lastCoordSize - 2) {
                            if (dist >= 50) {
                                gp = getDistancePoint(x0, y0, x1, y1, 50);
                                naviArrowStack.add(new LatLng(gp.getLatitude(),gp.getLongitude()));
//                                naviArrowStack.add(new LatLng(naviSection.ys[i], naviSection.xs[i]));
                                Log.i(TAG,"naviArrowStack.add5 latitude="+"  dist="+dist+"   rightLength="+rightPixel+"   wholeLength="+wholeLength);
                            } else {
                                //new LatLng(x1, y1) x1，y1传参应该为经纬度坐标 ,这里x1，y1为像素坐标
//                                naviArrowStack.add(new LatLng(x1, y1));
                                naviArrowStack.add(new LatLng(naviSection.ys[i+1], naviSection.xs[i+1]));
                                Log.i(TAG,"naviArrowStack.add6 latitude="+"  dist="+dist+"   rightLength="+rightPixel+"   wholeLength="+wholeLength);
                                wholeLength += dist;
                            }

                        } else {
                            naviArrowStack.add(new LatLng(naviSection.ys[i], naviSection.xs[i]));
                            Log.i(TAG,"naviArrowStack.add7 latitude="+"  dist="+dist+"   rightLength="+rightPixel+"   wholeLength="+wholeLength);
                            wholeLength += dist;
                        }
                    } else {
                        // 截取线段上的点
//                        gp = getDistancePoint(x0, y0, x1, y1, dist - (rightPixel - wholeLength));
//                        naviArrowStack.add(new LatLng(gp.getLatitude(),gp.getLongitude()));
                        naviArrowStack.add(new LatLng(naviSection.ys[i], naviSection.xs[i]));
                        break;
                    }
                }
            }
            return naviArrowStack;
        } catch (Exception e) {
            CatchExceptionUtil
                    .normalPrintStackTrace(e);
            return null;
        }
    }

    public static float computeMeterPerPixel(int x, int y) {
        PointD dpoint1 = Projection.PixelsToLatLong(x, y, 20);
        PointD dpoint2 = Projection
                .PixelsToLatLong(x + 1000, y, 20);

        float[] results = new float[1];
        Location.distanceBetween(dpoint1.y, dpoint1.x, dpoint2.y, dpoint2.x,
                results);

        return results[0] / 1000;
    }

    static GeoPoint getDistancePoint(int x1, int y1, int x2, int y2, int distance) {

        double mAB = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (mAB == 0)
            return null;
        double dx = (x2 - x1) * (distance) / java.lang.Math.sqrt(mAB);
        double dy = (y2 - y1) * (distance) / java.lang.Math.sqrt(mAB);
        int px = (int) (x2 - dx);
        int py = (int) (y2 - dy);
        return new GeoPoint(px, py);
    }

    public static int getDrawableID(String path, int id) {
        Field f = null;
        int drawableId = 0;
        try {
            f = R.drawable.class.getDeclaredField(path + id);
            drawableId = f.getInt(R.drawable.class);
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return drawableId;
    }
}

