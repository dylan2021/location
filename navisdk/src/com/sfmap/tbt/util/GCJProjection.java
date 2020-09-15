/*
 * MapABC.com 2014
 */
package com.sfmap.tbt.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * GCJ投影变换
 *
 * @preserve
 * @author joseph
 *
 */
public class GCJProjection {


    /**
     * 构造函数
     */
    public GCJProjection() {
        init();
    }

    /**
     * 等价于Math.sin
     *
     * @param a1 弧度
     * @return
     */
    private static double tl_sin2(double a1) {
        int v1;
        double v2;
        double v3;
        double v4;
        double v5;
        double result;
        double v7;
        double v8;

        v2 = a1;
        v1 = 0;
        if (a1 < 0.0) {
            v2 = -v2;
            v1 = 1;
        }
        v3 = v2 - 6.28318530717959 * (double) (int) (v2 / 6.28318530717959);
        if (v3 <= 3.141592653589793) {
            v4 = v3;
        } else {
            v4 = v3 - 3.141592653589793;
            if (v1 == 1) {
                v1 = 0;
            } else if (v1 == 0) {
                v1 = 1;
            }
        }
        v5 = v4;
        v7 = v4 * v4;
        v8 = v4 * v4 * v4;
        result = v8 * v7 * v7 * v7 * 0.00000275573192239859 + v8 * v7
                * 0.00833333333333333 + v5 - v8 * 0.166666666666667 - v8 * v7
                * v7 * 0.000198412698412698 - v7 * v8 * v7 * v7 * v7
                * 0.0000000250521083854417;
        if (v1 == 1) {
            result = -result;
        }
        return result;
    }

    /**
     *
     * @param deltaLon
     * @param deltaLat
     * @return
     */
    private static double tyj5(double deltaLon, double deltaLat) {
        double v2;
        double v3;

        double v8;
        double v9;

        double v14;
        double v16;
        double v17;
        double v18;
        double v19;
        double v20;
        double v21;

        v3 = deltaLon * deltaLon;
        //
        v2 = Math.sqrt(v3);

        if (v3 == v2) {
            v8 = v2;
        } else {
            v8 = Math.sqrt(v3);
        }
        v9 = Math.sqrt(v8);

        if (v9 == v3) {
            v14 = v9;
        } else {
            v14 = Math.sqrt(v8);
        }
        //
        v16 = (double) (deltaLat * deltaLon * 0.1 + deltaLon * deltaLon * 0.1
                + deltaLat + deltaLat + (double) (deltaLon + 300.0))
                + (double) (v14 * 0.1);
        v17 = Math.sin(deltaLon * 18.84955592153876) * 20.0;
        v18 = (Math.sin(deltaLon * 6.283185307179588) * 20.0 + v17) * 0.6667
                + v16;
        v19 = Math.sin(deltaLon * 3.141592653589794) * 20.0;
        v20 = (Math.sin(deltaLon * 1.047197551196598) * 40.0 + v19) * 0.6667
                + v18;
        v21 = Math.sin(deltaLon * 0.2617993877991495) * 150.0;
        return (Math.sin(deltaLon * 0.1047197551196598) * 300.0 + v21) * 0.6667
                + v20;
    }

    /**
     * 投影反变换
     *
     * @param lat
     * @param yOffset
     * @return
     */
    private static double tjyj5(double lat, double yOffset) {
        double v2;
        double v3;
        double v4;
        double v5;

        double v11;
        // 0.0174532925199433=PI/180,转变角度为弧度
        v4 = 0.0174532925199433 * lat;
        // e代表椭圆扁率， e^2=0.00669342
        v5 = Math.sin(v4) * (double) 0.00669342;
        v3 = 1.0 - Math.sin(v4) * v5;
        v2 = Math.sqrt(v3);
        return yOffset * 180.0 / (6335552.7273521 / (v3 * v2) * 3.1415926);
    }

    private static double tyjy5(double a1, double a2) {
        double v2;
        double v3;
        double v4;
        double v9;
        double v10;
        //
        double v15; //
        double v17; //
        double v18; //
        double v19; //
        double v20; //
        double v21; //
        double v22; //
        double v23; //
        double v24; //

        v4 = a1 + a1 + -100.0 + a2 * 3.0 + a2 * 0.2 * a2 + a1 * 0.1 * a2;
        v3 = a1 * a1;
        v2 = Math.sqrt(v3);

        if (v2 == v3) {
            v9 = v2;
        } else {
            v24 = v4;
            v9 = Math.sqrt(v3);
            v4 = v24;
        }
        v10 = Math.sqrt(v9);

        if (v10 == v3) {
            v15 = v10;
        } else {
            v23 = v4;
            v15 = Math.sqrt(v9);
            v4 = v23;
        }
        v17 = v4 + v15 * 0.2;
        v18 = Math.sin(a1 * 18.84955592153876) * 20.0;
        v19 = (Math.sin(a1 * 6.283185307179588) * 20.0 + v18) * 0.6667 + v17;
        v20 = Math.sin(a2 * 3.141592653589794) * 20.0;
        v21 = (Math.sin(a2 * 1.047197551196598) * 40.0 + v20) * 0.6667 + v19;
        v22 = Math.sin(a2 * 0.2617993877991495) * 160.0;
        return (Math.sin(a2 * 0.1047197551196598) * 320.0 + v22) * 0.6667 + v21;
    }

    /**
     *
     * @param fLat
     * @param xOffset
     * @return
     */
    private static double tjy5(double fLat, double xOffset) {
        double v2;
        double v3;
        double v4;
        //
        double v9;
        //1-sin(getSDKInfo)^2*e^2
        v4 = Math.sin(fLat * Math.PI / 180);
        v3 = 1.0 - Math.sin(fLat * Math.PI / 180)
                * (v4 * 0.00669342);
        v2 = Math.sqrt(v3);
        //
        // 克拉索夫椭球体，54椭球的长半轴 6378245，也就是X方向
        return (double) (xOffset * 180.0)
                / (Math.cos(fLat * Math.PI / 180)
                * (double) (6378245.0 / v2) * Math.PI);
    }

    /**
     *
     * @param flag
     * @param lon
     * @param lat
     * @param height
     * @param wg_week
     * @param wg_time
     * @param olon
     * @param olat
     * @return
     */
    private static int wgtogcj(int flag, long lon, long lat, int height, int wg_week,
            int wg_time, double[] olon, double[] olat) {
        int result; //
        double deltaLat; //
        double deltaLng;
        double tx; //
        double ty; //
        double v11; //
        double v12; //
        double v13; //
        double xGdOffset; //
        double xOffset; //
        double yGdOffset; //
        double yOffset; //
        // 纬度值
        double fLat; //
        // 经度值
        double fLon; //
        // 验证经纬度范围在中国范围内
        // 72.004--137.8347
        // 0.8293--55.8271
        fLon = (double) lon / 3686400.0;
        fLat = (double) lat / 3686400.0;
        if (height <= 5000) {
            if (flag != 0) {
                // 纬度与35度的偏移
                deltaLat = fLat - 35.0;
                deltaLng = fLon - 105.0;
                // 进行一定的二次区线+三角函数多项式变换
                tx = tyj5(deltaLng, deltaLat);
                ty = tyjy5(deltaLng, deltaLat);
                //
                // System.out.println("tx="+tx);
                // System.out.println("ty="+ty);
                //
                v11 = 0.001 * (double) height;
                v12 = v11;
                // 0.0174532925199433=PI/180,转变角度为弧度
                v13 = (double) (wg_time) * 0.0174532925199433;
                // 固定偏移量x
                xGdOffset = Math.sin(v13) + (tx + v11);
                // 加上随机偏移量
                xOffset =/* random_yj() +*/ xGdOffset;
                //
                // 固定偏移量y
                yGdOffset = Math.sin(v13) + ty + v12;
                // 加上随机偏移量y
                yOffset =/* random_yj() + */ yGdOffset;
                //
                // 利用投影变换把偏移量回算回去
                olon[0] = (tjy5(fLat, xOffset) + fLon) * 3686400.0;
                //
                // System.out.println("latOffset="+tjyj5(fLat,
                // yOffset));
                olat[0] = (tjyj5(fLat, yOffset) + fLat) * 3686400.0;
            } else {
                // 初始化
                olon[0] = lon;
                olat[0] = lat;
            }
            result = 0;
        } else {
            // 不在允许的经纬度范围内
            olon[0] = 0;
            olat[0] = 0;
            result = -1;
        }
        return result;
    }

    /**
     * 坐标偏移
     *
     * @param lon 输入经度 *3686400
     * @param lat 输入纬度*3686400
     * @param olon 输出经度*3686400
     * @param olat 输出纬度*3686400
     * @return 0表示偏移成功
     */
    private static int wgs2gcj(long lon, long lat, double[] olon, double[] olat) {
        int result;
        int v6[] = new int[1];
        int v7[] = new int[1];
        // 获取时间参数
        //GetTimeParams(v6, v7);
        v6[0] = 0;
        v7[0] = 0;
        // 调用真正的偏移函数
        result = wgtogcj(1, lon, lat, 50, v6[0], v7[0], olon, olat);
        // result=0表示偏移成功
        if (result != 0) {
            olon[0] = 2147483647;
            olat[0] = 2147483647;
        }
        return result;
    }

    /**
     * 进行坐标偏移
     *
     * @param fLon 输入经度，单位度
     * @param fLat 输入纬度，单位度
     * @param oxy 输出经纬度，单位度
     * @return 偏移结果标识
     */
    private static int wgs2gcj(double fLon, double fLat, double[] oxy) {
        int result;
        double[] oLon = new double[1];
        double[] oLat = new double[1];
        //
        result = wgs2gcj((long) (3686400.0 * fLon), (long) (3686400.0 * fLat), oLon,
                oLat);
        oxy[0] = oLon[0] / 3686400.0;
        oxy[1] = oLat[0] / 3686400.0;
        return result == 0 ? 0 : -1;
    }

    /**
     * 进行坐标偏移
     *
     * @param lon 输入经度
     * @param lat 输入纬度
     * @param oxy 输出经度数组[0]为结果
     * @param df 浮点格式类，用来格式化结果
     * @return
     */
    private static int wgs2gcj(double lon, double lat, double[] oxy,
            DecimalFormat df) {
        int result = GCJProjection.wgs2gcj(lon, lat, oxy);
        // 进行浮点格式化会导致性能损失0.07ms左右
        // DecimalFormat df=new DecimalFormat("0.######");
        df.setRoundingMode(RoundingMode.CEILING);
        oxy[0] = new Double(df.format(oxy[0]));
        oxy[1] = new Double(df.format(oxy[1]));
        return result;
    }

    /**
     * 初始化系统
     *
     * @return
     */
    private void init() {
        //
    }

    /**
     * 投影
     *
     * @param x 经度
     * @param y 纬度
     * @param oxy 投影后的经纬度数据,oxy[0]-经度，oxy[1]-纬度
     * @return 0-成功，-1失败.
     */
    public static int project(double x, double y, double[] oxy) {
        int rs = wgs2gcj(x, y, oxy);
        return rs;
    }

    /**
     * 批量反投影
     *
     * @param srcPts 源数据数组,x1,y1,x2,y2...
     * @param srcOff 源数据数组偏移
     * @param dstPts 目的数据数组,ox1,oy1,ox2,oy2...
     * @param dstOff 目的数据数据偏移
     * @param numPts 数据点数
     */
    public static void unprojects(double[] srcPts, int srcOff,
            double[] dstPts, int dstOff, int numPts) {
        for (int i = srcOff; i < srcOff + numPts; i += 2) {
            double lng = srcPts[i];
            double lat = srcPts[i + 1];
            double[] cxy = new double[2];
            unproject(lng, lat, cxy);
            dstPts[i] = cxy[0];
            dstPts[i + 1] = cxy[1];
        }

    }

    /**
     * 批量执行投影变换
     *
     * @param srcPts 源数据数组,x1,y1,x2,y2...
     * @param srcOff 源数据数组偏移
     * @param dstPts 目的数据数组,ox1,oy1,ox2,oy2...
     * @param dstOff 目的数据数据偏移
     * @param numPts 数据点数
     */
    public static void projects(double[] srcPts, int srcOff,
            double[] dstPts, int dstOff, int numPts) {
        for (int i = srcOff; i < srcOff + numPts; i += 2) {
            double lng = srcPts[i];
            double lat = srcPts[i + 1];
            double[] oxy = new double[2];
            project(lng, lat, oxy);
            dstPts[i] = oxy[0];
            dstPts[i + 1] = oxy[1];
        }

    }

    /**
     * 反投影
     *
     * @param x 经度
     * @param y 纬度
     * @param oxy 反投影后的结果数组,oxy[0]-经度，oxy[1]-纬度
     * @return 成功返回0，失败返回-1
     */
    public static int unproject(double x, double y, double[] oxy) {
        double[] tmp = new double[2];
        double dx, dy;
        int rs = wgs2gcj(x, y, tmp);
        dx = tmp[0] - x;
        dy = tmp[1] - y;
        oxy[0] = x - dx;
        oxy[1] = y - dy;
        return rs == 0 ? 0 : -1;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        double x = 116.345;
        double y = 39.3435;
        //
        long s = System.currentTimeMillis();
        int count = 10000;
        for (int i = 0; i < count; i++) {
            double[] oxy = new double[2];
            int rs = project(x, y, oxy);
            System.out.println("oxy="+oxy[0]+","+oxy[1]);
        }
        long e = System.currentTimeMillis();
        System.out.println("spare=" + (e - s) * 1.0 / count);
    }
}
