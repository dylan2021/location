package com.sfmap.tbt.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.sfmap.api.navi.model.Tracks;
import com.sfmap.tbt.DeviceIdManager;

import java.lang.ref.PhantomReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * 工具类 获取服务地址、获取包名、sha1、ak
 */
public class AppInfo//bm
{
  private static String TAG = "AppInfo";
  private static String appName = "";
  private static String packageName = "";
  private static String appVersion = "";
  private static String key = "";
  private static String sha1AndPackage = null;
  private static String sdkVersion = "1.0.1";

  //汽车和货车导航服务地址
  private static String routeCarURL = "";
  private static String routeCarDefURL = "https://gis.sf-express.com/mms/navi";

  //鉴权
  private static String sfAuthURL = "";
  private static String sfAuthDefURL = "http://221.122.119.63:25001/v3/mobile/auth";

  //路口放大图服务地址-腾讯云
  private static String sfCrossPicURL = "";
  private static String sfCrossPicDefURL="http://gis-lss-mms-navi.sit.sf-express.com:2098/mms/download";

  //路径规划实时路况服务地址-生产
  private static String sfRouteTmcURL = "";
  private static String sfRouteTmcDefURL="http://gis-lss-mms-navi.sit.sf-express.com:2098/mms/eta/noYawc";

  //路径规划实时路况服务地址-生产
  private static String sfRouteEtaURL = "";
  private static String sfRouteEtaDefURL="http://gis-lss-mms-navi.sit.sf-express.com:2098/mms/eta/naviTop3c";

  //路径规划实时路况服务地址-生产
  private static String sfReRouteEtaURL = "";
  private static String sfReRouteEtaDefURL="http://gis-lss-mms-navi.sit.sf-express.com:2098/mms/eta/yawc";

  //图咕系统配置的KEY
  private static String systemAk = "";
//  private static String systemAkDef = "d26058f0374b4748af80aba7a9baa6ee";
  private static String systemAkDef = "ec85d3648154874552835438ac6a02b2";

  private static String userId;
  private static String taskId;
  private static String naviId;
  private static String routeId = "0";
  private static double startX;
  private static double startY;
  private static double endX;
  private static double endY;
  private static String planDate;
  private static String carPlate;
  private static String destDeptCode;
  private static String srcDeptCode;
  private static List<Tracks> tracks = new ArrayList<>();

  public static String getAkKey(Context context)
  {
    if ((key == null) || (key.equals("")))
    {
      try {
        ApplicationInfo localApplicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        if (localApplicationInfo == null||localApplicationInfo.metaData==null) {
          return key;
        }
        key = localApplicationInfo.metaData.getString("com.sfmap.apikey");
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }
    return key;
  }

  public static String getApplicationName(Context context)
  {
    PackageManager localPackageManager = null;
    ApplicationInfo localApplicationInfo = null;
    try
    {
      if (!"".equals(appName)) {
        return appName;
      }
      localPackageManager = context.getPackageManager();
      localApplicationInfo = localPackageManager.getApplicationInfo(context.getPackageName(), 0);
      
      appName = (String)localPackageManager.getApplicationLabel(localApplicationInfo);
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
//      BasicLogHandler.getSDKInfo(localNameNotFoundException, "AppInfo", "getApplicationName");
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "AppInfo", "getApplicationName");
    }
    return appName;
  }
  
  public static String getPackageName(Context context)
  {
    try
    {
      if ((packageName != null) && (!"".equals(packageName))) {
        return packageName;
      }
      packageName = context.getApplicationContext().getPackageName();
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "AppInfo", "getPackageName");
    }
    return packageName;
  }
  
  public static String getApplicationVersion(Context context)
  {
    PackageInfo localPackageInfo = null;
    try
    {
      if (!"".equals(appVersion)) {
        return appVersion;
      }
      PackageManager localPackageManager = context.getPackageManager();
      localPackageInfo = localPackageManager.getPackageInfo(context.getPackageName(), 0);
      
      appVersion = localPackageInfo.versionName;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
//      BasicLogHandler.getSDKInfo(localNameNotFoundException, "AppInfo", "getApplicationVersion");
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "AppInfo", "getApplicationVersion");
    }
    return appVersion;
  }
  
  public static String getSHA1AndPackage(Context context)
  {
    try
    {
      if ((sha1AndPackage != null) && (!"".equals(sha1AndPackage))) {
        return sha1AndPackage;
      }
      PackageInfo localPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

      byte[] arrayOfByte1 = localPackageInfo.signatures[0].toByteArray();
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
      byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
      StringBuffer localStringBuffer = new StringBuffer();

      for (int i = 0; i < arrayOfByte2.length; i++)
      {
        String str = Integer.toHexString(0xFF & arrayOfByte2[i]).toUpperCase(Locale.US);
        if (str.length() == 1) {
          localStringBuffer.append("0");
        }
        localStringBuffer.append(str);
        localStringBuffer.append(":");
      }

      localStringBuffer.append(localPackageInfo.packageName);
      sha1AndPackage = localStringBuffer.toString();
      return sha1AndPackage;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
//      BasicLogHandler.getSDKInfo(localNameNotFoundException, "AppInfo", "getSHA1AndPackage");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
//      BasicLogHandler.getSDKInfo(localNoSuchAlgorithmException, "AppInfo", "getSHA1AndPackage");
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "AppInfo", "getSHA1AndPackage");
    }
    return sha1AndPackage;
  }
  
  static void a(String paramString)
  {
    systemAk = paramString;
  }

  public static String getKey(Context context)
  {
      return getSystemAk(context);
  }


  /*
  * 获取服务地址方法 说明：优先采用app中设置的地址 获取不到用sdk中设置的地址
  * @param context application上下文
  * @param curValue app中sfmap_configer中的地址
  * @param defaultValue sdk中默认的地址
  * @param metaKey 获取app中地址用的标识
  * @return 服务地址
  */
  private static String getCustomOrDefaultURL(Context context, String curValue, String defaultValue, String metaKey) {
    try {
      if ((curValue == null) || (curValue.equals(""))) {
        curValue = ConfigerHelper.getInstance(context).getKeyValue(metaKey);
        if (curValue == null || curValue.equals("")) {
          curValue = defaultValue;
        }
      }
    }catch (Exception e){
      curValue = defaultValue;
    }
    return curValue;
  }

  /*
  * 搜索提示服务地址
  * @param context
  * @return
  */
  public static String getRouteCarURL(Context context) {
    return getCustomOrDefaultURL(context, routeCarURL, routeCarDefURL, ConfigerHelper.ROUTE_CAR_URL);
  }

  /*
  * 地图鉴权服务地址
  * @param context
  * @return
  */
  public static String getSfAuthURL(Context context) {
    return getCustomOrDefaultURL(context, sfAuthURL, sfAuthDefURL, ConfigerHelper.SF_AUTH_URL);
  }

  /*
  * 图咕系统配置的KEY
  * @param context
  * @return
  */
  public static String getSystemAk(Context context) {
    return getCustomOrDefaultURL(context, systemAk, systemAkDef, ConfigerHelper.SYSTEM_AK);
  }

  /*
  * 路口放大图服务地址
  * @param context
  * @return
  */
  public static String getSfCrossPicURL(Context context) {
    return getCustomOrDefaultURL(context, sfCrossPicURL, sfCrossPicDefURL, ConfigerHelper.SF_CROSS_PIC_URL);
  }

  /*
  * 路径规划路况服务地址
  * @param context
  * @return
  */
  public static String getSfRouteTmcURL(Context context) {
    return getCustomOrDefaultURL(context, sfRouteTmcURL, sfRouteTmcDefURL, ConfigerHelper.SF_ROUTE_TMC_URL);
  }

  public static String getAppName() {
    return appName;
  }

  public static void setAppName(String appName) {
    AppInfo.appName = appName;
  }

  public static String getTaskId() {
    return taskId;
  }

  public static void setTaskId(String taskId) {
    AppInfo.taskId = taskId;
  }

  public static String getNaviId() {
    return naviId;
  }

  public static void setNaviId(String naviId) {
    AppInfo.naviId = naviId;
  }

  public static String getRouteId() {
    return routeId;
  }

  public static void setRouteId(String routeId) {
    AppInfo.routeId = routeId;
  }

  public static double getStartX() {
    return startX;
  }

  public static void setStartX(double startX) {
    AppInfo.startX = startX;
  }

  public static double getStartY() {
    return startY;
  }

  public static void setStartY(double startY) {
    AppInfo.startY = startY;
  }

  public static String getPlanDate() {
    return planDate;
  }

  public static void setPlanDate(String planDate) {
    AppInfo.planDate = planDate;
  }

  public static String getUserId() {
    return userId;
  }

  public static void setUserId(String userId) {
    AppInfo.userId = userId;
  }

  public static String getCarPlate() {
    return carPlate;
  }

  public static void setCarPlate(String carPlate) {
    AppInfo.carPlate = carPlate;
  }

  public static String getSdkVersion() {
    return sdkVersion;
  }

  public static void setSdkVersion(String sdkVersion) {
    AppInfo.sdkVersion = sdkVersion;
  }

  public static String getDestDeptCode() {
    return destDeptCode;
  }

  public static void setDestDeptCode(String destDeptCode) {
    AppInfo.destDeptCode = destDeptCode;
  }

  public static String getSrcDeptCode() {
    return srcDeptCode;
  }

  public static void setSrcDeptCode(String srcDeptCode) {
    AppInfo.srcDeptCode = srcDeptCode;
  }

  public static double getEndX() {
    return endX;
  }

  public static void setEndX(double endX) {
    AppInfo.endX = endX;
  }

  public static double getEndY() {
    return endY;
  }

  public static void setEndY(double endY) {
    AppInfo.endY = endY;
  }

  public static List<Tracks> getTracks() {
    return tracks;
  }

  public static void setTracks(List<Tracks> tracks) {
    AppInfo.tracks = tracks;
  }

  public static void clearTracks() {
    AppInfo.tracks.clear();
  }

  public static String dealNaviId(Context context){
    long time = System.currentTimeMillis();
    String userId = getUserId();
    String deviceID = DeviceIdManager.getDeviceID(context);
    String tmp = time + userId + deviceID;
    String md5 = MD5.encryptString(tmp);
    md5 += "0";
    LogUtil.d(TAG,"tmp:"+tmp + "md5:"+md5);
    setNaviId(md5);
    return md5;
  }


  /*
   *
   * @param context
   * @return
   */
  public static String getSfRouteEtaURL(Context context) {
    return getCustomOrDefaultURL(context, sfRouteEtaURL, sfRouteEtaDefURL, ConfigerHelper.SF_ROUTE_ETA_URL);
  }

  /*
   *
   * @param context
   * @return
   */
  public static String getSfReRouteEtaURL(Context context) {
    return getCustomOrDefaultURL(context, sfReRouteEtaURL, sfReRouteEtaDefURL, ConfigerHelper.SF_REROUTE_ETA_URL);
  }
}
