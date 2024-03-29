package com.sfmap.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CommUtil {
    private static final String TAG = CommUtil.class.getSimpleName();
    private static String SALT = "#H58*fueJ4C!9kcy~";

    public static final String UTF8 = "UTF-8";

    public static byte[] compress(String strContent, String compressType)
            throws IOException{
        byte[] out = null;

        if(compressType.equalsIgnoreCase("gzip")){
            out = compressGZip(strContent);
        }else if(compressType.equalsIgnoreCase("deflate")){
            out = compressDeflate(strContent);
        }

        return out;
    }

    public static byte[] compressGZip(String strContent) throws IOException{

        byte[] out = null;
        ByteArrayOutputStream bos = null;

        if(null == strContent || strContent.isEmpty()){
            return out;
        }

        try{
            bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(strContent.getBytes(UTF8));
            gzip.close();
            out = bos.toByteArray();

        }catch(Exception e){
            throw new IOException(e);
        }finally{
            IOUtils.closeQuietly(bos);
        }

        return out;
    }


    public static byte[] compressDeflate(String strContent) throws IOException{

        byte[] out = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        if(null == strContent || strContent.isEmpty()){
            return out;
        }

        try {
            int len = 0;
            byte[] outputByte = new byte[1024];
            Deflater deflater = new Deflater(5);
            deflater.setInput(strContent.getBytes(UTF8));
            deflater.finish();

            byteArrayOutputStream = new ByteArrayOutputStream();
            while (!deflater.finished()) {
                len = deflater.deflate(outputByte);
                byteArrayOutputStream.write(outputByte, 0, len);
            }
            deflater.end();

            out = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
        }

        return out;
    }

    public static String genKey(String strContent){
        int mid = SALT.length()/2;
        int sub = 4;
        String p1 = SALT.substring(0, mid);
        String p2 = SALT.substring(mid);
        String p3 = SALT.substring(mid-sub,mid+sub);

        StringBuffer sb = new StringBuffer();
        sb.append(p3);
        sb.append(strContent);
        sb.append(p2);
        sb.append(p1);

        return MD5(sb.toString());
    }

    /**
     * MD5加密字符串
     * @param strContent
     * @return
     */
    public static String MD5(String strContent){
        try {
            MessageDigest lDigest = MessageDigest.getInstance("MD5");
            lDigest.update(strContent.getBytes(UTF8));
            BigInteger lHashInt = new BigInteger(1, lDigest.digest());
            return String.format("%1$032X", lHashInt);
        } catch (Exception lException) {
            return "unen";
        }
    }

    public long getTime(String strDate,String fromat){

        long time = 0;
        try {
            time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    .parse(strDate).getTime();

        } catch (ParseException e) {

        }

        return time;
    }

    /**
     * 判断两个二维点是否相同（在容差范围内）
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param tolerance	容差
     * @return 是否相同
     */
    public static boolean isEqual(double x1,double y1,double x2,double y2,double tolerance){
        return (Math.abs(x1-x2) < tolerance && Math.abs(y1-y2) < tolerance);
    }

    /**
     * 给定的double值保留指定的小数位数
     * @param d
     * @return
     */
    public static double getDouble(double d,int newScale){
        java.math.BigDecimal bd = java.math.BigDecimal.valueOf(d);
        return bd.setScale(newScale, java.math.BigDecimal.ROUND_FLOOR).doubleValue();
    }

    /**
     * 给定的float值保留指定的小数位数
     * @param d
     * @return
     */
    public static float getFloat(double d,int newScale){
        java.math.BigDecimal bd = java.math.BigDecimal.valueOf(d);
        return bd.setScale(newScale, java.math.BigDecimal.ROUND_FLOOR).floatValue();
    }

    public static String getPrefer(Context context,String name,String key){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(name, 0);
        return mSharedPreferences.getString(key, "gd");
    }

    public static void setPrefer(Context context,String name,String key,String value){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(name, 0);
        Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 记录日志
     * @param context
     * @param strContent
     */
    public static void log(Context context,String strContent){

        FileOutputStream fos = null;
        try{
            String logFile = getLogFile(context);
            StringBuffer sb = new StringBuffer();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.US);
            sb.append(sdf.format(new Date()));
            sb.append(":");
            sb.append(strContent);
            sb.append("\r\n");

            fos = new FileOutputStream(logFile,true);
            fos.write(sb.toString().getBytes());

        }catch(Exception e){
            IOUtils.emptyException(e);
        }finally {
            IOUtils.closeQuietly(fos);
        }

    }

    public static String getSDPath() {

        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }

        if(null == sdDir){
            return "";
        }else{
            return sdDir.toString();
        }

    }

    public static void d(Context context,String tag,String msg){
        Log.d(tag, msg);
        if(null != context){
            log(context,msg);
        }

    }

    public static String getStackTrace(Throwable throwable){
        if(null == throwable){
            return "null";
        }

        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            throwable.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            IOUtils.closeQuietly(sw);
            IOUtils.closeQuietly(pw);
        }

        return sw.toString();
    }

    //默认清理一周之前的所有日志
    public static void clearWLog(Context context){

        try{
            File logPath = new File(getLogPath(context));

            File[] logFiles = logPath.listFiles();
            if(null == logFiles){
                return;
            }

            for(File logFile : logFiles){
                String fileName = logFile.getName();

                if(!fileName.startsWith("loc_")){
                    continue;
                }

                if(!fileName.endsWith(".log")){
                    continue;
                }

                long fileTime = logFile.lastModified();
                if(System.currentTimeMillis() - fileTime > 1000*60*60*24*7){
                    logFile.delete();
                }
            }
        }catch(Exception e){
            IOUtils.emptyException(e);
        }

    }
    //获取日志保存路径。如果不存在，则创建
    public static String getLogPath(Context context){

        StringBuffer sb = new StringBuffer();
        sb.append(getSDPath());
        sb.append(File.separator);
        sb.append(context.getPackageName());
        sb.append(File.separator);
        sb.append("log");

        File path = new File(sb.toString());
        if(!path.exists()){
            path.mkdirs();
        }

        return sb.toString();
    }

    protected static String getLogFile(Context context) throws IOException{

        Date d = new Date();
        StringBuffer sb = new StringBuffer();
        sb.append(getLogPath(context));
        sb.append(File.separator);
        sb.append("loc_");
        sb.append(dateToNormalLogFileNameStr(d));
        sb.append(".log");
        File file = new File(sb.toString());
        if(!file.exists()){
            if(file.createNewFile()){
                log(context, getCurEnvInfo());
            }
        }
        return sb.toString();
    }

    public static String dateToNormalLogFileNameStr(Date date) {
        String d  = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
            d = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }


    @SuppressLint("HardwareIds")
    public static String getImei(Context context){

        String imei = "";

        try {
            imei = ((TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (SecurityException e) {
            CommUtil.d(context, "CommUtil", e.getMessage());
        } catch (Exception e) {
            //do nothing
        }

        //如果没有获取到IMEI(可能没权限)，则用时间戳作为IMEI
        if(null == imei || imei.isEmpty()){
            String spName = "sflbs";
            String spKey = "devid";
            SharedPreferences mSharedPreferences = context.getSharedPreferences(spName, 0);
            String devid = mSharedPreferences.getString(spKey, "");
            if(devid.isEmpty()){
                devid = Long.toString(System.currentTimeMillis());
                CommUtil.setPrefer(context,spName,spKey,devid);
            }

            imei = devid;
        }

        return imei;
    }

    public static String getCurEnvInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("手机型号:");
        sb.append(android.os.Build.MODEL);
        sb.append("\n产品型号:");
        sb.append(android.os.Build.PRODUCT);
        sb.append("\n系统版本:");
        sb.append(android.os.Build.VERSION.RELEASE);
        sb.append("\n版本显示:");
        sb.append(android.os.Build.DISPLAY);
        sb.append("\nSDK版本号:");
        sb.append(android.os.Build.VERSION.SDK_INT);
        sb.append("\n设备参数:");
        sb.append(android.os.Build.DEVICE);
        sb.append("\nROM制造商:");
        sb.append(android.os.Build.MANUFACTURER);
        sb.append("\n硬件类型:");
        sb.append(android.os.Build.HARDWARE);
        sb.append("\n主机:");
        sb.append(android.os.Build.HOST);
        sb.append("\n生产ID:");
        sb.append(android.os.Build.ID);

        return sb.toString();
    }

    public static void setBooleanArgMethod(Context context, Object object, String methodName, boolean value) {
        Class<?> bdOptionClass = object.getClass();
        Method booleanArgMethod = null;
        try {
            booleanArgMethod = bdOptionClass.getMethod(methodName, boolean.class);
            booleanArgMethod.setAccessible(true);
            booleanArgMethod.invoke(object, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            CommUtil.d(context, "CommUtil",
                    String.format("Class(%s) has no boolean arg method with name(%s)",
                            bdOptionClass.getCanonicalName(),
                            methodName
                    )
            );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static double pi = 3.14159265358979324;

    //// Krasovsky 1940(克拉索夫斯基椭球参数)
    //
    // 长半径：a = 6378245.0, 扁率：1/f = 298.3
    // b = a * (1 - f)
    // ee = (a^2 - b^2) / a^2
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;

    /**
     * WGS84-> GCJ02
     * @param wgLat 纬度(单位：度)
     * @param wgLon 经度(单位：度)
     * @return 数组[lat,lon] (单位都是度)
     */
    public static double[] transform(double wgLat, double wgLon) {

        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);

        double[] out = new double[2];
        out[0] = (wgLat + dLat);
        out[1] = (wgLon + dLon);

        return out;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * wgs84->gcj02(单位都是度)
     * @param latitude
     * @param longitude
     * @return [latitude,longitude]
     */
    public static double[] t(double latitude,double longitude) {
        int i = (int) ((longitude - 71.0D) / 3.0D);
        int j = (int) (latitude / 3.0D);
        i = i * 19 + j;

        if(i >= sample.length){
            return new double[]{latitude,longitude};
        }

        double d11 = sample[i][8];
        double d10 = sample[i][7];
        double d9 = sample[i][6];
        double d8 = sample[i][5];
        double d7 = sample[i][4];
        double d6 = sample[i][3];
        double d5 = sample[i][2];
        double d4 = sample[i][1];
        double d3 = sample[i][0];
        double d2 = latitude;
        double d1 = longitude;
        double d12 = d3 * d1 + d4 * d2 + d5;
        double d13 = d6 * d2 / 1000000000.0D + d7 / 1000000.0D;
        double d22 = (d1) - 105.0D;
        double d14 = (a(150.0D, 15.0D, d22) + a(40.0D, 60.0D, d22) + a(20.0D, 180.0D, d22) + a(20.0D, 360.0D, d22)
                + a(20.0D, 1080.0D, d22)) * 0.6666666666666666D;
        double d15 = d1 + (d12 + d14) * d13;
        double d16 = d8 * d1 + d9 * d2 + d10;
        double d17 = d11 * d2 / 1000000000.0D + 9.0539664E-006D;
        double d21 = d2;
        d22 = (d1) - 105.0D;
        double d23 = d21 - 35.0D;
        double d18 = (a(160.0D, 15.0D, d23) + a(40.0D, 60.0D, d23) + a(20.0D, 180.0D, d23) + a(20.0D, 360.0D, d22)
                + a(20.0D, 1080.0D, d22)) * 0.6666666666666666D;
        double d19 = d2 + (d16 + d18) * d17;
//		return new double[] { d15, d19 };
        return new double[] { d19,  d15};
    }

    private static double a(double paramDouble1, double paramDouble2, double paramDouble3) {
        return (paramDouble1 * Math.sin(paramDouble2 * paramDouble3 * 0.0174532925199433D));
    }

    private static double[][] sample = {
            { -28.951737900000001D, -1.3875927D, 2568.2098943999999D, 3.0597946D, 8.9830009D, -1.2925447D,
                    -34.278646299999998D, 290.26633040000002D, -0.0622D },
            { -28.651737900000001D, -1.3875927D, 2546.8726723999998D, 11.2644424D, 8.961452899999999D, -0.9925447D,
                    -34.795172200000003D, 269.63967439999999D, -0.2273342D },
            { -28.3517379D, -1.3875927D, 2525.5354505D, 19.625597299999999D, 8.914397900000001D, -0.6925447D,
                    -33.139089900000002D, 238.3347976D, -0.3899653D },
            { -28.051737899999999D, -1.3875927D, 2504.1982286000002D, 28.2628214D, 8.839864800000001D, -0.3925447D,
                    -29.355043899999998D, 183.72457510000001D, -0.5483032D },
            { -27.751737899999998D, -1.3875927D, 2482.8610066000001D, 37.305238799999998D, 8.734694899999999D,
                    -0.09254469999999999D, -23.695978700000001D, 96.003545200000005D, -0.7006056000000001D },
            { -27.451737900000001D, -1.3875927D, 2461.5237846999999D, 46.896400399999997D, 8.5943557D, 0.2074553D,
                    -16.5983786D, -29.676743900000002D, -0.8451975000000001D },
            { -27.151737900000001D, -1.3875927D, 2440.1865627000002D, 57.2001287D, 8.412664700000001D,
                    0.5074553000000001D, -8.6395418D, -191.7600726D, -0.9804902D },
            { -26.8517379D, -1.3875927D, 2418.8493407999999D, 68.407805800000006D, 8.1813959D, 0.8074553D, -0.4810703D,
                    -381.76253050000003D, -1.1049982D },
            { -26.551737899999999D, -1.3875927D, 2397.5121189000001D, 80.747736000000003D, 7.8897273D, 1.1074553D,
                    7.1958921D, -584.79144429999997D, -1.2173562D },
            { -26.251737899999998D, -1.3875927D, 2376.1748969D, 94.497482399999996D, 7.5234689D, 1.4074553D,
                    13.757335299999999D, -781.08595200000002D, -1.3163339D },
            { -25.951737900000001D, -1.3875927D, 2354.8376750000002D, 110.00047410000001D, 7.0639795D, 1.7074553D,
                    18.6784438D, -948.42031829999996D, -1.4008494D },
            { -25.651737900000001D, -1.3875927D, 2333.5004530000001D, 127.6888198D, 6.4866309D, 2.0074553D, 21.5949697D,
                    -1065.0888311000001D, -1.4699811D },
            { -25.3517379D, -1.3875927D, 2312.1632310999998D, 148.1152635D, 5.7586042D, 2.3074553D, 22.338887400000001D,
                    -1113.1008773999999D, -1.5229775D },
            { -25.051737899999999D, -1.3875927D, 2290.8260092D, 171.99884599999999D, 4.8356764D, 2.6074553D,
                    20.954841399999999D, -1081.1686677D, -1.559265D },
            { -24.751737899999998D, -1.3875927D, 2269.4887871999999D, 200.29154579999999D, 3.6574486D, 2.9074553D,
                    17.695776200000001D, -967.07507320000002D, -1.5784544D },
            { -24.451737900000001D, -1.3875927D, 2248.1515653000001D, 234.27778710000001D, 2.1401053D, 3.2074553D,
                    12.9981761D, -779.06617319999998D, -1.5803442D },
            { -24.151737900000001D, -1.3875927D, 2226.8143433D, 275.7268449D, 0.1651599D, 3.5074553D, 7.4393393D,
                    -536.01713319999999D, -1.5649228D },
            { -23.8517379D, -1.3875927D, 2205.4771214000002D, 327.13300479999998D, -2.4385346D, 3.8074553D, 1.6808678D,
                    -266.25992439999999D, -1.5323681D },
            { -23.551737899999999D, -1.3875927D, 2184.1398995D, 392.10647540000002D, -5.9244687D, 4.1074553D,
                    -3.5960946D, -5.1215304D, -1.4830451D },
            { -29.035007799999999D, -0.8320803D, 2572.9940385999998D, 3.0597946D, 8.9830009D, -1.293416D,
                    -33.978662200000002D, 289.65584960000001D, -0.0622D },
            { -28.735007800000002D, -0.8320803D, 2549.9902794D, 11.2644424D, 8.961452899999999D, -0.993416D,
                    -34.4951881D, 268.12924120000002D, -0.2273342D },
            { -28.435007800000001D, -0.8320803D, 2526.9865202999999D, 19.625597299999999D, 8.914397900000001D,
                    -0.693416D, -32.839105699999998D, 235.9244119D, -0.3899653D },
            { -28.1350078D, -0.8320803D, 2503.9827611999999D, 28.2628214D, 8.839864800000001D, -0.393416D,
                    -29.055059799999999D, 180.41423689999999D, -0.5483032D },
            { -27.8350078D, -0.8320803D, 2480.979002D, 37.305238799999998D, 8.734694899999999D, -0.093416D,
                    -23.3959945D, 91.793254500000003D, -0.7006056000000001D },
            { -27.535007799999999D, -0.8320803D, 2457.9752429D, 46.896400399999997D, 8.5943557D, 0.206584D,
                    -16.298394399999999D, -34.786987000000003D, -0.8451975000000001D },
            { -27.235007800000002D, -0.8320803D, 2434.9714837000001D, 57.2001287D, 8.412664700000001D, 0.506584D,
                    -8.339557599999999D, -197.7702682D, -0.9804902D },
            { -26.935007800000001D, -0.8320803D, 2411.9677246000001D, 68.407805800000006D, 8.1813959D, 0.806584D,
                    -0.1810862D, -388.67267850000002D, -1.1049982D },
            { -26.6350078D, -0.8320803D, 2388.9639655000001D, 80.747736000000003D, 7.8897273D, 1.106584D, 7.4958763D,
                    -592.60154480000006D, -1.2173562D },
            { -26.3350078D, -0.8320803D, 2365.9602063000002D, 94.497482399999996D, 7.5234689D, 1.406584D, 14.0573195D,
                    -789.79600500000004D, -1.3163339D },
            { -26.035007799999999D, -0.8320803D, 2342.9564472000002D, 110.00047410000001D, 7.0639795D, 1.706584D,
                    18.978428000000001D, -958.03032380000002D, -1.4008494D },
            { -25.735007800000002D, -0.8320803D, 2319.9526879999999D, 127.6888198D, 6.4866309D, 2.006584D,
                    21.894953900000001D, -1075.5987889999999D, -1.4699811D },
            { -25.435007800000001D, -0.8320803D, 2296.9489288999998D, 148.1152635D, 5.7586042D, 2.306584D,
                    22.638871600000002D, -1124.5107877999999D, -1.5229775D },
            { -25.1350078D, -0.8320803D, 2273.9451697999998D, 171.99884599999999D, 4.8356764D, 2.606584D, 21.2548256D,
                    -1093.4785305D, -1.559265D },
            { -24.8350078D, -0.8320803D, 2250.9414105999999D, 200.29154579999999D, 3.6574486D, 2.906584D,
                    17.995760300000001D, -980.28488849999997D, -1.5784544D },
            { -24.535007799999999D, -0.8320803D, 2227.9376514999999D, 234.27778710000001D, 2.1401053D, 3.206584D,
                    13.2981602D, -793.17594099999997D, -1.5803442D },
            { -24.235007800000002D, -0.8320803D, 2204.9338923D, 275.7268449D, 0.1651599D, 3.506584D, 7.7393234D,
                    -551.02685340000005D, -1.5649228D },
            { -23.935007800000001D, -0.8320803D, 2181.9301332D, 327.13300479999998D, -2.4385346D, 3.806584D, 1.980852D,
                    -282.1695972D, -1.5323681D },
            { -23.6350078D, -0.8320803D, 2158.9263741D, 392.10647540000002D, -5.9244687D, 4.106584D, -3.2961104D,
                    -21.9311556D, -1.4830451D },
            { -27.077328300000001D, -0.2807772D, 2420.8864662999999D, 3.0597946D, 8.9830009D, -1.2944316D,
                    -33.678682600000002D, 289.05910699999998D, -0.0622D },
            { -26.777328300000001D, -0.2807772D, 2396.2287978999998D, 11.2644424D, 8.961452899999999D, -0.9944316D,
                    -34.1952085D, 266.63255989999999D, -0.2273342D },
            { -26.4773283D, -0.2807772D, 2371.5711295000001D, 19.625597299999999D, 8.914397900000001D, -0.6944316D,
                    -32.539126199999998D, 233.5277921D, -0.3899653D },
            { -26.177328299999999D, -0.2807772D, 2346.9134610999999D, 28.2628214D, 8.839864800000001D, -0.3944316D,
                    -28.755080199999998D, 177.11767839999999D, -0.5483032D },
            { -25.877328299999999D, -0.2807772D, 2322.2557926999998D, 37.305238799999998D, 8.734694899999999D,
                    -0.0944316D, -23.096015000000001D, 87.596757400000001D, -0.7006056000000001D },
            { -25.577328300000001D, -0.2807772D, 2297.5981244D, 46.896400399999997D, 8.5943557D, 0.2055684D,
                    -15.9984149D, -39.883422799999998D, -0.8451975000000001D },
            { -25.277328300000001D, -0.2807772D, 2272.9404559999998D, 57.2001287D, 8.412664700000001D, 0.5055684D,
                    -8.0395781D, -203.76664249999999D, -0.9804902D },
            { -24.9773283D, -0.2807772D, 2248.2827876000001D, 68.407805800000006D, 8.1813959D, 0.8055684D, 0.1188934D,
                    -395.56899149999998D, -1.1049982D },
            { -24.677328299999999D, -0.2807772D, 2223.6251192D, 80.747736000000003D, 7.8897273D, 1.1055684D, 7.7958558D,
                    -600.39779650000003D, -1.2173562D },
            { -24.377328299999999D, -0.2807772D, 2198.9674507999998D, 94.497482399999996D, 7.5234689D, 1.4055684D,
                    14.357298999999999D, -798.49219530000005D, -1.3163339D },
            { -24.077328300000001D, -0.2807772D, 2174.3097824000001D, 110.00047410000001D, 7.0639795D, 1.7055684D,
                    19.2784075D, -967.62645269999996D, -1.4008494D },
            { -23.777328300000001D, -0.2807772D, 2149.6521140999998D, 127.6888198D, 6.4866309D, 2.0055684D, 22.1949334D,
                    -1086.0948565000001D, -1.4699811D },
            { -23.4773283D, -0.2807772D, 2124.9944457000001D, 148.1152635D, 5.7586042D, 2.3055684D, 22.938851100000001D,
                    -1135.9067938999999D, -1.5229775D },
            { -23.177328299999999D, -0.2807772D, 2100.3367773D, 171.99884599999999D, 4.8356764D, 2.6055684D,
                    21.554805099999999D, -1105.7744754D, -1.559265D },
            { -22.877328299999999D, -0.2807772D, 2075.6791088999998D, 200.29154579999999D, 3.6574486D, 2.9055684D,
                    18.295739900000001D, -993.48077190000004D, -1.5784544D },
            { -22.577328300000001D, -0.2807772D, 2051.0214405000002D, 234.27778710000001D, 2.1401053D, 3.2055684D,
                    13.5981398D, -807.27176310000004D, -1.5803442D },
            { -22.277328300000001D, -0.2807772D, 2026.3637721D, 275.7268449D, 0.1651599D, 3.5055684D, 8.039303D,
                    -566.02261410000006D, -1.5649228D },
            { -21.9773283D, -0.2807772D, 2001.7061037000001D, 327.13300479999998D, -2.4385346D, 3.8055684D, 2.2808315D,
                    -298.06529649999999D, -1.5323681D },
            { -21.677328299999999D, -0.2807772D, 1977.0484354D, 392.10647540000002D, -5.9244687D, 4.1055684D,
                    -2.9961309D, -38.726793499999999D, -1.4830451D },
            { -23.211670699999999D, 0.2417152D, 2110.3468520000001D, 3.0597946D, 8.9830009D, -1.2956363D,
                    -33.378709800000003D, 288.48056229999997D, -0.0622D },
            { -22.911670699999998D, 0.2417152D, 2084.1217065000001D, 11.2644424D, 8.961452899999999D, -0.9956363D,
                    -33.895235700000001D, 265.15409679999999D, -0.2273342D },
            { -22.611670700000001D, 0.2417152D, 2057.8965609000002D, 19.625597299999999D, 8.914397900000001D,
                    -0.6956363D, -32.239153399999999D, 231.14941049999999D, -0.3899653D },
            { -22.311670700000001D, 0.2417152D, 2031.6714153D, 28.2628214D, 8.839864800000001D, -0.3956363D,
                    -28.455107399999999D, 173.83937850000001D, -0.5483032D },
            { -22.0116707D, 0.2417152D, 2005.4462696999999D, 37.305238799999998D, 8.734694899999999D,
                    -0.09563629999999999D, -22.796042199999999D, 83.418538999999996D, -0.7006056000000001D },
            { -21.711670699999999D, 0.2417152D, 1979.2211241D, 46.896400399999997D, 8.5943557D, 0.2043637D,
                    -15.698442099999999D, -44.961559600000001D, -0.8451975000000001D },
            { -21.411670699999998D, 0.2417152D, 1952.9959785000001D, 57.2001287D, 8.412664700000001D, 0.5043637D,
                    -7.7396053D, -209.74469769999999D, -0.9804902D },
            { -21.111670700000001D, 0.2417152D, 1926.7708329D, 68.407805800000006D, 8.1813959D, 0.8043637D, 0.4188662D,
                    -402.4469651D, -1.1049982D },
            { -20.811670700000001D, 0.2417152D, 1900.5456873000001D, 80.747736000000003D, 7.8897273D, 1.1043637D,
                    8.095828600000001D, -608.17568849999998D, -1.2173562D },
            { -20.5116707D, 0.2417152D, 1874.3205416999999D, 94.497482399999996D, 7.5234689D, 1.4043637D, 14.6572718D,
                    -807.17000570000005D, -1.3163339D },
            { -20.211670699999999D, 0.2417152D, 1848.0953961D, 110.00047410000001D, 7.0639795D, 1.7043637D,
                    19.578380299999999D, -977.2041815D, -1.4008494D },
            { -19.911670699999998D, 0.2417152D, 1821.8702505000001D, 127.6888198D, 6.4866309D, 2.0043637D,
                    22.494906199999999D, -1096.5725038D, -1.4699811D },
            { -19.611670700000001D, 0.2417152D, 1795.6451049D, 148.1152635D, 5.7586042D, 2.3043637D, 23.2388239D,
                    -1147.2843596D, -1.5229775D },
            { -19.311670700000001D, 0.2417152D, 1769.4199593999999D, 171.99884599999999D, 4.8356764D, 2.6043637D,
                    21.854777899999998D, -1118.0519594D, -1.559265D },
            { -19.0116707D, 0.2417152D, 1743.1948138D, 200.29154579999999D, 3.6574486D, 2.9043637D, 18.5957127D,
                    -1006.6581744D, -1.5784544D },
            { -18.711670699999999D, 0.2417152D, 1716.9696681999999D, 234.27778710000001D, 2.1401053D, 3.2043637D,
                    13.898112599999999D, -821.34908399999995D, -1.5803442D },
            { -18.411670699999998D, 0.2417152D, 1690.7445226D, 275.7268449D, 0.1651599D, 3.5043637D, 8.339275799999999D,
                    -580.99985340000001D, -1.5649228D },
            { -18.111670700000001D, 0.2417152D, 1664.5193770000001D, 327.13300479999998D, -2.4385346D, 3.8043637D,
                    2.5808043D, -313.94245419999999D, -1.5323681D },
            { -17.811670700000001D, 0.2417152D, 1638.2942313999999D, 392.10647540000002D, -5.9244687D, 4.1043637D,
                    -2.6961581D, -55.503869600000002D, -1.4830451D },
            { -17.757792800000001D, 0.7136147000000001D, 1656.5271465999999D, 3.0597946D, 8.9830009D, -1.2970974D,
                    -33.078747399999997D, 287.92693639999999D, -0.0622D },
            { -17.4577928D, 0.7136147000000001D, 1628.8863025999999D, 11.2644424D, 8.961452899999999D, -0.9970974D,
                    -33.595273200000001D, 263.70058340000003D, -0.2273342D },
            { -17.157792799999999D, 0.7136147000000001D, 1601.2454585D, 19.625597299999999D, 8.914397900000001D,
                    -0.6970974D, -31.9391909D, 228.79600970000001D, -0.3899653D },
            { -16.857792799999999D, 0.7136147000000001D, 1573.6046143999999D, 28.2628214D, 8.839864800000001D,
                    -0.3970974D, -28.1551449D, 170.5860902D, -0.5483032D },
            { -16.557792800000001D, 0.7136147000000001D, 1545.9637703000001D, 37.305238799999998D, 8.734694899999999D,
                    -0.0970974D, -22.496079699999999D, 79.265363300000004D, -0.7006056000000001D },
            { -16.257792800000001D, 0.7136147000000001D, 1518.3229262D, 46.896400399999997D, 8.5943557D, 0.2029026D,
                    -15.3984796D, -50.014622699999997D, -0.8451975000000001D },
            { -15.9577928D, 0.7136147000000001D, 1490.6820822D, 57.2001287D, 8.412664700000001D, 0.5029026D,
                    -7.4396428D, -215.6976483D, -0.9804902D },
            { -15.657792799999999D, 0.7136147000000001D, 1463.0412381000001D, 68.407805800000006D, 8.1813959D,
                    0.8029026D, 0.7188287D, -409.29980319999999D, -1.1049982D },
            { -15.3577928D, 0.7136147000000001D, 1435.400394D, 80.747736000000003D, 7.8897273D, 1.1029026D, 8.3957911D,
                    -615.92841399999998D, -1.2173562D },
            { -15.0577928D, 0.7136147000000001D, 1407.7595498999999D, 94.497482399999996D, 7.5234689D, 1.4029026D,
                    14.9572343D, -815.82261870000002D, -1.3163339D },
            { -14.757792800000001D, 0.7136147000000001D, 1380.1187058999999D, 110.00047410000001D, 7.0639795D,
                    1.7029026D, 19.878342799999999D, -986.75668189999999D, -1.4008494D },
            { -14.4577928D, 0.7136147000000001D, 1352.4778618D, 127.6888198D, 6.4866309D, 2.0029026D,
                    22.794868699999999D, -1107.0248916D, -1.4699811D },
            { -14.157792799999999D, 0.7136147000000001D, 1324.8370176999999D, 148.1152635D, 5.7586042D, 2.3029026D,
                    23.538786399999999D, -1158.6366349D, -1.5229775D },
            { -13.8577928D, 0.7136147000000001D, 1297.1961736000001D, 171.99884599999999D, 4.8356764D, 2.6029026D,
                    22.154740400000001D, -1130.3041221999999D, -1.559265D },
            { -13.5577928D, 0.7136147000000001D, 1269.5553295D, 200.29154579999999D, 3.6574486D, 2.9029026D,
                    18.895675199999999D, -1019.8102246D, -1.5784544D },
            { -13.257792800000001D, 0.7136147000000001D, 1241.9144855D, 234.27778710000001D, 2.1401053D, 3.2029026D,
                    14.198075100000001D, -835.40102160000004D, -1.5803442D },
            { -12.9577928D, 0.7136147000000001D, 1214.2736414000001D, 275.7268449D, 0.1651599D, 3.5029026D,
                    8.639238300000001D, -595.95167849999996D, -1.5649228D },
            { -12.657792799999999D, 0.7136147000000001D, 1186.6327973D, 327.13300479999998D, -2.4385346D, 3.8029026D,
                    2.8807668D, -329.79416670000001D, -1.5323681D },
            { -12.3577928D, 0.7136147000000001D, 1158.9919531999999D, 392.10647540000002D, -5.9244687D, 4.1029026D,
                    -2.3961956D, -72.255469599999998D, -1.4830451D },
            { -11.190950600000001D, 1.1180895D, 1090.8163443999999D, 3.0597946D, 8.9830009D, -1.2989211D,
                    -32.778801700000002D, 287.4089209D, -0.0622D },
            { -10.8909506D, 1.1180895D, 1061.9620758999999D, 11.2644424D, 8.961452899999999D, -0.9989211D,
                    -33.295327499999999D, 262.28273089999999D, -0.2273342D },
            { -10.590950599999999D, 1.1180895D, 1033.1078075D, 19.625597299999999D, 8.914397900000001D, -0.6989211D,
                    -31.639245200000001D, 226.47832009999999D, -0.3899653D },
            { -10.2909506D, 1.1180895D, 1004.253539D, 28.2628214D, 8.839864800000001D, -0.3989211D,
                    -27.855199200000001D, 167.36856349999999D, -0.5483032D },
            { -9.9909506D, 1.1180895D, 975.39927049999994D, 37.305238799999998D, 8.734694899999999D, -0.0989211D,
                    -22.196134000000001D, 75.147999499999997D, -0.7006056000000001D },
            { -9.690950600000001D, 1.1180895D, 946.54500199999995D, 46.896400399999997D, 8.5943557D, 0.2010789D,
                    -15.0985339D, -55.031823699999997D, -0.8451975000000001D },
            { -9.3909506D, 1.1180895D, 917.69073349999996D, 57.2001287D, 8.412664700000001D, 0.5010789D, -7.1396971D,
                    -221.61468640000001D, -0.9804902D },
            { -9.090950599999999D, 1.1180895D, 888.83646510000005D, 68.407805800000006D, 8.1813959D, 0.8010789D,
                    1.0187744D, -416.11667829999999D, -1.1049982D },
            { -8.7909506D, 1.1180895D, 859.98219659999995D, 80.747736000000003D, 7.8897273D, 1.1010789D,
                    8.695736800000001D, -623.64512620000005D, -1.2173562D },
            { -8.4909506D, 1.1180895D, 831.12792809999996D, 94.497482399999996D, 7.5234689D, 1.4010789D, 15.25718D,
                    -824.439168D, -1.3163339D },
            { -8.190950600000001D, 1.1180895D, 802.27365959999997D, 110.00047410000001D, 7.0639795D, 1.7010789D,
                    20.178288500000001D, -996.27306840000006D, -1.4008494D },
            { -7.8909506D, 1.1180895D, 773.41939109999998D, 127.6888198D, 6.4866309D, 2.0010789D, 23.094814400000001D,
                    -1117.4411152D, -1.4699811D },
            { -7.5909506D, 1.1180895D, 744.56512269999996D, 148.1152635D, 5.7586042D, 2.3010789D, 23.838732100000001D,
                    -1169.9526956D, -1.5229775D },
            { -7.2909506D, 1.1180895D, 715.71085419999997D, 171.99884599999999D, 4.8356764D, 2.6010789D, 22.4546861D,
                    -1142.5200199000001D, -1.559265D },
            { -6.9909506D, 1.1180895D, 686.85658569999998D, 200.29154579999999D, 3.6574486D, 2.9010789D,
                    19.195620900000002D, -1032.9259595000001D, -1.5784544D },
            { -6.6909506D, 1.1180895D, 658.00231719999999D, 234.27778710000001D, 2.1401053D, 3.2010789D,
                    14.498020800000001D, -849.41659360000006D, -1.5803442D },
            { -6.3909506D, 1.1180895D, 629.1480487D, 275.7268449D, 0.1651599D, 3.5010789D, 8.939183999999999D,
                    -610.86708759999999D, -1.5649228D },
            { -6.0909506D, 1.1180895D, 600.29378029999998D, 327.13300479999998D, -2.4385346D, 3.8010789D, 3.1807125D,
                    -345.6094129D, -1.5323681D },
            { -5.7909506D, 1.1180895D, 571.43951179999999D, 392.10647540000002D, -5.9244687D, 4.1010789D, -2.0962499D,
                    -88.970552900000001D, -1.4830451D },
            { -4.0953997D, 1.4449038D, 458.56355880000001D, 3.0597946D, 8.9830009D, -1.3012877D, -32.478885499999997D,
                    286.94478320000002D, -0.0622D },
            { -3.7953997D, 1.4449038D, 428.72884740000001D, 11.2644424D, 8.961452899999999D, -1.0012877D,
                    -32.995411400000002D, 260.91884479999999D, -0.2273342D },
            { -3.4953997D, 1.4449038D, 398.89413589999998D, 19.625597299999999D, 8.914397900000001D, -0.7012877D,
                    -31.3393291D, 224.21468569999999D, -0.3899653D },
            { -3.1953997D, 1.4449038D, 369.05942440000001D, 28.2628214D, 8.839864800000001D, -0.4012877D, -27.5552831D,
                    164.2051807D, -0.5483032D },
            { -2.8953997D, 1.4449038D, 339.22471300000001D, 37.305238799999998D, 8.734694899999999D, -0.1012877D,
                    -21.8962179D, 71.084868299999997D, -0.7006056000000001D },
            { -2.5953997D, 1.4449038D, 309.39000149999998D, 46.896400399999997D, 8.5943557D, 0.1987123D,
                    -14.798617800000001D, -59.994703199999996D, -0.8451975000000001D },
            { -2.2953997D, 1.4449038D, 279.55529000000001D, 57.2001287D, 8.412664700000001D, 0.4987123D, -6.839781D,
                    -227.47731429999999D, -0.9804902D },
            { -1.9953997D, 1.4449038D, 249.72057860000001D, 68.407805800000006D, 8.1813959D, 0.7987123D, 1.3186905D,
                    -422.87905460000002D, -1.1049982D },
            { -1.6953997D, 1.4449038D, 219.88586710000001D, 80.747736000000003D, 7.8897273D, 1.0987123D, 8.9956529D,
                    -631.30725080000002D, -1.2173562D },
            { -1.3953997D, 1.4449038D, 190.05115559999999D, 94.497482399999996D, 7.5234689D, 1.3987123D,
                    15.557096100000001D, -833.00104099999999D, -1.3163339D },
            { -1.0953997D, 1.4449038D, 160.21644420000001D, 110.00047410000001D, 7.0639795D, 1.6987123D,
                    20.478204699999999D, -1005.7346897D, -1.4008494D },
            { -0.7953997D, 1.4449038D, 130.38173269999999D, 127.6888198D, 6.4866309D, 1.9987123D, 23.394730500000001D,
                    -1127.8024849000001D, -1.4699811D },
            { -0.4953997D, 1.4449038D, 100.5470212D, 148.1152635D, 5.7586042D, 2.2987123D, 24.138648199999999D,
                    -1181.2138136000001D, -1.5229775D },
            { -0.1953997D, 1.4449038D, 70.7123098D, 171.99884599999999D, 4.8356764D, 2.5987123D, 22.754602200000001D,
                    -1154.6808864D, -1.559265D },
            { 0.1046003D, 1.4449038D, 40.877598300000002D, 200.29154579999999D, 3.6574486D, 2.8987123D,
                    19.495536999999999D, -1045.9865741999999D, -1.5784544D },
            { 0.4046003D, 1.4449038D, 11.0428868D, 234.27778710000001D, 2.1401053D, 3.1987123D, 14.7979369D,
                    -863.37695670000005D, -1.5803442D },
            { 0.7046003D, 1.4449038D, -18.791824600000002D, 275.7268449D, 0.1651599D, 3.4987123D, 9.2391001D,
                    -625.72719910000001D, -1.5649228D },
            { 1.0046003D, 1.4449038D, -48.626536100000003D, 327.13300479999998D, -2.4385346D, 3.7987123D, 3.4806286D,
                    -361.36927279999998D, -1.5323681D },
            { 1.3046003D, 1.4449038D, -78.461247599999993D, 392.10647540000002D, -5.9244687D, 4.0987123D, -1.7963338D,
                    -105.6301611D, -1.4830451D },
            { 2.8927451D, 1.6914144D, -184.87363250000001D, 3.0597946D, 8.9830009D, -1.3045357D, -32.1790278D,
                    286.56898660000002D, -0.0622D },
            { 3.1927451D, 1.6914144D, -215.4478757D, 11.2644424D, 8.961452899999999D, -1.0045357D, -32.695553699999998D,
                    259.64347509999999D, -0.2273342D },
            { 3.4927451D, 1.6914144D, -246.02211879999999D, 19.625597299999999D, 8.914397900000001D, -0.7045357D,
                    -31.0394714D, 222.03974270000001D, -0.3899653D },
            { 3.7927451D, 1.6914144D, -276.596362D, 28.2628214D, 8.839864800000001D, -0.4045357D, -27.2554254D,
                    161.13066459999999D, -0.5483032D },
            { 4.0927451D, 1.6914144D, -307.17060520000001D, 37.305238799999998D, 8.734694899999999D, -0.1045357D,
                    -21.596360099999998D, 67.110779100000002D, -0.7006056000000001D },
            { 4.3927451D, 1.6914144D, -337.7448483D, 46.896400399999997D, 8.5943557D, 0.1954643D, -14.498760000000001D,
                    -64.868365600000004D, -0.8451975000000001D },
            { 4.6927451D, 1.6914144D, -368.31909150000001D, 57.2001287D, 8.412664700000001D, 0.4954643D, -6.5399232D,
                    -233.25054990000001D, -0.9804902D },
            { 4.9927451D, 1.6914144D, -398.89333470000003D, 68.407805800000006D, 8.1813959D, 0.7954643D, 1.6185482D,
                    -429.55186329999998D, -1.1049982D },
            { 5.2927451D, 1.6914144D, -429.46757789999998D, 80.747736000000003D, 7.8897273D, 1.0954643D, 9.2955106D,
                    -638.87963279999997D, -1.2173562D },
            { 5.5927451D, 1.6914144D, -460.04182100000003D, 94.497482399999996D, 7.5234689D, 1.3954643D,
                    15.856953799999999D, -841.47299610000005D, -1.3163339D },
            { 5.8927451D, 1.6914144D, -490.61606419999998D, 110.00047410000001D, 7.0639795D, 1.6954643D, 20.7780624D,
                    -1015.106218D, -1.4008494D },
            { 6.1927451D, 1.6914144D, -521.19030740000005D, 127.6888198D, 6.4866309D, 1.9954643D, 23.694588199999998D,
                    -1138.0735863D, -1.4699811D },
            { 6.4927451D, 1.6914144D, -551.76455050000004D, 148.1152635D, 5.7586042D, 2.2954643D, 24.438505899999999D,
                    -1192.3844882000001D, -1.5229775D },
            { 6.7927451D, 1.6914144D, -582.3387937D, 171.99884599999999D, 4.8356764D, 2.5954643D, 23.054459900000001D,
                    -1166.7511340999999D, -1.559265D },
            { 7.0927451D, 1.6914144D, -612.91303689999995D, 200.29154579999999D, 3.6574486D, 2.8954643D,
                    19.795394699999999D, -1058.9563952000001D, -1.5784544D },
            { 7.3927451D, 1.6914144D, -643.48728000000006D, 234.27778710000001D, 2.1401053D, 3.1954643D, 15.0977946D,
                    -877.24635079999996D, -1.5803442D },
            { 7.6927451D, 1.6914144D, -674.06152320000001D, 275.7268449D, 0.1651599D, 3.4954643D, 9.5389578D,
                    -640.49616639999999D, -1.5649228D },
            { 7.9927451D, 1.6914144D, -704.63576639999997D, 327.13300479999998D, -2.4385346D, 3.7954643D, 3.7804864D,
                    -377.03781320000002D, -1.5323681D },
            { 8.292745099999999D, 1.6914144D, -735.21000960000003D, 392.10647540000002D, -5.9244687D, 4.0954643D,
                    -1.4964761D, -122.1982747D, -1.4830451D },
            { 9.1476408D, 1.8628107D, -779.40997809999999D, 3.0597946D, 8.9830009D, -1.3094009D, -31.879307300000001D,
                    286.35694819999998D, -0.0622D },
            { 9.4476408D, 1.8628107D, -810.49841040000001D, 11.2644424D, 8.961452899999999D, -1.0094009D,
                    -32.395833199999998D, 258.53227509999999D, -0.2273342D },
            { 9.747640799999999D, 1.8628107D, -841.58684259999995D, 19.625597299999999D, 8.914397900000001D,
                    -0.7094009D, -30.739750900000001D, 220.02938109999999D, -0.3899653D },
            { 10.0476408D, 1.8628107D, -872.67527489999998D, 28.2628214D, 8.839864800000001D, -0.4094009D,
                    -26.955704900000001D, 158.22114149999999D, -0.5483032D },
            { 10.347640800000001D, 1.8628107D, -903.76370710000003D, 37.305238799999998D, 8.734694899999999D,
                    -0.1094009D, -21.296639599999999D, 63.302094400000001D, -0.7006056000000001D },
            { 10.6476408D, 1.8628107D, -934.85213940000006D, 46.896400399999997D, 8.5943557D, 0.1905991D, -14.1990395D,
                    -69.576211799999996D, -0.8451975000000001D },
            { 10.9476408D, 1.8628107D, -965.9405716D, 57.2001287D, 8.412664700000001D, 0.4905991D, -6.2402027D,
                    -238.85755760000001D, -0.9804902D },
            { 11.247640799999999D, 1.8628107D, -997.02900390000002D, 68.407805800000006D, 8.1813959D, 0.7905991D,
                    1.9182687D, -436.05803270000001D, -1.1049982D },
            { 11.5476408D, 1.8628107D, -1028.1174361000001D, 80.747736000000003D, 7.8897273D, 1.0905991D, 9.5952311D,
                    -646.28496359999997D, -1.2173562D },
            { 11.847640800000001D, 1.8628107D, -1059.2058684000001D, 94.497482399999996D, 7.5234689D, 1.3905991D,
                    16.156674299999999D, -849.7774885D, -1.3163339D },
            { 12.1476408D, 1.8628107D, -1090.2943006D, 110.00047410000001D, 7.0639795D, 1.6905991D, 21.077782899999999D,
                    -1024.3098719D, -1.4008494D },
            { 12.4476408D, 1.8628107D, -1121.3827329000001D, 127.6888198D, 6.4866309D, 1.9905991D, 23.994308799999999D,
                    -1148.1764017999999D, -1.4699811D },
            { 12.747640799999999D, 1.8628107D, -1152.4711651D, 148.1152635D, 5.7586042D, 2.2905991D, 24.7382265D,
                    -1203.3864653000001D, -1.5229775D },
            { 13.0476408D, 1.8628107D, -1183.5595973D, 171.99884599999999D, 4.8356764D, 2.5905991D, 23.354180499999998D,
                    -1178.6522726999999D, -1.559265D },
            { 13.347640800000001D, 1.8628107D, -1214.6480296D, 200.29154579999999D, 3.6574486D, 2.8905991D,
                    20.095115199999999D, -1071.7566953D, -1.5784544D },
            { 13.6476408D, 1.8628107D, -1245.7364617999999D, 234.27778710000001D, 2.1401053D, 3.1905991D, 15.3975151D,
                    -890.94581249999999D, -1.5803442D },
            { 13.9476408D, 1.8628107D, -1276.8248940999999D, 275.7268449D, 0.1651599D, 3.4905991D, 9.8386783D,
                    -655.09478960000001D, -1.5649228D },
            { 14.247640799999999D, 1.8628107D, -1307.9133263000001D, 327.13300479999998D, -2.4385346D, 3.7905991D,
                    4.0802069D, -392.53559799999999D, -1.5323681D },
            { 14.5476408D, 1.8628107D, -1339.0017585999999D, 392.10647540000002D, -5.9244687D, 4.0905991D, -1.1967556D,
                    -138.5952211D, -1.4830451D },
            { 14.1144778D, 1.9715174D, -1266.3088279000001D, 3.0597946D, 8.9830009D, -1.317945D, -31.5800248D,
                    286.52123410000002D, -0.0622D },
            { 14.4144778D, 1.9715174D, -1297.7233801D, 11.2644424D, 8.961452899999999D, -1.017945D,
                    -32.096550700000002D, 257.79871370000001D, -0.2273342D },
            { 14.714477799999999D, 1.9715174D, -1329.1379323000001D, 19.625597299999999D, 8.914397900000001D,
                    -0.7179450000000001D, -30.4404684D, 218.39797239999999D, -0.3899653D },
            { 15.0144778D, 1.9715174D, -1360.5524846000001D, 28.2628214D, 8.839864800000001D, -0.417945D, -26.6564224D,
                    155.69188539999999D, -0.5483032D },
            { 15.314477800000001D, 1.9715174D, -1391.9670368D, 37.305238799999998D, 8.734694899999999D, -0.117945D,
                    -20.9973572D, 59.8749909D, -0.7006056000000001D },
            { 15.6144778D, 1.9715174D, -1423.3815890000001D, 46.896400399999997D, 8.5943557D, 0.182055D, -13.8997571D,
                    -73.901162600000006D, -0.8451975000000001D },
            { 15.9144778D, 1.9715174D, -1454.7961412D, 57.2001287D, 8.412664700000001D, 0.482055D, -5.9409203D,
                    -244.08035580000001D, -0.9804902D },
            { 16.214477800000001D, 1.9715174D, -1486.2106934999999D, 68.407805800000006D, 8.1813959D,
                    0.7820549999999999D, 2.2175512D, -442.17867819999998D, -1.1049982D },
            { 16.514477800000002D, 1.9715174D, -1517.6252457000001D, 80.747736000000003D, 7.8897273D, 1.082055D,
                    9.8945136D, -653.30345650000004D, -1.2173562D },
            { 16.814477799999999D, 1.9715174D, -1549.0397978999999D, 94.497482399999996D, 7.5234689D, 1.382055D,
                    16.455956799999999D, -857.69382870000004D, -1.3163339D },
            { 17.1144778D, 1.9715174D, -1580.4543501999999D, 110.00047410000001D, 7.0639795D, 1.682055D,
                    21.377065300000002D, -1033.1240594999999D, -1.4008494D },
            { 17.4144778D, 1.9715174D, -1611.8689024D, 127.6888198D, 6.4866309D, 1.982055D, 24.293591200000002D,
                    -1157.8884367000001D, -1.4699811D },
            { 17.714477800000001D, 1.9715174D, -1643.2834545999999D, 148.1152635D, 5.7586042D, 2.282055D,
                    25.037508899999999D, -1213.9963475D, -1.5229775D },
            { 18.014477800000002D, 1.9715174D, -1674.6980069000001D, 171.99884599999999D, 4.8356764D, 2.582055D,
                    23.653462900000001D, -1190.1600023000001D, -1.559265D },
            { 18.314477799999999D, 1.9715174D, -1706.1125591D, 200.29154579999999D, 3.6574486D, 2.882055D,
                    20.394397699999999D, -1084.1622723D, -1.5784544D },
            { 18.6144778D, 1.9715174D, -1737.5271112999999D, 234.27778710000001D, 2.1401053D, 3.182055D, 15.6967976D,
                    -904.24923690000003D, -1.5803442D },
            { 18.9144778D, 1.9715174D, -1768.9416635D, 275.7268449D, 0.1651599D, 3.482055D, 10.1379608D,
                    -669.29606130000002D, -1.5649228D },
            { 19.214477800000001D, 1.9715174D, -1800.3562158D, 327.13300479999998D, -2.4385346D, 3.782055D, 4.3794893D,
                    -407.63471709999999D, -1.5323681D },
            { 19.514477800000002D, 1.9715174D, -1831.7707680000001D, 392.10647540000002D, -5.9244687D, 4.082055D,
                    -0.8974731D, -154.59218749999999D, -1.4830451D },
            { 17.360124800000001D, 2.0349358D, -1594.1434873000001D, 3.0597946D, 8.9830009D, -1.3406815D,
                    -31.283757600000001D, 288.1527509D, -0.0622D },
            { 17.660124799999998D, 2.0349358D, -1625.7482947999999D, 11.2644424D, 8.961452899999999D, -1.0406815D,
                    -31.800283499999999D, 258.54142869999998D, -0.2273342D },
            { 17.960124799999999D, 2.0349358D, -1657.3531022D, 19.625597299999999D, 8.914397900000001D, -0.7406815D,
                    -30.144201200000001D, 218.2518858D, -0.3899653D },
            { 18.2601248D, 2.0349358D, -1688.9579096D, 28.2628214D, 8.839864800000001D, -0.4406815D,
                    -26.360155200000001D, 154.65699699999999D, -0.5483032D },
            { 18.560124800000001D, 2.0349358D, -1720.562717D, 37.305238799999998D, 8.734694899999999D, -0.1406815D,
                    -20.701089899999999D, 57.951300799999999D, -0.7006056000000001D },
            { 18.860124800000001D, 2.0349358D, -1752.1675244999999D, 46.896400399999997D, 8.5943557D, 0.1593185D,
                    -13.6034898D, -76.713654500000004D, -0.8451975000000001D },
            { 19.160124799999998D, 2.0349358D, -1783.7723318999999D, 57.2001287D, 8.412664700000001D, 0.4593185D,
                    -5.644653D, -247.7816493D, -0.9804902D },
            { 19.460124799999999D, 2.0349358D, -1815.3771393D, 68.407805800000006D, 8.1813959D, 0.7593185D, 2.5138184D,
                    -446.76877339999999D, -1.1049982D },
            { 19.7601248D, 2.0349358D, -1846.9819467D, 80.747736000000003D, 7.8897273D, 1.0593185D, 10.190780800000001D,
                    -658.7823535D, -1.2173562D },
            { 20.060124800000001D, 2.0349358D, -1878.5867542000001D, 94.497482399999996D, 7.5234689D, 1.3593185D,
                    16.752223999999998D, -864.06152740000005D, -1.3163339D },
            { 20.360124800000001D, 2.0349358D, -1910.1915616000001D, 110.00047410000001D, 7.0639795D, 1.6593185D,
                    21.673332599999998D, -1040.3805600000001D, -1.4008494D },
            { 20.660124799999998D, 2.0349358D, -1941.7963689999999D, 127.6888198D, 6.4866309D, 1.9593185D,
                    24.589858499999998D, -1166.0337388999999D, -1.4699811D },
            { 20.960124799999999D, 2.0349358D, -1973.4011763999999D, 148.1152635D, 5.7586042D, 2.2593185D,
                    25.333776100000001D, -1223.0304515D, -1.5229775D },
            { 21.2601248D, 2.0349358D, -2005.0059838D, 171.99884599999999D, 4.8356764D, 2.5593185D, 23.949730200000001D,
                    -1200.0829080000001D, -1.559265D },
            { 21.560124800000001D, 2.0349358D, -2036.6107913000001D, 200.29154579999999D, 3.6574486D, 2.8593185D,
                    20.690664900000002D, -1094.9739797D, -1.5784544D },
            { 21.860124800000001D, 2.0349358D, -2068.2155987000001D, 234.27778710000001D, 2.1401053D, 3.1593185D,
                    15.993064800000001D, -915.949746D, -1.5803442D },
            { 22.160124799999998D, 2.0349358D, -2099.8204061000001D, 275.7268449D, 0.1651599D, 3.4593185D,
                    10.434227999999999D, -681.88537210000004D, -1.5649228D },
            { 22.460124799999999D, 2.0349358D, -2131.4252135000002D, 327.13300479999998D, -2.4385346D, 3.7593185D,
                    4.6757566D, -421.1128296D, -1.5323681D },
            { 22.7601248D, 2.0349358D, -2163.030021D, 392.10647540000002D, -5.9244687D, 4.0593185D, -0.6012059D,
                    -168.95910180000001D, -1.4830451D },
            { 18.693020000000001D, 2.1002653D, -1732.8007089D, 3.0597946D, 8.9830009D, -1.2405597D,
                    -30.942058100000001D, 276.9371337D, -0.0622D },
            { 18.993020000000001D, 2.1002653D, -1764.6015047000001D, 11.2644424D, 8.961452899999999D, -0.9405597D,
                    -31.458583900000001D, 246.30071290000001D, -0.2273342D },
            { 19.293019999999999D, 2.1002653D, -1796.4023004000001D, 19.625597299999999D, 8.914397900000001D,
                    -0.6405597D, -29.802501599999999D, 204.98607129999999D, -0.3899653D },
            { 19.593019999999999D, 2.1002653D, -1828.2030961999999D, 28.2628214D, 8.839864800000001D, -0.3405597D,
                    -26.018455599999999D, 140.366084D, -0.5483032D },
            { 19.89302D, 2.1002653D, -1860.003892D, 37.305238799999998D, 8.734694899999999D, -0.0405597D,
                    -20.359390399999999D, 42.635289200000003D, -0.7006056000000001D },
            { 20.193020000000001D, 2.1002653D, -1891.8046876999999D, 46.896400399999997D, 8.5943557D, 0.2594403D,
                    -13.261790299999999D, -93.054764700000007D, -0.8451975000000001D },
            { 20.493020000000001D, 2.1002653D, -1923.6054835D, 57.2001287D, 8.412664700000001D, 0.5594403D, -5.3029535D,
                    -265.14785810000001D, -0.9804902D },
            { 20.793019999999999D, 2.1002653D, -1955.4062792D, 68.407805800000006D, 8.1813959D, 0.8594403000000001D,
                    2.8555179D, -465.1600808D, -1.1049982D },
            { 21.093019999999999D, 2.1002653D, -1987.207075D, 80.747736000000003D, 7.8897273D, 1.1594403D,
                    10.532480400000001D, -678.19875950000005D, -1.2173562D },
            { 21.39302D, 2.1002653D, -2019.0078707D, 94.497482399999996D, 7.5234689D, 1.4594403D, 17.0939236D,
                    -884.50303199999996D, -1.3163339D },
            { 21.693020000000001D, 2.1002653D, -2050.8086665000001D, 110.00047410000001D, 7.0639795D, 1.7594403D,
                    22.015032099999999D, -1061.8471631D, -1.4008494D },
            { 21.993020000000001D, 2.1002653D, -2082.6094622999999D, 127.6888198D, 6.4866309D, 2.0594403D,
                    24.931557999999999D, -1188.5254407D, -1.4699811D },
            { 22.293019999999999D, 2.1002653D, -2114.4102579999999D, 148.1152635D, 5.7586042D, 2.3594403D, 25.6754757D,
                    -1246.5472517999999D, -1.5229775D },
            { 22.593019999999999D, 2.1002653D, -2146.2110538000002D, 171.99884599999999D, 4.8356764D, 2.6594403D,
                    24.291429699999998D, -1224.6248069999999D, -1.559265D },
            { 22.89302D, 2.1002653D, -2178.0118495000002D, 200.29154579999999D, 3.6574486D, 2.9594403D, 21.0323645D,
                    -1120.5409772999999D, -1.5784544D },
            { 23.193020000000001D, 2.1002653D, -2209.8126453D, 234.27778710000001D, 2.1401053D, 3.2594403D, 16.3347643D,
                    -942.54184210000005D, -1.5803442D },
            { 23.493020000000001D, 2.1002653D, -2241.613441D, 275.7268449D, 0.1651599D, 3.5594403D, 10.7759275D,
                    -709.50256690000003D, -1.5649228D },
            { 23.793019999999999D, 2.1002653D, -2273.4142367999998D, 327.13300479999998D, -2.4385346D, 3.8594403D,
                    5.0174561D, -449.75512300000003D, -1.5323681D },
            { 24.093019999999999D, 2.1002653D, -2305.2150324999998D, 392.10647540000002D, -5.9244687D, 4.1594403D,
                    -0.2595063D, -198.6264937D, -1.4830451D },
            { 17.9448291D, 2.1292101D, -1652.6782860000001D, 3.0597946D, 8.9830009D, -1.2205669D, -30.6815417D,
                    274.22234049999997D, -0.0622D },
            { 18.2448291D, 2.1292101D, -1684.5659165D, 11.2644424D, 8.961452899999999D, -0.9205669D,
                    -31.198067500000001D, 242.8043705D, -0.2273342D },
            { 18.544829100000001D, 2.1292101D, -1716.4535469D, 19.625597299999999D, 8.914397900000001D, -0.6205669D,
                    -29.541985199999999D, 200.70817959999999D, -0.3899653D },
            { 18.844829099999998D, 2.1292101D, -1748.3411773D, 28.2628214D, 8.839864800000001D, -0.3205669D,
                    -25.757939199999999D, 135.3066431D, -0.5483032D },
            { 19.144829099999999D, 2.1292101D, -1780.2288077999999D, 37.305238799999998D, 8.734694899999999D,
                    -0.0205669D, -20.098873999999999D, 36.794299100000003D, -0.7006056000000001D },
            { 19.4448291D, 2.1292101D, -1812.1164381999999D, 46.896400399999997D, 8.5943557D, 0.2794331D,
                    -13.001273899999999D, -99.677304100000001D, -0.8451975000000001D },
            { 19.7448291D, 2.1292101D, -1844.0040687000001D, 57.2001287D, 8.412664700000001D, 0.5794331D, -5.0424371D,
                    -272.5519468D, -0.9804902D },
            { 20.044829100000001D, 2.1292101D, -1875.8916991000001D, 68.407805800000006D, 8.1813959D, 0.8794331D,
                    3.1160344D, -473.34571870000002D, -1.1049982D },
            { 20.344829099999998D, 2.1292101D, -1907.7793294999999D, 80.747736000000003D, 7.8897273D, 1.1794331D,
                    10.792996799999999D, -687.16594659999998D, -1.2173562D },
            { 20.644829099999999D, 2.1292101D, -1939.66696D, 94.497482399999996D, 7.5234689D, 1.4794331D, 17.35444D,
                    -894.25176829999998D, -1.3163339D },
            { 20.9448291D, 2.1292101D, -1971.5545904000001D, 110.00047410000001D, 7.0639795D, 1.7794331D,
                    22.275548499999999D, -1072.3774487000001D, -1.4008494D },
            { 21.2448291D, 2.1292101D, -2003.4422208999999D, 127.6888198D, 6.4866309D, 2.0794331D, 25.192074399999999D,
                    -1199.8372755D, -1.4699811D },
            { 21.544829100000001D, 2.1292101D, -2035.3298513D, 148.1152635D, 5.7586042D, 2.3794331D, 25.9359921D,
                    -1258.6406357999999D, -1.5229775D },
            { 21.844829099999998D, 2.1292101D, -2067.2174817D, 171.99884599999999D, 4.8356764D, 2.6794331D,
                    24.551946099999999D, -1237.4997401999999D, -1.559265D },
            { 22.144829099999999D, 2.1292101D, -2099.1051121999999D, 200.29154579999999D, 3.6574486D, 2.9794331D,
                    21.2928809D, -1134.1974597000001D, -1.5784544D },
            { 22.4448291D, 2.1292101D, -2130.9927425999999D, 234.27778710000001D, 2.1401053D, 3.2794331D, 16.5952807D,
                    -956.97987379999995D, -1.5803442D },
            { 22.7448291D, 2.1292101D, -2162.8803730999998D, 275.7268449D, 0.1651599D, 3.5794331D, 11.036443999999999D,
                    -724.72214780000002D, -1.5649228D },
            { 23.044829100000001D, 2.1292101D, -2194.7680034999998D, 327.13300479999998D, -2.4385346D, 3.8794331D,
                    5.2779725D, -465.75625309999998D, -1.5323681D },
            { 23.344829099999998D, 2.1292101D, -2226.6556338999999D, 392.10647540000002D, -5.9244687D, 4.1794331D,
                    0.0010101D, -215.4091731D, -1.4830451D },
            { 15.2710638D, 2.2076295D, -1358.6295872000001D, 3.0597946D, 8.9830009D, -1.2355774D, -30.379691000000001D,
                    275.19345340000001D, -0.0622D },
            { 15.571063799999999D, 2.2076295D, -1390.7524757000001D, 11.2644424D, 8.961452899999999D, -0.9355774D,
                    -30.896216800000001D, 242.86993129999999D, -0.2273342D },
            { 15.8710638D, 2.2076295D, -1422.8753641999999D, 19.625597299999999D, 8.914397900000001D, -0.6355774D,
                    -29.2401345D, 199.86818840000001D, -0.3899653D },
            { 16.171063799999999D, 2.2076295D, -1454.9982527D, 28.2628214D, 8.839864800000001D, -0.3355774D,
                    -25.4560885D, 133.5610997D, -0.5483032D },
            { 16.4710638D, 2.2076295D, -1487.1211412D, 37.305238799999998D, 8.734694899999999D, -0.0355774D,
                    -19.797023299999999D, 34.1432036D, -0.7006056000000001D },
            { 16.7710638D, 2.2076295D, -1519.2440297000001D, 46.896400399999997D, 8.5943557D, 0.2644226D, -12.6994232D,
                    -103.2339516D, -0.8451975000000001D },
            { 17.071063800000001D, 2.2076295D, -1551.3669182000001D, 57.2001287D, 8.412664700000001D, 0.5644226D,
                    -4.7405864D, -277.01414640000002D, -0.9804902D },
            { 17.371063800000002D, 2.2076295D, -1583.4898066999999D, 68.407805800000006D, 8.1813959D, 0.8644226D,
                    3.4178851D, -478.71347050000003D, -1.1049982D },
            { 17.671063799999999D, 2.2076295D, -1615.6126951000001D, 80.747736000000003D, 7.8897273D, 1.1644226D,
                    11.0948475D, -693.43925049999996D, -1.2173562D },
            { 17.9710638D, 2.2076295D, -1647.7355835999999D, 94.497482399999996D, 7.5234689D, 1.4644226D, 17.6562907D,
                    -901.43062429999998D, -1.3163339D },
            { 18.2710638D, 2.2076295D, -1679.8584721D, 110.00047410000001D, 7.0639795D, 1.7644226D, 22.577399199999999D,
                    -1080.4618568000001D, -1.4008494D },
            { 18.571063800000001D, 2.2076295D, -1711.9813606D, 127.6888198D, 6.4866309D, 2.0644226D,
                    25.493925099999998D, -1208.8272357000001D, -1.4699811D },
            { 18.871063800000002D, 2.2076295D, -1744.1042491000001D, 148.1152635D, 5.7586042D, 2.3644226D,
                    26.237842799999999D, -1268.5361482000001D, -1.5229775D },
            { 19.171063799999999D, 2.2076295D, -1776.2271376000001D, 171.99884599999999D, 4.8356764D, 2.6644226D,
                    24.853796800000001D, -1248.3008046D, -1.559265D },
            { 19.4710638D, 2.2076295D, -1808.3500260999999D, 200.29154579999999D, 3.6574486D, 2.9644226D,
                    21.594731599999999D, -1145.9040762D, -1.5784544D },
            { 19.7710638D, 2.2076295D, -1840.4729146D, 234.27778710000001D, 2.1401053D, 3.2644226D, 16.8971315D,
                    -969.59204250000005D, -1.5803442D },
            { 20.071063800000001D, 2.2076295D, -1872.5958031D, 275.7268449D, 0.1651599D, 3.5644226D,
                    11.338294700000001D, -738.23986849999994D, -1.5649228D },
            { 20.371063800000002D, 2.2076295D, -1904.7186916000001D, 327.13300479999998D, -2.4385346D, 3.8644226D,
                    5.5798232D, -480.17952600000001D, -1.5323681D },
            { 20.671063799999999D, 2.2076295D, -1936.8415801000001D, 392.10647540000002D, -5.9244687D, 4.1644226D,
                    0.3028608D, -230.737998D, -1.4830451D },
            { 11.013004499999999D, 2.3359621D, -877.67167619999998D, 3.0597946D, 8.9830009D, -1.2424793D,
                    -30.079189199999998D, 275.29698309999998D, -0.0622D },
            { 11.3130045D, 2.3359621D, -910.17956249999997D, 11.2644424D, 8.961452899999999D, -0.9424793D, -30.5957151D,
                    242.07195580000001D, -0.2273342D },
            { 11.613004500000001D, 2.3359621D, -942.68744890000005D, 19.625597299999999D, 8.914397900000001D,
                    -0.6424793D, -28.939632799999998D, 198.1687076D, -0.3899653D },
            { 11.9130045D, 2.3359621D, -975.19533520000005D, 28.2628214D, 8.839864800000001D, -0.3424793D,
                    -25.155586799999998D, 130.96011369999999D, -0.5483032D },
            { 12.2130045D, 2.3359621D, -1007.7032215D, 37.305238799999998D, 8.734694899999999D, -0.0424793D,
                    -19.496521600000001D, 30.640712400000002D, -0.7006056000000001D },
            { 12.513004499999999D, 2.3359621D, -1040.2111078999999D, 46.896400399999997D, 8.5943557D, 0.2575207D,
                    -12.398921400000001D, -107.6379481D, -0.8451975000000001D },
            { 12.8130045D, 2.3359621D, -1072.7189942D, 57.2001287D, 8.412664700000001D, 0.5575207D, -4.4400846D,
                    -282.31964809999999D, -0.9804902D },
            { 13.113004500000001D, 2.3359621D, -1105.2268805000001D, 68.407805800000006D, 8.1813959D, 0.8575207D,
                    3.7183868D, -484.92047730000002D, -1.1049982D },
            { 13.4130045D, 2.3359621D, -1137.7347669000001D, 80.747736000000003D, 7.8897273D, 1.1575207D, 11.3953492D,
                    -700.54776249999998D, -1.2173562D },
            { 13.7130045D, 2.3359621D, -1170.2426531999999D, 94.497482399999996D, 7.5234689D, 1.4575207D,
                    17.956792400000001D, -909.44064160000005D, -1.3163339D },
            { 14.013004499999999D, 2.3359621D, -1202.7505395999999D, 110.00047410000001D, 7.0639795D, 1.7575207D,
                    22.877901000000001D, -1089.3733792999999D, -1.4008494D },
            { 14.3130045D, 2.3359621D, -1235.2584259D, 127.6888198D, 6.4866309D, 2.0575207D, 25.7944268D,
                    -1218.6402634000001D, -1.4699811D },
            { 14.613004500000001D, 2.3359621D, -1267.7663121999999D, 148.1152635D, 5.7586042D, 2.3575207D,
                    26.538344500000001D, -1279.2506811000001D, -1.5229775D },
            { 14.9130045D, 2.3359621D, -1300.2741986000001D, 171.99884599999999D, 4.8356764D, 2.6575207D,
                    25.154298499999999D, -1259.9168428D, -1.559265D },
            { 15.2130045D, 2.3359621D, -1332.7820849D, 200.29154579999999D, 3.6574486D, 2.9575207D, 21.895233300000001D,
                    -1158.4216196D, -1.5784544D },
            { 15.513004499999999D, 2.3359621D, -1365.2899712000001D, 234.27778710000001D, 2.1401053D, 3.2575207D,
                    17.197633199999999D, -983.01109099999996D, -1.5803442D },
            { 15.8130045D, 2.3359621D, -1397.7978576D, 275.7268449D, 0.1651599D, 3.5575207D, 11.6387964D,
                    -752.56042230000003D, -1.5649228D },
            { 16.113004499999999D, 2.3359621D, -1430.3057438999999D, 327.13300479999998D, -2.4385346D, 3.8575207D,
                    5.8803249D, -495.40158500000001D, -1.5323681D },
            { 16.4130045D, 2.3359621D, -1462.8136303000001D, 392.10647540000002D, -5.9244687D, 4.1575207D, 0.6033625D,
                    -246.8615623D, -1.4830451D },
            { 5.6431514D, 2.5315694D, -255.15657139999999D, 3.0597946D, 8.9830009D, -1.2466774D, -29.7789708D,
                    275.10836360000002D, -0.0622D },
            { 5.9431514D, 2.5315694D, -288.25127950000001D, 11.2644424D, 8.961452899999999D, -0.9466774D,
                    -30.295496700000001D, 240.98268100000001D, -0.2273342D },
            { 6.2431514D, 2.5315694D, -321.3459876D, 19.625597299999999D, 8.914397900000001D, -0.6466774D, -28.6394144D,
                    196.17877759999999D, -0.3899653D },
            { 6.5431514D, 2.5315694D, -354.44069569999999D, 28.2628214D, 8.839864800000001D, -0.3466774D, -24.8553684D,
                    128.06952849999999D, -0.5483032D },
            { 6.8431514D, 2.5315694D, -387.53540390000001D, 37.305238799999998D, 8.734694899999999D, -0.0466774D,
                    -19.196303100000002D, 26.849471900000001D, -0.7006056000000001D },
            { 7.1431514D, 2.5315694D, -420.630112D, 46.896400399999997D, 8.5943557D, 0.2533226D, -12.098703D,
                    -112.32984380000001D, -0.8451975000000001D },
            { 7.4431514D, 2.5315694D, -453.72482009999999D, 57.2001287D, 8.412664700000001D, 0.5533226D, -4.1398662D,
                    -287.91219910000001D, -0.9804902D },
            { 7.7431514D, 2.5315694D, -486.81952819999998D, 68.407805800000006D, 8.1813959D, 0.8533226D, 4.0186052D,
                    -491.41368360000001D, -1.1049982D },
            { 8.043151399999999D, 2.5315694D, -519.91423629999997D, 80.747736000000003D, 7.8897273D, 1.1533226D,
                    11.6955676D, -707.94162410000001D, -1.2173562D },
            { 8.3431514D, 2.5315694D, -553.00894449999998D, 94.497482399999996D, 7.5234689D, 1.4533226D, 18.2570108D,
                    -917.73515850000001D, -1.3163339D },
            { 8.643151400000001D, 2.5315694D, -586.10365260000003D, 110.00047410000001D, 7.0639795D, 1.7533226D,
                    23.1781194D, -1098.5685513999999D, -1.4008494D },
            { 8.9431514D, 2.5315694D, -619.19836069999997D, 127.6888198D, 6.4866309D, 2.0533226D, 26.0946453D,
                    -1228.7360908000001D, -1.4699811D },
            { 9.2431514D, 2.5315694D, -652.29306880000001D, 148.1152635D, 5.7586042D, 2.3533226D, 26.838563000000001D,
                    -1290.2471637000001D, -1.5229775D },
            { 9.543151399999999D, 2.5315694D, -685.38777689999995D, 171.99884599999999D, 4.8356764D, 2.6533226D,
                    25.454516999999999D, -1271.8139807D, -1.559265D },
            { 9.8431514D, 2.5315694D, -718.48248509999996D, 200.29154579999999D, 3.6574486D, 2.9533226D, 22.1954517D,
                    -1171.2194128000001D, -1.5784544D },
            { 10.143151400000001D, 2.5315694D, -751.57719320000001D, 234.27778710000001D, 2.1401053D, 3.2533226D,
                    17.497851600000001D, -996.70953950000001D, -1.5803442D },
            { 10.4431514D, 2.5315694D, -784.67190129999994D, 275.7268449D, 0.1651599D, 3.5533226D, 11.939014800000001D,
                    -767.15952609999999D, -1.5649228D },
            { 10.7431514D, 2.5315694D, -817.76660939999999D, 327.13300479999998D, -2.4385346D, 3.8533226D, 6.1805434D,
                    -510.90134399999999D, -1.5323681D },
            { 11.043151399999999D, 2.5315694D, -850.86131750000004D, 392.10647540000002D, -5.9244687D, 4.1533226D,
                    0.9035809D, -263.2619765D, -1.4830451D },
            { -0.2550329D, 2.804772D, 446.12607400000002D, 3.0597946D, 8.9830009D, -1.2495776D, -29.478853000000001D,
                    274.7781688D, -0.0622D },
            { 0.0449671D, 2.804772D, 412.2117581D, 11.2644424D, 8.961452899999999D, -0.9495776D, -29.995378899999999D,
                    239.75213289999999D, -0.2273342D },
            { 0.3449671D, 2.804772D, 378.29744210000001D, 19.625597299999999D, 8.914397900000001D, -0.6495776D,
                    -28.339296600000001D, 194.04787619999999D, -0.3899653D },
            { 0.6449671D, 2.804772D, 344.38312610000003D, 28.2628214D, 8.839864800000001D, -0.3495776D,
                    -24.555250600000001D, 125.0382737D, -0.5483032D },
            { 0.9449671D, 2.804772D, 310.46881020000001D, 37.305238799999998D, 8.734694899999999D, -0.0495776D,
                    -18.8961854D, 22.917863799999999D, -0.7006056000000001D },
            { 1.2449671D, 2.804772D, 276.55449420000002D, 46.896400399999997D, 8.5943557D, 0.2504224D,
                    -11.798585299999999D, -117.1618052D, -0.8451975000000001D },
            { 1.5449671D, 2.804772D, 242.64017820000001D, 57.2001287D, 8.412664700000001D, 0.5504224D, -3.8397485D,
                    -293.64451380000003D, -0.9804902D },
            { 1.8449671D, 2.804772D, 208.72586219999999D, 68.407805800000006D, 8.1813959D, 0.8504224D, 4.318723D,
                    -498.04635159999998D, -1.1049982D },
            { 2.1449671D, 2.804772D, 174.8115463D, 80.747736000000003D, 7.8897273D, 1.1504224D, 11.995685399999999D,
                    -715.47464539999999D, -1.2173562D },
            { 2.4449671D, 2.804772D, 140.89723029999999D, 94.497482399999996D, 7.5234689D, 1.4504224D,
                    18.557128599999999D, -926.16853300000002D, -1.3163339D },
            { 2.7449671D, 2.804772D, 106.9829143D, 110.00047410000001D, 7.0639795D, 1.7504224D, 23.478237199999999D,
                    -1107.9022792999999D, -1.4008494D },
            { 3.0449671D, 2.804772D, 73.068598399999999D, 127.6888198D, 6.4866309D, 2.0504224D, 26.394763000000001D,
                    -1238.970172D, -1.4699811D },
            { 3.3449671D, 2.804772D, 39.1542824D, 148.1152635D, 5.7586042D, 2.3504224D, 27.138680699999998D,
                    -1301.3815982000001D, -1.5229775D },
            { 3.6449671D, 2.804772D, 5.2399664D, 171.99884599999999D, 4.8356764D, 2.6504224D, 25.7546347D,
                    -1283.8487685D, -1.559265D },
            { 3.9449671D, 2.804772D, -28.674349500000002D, 200.29154579999999D, 3.6574486D, 2.9504224D,
                    22.495569499999998D, -1184.1545539000001D, -1.5784544D },
            { 4.2449671D, 2.804772D, -62.588665499999998D, 234.27778710000001D, 2.1401053D, 3.2504224D,
                    17.797969399999999D, -1010.5450339D, -1.5803442D },
            { 4.5449671D, 2.804772D, -96.502981500000004D, 275.7268449D, 0.1651599D, 3.5504224D, 12.2391326D,
                    -781.89537380000002D, -1.5649228D },
            { 4.8449671D, 2.804772D, -130.4172974D, 327.13300479999998D, -2.4385346D, 3.8504224D, 6.4806611D,
                    -526.53754500000002D, -1.5323681D },
            { 5.1449671D, 2.804772D, -164.33161340000001D, 392.10647540000002D, -5.9244687D, 4.1504224D, 1.2036987D,
                    -279.7985309D, -1.4830451D },
            { -6.045881D, 3.1582262D, 1151.787501D, 3.0597946D, 8.9830009D, -1.2517361D, -29.178781099999998D,
                    274.366309D, -0.0622D },
            { -5.745881D, 3.1582262D, 1116.8128222D, 11.2644424D, 8.961452899999999D, -0.9517361D, -29.695307D,
                    238.44005730000001D, -0.2273342D },
            { -5.445881D, 3.1582262D, 1081.8381434999999D, 19.625597299999999D, 8.914397900000001D, -0.6517361D,
                    -28.039224699999998D, 191.83558479999999D, -0.3899653D },
            { -5.145881D, 3.1582262D, 1046.8634646999999D, 28.2628214D, 8.839864800000001D, -0.3517361D,
                    -24.255178699999998D, 121.92576649999999D, -0.5483032D },
            { -4.845881D, 3.1582262D, 1011.888786D, 37.305238799999998D, 8.734694899999999D, -0.0517361D, -18.5961134D,
                    18.905140800000002D, -0.7006056000000001D },
            { -4.545881D, 3.1582262D, 976.91410719999999D, 46.896400399999997D, 8.5943557D, 0.2482639D,
                    -11.498513300000001D, -122.074744D, -0.8451975000000001D },
            { -4.245881D, 3.1582262D, 941.93942849999996D, 57.2001287D, 8.412664700000001D, 0.5482639D, -3.5396765D,
                    -299.45766839999999D, -0.9804902D },
            { -3.945881D, 3.1582262D, 906.96474969999997D, 68.407805800000006D, 8.1813959D, 0.8482639D, 4.6187949D,
                    -504.75972200000001D, -1.1049982D },
            { -3.645881D, 3.1582262D, 871.99007099999994D, 80.747736000000003D, 7.8897273D, 1.1482639D, 12.2957573D,
                    -723.08823159999997D, -1.2173562D },
            { -3.345881D, 3.1582262D, 837.01539219999995D, 94.497482399999996D, 7.5234689D, 1.4482639D,
                    18.857200500000001D, -934.68233510000005D, -1.3163339D },
            { -3.045881D, 3.1582262D, 802.04071350000004D, 110.00047410000001D, 7.0639795D, 1.7482639D,
                    23.778309100000001D, -1117.3162970999999D, -1.4008494D },
            { -2.745881D, 3.1582262D, 767.06603470000005D, 127.6888198D, 6.4866309D, 2.0482639D, 26.694835000000001D,
                    -1249.2844055999999D, -1.4699811D },
            { -2.445881D, 3.1582262D, 732.09135600000002D, 148.1152635D, 5.7586042D, 2.3482639D, 27.438752699999998D,
                    -1312.5960477000001D, -1.5229775D },
            { -2.145881D, 3.1582262D, 697.11667720000003D, 171.99884599999999D, 4.8356764D, 2.6482639D,
                    26.054706700000001D, -1295.9634337D, -1.559265D },
            { -1.845881D, 3.1582262D, 662.1419985D, 200.29154579999999D, 3.6574486D, 2.9482639D, 22.795641400000001D,
                    -1197.1694348999999D, -1.5784544D },
            { -1.545881D, 3.1582262D, 627.16731970000001D, 234.27778710000001D, 2.1401053D, 3.2482639D,
                    18.098041299999998D, -1024.4601307D, -1.5803442D },
            { -1.245881D, 3.1582262D, 592.19264099999998D, 275.7268449D, 0.1651599D, 3.5482639D, 12.5392045D,
                    -796.71068639999999D, -1.5649228D },
            { -0.945881D, 3.1582262D, 557.21796219999999D, 327.13300479999998D, -2.4385346D, 3.8482639D, 6.7807331D,
                    -542.25307350000003D, -1.5323681D },
            { -0.645881D, 3.1582262D, 522.24328349999996D, 392.10647540000002D, -5.9244687D, 4.1482639D, 1.5037707D,
                    -296.4142751D, -1.4830451D },
            { -11.104053D, 3.586714D, 1783.0305780000001D, 3.0597946D, 8.9830009D, -1.2534236D, -28.878733400000002D,
                    273.90211240000002D, -0.0622D },
            { -10.804053D, 3.586714D, 1746.770436D, 11.2644424D, 8.961452899999999D, -0.9534236D, -29.395259299999999D,
                    237.07571759999999D, -0.2273342D },
            { -10.504053000000001D, 3.586714D, 1710.5102939999999D, 19.625597299999999D, 8.914397900000001D,
                    -0.6534236D, -27.7391769D, 189.571102D, -0.3899653D },
            { -10.204053D, 3.586714D, 1674.2501520000001D, 28.2628214D, 8.839864800000001D, -0.3534236D,
                    -23.955131000000002D, 118.7611406D, -0.5483032D },
            { -9.904052999999999D, 3.586714D, 1637.99001D, 37.305238799999998D, 8.734694899999999D, -0.0534236D,
                    -18.2960657D, 14.8403718D, -0.7006056000000001D },
            { -9.604053D, 3.586714D, 1601.7298679999999D, 46.896400399999997D, 8.5943557D, 0.2465764D, -11.1984656D,
                    -127.0396562D, -0.8451975000000001D },
            { -9.304053D, 3.586714D, 1565.469726D, 57.2001287D, 8.412664700000001D, 0.5465764D, -3.2396288D,
                    -305.32272369999998D, -0.9804902D },
            { -9.004053000000001D, 3.586714D, 1529.2095839999999D, 68.407805800000006D, 8.1813959D, 0.8465764D,
                    4.9188426D, -511.52492050000001D, -1.1049982D },
            { -8.704053D, 3.586714D, 1492.9494420000001D, 80.747736000000003D, 7.8897273D, 1.1465764D, 12.5958051D,
                    -730.75357320000001D, -1.2173562D },
            { -8.404052999999999D, 3.586714D, 1456.6893D, 94.497482399999996D, 7.5234689D, 1.4465764D,
                    19.157248200000002D, -943.24781970000004D, -1.3163339D },
            { -8.104053D, 3.586714D, 1420.4291579000001D, 110.00047410000001D, 7.0639795D, 1.7465764D,
                    24.078356800000002D, -1126.7819248999999D, -1.4008494D },
            { -7.804053D, 3.586714D, 1384.1690159D, 127.6888198D, 6.4866309D, 2.0465764D, 26.994882700000002D,
                    -1259.6501765D, -1.4699811D },
            { -7.504053D, 3.586714D, 1347.9088738999999D, 148.1152635D, 5.7586042D, 2.3465764D, 27.738800399999999D,
                    -1323.8619616999999D, -1.5229775D },
            { -7.204053D, 3.586714D, 1311.6487319D, 171.99884599999999D, 4.8356764D, 2.6465764D, 26.354754400000001D,
                    -1308.1294909000001D, -1.559265D },
            { -6.904053D, 3.586714D, 1275.3885898999999D, 200.29154579999999D, 3.6574486D, 2.9465764D,
                    23.095689100000001D, -1210.2356351999999D, -1.5784544D },
            { -6.604053D, 3.586714D, 1239.1284479000001D, 234.27778710000001D, 2.1401053D, 3.2465764D,
                    18.398088999999999D, -1038.4264741D, -1.5803442D },
            { -6.304053D, 3.586714D, 1202.8683059D, 275.7268449D, 0.1651599D, 3.5465764D, 12.839252200000001D,
                    -811.57717290000005D, -1.5649228D },
            { -6.004053D, 3.586714D, 1166.6081638999999D, 327.13300479999998D, -2.4385346D, 3.8465764D, 7.0807808D,
                    -558.01970300000005D, -1.5323681D },
            { -5.704053D, 3.586714D, 1130.3480219D, 392.10647540000002D, -5.9244687D, 4.1465764D, 1.8038184D,
                    -313.08104780000002D, -1.4830451D },
            { -14.875845200000001D, 4.0776653D, 2264.6197375000002D, 3.0597946D, 8.9830009D, -1.2547898D,
                    -28.578699799999999D, 273.40190050000001D, -0.0622D },
            { -14.5758452D, 4.0776653D, 2226.8867414000001D, 11.2644424D, 8.961452899999999D, -0.9547898D, -29.0952257D,
                    235.67540510000001D, -0.2273342D },
            { -14.275845199999999D, 4.0776653D, 2189.1537453999999D, 19.625597299999999D, 8.914397900000001D,
                    -0.6547898D, -27.439143399999999D, 187.27068879999999D, -0.3899653D },
            { -13.9758452D, 4.0776653D, 2151.4207494000002D, 28.2628214D, 8.839864800000001D, -0.3547898D,
                    -23.655097399999999D, 115.56062679999999D, -0.5483032D },
            { -13.675845199999999D, 4.0776653D, 2113.6877534D, 37.305238799999998D, 8.734694899999999D, -0.0547898D,
                    -17.996032199999998D, 10.739757300000001D, -0.7006056000000001D },
            { -13.375845200000001D, 4.0776653D, 2075.9547573999998D, 46.896400399999997D, 8.5943557D, 0.2452102D,
                    -10.898432100000001D, -132.04037120000001D, -0.8451975000000001D },
            { -13.0758452D, 4.0776653D, 2038.2217614000001D, 57.2001287D, 8.412664700000001D, 0.5452102D, -2.9395953D,
                    -311.22353939999999D, -0.9804902D },
            { -12.775845199999999D, 4.0776653D, 2000.4887653000001D, 68.407805800000006D, 8.1813959D, 0.8452102D,
                    5.2188762D, -518.32583680000005D, -1.1049982D },
            { -12.4758452D, 4.0776653D, 1962.7557693000001D, 80.747736000000003D, 7.8897273D, 1.1452102D,
                    12.895838599999999D, -738.45459010000002D, -1.2173562D },
            { -12.175845199999999D, 4.0776653D, 1925.0227732999999D, 94.497482399999996D, 7.5234689D, 1.4452102D,
                    19.457281800000001D, -951.84893729999999D, -1.3163339D },
            { -11.875845200000001D, 4.0776653D, 1887.2897773D, 110.00047410000001D, 7.0639795D, 1.7452102D, 24.3783903D,
                    -1136.2831431D, -1.4008494D },
            { -11.5758452D, 4.0776653D, 1849.5567813D, 127.6888198D, 6.4866309D, 2.0452102D, 27.294916199999999D,
                    -1270.0514954D, -1.4699811D },
            { -11.275845199999999D, 4.0776653D, 1811.8237853000001D, 148.1152635D, 5.7586042D, 2.3452102D, 28.0388339D,
                    -1335.1633812D, -1.5229775D },
            { -10.9758452D, 4.0776653D, 1774.0907892D, 171.99884599999999D, 4.8356764D, 2.6452102D, 26.654787899999999D,
                    -1320.331011D, -1.559265D },
            { -10.675845199999999D, 4.0776653D, 1736.3577932000001D, 200.29154579999999D, 3.6574486D, 2.9452102D,
                    23.3957227D, -1223.3372558999999D, -1.5784544D },
            { -10.375845200000001D, 4.0776653D, 1698.6247972000001D, 234.27778710000001D, 2.1401053D, 3.2452102D,
                    18.698122600000001D, -1052.4281954999999D, -1.5803442D },
            { -10.0758452D, 4.0776653D, 1660.8918011999999D, 275.7268449D, 0.1651599D, 3.5452102D, 13.1392858D,
                    -826.47899489999998D, -1.5649228D },
            { -9.775845199999999D, 4.0776653D, 1623.1588052D, 327.13300479999998D, -2.4385346D, 3.8452102D, 7.3808143D,
                    -573.82162570000003D, -1.5323681D },
            { -9.4758452D, 4.0776653D, 1585.4258092D, 392.10647540000002D, -5.9244687D, 4.1452102D, 2.1038519D,
                    -329.78307109999997D, -1.4830451D },
            { -16.933428299999999D, 4.6123929D, 2532.8424602D, 3.0597946D, 8.9830009D, -1.2559254D,
                    -28.278675199999999D, 272.87559420000002D, -0.0622D },
            { -16.633428299999999D, 4.6123929D, 2493.5052816000002D, 11.2644424D, 8.961452899999999D, -0.9559254D,
                    -28.7952011D, 234.2490248D, -0.2273342D },
            { -16.333428300000001D, 4.6123929D, 2454.1681029000001D, 19.625597299999999D, 8.914397900000001D,
                    -0.6559254D, -27.139118799999999D, 184.94423459999999D, -0.3899653D },
            { -16.033428300000001D, 4.6123929D, 2414.8309242999999D, 28.2628214D, 8.839864800000001D, -0.3559254D,
                    -23.355072799999999D, 112.3340987D, -0.5483032D },
            { -15.7334283D, 4.6123929D, 2375.4937455999998D, 37.305238799999998D, 8.734694899999999D, -0.0559254D,
                    -17.6960075D, 6.6131553D, -0.7006056000000001D },
            { -15.433428299999999D, 4.6123929D, 2336.156567D, 46.896400399999997D, 8.5943557D, 0.2440746D,
                    -10.598407399999999D, -137.06704719999999D, -0.8451975000000001D },
            { -15.1334283D, 4.6123929D, 2296.8193882999999D, 57.2001287D, 8.412664700000001D, 0.5440746D, -2.6395706D,
                    -317.1502893D, -0.9804902D },
            { -14.8334283D, 4.6123929D, 2257.4822097000001D, 68.407805800000006D, 8.1813959D, 0.8440746D, 5.5189008D,
                    -525.15266059999999D, -1.1049982D },
            { -14.533428300000001D, 4.6123929D, 2218.145031D, 80.747736000000003D, 7.8897273D, 1.1440746D, 13.1958632D,
                    -746.18148789999998D, -1.2173562D },
            { -14.2334283D, 4.6123929D, 2178.8078523999998D, 94.497482399999996D, 7.5234689D, 1.4440746D,
                    19.757306400000001D, -960.475909D, -1.3163339D },
            { -13.933428299999999D, 4.6123929D, 2139.4706737000001D, 110.00047410000001D, 7.0639795D, 1.7440746D,
                    24.678415000000001D, -1145.8101887D, -1.4008494D },
            { -13.6334283D, 4.6123929D, 2100.1334950999999D, 127.6888198D, 6.4866309D, 2.0440746D, 27.594940900000001D,
                    -1280.4786148999999D, -1.4699811D },
            { -13.3334283D, 4.6123929D, 2060.7963163999998D, 148.1152635D, 5.7586042D, 2.3440746D, 28.338858500000001D,
                    -1346.4905745999999D, -1.5229775D },
            { -13.033428300000001D, 4.6123929D, 2021.4591378D, 171.99884599999999D, 4.8356764D, 2.6440746D, 26.9548126D,
                    -1332.5582784000001D, -1.559265D },
            { -12.7334283D, 4.6123929D, 1982.1219590999999D, 200.29154579999999D, 3.6574486D, 2.9440746D,
                    23.695747300000001D, -1236.4645972000001D, -1.5784544D },
            { -12.433428299999999D, 4.6123929D, 1942.7847804999999D, 234.27778710000001D, 2.1401053D, 3.2440746D,
                    18.998147199999998D, -1066.4556107000001D, -1.5803442D },
            { -12.1334283D, 4.6123929D, 1903.4476018D, 275.7268449D, 0.1651599D, 3.5440746D, 13.4393104D,
                    -841.40648409999994D, -1.5649228D },
            { -11.8334283D, 4.6123929D, 1864.1104232D, 327.13300479999998D, -2.4385346D, 3.8440746D, 7.680839D,
                    -589.64918880000005D, -1.5323681D },
            { -11.533428300000001D, 4.6123929D, 1824.7732444999999D, 392.10647540000002D, -5.9244687D, 4.1440746D,
                    2.4038765D, -346.51070809999999D, -1.4830451D },
            { -17.016744200000002D, 5.1679226D, 2542.6289181000002D, 3.0597946D, 8.9830009D, -1.2568887D, -27.9786565D,
                    272.32962730000003D, -0.0622D },
            { -16.716744200000001D, 5.1679226D, 2501.6251504000002D, 11.2644424D, 8.961452899999999D, -0.9568887D,
                    -28.4951823D, 232.80300170000001D, -0.2273342D },
            { -16.4167442D, 5.1679226D, 2460.6213827000001D, 19.625597299999999D, 8.914397900000001D, -0.6568887D,
                    -26.839099999999998D, 182.5981553D, -0.3899653D },
            { -16.116744199999999D, 5.1679226D, 2419.6176150000001D, 28.2628214D, 8.839864800000001D, -0.3568887D,
                    -23.055053999999998D, 109.0879631D, -0.5483032D },
            { -15.8167442D, 5.1679226D, 2378.6138473000001D, 37.305238799999998D, 8.734694899999999D, -0.0568887D,
                    -17.395988800000001D, 2.4669636D, -0.7006056000000001D },
            { -15.5167442D, 5.1679226D, 2337.6100796000001D, 46.896400399999997D, 8.5943557D, 0.2431113D, -10.2983887D,
                    -142.11329509999999D, -0.8451975000000001D },
            { -15.216744200000001D, 5.1679226D, 2296.6063119D, 57.2001287D, 8.412664700000001D, 0.5431113D, -2.3395519D,
                    -323.09659340000002D, -0.9804902D },
            { -14.9167442D, 5.1679226D, 2255.6025441000002D, 68.407805800000006D, 8.1813959D, 0.8431113D, 5.8189195D,
                    -531.9990209D, -1.1049982D },
            { -14.616744199999999D, 5.1679226D, 2214.5987764000001D, 80.747736000000003D, 7.8897273D, 1.1431113D,
                    13.495882D, -753.92790430000002D, -1.2173562D },
            { -14.3167442D, 5.1679226D, 2173.5950087000001D, 94.497482399999996D, 7.5234689D, 1.4431113D,
                    20.057325200000001D, -969.12238170000001D, -1.3163339D },
            { -14.0167442D, 5.1679226D, 2132.5912410000001D, 110.00047410000001D, 7.0639795D, 1.7431113D, 24.9784337D,
                    -1155.3567175999999D, -1.4008494D },
            { -13.716744200000001D, 5.1679226D, 2091.5874733000001D, 127.6888198D, 6.4866309D, 2.0431113D, 27.8949596D,
                    -1290.9251999999999D, -1.4699811D },
            { -13.4167442D, 5.1679226D, 2050.5837056D, 148.1152635D, 5.7586042D, 2.3431113D, 28.638877300000001D,
                    -1357.8372159D, -1.5229775D },
            { -13.116744199999999D, 5.1679226D, 2009.5799379D, 171.99884599999999D, 4.8356764D, 2.6431113D,
                    27.254831299999999D, -1344.8049758D, -1.559265D },
            { -12.8167442D, 5.1679226D, 1968.5761702D, 200.29154579999999D, 3.6574486D, 2.9431113D, 23.995766100000001D,
                    -1249.6113508999999D, -1.5784544D },
            { -12.5167442D, 5.1679226D, 1927.5724025D, 234.27778710000001D, 2.1401053D, 3.2431113D, 19.298165900000001D,
                    -1080.5024206000001D, -1.5803442D },
            { -12.216744200000001D, 5.1679226D, 1886.5686347000001D, 275.7268449D, 0.1651599D, 3.5431113D,
                    13.739329100000001D, -856.35335009999994D, -1.5649228D },
            { -11.9167442D, 5.1679226D, 1845.564867D, 327.13300479999998D, -2.4385346D, 3.8431113D, 7.9808577D,
                    -605.49611100000004D, -1.5323681D },
            { -11.616744199999999D, 5.1679226D, 1804.5610993D, 392.10647540000002D, -5.9244687D, 4.1431113D, 2.7038953D,
                    -363.25768649999998D, -1.4830451D },
            { -15.058972300000001D, 5.7192432D, 2273.0478588000001D, 3.0597946D, 8.9830009D, -1.2577194D,
                    -27.678641800000001D, 271.76838479999998D, -0.0622D },
            { -14.7589723D, 5.7192432D, 2230.3901292D, 11.2644424D, 8.961452899999999D, -0.9577194D,
                    -28.195167699999999D, 231.3417153D, -0.2273342D },
            { -14.458972299999999D, 5.7192432D, 2187.7323996D, 19.625597299999999D, 8.914397900000001D, -0.6577194D,
                    -26.539085400000001D, 180.23682500000001D, -0.3899653D },
            { -14.1589723D, 5.7192432D, 2145.07467D, 28.2628214D, 8.839864800000001D, -0.3577194D, -22.755039400000001D,
                    105.8265889D, -0.5483032D },
            { -13.8589723D, 5.7192432D, 2102.4169403999999D, 37.305238799999998D, 8.734694899999999D, -0.0577194D,
                    -17.095974200000001D, -1.6944545D, -0.7006056000000001D },
            { -13.558972300000001D, 5.7192432D, 2059.7592109000002D, 46.896400399999997D, 8.5943557D, 0.2422806D,
                    -9.9983741D, -147.17475709999999D, -0.8451975000000001D },
            { -13.2589723D, 5.7192432D, 2017.1014812999999D, 57.2001287D, 8.412664700000001D, 0.5422806D, -2.0395373D,
                    -329.05809929999998D, -0.9804902D },
            { -12.958972299999999D, 5.7192432D, 1974.4437516999999D, 68.407805800000006D, 8.1813959D,
                    0.8422806000000001D, 6.1189342D, -538.86057059999996D, -1.1049982D },
            { -12.6589723D, 5.7192432D, 1931.7860221000001D, 80.747736000000003D, 7.8897273D, 1.1422806D,
                    13.795896600000001D, -761.68949799999996D, -1.2173562D },
            { -12.3589723D, 5.7192432D, 1889.1282925D, 94.497482399999996D, 7.5234689D, 1.4422806D, 20.357339799999998D,
                    -977.78401919999999D, -1.3163339D },
            { -12.058972300000001D, 5.7192432D, 1846.4705629D, 110.00047410000001D, 7.0639795D, 1.7422806D,
                    25.278448300000001D, -1164.9183989999999D, -1.4008494D },
            { -11.7589723D, 5.7192432D, 1803.8128333D, 127.6888198D, 6.4866309D, 2.0422806D, 28.194974200000001D,
                    -1301.3869253D, -1.4699811D },
            { -11.458972299999999D, 5.7192432D, 1761.1551036999999D, 148.1152635D, 5.7586042D, 2.3422806D,
                    28.938891900000002D, -1369.1989851000001D, -1.5229775D },
            { -11.1589723D, 5.7192432D, 1718.4973742D, 171.99884599999999D, 4.8356764D, 2.6422806D, 27.5548459D,
                    -1357.0667888999999D, -1.559265D },
            { -10.8589723D, 5.7192432D, 1675.8396445999999D, 200.29154579999999D, 3.6574486D, 2.9422806D,
                    24.295780700000002D, -1262.7732079D, -1.5784544D },
            { -10.558972300000001D, 5.7192432D, 1633.1819149999999D, 234.27778710000001D, 2.1401053D, 3.2422806D,
                    19.598180599999999D, -1094.5643213999999D, -1.5803442D },
            { -10.2589723D, 5.7192432D, 1590.5241854000001D, 275.7268449D, 0.1651599D, 3.5422806D, 14.039343799999999D,
                    -871.31529490000003D, -1.5649228D },
            { -9.958972299999999D, 5.7192432D, 1547.8664558D, 327.13300479999998D, -2.4385346D, 3.8422806D, 8.2808723D,
                    -621.35809970000003D, -1.5323681D },
            { -9.6589723D, 5.7192432D, 1505.2087262D, 392.10647540000002D, -5.9244687D, 4.1422806D, 3.0039099D,
                    -380.01971909999997D, -1.4830451D } };

    //判断网络是否可用。当网络不可用时，不要网络校正位置了。
    public static boolean isConnect(Context mContext) {
        boolean out = false;
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                out = true;
            }
        }

        return out;
    }

//    /**
//     * 中文转unicode
//     *
//     * @param string
//     * @return
//     */
//    public static String stringToUnicode(String string) {
//        StringBuffer unicode = new StringBuffer();
//        for (int i = 0; i < string.length(); i++) {
//            // 取出每一个字符
//            char c = string.charAt(i);
//            // 转换为unicode
//            //"\\u只是代号，请根据具体所需添加相应的符号"
//            unicode.append("\\u" + Integer.toHexString(c));
//        }
//        return unicode.toString();
//    }
    /*
     * 中文转unicode编码
     */
    public static String stringToUnicode(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
    /*
     * unicode编码转中文
     */
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

}
