package com.sfmap.tbt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.sfmap.tbt.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

class NaviLocationListener {
	private final String TAG = this.getClass().getSimpleName();
	private static NaviLocationListener listener;
	private static String c = NaviUtilDecode.b() + File.separator
			+ "navigation" + File.separator;
	private LocationManager locationManager;
	private Context context;
	private TBTControlDecode tbtControl;
	private LocationListener locationListener;
	private LocationListenerImpl myLocationlistener;
	public Handler handler = new Handler() {
		public void handleMessage(Message paramMessage) {
			super.handleMessage(paramMessage);
			Location localLocation = (Location) paramMessage.obj;
		}
	};
	private InnerA innerA ;
	private boolean h = false;
	private long gpsTime = 1000L;
	private int gpsDis = 1;
	private int k = 0;
	private int l = 0;
	private boolean m = false;
	private boolean n = false;
	private int o = 0;
	private long p = 0L;
	private Location location = null;

	//是否保存NMEA数据
	private boolean saveNmea = false;

	//保存的NMEA数据绝对路径
	private String nmeaFileName = null;
	private  IntentFilter localIntentFilter;

	private NaviLocationListener(Context paramContext, TBTControlDecode paramh) {
		try {
			this.context = paramContext;
			this.tbtControl = paramh;
			this.myLocationlistener = new LocationListenerImpl();
			if (this.context != null)
				this.locationManager = ((LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE));

			localIntentFilter = new IntentFilter();
			localIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public static synchronized NaviLocationListener singleton(Context paramContext, TBTControlDecode paramh) {
		if (listener == null)
			listener = new NaviLocationListener(paramContext, paramh);

		return listener;
	}

	public void removeListener() {
		try {
			stopGPS();
			destroy();
			try {
				if (this.context != null)
					if(this.innerA != null){
						this.context.unregisterReceiver(this.innerA);
					}
			} catch (Throwable localThrowable1) {
				localThrowable1.printStackTrace();
			} finally {
				this.innerA = null;

				this.tbtControl = null;
				this.handler = null;
			}
			this.m = false;
		} catch (Throwable localThrowable2) {
			localThrowable2.printStackTrace();
			NaviUtilDecode.a(localThrowable2);
		}
	}

	private synchronized void destroy() {
		listener = null;
		this.locationManager = null;
	}


	public boolean b() {
		return this.h;
	}


	public GpsStatus getGpsStatus(){
		if(locationManager==null)return null;
		return  locationManager.getGpsStatus(null);
	}

	/*
	启动GPS定位更新,
	 */
	public void startGPS() {
		startGPS(1000l,1);
		//注册NMEA数据监听
		if(saveNmea) {
			this.locationManager.addNmeaListener(mNmeaListener);
		}

	}

	public void setGpsStatusListener( GpsStatus.Listener listener){
		if (this.locationManager != null)
			this.locationManager.addGpsStatusListener(listener);
	}

	public void removeGpsStatusListener( GpsStatus.Listener listener){
		if (this.locationManager != null)
			this.locationManager.removeGpsStatusListener(listener);
	}

	public void startGPS(long time, int dis) {
		try {
			if (this.locationManager == null) {
				return;
			}
			Log.i(TAG,"h:"+String.valueOf(this.h));
			if ((!(b())) || (this.gpsTime != time) || (this.gpsDis != dis)) {
				if(locationListener==null)locationListener=myLocationlistener;
				this.locationManager.removeUpdates(locationListener);
				this.locationManager.requestLocationUpdates("gps", time, dis, locationListener);
				this.gpsTime = time;
				this.gpsDis = dis;
				this.tbtControl.initLocationManager();

				if(innerA==null)innerA = new InnerA();//定位广播
				if (this.context != null)
					this.context.registerReceiver(this.innerA, localIntentFilter);
			}
			this.h = true;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}
	public void setLocationListener(LocationListener listener){
		this.locationListener=listener;
	}

	public void stopGPS() {
		if (this.locationManager == null)
			return;

		this.locationManager.removeUpdates(locationListener);

		if(saveNmea) {
			this.locationManager.removeNmeaListener(mNmeaListener);
		}
		this.h = false;
	}

	private class InnerA extends BroadcastReceiver {
		public void onReceive(Context paramContext, Intent paramIntent) {
			try {
				String str = paramIntent.getAction();
				if (("android.location.PROVIDERS_CHANGED".equals(str))	&& (NaviLocationListener.this.locationManager != null)	&& (NaviLocationListener.this.locationManager.isProviderEnabled("gps")) && (NaviLocationListener.this.b())) {
					NaviLocationListener.this.locationManager.removeUpdates(locationListener);
					NaviLocationListener.this.locationManager.requestLocationUpdates("gps", NaviLocationListener.this.gpsTime, NaviLocationListener.this.gpsDis,locationListener);
				}
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
		}
	}


	private GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener(){
		/*
		* 解析串口接收到的字节串（RMC）推荐定位信息
		* $GPRMC,013946,A,3202.1855,N,11849.0769,E,0.05,218.30,111105,4.5,W,A*20..
		* $GPRMC,<1> ,2,<3> ,4,<5> ,6,<7> ,<8> ,<9> ,10,11,12*hh<CR><LF>
	    * <1>UTC时间，hhmmss（时分秒）格式 <2> 定位状态，A=有效定位，V=无效定位
	    * <3>纬度ddmm.mmmm（度分）格式（前面的0也将被传输） <4> 纬度半球N（北半球）或S（南半球）
	    * <5>经度dddmm.mmmm（度分）格式（前面的0也将被传输） <6> 经度半球E（东经）或W（西经）
	    * <7>地面速率（000.0~999.9节，前面的0也将被传输） <8> 地面航向（000.0~359.9度，以真北为参考基准，前面的0也将被传输）
	    * <9> UTC日期，ddmmyy（日月年）格式 <10> 磁偏角（000.0~180.0度，前面的0也将被传输）
	    * <11>磁偏角方向，E（东）或W（西） <12> 模式指示（仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效）
	    * 返回值 0 正确 1校验失败 2非GPRMC信息 3无效定位 4格式错误 5校验错误
	    */
		@Override
		public void onNmeaReceived(long timestamp, String nmea) {
			gpslog(nmea, NaviLocationListener.this.nmeaFileName);
		}
	};

	public static void gpslog(String xy, String fileName){
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(xy.getBytes());
			raf.close();
		} catch (Exception ex) {

		}
	}

	private static String getSystemDateTime() {
		java.util.Calendar cal=java.util.Calendar.getInstance();
		int year=cal.get(cal.YEAR);
		int month=cal.get(cal.MONTH)+1;
		int day=cal.get(cal.DAY_OF_MONTH);
		int hours=cal.get(cal.HOUR_OF_DAY);
		int min=cal.get(cal.MINUTE);
		int sec=cal.get(cal.SECOND);
		return year+"-"+format2Int(month)+"-"+format2Int(day)+" "+format2Int(hours)+":"+format2Int(min)+":"+format2Int(sec);
	}

	private static String format2Int(int n){
		String s=""+n;
		if(s.length()<2){
			s="0"+s;
		}
		return s;
	}

	public boolean saveGpsNmea(boolean enable, String nmeaFileName){
		if(this.saveNmea != enable)
		{
			if(enable) { //启动监听，并进行保存
				this.nmeaFileName = nmeaFileName;
				if(null != this.locationManager)
					this.locationManager.addNmeaListener(mNmeaListener);
			}
			else{ //停止监听
				this.locationManager.removeNmeaListener(mNmeaListener);
			}

			this.saveNmea = enable;
			return true;
		}else{
			return true;
		}
	}

	public boolean isSaveGpsNmea(){
		return this.saveNmea;
	}


	private class LocationListenerImpl implements LocationListener{

		public void onLocationChanged(Location paramLocation) {
			try {
				if (paramLocation == null)
					return;
				//SDK层面坐标偏移
//				double[] arrayOfDouble = CoordUtilDecode.a(paramLocation.getLongitude(),paramLocation.getLatitude());
//
//				if ((arrayOfDouble != null) && (arrayOfDouble.length > 1)) {
//					paramLocation.setLongitude(arrayOfDouble[0]);
//					paramLocation.setLatitude(arrayOfDouble[1]);
//				}
				if (tbtControl != null) {
					NaviUtilDecode.a(new Object[] { "onLocationChanged_setCarLocation_setGpsInfo" });
					tbtControl.updateCarLocation(1, paramLocation.getLongitude(),paramLocation.getLatitude(),paramLocation.getBearing());
					if(paramLocation != null && paramLocation.getLatitude() != 0){
						tbtControl.setGpsInfo(1, paramLocation);
						LogUtil.d(TAG,"setGpsInfo"+new Gson().toJson(paramLocation));
						EventBus.getDefault().postSticky(paramLocation);
					}
					NaviUtilDecode.a(new Object[] { "lon="	+ paramLocation.getLongitude() + "lat="	+ paramLocation.getLatitude() + "speed="+ paramLocation.getSpeed() + "accuracy="+ paramLocation.getAccuracy() + "bearing="	+ paramLocation.getBearing() + "time="+ paramLocation.getTime() });
				}
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
				NaviUtilDecode.a(localThrowable);
			}
		}

		public void onStatusChanged(String paramString, int paramInt,Bundle paramBundle) {
		}

		public void onProviderEnabled(String paramString) {
		}

		public void onProviderDisabled(String paramString) {
		}



	}

	/**
	 * 根据格式返回当前时间
	 * @return 返回指定格式的时间
	 */
	public static String getTimeStampFormat(String format) {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(dt);
	}
}