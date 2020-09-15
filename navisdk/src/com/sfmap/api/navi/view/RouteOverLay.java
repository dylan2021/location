package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdate;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.Circle;
import com.sfmap.api.maps.model.CircleOptions;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.NavigateArrow;
import com.sfmap.api.maps.model.NavigateArrowOptions;
import com.sfmap.api.maps.model.Polyline;
import com.sfmap.api.maps.model.PolylineOptions;
//import com.sfmap.api.maps.utils.SpatialRelationUtil;
import com.sfmap.api.navi.Navi;
import com.sfmap.api.navi.NaviException;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.model.NaviLink;
import com.sfmap.api.navi.model.NaviPath;
import com.sfmap.api.navi.model.NaviStep;
import com.sfmap.api.navi.model.NaviTrafficStatus;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.ResourcesUtil;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tbt.util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.util.Vector;


/**
 * 导航路线图层类。
 */
public class RouteOverLay {
	String TAG = "RouteOverLay";
	BitmapDescriptor routeResource = null;
	Bitmap startBitmap; //起点图片
	Bitmap endBitmap;//终点图片
	Bitmap wayBitmap;//途径点图片
	BitmapDescriptor wayPointBitmapDescriptor = null;//途径点图片描述
	List<Polyline> mTrafficColorfulPolylines = new ArrayList();//
	private BitmapDescriptor aolrResource = null;
	private BitmapDescriptor greenResource = null;
	private BitmapDescriptor noResource = null;
	private BitmapDescriptor slowResource = null;
	private BitmapDescriptor badResource = null;
	private BitmapDescriptor grayredResource = null;
	private BitmapDescriptor darkResource = null;
	private int arrowHLen = 50;
	private float mWidth = 50.0F;
	private NaviPath mMapNaviPath = null;
	private MapController mMap;
	private Context mContext;

	private Marker startMarker; //起点标注
	private List<Marker> wayMarkers; //途径点标注
	private Marker endMarker; //终点标注
	private List<LatLng> mapList;

	private Polyline guideLink = null;
	private Polyline mDefaultPolyline;

	private List<Circle> gpsCircles = null;
	private boolean emulateGPSLocationVisibility = true;
	private NavigateArrow naviArrow = null;
	private boolean isTrafficLine = false;
	private List<Polyline> mCustomPolylines = new ArrayList();//自定义路径集合
	private BitmapDescriptor startBitmapDescriptor;
	private BitmapDescriptor endBitmapDescriptor;
	private List<Marker> trafficLigths=new ArrayList<>();
	private Polyline passedWayPolylines;//导航已走过的路径集合
	private Polyline endPassedWayPolylines;//最近点到定位点路线集合 分10段绘制
	/**
	 * 根据给定的参数，构造一个导航路线图层类对象。
	 * @param map - 地图对象。
	 * @param naviPath - 导航路线规划方案。
	 */
	public RouteOverLay(MapController map,NaviPath naviPath	) {
		init(map, naviPath);
	}

	/**
	 * 根据给定的参数，构造一个导航路线图层类对象。
	 * @param map - 地图对象。
	 * @param naviPath - 导航路线规划方案。
	 * @param context - 当前的activity对象。
	 */
	public RouteOverLay(MapController map, NaviPath naviPath, Context context) {
		this.mContext = context;
		init(map, naviPath);
	}

	void setRouteResource(BitmapDescriptor paramBitmapDescriptor) {
		this.routeResource = paramBitmapDescriptor;
	}

	/**
	 * 设置路线宽度。
	 * @param width - 路线宽度，取值范围：大于0
	 * @throws NaviException - 当取值 <= 0时，抛出异常
	 */
	public void setWidth(float width) throws NaviException {
		if (width <= 0.0F)
			throw new NaviException("非法参数-宽度必须>0");
		this.mWidth = width;
	}

	/**
	 * 获取路线信息。
	 * @return 路线信息。
	 */
	public NaviPath getMapNaviPath() {
		return this.mMapNaviPath;
	}

	/**
	 * 设置路线信息。
	 * @param naviPath - 路线信息。
	 */
	public void setMapNaviPath(NaviPath naviPath) {
		this.mMapNaviPath = naviPath;
	}

	private void init(MapController paramMap, NaviPath paramMapNaviPath) {
		try {
			this.mMap = paramMap;
			this.mMapNaviPath = paramMapNaviPath;
			this.routeResource = BitmapDescriptorFactory
					.fromAsset("routetexture.png");
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		this.aolrResource = BitmapDescriptorFactory.fromAsset("routetexture_aolr.png");
		this.greenResource = BitmapDescriptorFactory.fromAsset("routetexture_green.png");
		this.noResource = BitmapDescriptorFactory.fromAsset("routetexture_no.png");
		this.slowResource = BitmapDescriptorFactory.fromAsset("routetexture_slow.png");
		this.badResource = BitmapDescriptorFactory.fromAsset("routetexture_bad.png");
		this.grayredResource = BitmapDescriptorFactory.fromAsset("routetexture_grayred.png");
		this.darkResource = BitmapDescriptorFactory.fromAsset("routetexture_passed.png");
//		mWidth = greenResource.getWidth();
		mWidth = Utils.dp2px(mContext,20);
		LogUtil.d(TAG,"线路宽："+mWidth);
	}


	/**
	 * 将routeOverlay添加到地图上。
	 */
	public void addToMap(){
		addToMap(0,false);
	}
	public void addToMap(boolean showTrafficLigth){
		addToMap(0,showTrafficLigth);
	}

	/**
	 * 将routeOverlay添加到地图上。
	 * @param routeColorType - 路线的配色。
	 * @param isAddTrafficLigth - 是否添加红绿灯。
	 *
	 */
	public void addToMap(int routeColorType,boolean isAddTrafficLigth) {
		try {
			int j;
			Object localObject4;
			if (this.mMap == null) {
				return;
			}
			if (this.mDefaultPolyline != null) {
				this.mDefaultPolyline.remove();
				this.mDefaultPolyline = null;
			}
			if ((this.mWidth == 0.0F) || (this.mMapNaviPath == null))
				return;
			if (this.naviArrow != null)
				this.naviArrow.setVisible(false);

			List<NaviLatLng> localList = null;

			localList = this.mMapNaviPath.getCoordList();

			if (localList == null)
				return;
			int i = localList.size();
			this.mapList = new ArrayList(i);
			for (Iterator<NaviLatLng> localObject1 = localList.iterator(); ((Iterator) localObject1)
					.hasNext();) {
				 NaviLatLng localObject2 =
				 (NaviLatLng)((Iterator)localObject1).next();
				LatLng localObject3 = new LatLng(((NaviLatLng)localObject2).getLatitude(),
				 ((NaviLatLng)localObject2).getLongitude(), false);
				 this.mapList.add(localObject3);
			}

			if (this.mapList.size() == 0)
				return;
			clearTrafficLineAndInvisibleOriginalLine();
			BitmapDescriptor colorType=routeResource;
			if(routeColorType!=0)colorType=noResource;
			this.mDefaultPolyline = this.mMap.addPolyline(new PolylineOptions().addAll(this.mapList).setCustomTexture(colorType).width(this.mWidth));
			LogUtil.d(TAG,"添加mDefaultPolyline"+mCustomPolylines.size());
			this.mDefaultPolyline.setVisible(true);
			Log.i(TAG,"1-mMapNaviPath size:"+mMapNaviPath.getCoordList().size()+"mMapNaviPath steps size:"+mMapNaviPath.getSteps().size() +"trafficLigths size:"+trafficLigths.size());
			if(isAddTrafficLigth)addTrafficLight(mMapNaviPath);
			// localObject1 = null;
			LatLng localObject1 = null;
			Object localObject2 = null;
			Object localObject3 = null;
			if (this.mMapNaviPath.getStartPoint() != null )
				localObject1 = new LatLng(this.mMapNaviPath.getStartPoint()
						.getLatitude(), this.mMapNaviPath.getStartPoint()
						.getLongitude());
			if(this.mMapNaviPath.getEndPoint() != null)
				localObject2 = new LatLng(this.mMapNaviPath.getEndPoint()
						.getLatitude(), this.mMapNaviPath.getEndPoint()
						.getLongitude());
				localObject3 = this.mMapNaviPath.getWayPoint();

			if (this.startMarker != null) {
				this.startMarker.remove();
				this.startMarker = null;
			}
			if (this.endMarker != null) {
				this.endMarker.remove();
				this.endMarker = null;
			}
			if ((this.wayMarkers != null) && (this.wayMarkers.size() > 0)) {
				for (j = 0; j < this.wayMarkers.size(); ++j) {
					localObject4 = (Marker) this.wayMarkers.get(j);
					if (localObject4 != null) {
						((Marker) localObject4).remove();
						localObject4 = null;
					}

				}

			}
			if (this.startBitmap == null)
				this.startMarker = this.mMap.addMarker(new MarkerOptions().position((LatLng) localObject1).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.startpoint))));
			else {
				this.startMarker = this.mMap.addMarker(new MarkerOptions().position((LatLng) localObject1).icon(BitmapDescriptorFactory	.fromBitmap(this.startBitmap)));
			}

			if ((localObject3 != null) && (((List) localObject3).size() > 0)) {
				j = ((List) localObject3).size();
				if (this.wayMarkers == null) {
					this.wayMarkers = new ArrayList(j);
				}

				for (localObject4 = ((List) localObject3).iterator(); ((Iterator) localObject4).hasNext();) {
					NaviLatLng localNaviLatLng = (NaviLatLng) ((Iterator) localObject4)	.next();
					LatLng localLatLng = new LatLng(localNaviLatLng.getLatitude(),localNaviLatLng.getLongitude());
					Marker localMarker = null;
					if (this.wayBitmap == null)
						localMarker = this.mMap.addMarker(new MarkerOptions().position(localLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.waypoint))));
					else
						localMarker = this.mMap.addMarker(new MarkerOptions().position(localLatLng).icon(BitmapDescriptorFactory.fromBitmap(this.wayBitmap)));

					this.wayMarkers.add(localMarker);
				}
			}
			if (this.endBitmap == null)
				this.endMarker = this.mMap.addMarker(new MarkerOptions().position((LatLng) localObject2).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(), ResourcesUtil.endpoint))));
			else {
				this.endMarker = this.mMap.addMarker(new MarkerOptions().position((LatLng) localObject2).icon(BitmapDescriptorFactory.fromBitmap(this.endBitmap)));
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	private void addTrafficLight(NaviPath path){
		Bitmap mark= BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.navi_popup);
		BitmapDescriptor markDes=BitmapDescriptorFactory.fromBitmap(mark);
		List<NaviStep> steps=path.getSteps();

		if(trafficLigths.size()>0){
			for(Marker marker:trafficLigths){
				marker.setVisible(false);
				marker.destroy();
			}
			trafficLigths.clear();
		}
		for(int i=0;i<steps.size();i++){
			List<NaviLink> links=  steps.get(i).getLinks();
			for(int j=0;j<links.size();j++){
				NaviLink link=links.get(j);
				if(link.getTrafficLights()){
					List<NaviLatLng> latLngs=link.getCoords();
					NaviLatLng latlng= latLngs.get(latLngs.size()-1);
					LatLng localLatLng = new LatLng(latlng.getLatitude(), latlng.getLongitude() );
					Marker marker=this.mMap.addMarker(new MarkerOptions().icon(markDes).position(localLatLng).anchor(0.5f,0.5f));
					if(this.mMap.getCameraPosition().zoom<15f)
						marker.setVisible(false);
					else marker.setVisible(true);
					trafficLigths.add(marker);
//					Log.i(TAG, "trafficLigths.add(marker) size:"+trafficLigths.size());

				}
			}
		}
	}
	public void setShowTrafficLigth(float zoomlevel){
		boolean isShow=false;
		if(zoomlevel>15f)isShow=true;
		for(Marker marker:trafficLigths){
			marker.setVisible(isShow);
//			Log.i(TAG, "zoomlevel:"+zoomlevel +"isShow:"+isShow+"trafficLigths:"+trafficLigths.size());
		}
	}

	private void addLinkEndMarkerTest(){
		if( null == mMapNaviPath)
			return;
		List<NaviStep> listSteps = mMapNaviPath.getSteps();
		for( int i = 0 ; i < listSteps.size() ; i ++ )
		{
			List<NaviLink>  listLinks =  listSteps.get(i).getLinks();
			for( int j = 0 ; j < listLinks.size() ; j ++ )
			{
				NaviLink link = listLinks.get(j);

				List<NaviLatLng> listCoords = link.getCoords();
				NaviLatLng latlng = listCoords.get( listCoords.size() - 1);

				com.sfmap.api.maps.model.LatLng pos = new com.sfmap.api.maps.model.LatLng(latlng.getLatitude(), latlng.getLongitude());

				this.mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromBitmap(this.wayBitmap)));
			}
		}
	}

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
				this.guideLink = this.mMap.addPolyline(new PolylineOptions()
						.addAll(localArrayList).width(this.mWidth / 3.0F)
						.setDottedLine(true));
			} else
				this.guideLink.setPoints(localArrayList);

			this.guideLink.setVisible(true);
		} else if (this.guideLink != null) {
			this.guideLink.setVisible(false);
		}
	}

	/**
	 * 绘制模拟GPS位置。
	 * @param gpsData  - gps位置。
	 */
	public void drawEmulateGPSLocation(Vector<String> gpsData) {
		Iterator localIterator;
		Object localObject;
		try {
			if (this.gpsCircles == null) {
				this.gpsCircles = new ArrayList(gpsData.size());
			} else {
				for (localIterator = this.gpsCircles.iterator(); localIterator.hasNext();) {
					localObject = (Circle) localIterator.next();
					((Circle) localObject).remove();
				}
				this.gpsCircles.clear();
			}
			for (localIterator = gpsData.iterator(); localIterator.hasNext();) {
				localObject = (String) localIterator.next();
				String[] arrayOfString = ((String) localObject).split(",");
				if ((arrayOfString != null) && (arrayOfString.length >= 11)) {
					LatLng localLatLng = new LatLng(
							Double.parseDouble(arrayOfString[0]),
							Double.parseDouble(arrayOfString[1]));
					Circle localCircle = this.mMap
							.addCircle(new CircleOptions().center(localLatLng)
									.radius(1.5D).strokeWidth(0.0F)
									.fillColor(-65536));

					this.gpsCircles.add(localCircle);
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * 模拟GPS位置是否可见。
	 */
	public void setEmulateGPSLocationVisible() {
		if (this.gpsCircles != null) {
			this.emulateGPSLocationVisibility = (!(this.emulateGPSLocationVisibility));
			for (Circle localCircle : this.gpsCircles)
				localCircle.setVisible(this.emulateGPSLocationVisibility);
		}
	}

	/**
	 * 设置起始点bitmap。
	 * @param bitmap - 起始点的bitmap。
	 */
	public void setStartPointBitmap(Bitmap bitmap) {
		this.startBitmap = bitmap;
		if (this.startBitmap != null)
			this.startBitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(this.startBitmap);
	}

	/**
	 * 设置途经点的bitmap。
	 * @param bitmap - 途径点的bitmap。
	 */
	public void setWayPointBitmap(Bitmap bitmap) {
		this.wayBitmap = bitmap;
		if (this.wayBitmap != null)
			this.wayPointBitmapDescriptor = BitmapDescriptorFactory	.fromBitmap(this.wayBitmap);
	}

	/**
	 * 设置结束点的bitmap。
	 * @param bitmap - 结束点的bitmap。
	 */
	public void setEndPointBitmap(Bitmap bitmap) {
		this.endBitmap = bitmap;
		if (this.endBitmap != null)
			this.endBitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(this.endBitmap);
	}

	public boolean getVisible(){
		return this.mDefaultPolyline.isVisible();
	}
	/**
	 * 将RouteOverlay从地图上移除。
	 */
	public void removeFromMap() {
		try {
			Log.i(TAG, "removeFromMap()");
			Iterator localIterator;
			Object localObject;
			if (this.mDefaultPolyline != null) {
				this.mDefaultPolyline.setVisible(false);
			}

			if (this.startMarker != null){
				this.startMarker.setVisible(false);
				this.startMarker.remove();
			}

			if (this.wayMarkers != null)
				for (localIterator = this.wayMarkers.iterator(); localIterator
						.hasNext();) {
					localObject = (Marker) localIterator.next();
					((Marker) localObject).setVisible(false);
				}
			if(trafficLigths!=null&&trafficLigths.size()>0){
				for(Marker marker:trafficLigths){
					marker.setVisible(false);
					marker.destroy();
					Log.i(TAG, "removeFromMap() -- marker.destroy()--trafficLigths.size()"+trafficLigths.size());
				}
			}
			if (this.endMarker != null){
				this.endMarker.setVisible(false);
				this.endMarker.remove();
			}

			if (this.naviArrow != null)
				this.naviArrow.remove();

			if(passedWayPolylines != null){
				passedWayPolylines.remove();
			}

			if(arrowPolyline != null){
				arrowPolyline.remove();
			}

			if (this.guideLink != null)
				this.guideLink.setVisible(false);

			if (this.gpsCircles != null)
				for (localIterator = this.gpsCircles.iterator(); localIterator
						.hasNext();) {
					localObject = (Circle) localIterator.next();
					((Circle) localObject).setVisible(false);
				}

			clearTrafficLineAndInvisibleOriginalLine();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	private void clearTrafficLineAndInvisibleOriginalLine() {
		int i;
		LogUtil.d(TAG,"隐藏mTrafficColorfulPolylines"+mTrafficColorfulPolylines.size());
		if (this.mTrafficColorfulPolylines.size() > 0)
			for (i = 0; i < this.mTrafficColorfulPolylines.size(); ++i)
				if (this.mTrafficColorfulPolylines.get(i) != null)
					((Polyline) this.mTrafficColorfulPolylines.get(i)).remove();

		this.mTrafficColorfulPolylines.clear();

		if (this.mDefaultPolyline != null) {
			this.mDefaultPolyline.setVisible(false);
		}

		if (this.mCustomPolylines.size() > 0)
			for (i = 0; i < this.mCustomPolylines.size(); ++i)
				if (this.mCustomPolylines.get(i) != null)
					((Polyline) this.mCustomPolylines.get(i)).setVisible(false);
	}

	int newpos = 0;
	List<LatLng> beforeList = null;
	List<LatLng> endList = null;
	Polyline arrowPolyline;
//	public void passedWayUpdate(NaviInfo naviInfo){
//		if (this.mMap == null)
//			return;
//
//		if (naviInfo == null)
//			return;
//
//		if ((this.mapList == null) || (this.mapList.size() <= 0))
//			return;
//
//		NaviLatLng naviLatLng = getMapNaviPath().getSteps().get(naviInfo.getCurStep()).getCoords().get(naviInfo.getCurPoint());
//		LatLng navi = new LatLng(naviLatLng.getLatitude(),naviLatLng.getLongitude());
//		newpos = mapList.indexOf(navi);
//
//		beforeList = new ArrayList<>();
//		endList = new ArrayList<>();
//
//		for(int i=0;i<=newpos;i++){
//			beforeList.add(mapList.get(i));
//		}
//		if(passedWayPolylines != null){
//			passedWayPolylines.remove();
//		}
//		passedWayPolylines = mMap.addPolyline(new PolylineOptions().addAll(beforeList).width(mWidth).setCustomTexture(darkResource));
//
//        LatLng naviInfoLatlng = new LatLng(naviInfo.getCoord().getLatitude(),naviInfo.getCoord().getLongitude());
//		LatLng shortestDistancePoint;
//        if(mapList.get(newpos).equals(mapList.get(newpos+1))){
//			shortestDistancePoint = SpatialRelationUtil.getProjectivePoint(mapList.get(newpos),mapList.get(newpos+2),naviInfoLatlng);
//		}else {
//			shortestDistancePoint = SpatialRelationUtil.getProjectivePoint(mapList.get(newpos),mapList.get(newpos+1),naviInfoLatlng);
//		}
////		 shortestDistancePoint = SpatialRelationUtil.getProjectivePoint(mapList.get(newpos),mapList.get(newpos+1),naviInfoLatlng);
////        LatLng shortestDistancePoint = SpatialRelationUtil.calShortestDistancePoint(mapList,naviInfoLatlng);
////		endList.addAll(getlists(mapList.get(oldpos),tmp));
////		endPass = true;
//
//		if(endPassedWayPolylines != null){
//			endPassedWayPolylines.remove();
//		}
//		endPassedWayPolylines = mMap.addPolyline(new PolylineOptions().add(mapList.get(newpos)).add(shortestDistancePoint).width(mWidth).setCustomTexture(darkResource));
//
////		new Thread(new MyThread()).start();
////		getProjectivePoint(mapList.get(oldpos),mapList.get(newpos),tmp,tmp);
////		oldpos = newpos;
////		endList.add(mapList.get(oldpos));
////		endList.add(tmp);
////
////		endList.addAll(getlists(mapList.get(oldpos),real));
////		endList.add(new LatLng(naviInfo.getCoord().getLatitude(),naviInfo.getCoord().getLongitude()));
//
////		endPassedWayPolylines.add(mMap.addPolyline(new PolylineOptions().addAll(endList).width(mWidth).setCustomTexture(darkResource)));
//		List<LatLng> arrows = new ArrayList<>();
//		for(int i=0;i<=newpos;i++){
//			arrows.add(mapList.get(i));
//		}
//		arrows.add(shortestDistancePoint);
//		if(arrowPolyline!=null){
//			arrowPolyline.remove();
//		}
//		arrowPolyline = this.mMap.addPolyline(new PolylineOptions().addAll(arrows).width(this.mWidth).setCustomTexture(this.aolrResource));
//
//	}

	public void colorWayUpdate(List<NaviTrafficStatus> tmcList) {
		LogUtil.d(TAG,"colorWayUpdate1"+tmcList.size());
		if (this.mMap == null)
			return;

		if ((this.mapList == null) || (this.mapList.size() <= 0))
			return;
		LogUtil.d(TAG,"colorWayUpdate2");
		if ((tmcList == null) || (tmcList.size() <= 0))
			return;
		LogUtil.d(TAG,"colorWayUpdate3");
		clearTrafficLineAndInvisibleOriginalLine();//清除所有路况
		int i = 0;
		Object startpoint = (LatLng) this.mapList.get(0);//起点坐标
		LatLng localLatLng1 = null;
		double d1 = 0.0D;
		LogUtil.d(TAG,"colorWayUpdate4");
		ArrayList localArrayList = new ArrayList();
		Polyline localPolyline = null;
		for (int j = 0; (j < this.mapList.size()) && (i < tmcList.size()); ++j) {
			LogUtil.d(TAG,"colorWayUpdate5"+mapList.size());
			NaviTrafficStatus localMapTrafficStatus = (NaviTrafficStatus) tmcList.get(i);
			localLatLng1 = (LatLng) this.mapList.get(j);
			NaviLatLng localNaviLatLng1 = new NaviLatLng(((LatLng) startpoint).latitude,((LatLng) startpoint).longitude);
			NaviLatLng localNaviLatLng2 = new NaviLatLng(localLatLng1.latitude,	localLatLng1.longitude);
			double d2 = NaviUtilDecode.calculateLength(localNaviLatLng1, localNaviLatLng2);
			d1 += d2;
			if (d1 > localMapTrafficStatus.getLength() + 1) {
				double d3 = d2 - (d1 - localMapTrafficStatus.getLength());
				NaviLatLng localNaviLatLng3 = NaviUtilDecode.a(localNaviLatLng1,localNaviLatLng2, d3);
				LatLng localLatLng3 = new LatLng(localNaviLatLng3.getLatitude(),localNaviLatLng3.getLongitude());//计算路况段尾的坐标
				localArrayList.add(localLatLng3);
				startpoint = localLatLng3;
				--j;
			} else {
				localArrayList.add(localLatLng1);
				startpoint = localLatLng1;
			}
			//如果是最后一段或到最后路况相同,绘制剩下所有路径
			if ((d1 >= localMapTrafficStatus.getLength())|| (j == this.mapList.size() - 1)) {
				if ((i == tmcList.size() - 1)&& (j < this.mapList.size() - 1))
					for (++j; j < this.mapList.size(); ++j) {
						LatLng localLatLng2 = (LatLng) this.mapList.get(j);
						localArrayList.add(localLatLng2);
					}

				++i;
					LogUtil.d(TAG,"colorWayUpdate"+localMapTrafficStatus.getStatus());
				switch (localMapTrafficStatus.getStatus()) {
				case 0://无路况的默认为畅通
					localPolyline = this.mMap.addPolyline(new PolylineOptions().addAll(localArrayList).width(this.mWidth).setCustomTexture(this.greenResource));

					break;
				case 1:
					localPolyline = this.mMap.addPolyline(new PolylineOptions()	.addAll(localArrayList).width(this.mWidth).setCustomTexture(this.greenResource));

					break;
				case 2:
					localPolyline = this.mMap.addPolyline(new PolylineOptions()	.addAll(localArrayList).width(this.mWidth).setCustomTexture(this.slowResource));

					break;
				case 3:
					localPolyline = this.mMap.addPolyline(new PolylineOptions()	.addAll(localArrayList).width(this.mWidth).setCustomTexture(this.badResource));

					break;
				case 4:
					localPolyline = this.mMap.addPolyline(new PolylineOptions()	.addAll(localArrayList).width(this.mWidth).setCustomTexture(this.grayredResource));
				}

				this.mTrafficColorfulPolylines.add(localPolyline);
				localArrayList.clear();
				localArrayList.add(startpoint);
				d1 = 0.0D;
			}
		}
		//绘制箭头
		localPolyline = this.mMap.addPolyline(new PolylineOptions()	.addAll(this.mapList).width(this.mWidth).setCustomTexture(this.aolrResource));

		this.mTrafficColorfulPolylines.add(localPolyline);
	}

	/**
	 * 将地图zoom到可以全览全路段的级别。
	 */
	public void zoomToSpan() {
		try {
			if (this.mMapNaviPath == null)
				return;

			CameraUpdate localCameraUpdate = CameraUpdateFactory
					.newLatLngBounds(this.mMapNaviPath.getBoundsForPath(), 100);
			this.mMap.animateCamera(localCameraUpdate, 1000L, null);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 释放路线资源。
	 */
	public void destroy() {
		try {
			LogUtil.d(TAG,"destroy（）");
			if (this.mDefaultPolyline != null)
				this.mDefaultPolyline.remove();

			this.mMapNaviPath = null;
			if (this.aolrResource != null)
				this.aolrResource.recycle();
			if (this.greenResource != null)
				this.greenResource.recycle();
			if (this.noResource != null)
				this.noResource.recycle();
			if (this.slowResource != null)
				this.slowResource.recycle();
			if (this.badResource != null)
				this.badResource.recycle();
			if (this.grayredResource != null)
				this.grayredResource.recycle();
			if (this.darkResource != null)
				this.darkResource.recycle();
			if (this.startBitmap != null)
				this.startBitmap.recycle();

			if (this.endBitmap != null)
				this.endBitmap.recycle();

			if (this.wayBitmap != null)
				this.wayBitmap.recycle();
			if(trafficLigths!=null)
				trafficLigths=null;
			Log.i(TAG, "destroy()");


		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 绘制箭头。
	 * @param list - 坐标列表。
	 */
	public void drawArrow(List<NaviLatLng> list) {
		try {
			if (list == null) {
				this.naviArrow.setVisible(false);
				return;
			}

			int i = list.size();
			ArrayList localArrayList = new ArrayList(i);
			for (NaviLatLng localNaviLatLng : list) {
				LatLng localLatLng = new LatLng(localNaviLatLng.getLatitude(),
						localNaviLatLng.getLongitude(), false);
				localArrayList.add(localLatLng);
			}

			if (this.naviArrow == null)
				this.naviArrow = this.mMap
						.addNavigateArrow(new NavigateArrowOptions().addAll(
								localArrayList).width(20.0F));
			else
				this.naviArrow.setPoints(localArrayList);

			this.naviArrow.setZIndex(1.0F);
			this.naviArrow.setVisible(true);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 获取箭头点集。
	 * @param segIndex - 路段的索引。
	 * @return 箭头坐标点集。
	 */
	public List<NaviLatLng> getArrowPoints(int segIndex) {
		if (this.mMapNaviPath == null||segIndex<=0)
			return null;
		try {
			NaviLatLng localNaviLatLng2;
			int i1;
			NaviLatLng localNaviLatLng3;
			if (segIndex >= this.mMapNaviPath.getStepsCount())
				return null;

			List localList1 = this.mMapNaviPath.getCoordList();
			int i = localList1.size();
			List localList2 = this.mMapNaviPath.getSteps();
			NaviStep localMapNaviStep = (NaviStep) localList2
					.get(segIndex);
			int j = localMapNaviStep.getEndIndex();
			NaviLatLng localNaviLatLng1 = (NaviLatLng) localList1.get(j);
			Vector localVector = new Vector();
			Object localObject = localNaviLatLng1;
			int k = 0;
			for (int l = j - 1; l >= 0; --l) {
				localNaviLatLng2 = (NaviLatLng) localList1.get(l);
				i1 = NaviUtilDecode.calculateLength((NaviLatLng) localObject, localNaviLatLng2);
				k += i1;
				if (k >= this.arrowHLen) {
					localNaviLatLng3 = NaviUtilDecode.a((NaviLatLng) localObject,
							localNaviLatLng2, this.arrowHLen + i1 - k);
					localVector.add(localNaviLatLng3);
					break;
				}
				localObject = localNaviLatLng2;
				localVector.add(localNaviLatLng2);
			}

			Collections.reverse(localVector);

			localVector.add(localNaviLatLng1);
			k = 0;
			localObject = localNaviLatLng1;
			// for ( l = j + 1; l < i; ++l)
			for (int l = j + 1; l < i; ++l) {
				localNaviLatLng2 = (NaviLatLng) localList1.get(l);
				i1 = NaviUtilDecode.calculateLength((NaviLatLng) localObject, localNaviLatLng2);
				k += i1;
				if (k >= this.arrowHLen) {
					localNaviLatLng3 = NaviUtilDecode.a((NaviLatLng) localObject,
							localNaviLatLng2, this.arrowHLen + i1 - k);
					localVector.add(localNaviLatLng3);
					break;
				}
				localObject = localNaviLatLng2;
				localVector.add(localNaviLatLng2);
			}

			if (localVector.size() > 2)
				return localVector;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return ((List<NaviLatLng>) null);
	}

//	/**
//	 * 当前是否显示交通线。
//	 * @return 是否显示交通线。
//	 */
	public boolean isTrafficLine() {
		return this.isTrafficLine;
	}
//
//	/**
//	 * 是否开启交通线。
//	 * @param enabled - 是否开启交通线
//	 */
	public void setTrafficLine(Boolean enabled) {
		//屏蔽掉TMC功能
		int i;
		try {
			if (this.mContext == null)
				return;

			this.isTrafficLine = enabled.booleanValue();
			List localList = null;
			clearTrafficLineAndInvisibleOriginalLine();
			if (this.isTrafficLine) {
				if (this.mMapNaviPath != null)
					localList = Navi.getInstance(this.mContext).getTrafficStatuses(0,	this.mMapNaviPath.getAllLength());
				if(null == localList){
//					LogUtil.d(TAG,"null == localList"+this.mMapNaviPath.getAllLength());
				}
				colorWayUpdate(localList);
			} else {
				if (this.mDefaultPolyline != null) {
					this.mDefaultPolyline.setVisible(true);
					LogUtil.d(TAG,"显示mDefaultPolyline1"+mCustomPolylines.size());
				}

				if (this.mCustomPolylines.size() > 0)
					for (i = 0; i < this.mCustomPolylines.size(); ++i)
						if (this.mCustomPolylines.get(i) != null)
							((Polyline) this.mCustomPolylines.get(i)).setVisible(true);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	private void addToMap(int[] paramArrayOfInt1, int[] paramArrayOfInt2,BitmapDescriptor[] paramArrayOfBitmapDescriptor) {
		try {
			// label375: int i1;
			int i1;
			Object localObject3;
			if (this.mMap == null) {
				return;
			}

			if (this.mDefaultPolyline != null) {
				this.mDefaultPolyline.remove();
				this.mDefaultPolyline = null;
				LogUtil.d(TAG,"移除mDefaultPolyline1");
			}

			if ((this.mWidth == 0.0F) || (this.mMapNaviPath == null)
					|| (this.routeResource == null))
				return;

			if (this.naviArrow != null)
				this.naviArrow.setVisible(false);

			List localList = null;

			localList = this.mMapNaviPath.getCoordList();

			if (localList == null)
				return;

			clearTrafficLineAndInvisibleOriginalLine();
			int i = localList.size();
			this.mapList = new ArrayList(i);
			ArrayList localArrayList = new ArrayList();
			int j = 0;

			int k = 0;
			if (paramArrayOfInt1 == null)
				k = paramArrayOfBitmapDescriptor.length;
			else
				k = paramArrayOfInt1.length;

			for (int l = 0; l < k; ++l) {
				if ((paramArrayOfInt2 != null) && (l < paramArrayOfInt2.length)	&& (paramArrayOfInt2[l] <= 0))
					// break label375:
					break;

				localArrayList.clear();
				for (; j < localList.size(); ++j) {
					// localObject1 = (NaviLatLng)localList.get(j);
					NaviLatLng localObject1 = (NaviLatLng) localList.get(j);
					LatLng localObject2 = new LatLng(((NaviLatLng) localObject1).getLatitude(),	((NaviLatLng) localObject1).getLongitude(), false);
					this.mapList.add(localObject2);
					localArrayList.add(localObject2);
					if ((paramArrayOfInt2 != null)	&& (l < paramArrayOfInt2.length)&& (j == paramArrayOfInt2[l]))
						break;
				}

				Polyline localPolyline; // //
				if ((paramArrayOfBitmapDescriptor == null)	|| (paramArrayOfBitmapDescriptor.length == 0)) {
					localPolyline = this.mMap.addPolyline(new PolylineOptions().addAll(localArrayList).color(paramArrayOfInt1[l])	.width(this.mWidth));
				} else {
					localPolyline = this.mMap.addPolyline(new PolylineOptions().addAll(localArrayList)	.setCustomTexture(paramArrayOfBitmapDescriptor[l])	.width(this.mWidth));
				}

				localPolyline.setVisible(true);
				this.mCustomPolylines.add(localPolyline);
				LogUtil.d(TAG,"添加mCustomPolylines");
			}
			Polyline localPolyline = this.mMap.addPolyline(new PolylineOptions().addAll(this.mapList).width(this.mWidth).setCustomTexture(this.aolrResource));
			LogUtil.d(TAG,"添加mCustomPolylines1"+mCustomPolylines.size());
			this.mCustomPolylines.add(localPolyline);

			LatLng localLatLng1 = null;
			Object localObject1 = null;
			Object localObject2 = null;
			if ((this.mMapNaviPath.getStartPoint() != null) && (this.mMapNaviPath.getEndPoint() != null)) {
				localLatLng1 = new LatLng(this.mMapNaviPath.getStartPoint().getLatitude(), this.mMapNaviPath.getStartPoint()	.getLongitude());
				localObject1 = new LatLng(this.mMapNaviPath.getEndPoint().getLatitude(), this.mMapNaviPath.getEndPoint()	.getLongitude());
				localObject2 = this.mMapNaviPath.getWayPoint();
			}
			if (this.startMarker != null) {
				this.startMarker.remove();
				this.startMarker = null;
			}
			if (this.endMarker != null) {
				this.endMarker.remove();
				this.endMarker = null;
			}
			if ((this.wayMarkers != null) && (this.wayMarkers.size() > 0))
				for (i1 = 0; i1 < this.wayMarkers.size(); ++i1) {
					localObject3 = (Marker) this.wayMarkers.get(i1);
					if (localObject3 != null) {
						((Marker) localObject3).remove();
						((Marker) localObject3).destroy();
						localObject3 = null;
					}

				}

			if (this.startBitmap == null) {
				this.startMarker = this.mMap.addMarker(new MarkerOptions().position(localLatLng1).icon(	BitmapDescriptorFactory	.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.startpoint))));
			} else if (this.startBitmapDescriptor != null) {
				this.startMarker = this.mMap.addMarker(new MarkerOptions().position(localLatLng1).icon(this.startBitmapDescriptor));
			}

			if ((localObject2 != null) && (((List) localObject2).size() > 0)) {
				i1 = ((List) localObject2).size();
				if (this.wayMarkers == null)
					this.wayMarkers = new ArrayList(i1);

				for (localObject3 = ((List) localObject2).iterator(); ((Iterator) localObject3)	.hasNext();) {
					NaviLatLng localNaviLatLng = (NaviLatLng) ((Iterator) localObject3)	.next();
					LatLng localLatLng2 = new LatLng(localNaviLatLng.getLatitude(),localNaviLatLng.getLongitude());

					Marker localMarker = null;
					if (this.wayBitmap == null) {
						localMarker = this.mMap.addMarker(new MarkerOptions().position(localLatLng2).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.waypoint))));
					} else if (this.wayPointBitmapDescriptor != null) {
						localMarker = this.mMap.addMarker(new MarkerOptions().position(localLatLng2).icon(this.wayPointBitmapDescriptor));
					}

					this.wayMarkers.add(localMarker);
				}
			}

			if (this.endBitmap == null) {
				this.endMarker = this.mMap.addMarker(new MarkerOptions().position((LatLng) localObject1).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.endpoint))));
			} else if (this.endBitmapDescriptor != null) {
				this.endMarker = this.mMap.addMarker(new MarkerOptions()
						.position((LatLng) localObject1).icon(
								this.endBitmapDescriptor));
			}

			//不支持功能
//			if (this.isTrafficLine)
//				setTrafficLine(Boolean.valueOf(this.isTrafficLine));
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 将routeOverlay添加到地图上。
	 * @param color - 用户自定义的各路段的颜色。
	 * @param index - 用来间隔各路段的索引数值。
	 */
	public void addToMap(int[] color, int[] index) {
		if ((color == null) || (color.length == 0))
			return;

		addToMap(color, index, null);//参数1，数段颜色数组，参数2.途径点index数组
	}

	/**
	 * 将routeOverlay添加到地图上。
	 * @param routeResource - 用户自定义的各路段的纹理资源。
	 * @param index - 用来间隔各路段的索引数组。
	 */
	public void addToMap(BitmapDescriptor[] routeResource, int[] index) {
		if ((routeResource == null)
				|| (routeResource.length == 0))
			return;

		addToMap(null, index, routeResource);
	}

	/**
	 * 设置该routeOverlay的透明度。
	 * @param value - 范围为[0,1]，设置1的时候为完全透明，设置0的时候为完全不透明
	 */
	public void setTransparency(float value) {
		if (this.mDefaultPolyline != null)
			this.mDefaultPolyline.setTransparency(value);

		for (Polyline localPolyline : this.mTrafficColorfulPolylines)
			localPolyline.setTransparency(value);
	}

	private List<LatLng> getlists(LatLng start,LatLng end){
		List<LatLng> latLngs = new ArrayList<>();
		latLngs.add(start);
		for(int i= 1;i<10;i++){
			double midLatitude = add(start.latitude,(sub(end.latitude,start.latitude)*(double) i)/10.0);
			double midLongitude = add(start.longitude,(sub(end.longitude,start.longitude)*(double)i)/10.0);
			latLngs.add(new LatLng(midLatitude,midLongitude));
		}
		latLngs.add(end);
		return latLngs;
	}

	public static double sub(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();
	}

	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2).doubleValue();
	}

	int curPos = 0;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// 要做的事情
			if(curPos > (endList.size()-1)){
				endPass = false;
				curPos = 0;
				return;
			}
			if(curPos == 0 && endList.size()>1){
				endPassedWayPolylines = mMap.addPolyline(new PolylineOptions().add(endList.get(0)).add(endList.get(1)).width(mWidth).setCustomTexture(darkResource));
				curPos++;

			}else {
				endPassedWayPolylines.addPoint(endList.get(curPos));

			}
			curPos++;
			super.handleMessage(msg);
		}
	};

	boolean endPass = false;
	public class MyThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (endPass && curPos < (endList.size()-1)) {
				try {
					Thread.sleep(100);// 线程暂停10秒，单位毫秒
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);// 发送消息

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			endPass = false;
			curPos = 0;
		}
	}
}