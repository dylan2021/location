package com.sfmap.library.util;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * DeviceUtil设备
 */
public class DeviceUtil {

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context 上下文
     * @param pxValue 精密度
     *                （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int pxToSp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    public static int getDrawableHeight(Context context, int resID) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(context.getResources(), resID, options);
        return options.outHeight;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context 上下文
     * @return 屏幕宽度大小
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context 上下文
     * @return 屏幕高度大小
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * 获得状态栏的高度
     *
     * @param context 上下文
     * @return 屏幕高度大小
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen.xml");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context  上下文
     * @param dipValue dp大小
     * @return px大小
     */
    public static int dipToPixel(Context context, int dipValue) {
        if (context == null) {
            return dipValue; // 原值返回
        }
        try {
            float pixelFloat = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                            .getResources().getDisplayMetrics());
            return (int) pixelFloat;
        } catch (Exception e) {

        }
        return dipValue;
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param spValue sp大小
     * @return px大小
     */
    public static int spToPixel(Context context, int spValue) {
        float pixelFloat = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources()
                        .getDisplayMetrics());
        return (int) pixelFloat;
    }

    /**
     * dip转px
     *
     * @param context  上下文
     * @param dipValue dip
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("@@@", "dip2px: density = " + scale);
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param context 上下文
     * @param pxValue px
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
