package com.sfmap.api.navi.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 导航经纬度对象类。
 */
public class NaviLatLng implements Parcelable {
	public static final Parcelable.Creator<NaviLatLng> CREATOR = new NaviLatLngCreatorDecode();
	private double latitude;
	private double longitude;

	/**
	 * 默认构造函数
	 */
	public NaviLatLng() {
	}

	/**
	 * 构造函数：用给定的经度和纬度构造一个NaviLatLng对象。
	 * @param gcjlatitude - 国测局坐标系,该点的纬度。为保持Mercator 投影精确度，其取值范围是[-80,80]。
	 * @param gcjlongitude - 国测局坐标系,该点的经度，可被规范化到(-180,180]。
	 */
	public NaviLatLng(double gcjlatitude, double gcjlongitude) {
		this.latitude = gcjlatitude;
		this.longitude = gcjlongitude;

	}

	/**
	 * 获得GCJ02纬度。
	 * @return 纬度信息。
	 */
	public double getLatitude() {
		return this.latitude;
	}




	/**
	 * 设置GCJ02纬度。
	 * @param latitude - 纬度信息。
	 */
	public void setLatitude(double latitude) {

		this.latitude = latitude;
	}


	/**
	 * 获得GCJ02经度。
	 * @return 经度信息。
	 */
	public double getLongitude() {
		return this.longitude;
	}




	/**
	 * 设置GCJ02经度。
	 * @param longitude - 经度信息。
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	/**
	 * 对象的描述标识。
	 * @return 对象标识。
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * 将对象写入Parcel中。
	 * @param dest - 目标对象
	 * @param flags - 写入辅助flag, 0 or PARCELABLE_WRITE_RETURN_VALUE。
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(this.latitude);
		dest.writeDouble(this.longitude);
	}

	/**
	 * 判断与对象是否相等
	 * @param object - 相比较的对象
	 * @return 是否相等。true 代表相等， false 代表不相等
	 */
	public boolean equals(Object object) {
		if (object == null)
			return false;

		if (hashCode() == object.hashCode())
			return true;
		if (!(object instanceof NaviLatLng))
			return false;
		NaviLatLng localNaviLatLng = (NaviLatLng) object;
		return ((Double.doubleToLongBits(this.latitude) == Double
				.doubleToLongBits(localNaviLatLng.latitude)) && (Double
				.doubleToLongBits(this.longitude) == Double
				.doubleToLongBits(localNaviLatLng.longitude)))
		;
	}

	/**
	 * 返回对象的哈希码
	 * @return 对象哈希码值
	 */
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * 将经纬度转换为字符串输出。
	 * @return 格式化后的经纬度
	 */
	public String toString() {
		return this.latitude + "," + this.longitude ;
	}
}