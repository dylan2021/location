package com.sfmap.map.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.sfmap.plugin.IMPluginManager;

import proguard.annotation.KeepName;
import proguard.annotation.KeepPublicClassMemberNames;

@KeepName
@KeepPublicClassMemberNames
public class MapSharePreference {

    private SharedPreferences mSp;

    private SharedPreferences.Editor mEditor;

    public static final int DEFAULT_INI_VALUE = -1;

    /**
     * 模块类型
     */
    public enum SharePreferenceName {
        SharedPreferences, user_route_method_info, appDownloadUrl,
        /**
         * 用户登录注册相关缓存
         */
        user_setting,

        base_path,

        truck_setting,
    }

    public enum SharePreferenceKeyEnum {
        //////////////////// 模块类型SharedPreferences ////////////////////////
        satellite,
        /**
         * 实时路况
         */
        traffic,
        /**
         * 图层按钮控制2d/3d模式
         */
        is3DMode,
        Disclaimer, showAccurate,
        /**
         * 是否更新后第一次启动APP
         */
        is_first_update_app,
        /**
         * 记录app的版本号
         */
        app_version,
        /**
         * 是否第一次启动APP
         */
        is_first_start_app,
        /**
         * 最后一次获取Json时间
         */
        GetJsonTime,
        /**
         * 闪屏json
         */
        SPLAHS_JSON,
        /**
         * 下一次需要显示的图片序号
         */
        NextShowImageIndex,
        /**
         * POISearch & IshowMapCenter
         */
        X, Y, Z,
        /**
         * 热门城市
         */
        Hotcity,
        // SharedPreferences setting
        wifiAutoUpdateEnabled,
        /**
         * 锁定地图2d旋转
         */
        LockMapAngle,
        /**
         * 放大缩小按钮的显示隐藏
         */
        ShowzoomBtn,
        /**
         * 保持屏幕常亮
         */
        screenon,
        CrossStatus, SoundMode, LightMode,
        /**
         * 车头朝向
         */
        NaviMapMode,
        /**
         * 导航视角 3d,2d
         */
        Perspective,
        /**
         * 日夜模式
         */
        NaviModeSet,
        TmcMode,
        /**
         * 导航前的协议
         */
        agree_navi_declare,

        Hidetmcchart,
        /***/
        guide_map_show,
        WifiEnabled,
        /**
         * 设备唯一编码
         */
        UserID,
        // SharedPreferences track
        track_pause,

        // SharedPreferences 离线地图wifi自动更新控制器
        wifiAutoUpdateOfflineMapEnabled,
        /**
         * mapstate记录的地图中心点纬度
         */
        lat_state,
        /**
         * mapstate记录的地图中心点经度
         */
        lon_state,
        /**
         * mapstate记录的地图缩放级别
         */
        zoom_state,
        /**
         * mapstate记录的地图方向
         */
        bearing_state,
        /**
         * mapstate记录的地图倾斜度
         */
        tilt_state,
        /**
         * 记录定位按钮的状态
         */
        gpsbtn_state,
        /**
         * 记录全景按钮的状态
         */
        streetview,
        //////////////////////////// 模块类型user_route_method_info ///////////////////////////
        car_method, // 驾车偏好

        // user_route_method_info
        last_route_type, // 最近一次路线规划方式

        //////////////////////////// 模块类型appDownloadUrl APP下载 ////////////////////////////
        UpdateMapUrl,

        mapProgress,
        /***/
        scheme,
        /***/
        mapVersion,
        /**
         * app下载版本名称，如：Version_45
         */
        FinishName,
        appName, AppPckName,
        /**
         * 是否正在下载
         */
        IsDownload,
        /**
         * 是否后台下载
         */
        IsBackgroundDownload,
        /**
         * 需要下载的APK名称
         */
        updateFileName,
        /**
         * 是否强制更新
         */
        IsForceUpdateApp,
        /**
         * 版本名称
         */
        NeedForceUpdateVersion,
        app_uct,
        /**
         * 是否下载APP
         */
        UpdateApp,
        /**
         * 升级描述
         */
        UpdateDesc,
        /**
         * 下载URL
         */
        UpdateUrl,
        /**
         * 是否使用APP市场下载
         */
        UpdateUseApp,
        /***/
        UpdateMapName,
        /***/
        VersionCode
        /***/
        , AppInstallDialog,
        /**
         * 检测请求成功后记录日期,用于判断今天是否使用过
         */
        Date,

        ////////////////////////////// 模块类型user_setting //////////////////////
        ignore_login,
        qucik_login,
        is_login,
        Email,
        //        Pwd,
        Mobile,
        track_guide,
        id,
        rseat,
        token,

        ///////////////base_path////////////////////////////////////

        offline_data_storage,

        offline_data_storage_sdcard,
        clear_local_data,

        map_base_path,
        ////////////////////////////////////////////////////////////

        ///////////////货车参数//////////////////////////////////////
        //货车所有参数是否填写完毕
        CarDone,
        //车牌归属地
        CarCode,
        //车牌号码
        CarPlate,
        // 车辆类型
        CarVehicle,
        // 货车载重 单位吨
        CarWeight,
        // 轴数，单位：个
        CarAxleNumber,
        // 高度，单位：米
        CarHeight,
        // 动力类型
        CarPowerType,
        //车长
        CarLenth,
        //车宽
        CarWidth,
        // 货车载重 单位吨
        CarLoadWeight
        ////////////////////////////////////////////////////////////
    }

    public MapSharePreference(SharePreferenceName name) {
        this(IMPluginManager.getApplication().getApplicationContext(), name);
    }

    public MapSharePreference(Context context, SharePreferenceName name) {
        this.mSp = context.getSharedPreferences(name.toString(),
                Context.MODE_PRIVATE);
        this.mEditor = this.mSp.edit();
    }

    public void clear() {
        this.mEditor.clear();
    }

    public int getIntValue(SharePreferenceKeyEnum key, int defValue) {
        return mSp.getInt(key.toString(), defValue);
    }

    public void putIntValue(SharePreferenceKeyEnum key, int value) {
        mEditor.putInt(key.toString(), value);
        commit();
    }

    public boolean getBooleanValue(SharePreferenceKeyEnum key, boolean defValue) {
        return mSp.getBoolean(key.toString(), defValue);
    }

    public void putBooleanValue(SharePreferenceKeyEnum key, boolean value) {
        mEditor.putBoolean(key.toString(), value);
        commit();
    }

    public float getFloatValue(SharePreferenceKeyEnum key, float defValue) {
        return mSp.getFloat(key.toString(), defValue);
    }

    public void putFloatValue(SharePreferenceKeyEnum key, float value) {
        mEditor.putFloat(key.toString(), value);
        commit();
    }

    public long getLongValue(SharePreferenceKeyEnum key, long defValue) {
        return mSp.getLong(key.toString(), defValue);
    }

    public void putLongValue(SharePreferenceKeyEnum key, long value) {
        mEditor.putLong(key.toString(), value);
        commit();
    }

    public String getStringValue(SharePreferenceKeyEnum key, String defValue) {
        return mSp.getString(key.toString(), defValue);
    }

    public void putStringValue(SharePreferenceKeyEnum key, String value) {
        mEditor.putString(key.toString(), value);
        commit();
    }

    public double getDoubleValue(final SharePreferenceKeyEnum key, final double defValue)
            throws NumberFormatException {
//        return Double.doubleToLongBits(getLongValue(key, Double.doubleToLongBits(defValue))); // 有问题需要验证
        return Double.parseDouble(getStringValue(key, String.valueOf(defValue)));
    }

    public void putDoubleValue(final SharePreferenceKeyEnum key, final double value) {
//        putLongValue(key, Double.doubleToRawLongBits(value)); // 有问题需要验证
        putStringValue(key, String.valueOf(value));
    }

    public void put(SharePreferenceKeyEnum key, String value) {
        mEditor.putString(key.toString(), value);
        commit();
    }

    public void remove(SharePreferenceKeyEnum key) {
        this.mEditor.remove(key.toString());
        commit();
    }

    public SharedPreferences getSharedPreferences() {
        return mSp;
    }

    public Editor edit() {
        return mEditor;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void commit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }
}
