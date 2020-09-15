package com.sfmap.library.hardware;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;

import com.sfmap.library.util.CatchExceptionUtil;
import com.sfmap.plugin.IMPluginManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 传感器核心类
 */
public class SensorHelper implements SensorEventListener {
    float[] I;
    float[] Rcorrected;
    float[] Rraw;
    float[] values;
    protected float[] Rsmooth;
    private float[] accels;
    private float[] mags;
    protected float[] outR;
    private boolean isReady;
    private boolean active;
    private int mScreenState;
    private SensorManager mSensorManager;
    private static SensorHelper instance;
    protected static final int LANDSCAPE = 1;
    protected static final int LANDSCAPE_REVERSE = 3;
    protected static final int PORTRAIT = 0;
    protected static final int PORTRAIT_REVERSE = 2;
    protected static final int UNKNOWN = -1;
    protected static final int SENSOR_MAG = 0;
    public static final int SENSOR_ORN = 1;
    private Set<RotationListener> mScreenRotationListeners;
    private Set<SensorHelperListener> mSensorListeners;
    private int mSensorMode = SENSOR_MAG;
    private float mAngle = 0.0f;
    private boolean isInited = false;

    private Context mContext;

    private SensorHelper() {
        float[] arrayOfFloat1 = {1065353216, 0, 0, 0, 0, 1065353216, 0, 0, 0,
                0, 1065353216, 0, 0, 0, 0, 1065353216};
        this.Rsmooth = arrayOfFloat1;
        float[] arrayOfFloat2 = {1065353216, 0, 0, 0, 0, 1065353216, 0, 0, 0,
                0, 1065353216, 0, 0, 0, 0, 1065353216};
        this.Rraw = arrayOfFloat2;
        float[] arrayOfFloat3 = new float[16];
        this.Rcorrected = arrayOfFloat3;
        float[] arrayOfFloat4 = new float[16];
        this.outR = arrayOfFloat4;
        float[] arrayOfFloat5 = new float[16];
        this.I = arrayOfFloat5;
        float[] arrayOfFloat6 = new float[3];
        this.values = arrayOfFloat6;
        try {
            mSensorManager = (SensorManager) IMPluginManager.getApplication()
                    .getSystemService(Context.SENSOR_SERVICE);
        } catch (Exception e) {
            CatchExceptionUtil
                    .normalPrintStackTrace(e);
        }
        mContext = IMPluginManager.getApplication();
        mScreenState = mContext.getResources().getConfiguration().orientation;
        mScreenRotationListeners = new HashSet<RotationListener>();
        mSensorListeners = new HashSet<SensorHelperListener>();
    }

    /**
     * 获取SensorHelper实例(单利模式)
     * @return
     */
    public static SensorHelper getInstance() {
        if (instance == null)
            instance = new SensorHelper();
        return instance;
    }

    private float radToDeg(float paramFloat) {
        return (float) Math.toDegrees(paramFloat);
    }

    /**
     * @deprecated
     * @param arg0
     * @param arg1
     */
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    protected boolean _isScreenRotationStateChanged(int i, int j) {
        if (mScreenState == UNKNOWN || i >= 30 || i <= 65506) {
            int m = ((j + 360 + 45) % 360) / 90;
            int k = LANDSCAPE;
            switch (m) {
                case 0:
                    k = LANDSCAPE;
                    break;
                case 1:
                    k = PORTRAIT;
                    break;
                case 2:
                    k = LANDSCAPE_REVERSE;
                    break;
                case 3:
                    k = PORTRAIT_REVERSE;
                    break;
            }

            int l = mScreenState;
            boolean flag = false;
            if (k != l)
                flag = true;
            else
                flag = false;
            if (flag)
                mScreenState = k;
            return flag;
        } else {
            return false;
        }
    }

    protected void notifyScreenRotationListeners() {
        Iterator<RotationListener> iterator = mScreenRotationListeners
                .iterator();
        do {
            if (!iterator.hasNext())
                return;
            RotationListener rotationlistener = (RotationListener) iterator
                    .next();
            int i = mScreenState;
            rotationlistener.rotationChanged(i);
        } while (true);
    }

    protected void notifySensorListerners(float azimuth, float pitch, float roll) {
        Iterator<SensorHelperListener> iterator = mSensorListeners.iterator();
        do {
            if (!iterator.hasNext())
                return;
            SensorHelperListener sensorlistener = (SensorHelperListener) iterator
                    .next();
            sensorlistener.sensorChanged(azimuth, pitch, roll);
        } while (true);
    }

    /**
     * @deprecated
     * @param event
     */
    public void onSensorChanged(SensorEvent event) {

        if (mSensorManager == null)
            return;

        if (mSensorMode == SENSOR_ORN) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
//		Log.e("@@@", "onSensorChanged: event.values[0] = "+x);
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION: {

                    WindowManager wmManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    int curScreenState = wmManager.getDefaultDisplay().getRotation();
                    if (isInited == false) {
                        isInited = true;
                        if (x == 0)
                            return;
                    }

                    if (curScreenState == LANDSCAPE) {
                        Log.e("@@@", "onSensorChanged: curScreenState == LANDSCAPE");
                        x += 90;
                    } else if (curScreenState == PORTRAIT_REVERSE) {
                        Log.e("@@@", "onSensorChanged: curScreenState == PORTRAIT_REVERSE");
                        x += 180;
                    } else if (curScreenState == LANDSCAPE_REVERSE) {
                        Log.e("@@@", "onSensorChanged: curScreenState == LANDSCAPE_REVERSE");
                        x += 270;
                    }

                    x = (x + 360.0F) % 360.0F;
                    if (Math.abs(mAngle - x) < 5.0f) {
                        break;
                    }

                    mAngle = x;

//				Log.e("@@@", "onSensorChanged: notify1 x="+x);
                    this.notifySensorListerners(x, y, z);
                }
            }

            return;
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                isReady = true;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();
        }

        if (mags != null && accels != null && isReady) {
            isReady = false;

            SensorManager.getRotationMatrix(Rraw, I, accels, mags);

            SensorManager.remapCoordinateSystem(Rraw, SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X, Rcorrected);

            for (int i = 0; i < 16; i++) {
                float f7 = Rcorrected[i];
                float f8 = Rsmooth[i];
                float f9 = f7 - f8;
                float f10;
                if (f9 > 0F)
                    f10 = f9;
                else
                    f10 = -f9;
                if (0.005F > f10)
                    f10 = 0.005F;
                if (f10 > 2F) {
                    Rsmooth[i] = Rcorrected[i];
                } else {
                    float af13[] = Rsmooth;
                    float f12 = 2F - f10;
                    float f13 = Rsmooth[i];
                    float f14 = (f12 * f13) / 2F;
                    float f15 = (Rcorrected[i] * f10) / 2F;
                    float f16 = f14 + f15;
                    af13[i] = f16;
                }
            }

            SensorManager.remapCoordinateSystem(Rsmooth, SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, values);
            values[0] = radToDeg(values[0]);
            values[1] = -90.0F + radToDeg(values[1]);
            values[2] = radToDeg(values[2]);

//			Log.e("@@@", "onSensorChanged: notify2 x="+values[0]);
            this.notifySensorListerners(values[0], values[1], values[2]);

            if (_isScreenRotationStateChanged((int) values[1], (int) values[2])) {
                notifyScreenRotationListeners();
            }
        }
    }

    /**
     * k
     * @return
     */
    public int getRotation() {
        return mScreenState;
    }

    /**
     * 开始传感器监听
     * @param sensorMode
     */
    public void startSensor(int sensorMode) {

        if (this.active)
            return;

        if (mSensorManager == null)
            return;

        mSensorMode = sensorMode;
        if (sensorMode == SENSOR_MAG) {
            Sensor s1 = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor s2 = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mSensorManager.registerListener(this, s1,
                    SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(this, s2,
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            Sensor s = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            mSensorManager.registerListener(this, s,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        active = true;
    }

    /**
     * 停止传感器监听
     */
    public void stopSensor() {
        isInited = false;
        if (mSensorManager == null)
            return;

        if (mSensorMode == SENSOR_MAG) {
            Sensor s1 = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor s2 = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mSensorManager.unregisterListener(this, s1);
            mSensorManager.unregisterListener(this, s2);
        } else {
            Sensor s = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            mSensorManager.unregisterListener(this, s);
        }

        active = false;
        mScreenState = UNKNOWN;
        mSensorMode = SENSOR_MAG;
    }

    /**
     * 注册旋转监听
     * @param rotationlistener
     */
    public void registerRotationListener(RotationListener rotationlistener) {
        if (rotationlistener != null)
            mScreenRotationListeners.add(rotationlistener);
    }

    /**
     * 反注册旋转监听
     * @param rotationlistener
     */
    public void unregisterRotationListener(RotationListener rotationlistener) {
        if (rotationlistener != null)
            mScreenRotationListeners.remove(rotationlistener);
    }

    /**
     * 注册传感器监听
     * @param sensorlistener
     */
    public void registerSensorListener(SensorHelperListener sensorlistener) {
        if (sensorlistener != null)
            mSensorListeners.add(sensorlistener);
    }

    /**
     * 反注册传感器监听
     * @param sensorlistener
     */
    public void unregisterSensorListener(SensorHelperListener sensorlistener) {
        if (sensorlistener != null)
            mSensorListeners.remove(sensorlistener);
    }

    /**
     * 停止所有注册时间
     */
    public void stopAndUnregisterAll() {
        stopSensor();
        if (mSensorListeners != null) {
            mSensorListeners.clear();
        }
        if (mScreenRotationListeners != null) {
            mScreenRotationListeners.clear();
        }
    }

    /**
     * 传感器接口
     */
    public abstract interface SensorHelperListener {

        /**
         * 传感器变化时候回调
         * @param azimuth    azimuth 方位角：就是绕z轴转动的角度
         * @param pitch      仰俯,绕Y轴转动
         * @param roll       滚转,绕Y轴转动
         */
        public abstract void sensorChanged(float azimuth, float pitch,
                                           float roll);
    }
}
