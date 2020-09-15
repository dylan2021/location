package com.sfmap.library.util;


import android.location.Location;

/**
 * 计算工具类
 */
public class CalculateUtil {
    /**
     * 计算两点间的距离
     *
     * @param startLatitude     开始纬度
     * @param startLongitude    开始经度
     * @param endLatitude       结束纬度
     * @param endLongitude      结束经度
     * @return
     */
    public static float getDistance(double startLatitude,
                                    double startLongitude, double endLatitude, double endLongitude) {
        if (startLatitude <= 0 || startLongitude <= 0 || endLatitude <= 0
                || endLongitude <= 0) { // 过滤掉不在大陆地区的错误经纬度
            return -1;
        }
        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude,
                endLongitude, results);
        if (results != null && results.length > 0) {
            return results[0];
        }
        return -1;
    }

    /**
     * 计算距离
     * @param meter  距离大小
     * @return       单位 米和公里
     */
    public static String getLengDesc(int meter) {
        if (meter < 1000)
            return meter + "米";
        int kiloMeter = meter / 1000;
        int leftMeter = meter % 1000;
        leftMeter = leftMeter / 100;
        String rs = kiloMeter + "";
        if (leftMeter > 0) {
            rs += "." + leftMeter + "公里";
        } else {
            rs += "公里";
        }
        return rs;
    }

    /**
     * 计算距离
     * @param meter 距离大小
     * @return string[0] 数值 stirng[1] 单位
     */
    public static String[] getLengDesc2(int meter) {
        String[] tmp = new String[2];
        tmp[0] = "";
        tmp[1] = "";
        if (meter < 1000) {
            tmp[0] = meter + "";
            tmp[1] = "米";
            return tmp;
        }
        int kiloMeter = meter / 1000;
        int leftMeter = meter % 1000;
        leftMeter = leftMeter / 100;
        String rs = kiloMeter + "";
        if (leftMeter > 0) {
            tmp[0] = rs += "." + leftMeter;
            tmp[1] = "公里";
        } else {
            tmp[0] = rs;
            tmp[1] = "公里";
        }
        return tmp;
    }

    /**
     * 计算两个点连线的弧度
     * @param dStartLon     开始弧度纬度
     * @param dStartLat     开始弧度经度
     * @param dEndLon       结束弧度纬度
     * @param dEndLat       结束弧度经度
     * @return
     */
    public static double calcAngle(double dStartLon,  double dStartLat,  double dEndLon,  double dEndLat) {
        double dAngle;
        if(dEndLon != dStartLon) {
            double s = Math.cos((dEndLat + dStartLat) * 0.008726646);
            double dAtan = (dEndLat-dStartLat) / ((dEndLon-dStartLon) * s);
            dAngle = Math.atan(dAtan);
            if(dEndLon - dStartLon < 0)
            {
                dAngle += Math.PI;
            }
            else
            {
                if(dAngle < 0)
                {
                    dAngle += 2*Math.PI;
                }
            }
        } else {
            if(dEndLat > dStartLat)
            {
                dAngle = Math.PI/2;
            }
            else
            {
                dAngle = Math.PI/2*3;
            }
        }

        dAngle = Math.PI*5/2 - dAngle;
        if(dAngle > Math.PI*2)
        {
            dAngle = dAngle - Math.PI*2;
        }

        return dAngle;
    }


    private static int getInt(byte[] b, int startIndex) {
        int ch1 = (b[3 + startIndex]) & 0xff;
        int ch2 = (b[2 + startIndex]) & 0xff;
        int ch3 = (b[1 + startIndex]) & 0xff;
        int ch4 = (b[0 + startIndex]) & 0xff;
        int r = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        return r;
    }

    private static short getShort(byte b[], int startIndex) {
        int ch3 = (b[startIndex + 1]) & 0xff;
        int ch4 = (b[startIndex + 0]) & 0xff;
        short r = (short) ((ch3 << 8) + (ch4 << 0));
        return r;
    }

    /**
     * 数组转换成十六进制字符串
     * @param bArray    一个byte数组对象
     * @return          十六进制字符串
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


}
