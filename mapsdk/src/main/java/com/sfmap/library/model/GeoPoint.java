package com.sfmap.library.model;


import com.sfmap.adcode.AdCity;
import com.sfmap.adcode.AdCode;
import com.sfmap.adcode.AdCodeMonitor;
import com.sfmap.library.util.Projection;

import java.io.Serializable;

/**
 * 坐标模型(包含坐标转换)
 */
public  class GeoPoint /* extends Point */ implements Serializable, Cloneable {
	private transient ExtData ext;// cache only
	/**
	 * 兼容老接口
	 */
	public int x;
	public int y;

	/**
	 * 无参构造方法
	 */
	public GeoPoint() {

	}

	/**
	 * 构造方法
	 *
	 * @param x 20级像素坐标
	 * @param y 20级像素坐标
	 */
	public GeoPoint(int x, int y) {
		// super(ax,ay);
		this.x = x;
		this.y = y;
	}

	/**
	 * 构造方法
	 *
	 * @param lon 真实经纬度坐标--经度（对应x）
	 * @param lat 真实经纬度坐标--纬度（对应y）
	 */
	public GeoPoint(double lon, double lat) {
		this.ext = new ExtData();
		ext.lat = lat;
		ext.lon = lon;
		fromLonlat(ext);
		this.x = ext.x;
		this.y = ext.y;
	}

	/**
	 * 获取当前精度信息（与x对应，修改x值，该方法返回数据也自动修改）
	 *
	 * @return
	 */
	public double getLongitude() {
		return initlonLat(x, y).lon;
	}

	/**
	 * 获取当前纬度 信息（与y对应，修改y值，也将印象该方法的返回数据）
	 *
	 * @return
	 */
	public double getLatitude() {
		return initlonLat(x, y).lat;
	}

	/**
	 * 重新设置geopoint 位置，同步修改 x,y 值
	 *
	 * @param lon
	 * @param lat
	 * @return this
	 */
	public GeoPoint setLonLat(double lon, double lat) {
		this.ext = new ExtData();
		ext.lat = lat;
		ext.lon = lon;
		fromLonlat(ext);
		this.x = ext.x;
		this.y = ext.y;
		return this;
	}

	/**
	 * 拷贝一份独立的GeoPoint,以后冲构成clone吧？
	 */
	public GeoPoint clone() {
		return new GeoPoint(x, y);
	}

	/**
	 * 判断是否国内
	 * @return	true是否者反之
	 */
	public boolean inMainland() {
		return inMainland(getAdCode());
	}

	/**
	 * 返回城市adcode
	 * @return	城市adcode
     */
	public int getAdCode() {
		return initlonLat(x, y).getAdCode();
	}

	/**
	 * 返回城市名
	 * @return	城市名称
     */
	public String getCity() {
		return getCity(this.getAdCode());
	}

	/**
	 * 返回省份名
	 * @return	省份名称
     */
	public String getProvince() {
		return getProvince(this.getAdCode());
	}

	/**
	 * 带缓存的经纬度初始化
	 * @param x	屏幕x坐标
	 * @param y	屏幕y坐标
     * @return	一个ExData对象
     */
	private ExtData initlonLat(int x, int y) {
		if (ext == null) {
			ext = new ExtData();
		}
		if (x != ext.x || y != ext.y) {
			ext.x = x;
			ext.y = y;
			toLonlat(ext);
		}
		return ext;
	}

	/**
	 * 为了给GeoPoint提供内部模型
	 */
	public static class ExtData {
		public double lon, lat;
		public int x, y;
		private int addressCode;

		public int getAdCode() {
			if (addressCode == 0) {
				addressCode = getAddressCode(x, y);
			}
			return addressCode;
		}
	}

	/**
	 * 经纬度坐标转换为xy坐标
	 * @param data	一个ExtData对象
	 * @hide
	 */
	public void fromLonlat(GeoPoint.ExtData data){
		GeoPoint pd = Projection.LatLongToPixels(data.lat, data.lon,
				Projection.MAXZOOMLEVEL);
		data.x = pd.x;
		data.y = pd.y;
	}

	/**
	 * 判断是否国内
	 * @param addressCode 地址code
	 * @return			  true是否者反之
     */
	public boolean inMainland(long addressCode) {
		return !(addressCode == 0 || addressCode == 710000);
	}

	/**
	 * 返回城市名 根据地址code查询
	 * @param addressCode	地址code
	 * @return				城市名称
     */
	public String getCity(int addressCode) {
		if (AdCodeMonitor.getAdCodeInst().getLoadingStatus() == AdCode.SO_LOADING_SUCCESS) {
			AdCity adCity = AdCodeMonitor.getAdCodeInst()
					.getAdCity(addressCode);
			return adCity != null ? adCity.getCity() : null;
		}
		return null;
	}

	/**
	 * 返回省名称 根据地址code查询
	 * @param addressCode	地址code
	 * @return				省份名称
     */
	public String getProvince(int addressCode) {
		if (AdCodeMonitor.getAdCodeInst().getLoadingStatus() == AdCode.SO_LOADING_SUCCESS) {
			AdCity adCity = AdCodeMonitor.getAdCodeInst()
					.getAdCity(addressCode);
			return adCity != null ? adCity.getProvince() : null;
		}
		return null;
	}

	/**
	 * 真是坐标转换
	 * @param data	一个ExtData对象
     */
	public void toLonlat(ExtData data) {
		PointD pd = Projection.PixelsToLatLong(data.x, data.y,
				Projection.MAXZOOMLEVEL);
		data.lon = pd.x;
		data.lat = pd.y;
	}

	/**
	 * 返回adcode 根据经纬度查询
	 * @param x	屏幕坐标
	 * @param y	屏幕坐标
     * @return	adcode
     */
	public static int getAddressCode(int x, int y) {
		if (AdCodeMonitor.getAdCodeInst().getLoadingStatus() == AdCode.SO_LOADING_SUCCESS) {
			return (int) AdCodeMonitor.getAdCodeInst().getAdcode(x, y);
		}
		return 0;
	}
}
