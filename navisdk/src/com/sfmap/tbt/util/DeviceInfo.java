package com.sfmap.tbt.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class DeviceInfo //bq
{
  private static String utdid = "";
  private static boolean b = false;
  
  static String a(Context paramContext)
  {
    try
    {
      return u(paramContext);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return "";
  }
  
  static String b(Context paramContext)
  {
    String str = "";
    try
    {
      return w(paramContext);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return str;
  }
  
  static int c(Context paramContext)
  {
    int i = -1;
    try
    {
      return x(paramContext);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return i;
  }
  
  static int d(Context paramContext)
  {
    int i = -1;
    try
    {
      return v(paramContext);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return i;
  }
  
  static String e(Context paramContext)
  {
    try
    {
      return t(paramContext);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return "";
  }
  
  public static void a()
  {
    try
    {
      if (Build.VERSION.SDK_INT > 14)
      {
        Method localMethod = TrafficStats.class.getDeclaredMethod("setThreadStatsTag", new Class[] { Integer.TYPE });
        
        localMethod.invoke(null, new Object[] { Integer.valueOf(40964) });
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      BasicLogHandler.a(localNoSuchMethodException, "DeviceInfo", "setTraficTag");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      BasicLogHandler.a(localIllegalAccessException, "DeviceInfo", "setTraficTag");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      BasicLogHandler.a(localIllegalArgumentException, "DeviceInfo", "setTraficTag");
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      BasicLogHandler.a(localInvocationTargetException, "DeviceInfo", "setTraficTag");
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "setTraficTag");
    }
  }
  
  static class a
    extends DefaultHandler
  {
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
      throws SAXException
    {
      if ((paramString2.equals("string")) && ("UTDID".equals(paramAttributes.getValue("name")))) {
        //bq.getSDKInfo(true);
    	 b = true; //上一句翻译
      }
    }
    
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws SAXException
    {
      //if (bq.b()) {
      //  bq.getSDKInfo(new String(paramArrayOfChar, paramInt1, paramInt2));
      //}
    	if(b){ //上两句翻译
    		utdid = new String(paramArrayOfChar, paramInt1, paramInt2);
    	}
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
      throws SAXException
    {
     // bq.getSDKInfo(false);
    	b = false; //上一句翻译
    }
  }
  
  public static String getUTDID(Context paramContext)
  {
    try
    {
      if ((utdid != null) && (!"".equals(utdid))) {
        return utdid;
      }
      if (paramContext.checkCallingOrSelfPermission("android.permission.WRITE_SETTINGS") == 0) {
        utdid = Settings.System.getString(paramContext.getContentResolver(), "mqBRboGZkQPcAkyk");
      }
      if ((utdid != null) && (!"".equals(utdid))) {
        return utdid;
      }
    }
    catch (Throwable localThrowable1)
    {
      localThrowable1.printStackTrace();
    }
    try
    {
      if ("mounted".equals(Environment.getExternalStorageState()))
      {
        String str1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        
        String str2 = str1 + "/.UTSystemConfig/Global/Alvin2.xml";
        File localFile = new File(str2);
        if (localFile.exists())
        {
          SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
          SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
          a locala = new a();
          localSAXParser.parse(localFile, locala);
        }
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localFileNotFoundException.printStackTrace();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      localParserConfigurationException.printStackTrace();
    }
    catch (SAXException localSAXException)
    {
      localSAXException.printStackTrace();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    catch (Throwable localThrowable2)
    {
      localThrowable2.printStackTrace();
    }
    return utdid;
  }
  
  static String g(Context context)
  {
    String str = null;
    try
    {
      if ((context == null) || (context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0)) {
        return str;
      }
      WifiManager localWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
      if (localWifiManager.isWifiEnabled())
      {
        WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
        str = localWifiInfo.getBSSID();
      }
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getWifiMacs");
    }
    return str;
  }
  
  static String getWifiMacs(Context context)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      if ((context == null) || (context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0)) {
        return localStringBuilder.toString();
      }
      WifiManager localWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
      if (localWifiManager.isWifiEnabled())
      {
        List<ScanResult> localList = localWifiManager.getScanResults();
        if ((localList == null) || (localList.size() == 0)) {
          return localStringBuilder.toString();
        }
        localList = a(localList);
        int i = 1;
        for (int j = 0; (j < localList.size()) && (j < 7); j++)
        {
          ScanResult localScanResult = (ScanResult)localList.get(j);
          if (i != 0) {
            i = 0;
          } else {
            localStringBuilder.append(";");
          }
          localStringBuilder.append(localScanResult.BSSID);
        }
      }
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getWifiMacs");
    }
    return localStringBuilder.toString();
  }
  
  private static String c = "";
  
  static String getDeviceMac(Context paramContext)
  {
    try
    {
      if ((c != null) && (!"".equals(c))) {
        return c;
      }
      if (paramContext.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0) {
        return c;
      }
      WifiManager localWifiManager = (WifiManager)paramContext.getSystemService(Context.WIFI_SERVICE);
      
      c = localWifiManager.getConnectionInfo().getMacAddress();
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getDeviceMac");
    }
    return c;
  }
  
  static String[] cellInfo(Context paramContext)
  {
    try
    {
      if ((paramContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) || (paramContext.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
        return new String[] { "", "" };
      }
      TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
      
      CellLocation localCellLocation = localTelephonyManager.getCellLocation();
      Object localObject;
      int i;
      int j;
      if ((localCellLocation instanceof GsmCellLocation))
      {
        localObject = (GsmCellLocation)localCellLocation;
        i = ((GsmCellLocation)localObject).getCid();
        j = ((GsmCellLocation)localObject).getLac();
        return new String[] { j + "||" + i, "gsm" };
      }
      if ((localCellLocation instanceof CdmaCellLocation))
      {
        localObject = (CdmaCellLocation)localCellLocation;
        i = ((CdmaCellLocation)localObject).getSystemId();
        j = ((CdmaCellLocation)localObject).getNetworkId();
        int k = ((CdmaCellLocation)localObject).getBaseStationId();
        if ((i >= 0) && (j >= 0) && (k < 0)) {}
        return new String[] { i + "||" + j + "||" + k, "cdma" };
      }
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "cellInfo");
    }
    return new String[] { "", "" };
  }
  
  static String getMNC(Context paramContext)
  {
    String str1 = "";
    try
    {
      if (paramContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
        return str1;
      }
      TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
      
      String str2 = localTelephonyManager.getNetworkOperator();
      if ((TextUtils.isEmpty(str2)) && (str2.length() < 3)) {
        return str1;
      }
      str1 = str2.substring(3);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getMNC");
    }
    return str1;
  }
  
  public static int getNetWorkType(Context paramContext)
  {
    int i = -1;
    try
    {
      return x(paramContext);
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getNetWorkType");
    }
    return i;
  }
  
  public static int getActiveNetWorkType(Context paramContext)
  {
    int i = -1;
    try
    {
      return v(paramContext);
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getActiveNetWorkType");
    }
    return i;
  }
  
  public static NetworkInfo n(Context paramContext)
  {
    NetworkInfo localNetworkInfo = null;
    if (paramContext.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != 0) {
      return localNetworkInfo;
    }
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (localConnectivityManager == null) {
      return localNetworkInfo;
    }
    localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
    return localNetworkInfo;
  }
  
  static String getNetworkExtraInfo(Context paramContext)
  {
    String str = null;
    try
    {
      NetworkInfo localNetworkInfo = n(paramContext);
      if (localNetworkInfo == null) {
        return str;
      }
      str = localNetworkInfo.getExtraInfo();
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getNetworkExtraInfo");
    }
    return str;
  }
  
  private static String d = "";
  
  static String getReslution(Context context)
  {
    try
    {
      if ((d != null) && (!"".equals(d))) {
        return d;
      }
      DisplayMetrics localDisplayMetrics = new DisplayMetrics();
      WindowManager localWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
      
      localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
      int i = localDisplayMetrics.widthPixels;
      int j = localDisplayMetrics.heightPixels;
      d = j + "*" + i;
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getReslution");
    }
    return d;
  }
  
  private static String e = "";
  
  public static String getDeviceID(Context paramContext)
  {
    try
    {
      if ((e != null) && (!"".equals(e))) {
        return e;
      }
      if (paramContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
        return e;
      }
      TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
      
      e = localTelephonyManager.getDeviceId();
      if (e == null) {
        e = "";
      }
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getDeviceID");
    }
    return e;
  }
  
  public static String getSubscriberId(Context paramContext)
  {
    String str = "";
    try
    {
      return t(paramContext);
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getSubscriberId");
    }
    return str;
  }
  
  static String getNetworkOperatorName(Context paramContext)
  {
    try
    {
      return u(paramContext);
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "DeviceInfo", "getNetworkOperatorName");
    }
    return "";
  }
  
  private static String f = "";
  
  private static String t(Context paramContext)
  {
    if ((f != null) && (!"".equals(f))) {
      return f;
    }
    if (paramContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
      return f;
    }
    TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
    
    f = localTelephonyManager.getSubscriberId();
    if (f == null) {
      f = "";
    }
    return f;
  }
  
  private static String u(Context paramContext)
  {
    String str = null;
    if (paramContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
      return str;
    }
    TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
    
    str = localTelephonyManager.getSimOperatorName();
    if (TextUtils.isEmpty(str)) {
      str = localTelephonyManager.getNetworkOperatorName();
    }
    return str;
  }
  
  private static int v(Context paramContext)
  {
    int i = -1;
    if ((paramContext == null) || (paramContext.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != 0)) {
      return i;
    }
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (localConnectivityManager == null) {
      return i;
    }
    NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
    if (localNetworkInfo == null) {
      return i;
    }
    i = localNetworkInfo.getType();
    
    return i;
  }
  
  private static String w(Context paramContext)
  {
    String str1 = "";
    String str2 = getSubscriberId(paramContext);
    if ((str2 == null) || (str2.length() < 5)) {
      return str1;
    }
    str1 = str2.substring(3, 5);
    
    return str1;
  }
  
  private static int x(Context paramContext)
  {
    int i = -1;
    if (paramContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
      return i;
    }
    TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE);
    
    i = localTelephonyManager.getNetworkType();
    
    return i;
  }
  
  private static List<ScanResult> a(List<ScanResult> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++) {
      for (int k = 1; k < i - j; k++) {
        if (((ScanResult)paramList.get(k - 1)).level > ((ScanResult)paramList.get(k)).level)
        {
          ScanResult localScanResult = (ScanResult)paramList.get(k - 1);
          paramList.set(k - 1, paramList.get(k));
          paramList.set(k, localScanResult);
        }
      }
    }
    return paramList;
  }
}
