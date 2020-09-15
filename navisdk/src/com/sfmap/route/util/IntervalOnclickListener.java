package com.sfmap.route.util;

import android.os.Handler;
import android.view.View;

/**
 * 防止快速点击的click 监听
 */
public class IntervalOnclickListener implements View.OnClickListener {
    Runnable clickRunnable;
    Handler handler;
    boolean isResponsed = true;
    long intervalResponsedTime = 500;
    Runnable timeRunnable = new Runnable() {

        @Override
        public void run() {
            // 防止错误
            handler.removeCallbacks(clickRunnable);
            // 防止错误
            handler.removeCallbacks(timeRunnable);
            isResponsed = true;
        }
    };

    /**
     * @param clickRunnable         响应CLICK事件
     * @param intervalResponsedTime 间隔响应时间
     */
    public IntervalOnclickListener(Runnable clickRunnable,
                                   long intervalResponsedTime) {
        handler = new Handler();
        this.clickRunnable = clickRunnable;
        this.intervalResponsedTime = intervalResponsedTime;
    }

    @Override
    public void onClick(View v) {
        if (isResponsed) {
            if (intervalResponsedTime > 0) {
                isResponsed = false;
                handler.postDelayed(timeRunnable, intervalResponsedTime);
            }
            handler.post(clickRunnable);
        }
    }

}
