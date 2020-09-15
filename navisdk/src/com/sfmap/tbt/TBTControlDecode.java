package com.sfmap.tbt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.navi.NaviListener;
import com.sfmap.api.navi.model.NaviGuide;
import com.sfmap.api.navi.model.NaviLink;
import com.sfmap.api.navi.model.NaviPath;
import com.sfmap.api.navi.model.NaviStep;
import com.sfmap.api.navi.model.NaviTrafficStatus;
import com.sfmap.api.navi.model.NaviAvoidBound;
import com.sfmap.api.navi.model.NaviGuideWrapper;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.api.navi.model.NaviResultDecode;
import com.sfmap.api.navi.model.TrafficStatus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

//import com.sfmap.mapcore.ExceptionUtil;
import com.sfmap.tbt.util.AppInfo;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tbt.util.SDKInfo;
import com.sfmap.tbt.util.AuthManager;
import com.sfmap.tbt.util.ConfigableConst;

import org.greenrobot.eventbus.EventBus;

public class TBTControlDecode {
    private String TAG = TBTControlDecode.class.getSimpleName();
    private static TBTControlDecode tbtControlInstance;
    public TBT tbtControl;
    public TPoolDecode threadPool;
    public NaviResultDecode naviResult;
    public int naviModel = -1;
    public Object lock = new Object();
    List<NaviTrafficStatus> trafficStaus = new ArrayList<NaviTrafficStatus>();
    private Context context;
    private FrameForTBT frameForTBT;
    private boolean initOver = false;
    private NaviLocationListener naviLocationListener;
    private int engineType = 0;
    private NaviOperDecode naviOper;
    private HashMap<Integer, NaviPath> naviPaths;
    private Thread authThread;
    private LooperHandler looperHandler;
    private int routeFlag;

    private NaviLatLng endPoint;//endPoint
    private List<NaviLatLng> wayPoints;
    //算路协议版本,默认为2.2  1:2.0版本;    2:2.1版本;    3:2.2版本   5 2.5  7 2.5.2 8 2.5.3
    private int routeProtocol = 13;//2.5.8
    private double v = 0.0D;
    private double w = 0.0D;
    private List<NaviGuide> naviGuideList = new ArrayList<NaviGuide>();
    private boolean gpsReady = false;
    private Method A;
    public int routeId;
    public static boolean isSwithRoute;
    private static String deviceID = "";
    private Location lastLocation;

    public boolean isGPSReady() {
        return this.gpsReady;
    }

    private TBTControlDecode(Context context) {
        this.context = context;
        this.tbtControl = new TBT();
        this.frameForTBT = new FrameForTBT(this.context, this);
        this.deviceID = DeviceIdManager.getDeviceID(context);
        this.naviLocationListener = NaviLocationListener.singleton(this.context, this);
        if (Looper.myLooper() == null)
            this.looperHandler = new LooperHandler(this.context, this.context.getMainLooper());
        else
            this.looperHandler = new LooperHandler(this.context);

    }

    public static synchronized TBTControlDecode getInstance(Context paramContext) {
        if (tbtControlInstance == null)
            tbtControlInstance = new TBTControlDecode(paramContext);

        return tbtControlInstance;
    }

    public HashMap<Integer, NaviPath> getNaviPaths() {
        return this.naviPaths;
    }

    public int getEngineType() {
        return this.engineType;
    }

    /*
        拼接15个随机数成字符串
     */
    private static String getRandomString() {
        Random localRandom = new Random();
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i1 = 0; i1 < 15; ++i1) {
            int i2 = localRandom.nextInt(10);
            localStringBuilder.append(i2);
        }

        return localStringBuilder.toString();
    }

    public void init() {

        //启动SDK鉴权线程
        verifyKey();

        ResourcesUtil.init(this.context);
        if (this.initOver)
            return;

        if (this.context == null)
            return;

        if (this.tbtControl == null)
            this.tbtControl = new TBT();


        if (this.frameForTBT == null)
            this.frameForTBT = new FrameForTBT(this.context, this);


        String str1 = "";
        this.naviOper = this.tbtControl;
        try {
            TelephonyManager localTelephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

            if (localTelephonyManager != null)
                str1 = localTelephonyManager.getSimSerialNumber();

            if ((str1 == null) || ("".equals(str1)))
                str1 = localTelephonyManager.getDeviceId();

            if ((str1 == null) || ("".equals(str1)))
                str1 = getUIDFromPreference(this.context);

            if ((str1 == null) || ("".equals(str1))) {
                str1 = getRandomString();
                saveUIDToPreference(this.context, str1);
            }
            if ((str1 == null) || ("".equals(str1)))
                str1 = "000000000000000";
        } catch (Exception localException) {
            localException.printStackTrace();
            if ((str1 == null) || ("".equals(str1))) {
                str1 = getRandomString();
                saveUIDToPreference(this.context, str1);
            }

        }


        //init tbt engine
        String basePath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + AppInfoUtilDecode.CONFIT_KEY_COMPANY; //导航引擎基础资源根路径

        tbtControl.setParam("SimNaviNmeaPath", "/nmea/gps.nmea");
//		tbtControl.setParam("LocalNaviPath","/localNavi");//本地算路初始参数
        int enginInitResult = this.tbtControl.init(this.frameForTBT, basePath, "", " ", str1);

        //设置驾车路径服务地址
//        this.tbtControl.setParam("RouteServiceAddress", "/mobile/route/car");
        this.tbtControl.setParam("RouteServiceAddress", "/navi");
        //设置路口放大图服务路径
//        this.tbtControl.setParam("CrossServiceAddress", "/mobile/route/crossicon");

        //设置协议版本, 对于SDK，默认要使用1。2代表使用1.3引擎以上。如果使用5则代表使用了最新的2.5版本协议
        //设置协议版本, 对于SDK，默认要使用1。2代表使用1.3引擎以上。如果使用5则代表使用了最新的2.5版本协议
//		this.tbtControl.setParam("RouteProtocol","2");
        //测试tmc功能
        this.tbtControl.setParam("passport", "100000"); //顺丰货车有通行证，默认设置成100000
        this.tbtControl.setParam("RouteProtocol", Integer.toString(routeProtocol));
//        this.tbtControl.setParam("TmcServiceAddress", "");
        this.tbtControl.setParam("TmcServiceAddress", AppInfo.getSfRouteTmcURL(context));
//        setAuthFilePath(basePath);//设置默认鉴权路径
        //wenbaolin 20180720 屏蔽，据说速度会快很多
//        setLocalDataPath(basePath + "/localnavi");
//        String serviceKey = "2dce4f7be9294f1bbf280dbbd1355a02";
        String serviceKey = AppInfo.getSystemAk(this.context);
        if (serviceKey == null || "请输入您的key".equals(serviceKey)) {
            serviceKey = "";
        }
        int keySetResult = this.tbtControl.setParam("RouteServiceKey", serviceKey);

//        if ((enginInitResult == 0) || (keySetResult == 0)) {
//            this.frameForTBT.initFailure();
//            return;
//        }
        this.frameForTBT.initSuccess();
        initTBT();

        this.threadPool = TPoolDecode.getInstance(2);
        this.initOver = true;

        startGPS();

        byte[] datdata = DeviceIdManager.getDeviceIDDat(basePath);
        if (datdata == null) {
            DeviceIdManager.saveDeviceID(basePath, deviceID);
        }


    }

    public void setRouteProtocol(int number) {
        this.routeProtocol = number;
        this.tbtControl.setParam("RouteProtocol", Integer.toString(routeProtocol ));
    }

    public void setLocalDataPath(String path) {
        tbtControl.setParam("LocalNaviPath", path);//本地算路初始参数
    }

    public void setAuthFilePath(String path) {
        if (path == null || "".equals(path))
            return;
        path += "/product.sn";
//        if (this.tbtControl != null)
//            this.tbtControl.setParam("ProductSNNamePath", path);
    }

    public GpsStatus getGpsStatus() {
        if (naviLocationListener == null)
            return null;
        return naviLocationListener.getGpsStatus();
    }

    public void setGpsStatusListener(GpsStatus.Listener listener) {
        if (naviLocationListener != null)
            this.naviLocationListener.setGpsStatusListener(listener);
    }

    public void removeGpsStatusListener(GpsStatus.Listener listener) {
        if (naviLocationListener != null)
            this.naviLocationListener.removeGpsStatusListener(listener);
    }

    public void setLocationListener(LocationListener listener) {
        this.naviLocationListener.setLocationListener(listener);
    }

    /**
     * 鉴权线程
     */
    private void verifyKey() {
        try {
            this.authThread = new Thread() {
                @Override
                public void run() {
                    try {
                        //判断网络是否可用
                        while (!TBTControlDecode.this.isNetworkAvailable(TBTControlDecode.this.context)) {
                            Thread.sleep(5000L);
                        }

                        //鉴权
                        SDKInfo sdkInfo = new SDKInfo.init(ConfigableConst.product, "1.0.0", ConfigableConst.desc).getSDKInfo();
                        boolean success = AuthManager.getKeyAuth(context, sdkInfo);
                        if (success) {
                            int n = 0;
                        }
                    } catch (Throwable throwable) {

                    }
                }

            };
            this.authThread.setName("AuthThread");
            this.authThread.start();
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            NaviUtilDecode.a(localThrowable);
        }
    }


    /*
    设置导航路线
     */
    public int getRouteInfos(int[] routeIds) {
        if (this.naviOper != null) {
            this.naviPaths = new HashMap<Integer, NaviPath>();
            for (int i = routeIds.length-1; i >=0;  i--) {
                LogUtil.d(TAG,"routeIds.length:"+routeIds.length);
                int selResult = this.naviOper.selectRoute(routeIds[i]);
                if (selResult == i) {
                    this.naviPaths.put(Integer.valueOf(routeIds[i]), this.getNaviResult().naviPath);
                }
            }
            return 1;
        }
        return 0;
    }

    private void initTBT() {
        if (this.tbtControl == null) {
            return;
        }

        this.tbtControl.closeTrafficPanel();

        this.tbtControl.openTrafficRadio();

        this.tbtControl.openTMC();

        this.tbtControl.openCamera();

        this.tbtControl.setCrossDisplayMode(NaviUtilDecode.crossDisplayMode);

        this.tbtControl.setTMCRerouteStrategy(1);
    }

    public void initLocationManager() {
        LocationManager localLocationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

        if (localLocationManager.isProviderEnabled("gps"))
            this.frameForTBT.onGpsOpenStatus(true);
        else
            this.frameForTBT.onGpsOpenStatus(false);
    }

    public synchronized void releaseResource() {
        this.looperHandler.removeCallbacksAndMessages(null);
        synchronized (this.lock) {
            if (this.tbtControl != null) {
                tbtControl.destroy();
                tbtControl = null;
            }
        }
        if (this.threadPool != null)
            TPoolDecode.onDestroy();
        if (this.naviLocationListener != null) {
            this.naviLocationListener.removeListener();
        }

        this.authThread = null;
        this.deviceID = null;
        tbtControlInstance = null;
        this.frameForTBT.routeDestroy();
        this.frameForTBT.destroy();
        this.frameForTBT = null;
    }

    public boolean startNavi(int naviType) {
        int i1;
        if (this.naviOper == null)
            return false;

        if (naviType == 1) { //启动实时导航
            NaviUtilDecode.a("startNavi");
            startGPS();

            i1 = (this.naviOper.startGPSNavi() == 1) ? 1 : 0;
            if (i1 != 0)
                if (this.frameForTBT != null) {
                    this.frameForTBT.onStartNavi(naviType);
                    LogUtil.d(TAG,"frameForTBT.onStartNavi");
                } else
                    return false;
        }

        if (naviType == 2) { //启动模拟导航
            i1 = (this.naviOper.startEmulatorNavi() == 1) ? 1 : 0;
            if (i1 != 0)
                if (this.frameForTBT != null) {
                    this.frameForTBT.onStartNavi(naviType);
                } else
                    return false;
        }

        this.naviModel = naviType;
        return true;
    }

    public int getNaviType() {
        return this.naviModel;
    }

    public void pauseNavi() {
        if (this.naviOper != null)
            this.naviOper.pauseNavi();
    }

    public void stopNavi() {
        if (this.naviOper != null) {
            this.naviOper.stopNavi();
            stopGPS();
        }
    }

    public void resumeNavi() {
        if (this.naviOper != null)
            this.naviOper.resumeNavi();
    }

    public void manualRefreshTMC() {
        if (this.naviOper != null)
            this.naviOper.manualRefreshTMC();
    }

    public boolean playNaviManual() {
        if (this.naviOper != null)
            return (this.naviOper.playNaviManual() == 1);

        return false;
    }

    public boolean playTrafficRadioManual(int frontDistance) {
        if (this.naviOper != null) {
            return (this.naviOper.playTrafficRadioManual(frontDistance) == 1);
        }

        return false;
    }

    /**
     * 驾车算路
     *
     * @param from      起点
     * @param to        终点
     * @param wayPoints 途经点
     * @param strategy  策略
     * @param isLocal   是否为本地算路
     * @return
     */
    public boolean calculateDriveRoute(List<NaviLatLng> from, List<NaviLatLng> to, List<NaviLatLng> wayPoints, int strategy, boolean isLocal, String avoidRoadName, List<NaviAvoidBound> naviAvoidBounds) {
        this.wayPoints = wayPoints;
        if ((this.tbtControl != null) && (from != null) && (to != null) && (!to.isEmpty())) {
            NaviLatLng localNaviLatLng;
            this.engineType = 0;
            int fromSize = from.size();
            int toSize = to.size();
            int passSize = 0;
            if (wayPoints != null)
                passSize = wayPoints.size();

            double[] fromLngLatValues = new double[fromSize << 1];
            double[] toLngLatValues = new double[toSize << 1];
            double[] passLngLatValues = null;
            if (passSize != 0)
                passLngLatValues = new double[passSize << 1];

            int openIndex = 0;
            Iterator<NaviLatLng> localIterator = from.iterator();
            while ( localIterator.hasNext() ) {
                localNaviLatLng = localIterator.next();
                if (localNaviLatLng != null) {
                    fromLngLatValues[(openIndex << 1)] = localNaviLatLng.getLongitude();
                    fromLngLatValues[((openIndex << 1) + 1)] = localNaviLatLng.getLatitude();
                } else {
                    return false;
                }
                ++openIndex;
            }

            openIndex = 0;
            localIterator = to.iterator();
            while ( localIterator.hasNext() ) {
                localNaviLatLng = localIterator.next();
                if (openIndex == 0)
                    this.endPoint = localNaviLatLng;

                if (localNaviLatLng != null) {
                    toLngLatValues[(openIndex << 1)] = localNaviLatLng.getLongitude();
                    toLngLatValues[((openIndex << 1) + 1)] = localNaviLatLng.getLatitude();
                } else {
                    return false;
                }
                ++openIndex;
            }
            if (passSize != 0) {
                openIndex = 0;

                for (localIterator = wayPoints.iterator(); localIterator.hasNext(); ) {
                    localNaviLatLng = (NaviLatLng) localIterator.next();
                    if (localNaviLatLng != null) {
                        passLngLatValues[(openIndex << 1)] = localNaviLatLng.getLongitude();
                        passLngLatValues[((openIndex << 1) + 1)] = localNaviLatLng.getLatitude();
                    } else {
                        return false;
                    }
                    ++openIndex;
                }
            }
            this.naviOper = this.tbtControl;

            float forwardDir = -1f;
            if(null != lastLocation){
                forwardDir = lastLocation.getBearing();
            }
            int flag = 0;
            if ((strategy == 3) || (strategy == 12))
                flag = 1;
            int result ;
            if (avoidRoadName != null && !"".equals(avoidRoadName)) {
                //带避让道路算路
                result = this.tbtControl.requestRouteWithAvoidRoad(strategy, flag,forwardDir, fromSize, fromLngLatValues, toSize, toLngLatValues, passSize, passLngLatValues, avoidRoadName);
            } else if (naviAvoidBounds != null && !naviAvoidBounds.isEmpty()) {
                //带避让区域算路
                MPolygon[] mPolygons = new MPolygon[naviAvoidBounds.size()];
                for (int j = 0; j < naviAvoidBounds.size(); j++) {
                    NaviAvoidBound naviAvoidBound = naviAvoidBounds.get(j);
                    MPolygon mPolygon = new MPolygon();
                    mPolygon.pointCount = naviAvoidBound.getCoords().size();
                    float[] Xs = new float[mPolygon.pointCount];
                    float[] Ys = new float[mPolygon.pointCount];
                    for (int i = 0; i < mPolygon.pointCount; i++) {
                        NaviLatLng naviLatLng = naviAvoidBound.getCoords().get(i);
                        Xs[i] = (float) naviLatLng.getLongitude();
                        Ys[i] = (float) naviLatLng.getLatitude();
                    }
                    mPolygon.latitudes = Ys;
                    mPolygon.longitudes = Xs;
                    mPolygons[j] = mPolygon;
                }
                result = this.tbtControl.requestRouteWithAvoidAreas(strategy, flag, forwardDir,fromSize, fromLngLatValues, toSize, toLngLatValues, passSize, passLngLatValues, mPolygons.length, mPolygons);
            } else {
                if (isLocal)
                    flag = 0x100;//本地算路
                //普通算路

                result = this.tbtControl.requestRouteWithStart(strategy, flag, forwardDir, fromSize, fromLngLatValues, toSize, toLngLatValues, passSize, passLngLatValues);
            }
            this.routeFlag = flag;
            return (result == 1);
        }
//        Log.i(TAG,"calculateDriveRoute Time After:"+System.currentTimeMillis());
        return false;
    }

    public boolean calculateDriveRoute(List<NaviLatLng> to, List<NaviLatLng> wayPoints, int strategy, boolean isLocal) {
        this.wayPoints = wayPoints;
        if ((this.tbtControl != null) && (to != null) && (this.gpsReady)) {
            NaviLatLng localNaviLatLng;
            this.engineType = 0;
            int i1 = 0;
            i1 = to.size();
            int i2 = 0;
            if (wayPoints != null)
                i2 = wayPoints.size();
            double[] arrayOfDouble1 = null;
            if (i2 != 0) {
                arrayOfDouble1 = new double[i2 << 1];
            }
            double[] arrayOfDouble2 = new double[i1 << 1];
            int i3 = 0;
            for (Iterator localIterator = to.iterator(); localIterator.hasNext(); ) {
                localNaviLatLng = (NaviLatLng) localIterator.next();
                if (i3 == 0)
                    this.endPoint = localNaviLatLng;
                if (localNaviLatLng != null) {
                    arrayOfDouble2[(i3 << 1)] = localNaviLatLng.getLongitude();
                    arrayOfDouble2[((i3 << 1) + 1)] = localNaviLatLng.getLatitude();
                } else {
                    return false;
                }
                ++i3;
            }
            if (i2 != 0) {
                i3 = 0;
                Iterator<NaviLatLng> localIterator = wayPoints.iterator();
                for (; localIterator.hasNext(); ) {
                    localNaviLatLng = (NaviLatLng) localIterator.next();
                    if (localNaviLatLng != null) {
                        arrayOfDouble1[(i3 << 1)] = localNaviLatLng.getLongitude();
                        arrayOfDouble1[((i3 << 1) + 1)] = localNaviLatLng.getLatitude();
                    } else {
                        return false;
                    }
                    ++i3;
                }
            }
            this.naviOper = this.tbtControl;
            int i4 = 0;
            if ((strategy == 3) || (strategy == 12)) {
                i4 = 1;
            }
            if (isLocal) i4 = 0x100;
            this.routeFlag = i4;
            float forwardDir = -1f;
            if(null != lastLocation){
                forwardDir = lastLocation.getBearing();
            }
            return (this.tbtControl.requestRoute(strategy, i4,forwardDir, i1, arrayOfDouble2, i2, arrayOfDouble1) == 1);
        }
        return false;
    }

    /*
    更新自车位置
     */
    public void updateCarLocation(int paramInt, double paramDouble1, double paramDouble2, float forwardDir) {
        this.v = paramDouble1;
        this.w = paramDouble2;
        if (this.tbtControl != null)
            this.tbtControl.setCarLocation(paramInt, paramDouble1, paramDouble2,forwardDir);

    }

    public boolean reCalculateRoute(int strategy) {
        if (this.naviOper != null)
            return (this.naviOper.reroute(strategy, this.routeFlag) == 1);
        return false;
    }

    public List<NaviTrafficStatus> getTrafficStatuses(int startPos, int distance) {
        LogUtil.d(TAG,"getTrafficStatuses1");
        if ((this.engineType == 0) && (this.tbtControl != null)) {
            LogUtil.d(TAG,"getTrafficStatuses2"+" startPos:"+startPos+" distance:"+distance);
            TmcBarItem[] arrayOfTmcBarItem = this.tbtControl.createTmcBar(startPos, distance);
            LogUtil.d(TAG,"getTrafficStatuses3");
            if ((arrayOfTmcBarItem != null) && (arrayOfTmcBarItem.length > 0)) {
                LogUtil.d(TAG,"getTrafficStatuses4");
                this.trafficStaus.clear();
                for (int i1 = 0; i1 < arrayOfTmcBarItem.length; ++i1) {
                    TrafficStatus locale = new TrafficStatus(arrayOfTmcBarItem[i1]);
                    this.trafficStaus.add(locale.a);
                }
                LogUtil.d(TAG,"getTrafficStatuses6");
                return this.trafficStaus;
            }else {
                this.trafficStaus.clear();
                TmcBarItem tmcBarItem = new TmcBarItem();
                tmcBarItem.m_Length = distance;
                tmcBarItem.m_Status = 1;
                NaviTrafficStatus naviTrafficStatus = new NaviTrafficStatus(tmcBarItem);
                this.trafficStaus.add(naviTrafficStatus);
                LogUtil.d(TAG,"getTrafficStatuses7");
                return this.trafficStaus;
            }
        }
        LogUtil.d(TAG,"getTrafficStatuses8");
        return this.trafficStaus;
    }

    public NaviPath getNaviPath() {
        if (this.naviResult != null)
            return this.naviResult.naviPath;

        return null;
    }

    public List<NaviGuide> getNaviGuideList() {
        NaviGuideItem[] naviGuideItemArray ;
        int i1;
        NaviGuideWrapper naviGuideWrapper;
        if ((this.engineType == 0) && (this.tbtControl != null)) {
            naviGuideItemArray = this.tbtControl.getNaviGuideList();
            if ((naviGuideItemArray != null) && (naviGuideItemArray.length > 0)) {
                this.naviGuideList.clear();
                for (i1 = 0; i1 < naviGuideItemArray.length; ++i1) {
                    naviGuideWrapper = new NaviGuideWrapper(naviGuideItemArray[i1]);
                    this.naviGuideList.add(naviGuideWrapper.naviGuide);
                }
                return this.naviGuideList;
            }
        }
        return null;
    }

    public TBT getTBT() {
        if (this.tbtControl == null)
            this.tbtControl = new TBT();

        return this.tbtControl;
    }

    public void setEmulatorNaviSpeed(int speed) {
        if (this.naviOper != null)
            this.naviOper.setEmulatorSpeed(speed);
    }

    public void setTimeForOneWord(int time) {
        if (this.tbtControl != null)
            this.tbtControl.setTimeForOneWord(time);
    }

    public void setMapNaviListener(NaviListener naviListener) {
        if (this.frameForTBT != null)
            this.frameForTBT.setMapNaviListener(naviListener);
    }

    public void removeMapNaviListener(NaviListener naviListener) {
        if (this.frameForTBT != null)
            this.frameForTBT.removeNaviListener(naviListener);
    }

    public void startGPS() {
        if (this.naviLocationListener != null)
            this.naviLocationListener.startGPS();
    }

    public void startGPS(long time, int dis) {
        if (this.naviLocationListener != null)
            this.naviLocationListener.startGPS(time, dis);
    }

    public void stopGPS() {
        if (this.naviLocationListener != null)
            this.naviLocationListener.stopGPS();
    }

    public boolean saveGpsNmea(boolean enable, String nmeaFileName) {
        if (this.naviLocationListener != null)
            return this.naviLocationListener.saveGpsNmea(enable, nmeaFileName);
        return false;
    }

    public boolean isSaveGpsNmea() {
        if (this.naviLocationListener != null)
            return this.naviLocationListener.isSaveGpsNmea();
        return false;
    }

    public int selectRouteId(int id) {
        if (this.naviOper != null) {
            int result = this.naviOper.selectRoute(id);
            if (result == id) {
                getNaviResult();
                return 1;
            }
        }
        return -1;
    }

    public int[] getAllRouteID() {
        int[] arrayOfInt = this.naviOper.getAllRouteID();
        return arrayOfInt;
    }

    private NaviResultDecode getNaviResult() {
        this.naviResult = new NaviResultDecode();
        this.naviResult.setRouteLength(this.naviOper.getRouteLength());
        this.naviResult.setRouteTime(this.naviOper.getRouteTime());
        this.naviResult.setSegNum(this.naviOper.getSegNum());
        this.naviResult.setEndPoint(this.endPoint);
        this.naviResult.setWayPoints(this.wayPoints);
        this.naviResult.setByPassLinitedRoad(this.naviOper.getBypassLimitedRoad());
        this.naviResult.setRouteIncident(this.naviOper.getRouteIncident());
        this.naviResult.setAvoidJamArea(this.naviOper.getAvoidJamArea());
        this.naviResult.setJamInfoList(this.naviOper.getJamInfoList());
        this.naviResult.setGroupSegmentList(this.naviOper.getGroupSegmentList());
        this.naviResult.setDiffToTMCRoute(this.naviOper.getDiffToTMCRoute());
        int routeStrategy = this.naviOper.getRouteStrategy();
        if (routeStrategy == 5)
            this.naviResult.setStrategy(3);
        else
            this.naviResult.setStrategy(routeStrategy);
        int segmentNum = this.naviOper.getSegNum();
        ArrayList<NaviStep> stepList = new ArrayList<NaviStep>();
        ArrayList<NaviLatLng> routeCoordList = new ArrayList<NaviLatLng>();
        int tollCost = 0;
        int stepCoordStartIndex = -1;
        int ii = 0;
        double maxLat = 4.9E-324D;
        double minLat = 1.7976931348623157E+308D;
        double maxLng = 4.9E-324D;
        double minLng = 1.7976931348623157E+308D;
        if (this.naviResult.getWayPoints() != null) {
            int i4 = this.naviResult.getWayPoints().size();
            this.naviResult.naviPath.wayPointIndex = new int[i4];
        }

        for (int segIndex = 0; segIndex < segmentNum; ++segIndex) {
            NaviStep naviStep = new NaviStep();
            naviStep.setChargeLength(this.naviOper.getSegChargeLength(segIndex));
            naviStep.setSegNaviAction(this.naviOper.getSegNaviAction(segIndex));
            tollCost += this.naviOper.getSegTollCost(segIndex);
            naviStep.setTime(this.naviOper.getSegTime(segIndex));
            double[] segCoords = this.naviOper.getSegCoor(segIndex);
            ArrayList<NaviLatLng> lngLatList = new ArrayList<NaviLatLng>();
            if (segCoords != null) {
                for (int segCoordIndex = 0; segCoordIndex < segCoords.length - 1; segCoordIndex += 2) {
                    NaviLatLng lnglat = new NaviLatLng(segCoords[(segCoordIndex + 1)], segCoords[segCoordIndex]);
                    lngLatList.add(lnglat);
                }
            }

            naviStep.setCoords(lngLatList);
            naviStep.setLength(this.naviOper.getSegLength(segIndex));
            ArrayList linkList = new ArrayList();
            int linkNum = this.naviOper.getSegLinkNum(segIndex);
            naviStep.setStartIndex(stepCoordStartIndex + 1);
            for (int linkIndex = 0; linkIndex < linkNum; ++linkIndex) {
                NaviLink mapNaviLink = new NaviLink();
                mapNaviLink.setLength(this.naviOper.getLinkLength(segIndex, linkIndex));
                mapNaviLink.setTime(this.naviOper.getLinkTime(segIndex, linkIndex));
                mapNaviLink.setRoadClass(this.naviOper.getLinkRoadClass(segIndex, linkIndex));
                mapNaviLink.setRoadType(this.naviOper.getLinkFormWay(segIndex, linkIndex));
                mapNaviLink.setRoadName(this.naviOper.getLinkRoadName(segIndex, linkIndex));
                mapNaviLink.setTrafficLights(this.naviOper.haveTrafficLights(segIndex, linkIndex) == 1 ? true : false);
                double[] linkCoords = this.naviOper.getLinkCoor(segIndex, linkIndex);
                ArrayList<NaviLatLng> linkCoordList = new ArrayList<NaviLatLng>();
                for (int linkCoordIndex = 0; linkCoordIndex < linkCoords.length - 1; linkCoordIndex += 2) {
                    double latValue = linkCoords[(linkCoordIndex + 1)];
                    double lngValue = linkCoords[linkCoordIndex];
                    if (maxLat < latValue)
                        maxLat = latValue;
                    if (maxLng < lngValue)
                        maxLng = lngValue;
                    if (minLat > latValue)
                        minLat = latValue;
                    if (minLng > lngValue)
                        minLng = lngValue;
                    NaviLatLng linkLngLat = new NaviLatLng(latValue, lngValue);
                    linkCoordList.add(linkLngLat);
                    routeCoordList.add(linkLngLat);
                    ++stepCoordStartIndex;
                }
                mapNaviLink.setCoords(linkCoordList);
                linkList.add(mapNaviLink);
            }
            naviStep.setEndIndex(stepCoordStartIndex);
            if (this.naviResult.getWayPoints() != null) {
                NaviAction localNaviAction = this.tbtControl.getSegNaviAction(segIndex);
                if (localNaviAction.m_AssitAction == 35) {
                    this.naviResult.naviPath.wayPointIndex[ii] = stepCoordStartIndex;
                    ++ii;
                }
            }
            naviStep.setLink(linkList);
            stepList.add(naviStep);
        }

        this.naviResult.b().setLatitude(maxLat);
        this.naviResult.b().setLongitude(maxLng);
        this.naviResult.c().setLatitude(minLat);
        this.naviResult.c().setLongitude(minLng);
        this.naviResult.setTollCost(tollCost);
        this.naviResult.setSteps(stepList);
        if ((routeCoordList != null) && (!routeCoordList.isEmpty())) {
            NaviLatLng localNaviLatLng1 = (NaviLatLng) routeCoordList.get(0);
            this.naviResult.setStartPoint(localNaviLatLng1);
            if (this.endPoint == null)
                this.naviResult.setEndPoint((NaviLatLng) routeCoordList.get(routeCoordList.size() - 1));
        }
        this.naviResult.setCoordList(routeCoordList);
        NaviLatLng localNaviLatLng1 = NaviUtilDecode.a(this.naviResult.c()
                .getLatitude(), this.naviResult.c().getLongitude(), this.naviResult.b()
                .getLatitude(), this.naviResult.b().getLongitude());
        try {
            LatLng localObject11 = new LatLng(this.naviResult.c().getLatitude(), this.naviResult.c().getLongitude());

            LatLng localObject22 = new LatLng(this.naviResult.b().getLatitude(), this.naviResult.b().getLongitude());

            LatLngBounds localObject33 = new LatLngBounds((LatLng) localObject11, (LatLng) localObject22);
            this.naviResult.setBounds((LatLngBounds) localObject33);
        } catch (Exception localThrowable) {
//            ExceptionUtil.exceptionLogPrint(localThrowable);
        }
        this.naviResult.setCenter(localNaviLatLng1);
        return ((NaviResultDecode) this.naviResult);
    }

    public void setGpsInfo(int offsetFlag, Location paramLocation) {
        lastLocation = paramLocation;
        this.gpsReady = true;
        Calendar localCalendar = Calendar.getInstance(Locale.CHINA);
        localCalendar.setTimeInMillis(paramLocation.getTime());
        int year = localCalendar.get(Calendar.YEAR);
        int month = localCalendar.get(Calendar.MONTH) + 1;
        int date = localCalendar.get(Calendar.DATE);
        int day = localCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = localCalendar.get(Calendar.MINUTE);
        int second = localCalendar.get(Calendar.SECOND);
        LogUtil.d(TAG,"offsetFlag:"+offsetFlag+"Longitude:"+paramLocation.getLongitude()+"Latitude:"+paramLocation.getLatitude()+"Speed:"+paramLocation.getSpeed() * 3.6D + "Bearing:"+paramLocation.getBearing()+"year:"+year+"month:"+month+"date:"+date+"day:"+day+"minute:"+minute+"second:"+second);
        NaviUtilDecode.a(new Object[]{"year=" + year + "hour=" + day + "minute=" + minute + "second=" + second});
        if (this.engineType == 0) {
            this.tbtControl.setGPSInfo(offsetFlag, paramLocation.getLongitude(), paramLocation.getLatitude(), paramLocation.getSpeed() * 3.6D, paramLocation.getBearing(), year, month, date, day, minute, second);
        }
    }

    public void setReCalculateRouteForYaw(boolean isReroute) {
        if (this.frameForTBT != null)
            this.frameForTBT.setReCalculateRouteForYaw(isReroute);
    }

    public void setReCalculateRouteForTrafficJam(boolean isReroute) {
        if (this.frameForTBT != null)
            this.frameForTBT.setReCalculateRouteForTrafficJam(isReroute);
    }

    public void clearRouteResult() {
        this.naviResult = null;
    }

    public void enableTrafficRadio(boolean paramBoolean) {
        if (this.tbtControl != null)
            if (paramBoolean)
                this.tbtControl.openTrafficRadio();
            else
                this.tbtControl.closeTrafficRadio();
    }

    public void enabelCameraInfoUpdate(boolean enableUpdate) {
        if (this.tbtControl != null)
            if (enableUpdate)
                this.tbtControl.openCamera();
            else
                this.tbtControl.closeCamera();
    }


    public int b(int paramInt1, int paramInt2) {
        return this.tbtControl.switchNaviRoute(paramInt1);
    }

    private String getUIDFromPreference(Context paramContext) {
        try {
            SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("navigation_uid", 0);
            String str = localSharedPreferences.getString("uid", "");
            return str;
        } catch (Exception localThrowable) {
//            ExceptionUtil.exceptionLogPrint(localThrowable);
        }
        return null;
    }

    void saveUIDToPreference(Context paramContext, String paramString) {
        try {
            SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("navigation_uid", 0);
            SharedPreferences.Editor localEditor = localSharedPreferences.edit();
            localEditor.putString("uid", paramString);
            a(localEditor);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }

    private void a(SharedPreferences.Editor paramEditor) {
        if (paramEditor == null)
            return;

        if (Build.VERSION.SDK_INT >= 9)
            try {
                if (this.A == null) {
                    Class<Editor> localEditor = SharedPreferences.Editor.class;
                    this.A = localEditor.getDeclaredMethod("apply",
                            new Class[0]);
                }
                this.A.invoke(paramEditor, new Object[0]);
            } catch (Throwable localThrowable) {
                localThrowable.printStackTrace();
                paramEditor.commit();
            }
        else
            paramEditor.commit();
    }

    public NaviInfo getNaviInfo() {
        return this.frameForTBT.getNaviInfo();
    }

    public void setDetectedMode(int detectedMode) {
        this.engineType = 0;
        this.naviOper = this.tbtControl;
        if (this.tbtControl != null)
            this.tbtControl.setDetectedMode(detectedMode);

        startGPS();
    }

    public RestrictionArea[] getRestrictionInfo() {
        if (this.tbtControl != null){
            return this.tbtControl.getRestrictionInfo();
        }
        return null;
    }

    public String GetRouteUID(int type) {
        if (this.tbtControl != null){
            return this.tbtControl.GetRouteUID(type);
        }
        return null;
    }

    public String getDecodeRouteError() {
        if (this.tbtControl != null){
            return this.tbtControl.getDecodeRouteError();
        }
        return null;
    }

    public int getSegTollCost() {
        if (this.tbtControl != null){
            return this.tbtControl.getSegTollCost(0);
        }
        return 0;
    }


    static class LooperHandler extends Handler {
        private WeakReference<Context> contextReference = null;

        public LooperHandler(Context paramContext, Looper paramLooper) {
            super(paramLooper);
            this.contextReference = new WeakReference(paramContext);
            try {
                Looper.prepare();
            } catch (Throwable localThrowable) {
                localThrowable.printStackTrace();
            }
        }

        public LooperHandler(Context paramContext) {
            this.contextReference = new WeakReference(paramContext);
        }

        public void handleMessage(Message paramMessage) {
            try {
                String str = (String) paramMessage.obj;
                Context localContext = (Context) this.contextReference.get();
                if (localContext != null)
                    Toast.makeText(localContext, str, Toast.LENGTH_SHORT).show();
            } catch (Throwable localThrowable) {
                localThrowable.printStackTrace();
            }
        }
    }


    /**
     * 检查当前网络是否可用
     *
     * @param context 设备上下文
     * @return true 代表网络可用，false代表网络不可用
     */
    private boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int isSwithRoute(int segmentID, int linkID) {
        int linkAttri = tbtControl.getLinkAttributeInfo(segmentID, linkID);//1,3,4,12 //普通路 0x00,高架路上	0x01,高架路下	0x02,主路	0x03,辅路	0x04
        // 主辅路
        if ((linkAttri & 1) > 0) {
            if ((linkAttri & 2) > 0) {
                routeId = 4; //当前在主路上，希望算到辅路上去！
            } else {
                routeId = 3;  //当前在辅路上，希望算到主路上去！
            }
            return routeId;
        }
        // 高架-非高架
        if ((linkAttri & 4) > 0) {
            if ((linkAttri & 8) > 0) {
                routeId = 2; //当前在高架上，希望算到高架下去！
            } else {
                routeId = 1;    //当前在高架下，希望算到高架上去
            }
            return routeId;
        }
        return 0;
    }

    public void swithRoute() {
        if (this.tbtControl != null && routeId > 0) {
            this.tbtControl.switchParallelRoad(routeId);
            if (routeId != 0) isSwithRoute = true;
        }
    }


}