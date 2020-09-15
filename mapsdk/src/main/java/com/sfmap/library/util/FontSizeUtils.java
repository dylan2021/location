package com.sfmap.library.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * 字体工具类
 */
public class FontSizeUtils {

    /**
     * 用于适配当系统字体设为大时，显示不全的问题
     *
     * @param context  上下文
     * @param textView textview控件
     */
    public static void scaleFontSize(Context context, TextView textView) {
        if (context == null || textView == null) {
            return;
        }

        try {
            float fontScale = getFontScale();
            if (fontScale > 1) {// 如果体统字体为大或者特大
                float textSize = textView.getTextSize();
                textSize = DeviceUtil.pxToSp(context, textSize) / fontScale;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
    }

    /**
     * 用于适配当系统字体设为大时，显示不全的问题
     *
     * @param context   上下文
     * @param button  按钮
     */
    public static void scaleFoneSize(Context context, Button button) {
        try {
            float fontScale = getFontScale();
            if (fontScale > 1) {// 如果体统字体为大或者特大
                float textSize = button.getTextSize();
                textSize = DeviceUtil.pxToSp(context, textSize) / fontScale;
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
    }

    /**
     * 用于适配当系统字体设为大时，显示不全的问题
     *
     * @param context   上下文
     * @param editText 输入框
     */
    public static void scaleFontSizeForEditText(Context context,
                                                EditText editText) {
        try {
            float fontScale = getFontScale();
            if (fontScale > 1) {// 如果体统字体为大或者特大
                float textSize = editText.getTextSize();
                textSize = DeviceUtil.pxToSp(context, textSize) / fontScale;
                editText.setTextSize(textSize);
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
    }

    /**
     * 获取系统字体大小
     * @return 系统字体大小
     */
    private static float getFontScale() {
        try {
            Object objIActMag, objActMagNative;
            Class clzIActMag = Class.forName("android.app.IActivityManager");
            Class clzActMagNative = Class
                    .forName("android.app.ActivityManagerNative");
            Method mtdActMagNative$getDefault = clzActMagNative
                    .getDeclaredMethod("getDefault");
            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            Method mtdIActMag$getConfiguration = clzIActMag
                    .getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration
                    .invoke(objIActMag);
            return config.fontScale;
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return -1;
    }

}
