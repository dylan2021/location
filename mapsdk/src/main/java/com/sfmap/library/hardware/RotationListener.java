package com.sfmap.library.hardware;

/**
 * 旋转监听
 */
public abstract interface RotationListener {
    /**
     * 回调用旋转角度
     * @param paramInt  角度值
     */
    public abstract void rotationChanged(int paramInt);
}