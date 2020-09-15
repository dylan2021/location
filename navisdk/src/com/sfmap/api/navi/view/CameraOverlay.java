package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.ResourcesUtil;

/**
 * 电子眼类
 */
public class CameraOverlay {
	private BitmapDescriptor bitmapDescriptor = null; //电子眼图片
	private Marker marker; 			//电子眼对象
	private LatLng latLng = null;	//电子眼位置

	/**
	 * 构造CameraOverlay对象
	 * @param context - Android Context
     */
	public CameraOverlay(Context context) {
		try {
			this.bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(), ResourcesUtil.cameraicon));
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 设置电子眼图片资源。
	 * @param bitmapDescriptor - 图片资源
     */
	public void setBitmap(BitmapDescriptor bitmapDescriptor) {
		this.bitmapDescriptor = bitmapDescriptor;
	}

	/**
	 * 将电子眼对象添加到地图上。
	 * @param mapController - 地图对象
	 * @param latLng - 电子眼位置
     */
	public void addToMap(MapController mapController, LatLng latLng) {
		try {
			if (mapController == null)
				return;

			if (this.marker == null) {
				this.marker = mapController.addMarker(new MarkerOptions()
						.position(latLng).anchor(0.5F, 0.5F).icon(this.bitmapDescriptor));
			} else {
				if (latLng.equals(this.latLng))
					return;

				this.marker.setPosition(latLng);
			}
			this.latLng = latLng;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 设置电子眼对象的可见性。
	 * @param visible - 是否可见
     */
	public void setVisible(boolean visible) {
		if (this.marker != null)
			this.marker.setVisible(visible);
	}

	/**
	 * 销毁电子眼，释放图片资源。
	 */
	public void destroy() {
		if (this.marker != null)
			this.marker.remove();

		if (this.bitmapDescriptor != null)
			this.bitmapDescriptor.recycle();

		this.bitmapDescriptor = null;
	}
}