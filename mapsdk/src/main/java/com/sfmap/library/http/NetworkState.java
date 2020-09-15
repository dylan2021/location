package com.sfmap.library.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import com.sfmap.library.Callback;
import com.sfmap.plugin.IMPluginManager;
import java.net.Proxy;
import java.util.Map;
import java.util.WeakHashMap;

abstract class NetworkState {
	private int networkType;
	private int mobileClass;
	private boolean connected;
	private boolean wifiConnected;
	private Proxy[] proxy;
	private Map<Object, Callback<Boolean>> wifiCallbacks = new WeakHashMap<Object, Callback<Boolean>>();
	private Map<Object, Callback<Boolean>> connectedCallbacks = new WeakHashMap<Object, Callback<Boolean>>();
	private Object callbacksLock = new Object();

	private boolean networkReceiverInitFlag;
	private Object networkInitLock = new Object();

	private int networkSubtype;

	/**
	 * 	@deprecated
	 * @param callback
	 * @param page
     */
	public void addWifiCallback(Callback<Boolean> callback, Object page) {
		synchronized (callbacksLock) {
			wifiCallbacks.put(page, callback);
		}

		initNetworkReceiver();
	}

	/**
	 * @deprecated
	 * @param callback
	 * @param page
     */
	public void addConnectedCallback(Callback<Boolean> callback, Object page) {
		synchronized (callbacksLock) {
			connectedCallbacks.put(page, callback);
		}
		initNetworkReceiver();
	}

	/**
	 * @deprecated
	 * @return
     */
	public boolean isWifiConnected() {
		initNetworkReceiver();
		return this.wifiConnected;
	}

	/**
	 * @deprecated
	 * @return
     */
	public boolean isInternetConnected() {
		initNetworkReceiver();
		return this.connected;
	}

	/**
	 * @deprecated
	 * @return
     */
	public int getMobileGeneration() {
		initNetworkReceiver();
		return this.mobileClass;
	}

	private void initNetworkReceiver() {
		if (!networkReceiverInitFlag) {
			synchronized (networkInitLock) {
				if (!networkReceiverInitFlag) {
					IntentFilter intentFilter = new IntentFilter();
					intentFilter
						.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
					IMPluginManager.getApplication().registerReceiver(new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							resetNetwork(context);
						}
					}, intentFilter);
					resetNetwork(IMPluginManager.getApplication());
					networkReceiverInitFlag = true;
				}

			}
		}
	}

	private void resetNetwork(Context context) {
		proxy = null;
		ConnectivityManager connManager = (ConnectivityManager) context
			.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			boolean connected = NetworkInfo.State.CONNECTED == networkInfo
				.getState();
			if (connected) {

				// SensorManager mSensorManager =
				// (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
				// //Sensor mAccelerometer =
				// mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				// log.error("SENSOR:"+mSensorManager.getSensorList(Sensor.TYPE_ALL));
				// log.error(networkInfo.getExtraInfo());
				// log.error(networkInfo.getSubtypeName());
				// log.error(networkInfo.getTypeName());
				int networkType = networkInfo.getType();
				int networkSubtype = networkInfo.getSubtype();
				if (!this.connected || this.networkType != networkType
					|| this.networkSubtype != networkSubtype) {
					networkChange(context, connected, networkType,
						networkSubtype);
				}
			} else if (this.connected) {
				networkChange(context, false, -1, -1);
			}

		} else {
			if (this.connected) {
				networkChange(context, false, -1, -1);
			}
		}
	}

	private static int getNetworkClass(int networkType) {
		switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return 2;// TelephonyManager.NETWORK_CLASS_2_G;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return 3;// TelephonyManager.NETWORK_CLASS_3_G;
			case TelephonyManager.NETWORK_TYPE_LTE:
				return 4;// TelephonyManager.NETWORK_CLASS_4_G;
			default:
				return -1;// TelephonyManager.NETWORK_CLASS_UNKNOWN;
		}
	}

	private void networkChange(Context context, boolean connected,
	                           int networkType, int networkSubtype) {
		int mobileClass = -1;
		boolean wifi = false;
		if (connected) {
			switch (networkType) {
				case ConnectivityManager.TYPE_WIFI:
				case ConnectivityManager.TYPE_WIMAX:// 全球微博互联接入,3G?
				case ConnectivityManager.TYPE_BLUETOOTH:
				case ConnectivityManager.TYPE_ETHERNET:
					// printWifi(context);
					wifi = true;
					break;
				default:
					// TYPE_MOBILE : int
					// TYPE_MOBILE_DUN : int
					// TYPE_MOBILE_HIPRI : int
					// TYPE_MOBILE_MMS : int
					// TYPE_MOBILE_SUPL : int
					mobileClass = getNetworkClass(networkSubtype);

			}
		}

		boolean wifiChanged = wifi != this.wifiConnected;
		this.wifiConnected = wifi;
		if (connected != this.connected) {
			this.connected = connected;
			if (mobileClass != this.mobileClass) {
				this.mobileClass = mobileClass;
			}
			synchronized (callbacksLock) {
				for (Callback<Boolean> cb : connectedCallbacks.values()) {
					cb.callback(connected);
				}
			}
		}
		if (wifiChanged) {
			synchronized (callbacksLock) {
				for (Callback<Boolean> cb : wifiCallbacks.values()) {
					cb.callback(wifi);
				}
			}
		}
	}

	//
	// private void printWifi(Context context) {
	// WifiManager wifi_service = (WifiManager)
	// context.getSystemService(Context.WIFI_SERVICE);
	// WifiInfo wifiinfo = wifi_service.getConnectionInfo();
	// log.error("getBSSID:"+ wifiinfo.getBSSID() + "");
	// log.error("getHiddenSSID:"+wifiinfo.getBSSID() + "");
	// log.error("getIpAddress:"+wifiinfo.getIpAddress() + "");
	// log.error("getLinkSpeed:"+wifiinfo.getLinkSpeed() + "");
	// log.error("getMacAddress:"+wifiinfo.getMacAddress() + "");
	// log.error("getNetworkId:"+wifiinfo.getNetworkId() + "");
	// log.error("getRssi:"+wifiinfo.getRssi() + "");
	// log.error("getSSID:"+wifiinfo.getSSID() + "");
	//
	// DhcpInfo dhcpinfo = wifi_service.getDhcpInfo();
	// log.error("ipaddr:"+dhcpinfo.ipAddress + "");
	// log.error("gatewau:"+dhcpinfo.gateway + "");
	// log.error("netmask:"+dhcpinfo.netmask + "");
	// log.error("dns1:"+dhcpinfo.dns1 + "");
	// log.error("dns1:"+dhcpinfo.dns2 + "");
	// log.error("serverAddress:"+dhcpinfo.serverAddress + "");
	// }

	/**
	 * @deprecated
	 * @return
     */
	public Proxy getProxy() {
		if (this.wifiConnected) {
			return null;
		} else if (this.connected) {
			if (proxy != null) {
				return proxy[0];
			}
			//return (proxy = new Proxy[] { android.net.Proxy.getDefaultProxy(this.application) })[0];
		}
		return null;
	}


}