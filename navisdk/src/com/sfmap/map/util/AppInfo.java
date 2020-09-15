package com.sfmap.map.util;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/*
 * 工具类 获取包名、sha1、ak
 */
public class AppInfo
{
    private static String TAG = "AppInfo";
    private static String appName = "";
    private static String packageName = "";
    private static String appVersion = "";
    private static String key = "";
    private static String sha1AndPackage = null;
    private static String sha1 = null;

    public static int NormalVersion = 0;
    public static int OrderVersion = 1;

    //骑行导航服务地址
    private static String routeFootURL = "";
    private static String routeFootDefURL = "https://gis.sf-express.com/mms/navi";

    //汽车和货车导航服务地址
    private static String routeCarURL = "";
    private static String routeCarDefURL = "https://gis.sf-express.com/mms/navi";

    //查询劳动者港湾服务地址
    private static String searchCcbURL = "";
    private static String searchCcbDefURL = "http://58.48.194.238:8891/msp/v3/search/ccb";


    //根据poiid查询poi详细信息
    private static String searchPoiidURL = "";
    private static String searchPoiidDefURL = "https://gis.sf-express.com/mms/search/poiid";

    //关键字搜索、周边搜索、综合搜索服务地址
    private static String searchDetailURL = "";
    private static String searchDetailDefURL = "https://gis.sf-express.com/mms/search/";

    //用户名密码登录服务地址
    private static String sfLoginAppURL = "";
    private static String sfLoginAppDefURL = "https://gis.sf-express.com/mms/loginApp";

    //自动登录服务地址
    private static String sfTokenURL = "";
    private static String sfTokenDefURL = "https://gis.sf-express.com/mms/token";

    //网点小哥位置服务地址
    private static String sfBranchUserURL = "";
    private static String sfBranchUserDefURL = "https://gis.sf-express.com/mms/branchUser";

    //小哥轨迹服务地址
    private static String sfPoshisPeriodURL = "";
    private static String sfPoshisPeriodDefURL = "https://gis.sf-express.com/mms/poshisPeriod";

    //查询权限网点服务地址
    private static String sfZnoURL = "";
    private static String sfZnoDefURL = "https://gis.sf-express.com/mms/zno";

    //根据工号和屏幕坐标查询权限网点服务地址
    private static String sfTnoURL = "";
    private static String sfTnoDefURL = "https://gis.sf-express.com/mms/tno";

    //上报和报错接口服务地址
    private static String sfErrorReportUrl = "";
    private static String sfErrorReportDefURL = "http://193.112.85.146/mms/";

    //图咕系统配置的KEY
    private static String systemAk = "";
    private static String systemAkDef = "ec85d3648154874552835438ac6a02b2";

    public static String getAppKey(Context context)
    {
        try
        {
            return getKey1(context);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
            localNameNotFoundException.printStackTrace();
        }
        catch (Throwable localThrowable)
        {
            localThrowable.printStackTrace();
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
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getApplicationName");
        }
        catch (Throwable localThrowable)
        {
            BasicLogHandler.a(localThrowable, "AppInfo", "getApplicationName");
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
            BasicLogHandler.a(localThrowable, "AppInfo", "getPackageName");
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
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getApplicationVersion");
        }
        catch (Throwable localThrowable)
        {
            BasicLogHandler.a(localThrowable, "AppInfo", "getApplicationVersion");
        }
        return appVersion;
    }

    public static String getSHA1(Context context){
        try{
            if ((sha1 != null) && (!sha1.isEmpty())) {
                return sha1;
            }
            PackageInfo localPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] arrayOfByte1 = localPackageInfo.signatures[0].toByteArray();
            MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
            byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
            StringBuffer localStringBuffer = new StringBuffer();

            for (int i = 0; i < arrayOfByte2.length; i++){
                String str = Integer.toHexString(0xFF & arrayOfByte2[i]).toUpperCase(Locale.US);
                if (str.length() == 1) {
                    localStringBuffer.append("0");
                }
                localStringBuffer.append(str);
                if(i != arrayOfByte2.length-1){
                    //不是最后一个，就添加冒号
                    localStringBuffer.append(":");
                }
            }
            sha1 = localStringBuffer.toString();
            return sha1;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getSHA1AndPackage");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
            BasicLogHandler.a(localNoSuchAlgorithmException, "AppInfo", "getSHA1AndPackage");
        }
        catch (Throwable localThrowable)
        {
            BasicLogHandler.a(localThrowable, "AppInfo", "getSHA1AndPackage");
        }
        return sha1AndPackage;
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
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getSHA1AndPackage");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
            BasicLogHandler.a(localNoSuchAlgorithmException, "AppInfo", "getSHA1AndPackage");
        }
        catch (Throwable localThrowable)
        {
            BasicLogHandler.a(localThrowable, "AppInfo", "getSHA1AndPackage");
        }
        return sha1AndPackage;
    }

    static void a(String paramString)
    {
        key = paramString;
    }

    private static String getKey1(Context context)
            throws PackageManager.NameNotFoundException
    {
        if ((key == null) || (key.equals("")))
        {
            ApplicationInfo localApplicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (localApplicationInfo == null||localApplicationInfo.metaData==null) {
                android.util.Log.e(TAG,"获取key失败");
                return key;
            }
            key = localApplicationInfo.metaData.getString("com.sfmap.apikey");
        }
        return key;
    }

    public static String getKey(Context context)
    {
        try
        {
            return getKey1(context);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getKey");
        }
        catch (Throwable localThrowable)
        {
            BasicLogHandler.a(localThrowable, "AppInfo", "getKey");
        }
        return key;
    }

    /*
     * 获取服务地址方法 说明：优先采用app中设置的地址 获取不到用sdk中设置的地址
     * @param context application上下文
     * @param curValue app中sfmap_configer中的地址
     * @param defaultValue sdk中默认的地址
     * @param metaKey 获取app中地址用的标识
     * @return 服务地址
     */
    private static String getCustomOrDefaultURL(String curValue, String defaultValue, String metaKey) {
        try {
            if ((curValue == null) || (curValue.equals(""))) {
                curValue = ConfigerHelper.getInstance().getKeyValue(metaKey);
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
     * 骑行导航服务地址
     * @param context
     * @return
     */
    public static String getRouteFootURL() {
        return getCustomOrDefaultURL(routeFootURL, routeFootDefURL, ConfigerHelper.ROUTE_FOOT_URL);
    }

    /*
     * 汽车和货车导航服务地址
     * @param context
     * @return
     */
    public static String getRouteCarURL() {
        return getCustomOrDefaultURL(routeCarURL, routeCarDefURL, ConfigerHelper.ROUTE_CAR_URL);
    }

    /*
     * 查询劳动者港湾服务地址
     * @param context
     * @return
     */
    public static String getSearchCcbURL() {
        return getCustomOrDefaultURL(searchCcbURL, searchCcbDefURL, ConfigerHelper.SEARCH_CCB_URL);
    }

    /*
     * 根据poiid查询poi详细信息
     * @param context
     * @return
     */
    public static String getSearchPoiidURL() {
        return getCustomOrDefaultURL(searchPoiidURL, searchPoiidDefURL, ConfigerHelper.SEARCH_POIID_URL);
    }

    /*
     * 关键字搜索、周边搜索、综合搜索服务地址
     * @param context
     * @return
     */
    public static String getSearchDetailURL() {
        return getCustomOrDefaultURL(searchDetailURL, searchDetailDefURL, ConfigerHelper.SEARCH_DETAIL_URL);
    }


    /*
     * mvp综合搜索服务地址
     * @param context
     * @return
     */
    public static String getSearchURL() {
        return getCustomOrDefaultURL(searchDetailURL, searchDetailDefURL, ConfigerHelper.SFMAP_API_URL);
    }

    /*
     * 用户名密码登录服务地址
     * @param context
     * @return
     */
    public static String getSfLoginAppURL() {
        return getCustomOrDefaultURL(sfLoginAppURL, sfLoginAppDefURL, ConfigerHelper.SF_LOGINAPP_URL);
    }

    /*
     * 自动登录服务地址
     * @param context
     * @return
     */
    public static String getSfTokenURL() {
        return getCustomOrDefaultURL(sfTokenURL, sfTokenDefURL, ConfigerHelper.SF_TOKEN_URL);
    }

    /*
     * 小哥轨迹服务地址
     * @param context
     * @return
     */
    public static String getSfPoshisPeriodURL() {
        return getCustomOrDefaultURL(sfPoshisPeriodURL, sfPoshisPeriodDefURL, ConfigerHelper.SF_POSHISPERIOD_URL);
    }

    /*
     * 查询权限网点服务地址
     * @param context
     * @return
     */
    public static String getSfZnoURL() {
        return getCustomOrDefaultURL(sfZnoURL, sfZnoDefURL, ConfigerHelper.SF_ZNO_URL);
    }

    /*
     * 根据工号和屏幕坐标查询权限网点服务地址
     * @param context
     * @return
     */
    public static String getSfTnoURL() {
        return getCustomOrDefaultURL(sfTnoURL, sfTnoDefURL, ConfigerHelper.SF_TNO_URL);
    }

    /*
     * 图咕系统配置的KEY
     * @param context
     * @return
     */
    public static String getSystemAk() {
        return getCustomOrDefaultURL(systemAk, systemAkDef, ConfigerHelper.SYSTEM_AK);
    }

    /*
     * 网点小哥位置服务地址
     * @param context
     * @return
     */
    public static String getSfBranchUserURL() {
        return getCustomOrDefaultURL(sfBranchUserURL, sfBranchUserDefURL, ConfigerHelper.SF_BRANCHUSER_URL);
    }


    /*
     * 上报和报错接口服务地址
     * @param context
     * @return
     */
    public static String getSfErrorReportUrl() {
        return getCustomOrDefaultURL(sfErrorReportUrl, sfErrorReportDefURL, ConfigerHelper.SF_ERROR_REPORT_URL);
    }

    /*
     * 控制地图版本类型 0:通用版本 1:内部定制版
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        return getPackageName(context).equals("com.sfmap.map.internal") ? OrderVersion:NormalVersion;
    }

    public static String getWhiteUserList(Context context) {
        return getCustomOrDefaultURL("", "", ConfigerHelper.WHITE_USER_LIST_URL);
    }
}

