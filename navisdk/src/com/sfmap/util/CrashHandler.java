package com.sfmap.util;
import java.lang.Thread.UncaughtExceptionHandler;

import com.tencent.bugly.crashreport.CrashReport;

import android.annotation.SuppressLint;
import android.content.Context;

public class CrashHandler implements UncaughtExceptionHandler {

    @SuppressLint("StaticFieldLeak")
    private static CrashHandler mHandler;

    private Context mContext;

    private CrashHandler(Context context) {
        mContext = context;
    }

    public static CrashHandler getInstance(Context context) {

        if (null == mHandler) {
            synchronized (CrashHandler.class) {
                if (null == mHandler) {
                    mHandler = new CrashHandler(context.getApplicationContext());
                }
            }
        }

        return mHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        CrashReport.postCatchedException(ex, thread);
        System.exit(-1);
    }
}
