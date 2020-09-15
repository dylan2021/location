package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdate;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.Polyline;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.api.navi.Navi;
import com.sfmap.api.navi.NaviView;
import com.sfmap.mapcore.DPoint;
import com.sfmap.mapcore.IPoint;
import com.sfmap.mapcore.MapProjection;
import com.sfmap.tbt.ResourcesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 导航自车view
 */
public class NaviCarOverlay {
	private final String TAG = this.getClass().getSimpleName();
	boolean lockCar = true;//锁车标记
	 public boolean north_up_mode=false;//正北模式
	IPoint lastCarPos = null;
	int deltax;
	int deltay;
	float lastCarAngle = 0.0F;
	float deltaAngle;
	int loopTime;//更新转向次数 -- 已经更新了的次数
	boolean needChangeCarAngle = false;
	IPoint i = null;
	int divideTime = 10;
	Timer k;
	float l = 0.0F;//车向角度
	int m = -1;
	private static float zoomflag;
	private  boolean zoomin;
	private  boolean zoomout;
	private boolean zoomIng;
	private NaviView mNaviView;
	private BitmapDescriptor carIcon = null;
	private BitmapDescriptor gpsFailcarIcon = null;
	private BitmapDescriptor naviDirIcon = null;
	private Marker carMarker;  //车标
	private Marker hideCarMarker;
	private Marker naviDirMarker;	//车辆方向
	private MapController mMapController = null;
	private MapView mMapView;
	private boolean vehicleInCenter = true; //当前车辆是否在屏幕的中心
	private LatLng w = null;
	private Polyline x = null;
	private List<LatLng> y = new ArrayList();
	public NaviCarOverlay(MapView mapView, NaviView naviView, Context context) {
		super();
		this.lockCar = true;
		this.north_up_mode = false;
		this.lastCarPos = null; //上一次车位信息
		this.lastCarAngle = 0f;
		this.needChangeCarAngle = false;
		this.i = null;
		this.divideTime = 10;
		this.l = 0f;
		this.m = -1;
		this.carIcon = null;
		this.gpsFailcarIcon = null;
		this.naviDirIcon = null;
		this.mMapController = null;
		this.vehicleInCenter = true;
		this.w = null;
		this.x = null;
		this.y = new ArrayList();
		this.carIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(), ResourcesUtil.caricon));//2130837511
		this.gpsFailcarIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(), ResourcesUtil.caricon_disable));//2130837511
		this.naviDirIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.navi_direction ));//原：2130837586
		this.mMapView = mapView;
		this.mNaviView = naviView;
	}
/*
设置锁车或非锁车状态
 */
	public void setLockCar(boolean isLockCar,boolean isNorthUpMode) {
		this.lockCar = isLockCar;
		this.north_up_mode=isNorthUpMode;
		if (this.carMarker == null)
			return;

		if (this.mMapController == null)
			return;

		if (this.naviDirMarker == null)
			return;

		if (this.hideCarMarker == null)
			return;

		if (this.lockCar) {

			float mapAngle=l;
			float carAngle=0.0f;
			if(isNorthUpMode){
				mapAngle=0.0f;
				carAngle=360.0f-this.l;
			}
			LatLng localLatLng = this.hideCarMarker.getPosition();
			CameraPosition localCameraPosition = new CameraPosition.Builder().target(localLatLng).bearing(mapAngle).tilt(this.mNaviView.getCurrentTilt()).zoom(this.mNaviView.getCurrentZoom()).build();
			this.mMapController.moveCamera(CameraUpdateFactory.newCameraPosition(localCameraPosition));

			int i1 = (int) (this.mMapView.getWidth() * mNaviView.getAnchorX());
			int i2 = (int) (this.mMapView.getHeight() * mNaviView.getAnchorY());
			this.carMarker.setPositionByPixels(i1, i2);
			this.carMarker.setRotateAngle(carAngle);
			this.carMarker.setFlat(false);
			if (this.vehicleInCenter)
				this.naviDirMarker.setVisible(true);
			else
				this.naviDirMarker.setVisible(false);
		} else {
			this.carMarker.setFlat(true);
			this.naviDirMarker.setVisible(false);
			this.carMarker.setGeoPoint(this.hideCarMarker.getGeoPoint());
			this.carMarker.setRotateAngle(this.hideCarMarker.getRotateAngle());
		}
	}

	public void a() {
		if (this.carMarker != null)
			this.carMarker.remove();

		if (this.naviDirMarker != null)
			this.naviDirMarker.remove();

		if (this.hideCarMarker != null)
			this.hideCarMarker.remove();

		if (this.x != null)
			this.x.remove();

		this.x = null;
		this.carMarker = null;
		this.naviDirMarker = null;
		this.hideCarMarker = null;
	}
/*
更新car位置
 */
	public void updataCar(MapController mapController, LatLng carPosition, float carAngle, float paramFloat2) {
		if ((mapController == null) || (carPosition == null) || (this.carIcon == null))
			return;

		this.mMapController = mapController;
		if (this.carMarker == null) {
			this.carMarker = mapController.addMarker(new MarkerOptions().anchor(0.5F, 0.5F).setFlat(true).icon(this.carIcon).position(carPosition));
		}

		if (this.hideCarMarker == null) {
			this.hideCarMarker = mapController.addMarker(new MarkerOptions().anchor(0.5F, 0.5F).setFlat(true).icon(this.carIcon).position(carPosition));
			this.hideCarMarker.setRotateAngle(carAngle);
			this.hideCarMarker.setVisible(false);
		}

		if (this.naviDirMarker == null) {
			this.naviDirMarker = mapController.addMarker(new MarkerOptions().anchor(0.5F, 0.5F)	.setFlat(true).icon(this.naviDirIcon).position(carPosition));
			if (this.vehicleInCenter)
				this.naviDirMarker.setVisible(true);
			else
				this.naviDirMarker.setVisible(false);

			this.naviDirMarker.setPositionByPixels(	(int) (this.mMapView.getWidth() * mNaviView.getAnchorX()),(int) (this.mMapView.getHeight() * mNaviView.getAnchorY()));
		}
		this.carMarker.setVisible(true);

		IPoint localIPoint = new IPoint();
		MapProjection.lonlat2Geo(carPosition.longitude, carPosition.latitude,localIPoint);
		computeCarAngle(localIPoint, carAngle, paramFloat2);//计算车向
		doWhileUpdataCar();//更新车向

	}
	public void a(LatLng paramLatLng) {
		this.w = paramLatLng;
	}

	public void destroy() {
		if (this.carMarker != null)
			this.carMarker.remove();

		if (this.hideCarMarker != null)
			this.hideCarMarker.remove();

		if (this.naviDirMarker != null)
			this.naviDirMarker.remove();

		this.carIcon = null;
		this.gpsFailcarIcon = null;
		if (this.k != null)
			this.k.cancel();
	}

	private void computeCarAngle(IPoint paramIPoint, float paramFloat1, float paramFloat2) {
		if (this.carMarker == null)
			return;

		this.i = this.hideCarMarker.getGeoPoint();
		if ((this.i == null) || (this.i.x == 0) || (this.i.y == 0))
			this.i = paramIPoint;
		this.loopTime = 0;
		this.lastCarPos = this.i;
		this.deltax = ((paramIPoint.x - this.i.x) / this.divideTime);
		this.deltay = ((paramIPoint.y - this.i.y) / this.divideTime);
		this.lastCarAngle = this.hideCarMarker.getRotateAngle();//车向的旋转角度
		int i1 = 0;
		if (this.lastCarAngle == paramFloat1)//车向等于路向
			i1 = 1;
		else {
			this.lastCarAngle = (360.0F - this.lastCarAngle);//车向转成顺时角度
		}

		float f1 = paramFloat1 - this.lastCarAngle;//计算路向和车向的角度差
		if (i1 != 0)
			f1 = 0.0F;

		if (f1 > 180.0F)
			f1 -= 360.0F;
		else if (f1 < -180.0F)
			f1 += 360.0F;
		this.deltaAngle = (f1 / this.divideTime);//角度分十次完成转向

		this.needChangeCarAngle = true;
	}
/*
定时调整车位
 */
	private void doWhileUpdataCar() {
		if (this.k == null) {
			this.k = new Timer();
			this.k.schedule(new TimerTask() {
				public void run() {
					NaviCarOverlay.this.changeCarAngle();
				}
			}, 0L, 100L);
		}
	}
/*
调整车向
 */
	private void changeCarAngle() {
		if (!(this.needChangeCarAngle))
			return;
		if (this.carMarker == null)
			return;

		if (this.mMapController == null)
			return;
		try {
			IPoint localIPoint = this.carMarker.getGeoPoint();
			int newCarPosX = 0;
			int newCarPosY = 0;
			if (this.loopTime++ < this.divideTime) {
				newCarPosX = this.lastCarPos.x + this.deltax * this.loopTime;
				newCarPosY = this.lastCarPos.y + this.deltay * this.loopTime;
				this.l = (this.lastCarAngle + this.deltaAngle * this.loopTime);//车向+车向和路向的角度差
				this.l %= 360.0F;
				if ((newCarPosX != 0) || (newCarPosY != 0))
					localIPoint = new IPoint(newCarPosX, newCarPosY);
				if (this.lockCar) {
					float mapAngle=l;
					float carAngle=0.0f;
					if(this.north_up_mode){
						mapAngle=0.0f;
						carAngle=360.0f-this.l;
					}

					if((zoomin||zoomout)){
						if(zoomflag<=0){
							zoomin=false;
							zoomout=false;
						}
						if(zoomin&&!zoomIng){
							this.mMapController.animateCamera(CameraUpdateFactory.zoomIn(), new ZoomMapCallback());
							zoomIng=true;
						}
						if(zoomout&&!zoomIng)
						{
							this.mMapController.animateCamera(CameraUpdateFactory.zoomOut(), new ZoomMapCallback());
							zoomIng=true;
						}
					}else{
						this.mMapController.moveCamera(CameraUpdateFactory.changeBearingGeoCenter(mapAngle, localIPoint));//this.l
					}
					int i3 = (int) (this.mMapView.getWidth() * mNaviView.getAnchorX());
					int i4 = (int) (this.mMapView.getHeight() * mNaviView.getAnchorY());
					this.carMarker.setPositionByPixels(i3, i4);
					this.carMarker.setRotateAngle(carAngle);
					this.carMarker.setFlat(false);
					if (this.naviDirMarker != null) {
						this.naviDirMarker.setPositionByPixels(i3, i4);
						if (this.vehicleInCenter)
							this.naviDirMarker.setVisible(true);
						else
							this.naviDirMarker.setVisible(false);
					}

				} else {
					this.carMarker.setGeoPoint(localIPoint);
					this.carMarker.setFlat(true);
					this.carMarker.setRotateAngle(360.0F - this.l);
					if (this.naviDirMarker != null)
						this.naviDirMarker.setVisible(false);

				}

				if (this.hideCarMarker != null)
					this.hideCarMarker.setGeoPoint(localIPoint);

				if (this.hideCarMarker != null)
					this.hideCarMarker.setRotateAngle(360.0F - this.l);

				a(localIPoint);
				if(mNaviView.getNaviType() == Navi.GPSNaviMode){
					drawGuideLink(this.carMarker.getPosition(), this.w,true);
				}
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}
	class ZoomMapCallback implements MapController.CancelableCallback{
		@Override
		public void onFinish() {
			zoomflag-=1.0f;
			zoomIng=false;
		}
		@Override
		public void onCancel() {
			zoomflag-=1.0f;
			zoomIng=false;
		}
	}

	void a(IPoint paramIPoint) {
		try {
			if (this.m == -1)
				return;

			if (this.w != null) {
				DPoint localDPoint = new DPoint();
				MapProjection.geo2LonLat(paramIPoint.x, paramIPoint.y,
						localDPoint);
				LatLng localLatLng = new LatLng(localDPoint.y, localDPoint.x,
						false);
				this.y.clear();
				this.y.add(localLatLng);
				this.y.add(this.w);
				if (this.x == null)
					this.x = this.mMapController.addPolyline(new PolylineOptions()
							.add(localLatLng).add(this.w).color(this.m)
							.width(5.0F));
				else
					this.x.setPoints(this.y);

			} else if (this.x != null) {
				this.x.remove();
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	public void c() {
		if (this.x != null)
			this.x.remove();
	}

	public void a(int paramInt) {
		if ((paramInt == -1) && (this.x != null))
			this.x.remove();

		this.m = paramInt;
	}

	public void d() {
		if (this.carMarker == null)
			return;

		int i1 = (int) (this.mMapView.getWidth() * mNaviView.getAnchorX());
		int i2 = (int) (this.mMapView.getHeight() * mNaviView.getAnchorY());
		if (this.lockCar) {

			LatLng localLatLng = this.hideCarMarker.getPosition();
			CameraUpdate localCameraUpdate1 = CameraUpdateFactory
					.changeBearing(this.l);
			this.mMapController.moveCamera(localCameraUpdate1);
			CameraUpdate localCameraUpdate2 = CameraUpdateFactory
					.changeLatLng(localLatLng);
			this.mMapController.moveCamera(localCameraUpdate2);
			this.carMarker.setPositionByPixels(i1, i2);
		}
		if (this.naviDirMarker != null) {
			this.naviDirMarker.setPositionByPixels(i1, i2);
			if ((this.vehicleInCenter) && (this.lockCar))
				this.naviDirMarker.setVisible(true);
			else
				this.naviDirMarker.setVisible(false);
		}
	}

	public void zoomInMapLevel(float inLevel){
		this.zoomin=true;
		this.zoomflag=inLevel;
	}
	public void zoomOutMapLevel(float outLevel){
		this.zoomout=true;
		this.zoomflag=outLevel;
	}

	private Polyline guideLink = null;
	/**
	 * 绘制黑色虚线引导线，用于指示当前位置与导航规划路线之间连线。
	 * @param startPos - 起始位置
	 * @param endPos - 结束位置
	 * @param isShow - 是否显示
	 */
	public void drawGuideLink(LatLng startPos, LatLng endPos, boolean isShow) {
		if (isShow) {
			ArrayList localArrayList = new ArrayList(2);
			localArrayList.add(startPos);
			localArrayList.add(endPos);
			if (this.guideLink == null) {
				this.guideLink = this.mMapController.addPolyline(new PolylineOptions()
						.addAll(localArrayList).width(5.0F).color(0xFFd34e3b)
						);
			} else
				this.guideLink.setPoints(localArrayList);

			this.guideLink.setVisible(true);
		} else if (this.guideLink != null) {
			this.guideLink.setVisible(false);
		}
	}

	public void setGpsDisable(boolean gpsDisable){
		if(null == this.carMarker){
			return;
		}
		if(gpsDisable){
			this.carMarker.setIcon(carIcon);
		}else {
			this.carMarker.setIcon(gpsFailcarIcon);
		}
	}

}