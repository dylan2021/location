package com.sfmap.library.animation;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 动画工厂类，获取Animation和Interpolator.
 */
public class AnimationFactory {
    /**
     * 开始动画
     *
     * @param view       视图对象
     * @param fromXValue 屏幕横坐标
     * @param fromYValue 屏幕纵坐标
     * @param duration   动画执行时间
     * @param listener   动画监听
     */
    public static void startShowAnimation(View view, float fromXValue,
                                          float fromYValue, int duration, AnimationListener listener) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0F, 1.0F, 0.0F,
                1.0F);
        scaleAnimation.setDuration(duration);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2F, 1.0F);
        alphaAnimation.setDuration(duration);
        animationSet.addAnimation(alphaAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromXValue,
                Animation.RELATIVE_TO_SELF, 0.0F, Animation.RELATIVE_TO_SELF,
                fromYValue, Animation.RELATIVE_TO_SELF, 0.0F);
        translateAnimation.setDuration(duration);
        animationSet.addAnimation(translateAnimation);
        if (listener != null)
            animationSet.setAnimationListener(listener);

        view.setDrawingCacheEnabled(true);
        view.startAnimation(animationSet);
    }

    /**
     * 结束动画
     *
     * @param view     视图对象
     * @param toXValue 屏幕横坐标
     * @param toYValue 屏幕纵坐标
     * @param duration 动画执行时间
     * @param listener 动画监听
     */
    public static void startDismissAnimation(View view, float toXValue,
                                             float toYValue, int duration, AnimationListener listener) {

        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 0.0F, 1.0F,
                0.0F);
        scaleAnimation.setDuration(duration);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 0.2F);
        alphaAnimation.setDuration(duration);
        animationSet.addAnimation(alphaAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0F, Animation.RELATIVE_TO_SELF,
                toXValue, Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toYValue);
        translateAnimation.setDuration(duration);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(listener);
        view.startAnimation(animationSet);
    }

    /**
     * 制定view从底部进入动画
     *
     * @param view         视图对象
     * @param duration     动画执行时间
     * @param listener     动画监听
     * @param isFillAfter  动画终止时停留在最后一帧
     * @param delaytedTime 延时时间
     */
    public static void startBottomInAnimation(View view, int duration,
                                              AnimationListener listener, boolean isFillAfter, int delaytedTime) {
        if (view == null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(duration);
        if (listener != null)
            translateAnimation.setAnimationListener(listener);

        translateAnimation.setFillAfter(isFillAfter);
        translateAnimation.setStartOffset(delaytedTime);
        view.setDrawingCacheEnabled(true);
        view.startAnimation(translateAnimation);
    }

    /**
     * 制定view从底部退出动画
     *
     * @param view         视图对象
     * @param duration     动画执行时间
     * @param listener     动画监听
     * @param isFillAfter  动画终止时停留在最后一帧
     * @param delaytedTime 延时时间
     */
    public static void startBottomOutAnimation(View view, int duration,
                                               AnimationListener listener, boolean isFillAfter, int delaytedTime) {
        if (view == null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        translateAnimation.setDuration(duration);
        if (listener != null)
            translateAnimation.setAnimationListener(listener);

        translateAnimation.setFillAfter(isFillAfter);
        translateAnimation.setStartOffset(delaytedTime);
        view.setDrawingCacheEnabled(true);
        view.startAnimation(translateAnimation);
    }

    /**
     * 制定view从顶部退出动画
     *
     * @param view         视图对象
     * @param duration     动画执行时间
     * @param listener     动画监听
     * @param isFillAfter  动画终止时停留在最后一帧
     * @param delaytedTime 延时时间
     */
    public static void startTopInAnimation(View view, int duration,
                                           AnimationListener listener, boolean isFillAfter, int delaytedTime) {
        if (view == null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(duration);
        if (listener != null)
            translateAnimation.setAnimationListener(listener);

        translateAnimation.setFillAfter(isFillAfter);
        translateAnimation.setStartOffset(delaytedTime);
        view.setDrawingCacheEnabled(true);
        view.startAnimation(translateAnimation);
    }

    /**
     * 制定view从顶部进入动画
     *
     * @param view         视图对象
     * @param duration     动画执行时间
     * @param listener     动画监听
     * @param isFillAfter  动画终止时停留在最后一帧
     * @param delaytedTime 延时时间
     */
    public static void startTopOutAnimation(View view, int duration,
                                            AnimationListener listener, boolean isFillAfter, int delaytedTime) {
        if (view == null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, // fromXType,fromX
                Animation.RELATIVE_TO_SELF, 0.0f, // toXType,toX
                Animation.RELATIVE_TO_SELF, 0.0f, // fromYType,fromY
                Animation.RELATIVE_TO_SELF, -1.0f); // toYType,toY
        translateAnimation.setDuration(duration);
        if (listener != null)
            translateAnimation.setAnimationListener(listener);

        translateAnimation.setFillAfter(isFillAfter);
        translateAnimation.setStartOffset(delaytedTime);
        view.setDrawingCacheEnabled(true);
        view.startAnimation(translateAnimation);
    }

    /**
     * 开始动画
     *
     * @param view         视图对象
     * @param fromXValue   动画开始横坐标
     * @param toXValue     动画结束横坐标
     * @param fromYValue   动画开始纵坐标
     * @param toYValue     动画结束纵坐标
     * @param duration     动画执行时间
     * @param listener     动画监听
     * @param isFillAfter  动画终止时停留在最后一帧
     * @param delaytedTime 延时时间
     */
    public static void startShowAnimationFromPoint(View view, float fromXValue,
                                                   float toXValue, float fromYValue, float toYValue, int duration,
                                                   AnimationListener listener, boolean isFillAfter, int delaytedTime) {
        if (view == null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue,
                Animation.ABSOLUTE, fromYValue, Animation.ABSOLUTE, toYValue);
        translateAnimation.setDuration(duration);

        if (listener != null)
            translateAnimation.setAnimationListener(listener);

        translateAnimation.setFillAfter(isFillAfter);
        translateAnimation.setStartOffset(delaytedTime);

        view.setDrawingCacheEnabled(true);
        view.startAnimation(translateAnimation);
    }

    /**
     * 获取动画
     *
     * @param fromXValue  动画开始横坐标
     * @param toXValue    动画结束横坐标
     * @param fromYValue  动画开始纵坐标
     * @param toYValue    动画结束纵坐标
     * @param duration    动画执行时间
     * @param listener    动画监听
     * @param delayedTime 延时时间
     * @return
     */
    public static Animation getFromPointAnimation(float fromXValue,
                                                  float toXValue, float fromYValue, float toYValue, int duration,
                                                  AnimationListener listener, int delayedTime) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue,
                Animation.ABSOLUTE, fromYValue, Animation.ABSOLUTE, toYValue);
        translateAnimation.setDuration(duration);
        if (listener != null)
            translateAnimation.setAnimationListener(listener);
        translateAnimation.setStartOffset(delayedTime);
        return translateAnimation;
    }

    /**
     * 动画消失
     *
     * @param view     视图对象
     * @param toXValue 动画结束横坐标
     * @param toYValue 动画结束纵坐标
     * @param duration 动画执行时间
     * @param listener 动画监听
     */
    public static void startDismissAnimationFromPoint(View view,
                                                      float toXValue, float toYValue, int duration,
                                                      AnimationListener listener) {

        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 0.0F, 1.0F,
                0.0F);
        scaleAnimation.setDuration(duration);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 0.2F);
        alphaAnimation.setDuration(duration);
        animationSet.addAnimation(alphaAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0F, Animation.ABSOLUTE, toXValue,
                Animation.RELATIVE_TO_SELF, 0.0F, Animation.ABSOLUTE, toYValue);
        translateAnimation.setDuration(duration);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(listener);
        view.startAnimation(animationSet);
    }


    public static void startRotationAnimation(View view, float fromValue,
                                              float toValue, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", fromValue, toValue);
        animator.setDuration(duration);
        animator.start();
    }
}