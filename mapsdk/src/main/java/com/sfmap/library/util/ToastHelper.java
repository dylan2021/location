package com.sfmap.library.util;

import java.lang.ref.SoftReference;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastHelper {

    private static ToastImpl toastImpl;

    public static void init(Application app) {
        toastImpl = new ToastImpl(app);
    }

    /**
     * 初始化特殊样式 或者使用默认样式
     *
     * @param content  上下文
     * @param textView 控件TextView
     */
    public static void initStyle(View content, TextView textView) {
        assertInit();
        toastImpl.initStyle(content, textView);
    }

    private static void assertInit() {
        if (toastImpl == null) {
            throw new IllegalStateException("ToastHelper need be init first..");
        }
    }

    /**
     * 取消toast显示
     */
    public static void cancel() {
        assertInit();
        toastImpl.cancel();
    }

    /**
     * 短时间显示toast
     *
     * @param message 显示的内容
     */
    public static void showToast(CharSequence message) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示toast,可自定义
     *
     * @param message 显示的内容
     * @param gravity 放置位置
     * @param xofsset x方向的偏差
     * @param yoffset y方向的偏差
     */
    public static void showToast(CharSequence message, int gravity, int xofsset,
                                 int yoffset) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_SHORT, gravity, xofsset,
                yoffset);
    }

    /**
     * 长时间显示toast
     *
     * @param message 显示的内容
     */
    public static void showLongToast(CharSequence message) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示toast,可自定义
     *
     * @param message 显示的内容
     * @param gravity 放置位置
     * @param xofsset x方向的偏差
     * @param yoffset y方向的偏差
     */
    public static void showLongToast(CharSequence message, int gravity, int xofsset,
                                     int yoffset) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_LONG, gravity, xofsset,
                yoffset);
    }

    /**
     * @deprecated
     */
    private static class ToastImpl {
        private Application app;
        private View toastView;
        private TextView textView;
        private SoftReference<Toast> softRefToast;
        private static Handler handler = new Handler(Looper.getMainLooper());

        ToastImpl(Application app) {
            this.app = app;
        }

        void initStyle(View contentView, TextView textView) {
            if (contentView != null && textView != null) {
                this.toastView = contentView;
                this.textView = textView;
            }
        }

        void cancel() {
            if (softRefToast != null && softRefToast.get() != null) {
                softRefToast.get().cancel();
            }
        }

        void showToast(CharSequence message, int duration) {
            showToast(message, duration, null, null);
        }

        void showToast(CharSequence message, int duration, int gravity,
                       int xofsset, int yoffset) {
            showToast(message, duration, new GravityBean(gravity, xofsset,
                    yoffset), null);
        }

        @SuppressWarnings("unused")
        void showToast(CharSequence message, int duration,
                       float horizontalMargin, float verticalMargin) {
            showToast(message, duration, null, new MarginBean(horizontalMargin,
                    verticalMargin));
        }

        private void showToast(final CharSequence message, final int duration,
                               final GravityBean gravity, final MarginBean margin) {
            handler.post(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(app, message, duration);
                    if (gravity != null) {
                        toast.setGravity(gravity.gravity, gravity.xoffset,
                                gravity.yoffset);
                    }
                    if (margin != null) {
                        toast.setMargin(margin.horizontalMargin,
                                margin.verticalMargin);
                    }
                    if (toastView != null && textView != null) {
                        toast.setView(toastView);
                        textView.setText(message);
                    }
                    synchronized (ToastImpl.this) {
                        if (softRefToast != null && softRefToast.get() != null) {
                            softRefToast.get().cancel();
                        }
                        softRefToast = new SoftReference<Toast>(toast);
                        toast.show();
                    }
                }
            });
        }

        /**
         * @deprecated
         */
        private class GravityBean {

            int gravity;
            int xoffset;
            int yoffset;

            public GravityBean(int g, int xo, int yo) {
                gravity = g;
                xoffset = xo;
                yoffset = yo;
            }
        }

        private class MarginBean {

            float horizontalMargin;
            float verticalMargin;

            public MarginBean(float hm, float vm) {
                horizontalMargin = hm;
                verticalMargin = vm;
            }
        }

    }


}
