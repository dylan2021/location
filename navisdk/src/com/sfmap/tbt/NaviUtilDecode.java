package com.sfmap.tbt;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import com.sfmap.api.navi.model.NaviLatLng;

public class NaviUtilDecode {
	public static String a = "";
	public static float b = 1.0F;
	public static int crossDisplayMode = 2;
	public static int[] d = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
	public static double[] e = { 7453.6419999999998D, 3742.9904999999999D,
			1873.3330000000001D, 936.89026000000001D, 468.47199999999998D,
			234.239D, 117.12D, 58.560000000000002D, 29.280000000000001D,
			14.640000000000001D, 7.32D, 3.66D, 1.829D, 0.915D, 0.4575D, 0.228D,
			0.1144D };
	private static String[] f = { AppInfoUtilDecode.PACKAGE_API_NAVI, AppInfoUtilDecode.PACKAGE_TBT };
	private static int g = 2048;
	private static double h = 1.E-005D;

	public static String[] a() {
		return ((String[]) f.clone());
	}

	public static void a(Object[] paramArrayOfObject) {
	}

	public static void a(Throwable paramThrowable) {
	}

	public static String b() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/*
		单位转换
 	*/
	public static String a(int paramInt) {
		if (paramInt == 0)
			return "0";
		if (paramInt < 100)
			return paramInt + "";
		if ((100 <= paramInt) && (paramInt < 1000))
			return paramInt + "";
		if ((1000 <= paramInt) && (paramInt < 10000))
			return (paramInt / 100 * 100 / 1000.0D) + "";
		if ((10000 <= paramInt) && (paramInt < 100000))
			return (paramInt / 100 * 100 / 1000.0D) + "";

		return (paramInt / 1000) + "";
	}

	public static String a(int paramInt, String paramString1,String paramString2) {
		if (paramInt == 0) {
			return "<font color='" + paramString1
					+ "' ><B>0</B></font><font color ='" + paramString2
					+ "'>米</font>";
		}

		if (paramInt < 1000) {
			return "<font color='" + paramString1 + "'><B>" + paramInt
					+ "</B></font><font color ='" + paramString2 + "'>米</font>";
		}

		return "<font color='" + paramString1 + "'><B>"
				+ (paramInt / 100 * 100 / 1000.0D)
				+ "</B></font><font color ='" + paramString2
				+ "'>公里</font>";


	}

	public static SpannableString a(int paramInt, int textSize) {
		String result = "";
		SpannableString spanString ;
		AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(textSize);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
		if (paramInt < 1000) {
			result = paramInt+ "米";
			spanString = new SpannableString(result);
			spanString.setSpan(sizeSpan, spanString.length()-1, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
		}else {
			result = (paramInt / 100 * 100 / 1000.0D)+ "公里";
			spanString = new SpannableString(result);
			spanString.setSpan(sizeSpan, spanString.length()-2, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
		}
		spanString.setSpan(colorSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
		return spanString;
	}

	public static String a(String paramString) {
		if (paramString == null)
			return "";

		String[] arrayOfString = paramString.split(":");
		StringBuffer localStringBuffer = new StringBuffer();
		if ((arrayOfString != null) && (arrayOfString.length > 2)) {
			int i;
			String str;
			if (!("00".equals(arrayOfString[0])))
				if (arrayOfString[0].indexOf("0") != -1) {
					i = arrayOfString[0].indexOf("0");
					str = "";
					if (i + 1 < arrayOfString[0].length())
						str = arrayOfString[0].substring(i + 1);
					else
						str = arrayOfString[0];

					localStringBuffer.append(str + "小时");
				} else {
					localStringBuffer.append(arrayOfString[0] + "小时");
				}

			if (!("00".equals(arrayOfString[1])))
				if (arrayOfString[1].indexOf("0") != -1) {
					i = arrayOfString[1].indexOf("0");
					str = "";
					if (i + 1 < arrayOfString[1].length())
						str = arrayOfString[1].substring(i + 1);
					else
						str = arrayOfString[1];

					localStringBuffer.append(str + "分钟");
				} else {
					localStringBuffer.append(arrayOfString[1] + "分钟");
				}

		}

		return localStringBuffer.toString();
	}

	public static String a(String values, String valueColor,
			String unitColor) {
		if (values == null)
			return "";

		String[] arrayOfString = values.split(":");
		StringBuffer localStringBuffer = new StringBuffer();
		if ((arrayOfString != null) && (arrayOfString.length > 2)) {
			int i;
			String str;
			if (!("00".equals(arrayOfString[0])))
				if (arrayOfString[0].indexOf("0") != -1) {
					i = arrayOfString[0].indexOf("0");
					str = "";
					if ((i == 0) && (i + 1 < arrayOfString[0].length()))
						str = arrayOfString[0].substring(i + 1);
					else
						str = arrayOfString[0];

					localStringBuffer.append("<font color='" + valueColor
							+ "' ><B>" + str + "</B></font><font color ='"
							+ unitColor + "'>小时</font>");
				} else {
					localStringBuffer.append("<font color='" + valueColor
							+ "' ><B>" + arrayOfString[0]
							+ "</B></font><font color ='" + unitColor
							+ "'>小时</font>");
				}

			if (!("00".equals(arrayOfString[1])))
				if (arrayOfString[1].indexOf("0") != -1) {
					i = arrayOfString[1].indexOf("0");
					str = "";
					if (i + 1 < arrayOfString[1].length())
						str = arrayOfString[1].substring(i + 1);
					else {
						str = arrayOfString[1];
					}

					localStringBuffer.append("<font color='" + valueColor
							+ "' ><B>" + str + "</B></font><font color ='"
							+ unitColor + "'>分钟</font>");
				} else {
					localStringBuffer.append("<font color='" + valueColor
							+ "' ><B>" + arrayOfString[1]
							+ "</B></font><font color ='" + unitColor
							+ "'>分钟</font>");
				}
			if("00".equals(arrayOfString[0])&&"00".equals(arrayOfString[1])){
				if("00".equals(arrayOfString[2])){
					localStringBuffer.append("<font color='" + valueColor
							+ "' ><B>" + "0"+ "</B></font><font color ='"
							+ unitColor + "'>分钟</font>");
				}else{
				localStringBuffer.append("<font color='" + valueColor
						+ "' ><B>" + "&lt;1"+ "</B></font><font color ='"
						+ unitColor + "'>分钟</font>");
				}
			}

		}

		return localStringBuffer.toString();
	}

	public static SpannableString a(String values, int textSize,
			String unitColor) {
		if (values == null)
			return null;
		StringBuffer localStringBuffer = new StringBuffer();
		int hourLoc = 0;
		int minLoc = 0;
		String[] arrayOfString = values.split(":");
		if ((arrayOfString != null) && (arrayOfString.length > 2)) {
			int i;
			String str;
			if (!("00".equals(arrayOfString[0])))
				if (arrayOfString[0].indexOf("0") != -1) {
					i = arrayOfString[0].indexOf("0");
					str = "";
					if ((i == 0) && (i + 1 < arrayOfString[0].length())){
						str = arrayOfString[0].substring(i + 1);
					}else{
						str = arrayOfString[0];
					}
					hourLoc = str.length();
					localStringBuffer.append(str+"小时");
				} else {
					hourLoc = arrayOfString[0].length();
					localStringBuffer.append(arrayOfString[0]+"小时");
				}
			if (!("00".equals(arrayOfString[1])))
				if (arrayOfString[1].indexOf("0") != -1) {
					i = arrayOfString[1].indexOf("0");
					str = "";
					if (i + 1 < arrayOfString[1].length())
						str = arrayOfString[1].substring(i + 1);
					else {
						str = arrayOfString[1];
					}
					minLoc = localStringBuffer.length()+str.length();
					if("00".equals(arrayOfString[0])){
						localStringBuffer.append(str+"分钟");
					}else{
						localStringBuffer.append(str+"分");
					}

				} else {
					minLoc = localStringBuffer.length()+arrayOfString[1].length();
					if("00".equals(arrayOfString[0])){
						localStringBuffer.append(arrayOfString[1]+"分钟");
					}else{
						localStringBuffer.append(arrayOfString[1]+"分");
					}
				}
			if("00".equals(arrayOfString[0])&&"00".equals(arrayOfString[1])){
				if("00".equals(arrayOfString[2])){
					minLoc = localStringBuffer.length()+1;
					localStringBuffer.append(0+"分钟");
				}else{
					minLoc = localStringBuffer.length()+2;
					localStringBuffer.append("<1分钟");
				}
			}
		}
		SpannableString sStr  = new SpannableString(localStringBuffer.toString());
		if(hourLoc != 0){
			sStr.setSpan(new AbsoluteSizeSpan(textSize), hourLoc, hourLoc+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		sStr.setSpan(new AbsoluteSizeSpan(textSize), minLoc, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sStr;
	}

	public static String getTimeFormatString(int paramInt) {
		paramInt = Math.abs(paramInt);
		StringBuffer localStringBuffer = new StringBuffer();
		int i = paramInt / 3600;
		if (i == 0)
			localStringBuffer.append("00:");

		if (i > 0)
			localStringBuffer.append(c(i) + ":");

		i = paramInt % 3600;
		int j = i/ 60;
		localStringBuffer.append(c(j) + ":");

		j = i % 60;
		localStringBuffer.append(c(j));

		return localStringBuffer.toString();
	}

	private static String c(int paramInt) {
		if (paramInt < 10)
			return "0" + paramInt;
		return "" + paramInt;
	}

	public static NaviLatLng a(double paramDouble1, double paramDouble2,
			double paramDouble3, double paramDouble4) {
		double d1 = 0.0D;
		double d2 = 0.0D;
		if ((paramDouble1 > 0.0D) && (paramDouble2 > 0.0D)
				&& (paramDouble3 > 0.0D) && (paramDouble4 > 0.0D)) {
			d1 = (paramDouble1 + paramDouble3) / 2.0D;
			d2 = (paramDouble2 + paramDouble4) / 2.0D;
		}
		NaviLatLng localNaviLatLng = new NaviLatLng(d1, d2);
		return localNaviLatLng;
	}

	public static NaviLatLng a(NaviLatLng paramNaviLatLng1,	NaviLatLng paramNaviLatLng2, double paramDouble) {
		double d1 = calculateLength(paramNaviLatLng1, paramNaviLatLng2);
		NaviLatLng localNaviLatLng = new NaviLatLng();

		double d2 = paramDouble / d1;
		localNaviLatLng	.setLatitude((paramNaviLatLng2.getLatitude() - paramNaviLatLng1	.getLatitude()) * d2 + paramNaviLatLng1.getLatitude());
		localNaviLatLng	.setLongitude((paramNaviLatLng2.getLongitude() - paramNaviLatLng1.getLongitude()) * d2 + paramNaviLatLng1.getLongitude());
		return localNaviLatLng;
	}

	public static int calculateLength(NaviLatLng paramNaviLatLng1, NaviLatLng paramNaviLatLng2) {
		double d1 = paramNaviLatLng1.getLongitude();
		double d2 = paramNaviLatLng1.getLatitude();
		double d3 = paramNaviLatLng2.getLongitude();
		double d4 = paramNaviLatLng2.getLatitude();
		return calculateLength(d1, d2, d3, d4);
	}

	public static int calculateLength(double paramDouble1, double paramDouble2,
			double paramDouble3, double paramDouble4) {
		double d1 = 0.01745329251994329D;
		paramDouble1 *= 0.01745329251994329D;
		paramDouble2 *= 0.01745329251994329D;
		paramDouble3 *= 0.01745329251994329D;
		paramDouble4 *= 0.01745329251994329D;
		double d2 = Math.sin(paramDouble1);
		double d3 = Math.sin(paramDouble2);
		double d4 = Math.cos(paramDouble1);
		double d5 = Math.cos(paramDouble2);
		double d6 = Math.sin(paramDouble3);
		double d7 = Math.sin(paramDouble4);
		double d8 = Math.cos(paramDouble3);
		double d9 = Math.cos(paramDouble4);
		double[] arrayOfDouble = new double[3];

		arrayOfDouble[0] = (d5 * d4 - (d9 * d8));
		arrayOfDouble[1] = (d5 * d2 - (d9 * d6));
		arrayOfDouble[2] = (d3 - d7);

		double d10 = Math.sqrt(arrayOfDouble[0] * arrayOfDouble[0]
				+ arrayOfDouble[1] * arrayOfDouble[1] + arrayOfDouble[2]
				* arrayOfDouble[2]);

		return (int) (Math.asin(d10 / 2.0D) * 12742001.579854401D);
	}

	public static void a(Context paramContext) {
		int i = paramContext.getResources().getDisplayMetrics().densityDpi;

		if (i <= 120)
			b = 0.5F;
		else if (i <= 160)
			b = 0.6F;
		else if (i <= 240)
			b = 0.8F;
		else if (i <= 320)
			b = 0.9F;
		else
			b = 1.0F;
	}

	public static int a(Context paramContext, int paramInt) {
		if (paramContext == null)
			return paramInt;
		try {
			float f1 = TypedValue.applyDimension(1, paramInt, paramContext
					.getResources().getDisplayMetrics());

			return (int) f1;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return paramInt;
	}
}