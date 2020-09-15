package com.sfmap.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.sfmap.SfNaviSDK;
import com.sfmap.navi.BuildConfig;

import java.util.ArrayList;

public class ShakeDetector implements SensorEventListener {
    private final static String TAG = "ShakeDetector";
    /**
     * 检测的时间间隔
     */
    private static final int UPDATE_INTERVAL = 1000;
    /**
     * 上一次检测的时间
     */
    private long mLastUpdateTime;
    /**
     * 上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
     */
    private float mLastX, mLastY, mLastZ;
    private SensorManager mSensorManager;
    private ArrayList<OnShakeListener> mListeners;
    /**
     * 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
     */
    private int shakeThreshold = 5;
    private long mStopContinueTime = 0 - System.currentTimeMillis();
    private long mSHakeContinueTime = 0 - System.currentTimeMillis();

    private boolean isStarted = false;
    private int MOVE_DELAYED = 1500;
    private int STATIC_DELAYED = 1000 * 60 * 2;

    public ShakeDetector(Context context) {
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mListeners = new ArrayList<>();
    }

    /**
     * 当摇晃事件发生时，接收通知
     */
    public interface OnShakeListener {
        /**
         * 当手机摇晃时被调用
         */
        void exitStatic();

        void enterStatic();
    }

    /**
     * 注册OnShakeListener，当摇晃时接收通知
     *
     * @param listener
     */
    public void registerOnShakeListener(OnShakeListener listener) {
        if (mListeners.contains(listener))
            return;
        mListeners.add(listener);
    }

    /**
     * 移除已经注册的OnShakeListener
     *
     * @param listener
     */
    public void unregisterOnShakeListener(OnShakeListener listener) {
        mListeners.remove(listener);
    }

    /**
     * 启动摇晃检测
     */
    public void start() {

        this.mSHakeContinueTime = 0;
        this.mStopContinueTime = 0;

        if (mSensorManager == null) {
            throw new UnsupportedOperationException();
        }
        Sensor sensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            throw new UnsupportedOperationException();
        }
        boolean success = mSensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_UI);
        if (!success) {
            throw new UnsupportedOperationException();
        }
        isStarted = true;
    }

    /**
     * 停止摇晃检测
     */
    public void stop() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);

        isStarted = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL)
            return;
        mLastUpdateTime = currentTime;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;
        mLastX = x;
        mLastY = y;
        mLastZ = z;
        float delta = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / diffTime * 10000;
        if (delta > shakeThreshold) { // 当加速度的差值大于指定的阈值，认为这是一个摇晃
            mStopContinueTime = 0;
            mSHakeContinueTime += diffTime;
        } else {
            //wenbaolin 20170517 不能在此处重置mSHakeContinueTime。否则就会短时间内反复退出静止状态
            //mSHakeContinueTime = 0;
            mStopContinueTime += diffTime;
        }
        if(mStopContinueTime >= STATIC_DELAYED){
            this.notifyStopListeners();
            mStopContinueTime = 0 - System.currentTimeMillis();
            mSHakeContinueTime = 0;
        }
        if(mSHakeContinueTime >= MOVE_DELAYED){
            this.notifyRunListeners();
            mSHakeContinueTime = 0 - System.currentTimeMillis();
            mStopContinueTime = 0;
        }
    }

    /**
     * 当摇晃事件发生时，通知所有的listener
     */
    private void notifyRunListeners() {
        if(BuildConfig.DEBUG) {
            CommUtil.d(SfNaviSDK.mContext, TAG, "ShakeDetector detect move...");
        }
        for (OnShakeListener listener : mListeners) {
            listener.exitStatic();
        }
    }

    /**
     * 当静止事件发生时，通知所有的listener
     */
    private void notifyStopListeners() {
        if(BuildConfig.DEBUG) {
            CommUtil.d(SfNaviSDK.mContext, TAG, "ShakeDetector detect motionless ...");
        }
        for (OnShakeListener listener : mListeners) {
            listener.enterStatic();
        }
    }

    public  boolean isStarted(){
        return isStarted;
    }

    public void updateShakeDelayed(){
//        LocationOption option = LocationOption.getInstance();
//        MOVE_DELAYED = option.getShakeMoveDelayed();
//        STATIC_DELAYED = option.getShakeStaticDelayed();
    }
}
