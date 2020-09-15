package com.sfmap.map.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfmap.library.container.FragmentContainerDelegater;
import com.sfmap.library.container.NodeFragment;
import com.sfmap.library.container.NodeFragmentBundle;
import com.sfmap.library.util.CalculateUtil;
import com.sfmap.library.util.DateTimeUtil;
import com.sfmap.route.RouteUtil;
import com.sfmap.route.model.ICarRouteResult;
import com.sfmap.route.model.POI;
import com.sfmap.route.util.RouteCalType;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RoutePathHelper {

    /**
     * 计算步行时间
     *
     * @param length 距离(单位 m)
     * @return 步行所需时间
     */
    public static String getWalkTime(int length) {
        // 默认步行速度为60m/分钟
        int time = length / 60;
        return DateTimeUtil.getTimeStr(time * 60);
    }

    public static String createCarSubDesNoPrice(int length) {

        String tmp1[] = CalculateUtil.getLengDesc2(length);

        String tmp10 = " " + tmp1[0] + " ";
        String tmp11 = tmp1[1];
        String txt = tmp10 + tmp11;

        String res = txt;

        return res;
    }

    /**
     * 保存直接进入导航界面的POI，导航历史纪录，暂不实现。
     *
     * @param toPOI
     */
    public static void saveGotoNaviPOI(Context context, POI toPOI) {

    }


//    /**
//     * 开始导航
//     *
//     * @param activity
//     * @param carPathSearchResult 驾车线路
//     * @param isSimulateNavi      是否启动模拟导航
//     */
//    public static void startIshowFromCarPathResult(Activity activity,
//                                                   ICarRouteResult carPathSearchResult, boolean isSimulateNavi) {
//        if (carPathSearchResult == null)
//            return;
////        String method = activity.getSharedPreferences(
////                "user_route_method_info", 0).getString("car_method", RouteCalType.CARROUTE_INDEX_4);
//        String method = RouteUtil.getCarUserMethod();
////        String method = carPathSearchResult.getMethod();
//        byte data[] = carPathSearchResult.getBackUpTbtData();
//        int routeID = carPathSearchResult.getFocusRouteIndex();
//        int naviType = RouteCalType.getNaviType(method);
//        int naviFlags = RouteCalType.getNaviFlags(method);
//        gotoIshowActivity(activity, carPathSearchResult.getFromPOI(),
//                carPathSearchResult.getMidPOIs(), carPathSearchResult.getToPOI(), data, routeID,
//                naviType, naviFlags, isSimulateNavi);
//    }

//    public static void gotoIshowActivity(final Activity activity, final POI fromPOI,
//                                         final List<POI> midPOIs, final POI endPOI, final byte[] data, final int routeID,
//                                         final int method, final int iFlags, final boolean isSimNavi) {
//        if (!InMap.Ext.getLocator().isProviderEnabled(Provider.PROVIDER_GPS)) {
//
//            AlertDialogFragment newFragment = AlertDialogFragment.newInstance(R.string.caution,
//                    R.string.start_navi_msg, R.string.sure, R.string.cancel);
//            newFragment.setClickListener(new AlertDialogClickListener() {
//
//                @Override
//                public void onPositiveClick() {
//                    try {
//
//                        Intent it = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
//                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivityForResult(it, 0x1002);// 5.3.1是activity.startActivity(it);
//
//                    } catch (ActivityNotFoundException e) {
//                        ToastHelper.showToast("打开设置失败");
//                    } catch (Exception e) {
//                        CatchExceptionUtil.normalPrintStackTrace(e);
//                    }
//                }
//
//                @Override
//                public void onNegativeClick() {
//                }
//            });
//            FragmentManager m = ((FragmentActivity) activity).getSupportFragmentManager();
//            m.beginTransaction().add(newFragment, "dialog").commitAllowingStateLoss();
//            m.executePendingTransactions();
//            return;
//        }
//        NodeFragmentBundle bundle = new NodeFragmentBundle();
//        if (null != data) {
//            bundle.putByteArray(Config.ARGUMENTS_KEY_PUSHTOTBTBYTEARRAY, data);
//        }
//        buildBundle(bundle, isSimNavi, method, iFlags, routeID, fromPOI, midPOIs, endPOI);
//        startNavi(activity, bundle);
//
//    }

//    /**
//     * 启动导航界面
//     *
//     * @param activity
//     * @param bundle
//     */
//    public static void startNavi(Activity activity, NodeFragmentBundle bundle) {
//        if (activity instanceof FragmentContainerDelegater) {
//            FragmentContainerDelegater delegate = (FragmentContainerDelegater) activity;
//            delegate.getFragmentContainer().getLastFragment()
//                    .startFragment(NaviFragment.class, bundle);
//        }
//    }

    /**
     * 赋值
     *
     * @param bundle
     * @param isSimNavi   是否模拟导航
     * @param method
     * @param iFlags
     * @param routeID     路径id
     * @param fromPOI     开始点
     * @param throughPOIs 途经点
     * @param endPOI      结束点
     */
    private static void buildBundle(NodeFragmentBundle bundle, final boolean isSimNavi,
                                    final int method, final int iFlags, final int routeID, final POI fromPOI,
                                    final List<POI> throughPOIs, final POI endPOI) {
        bundle.putBoolean("IsSimNavi", isSimNavi);
        bundle.putInt("NaviMethod", method);
        bundle.putInt("RouteId", routeID);
        bundle.putSerializable("StartPOI", fromPOI == null ? null : fromPOI.as(POI.class));
        if (throughPOIs != null && throughPOIs.size() > 0) {
            ArrayList<POI> midList = new ArrayList<POI>();
            midList.addAll(throughPOIs);
            bundle.putSerializable("ThrouthPOIs", midList);
        } else {
            bundle.putSerializable("ThrouthPOIs", null);
        }
        bundle.putSerializable("EndPOI", endPOI == null ? null : endPOI.as(POI.class));
    }

    public static String dealName(String name) {
        String res = "";
        String[] sres = null;
        if (name == null)
            return "";
        if (name.indexOf("(") >= 0) {
            sres = name.split("\\(");
            if (sres.length > 1) {
                res = sres[0];
            }
        }
        return res;
    }


    /**
     * 获取 用户常用的公交规划的 method值
     *
     * @param context
     * @param defaulter
     * @return
     */
    public static String getBusUserMethod(Context context, String defaulter) {
        if (context == null) {
            return defaulter;
        }
//		SharedPreferences share = context.getSharedPreferences("user_route_method_info", 0);
//		String method = share.getString("bus_method", "");
//		if (method != null && method.length() > 0) {
//			return method;
//		} else {
        return defaulter;
//		}
    }

    /**
     * 获取 用户常用的驾车规划的 method值
     *
     * @param context
     * @param defaulter
     * @return
     */
    public static String getCarUserMethod(Context context, String defaulter) {
        MapSharePreference share = new MapSharePreference(MapSharePreference.SharePreferenceName.user_route_method_info);
        String method = share.getStringValue(MapSharePreference.SharePreferenceKeyEnum.car_method, defaulter);
        if (method != null && method.length() > 0) {
            return method;
        } else {
            return defaulter;
        }
    }

    /**
     * 保存直接进入导航界面的POI，导航历史纪录，暂不实现。
     *
     * @param toPOI
     */
    public static void saveGotoNaviPOI(POI toPOI) {

    }

    /**
     * 取最近一次设置公交时间的日期
     *
     * @param context
     * @return
     */
    public static long getBusLastSetTime(Context context) {
//		SharedPreferences share = context.getSharedPreferences("user_route_method_info", 0);
//		long time = share.getLong("bus_time_lastset", -1);
        return -1;
    }

    public static void putCarUserMethod(Context context, String method) {
        MapSharePreference share = new MapSharePreference(MapSharePreference.SharePreferenceName.user_route_method_info);
        share.putStringValue(MapSharePreference.SharePreferenceKeyEnum.car_method, method);
    }


    /**
     * 返回整数距离描述
     *
     * @param meter
     * @return
     */
    public static String getLengDesc(int meter) {
        if (meter < 1000)
            return meter + "米";
        int kiloMeter = meter / 1000;
        int leftMeter = meter % 1000;
        leftMeter = leftMeter / 100;
        return kiloMeter + "公里";
    }

    private static String strFormat = "<font color=\"[color]\">%s</font>";

    /**
     * 将指定文本中的数字标成黑色
     *
     * @param text
     */
    public static SpannableString textNumberHighlightBlack(String text) {
        SpannableString sp = new SpannableString(text);
        try {
            if (!TextUtils.isEmpty(text)) {
                // 把字符串转换为字符数组
                char num[] = text.toCharArray();
                for (int i = 0; i < num.length; i++) {
                    // 判断输入的数字是否为数字还是字符
                    if (Character.isDigit(num[i]) || num[i] == '.') { // 把字符串转换为字符，再调用Character.isDigit(char)方法判断是否是数字，是返回True，否则False
                        // 黑色
                        sp.setSpan(new ForegroundColorSpan(Color.argb(255, 59, 59, 59)), i, i + 2,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return sp;
    }


    /**
     * 将关键字加粗标黑
     *
     * @param result
     * @param keyword
     * @return
     */
    public static SpannableString getMarkBoldString(String result, String keyword) {
        SpannableString sp = new SpannableString(result);
        try {
            if (keyword != null && result.contains(keyword)) {
                int sIndex = result.indexOf(keyword);
                int eIndex = sIndex + keyword.length();
                if (sIndex >= 0 && eIndex > 0 && sIndex < eIndex) {
                    // 将关键字颜色标记为黑色
                    sp.setSpan(new StyleSpan(Typeface.NORMAL), 0, sIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new StyleSpan(Typeface.NORMAL), eIndex, result.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new StyleSpan(Typeface.BOLD), sIndex, eIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return sp;
    }

    /**
     * 将关键字以外的字体加粗
     *
     * @param result
     * @param keyword
     * @return
     */
    public static SpannableString getCustomMarkBoldString(String result, String keyword) {
        SpannableString sp = new SpannableString(result);
        try {
            if (keyword != null && result.contains(keyword)) {
                int sIndex = result.indexOf(keyword);
                int eIndex = sIndex + keyword.length();
                sp.setSpan(new StyleSpan(Typeface.BOLD), 0, sIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(new StyleSpan(Typeface.BOLD), eIndex, result.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(new StyleSpan(Typeface.NORMAL), sIndex, eIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return sp;
    }

    /**
     * 将指定字符串中的关键字标记成0x333333颜色，并加粗
     *
     * @param result  指定字符串
     * @param keyword 关键字
     * @return
     */
    public static SpannableString getMarkBlodAndColorString(String result, String keyword) {
        SpannableString sp = new SpannableString(result);
        try {
            if (keyword != null && result.contains(keyword)) {
                int sIndex = result.indexOf(keyword);
                int eIndex = sIndex + keyword.length();
                if (sIndex >= 0 && eIndex > 0 && sIndex < eIndex) {
                    // 将关键字颜色标记为指定颜色
                    sp.setSpan(new ForegroundColorSpan(Color.argb(255, 51, 51, 51)),
                            sIndex, eIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // 将关键字加粗
                    sp.setSpan(new StyleSpan(Typeface.BOLD),
                            sIndex, eIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return sp;
    }


    /**
     * 保存图片至相册
     *
     * @param bmap
     */
    private static void saveBitmapToAlbum(Context context, Bitmap bmap) {
        try {
            ContentValues values = new ContentValues(7);
            String newname = DateFormat.format("yyyy-MM-dd kk.mm.ss", System.currentTimeMillis())
                    .toString();
            // 名称，随便
            values.put(MediaStore.Images.Media.TITLE, newname);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, newname);
            // 描述，随便
            values.put(MediaStore.Images.Media.DESCRIPTION, "顺丰地图公交方案详情");
            // 图像的拍摄时间，显示时根据这个排序
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            // 默认为jpg格式
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.ORIENTATION, 0);

            final String CAMERA_IMAGE_BUCKET_NAME = context.getExternalFilesDir(
                    Environment.DIRECTORY_DCIM).getPath();

            final String CAMERA_IMAGE_BUCKET_ID = String.valueOf(CAMERA_IMAGE_BUCKET_NAME
                    .hashCode());
            values.put(Images.ImageColumns.BUCKET_ID, CAMERA_IMAGE_BUCKET_ID);
            // 先得到新的URI
            Uri uri = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // 写入数据
            OutputStream outStream = context.getContentResolver().openOutputStream(uri);
            bmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            mediaScanner(context, getFilePathByContentResolver(context, uri));
            return;
        } catch (Exception e) {
        }
    }

    /**
     * 扫描相册目录，解决小米2s等手机截图后无法显示图片的问题
     *
     * @param filepath
     */
    private static void mediaScanner(Context context, String filepath) {
        if (!hasKitkat()) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(filepath));
            intent.setData(uri);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 检测版本
     *
     * @return
     */
    private static boolean hasKitkat() {
        // 解决小米3手机截图后，相册无法显示问题
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
    }

    /**
     * 获取该图片显示路径
     *
     * @param context
     * @param uri
     * @return
     */
    private static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException("Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    private static void measureTextWidth(TextView textView, String str, int width, float density) {
        float textViewWidth = width - (30) * density;
        Paint paint = textView.getPaint();
        paint.setTextSize(textView.getTextSize());
        String txt = (String) TextUtils.ellipsize(str, (TextPaint) paint, textViewWidth,
                TextUtils.TruncateAt.END);
        textView.setText(txt);
    }
}
