package com.sfmap.tbt.util;

import android.util.Log;

public class LogUtil {
    /**
     * 截断输出日志
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;

        int segmentSize = 3 * 1024;
        long length = msg.length();
        Log.d(tag, msg);
//        if (length <= segmentSize ) {// 长度小于等于限制直接打印
//            Log.d(tag, msg);
//        }else {
//            while (msg.length() > segmentSize ) {// 循环分段打印日志
//                String logContent = msg.substring(0, segmentSize );
//                msg = msg.replace(logContent, "");
//                Log.d(tag, logContent);
//            }
//            Log.d(tag, msg);// 打印剩余日志
//        }
    }
}
