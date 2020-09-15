package com.sfmap.util;

import android.location.Location;
import android.text.format.Time;

public class CalcSunRiseAndSunSetTools {

    public static boolean isDay(boolean isValidGps, double dX, double dY,
                                Location location) {
        int wYear = 0, wMonth = 0, wDay = 0, wHour = 0, wMinute = 0;
        if (isValidGps && location != null) {
            // GPS是否有效,取得GPS时间
            Time time = new Time();
            time.set(location.getTime());
            wYear = time.year;
            wMonth = time.month + 1;
            wDay = time.monthDay;
            wHour = time.hour;
            wMinute = time.minute;
        } else {
            Time t = new Time();
            t.setToNow();
//            t.set(1599138107);
            wYear = t.year;
            wMonth = t.month + 1;
            wDay = t.monthDay;
            wHour = t.hour;
            wMinute = t.minute;
        }
        // 计算当天的太阳升起时间
        double sunup = coumputeSunUpAndDown(wYear, wMonth, wDay, dX, dY, true);
        // 计算当天的太阳降落时间
        double sundown = coumputeSunUpAndDown(wYear, wMonth, wDay, dX, dY,
                false);
        double curtime = wHour + (double) wMinute / 60;
        // 比较当前时间是否在白天范围内
        if (curtime >= sunup && curtime < sundown) {
            return true;
        } else {
            return false;
        }
    }

    private static double coumputeSunUpAndDown(int wYear, int wMonth, int wDay,
                                               double dX, double dY, boolean bIsSunRise) {

        // 累加本月份前的总天数
        int iTotalDays = 0;
        for (int iLoop = 1; iLoop < wMonth; iLoop++) {
            int iDaysOfMonth = 0;
            switch (iLoop) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12: {
                    iDaysOfMonth = 31;
                }
                break;
                case 4:
                case 6:
                case 9:
                case 11: {
                    iDaysOfMonth = 30;
                }
                break;
                case 2: {
                    boolean isLongYear = false;
                    if ((wYear % 4) != 0) {
                        isLongYear = false;
                    } else if ((wYear % 4) == 0 && (wYear % 100) != 0) {
                        isLongYear = true;
                    } else if ((wYear % 100) == 0 && (wYear % 400) == 0) {
                        isLongYear = true;
                    }

                    if (isLongYear) {
                        iDaysOfMonth = 29;
                    } else {
                        iDaysOfMonth = 28;
                    }
                }
                break;
            }
            iTotalDays += iDaysOfMonth;
        }
        // 累加本月份的天数至总天数
        iTotalDays += wDay;
        int iSign;
        if (bIsSunRise) {
            iSign = 1;
        } else {
            iSign = -1;
        }

        // 计算日升或日落的时间
        double UT0 = 180;
        double UT = 180;
        do {
            UT0 = UT;
            double L = 280.460 + 360.0077 * iTotalDays / 365.25;
            double G = 357.528 + 359.9905 * iTotalDays / 365.25;
            double Lh = L + 1.915 * Math.sin(G * Rads) + 0.020
                    * Math.sin(2 * G * Rads);
            double earthjiaodu = 23.4393 - 0.013 * iTotalDays / 36525;
            double suncha = Math.asin(Math.sin(earthjiaodu * Rads)
                    * Math.sin(Lh * Rads))
                    * Degs;
            double suntime = UT0 - 180 - 1.915 * Math.sin(G * Rads) - 0.020
                    * Math.sin(2 * G * Rads) + 2.466 * Math.sin(2 * Lh * Rads)
                    - 0.053 * Math.sin(4 * Lh * Rads);

            double xiuzheng = Math.acos((Math.sin(SUN_POSITION * Rads) - Math
                    .sin(dY * Rads) * Math.sin(suncha * Rads))
                    / (Math.cos(dY * Rads) * Math.cos(suncha * Rads)))
                    * Degs;
            UT = UT0 - (suntime + dX + iSign * xiuzheng);
        } while (Math.abs(UT - UT0) > 0.1);
        UT = UT / 15 + 8;
        return UT;
    }

    private final static double SUN_POSITION = -0.8333;
    private final static double Degs = 57.2957795130823;
    private final static double Rads = 1.74532925199433E-02;

}

