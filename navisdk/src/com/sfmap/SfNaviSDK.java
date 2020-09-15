package com.sfmap;

import android.app.Application;
import android.content.Context;

public class SfNaviSDK {
    public static Application mContext;

    public static void init(Context context) {
        mContext = (Application) context;

    }
}
