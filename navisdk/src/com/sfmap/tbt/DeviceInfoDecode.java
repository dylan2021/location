package com.sfmap.tbt;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeviceInfoDecode {
	private static String a = "";
	private static boolean b = false;
	private static String c = "";
	private static String d = "";
	private static String e = "";
	private static String f = "";

	public static void setTraficTag() {
		try {
			if (Build.VERSION.SDK_INT > 14) {
				Method localMethod = TrafficStats.class.getDeclaredMethod(
						"setThreadStatsTag", new Class[] { Integer.TYPE });

				localMethod.invoke(null,
						new Object[] { Integer.valueOf(40964) });
			}
		} catch (NoSuchMethodException localNoSuchMethodException) {
			BasicLogHandler.a(localNoSuchMethodException, "DeviceInfo", "setTraficTag");
			localNoSuchMethodException.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
			BasicLogHandler.a(localIllegalAccessException, "DeviceInfo", "setTraficTag");

			localIllegalAccessException.printStackTrace();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			BasicLogHandler.a(localIllegalArgumentException, "DeviceInfo", "setTraficTag");
			localIllegalArgumentException.printStackTrace();
		} catch (InvocationTargetException localInvocationTargetException) {
			BasicLogHandler.a(localInvocationTargetException, "DeviceInfo", "setTraficTag");
			localInvocationTargetException.printStackTrace();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "setTraficTag");
			localThrowable.printStackTrace();
		}
	}

	public static String a(Context paramContext) {
		try {
			if ((a != null) && (!("".equals(a)))) {
				return a;
			}

			if (paramContext
					.checkCallingOrSelfPermission("android.permission.WRITE_SETTINGS") == 0) {
				a = Settings.System.getString(
						paramContext.getContentResolver(), "mqBRboGZkQPcAkyk");
			}

			if ((a != null) && (!("".equals(a))))
				return a;
		} catch (Throwable localThrowable1) {
			localThrowable1.printStackTrace();
		}

		try {
			if ("mounted".equals(Environment.getExternalStorageState())) {
				String str1 = Environment.getExternalStorageDirectory()
						.getAbsolutePath();

				String str2 = str1 + "/.UTSystemConfig/Global/Alvin2.xml";
				File localFile = new File(str2);
				if (localFile.exists()) {
					SAXParserFactory localSAXParserFactory = SAXParserFactory
							.newInstance();
					SAXParser localSAXParser = localSAXParserFactory
							.newSAXParser();
					a locala = new a();
					localSAXParser.parse(localFile, locala);
				}
			}
		} catch (FileNotFoundException localFileNotFoundException) {
			localFileNotFoundException.printStackTrace();
		} catch (ParserConfigurationException localParserConfigurationException) {
			localParserConfigurationException.printStackTrace();
		} catch (SAXException localSAXException) {
			localSAXException.printStackTrace();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		} catch (Throwable localThrowable2) {
			localThrowable2.printStackTrace();
		}
		return a;
	}

	static String b(Context paramContext) {
		String str = null;
		try {
			if ((paramContext == null)
					|| (paramContext
							.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0)) {
				return str;
			}

			WifiManager localWifiManager = (WifiManager) paramContext
					.getSystemService("wifi");

			if (localWifiManager.isWifiEnabled()) {
				WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
				str = localWifiInfo.getBSSID();
			}
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getWifiMacs");
			localThrowable.printStackTrace();
		}
		return str;
	}

	static String c(Context paramContext) {
		List localList;
		int i;
		int j;
		StringBuilder localStringBuilder = new StringBuilder();
		try {
			if ((paramContext == null)
					|| (paramContext
							.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0)) {
				return localStringBuilder.toString();
			}

			WifiManager localWifiManager = (WifiManager) paramContext
					.getSystemService("wifi");

			if (localWifiManager.isWifiEnabled()) {
				localList = localWifiManager.getScanResults();
				if ((localList == null) || (localList.size() == 0))
					return localStringBuilder.toString();

				localList = a(localList);
				i = 1;
				for (j = 0; (j < localList.size()) && (j < 7); ++j) {
					ScanResult localScanResult = (ScanResult) localList.get(j);
					if (i != 0)
						i = 0;
					else
						localStringBuilder.append(";");

					localStringBuilder.append(localScanResult.BSSID);
				}
			}
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getWifiMacs");
			localThrowable.printStackTrace();
		}
		return localStringBuilder.toString();
	}

	static String d(Context paramContext) {
		try {
			if ((c != null) && (!("".equals(c))))
				return c;

			if (paramContext
					.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0)
				return c;

			WifiManager localWifiManager = (WifiManager) paramContext
					.getSystemService("wifi");

			c = localWifiManager.getConnectionInfo().getMacAddress();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getDeviceMac");
			localThrowable.printStackTrace();
		}
		return c;
	}

	static String e(Context paramContext) {
		StringBuilder localStringBuilder = new StringBuilder();
		try {
			Object localObject;
			int i;
			int j;
			if ((paramContext
					.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0)
					|| (paramContext
							.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
				return localStringBuilder.toString();
			}
			TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
					.getSystemService("phone");

			CellLocation localCellLocation = localTelephonyManager
					.getCellLocation();
			if (localCellLocation instanceof GsmCellLocation) {
				localObject = (GsmCellLocation) localCellLocation;
				i = ((GsmCellLocation) localObject).getCid();
				j = ((GsmCellLocation) localObject).getLac();
				localStringBuilder.append(j).append("||").append(i)
						.append("&bttype=gsm");
			} else if (localCellLocation instanceof CdmaCellLocation) {
				localObject = (CdmaCellLocation) localCellLocation;
				i = ((CdmaCellLocation) localObject).getSystemId();
				j = ((CdmaCellLocation) localObject).getNetworkId();
				int k = ((CdmaCellLocation) localObject).getBaseStationId();
				if ((i >= 0) && (j >= 0) && (k < 0))
					;
				localStringBuilder.append(i).append("||").append(j)
						.append("||").append(k).append("&bttype=cdma");
			}

		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "cellInfo");
			localThrowable.printStackTrace();
		}
		return ((String) localStringBuilder.toString());
	}

	static String f(Context paramContext) {
		String str1 = "";
		try {
			if (paramContext
					.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
				return str1;
			}

			TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
					.getSystemService("phone");

			String str2 = localTelephonyManager.getNetworkOperator();
			if ((TextUtils.isEmpty(str2)) && (str2.length() < 3)) {
				return str1;
			}
			str1 = str2.substring(3);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getMNC");
		}
		return str1;
	}

	static String g(Context paramContext) {
		String str = "";
		try {
			return v(paramContext);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return str;
	}

	static int h(Context paramContext) {
		int i = -1;
		try {
			return w(paramContext);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getNetWorkType");
			localThrowable.printStackTrace();
		}
		return i;
	}

	static int i(Context paramContext) {
		int i = -1;
		try {
			return w(paramContext);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return i;
	}

	public static int j(Context paramContext) {
		int i = -1;
		try {
			return u(paramContext);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getActiveNetWorkType");

			localThrowable.printStackTrace();
		}
		return i;
	}

	static int k(Context paramContext) {
		int i = -1;
		try {
			return u(paramContext);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return i;
	}

	static String l(Context paramContext) {
		String str = null;
		try {
			if (paramContext
					.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != 0)
				return str;

			ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
					.getSystemService("connectivity");

			if (localConnectivityManager == null)
				return str;

			NetworkInfo localNetworkInfo = localConnectivityManager
					.getActiveNetworkInfo();

			if (localNetworkInfo == null) {
				return str;
			}

			str = localNetworkInfo.getExtraInfo();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getNetworkExtraInfo");

			localThrowable.printStackTrace();
		}
		return str;
	}

	static String m(Context paramContext) {
		try {
			if ((d != null) && (!("".equals(d))))
				return d;

			DisplayMetrics localDisplayMetrics = new DisplayMetrics();
			WindowManager localWindowManager = (WindowManager) paramContext
					.getSystemService("window");

			localWindowManager.getDefaultDisplay().getMetrics(
					localDisplayMetrics);
			int i = localDisplayMetrics.widthPixels;
			int j = localDisplayMetrics.heightPixels;
			d = j + "*" + i;
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getReslution");
			localThrowable.printStackTrace();
		}
		return d;
	}

	public static String n(Context paramContext) {
		try {
			if ((e != null) && (!("".equals(e))))
				return e;

			if (paramContext
					.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0)
				return e;

			TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
					.getSystemService("phone");

			e = localTelephonyManager.getDeviceId();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getDeviceID");
			localThrowable.printStackTrace();
		}
		return e;
	}

	public static String o(Context paramContext) {
		String str = "";
		try {
			return s(paramContext);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getSubscriberId");
			localThrowable.printStackTrace();
		}
		return str;
	}

	static String p(Context paramContext) {
		try {
			return t(paramContext);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "DeviceInfo", "getNetworkOperatorName");

			localThrowable.printStackTrace();
		}
		return "";
	}

	static String q(Context paramContext) {
		try {
			return t(paramContext);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return "";
	}

	static String r(Context paramContext) {
		try {
			return s(paramContext);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return "";
	}

	private static String s(Context paramContext) {
		if ((f != null) && (!("".equals(f)))) {
			return f;
		}

		if (paramContext
				.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0)
			return f;

		TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
				.getSystemService("phone");

		f = localTelephonyManager.getSubscriberId();
		if (f == null)
			f = "";

		return f;
	}

	private static String t(Context paramContext) {
		String str = null;
		if (paramContext
				.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0)
			return str;

		TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
				.getSystemService("phone");

		str = localTelephonyManager.getSimOperatorName();
		if (TextUtils.isEmpty(str)) {
			str = localTelephonyManager.getNetworkOperatorName();
		}

		return str;
	}

	private static int u(Context paramContext) {
		int i = -1;
		if ((paramContext == null)
				|| (paramContext
						.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != 0)) {
			return i;
		}
		ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
				.getSystemService("connectivity");

		if (localConnectivityManager == null) {
			return i;
		}

		NetworkInfo localNetworkInfo = localConnectivityManager
				.getActiveNetworkInfo();
		if (localNetworkInfo == null) {
			return i;
		}

		i = localNetworkInfo.getType();

		return i;
	}

	private static String v(Context paramContext) {
		String str1 = "";
		String str2 = o(paramContext);
		if ((str2 == null) || (str2.length() < 5))
			return str1;

		str1 = str2.substring(3, 5);

		return str1;
	}

	private static int w(Context paramContext) {
		int i = -1;
		if (paramContext
				.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0)
			return i;

		TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
				.getSystemService("phone");

		i = localTelephonyManager.getNetworkType();

		return i;
	}

	private static List<ScanResult> a(List<ScanResult> paramList) {
		int i = paramList.size();
		for (int j = 0; j < i - 1; ++j)
			for (int k = 1; k < i - j; ++k) {
				if (((ScanResult) paramList.get(k - 1)).level > ((ScanResult) paramList
						.get(k)).level) {
					ScanResult localScanResult = (ScanResult) paramList
							.get(k - 1);
					paramList.set(k - 1, paramList.get(k));
					paramList.set(k, localScanResult);
				}
			}

		return paramList;
	}

	static class a extends DefaultHandler {
		public void startElement(String paramString1, String paramString2,
				String paramString3, Attributes paramAttributes)
				throws SAXException {
			if ((paramString2.equals("string"))
					&& ("UTDID".equals(paramAttributes.getValue("name")))) {
				// l.a(true);
			}
		}

		public void characters(char[] paramArrayOfChar, int paramInt1,
				int paramInt2) throws SAXException {
			// if (l.b)
			// l.a(new String(paramArrayOfChar, paramInt1, paramInt2));
		}

		public void endElement(String paramString1, String paramString2,
				String paramString3) throws SAXException {
			// l.a(false);
		}
	}
}