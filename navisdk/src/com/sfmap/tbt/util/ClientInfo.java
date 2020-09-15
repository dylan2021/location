package com.sfmap.tbt.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ClientInfo//bo
{
  public static String initXInfo(Context context, SDKInfo sdkInfo, Map<String, String> paramMap, boolean paramBoolean)
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      String deviceID = DeviceInfo.getDeviceID(context);
      
      writeField(localByteArrayOutputStream, deviceID);
      
      String deviceMac = DeviceInfo.getDeviceMac(context);
      writeField(localByteArrayOutputStream, deviceMac);
      
      String utdid = DeviceInfo.getUTDID(context);
      if (utdid == null) {
        utdid = "";
      }
      writeField(localByteArrayOutputStream, utdid);
      
      String packageName = AppInfo.getPackageName(context);
      
      writeField(localByteArrayOutputStream, packageName);
      
      String model = Build.MODEL;
      
      writeField(localByteArrayOutputStream, model);
      
      String manufacturer = Build.MANUFACTURER;//厂商
      
      writeField(localByteArrayOutputStream, manufacturer);
      
      String device = Build.DEVICE;
      
      writeField(localByteArrayOutputStream, device);
      
      String appName = AppInfo.getApplicationName(context);
      
      writeField(localByteArrayOutputStream, appName);
      
      String appVersion = AppInfo.getApplicationVersion(context);
      
      writeField(localByteArrayOutputStream, appVersion);
      
      String sdkversion = String.valueOf(Build.VERSION.SDK_INT);
      
      writeField(localByteArrayOutputStream, sdkversion);
      
      String imsi = DeviceInfo.getSubscriberId(context);
      
      writeField(localByteArrayOutputStream, imsi);
      
      String width_height = DeviceInfo.getReslution(context);//屏幕分辨率=x*y
      
      writeField(localByteArrayOutputStream, width_height);
      
      String netWorkType = DeviceInfo.getActiveNetWorkType(context) + "";//当前网络类型WIFI OR 3G 4G
      
      writeField(localByteArrayOutputStream, netWorkType);
      
      String str14 = DeviceInfo.getNetWorkType(context) + "";//当前使用的网络类型
      
      writeField(localByteArrayOutputStream, str14);
      
      String operatorName = DeviceInfo.getNetworkOperatorName(context);////运营商名称
      
      writeField(localByteArrayOutputStream, operatorName);
      
      String mnc = DeviceInfo.getMNC(context); //
      writeField(localByteArrayOutputStream, mnc);
      if (paramBoolean) {
        writeField(localByteArrayOutputStream, "");
      } else {
        writeField(localByteArrayOutputStream, DeviceInfo.getWifiMacs(context));
      }
      if (paramBoolean) {
        writeField(localByteArrayOutputStream, "");
      } else {
        writeField(localByteArrayOutputStream, DeviceInfo.g(context));
      }
      if (paramBoolean)
      {
        writeField(localByteArrayOutputStream, "");
        
        writeField(localByteArrayOutputStream, "");
      }
      else
      {
        String [] localObject1 = DeviceInfo.cellInfo(context);
        
        writeField(localByteArrayOutputStream, localObject1[0]);
        
        writeField(localByteArrayOutputStream, localObject1[1]);
      }
      byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();

//      byte[] arrayOfByte1 = Utils.gZip((byte[]) localObject1);
      PublicKey localPublicKey = null;
      
      localPublicKey = Utils.getPublicKey(context);
      byte[] arrayOfByte2 = null;
      Object localObject2;
      if (arrayOfByte1.length > 117)
      {
        localObject2 = new byte[117];
        
        System.arraycopy(arrayOfByte1, 0, localObject2, 0, 117);
        
        byte[] arrayOfByte3 = Encrypt.rsaEncrypt((byte[]) localObject2, localPublicKey);
        
        arrayOfByte2 = new byte[256 + arrayOfByte1.length - 117];
        
        System.arraycopy(arrayOfByte3, 0, arrayOfByte2, 0, 256);
        
        System.arraycopy(arrayOfByte1, 117, arrayOfByte2, 256, arrayOfByte1.length - 117);
      }
      else
      {
        arrayOfByte2 = Encrypt.rsaEncrypt(arrayOfByte1, localPublicKey);
      }
      return Encrypt.base64(arrayOfByte2);
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "CInfo", "InitXInfo");
    }
    return null;
  }
  
  static String paramEncrypt(Context paramContext, byte[] paramArrayOfByte)
    throws InvalidKeyException, IOException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, CertificateException
  {
    KeyGenerator localKeyGenerator = null;
    localKeyGenerator = KeyGenerator.getInstance("AES");
    if (localKeyGenerator == null) {
      return null;
    }
    localKeyGenerator.init(256);
    SecretKey localSecretKey = localKeyGenerator.generateKey();
    byte[] arrayOfByte1 = localSecretKey.getEncoded();
    
    PublicKey localPublicKey = null;
    
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    byte[] arrayOfByte4 = null;
    
    localPublicKey = Utils.getPublicKey(paramContext);
    if (localPublicKey == null) {
      return null;
    }
    arrayOfByte2 = Encrypt.rsaEncrypt(arrayOfByte1, localPublicKey);
    
    arrayOfByte3 = Encrypt.aesEncrypt(arrayOfByte1, paramArrayOfByte);
    
    arrayOfByte4 = new byte[arrayOfByte2.length + arrayOfByte3.length];
    
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 0, arrayOfByte2.length);
    
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, arrayOfByte2.length, arrayOfByte3.length);
    
    byte[] arrayOfByte5 = Utils.gZip(arrayOfByte4);
    if (arrayOfByte5 != null) {
      return Encrypt.base64(arrayOfByte5);
    }
    return "";
  }
  
  public static String b(Context paramContext, byte[] paramArrayOfByte)
  {
    try
    {
      return paramEncrypt(paramContext, paramArrayOfByte);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
//      BasicLogHandler.getSDKInfo(localInvalidKeyException, "CInfo", "AESData");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
//      BasicLogHandler.getSDKInfo(localNoSuchAlgorithmException, "CInfo", "AESData");
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
//      BasicLogHandler.getSDKInfo(localNoSuchPaddingException, "CInfo", "AESData");
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
//      BasicLogHandler.getSDKInfo(localIllegalBlockSizeException, "CInfo", "AESData");
    }
    catch (BadPaddingException localBadPaddingException)
    {
//      BasicLogHandler.getSDKInfo(localBadPaddingException, "CInfo", "AESData");
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
//      BasicLogHandler.getSDKInfo(localInvalidKeySpecException, "CInfo", "AESData");
    }
    catch (CertificateException localCertificateException)
    {
//      BasicLogHandler.getSDKInfo(localCertificateException, "CInfo", "AESData");
    }
    catch (IOException localIOException)
    {
//      BasicLogHandler.getSDKInfo(localIOException, "CInfo", "AESData");
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "CInfo", "AESData");
    }
    return "";
  }
  
  public static String getClientInfo(Context paramContext, SDKInfo parambv)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      String str = DeviceInfo.e(paramContext);
      localStringBuilder.append("\"sim\":\"").append(str).append("\",\"sdkversion\":\"").append(parambv.version).append("\",\"product\":\"").append(parambv.product).append("\",\"ed\":\"").append(parambv.d()).append("\",\"nt\":\"").append(DeviceInfo.c(paramContext)).append("\",\"np\":\"").append(DeviceInfo.a(paramContext)).append("\",\"mnc\":\"").append(DeviceInfo.b(paramContext)).append("\",\"ant\":\"").append(DeviceInfo.d(paramContext)).append("\"");
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return localStringBuilder.toString();
  }
  
  private static void writeField(ByteArrayOutputStream outputStream, String value)
  {
    if (!TextUtils.isEmpty(value))
    {
      int i = value.getBytes().length;
      byte b = 0;
      if (i > 255) {
        b = -1;
      } else {
        b = (byte)value.getBytes().length;
      }
      try
      {
        writeField(outputStream, b, value.getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        writeField(outputStream, b, value.getBytes());
      }
    }
    else
    {
      writeField(outputStream, (byte) 0, new byte[0]);
    }
  }
  
  private static void writeField(ByteArrayOutputStream outputStream, byte paramByte, byte[] paramArrayOfByte)
  {
    try
    {
      outputStream.write(new byte[] { paramByte });
      if (((paramByte > 0 ? 1 : 0) & ((paramByte & 0xFF) < 255 ? 1 : 0)) != 0) {
        outputStream.write(paramArrayOfByte);
      } else if ((paramByte & 0xFF) == 255) {
        outputStream.write(paramArrayOfByte, 0, 255);
      }
    }
    catch (IOException localIOException)
    {
//      BasicLogHandler.getSDKInfo(localIOException, "CInfo", "writeField");
    }
  }
  
  public static String getTS()
  {
    String time = null;
    try
    {
      time = String.valueOf(System.currentTimeMillis());
      String str2 = "1";
      int i = time.length();
      
      time = time.substring(0, i - 2) + str2 + time.substring(i - 1);
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "CInfo", "getTS");
    }
    return time;
  }
  
  public static String Scode(Context paramContext, String ts, String paramString2)
  {
    String str1 = null;
    try
    {
      String sha1AndPackage = AppInfo.getSHA1AndPackage(paramContext);
      str1 = MD5.encryptString(sha1AndPackage + ":" + ts);
    }
    catch (Throwable localThrowable)
    {
//      BasicLogHandler.getSDKInfo(localThrowable, "CInfo", "Scode");
    }
    return str1;
  }
}
