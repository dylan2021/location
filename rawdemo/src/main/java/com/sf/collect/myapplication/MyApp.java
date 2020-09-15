package com.sf.collect.myapplication;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.sfmap.SfNaviSDK;
import com.tencent.bugly.crashreport.CrashReport;

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SfNaviSDK.init(this);
        CrashReport.initCrashReport(getApplicationContext(), "065f906b5e", false);
    }
}
