package com.sfmap.library.util;

import com.sfmap.adcode.AdCode;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.model.PointD;

/**
 * 坐标转换工具类。
 */
public class Projection {
    /**
     * 最大地图级别。
     */
    public static final int MAXZOOMLEVEL = 20;
    public static final int PixelsPerTile = 256;
    private static final double MinLatitude = -85.0511287798;
    private static final double MaxLatitude = 85.0511287798;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    private static final int EarthRadiusInMeters = 6378137;
    private static final int TileSplitLevel = 0;
    private static final double EarthCircumferenceInMeters = 2 * Math.PI
            * EarthRadiusInMeters;
    /**
     * 构造方法。
     */
    public Projection() {

    }

    private static double Clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /**
     * 点坐标转换为level地图级别下像素坐标。
     * @param longitude 经度坐标 * 3600000。
     * @param latitude 纬度坐标 * 3600000。
     * @param level 地图级别。
     * @return 转换结果。
     */
    public static GeoPoint LatLongToPixels(int longitude, int latitude,
                                           int level) {
        return LatLongToPixels((double) latitude / 3600000,
                (double) longitude / 3600000, level);
    }

    /**
     * 点坐标转换为level地图级别下像素坐标。
     * @param longitude 经度坐标。
     * @param latitude 纬度坐标。
     * @param level 地图级别。
     * @return 转换结果。
     */
    public static GeoPoint LatLongToPixels(double latitude, double longitude,
                                           int level) {
        GeoPoint rPnt = new GeoPoint();
        latitude = Clip(latitude, MinLatitude, MaxLatitude) * Math.PI / 180;
        longitude = Clip(longitude, MinLongitude, MaxLongitude) * Math.PI / 180;
        double sinLatitude = Math.sin(latitude);
        double xMeters = EarthRadiusInMeters * longitude;
        double lLog = Math.log((1 + sinLatitude) / (1 - sinLatitude));
        double yMeters = EarthRadiusInMeters / 2 * lLog;
        long numPixels = (long) PixelsPerTile << level;
        double metersPerPixel = EarthCircumferenceInMeters / numPixels;
        rPnt.x = (int) Clip((double) (EarthCircumferenceInMeters / 2 + xMeters)
                / metersPerPixel + 0.5, 0, numPixels - 1);
        long tmp = (long) (EarthCircumferenceInMeters / 2 - yMeters);
        rPnt.y = (int) Clip((double) tmp / metersPerPixel + 0.5, 0,
                numPixels - 1);
        return rPnt;
    }

    /**
     * 某级别像素坐标转换为目标级别像素坐标。
     * @param px20
     * @param py20
     * @param srcLevel
     * @param toLevel
     * @return
     */
     static GeoPoint PixelsToPixels(long px20, long py20,
                                          int srcLevel, int toLevel) {
        int off = toLevel - srcLevel;
        if (off > 0) {
            // �Ŵ�off��
            px20 = px20 >> off;
            py20 = py20 >> off;
        } else if (off < 0) {
            px20 = px20 << off;
            py20 = py20 << off;
        }
        GeoPoint c = new GeoPoint();
        c.x = (int) px20;
        c.y = (int) py20;
        return c;
    }

    /**
     * 20级像素坐标转换为经纬度坐标。
     * @param px20 20级像素坐标。
     * @param py20 20级像素坐标。
     * @param level 地图级别。
     * @return
     */
    public static PointD PixelsToLatLong(long px20, long py20,
                                         int level) {
        PointD rPnt = new PointD();
        double fd = EarthCircumferenceInMeters
                / ((1 << level) * PixelsPerTile);
        double ia = px20 * fd - EarthCircumferenceInMeters / 2;
        double hT = EarthCircumferenceInMeters / 2 - py20 * fd;
        rPnt.y = Math.PI / 2 - 2
                * Math.atan(Math.exp(-hT / EarthRadiusInMeters));
        rPnt.y *= 180.0 / Math.PI;
        rPnt.x = ia / EarthRadiusInMeters;
        rPnt.x *= 180.0 / Math.PI;
        return rPnt;
    }

    /**
     * @deprecated
     * @param xPixel
     * @param yPixel
     * @return
     */
     static GeoPoint PixelsToTile(int xPixel, int yPixel) {
        GeoPoint rPnt = new GeoPoint();
        rPnt.x = (int) (xPixel / (PixelsPerTile / (1 << TileSplitLevel)));
        rPnt.y = (int) (yPixel / (PixelsPerTile / (1 << TileSplitLevel)));
        return rPnt;
    }

    /**
     * @deprecated
     * @param xTile
     * @param yTile
     * @param levelOfDetail
     * @return
     */
     static String TileToQuadKey(int xTile, int yTile, int levelOfDetail) {
        StringBuffer quadKey = new StringBuffer();
        for (int i = levelOfDetail + TileSplitLevel; i > 0; i--) {
            long mask = 1 << (i - 1);
            int cell = 0;
            if ((xTile & mask) != 0) {
                cell++;
            }
            if ((yTile & mask) != 0) {
                cell += 2;
            }
            quadKey.append(cell);
        }

        return quadKey.toString();
    }

    /**
     * WGS84坐标系的20级像素坐标转换为GCJ坐标系的20级像素坐标。
     * @param px20 20级像素坐标。
     * @param py20 20级像素坐标。
     * @return GCJ坐标系的20级像素坐标。
     */
    public static GeoPoint offsetCoordinat(int px20, int py20) {
        PointD PointD = PixelsToLatLong(px20, py20, MAXZOOMLEVEL);

        int nLon = (int) (PointD.x * 1000000);
        int nLat = (int) (PointD.y * 1000000);

        GeoPoint pt = new GeoPoint();

        // 此处需注意 矢量和3D无法统一
        int res[] = AdCode.translatePointLocal(nLon, nLat);
        if (res == null) {
            return new GeoPoint(px20, py20);
        } else {
            pt = new GeoPoint(res[0], res[1]);
        }

        double dLon = (double) pt.x / 1000000.0;
        double dLat = (double) pt.y / 1000000.0;

        return LatLongToPixels(dLat, dLon, MAXZOOMLEVEL);
    }
}
