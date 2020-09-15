package com.sfmap.tbt.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;


public class Utils {
    private Utils() {
        //
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    public static String sortParams(String paramString)
    {
        try
        {
            if (TextUtils.isEmpty(paramString)) {
                return "";
            }
            String[] arrayOfString = paramString.split("&");
            Arrays.sort(arrayOfString);
            StringBuffer localStringBuffer = new StringBuffer();
            for (String str : arrayOfString)
            {
                localStringBuffer.append(str);
                localStringBuffer.append("&");
            }
            String strValue = localStringBuffer.toString();
            if (strValue.length() > 1) {
                return (String)strValue.subSequence(0, (strValue.length() - 1));
            }
        }
        catch (Throwable localThrowable)
        {
//            BasicLogHandler.getSDKInfo(localThrowable, "Utils", "sortParams");
        }
        return paramString;
    }
    static PublicKey getPublicKey(Context context)
            throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, NullPointerException, IOException
    {
        String str="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFoAkykHtfxJJpD9UGIHM6/Z41L9pHlDlFCH10CcVDDRMAP3ilNQBMUFHBGM7qxNsTPGlcpYOOyQ5JaWcpL+EI+dUDmL+rdM6vpvIGIxKIgZv12qntHytheT9xs/ouvooZ6JHu2874Wa981sUqwlr5L8o7ZFc8Dix2CQn4L5Eq8wIDAQAB";
        KeyFactory localKeyFactory = null;
        try
        {
            localKeyFactory = KeyFactory.getInstance("RSA");
            Object localObject1 = new X509EncodedKeySpec(Base64.decode(str, 0));

            return localKeyFactory.generatePublic((KeySpec)localObject1);
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
        return null;
    }
    public static String byte2HexString(byte[] paramArrayOfByte)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        if (paramArrayOfByte == null) {
            return null;
        }
        for (int i = 0; i < paramArrayOfByte.length; i++)
        {
            String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str.length() == 1) {
                str = '0' + str;
            }
            localStringBuilder.append(str);
        }
        return localStringBuilder.toString();
    }
    public static byte[] gZip(byte[] paramArrayOfByte)
    {
        try
        {
            return f(paramArrayOfByte);
        }
        catch (IOException localIOException)
        {
//            BasicLogHandler.getSDKInfo(localIOException, "Utils", "gZip");
        }
        catch (Throwable localThrowable)
        {
//            BasicLogHandler.getSDKInfo(localThrowable, "Utils", "gZip");
        }
        return new byte[0];
    }
    private static byte[] f(byte[] paramArrayOfByte)
            throws IOException, Throwable
    {
        byte[] arrayOfByte = null;
        ByteArrayOutputStream localByteArrayOutputStream = null;
        GZIPOutputStream localGZIPOutputStream = null;
        if (paramArrayOfByte == null) {
            return null;
        }
        try
        {
            localByteArrayOutputStream = new ByteArrayOutputStream();
            localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
            localGZIPOutputStream.write(paramArrayOfByte);
            localGZIPOutputStream.finish();
            arrayOfByte = localByteArrayOutputStream.toByteArray();
        }
        catch (IOException localIOException)
        {
            throw localIOException;
        }
        catch (Throwable localThrowable1)
        {
            throw localThrowable1;
        }
        finally
        {
            if (localGZIPOutputStream != null) {
                try
                {
                    localGZIPOutputStream.close();
                }
                catch (Throwable localThrowable2)
                {
                    throw localThrowable2;
                }
            }
            if (localByteArrayOutputStream != null) {
                try
                {
                    localByteArrayOutputStream.close();
                }
                catch (Throwable localThrowable3)
                {
                    throw localThrowable3;
                }
            }
        }
        return arrayOfByte;
    }
    public static byte[] getIV()
    {
        try
        {
            String str1 = "16,16,18,77,15,911,121,77,121,911,38,77,911,99,86,67,611,96,48,77,84,911,38,67,021,301,86,67,611,98,48,77,511,77,48,97,511,58,48,97,511,84,501,87,511,96,48,77,221,911,38,77,121,37,86,67,25,301,86,67,021,96,86,67,021,701,86,67,35,56,86,67,611,37,221,87";

            StringBuffer localStringBuffer = new StringBuffer(str1);
            String str2 = localStringBuffer.reverse().toString();
            String[] arrayOfString1 = str2.split(",");
            byte[] arrayOfByte1 = new byte[arrayOfString1.length];
            for (int i = 0; i < arrayOfString1.length; i++) {
                arrayOfByte1[i] = Byte.parseByte(arrayOfString1[i]);
            }
            byte[] arrayOfByte2 = Encrypt.base64Decode(new String(arrayOfByte1));
            String str3 = new String(arrayOfByte2);
            localStringBuffer = new StringBuffer(str3);
            String str4 = localStringBuffer.reverse().toString();

            String[] arrayOfString2 = str4.split(",");
            byte[] arrayOfByte3 = new byte[arrayOfString2.length];
            for (int j = 0; j < arrayOfString2.length; j++) {
                arrayOfByte3[j] = Byte.parseByte(arrayOfString2[j]);
            }
            return arrayOfByte3;
        }
        catch (Throwable localThrowable)
        {
            BasicLogHandler.a(localThrowable, "Utils", "getIV");
        }
        return new byte[16];
    }
    public static Context getContext(Activity context){
        try{
            Context localContext = context.getBaseContext();

            if(!localContext.getClass().getName().equals("android.app.ContextImpl")){
                Method method = getContextField(localContext.getClass().getName(),"getContext");
                if(method==null)throw new Exception("重构Context时,请提供getContext()接口,返回android.app.ContextImpl对像");
                Object [] objects = null;
                Object obj = method.invoke(localContext,objects);
                return  (Context) obj;
            }else{
                return context.getBaseContext();
            }
        }catch (Exception e){
            e.printStackTrace();
            return context.getBaseContext();
        }
    }


    private static Method getContextField(String calssName,String methodName){
        Method method = null;
        try {
            Class localClass = Class.forName(calssName);
            method = localClass.getDeclaredMethod(methodName);
            method.setAccessible(true);
        } catch (Throwable localThrowable) {
            return method;
        }
        return method;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}