package com.sfmap.adcode;

/**
 */
 class AdProjection {
    public static final int MAXZOOMLEVEL = 20;
    public static final int PixelsPerTile = 256;
    public static final int EarthRadiusInMeters = 6378137;
    public static final double EarthCircumferenceInMeters = 2 * Math.PI
            * EarthRadiusInMeters;

    public AdProjection() {
    }

    public static double[] PixelsToLatLong(long xPixel, long yPixel,
                                         int levelOfDetail) {
        double [] xy = {0D,0D};
        double fd = EarthCircumferenceInMeters
                / ((1 << levelOfDetail) * PixelsPerTile);
        double ia = xPixel * fd - EarthCircumferenceInMeters / 2;
        double hT = EarthCircumferenceInMeters / 2 - yPixel * fd;
        xy[1] = Math.PI / 2 - 2
                * Math.atan(Math.exp(-hT / EarthRadiusInMeters));
        xy[1] *= 180.0 / Math.PI;
        xy[0] = ia / EarthRadiusInMeters;
        xy[0] *= 180.0 / Math.PI;
        return xy;
    }
}
