package com.sfmap.map.navi;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sfmap.api.maps.model.MyTrafficStyle;
import com.sfmap.api.navi.Navi;
import com.sfmap.api.navi.NaviView;
import com.sfmap.api.navi.NaviViewOptions;
import com.sfmap.api.navi.model.SatelliteEvent;
import com.sfmap.api.navi.model.CrossImgInfo;
import com.sfmap.api.navi.model.LaneImgInfo;
import com.sfmap.api.navi.model.NaviEndInfo;
import com.sfmap.api.navi.model.RouteInfo;
import com.sfmap.api.navi.model.Tracks;
import com.sfmap.api.navi.model.VoiceInfo;
import com.sfmap.api.navi.model.YawInfo;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.model.PointD;
import com.sfmap.library.util.Projection;
import com.sfmap.log.model.LogParam;
import com.sfmap.log.service.LocUploadService;
import com.sfmap.navi.R;
import com.sfmap.route.model.NaviJumpRouteEvent;
import com.sfmap.tbt.loc.GPS_FGService;
import com.sfmap.tbt.util.AppInfo;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tts.TtsManager;
import com.sfmap.util.CalcSunRiseAndSunSetTools;
import com.sfmap.util.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class NaviActivity extends NaviBaseActivity
        implements GPS_FGService.addLocationListener {
    private String TAG = NaviActivity.class.getSimpleName();
    private NaviView mNaviView;
    private Navi mNavi;
    private TtsManager ttsManager;
    //    private TTSPlayerListener ttsPlayerListener;
//    TTSPlayer player;
    private boolean emulatorNavi = false;
    private int selectRoueId = 0;
    private Location lastLoc;
    private List<Tracks> tracks = new ArrayList<>();
    private int endType = 1;
    private NaviViewOptions sfmapNaviViewOptions;
    private GPS_FGService gpsService;
    private NaviActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onCreate(savedInstanceState);
        context = this;
        EventBus.getDefault().register(this);
        emulatorNavi = getIntent().getBooleanExtra("emulatorNavi", false);
//        selectRoueId = getIntent().getIntExtra("currentItem", 0);
        ttsManager = TtsManager.getInstance(NaviActivity.this);
        setContentView(R.layout.activity_navi);
        initView(savedInstanceState);
        initNaviData();
//        initTts();//灵云
//        inittts1();//云知声

        Intent intent = new Intent(this, LocUploadService.class);
        conn = new MyConn();
        bindService(intent, conn, BIND_AUTO_CREATE);

        checkGPS();
        bindService();
        Log.d(TAG,"routeid:"+AppInfo.getRouteId());
        autoMapMode();
    }

    private void initView(Bundle savedInstanceState) {
        mNaviView = findViewById(R.id.navi_view);
        mNaviView.onCreate(savedInstanceState);
        mNaviView.setMapNaviViewListener(this);
        mNaviView.getMap().getUiSettings().setCompassEnabled(false);
        boolean trafficEnable = (boolean) SPUtils.get(this,"trafficEnable",false);
        mNaviView.getMap().setTrafficEnabled(trafficEnable);
        sfmapNaviViewOptions = mNaviView.getViewOptions();
    }

    private void initNaviData() {
        mNavi = Navi.getInstance(this);
        mNavi.addNaviListener(this);
        mNavi.setSoTimeout(15000);
        mNavi.setEmulatorNaviSpeed(100);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNavi.getNaviPath() != null) {
                    if (emulatorNavi) {
                        mNavi.startNavi(Navi.EmulatorNaviMode);
                    } else {
                        mNavi.startNavi(Navi.GPSNaviMode);
                    }

                }
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if (mNaviView.getMap() != null) {
            setTrafficStyle(1);
            setCameraDegree(0);
        }
        mNaviView.onResume();
        if (isForeground == false) {
            //由后台切换到前台
            isForeground = true;
            Log.d(TAG,"onResume1");
//            ttsManager.startSpeaking("顺丰地图持续为您导航");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mNaviView.onPause();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAppOnForeground()) {
                    //由前台切换到后台
                    isForeground = false;
                    Log.d(TAG,"onPause1");
                    ttsManager.startSpeaking("顺丰地图持续为您导航");
                }
            }
        },500);
    }

    @Override
    protected void onDestroy() {
        try {
            mNaviView.onDestroy();
            mNavi.stopNavi();
            mNavi.destroy();
            mNavi = null;
            ttsManager.destroy();
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EventBus.getDefault().post(new NaviJumpRouteEvent(AppInfo.getNaviId(),endType));
        Intent intent = new Intent(this, LocUploadService.class);
        unbindService(conn);
        stopService(intent);
        Log.d(TAG, "onDestroy()");
        EventBus.getDefault().unregister(this);
        AppInfo.clearTracks();
        super.onDestroy();
        Log.d(TAG, "super.onDestroy()");

        //注销关闭GPSService
        unbindService(connection); //this is a callback to the locationlisteneragent class to bind it to a service
        stopService(new Intent(this, GPS_FGService.class));
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        Log.d(TAG, "onGetNavigationText:" + s);
        VoiceInfo voiceInfo = new VoiceInfo();
        String voiceText = "{\"" + i + s + "\":1}";
        voiceInfo.setVoice(voiceText);

        if (myBinder != null) {
            myBinder.ReciveLog(getParam(voiceInfo));
        }
        ttsManager.startSpeaking(s);

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        Log.d(TAG,"onCalculateRouteFailure");
//        try {
//            String value = new String(mNavi.getDecodeRouteError().getBytes("gbk"),"utf-8");
////            String value = mNavi.getDecodeRouteError();
//            Toast.makeText(getApplicationContext(),"偏航规划返回码" + errorInfo,Toast.LENGTH_LONG).show();
//        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 多路线算路成功回调。
     *
     * @param routeIds - 路线id数组
     */
    @Override
    public void onCalculateMultipleRoutesSuccess(int[] routeIds) {
        if (mNavi.GetRouteUID(0) != null) {
            AppInfo.setRouteId(mNavi.GetRouteUID(0));
            LogUtil.d(TAG,"GetRouteUID:"+AppInfo.getRouteId());
        }else {
            LogUtil.d(TAG,"GetRouteUID失败null");
        }

        YawInfo yawInfo = new YawInfo();
        yawInfo.setRouteId(AppInfo.getRouteId());
        yawInfo.setHasYaw(true);
        if(myBinder != null){
            myBinder.ReciveLog(getParam(yawInfo));
        }

        VoiceInfo voiceInfo = new VoiceInfo();
        String voiceText = "{\""+1005+ "已为您重新规划路线"+"\":1}";
        voiceInfo.setVoice(voiceText);

        if(myBinder != null){
            myBinder.ReciveLog(getParam(voiceInfo));
        }
        ttsManager.startSpeaking("已为您重新规划路线");
    }


    @Override
    public void onCalculateRouteSuccess() {
        if (mNavi.GetRouteUID(0) != null) {
            AppInfo.setRouteId(mNavi.GetRouteUID(0));
            LogUtil.d(TAG,"GetRouteUID:"+AppInfo.getRouteId());
        }else {
            LogUtil.d(TAG,"GetRouteUID失败null");
        }

        YawInfo yawInfo = new YawInfo();
        yawInfo.setRouteId(AppInfo.getRouteId());
        yawInfo.setHasYaw(true);
        if(myBinder != null){
            myBinder.ReciveLog(getParam(yawInfo));
        }

        VoiceInfo voiceInfo = new VoiceInfo();
        String voiceText = "{\""+1005+ "已为您重新规划路线"+"\":1}";
        voiceInfo.setVoice(voiceText);

        if(myBinder != null){
            myBinder.ReciveLog(getParam(voiceInfo));
        }

        ttsManager.startSpeaking("已为您重新规划路线");
    }


    @Override
    public void onArrivedWayPoint(int i) {
//        sfSpeechSyntesizer.startSpeaking("到达第" + i + "个途径点");
    }

    /**
     * 导航结束
     */
    @Override
    public void onArriveDestination() {
        Log.d(TAG,"onArriveDestination");
//        ttsManager.startSpeaking("到达目的地,本次导航结束");
        sendNaviEndInfo(2);
        finish();

    }

    @Override
    public void onEndEmulatorNavi() {
        Log.d(TAG,"onEndEmulatorNavi");
        sendNaviEndInfo(2);
        finish();
    }

    /**
     * 导航结束日志上传
     * @param type
     */
    private void sendNaviEndInfo(int type){
        endType = type;

        if(myBinder != null){
            myBinder.EndNavi();
        }
        NaviEndInfo naviEndInfo = new NaviEndInfo();
        naviEndInfo.setCc(1);
        naviEndInfo.setEndType(type);
        if (lastLoc != null) {
            naviEndInfo.setX(AppInfo.getStartX());
            naviEndInfo.setY(AppInfo.getStartY());
        }

        if (myBinder != null) {
            myBinder.ReciveLog(getParam(naviEndInfo));
        }
    }

    /**
     * 导航页面左下角返回按钮点击后弹出的 "退出导航对话框" 中选择 "确定" 后的回调接口。
     */
    @Override
    public void onNaviCancel() {
        sendNaviEndInfo(1);
        finish();
    }

    private MyConn conn;
    private LocUploadService.MyBinder myBinder;//我定义的中间人对象

    //监视服务的状态
    private class MyConn implements ServiceConnection {

        //当服务连接成功调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取中间人对象
            myBinder = (LocUploadService.MyBinder) service;

            if (myBinder != null) {
                RouteInfo routeInfo = new RouteInfo();
                routeInfo.setRouteId(AppInfo.getRouteId());
                myBinder.ReciveLog(getParam(routeInfo));
            }
        }

        //失去连接
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBusReceived(Location data) {
        Log.d(TAG, "onEventBusReceived Location:" + new Gson().toJson(data));
//        Toast.makeText(getApplicationContext(),"来点啦",Toast.LENGTH_LONG).show();
        if (myBinder != null) {
            myBinder.ReciveLoc(data);
        }
        lastLoc = data;
        EventBus.getDefault().removeStickyEvent(data);
        saveTracks(data);
    }

    private void saveTracks(Location data) {
        Tracks track = new Tracks();
        track.setTime(data.getTime() / 1000);
        track.setX(data.getLongitude());
        track.setY(data.getLatitude());
        track.setAccuracy(data.getAccuracy());
        track.setSpeed(data.getSpeed());
        track.setType(1);
        track.setAzimuth(data.getBearing());
        track.setCoordinate(3);
        tracks.add(track);
        checkTracks();
        AppInfo.setTracks(tracks);
    }

    private void checkTracks() {
        if (tracks.size() < 2) {
            return;
        }
        Tracks start = tracks.get(0);
        Tracks end = tracks.get(tracks.size() - 1);
        Log.d(TAG, "毫秒相差 " + (end.getTime() - start.getTime()));
        if (end.getTime() - start.getTime() >= 5 * 60) {
            tracks.remove(0);
            Log.d(TAG, "刪除第一个点 " + tracks.size());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBusReceived(String data) {
        Log.d(TAG, "onEventBusReceived String:" + data);
        if (data.startsWith("#NAVIINFO")) {
        }
//        saveRouteInfo(gpsLogPath,data);
        EventBus.getDefault().removeStickyEvent(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBusReceived(CrossImgInfo data) {
        Log.d(TAG, "onEventBusReceived CrossImgInfo:" + data);
        if (myBinder != null) {
            myBinder.ReciveLog(getParam(data));
        }
        EventBus.getDefault().removeStickyEvent(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBusReceived(LaneImgInfo data) {
        Log.d(TAG, "onEventBusReceived CrossImgInfo:" + data);
        if (myBinder != null) {
            myBinder.ReciveLog(getParam(data));
        }
        EventBus.getDefault().removeStickyEvent(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBusReceived(SatelliteEvent data) {
        Log.d(TAG, "onEventBusReceived SatelliteEvent:" + data.getSate());
        if (myBinder != null) {
            myBinder.ReciveSatellite(data.getSate());
        }
        EventBus.getDefault().removeStickyEvent(data);
    }

    private LogParam getParam(Object object) {
        LogParam logParam = new LogParam();
        logParam.setAk("13e638bc4b21403585bcddcc728a17ee");
        logParam.setTaskId(AppInfo.getTaskId());
        logParam.setNaviId(AppInfo.getNaviId());
        logParam.setSdkVersion(AppInfo.getSdkVersion());
        logParam.setReportTime(System.currentTimeMillis());
        if (object instanceof RouteInfo) {
            logParam.setRouteInfo((RouteInfo) object);
            logParam.setType(1);
        } else if (object instanceof YawInfo) {
            logParam.setYawInfo((YawInfo) object);
            logParam.setType(2);
        } else if (object instanceof NaviEndInfo) {
            logParam.setNaviEndInfo((NaviEndInfo) object);
            logParam.setType(3);
        } else if (object instanceof LaneImgInfo) {
            logParam.setLaneImgInfo((LaneImgInfo) object);
            logParam.setType(4);
        } else if (object instanceof CrossImgInfo) {
            logParam.setCrossImgInfo((CrossImgInfo) object);
            logParam.setType(5);
        } else if (object instanceof VoiceInfo) {
            logParam.setVoiceInfo((VoiceInfo) object);
            logParam.setType(6);
        }

        return logParam;
    }

    // 连续按两次退出导航
    private long longLastBackTime = 0;
    private final long maxExitTime = 2000;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - longLastBackTime) < maxExitTime) {
            finish();
            sendNaviEndInfo(1);
        } else {
            longLastBackTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出导航", Toast.LENGTH_LONG).show();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 退出导航时调用
     */
    private void onExitNavi() {

    }

    public void setTrafficStyle(int type) { //0:首页白天 1:路径规划白天 2:导航黑夜
        switch (type) {
            case 0:
                MyTrafficStyle dayStyle = new MyTrafficStyle();
                dayStyle.setSmoothColor(0xff5BB64C);
                dayStyle.setSlowColor(0xffDFC289);
                dayStyle.setCongestedColor(0xff780A28);
                dayStyle.setSeriousCongestedColor(0xff780A28);
                mNaviView.getMap().setMyTrafficStyle(dayStyle);
                break;
            case 1:
                MyTrafficStyle dayRouteStyle = new MyTrafficStyle();
                dayRouteStyle.setSmoothColor(0xff9DD394);
                dayRouteStyle.setSlowColor(0xffEBDAB9);
                dayRouteStyle.setCongestedColor(0xffD76C8A);
                dayRouteStyle.setSeriousCongestedColor(0xffAE6C7E);
                mNaviView.getMap().setMyTrafficStyle(dayRouteStyle);
                break;
            case 2:
                MyTrafficStyle nightNaviStyle = new MyTrafficStyle();
                nightNaviStyle.setSmoothColor(0xff356733);
                nightNaviStyle.setSlowColor(0xff948467);
                nightNaviStyle.setCongestedColor(0xff7E1137);
                nightNaviStyle.setSeriousCongestedColor(0xff53102A);
                mNaviView.getMap().setMyTrafficStyle(nightNaviStyle);
                break;
            default:
                mNaviView.getMap().setMyTrafficStyle(new MyTrafficStyle());
                break;
        }
    }

    public void testGps() {
        Location location = new Location("gps");
        location.setLongitude(113.21212 + Math.random() * 0.001);
        location.setLatitude(30.4545 + Math.random() * 0.001);
        location.setTime(System.currentTimeMillis());
        location.setSpeed(100);
        location.setBearing(100);

        mNavi.setGPSInfo(1, location);
    }

    @Override
    public void onNaviSetting() {
//        testGps();
//        Toast.makeText(getApplicationContext(), "来个gps", Toast.LENGTH_LONG).show();
    }

    private void checkGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "请开启GPS权限", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
    }

    @Override
    public void onAddLocation(Location location) {
        if (location != null && context != null) {
            if(mNavi == null){
                return;
            }
            mNavi.setGPSInfo(1, location);
        }
    }

   private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            gpsService = ((GPS_FGService.LocalBinder) service).getService();
            if (gpsService != null) {
                gpsService.setOnAddLocationListener(context);
                //gpsService.setAddGPSListener(context);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void bindService() {
        Intent intent = new Intent(this, GPS_FGService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 设置地图倾角(3d视角)
     *
     * @param tilt
     */
    public void setCameraDegree(float tilt) {
        sfmapNaviViewOptions.setTilt((int) tilt);
        sfmapNaviViewOptions.setZoom(17);
        mNaviView.setViewOptions(sfmapNaviViewOptions);
    }


    /**
     * App前后台状态
     */
    public boolean isForeground = false;
    /**
     * 判断app是否处于前台
     *
     * @return
     */
    public boolean isAppOnForeground() {
        Log.d(TAG,"isAppOnForeground");
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        Log.d(TAG,"isAppOnForeground1");
        if (appProcesses == null)
            return false;
        Log.d(TAG,"isAppOnForeground2");
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Log.d(TAG,"isAppOnForeground3 TRUE");
                return true;
            }
        }
        Log.d(TAG,"isAppOnForeground4");
        return false;
    }


    /**
     * 自动设置地图模式x
     */
    private void autoMapMode() {
        Location location = lastLoc;
        if (location != null) {
            GeoPoint gp = new GeoPoint(location.getLongitude(),location.getLatitude());
            if (gp != null) {
                PointD cdPoint = Projection.PixelsToLatLong(gp.x,
                        gp.y, 20);
                double dX = cdPoint.x;
                double dY = cdPoint.y;
                boolean flag = !CalcSunRiseAndSunSetTools.isDay(true, dX, dY,
                        location);
                if (flag) {
                    setNaviMapSkin(true);
                } else {
                    setNaviMapSkin(false);
                }
                return;
            }
        }

        GeoPoint gp = new GeoPoint(AppInfo.getStartX(),AppInfo.getStartY());

        PointD cdPoint = Projection.PixelsToLatLong(gp.x, gp.y,
                Projection.MAXZOOMLEVEL);
        double dX = cdPoint.x;
        double dY = cdPoint.y;

        boolean flag = !CalcSunRiseAndSunSetTools
                .isDay(false, dX, dY, null);
        if (flag) {
            setNaviMapSkin(true);
        } else {
            setNaviMapSkin(false);
        }
    }

    /**
     * 设置皮肤
     *
     * @param night
     */
    public void setNaviMapSkin(boolean night) {
        sfmapNaviViewOptions = mNaviView.getViewOptions();
        sfmapNaviViewOptions.setNaviNight(night);
        mNaviView.setViewOptions(sfmapNaviViewOptions);
        setTrafficStyle(night ? 2 : 1 );
    }
}
