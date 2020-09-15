package com.sfmap.adcode;

import android.content.Context;
import android.text.TextUtils;

import com.sfmap.library.util.JsonHelper;
import com.sfmap.library.util.MD5Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class AdCode {

	public static final int LOADING = 0;
	public static final int FILE_LOADING_SUCCESS = 1;
	public static final int SO_LOADING_SUCCESS = 2;
	// 定义一个缓存区，将计算记过保存到该缓存中，避免频繁计算消耗cpu资源
	// private final static SparseLongArray mCityCodeBuffer = new SparseLongArray();
	private final static HashMap<Long, Integer> mCityCodeBuffer = new HashMap<Long, Integer>();

	static {
		try{
			System.loadLibrary("adcode");
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	int loadingStatus = LOADING;
	int native_instance = 0;
	ArrayList<AdProvince> mList = new ArrayList<AdProvince>();
	Context mContext;

	public int getLoadingStatus() {
		return loadingStatus;
	}

	AdCode(Context context) {

		mContext = context;
		new Thread() {
			public void run() {

				loadCityFile(mContext);
				loadingStatus = 1;
				String workPath = checkAdcodeFile(mContext);
				native_instance = create(mContext, workPath);
				loadingStatus = 2;
			}

		}.start();
	}

	public void destroy() {
		if (native_instance == 0)
			return;
		mList.clear();
		destroy(native_instance);
		native_instance = 0;

	}

	public long getAdcode(int px, int py) {
		if (loadingStatus != 2) {
			return 110000;
		}
		double[] dp = AdProjection.PixelsToLatLong(px, py, AdProjection.MAXZOOMLEVEL);
		long key = genBufferKey(dp[0], dp[1]);
		long bufferValue = getBufferValue(key);
		if (bufferValue > 0) {
			return bufferValue;
		}
		int ad = (int) getAdcode(native_instance, dp[0], dp[1]);
		pushBufferValue(key, ad);
		return ad;
	}

	/**
	 * 根据经纬度生成一个缓冲区key值
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private long genBufferKey(double x, double y) {
		long xi = (int) (x * 100);
		long yi = (int) (y * 100);
		long k = ((xi << 32) | yi);
		return k;
	}

	/**
	 * 从缓冲区读数据
	 *
	 * @param key
	 * @return
	 */
	private synchronized int getBufferValue(long key) {
		if (mCityCodeBuffer.containsKey(key)) {
			return mCityCodeBuffer.get(key);
		}
		return -1;
	}

	/**
	 * 将结果保存到缓冲区
	 *
	 * @param key
	 * @param ad
	 */
	private synchronized void pushBufferValue(long key, int ad) {
		if (mCityCodeBuffer.size() >= 100) {
			Iterator<Entry<Long, Integer>> iter = mCityCodeBuffer.entrySet().iterator();
			while (iter.hasNext()) {
				iter.next();
				iter.remove();
				break;
			}
		}
		mCityCodeBuffer.put(key, ad);
	}

	public int getAdcode(double lon, double lat) {
		long key = genBufferKey(lat, lon);
		int bufferValue = getBufferValue(key);
		if (bufferValue > 0) {
			return bufferValue;
		}
		int ad = (int) getAdcode(native_instance, lon, lat);
		pushBufferValue(key, ad);
		return ad;
	}

	public AdCity getAdCity(double lon, double lat) {
		if (native_instance == 0) {
			return null;
		}
		long adcode = getAdcode(lon, lat);
		return getAdCity(adcode);
	}

	public AdCity getAdCity(int px, int py) {
		if (native_instance == 0) {
			return null;
		}
		long adcode = getAdcode(px, py);
		AdCity city = getAdCity(adcode);
		return city;
	}

	protected void finalize() throws Throwable {
		destroy();
	}

	private String checkAdcodeFile(Context context) {
		File fileDir = context.getFilesDir();
		fileDir = new File(fileDir, "a");
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		String dat = "a.dat";
		String idx = "a.idx";
		try {
			File datFile = new File(fileDir, dat);
			File idxFile = new File(fileDir, idx);
			copyFromAssert(context, "a/a.dat", datFile);
			copyFromAssert(context, "a/a.idx", idxFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileDir.toString() + "/a";
	}

	public ArrayList<AdProvince> getProvinceList() {
		return mList;
	}

	private void loadCityFile(Context context) {
		try {
			byte[] data = getAssetsFileData(context, "a/c.dat");
			if (data != null) {
				mList.clear();
				String str = new String(data, "utf-8");
				JSONObject jo = new JSONObject(str);
				JSONArray pros = JsonHelper.getJsonArray(jo, "pros");
				for (int i = 0; i < pros.length(); i++) {
					JSONObject proJo = pros.getJSONObject(i);
					AdProvince adp = new AdProvince();
					adp.Parse(proJo);
					mList.add(adp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压assert目录下的dat.zip到应用目录下，返回指定的解压后的dat文件byte数组
	 *
	 * @param context
	 * @param dataName
	 * @return
	 * @throws Exception
	 */
	private byte[] getAssetsFileData(Context context, String dataName)
		throws Exception {
		InputStream fin = context.getAssets().open(dataName);
		ByteArrayOutputStream iBout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bufferLong = -1;
		while ((bufferLong = fin.read(buffer)) > 0) {
			iBout.write(buffer, 0, bufferLong);
		}
		fin.close();
		byte[] data = iBout.toByteArray();
		return data;
	}

	public AdCity getAdCity(long adcode) {
		int len = mList.size();
		for (int i = 0; i < len; i++) {
			AdProvince ap = mList.get(i);
			ArrayList<AdCity> cities = ap.getCitiesList();
			for (int j = 0; j < cities.size(); j++) {
				AdCity ci = cities.get(j);
				if (Integer.parseInt(ci.getAdCode()) == adcode) {
					return ci;
				}
			}
		}

		return null;
	}

	public AdCity getAdCity(String citycode) {
		int len = mList.size();
		for (int i = 0; i < len; i++) {
			AdProvince ap = mList.get(i);
			ArrayList<AdCity> cities = ap.getCitiesList();
			for (int j = 0; j < cities.size(); j++) {
				AdCity ci = cities.get(j);
				if (citycode.equals(ci.getCode())) {
					return ci;
				}
			}
		}

		return null;
	}

	public AdCity getAdCityName(String cityname) {
		int len = mList.size();
		for (int i = 0; i < len; i++) {
			AdProvince ap = mList.get(i);
			ArrayList<AdCity> cities = ap.getCitiesList();
			for (int j = 0; j < cities.size(); j++) {
				AdCity ci = cities.get(j);
				if (cityname.equals(ci.getCity())) {
					return ci;
				}
			}
		}

		return null;
	}

	private void copyFromAssert(Context context, String name, File file) {
		String md5 = null;
		if (file.exists()) {
			md5 = MD5Util.getFileMD5(file.getAbsolutePath());
		}
		byte[] data = null;
		try {
			data = getAssetsFileData(context, name);
		} catch (Exception e) {
		}
		if (data != null && data.length > 0) {
			if (!TextUtils.isEmpty(md5)) {
				String md5_asset = MD5Util.getByteArrayMD5(data);
				if (md5.equals(md5_asset)) {
					return;
				}
			}

			try {
				FileOutputStream fout = new FileOutputStream(file);
				fout.write(data);
				fout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * GPS转顺丰,正常坐标＊1000000
	 * @param x
	 * @param y
	 */
	public static native int[] translatePointLocal(int x, int y);
	/**
	 * 顺丰转GPS,正常坐标＊1000000
	 * @param x
	 * @param y
	 */
	static native int[] translatePointGPS(int x, int y);
	
	private native int create(Context context, String workPath);

	private native void destroy(int native_object);

	private native long getAdcode(int native_object, double dlon, double dlat);
}