package com.sfmap.api.navi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.navi.model.NaviCamera;
import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.api.navi.model.NaviLaneInfo;
import com.sfmap.api.navi.model.NaviLocation;
import com.sfmap.api.navi.model.NaviPath;
import com.sfmap.api.navi.model.NaviTrafficFacilityInfo;
import com.sfmap.api.navi.model.NaviServiceFacilityInfo;
import com.sfmap.api.navi.model.NaviTrafficStatus;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.api.navi.model.SatelliteEvent;
import com.sfmap.api.navi.model.TrafficStatus;
import com.sfmap.api.navi.view.NaviCarOverlay;
import com.sfmap.api.navi.view.RouteOverLay;
import com.sfmap.tbt.LinkStatus;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.ResourcesUtil;
import com.sfmap.tbt.TmcBarItem;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tbt.util.NaviViewUtil;
import com.sfmap.api.navi.model.CarLocation;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class NaviUIController implements MyNaviListener {
	private String TAG = "NaviUIController";
	private  ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
	public static final int[] ID = {
			ResourcesUtil.back_13,
			ResourcesUtil.action_1,
			ResourcesUtil.action_1,
			ResourcesUtil.action_2,
			ResourcesUtil.action_3,
			ResourcesUtil.action_4,
			ResourcesUtil.action_5,
			ResourcesUtil.action_6,
			ResourcesUtil.action_7,
			ResourcesUtil.action_8,
			ResourcesUtil.action_9,
			ResourcesUtil.action_10,
			ResourcesUtil.action_11,
			ResourcesUtil.action_12,
			ResourcesUtil.action_13,
			ResourcesUtil.action_14,
			ResourcesUtil.action_15,
			ResourcesUtil.action_16,
			ResourcesUtil.action_17,
			ResourcesUtil.action_18,
			ResourcesUtil.action_19,
			ResourcesUtil.action_20,
			ResourcesUtil.action_21,
			ResourcesUtil.action_22,
			ResourcesUtil.action_23,
			ResourcesUtil.action_24,
			ResourcesUtil.action_25

	};

	private static final int[] hud_imgActions = {
			ResourcesUtil.back_13,
			ResourcesUtil.hud_action_1,
			ResourcesUtil.hud_action_1,
			ResourcesUtil.hud_action_2,
			ResourcesUtil.hud_action_3,
			ResourcesUtil.hud_action_4,
			ResourcesUtil.hud_action_5,
			ResourcesUtil.hud_action_6,
			ResourcesUtil.hud_action_7,
			ResourcesUtil.hud_action_8,
			ResourcesUtil.hud_action_9,
			ResourcesUtil.hud_action_10,
			ResourcesUtil.hud_action_11,
			ResourcesUtil.hud_action_12,
			ResourcesUtil.hud_action_13,
			ResourcesUtil.hud_action_14,
			ResourcesUtil.hud_action_15,
			ResourcesUtil.hud_action_16,
			ResourcesUtil.hud_action_17,
			ResourcesUtil.hud_action_18,
			ResourcesUtil.hud_action_19,
			ResourcesUtil.hud_action_20,
			ResourcesUtil.hud_action_21,
			ResourcesUtil.hud_action_22,
			ResourcesUtil.hud_action_23,
			ResourcesUtil.hud_action_24,
			ResourcesUtil.hud_action_25
	};

	public static final int[] speed_img_arr={
			ResourcesUtil.speed_5_right,
			ResourcesUtil.speed_10_right,
			ResourcesUtil.speed_20_right,
			ResourcesUtil.speed_30_right,
			ResourcesUtil.speed_40_right,
			ResourcesUtil.speed_50_right,
			ResourcesUtil.speed_60_right,
			ResourcesUtil.speed_70_right,
			ResourcesUtil.speed_80_right,
			ResourcesUtil.speed_90_right,
			ResourcesUtil.speed_100_right,
			ResourcesUtil.speed_110_right,
			ResourcesUtil.speed_120_right
	};
	public static final int[] speed_img_arr_left={
			ResourcesUtil.speed_5_left,
			ResourcesUtil.speed_10_left,
			ResourcesUtil.speed_20_left,
			ResourcesUtil.speed_30_left,
			ResourcesUtil.speed_40_left,
			ResourcesUtil.speed_50_left,
			ResourcesUtil.speed_60_left,
			ResourcesUtil.speed_70_left,
			ResourcesUtil.speed_80_left,
			ResourcesUtil.speed_90_left,
			ResourcesUtil.speed_100_left,
			ResourcesUtil.speed_110_left,
			ResourcesUtil.speed_120_left
	};
public static final int[] camera_type={
			ResourcesUtil.peccancy_line,
			ResourcesUtil.camera,
			ResourcesUtil.traffic_light,
			ResourcesUtil.peccancy_line,
			ResourcesUtil.bus_line,
			ResourcesUtil.contingency_line
	};

	NaviInfo naviInfo;
	boolean c = false;
	int unitSize = 14; //单位的字体大小
	String valueColor = "#ffffff"; //字符值的颜色
	String unitColor = "#ffffff";	//单位的颜色，如公里、分钟
	String landvalueColor = "#0CCD74"; //横屏字符值的颜色
	String landunitColor = "#0CCD74";	//横屏单位的颜色，如公里、分钟
	ProgressDialog routeCalProgress;	//路径计算进度对话框
	int curNaviMode = Navi.EmulatorNaviMode; //当前导航模式，是模拟导航还是真实导航
	boolean carLock = true;
	boolean autoDrawRoute = true;
	boolean isLaneShow = true;
	boolean isCrossShow = true;
	float carDirection = 0.0F;
	private Context context;
	private RouteOverLay routeOverLay;
	private NaviCarOverlay naviCarOverlay;
	private Navi mNavi = null;
	private MapController mMapController;
	private Context applicatioinContext;
	private NaviView mapNaviView;
	private boolean showTrafficLine = false;
	private boolean showTrafficLayer = true;
	private NaviPath naviPath;
	private NaviLocation naviLocation = null;
	private int y = -1;
	private boolean monitorCameraEnable = true;
	private NaviPath A;
	private NaviInfo naviInfoAgain;
	private Marker cameraMarker;
	private Marker cameraMarkerLeft;
	private Marker cameraOverLay;
	private NaviViewListener mNaviViewListener;
	private Marker cameraOverLayLeft;
	private boolean isflash;
	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();
	private boolean sateEnable = false;
	private List<Marker> cameraList;
	private Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==1){
				mapNaviView.roadSignIV.setVisibility(mapNaviView.roadSignIV.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
				if(mapNaviView.isLandscape())mapNaviView.land_turn_image.setVisibility(mapNaviView.land_turn_image.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
				if(isflash)handler.sendEmptyMessageDelayed(1,500);
			}
			super.handleMessage(msg);
		}
	};

	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			if(mapNaviView == null){
				return;
			}
			switch (event) {
				// 第一次定位
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					break;
				// 卫星状态改变
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					// 获取当前状态
					LocationManager lm= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
					GpsStatus gpsStatus = mNavi.getGpsStatus();
					if(gpsStatus==null){
//						setCarSpeed("---"); //导航库返回车速这里就不用了
						return;
					}
					numSatelliteList.clear();
					int maxSatellites = gpsStatus.getMaxSatellites();
					Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
					int count = 0;//gps数量
					while (iters.hasNext() && count <= maxSatellites) {
						GpsSatellite s = iters.next();
						if(s.getSnr()!=0 && s.usedInFix()){
							numSatelliteList.add(s);
						}
						count++;
					}

					Drawable offGps=ResourcesUtil.getResources().getDrawable(ResourcesUtil.gps_img_red);
					offGps.setBounds(0, 0, offGps.getMinimumWidth(), offGps.getMinimumHeight());
					Drawable onGps=ResourcesUtil.getResources().getDrawable(ResourcesUtil.gps_img_white);
					onGps.setBounds(0, 0, onGps.getMinimumWidth(), onGps.getMinimumHeight());
					EventBus.getDefault().postSticky(new SatelliteEvent(numSatelliteList.size()));
					if(numSatelliteList.size()>3) {
						sateEnable = true;
						naviCarOverlay.setGpsDisable(true);
						mapNaviView.gps_sum_text_port.setText(numSatelliteList.size()+"");
						mapNaviView.gps_sum_text_port.setCompoundDrawables(onGps,null,null,null);
						if(mapNaviView.isLandscape()){
							mapNaviView.gps_sum_text_land.setText(numSatelliteList.size()+"");
							mapNaviView.gps_sum_text_land.setCompoundDrawables(onGps,null,null,null);
						}
						if( mapNaviView.waitGpsPortLy.getVisibility()==View.VISIBLE) mapNaviView.waitGpsPortLy.setVisibility(View.GONE);
						if(mapNaviView.isLandscape())if(mapNaviView.waitGpsLandLy.getVisibility()==View.VISIBLE)mapNaviView.waitGpsLandLy .setVisibility(View.GONE);
					}else{
						sateEnable = false;
						naviCarOverlay.setGpsDisable(false);
						setCarSpeed("---");
						mapNaviView.gps_sum_text_port.setText("");
						mapNaviView.gps_sum_text_port.setCompoundDrawables(offGps,null,null,null);
						if(mapNaviView.isLandscape()){
							mapNaviView.gps_sum_text_land.setText("");
							mapNaviView.gps_sum_text_land.setCompoundDrawables(offGps,null,null,null);
						}
						if(curNaviMode== Navi.GPSNaviMode){
							mapNaviView.waitGpsPortLy.setVisibility(View.VISIBLE);
							if(mapNaviView.isLandscape())
								mapNaviView.waitGpsLandLy .setVisibility(View.VISIBLE);
						}
					}
					break;
				// 定位启动
				case GpsStatus.GPS_EVENT_STARTED:
					break;
				// 定位结束
				case GpsStatus.GPS_EVENT_STOPPED:
					break;
			}
		};
	};
	private int lastID=50;

	public NaviUIController(Context context, MapView mapView, NaviView naviView, NaviViewListener naviViewistener) {
		if (naviView == null) {
			return;
		}

		this.context = context;
		this.applicatioinContext = context.getApplicationContext();
		this.routeOverLay = new RouteOverLay(mapView.getMap(), null, this.applicatioinContext);
		this.naviCarOverlay = new NaviCarOverlay(mapView, naviView,context);
		this.mNavi = Navi.getInstance(this.applicatioinContext);
		this.mNavi.setGpsStatusListener(listener);
		this.mapNaviView = naviView;
		this.mMapController = mapView.getMap();
		this.cameraMarker =mMapController.addMarker(new MarkerOptions());
		this.cameraMarkerLeft =mMapController.addMarker(new MarkerOptions());
		this.cameraOverLay =mMapController.addMarker(new MarkerOptions());
		this.mNaviViewListener =naviViewistener;
		this.cameraOverLayLeft =mMapController.addMarker(new MarkerOptions());
		onNaviInfoUpdate(mNavi.getNaviInfo());
	}


	public void onArriveDestination() {
		if (this.routeOverLay != null)
			this.routeOverLay.removeFromMap();
		if (this.cameraOverLay != null)
			this.cameraOverLay.setVisible(false);
		if(cameraMarker !=null)
			cameraMarker.setVisible(false);

		setCarLock(false,false);
		this.mapNaviView.arrivedEnd();
		this.naviLocation = null;
		this.naviInfo = null;
		if (this.naviCarOverlay != null)
			this.naviCarOverlay.c();
		this.c = false;
	}


	public void onArrivedWayPoint(int wayID) {
	}

	public void onCalculateRouteFailure(int paramInt) {
//		finishProgress();
	}

	public void onCalculateRouteSuccess() {
		if ((this.mMapController == null) || (this.mNavi == null))
			return;
//		finishProgress();
		setNaviPath(mNavi.getNaviPath());
		a();
		this.y = -1;
	}

	void a() {
		if (this.mNavi != null) {
			this.naviPath = this.mNavi.getNaviPath();
			if (this.naviPath == this.A)
				return;
		}
	}
	private void updataTmcView(){
		int startPos = 0, endPos = 0;
		NaviInfo naviInfo= mNavi.getNaviInfo();
		if(null == naviInfo){
			return;
		}
		startPos = mNavi.getRouteLength() - naviInfo.m_RouteRemainDis;
		endPos = naviInfo.m_RouteRemainDis;
		List<NaviTrafficStatus> statuses= mNavi.getTrafficStatuses(startPos,endPos);
		this.mapNaviView.tmcManager.updateRouteTotalLength(mNavi.getRouteLength());
		this.mapNaviView.tmcManager.createTmcBar(statuses);
		this.mapNaviView.tmcManager.setCurorPosition(naviInfo.m_RouteRemainDis);
}


	private List<NaviTrafficStatus>  getPathTrafficStatuses(int stepIndex, int linkIndex, int length){
		NaviPath path= mNavi.getNaviPath();
		if(path==null)return  null;
		List<NaviTrafficStatus> statusesList=new ArrayList<NaviTrafficStatus>();

		for(int i=stepIndex;i<path.getSteps().size();i++){
			for(int j=linkIndex;j<path.getSteps().get(i).getLinks().size();j++){
				int count= mNavi.getLinkStatusCount(i,j);
				int num=0;
				for(int z=0;z<count;z++){
					LinkStatus status= mNavi.getLinkStatus(i,j,z);
					num+=status.m_LinkLen;
					if(num<length&&i==stepIndex&&j==linkIndex)continue;
					TmcBarItem item=new TmcBarItem();
					item.m_Length=status.m_LinkLen;
					item.m_Status=status.m_Status;
					TrafficStatus myStatus=new TrafficStatus(item);
					statusesList.add(myStatus.a);
				}

			}
		}
		return statusesList;
	}


	public void setAutoDrawRoute(boolean autoDrawRoute) {
		this.autoDrawRoute = autoDrawRoute;
	}

	public void setLaneShow(boolean isLaneShow) {
		this.isLaneShow = isLaneShow;
	}
	public void setShowCross(boolean isCrossShow) {
		this.isCrossShow = isCrossShow;
	}

	void setNaviPath(NaviPath paramMapNaviPath) {
		LatLng localObject;
		if(naviLocation != null){
			if(mapNaviView.getNaviType() == Navi.GPSNaviMode){
				this.naviCarOverlay.drawGuideLink(new LatLng(naviLocation.getCoord().getLatitude(),naviLocation.getCoord().getLongitude()),new LatLng(paramMapNaviPath.getEndPoint().getLatitude(),paramMapNaviPath.getEndPoint().getLongitude()),true);
			}
		}
		if (paramMapNaviPath == this.A)
			return;
		if (!(this.autoDrawRoute))
			return;

		if (paramMapNaviPath == null)
			return;

		if (this.routeOverLay != null) {
			mThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					routeOverLay.removeFromMap();
					LogUtil.d(TAG,"routeOverLay.removeFromMap()");
				}
			});
			this.routeOverLay.setMapNaviPath(paramMapNaviPath);
			LogUtil.d(TAG,"routeOverLay.setMapNaviPath()");
			this.routeOverLay.addToMap(true);
			LogUtil.d(TAG,"routeOverLay.addToMap()");
			addAllCamera();
			onTrafficStatusUpdate();
		}


		LatLng localLatLng = null;
		if ((paramMapNaviPath.getStartPoint() != null)
				&& (paramMapNaviPath.getEndPoint() != null))
			localLatLng = new LatLng(paramMapNaviPath.getStartPoint()
					.getLatitude(), paramMapNaviPath.getStartPoint()
					.getLongitude());

		if (localLatLng != null) {
			this.naviCarOverlay.a();

			this.naviCarOverlay.updataCar(this.mMapController, localLatLng, this.carDirection, 51.0F);
			if (paramMapNaviPath.getEndPoint() != null) {
				localObject = new LatLng(paramMapNaviPath.getEndPoint()
						.getLatitude(), paramMapNaviPath.getEndPoint()
						.getLongitude());
				this.naviCarOverlay.a((LatLng) localObject);
			}
		}

		if (this.mapNaviView.remainingDisAtBottomBarTV != null) {
			this.mapNaviView.remainingDisAtBottomBarTV.setText(NaviUtilDecode.a(paramMapNaviPath.getAllLength(), sp2px(this.context,unitSize)));
		}

		if (this.mapNaviView.remainingTimeAtBottomBarTV != null) {
			String localObject2 = NaviUtilDecode.getTimeFormatString(paramMapNaviPath.getAllTime());
			this.mapNaviView.remainingTimeAtBottomBarTV.setText(NaviUtilDecode.a(localObject2, sp2px(this.context,unitSize), this.unitColor));

		}

		this.A = paramMapNaviPath;
	}

	private void addAllCamera(){
		NaviCamera[] cameras= mNavi.getAllCamera();
		if(cameras!=null){
			Bitmap mark= BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.cameraicon);
			BitmapDescriptor markDes=BitmapDescriptorFactory.fromBitmap(mark);
			if(cameraList==null)cameraList=new ArrayList<>();
			if(cameraList.size()>0){
				for(Marker marker:cameraList){
					marker.remove();
				}
			}
			cameraList.clear();
		for(NaviCamera camera:cameras){
			LatLng localLatLng = new LatLng(camera.coords.getLatitude(), camera.coords.getLongitude() );
			Marker marker=this.mapNaviView.getMap().addMarker(new MarkerOptions().icon(markDes).position(localLatLng).anchor(0.5f,0.5f));
			if(this.mapNaviView.getMap().getCameraPosition().zoom<15f)
				marker.setVisible(false);
			else marker.setVisible(true);
			cameraList.add(marker);

		}
}
	}
	/*
    路程全揽
     */
	void b() {
		if (this.routeOverLay != null) {
			LogUtil.d(TAG,"setMapNaviPath"+new Gson().toJson(naviPath));
			this.routeOverLay.setMapNaviPath(this.naviPath);
			this.routeOverLay.zoomToSpan();
		}
	}

	public void onEndEmulatorNavi() {
		if (this.cameraOverLay != null)
			this.cameraOverLay.setVisible(false);
	}

	public void onGetNavigationText(int paramInt, String paramString) {

	}

	public void onInitNaviFailure() {
	}

	public void onInitNaviSuccess() {
	}

	public void c() {
		this.routeOverLay.setEmulateGPSLocationVisible();
	}

	public void onLocationChange(NaviLocation paramMapNaviLocation) {
		if(this.mapNaviView == null){
			return;
		}
		if(!sateEnable){
			return;
		}
		this.naviLocation = paramMapNaviLocation;
		setCarSpeed((int)paramMapNaviLocation.getSpeed()+"");
		if (this.mapNaviView.isArrivedEnd) {
			float f1 = paramMapNaviLocation.getBearing();
			if (this.naviCarOverlay != null)
				this.naviCarOverlay.updataCar(this.mMapController, new LatLng(paramMapNaviLocation.getCoord().getLatitude(), paramMapNaviLocation.getCoord().getLongitude()), f1, 51.0F);
		} else if ((this.naviLocation != null) && (this.naviInfo != null)) {
			if ((Navi.getInstance(this.applicatioinContext).getEngineType() == 1)	&& (Navi.getInstance(this.applicatioinContext).getNaviType() == 1)) {
				LatLng localObject = new LatLng(this.naviInfo.getCoord().getLatitude(), this.naviInfo.getCoord().getLongitude());
				LatLng localLatLng1 = new LatLng(this.naviLocation.getCoord().getLatitude(), this.naviLocation.getCoord().getLongitude());
				if (!(this.naviLocation.isMatchNaviPath())) {
					this.routeOverLay.drawGuideLink(localLatLng1, (LatLng) localObject,	true);
				} else
					this.routeOverLay.drawGuideLink(localLatLng1, (LatLng) localObject,	false);
			}
			Object localObject = this.naviLocation.getCoord();
			float f2 = this.naviLocation.getBearing();
			LatLng localLatLng2 = new LatLng(((NaviLatLng) localObject).getLatitude(),	((NaviLatLng) localObject).getLongitude());

			if ((this.naviCarOverlay != null) && (!(paramMapNaviLocation.isMatchNaviPath()))) {
				this.naviCarOverlay.updataCar(this.mMapController, localLatLng2, f2, this.naviInfo.getCurStepRetainDistance());
				NaviUtilDecode.a("onLocationChange绘制");
			}
		}
	}

	public void setCarSpeed(String speed){
		if(mapNaviView == null){
			return;
		}
		if(mapNaviView.tv_car_speed == null){
			return;
		}
		mapNaviView.tv_car_speed.setText(speed);
	}

	/*
    更新导航信息
     */
	public void onNaviInfoUpdate(NaviInfo naviInfo) {
		if (naviInfo == null)
			return;
		//NaviUtilDecode.a("onNaviInfoUpdate");
		drawArrow(naviInfo);//绘制箭头
//		if (this.naviLocation.isMatchNaviPath()) {
//			routeOverLay.passedWayUpdate(naviInfo);
//		}
		updataCar(naviInfo);//更新车位
		if (this.mapNaviView == null)
			return;
		updateNaviTextInfo(naviInfo);
		updataTmcView();
		showSwitchRouteButton( naviInfo);
		updateCamera(naviInfo);

	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	 void showSwitchRouteButton(NaviInfo naviInfo) {
		if(naviInfo==null||(this.mapNaviView.isShowRoadEnlarge&&!mapNaviView.isLandscape())||!carLock||!(this.mapNaviView.isLayOutVisible)|| mNavi.getNaviType()== Navi.EmulatorNaviMode){
			mapNaviView.switch_img.setVisibility(View.GONE);
			return;
		}
		int id= mNavi.isSwitchParalleRoad(naviInfo.getCurStep(),naviInfo.getCurLink());
		Drawable drawable=null;
		switch(id){

			case 1://切到高架上
//				mapNaviView.switch_img.setVisibility(View.VISIBLE);//暂时无此功能屏蔽
				mapNaviView.switch_img.setVisibility(View.GONE);
				drawable = ResourcesUtil.getResources().getDrawable(ResourcesUtil.high_bridge_above);
				mapNaviView.switch_img.setBackgroundDrawable(drawable);
				break;
			case 2://切到高架下
				drawable = ResourcesUtil.getResources().getDrawable(ResourcesUtil.high_bridge_bottom);
				mapNaviView.switch_img.setBackgroundDrawable(drawable);
//				mapNaviView.switch_img.setVisibility(View.VISIBLE);//暂时无此功能屏蔽
				mapNaviView.switch_img.setVisibility(View.GONE);
				break;
			case 3://切到辅路
				drawable = ResourcesUtil.getResources().getDrawable(ResourcesUtil.road_main);
				mapNaviView.switch_img.setBackgroundDrawable(drawable);
//				mapNaviView.switch_img.setVisibility(View.VISIBLE);//暂时无此功能屏蔽
				mapNaviView.switch_img.setVisibility(View.GONE);
				break;
			case 4://切到主路
				drawable = ResourcesUtil.getResources().getDrawable(ResourcesUtil.road_auxiliary);
				mapNaviView.switch_img.setBackgroundDrawable(drawable);
//				mapNaviView.switch_img.setVisibility(View.VISIBLE);//暂时无此功能屏蔽
				mapNaviView.switch_img.setVisibility(View.GONE);
				break;
			default:
				mapNaviView.switch_img.setVisibility(View.GONE);
				break;

		}

		mapNaviView.updateTmcLayout();
	}

	public void switchRoute(){
		mNavi.switchParalleRoad();//切换主辅路
	}

	public void switchEmulatorStop(){
		if("暂停".equals(this.mapNaviView.port_emulator_stop.getText().toString())){
			this.mapNaviView.port_emulator_stop.setText("开始");
			mNavi.pauseNavi();
		}else {
			this.mapNaviView.port_emulator_stop.setText("暂停");
			mNavi.resumeNavi();
		}
	}

	public void switchEmulatorSpeed(){
		if("低速".equals(this.mapNaviView.port_emulator_speed.getText().toString())){
			mNavi.setEmulatorNaviSpeed(Navi.EmulatorSpeedMid);
			this.mapNaviView.port_emulator_speed.setText("中速");
		}else if("中速".equals(this.mapNaviView.port_emulator_speed.getText().toString())){
			mNavi.setEmulatorNaviSpeed(Navi.EmulatorSpeedHigh);
			this.mapNaviView.port_emulator_speed.setText("高速");
		}else if("高速".equals(this.mapNaviView.port_emulator_speed.getText().toString())){
			mNavi.setEmulatorNaviSpeed(Navi.EmulatorSpeedLow);
			this.mapNaviView.port_emulator_speed.setText("低速");
		}
	}

	private void drawArrow(NaviInfo paramNaviInfo) {
		if (this.y != paramNaviInfo.getCurStep())
			try {
				List localList = this.routeOverLay.getArrowPoints(paramNaviInfo.getCurStep());
				if ((localList != null) && (localList.size() > 0)) {
					this.routeOverLay.drawArrow(localList);
					this.y = paramNaviInfo.getCurStep();
				}
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
	}
	private boolean tag;
	private void updateCamera(NaviInfo paramNaviInfo) {
//		float zoomLevel=1.0f;
//		if ((paramNaviInfo.getCurStep() > 0)&& (!(this.mapNaviView.isAllRoute))) {
//			if ((paramNaviInfo.getCurStepRetainDistance() < 50) && (!(this.c))) {
//				this.naviCarOverlay.zoomInMapLevel(zoomLevel);
//				this.mapNaviView.curMapZoomLenvl += zoomLevel;
//				this.c = true;
//			}
//			if ((paramNaviInfo.getCurStepRetainDistance() > 50) && (this.c)) {
//				if(tag){//暂缓一秒缩小
//				this.naviCarOverlay.zoomOutMapLevel(zoomLevel);
//				this.mapNaviView.curMapZoomLenvl -= zoomLevel;
//				this.c = false;
//				tag=false;
//				}else
//				tag=true;
//			}
//		}
	}
	/*
    更新导航指示文字
     */
	private void updateNaviTextInfo(NaviInfo paramNaviInfo) {
		naviInfoAgain = paramNaviInfo;
		//更新转向指示图标
		updataNaviImgInfo(paramNaviInfo);


		//	更新转向文字
		if (this.mapNaviView.nextRoadDisBelowRoadSignTV != null) {
			int dis=paramNaviInfo.getCurStepRetainDistance();
			String distance=dis>1000?"公里后":"米后";
			String disFormatString= NaviUtilDecode.a(paramNaviInfo.getCurStepRetainDistance());
			if(paramNaviInfo.getCurStepRetainDistance()==0){
				disFormatString="";
				distance="现在";
			}
			this.mapNaviView.nextRoadDisBelowRoadSignTV.setText(disFormatString);
			this.mapNaviView.navi_dis_unit_text.setText(distance);
			if(mapNaviView.isLandscape()) {
				this.mapNaviView.land_dis.setText(disFormatString);
				this.mapNaviView.land_unit.setText(distance);
			}
		}
		//更新街道名称
		if (this.mapNaviView.nextRoadNameAtTopBarTV != null) {
			String nextRoad = paramNaviInfo.getNextRoadName();
			String nextRoadView = nextRoad;
//			String nextRoadView = nextRoad.replaceAll(";"," ");
			if(nextRoad.isEmpty()){
				return;
			}
			String[] deal = nextRoad.split("、");
			if(null != deal && deal.length>1){
				nextRoadView = "";
				int tmpLength = 0;
				for (int i=0;i<deal.length;i++){
					tmpLength += deal[i].length();
					if(i == 0 && deal[0].contains("出口")){
						continue;
					}
					if(tmpLength>11){
						break;
					}
					nextRoadView += deal[i]+" ";
				}
				if(deal[0].contains("出口")){
					this.mapNaviView.port_exit_text.setVisibility(View.VISIBLE);
					this.mapNaviView.port_exit_text.setText(deal[0]);
					if(mapNaviView.isLandscape()){
						this.mapNaviView.land_port_exit_text.setVisibility(View.VISIBLE);
						this.mapNaviView.land_port_exit_text.setText(deal[0]);
					}
				}else {
					this.mapNaviView.port_exit_text.setVisibility(View.GONE);
					this.mapNaviView.land_port_exit_text.setVisibility(View.GONE);
				}
			}else {
				this.mapNaviView.port_exit_text.setVisibility(View.GONE);
				this.mapNaviView.land_port_exit_text.setVisibility(View.GONE);
			}

			this.mapNaviView.nextRoadNameAtTopBarTV.setText(nextRoadView);
			if(mapNaviView.isLandscape()){
				this.mapNaviView.land_next_name.setText(nextRoadView);
			}
		}
		String action="目的地".equals(paramNaviInfo.getNextRoadName())?" 到达":" 进入";
		this.mapNaviView.navi_next_action_text.setText(action);
		if(mapNaviView.isLandscape())this.mapNaviView.land_action.setText(action);
		//服务器获取的剩余时间不准确 距离还有一两百米时间就为0了
		String timeFormatString = NaviUtilDecode.getTimeFormatString(paramNaviInfo.getPathRetainTime());
		Spanned localSpanned1 = Html.fromHtml(NaviUtilDecode.a(timeFormatString, this.valueColor, this.unitColor));
		Spanned localSpanned2 = Html.fromHtml(NaviUtilDecode.a(paramNaviInfo.getPathRetainDistance(), this.valueColor, this.unitColor));
		Log.i(TAG,"debug1--timeFormatString:"+timeFormatString+"localSpanned1:"+localSpanned1.toString()+"localSpanned2:"+localSpanned2.toString());

		//底部导航信息更新
		if (this.mapNaviView.remainingDisAtBottomBarTV != null) {
			this.mapNaviView.remainingDisAtBottomBarTV.setText(NaviUtilDecode.a(paramNaviInfo.getPathRetainDistance(), sp2px(this.context,unitSize)));
			if(mapNaviView.isLandscape())this.mapNaviView.land_restDis.setText(Html.fromHtml(NaviUtilDecode.a(paramNaviInfo.getPathRetainDistance(), this.landvalueColor, this.landunitColor)));
		}

		if (this.mapNaviView.remainingTimeAtBottomBarTV != null){
			//caohai 距离还没到终点时间显示为0分钟bug
			if((!localSpanned2.toString().equals("0米"))&&localSpanned1.toString().equals("0分钟")){
				this.mapNaviView.remainingTimeAtBottomBarTV.setText(NaviUtilDecode.a("00:00:01", sp2px(this.context,unitSize), this.unitColor));
			}else {
				this.mapNaviView.remainingTimeAtBottomBarTV.setText(NaviUtilDecode.a(timeFormatString, sp2px(this.context,unitSize), this.unitColor));
			}
		}
		if(mapNaviView.isLandscape())this.mapNaviView.land_time.setText(Html.fromHtml(NaviUtilDecode.a(timeFormatString, this.landvalueColor, this.landunitColor)));


	}


	private void updataNaviImgInfo(NaviInfo paramNaviInfo){
		if(paramNaviInfo==null)return;
//		if(paramNaviInfo.getIconType() == 0){ //转向图标异常情况传0过来，隐藏之前显示的图标，跳过此次图标显示
//			this.mapNaviView.roadSignIV.setVisibility(View.GONE);
//			this.mapNaviView.land_turn_image.setVisibility(View.GONE);
//			return;
//		}
		int resId=paramNaviInfo.getIconType();
		//更新转向指示图标
		Drawable localObject = ResourcesUtil.getResources().getDrawable(hud_imgActions[paramNaviInfo.getIconType()]);
		Drawable light_img = ResourcesUtil.getResources().getDrawable(ID[resId]);
		//更新放大图进度条
		if(paramNaviInfo.getCurStepRetainDistance()<=300&&this.mapNaviView.isShowRoadEnlarge){
			int index=(300-paramNaviInfo.getCurStepRetainDistance())*100/300;
			this.mapNaviView.cross_progress.setProgress(index);
		}

		if(paramNaviInfo.getPathRetainDistance()<=1||paramNaviInfo.getCurStepRetainDistance()>50||"目的地".equals(paramNaviInfo.getNextRoadName()))
		{
			isflash=false;
			handler.removeMessages(1);
//			localObject = ResourcesUtil.getResources().getDrawable(ID[paramNaviInfo.getIconType()]);
			this.mapNaviView.roadSignIV.setBackgroundDrawable((Drawable) localObject);
			if(mapNaviView.isLandscape()){
				this.mapNaviView.land_turn_image.setBackgroundDrawable((Drawable) localObject);
			}
			if(this.mapNaviView.roadSignIV.getVisibility()!=View.VISIBLE)
				this.mapNaviView.roadSignIV.setVisibility(View.VISIBLE);
			if(this.mapNaviView.land_turn_image.getVisibility()!=View.VISIBLE)
				this.mapNaviView.land_turn_image.setVisibility(View.VISIBLE);
		}else{
			if(!isflash){
				isflash=true;
				this.mapNaviView.roadSignIV.setBackgroundDrawable((Drawable) light_img);
				if(mapNaviView.isLandscape()){
					this.mapNaviView.land_turn_image.setBackgroundDrawable((Drawable) light_img);
				}
				handler.sendEmptyMessageDelayed(1,500);
			}

			if(lastID!=resId&&lastID!=50){
				isflash=true;
				handler.removeMessages(1);
				this.mapNaviView.roadSignIV.setBackgroundDrawable((Drawable) light_img);
				if(mapNaviView.isLandscape()){
					this.mapNaviView.land_turn_image.setBackgroundDrawable((Drawable) light_img);
				}
				handler.sendEmptyMessageDelayed(1,500);
			}
		}
		lastID = resId;
	}

	public void updataNaviInfoAgain(){
		if(naviInfoAgain!=null)updateNaviTextInfo(naviInfoAgain);
	}

	private void updataCar(NaviInfo paramNaviInfo) {
		NaviLatLng carPosition = paramNaviInfo.getCoord();//坐标
		float carDirection = paramNaviInfo.getDirection();//方向
		LatLng carPosLatLng = new LatLng(carPosition.getLatitude(),carPosition.getLongitude(), false);
		this.carDirection = carDirection;
		if (this.naviCarOverlay != null) {
			this.naviCarOverlay.updataCar(this.mMapController, carPosLatLng, carDirection,paramNaviInfo.getCurStepRetainDistance());
			//NaviUtilDecode.a("NaviInfo绘制");
		}
	}
	private int chcekSpeedImg(int  naviInfo){

		switch (naviInfo){
			case 5:
				return speed_img_arr[0];
			case 10:
				return speed_img_arr[1];
			case 20:
				return speed_img_arr[2];
			case 30:
				return speed_img_arr[3];
			case 40:
				return speed_img_arr[4];
			case 50:
				return speed_img_arr[5];
			case 60:
				return speed_img_arr[6];
			case 70:
				return speed_img_arr[7];
			case 80:
				return speed_img_arr[8];
			case 90:
				return speed_img_arr[9];
			case 100:
				return speed_img_arr[10];
			case 110:
				return speed_img_arr[11];
			case 120:
				return speed_img_arr[12];
			default:break;
		}
		return ResourcesUtil.eye_navi_line;
	}
	private int chcekSpeedImgLeft(int  naviInfo){

		switch (naviInfo){
			case 5:
				return speed_img_arr_left[0];
			case 10:
				return speed_img_arr_left[1];
			case 20:
				return speed_img_arr_left[2];
			case 30:
				return speed_img_arr_left[3];
			case 40:
				return speed_img_arr_left[4];
			case 50:
				return speed_img_arr_left[5];
			case 60:
				return speed_img_arr_left[6];
			case 70:
				return speed_img_arr_left[7];
			case 80:
				return speed_img_arr_left[8];
			case 90:
				return speed_img_arr_left[9];
			case 100:
				return speed_img_arr_left[10];
			case 110:
				return speed_img_arr_left[11];
			case 120:
				return speed_img_arr_left[12];
			default:break;
		}
		return ResourcesUtil.eye_navi_line;
	}


	//更新电子眼，限速

	public void onUpdateTrafficFacility(
			NaviTrafficFacilityInfo[] trafficFacilityInfo) {
		if(trafficFacilityInfo==null||trafficFacilityInfo.length==0)return;
		if ((trafficFacilityInfo[0].getBroardcastType() !=-1) && (this.monitorCameraEnable)) {
			int speed_img=ResourcesUtil.eye_navi_line;
			//根据电子眼的类型及实际限速来更新电子眼显示
			if ((trafficFacilityInfo[0].getBroardcastType() == 0)) {
				if(trafficFacilityInfo[0].getLimitSpeed() > 0){
				this.mapNaviView.speedCamera.setText(""+ trafficFacilityInfo[0].getLimitSpeed() );
				speed_img=chcekSpeedImg(trafficFacilityInfo[0].getLimitSpeed());}
				else {
					this.mapNaviView.speedCamera.setText("限速");
					speed_img=ResourcesUtil.speed_text;
				}
				this.mapNaviView.speedCamera.setVisibility(View.VISIBLE);
				this.mapNaviView.monitorCamera.setVisibility(View.GONE);
				this.mapNaviView.eye_speed_dis_text.setVisibility(View.VISIBLE);
				this.mapNaviView.eye_speed_dis_text.setText(trafficFacilityInfo[0].getDistance()+"米");


			} else	if ( (trafficFacilityInfo[0].getBroardcastType() >= 1) && (trafficFacilityInfo[0].getBroardcastType() <= 5)){
				//根据电子眼类型来控制监控电子眼的显示
				this.mapNaviView.monitorCamera.setVisibility(View.VISIBLE);
				Bitmap bitmap= BitmapFactory.decodeResource(ResourcesUtil.getResources(),camera_type[trafficFacilityInfo[0].getBroardcastType()]);
				((ImageView)(this.mapNaviView.monitorCamera.getChildAt(0))).setImageBitmap(bitmap);
				this.mapNaviView.speedCamera.setVisibility(View.GONE);
				this.mapNaviView.eye_speed_dis_text.setVisibility(View.VISIBLE);
				this.mapNaviView.eye_speed_dis_text.setText(trafficFacilityInfo[0].getDistance()+"米");
			}

			 if(trafficFacilityInfo[0].getBroardcastType()==1)
				speed_img=ResourcesUtil.camera_camera;
			else if(trafficFacilityInfo[0].getBroardcastType()==2)
				speed_img= ResourcesUtil.traffic_lights_camera;
			else if(trafficFacilityInfo[0].getBroardcastType()==3)
				speed_img= ResourcesUtil.eye_navi_line;
			else if(trafficFacilityInfo[0].getBroardcastType()==4)
				speed_img=ResourcesUtil.bus_line_camera;
			else if(trafficFacilityInfo[0].getBroardcastType()==5)
				speed_img=ResourcesUtil.contingency_line_camera;
			if (trafficFacilityInfo[0].getLatitude() >0&&trafficFacilityInfo[0].getLongitude()>0) {
				double latitude = trafficFacilityInfo[0].getLatitude();
				double longitude = trafficFacilityInfo[0].getLongitude();
				LatLng localLatLng = new LatLng(latitude, longitude );
				Bitmap mark= BitmapFactory.decodeResource(ResourcesUtil.getResources(),speed_img);
				BitmapDescriptor markDes=BitmapDescriptorFactory.fromBitmap(mark);

				if(this.cameraMarker !=null){
					this.cameraMarker.setIcon(markDes);
					this.cameraMarker.setAnchor(0,1f);
					this.cameraMarker.setPosition(localLatLng);
					this.cameraMarker.setVisible(true);
					this.cameraMarker.setToTop();
				}
			}
		} else {
			if (this.cameraOverLay != null)
				this.cameraOverLay.setVisible(false);
			if (this.cameraMarker != null)
				this.cameraMarker.setVisible(false);
			if (this.mapNaviView.speedCamera != null){
				this.mapNaviView.speedCamera.setVisibility(View.GONE);
			}
			if (this.mapNaviView.monitorCamera != null){
				this.mapNaviView.monitorCamera.setVisibility(View.GONE);
			}
			if(this.mapNaviView.eye_speed_dis_text.getVisibility()==View.VISIBLE){
				this.mapNaviView.eye_speed_dis_text.setVisibility(View.GONE);
				if(cameraMarker !=null) cameraMarker.setVisible(false);
			}
		}

		if(trafficFacilityInfo.length>1&&((trafficFacilityInfo[1].getBroardcastType() !=-1) && (this.monitorCameraEnable))){
			int speed_img_left=ResourcesUtil.eye_navi_line_left;
			//根据电子眼的类型及实际限速来更新电子眼显示
			if ((trafficFacilityInfo[1].getBroardcastType() == 0)){
				if(trafficFacilityInfo[1].getLimitSpeed() > 0)
				speed_img_left=chcekSpeedImgLeft(trafficFacilityInfo[1].getLimitSpeed());
				else
					speed_img_left=ResourcesUtil.speed_text_left;
			}
			if(trafficFacilityInfo[1].getBroardcastType()==3)
				speed_img_left=ResourcesUtil.eye_navi_line_left;
			else if(trafficFacilityInfo[1].getBroardcastType()==2)
				speed_img_left=ResourcesUtil.traffic_lights_camera_left;
			else if(trafficFacilityInfo[1].getBroardcastType()==1)
				speed_img_left=ResourcesUtil.camera_camera_left;
			else if(trafficFacilityInfo[1].getBroardcastType()==4)
				speed_img_left=ResourcesUtil.bus_line_camera_left;
			else if(trafficFacilityInfo[1].getBroardcastType()==5)
				speed_img_left=ResourcesUtil.contingency_line_camera_left;
			if (trafficFacilityInfo[1].getLatitude() >0&&trafficFacilityInfo[1].getLongitude()>0) {
				double latitude = trafficFacilityInfo[1].getLatitude();
				double longitude = trafficFacilityInfo[1].getLongitude();
				LatLng localLatLng = new LatLng(latitude, longitude );
				Bitmap mark= BitmapFactory.decodeResource(ResourcesUtil.getResources(),speed_img_left);
				BitmapDescriptor markDes=BitmapDescriptorFactory.fromBitmap(mark);
				if(this.cameraMarkerLeft!=null){
					this.cameraMarkerLeft.setIcon(markDes);
					this.cameraMarkerLeft.setAnchor(1f,1f);
					this.cameraMarkerLeft.setPosition(localLatLng);
					this.cameraMarkerLeft.setVisible(true);
					this.cameraMarkerLeft.setToTop();
				}
			}
		}else{
			if (this.cameraMarkerLeft != null)
				this.cameraMarkerLeft.setVisible(false);
			if (this.cameraOverLayLeft != null)
				this.cameraOverLayLeft.setVisible(false);
		}

	}

	public void showCross(NaviCross naviCross) {
		if(naviCross==null)return;
		if (!(this.mapNaviView.isLayOutVisible)||!isCrossShow)
			return;
		this.mapNaviView.showCross(naviCross);
		mapNaviView.switch_img.setVisibility(View.GONE);
		if(!this.mapNaviView.isLandscape()){
			if(this.mapNaviView.speedCamera!=null&&this.mapNaviView.speedCamera.getVisibility()==View.VISIBLE)
				this.mapNaviView.speedCamera.setVisibility(View.GONE);
			if(this.mapNaviView.monitorCamera!=null&&this.mapNaviView.monitorCamera.getVisibility()==View.VISIBLE)
				this.mapNaviView.monitorCamera.setVisibility(View.GONE);
//		if(this.mapNaviView.routeTMCOnOffView.getVisibility()==View.VISIBLE)this.mapNaviView.routeTMCOnOffView.setVisibility(View.GONE);
		if(this.mapNaviView.mode_up.getVisibility()==View.VISIBLE)this.mapNaviView.mode_up.setVisibility(View.GONE);
		if(this.mapNaviView.zoomLayout.getVisibility()==View.VISIBLE)this.mapNaviView.zoomLayout.setVisibility(View.GONE);
		}
	}

	public void hideCross() {
		this.mapNaviView.hideCross();
	}

	void setFontColor(String valueColor, String unitColor) {
		this.valueColor = valueColor;
		this.unitColor = unitColor;
		if (this.naviInfo != null) {
			if (this.mapNaviView.remainingDisAtBottomBarTV != null) {
				this.mapNaviView.remainingDisAtBottomBarTV.setText(NaviUtilDecode.a(this.naviInfo.getPathRetainDistance(), sp2px(this.context,unitSize)));
			}
			if(this.mapNaviView.land_restDis!=null)this.mapNaviView.land_restDis.setText(Html.fromHtml(NaviUtilDecode.a(this.naviInfo.getPathRetainDistance(),this.landvalueColor, this.landunitColor)));
			String str = NaviUtilDecode.getTimeFormatString(this.naviInfo.getPathRetainTime());
			if (this.mapNaviView.remainingTimeAtBottomBarTV != null) {
				this.mapNaviView.remainingTimeAtBottomBarTV.setText(NaviUtilDecode.a(str, sp2px(this.context,unitSize), this.unitColor));
			}
			if(this.mapNaviView.land_time!=null)this.mapNaviView.land_time.setText(Html.fromHtml(NaviUtilDecode.a(str,this.landvalueColor, this.landunitColor)));
		}
	}
	private void showDialog() {
		try {
			new AlertDialog.Builder(context).setTitle("提示").setMessage("确定切换到新路径?").setPositiveButton("确定",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface paramDialogInterface,	int paramInt) {
					paramDialogInterface.cancel();
					naviInfo = mNavi.getNaviInfo();
					if (routeOverLay != null) {
						LogUtil.d(TAG,"切换到新路径");
						routeOverLay.setMapNaviPath(mNavi.getNaviPath());
						routeOverLay.addToMap(true);
					}

				}
			}).setNegativeButton("取消",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface paramDialogInterface,int paramInt) {
					paramDialogInterface.cancel();
				}
			}).show();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}
	public void onReCalculateRouteForTrafficJam() {
		if(this.mNaviViewListener ==null||!this.mNaviViewListener.onReRouteForTraffic())showDialog();
		this.y = -1;
	}

	public void onReCalculateRouteForYaw() {
		this.naviInfo = null;
		this.y = -1;
		if (this.mapNaviView.getViewOptions().isReCalculateRouteForYaw()) {
//			showProgress();
		}
	}

	private void showProgress() {
		if (this.routeCalProgress == null)
			this.routeCalProgress = new ProgressDialog(this.context);
		this.routeCalProgress.setProgressStyle(0);
		this.routeCalProgress.setIndeterminate(false);
		this.routeCalProgress.setCancelable(false);
		this.routeCalProgress.setMessage("路线重新规划");
		this.routeCalProgress.show();
	}

	private void finishProgress() {
		if ((this.routeCalProgress != null) && (this.routeCalProgress.isShowing()))
			this.routeCalProgress.dismiss();
	}

	public void onStartNavi(int naviMode) {

		this.curNaviMode = naviMode;
		if(this.A==null)setNaviPath(mNavi.getNaviPath());
		this.mapNaviView.setCarLock(true);
		this.mapNaviView.setConfigurationChanged(this.mapNaviView.isLandscape());
		this.mapNaviView.isArrivedEnd = false;
		setEmulatorNaviUI(naviMode);
	}

	public void onTrafficStatusUpdate() {
		if(this.mapNaviView.tmc_layout.getVisibility()==View.VISIBLE){
			updataTmcView();
			List<NaviTrafficStatus> pathStatuses=getPathTrafficStatuses(0,0,0);
			routeOverLay.colorWayUpdate(pathStatuses);
		}
		a();
		if (this.showTrafficLine)
			setShowTrafficLine(this.showTrafficLine);
	}

	public void setCarLock(boolean paramBoolean,boolean northUpMode) {
		if (this.carLock == paramBoolean&&naviCarOverlay.north_up_mode==northUpMode)
			return;

		this.carLock = paramBoolean;
		if (this.naviCarOverlay != null)
			this.naviCarOverlay.setLockCar(paramBoolean,northUpMode);

		if(paramBoolean){
			showSwitchRouteButton(naviInfoAgain);
		}else{
			mapNaviView.switch_img.setVisibility(View.GONE);
		}
	}

	public void destroy() {
		if (this.routeOverLay != null){
			this.routeOverLay.destroy();
		this.routeOverLay=null;
		}
		if(cameraList!=null&&cameraList.size()>0){
			for(Marker marker:cameraList){
				marker.remove();
			}
		}
		if (this.naviCarOverlay != null){
			this.naviCarOverlay.destroy();
			naviCarOverlay=null;}

		if (this.cameraOverLay != null){
			this.cameraOverLay.destroy();
			cameraOverLay=null;
		}
		if (this.cameraMarker != null){
			this.cameraMarker.destroy();
			cameraMarker=null;
		}
		if(handler!=null){
			handler.removeMessages(1);
			handler=null;
		}
		if(listener!=null){
			mNavi.removeGpsStatusListener(listener);
			listener=null;
		}
		if(NaviViewUtil.config_style!=null){
			NaviViewUtil.config_style=null;
		}
		if(mapNaviView!=null)
			mapNaviView=null;

	}

	public void setStartPointBitmap(Bitmap paramBitmap) {
		if ((this.routeOverLay != null) && (paramBitmap != null))
			this.routeOverLay.setStartPointBitmap(paramBitmap);
	}

	public void setEndPointBitmap(Bitmap paramBitmap) {
		if ((this.routeOverLay != null) && (paramBitmap != null))
			this.routeOverLay.setEndPointBitmap(paramBitmap);
	}

	public void setWayPointBitmap(Bitmap paramBitmap) {
		if ((this.routeOverLay != null) && (paramBitmap != null))
			this.routeOverLay.setWayPointBitmap(paramBitmap);
	}

	public void isMonitorEnable(boolean enable) {
		this.monitorCameraEnable = enable;
	}

	public void onGpsOpenStatus(boolean enabled) {
	}

	public void showLaneInfo(NaviLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
		if (!(this.mapNaviView.isLayOutVisible))
			return;

		if (!(this.isLaneShow))
			return;

		if (this.mapNaviView.isShowRoadEnlarge)
			return;

		if ((laneBackgroundInfo != null) && (laneRecommendedInfo != null)	&& (this.mapNaviView.driveWayView != null)) {
			if (this.mapNaviView.enlargedRoadView.getVisibility() == View.VISIBLE)
				return;
			this.mapNaviView.driveWayView.loadDriveWayBitmap(laneBackgroundInfo, laneRecommendedInfo);
			this.mapNaviView.driveWayView.setVisibility(View.VISIBLE);
		}
	}

	public void hideLaneInfo() {
		if (!(this.isLaneShow))
			return;

		if (this.mapNaviView.driveWayView != null) {
			this.mapNaviView.driveWayView.setVisibility(View.INVISIBLE);
			this.mapNaviView.driveWayView.recycleResource();
		}
	}

	public void onCalculateMultipleRoutesSuccess(int[] routeIds) {
		if ((this.mMapController == null) || (this.mNavi == null))
			return;

		//关闭进度条
//		finishProgress();
		setNaviPath(this.mNavi.getNaviPath());
		a();
		this.y = -1;
	}

	public void carProjectionChange(CarLocation paramCarLocation) {
		float f1 = paramCarLocation.m_CarDir;
		LatLng localLatLng = new LatLng(paramCarLocation.m_Latitude,paramCarLocation.m_Longitude);
		if ((this.naviCarOverlay != null) && (this.naviInfo != null)) {
			this.naviCarOverlay.updataCar(this.mMapController, localLatLng, f1, this.naviInfo.getCurStepRetainDistance());
			NaviUtilDecode.a("carProjectionChange绘制");
		}
	}

	void setShowTrafficLine(boolean paramBoolean) {
		this.showTrafficLine = paramBoolean;
		//暂时不支持
		if (this.routeOverLay != null)
			this.routeOverLay.setTrafficLine(Boolean.valueOf(this.showTrafficLine));
		if(paramBoolean){updataTmcView();}
	}
	void setShowTrafficLayerEnable(boolean paramBoolean) {
		this.showTrafficLayer=paramBoolean;
		if (this.mapNaviView.routeTMCOnOffView != null)
			this.mapNaviView.routeTMCOnOffView.setVisibility(View.GONE);
//			this.mapNaviView.routeTMCOnOffView.setVisibility(paramBoolean?View.VISIBLE:View.GONE);
	}

	void setLineColor(int paramInt) {
		if (this.naviCarOverlay != null)
			this.naviCarOverlay.a(paramInt);
	}

	public void e() {
		if (this.naviCarOverlay != null)
			this.naviCarOverlay.d();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void updateServiceFacility(NaviServiceFacilityInfo[] serviceFacilityInfos) {
		if(serviceFacilityInfos==null||serviceFacilityInfos.length==0||serviceFacilityInfos.length==1&&serviceFacilityInfos[0].getRemainDist()<=0) {
			this.mapNaviView.service_station.setVisibility(View.GONE);
			this.mapNaviView.toll_station.setVisibility(View.GONE);
			return;
		}
		Drawable service_background=ResourcesUtil.getResources().getDrawable(ResourcesUtil.service_station_img);
		Drawable toll_background=ResourcesUtil.getResources().getDrawable(ResourcesUtil.toll_station_img);
		int type1=serviceFacilityInfos[0].getType();
		int dis1=serviceFacilityInfos[0].getRemainDist();
		if(dis1<0)dis1=0;
		String unit1=dis1>1000?"公里":"米";
		this.mapNaviView.service_station.setVisibility(View.VISIBLE);
		if(type1==0){
			//服务区
			this.mapNaviView.service_station.setBackground(service_background);
			this.mapNaviView.service_station_name.setText("服务区");
			this.mapNaviView.service_station_dis.setText(NaviUtilDecode.a(dis1)+unit1);
		}else{
			//收费站
			this.mapNaviView.service_station.setBackground(toll_background);
			this.mapNaviView.service_station_name.setText("收费站");
			this.mapNaviView.service_station_dis.setText(NaviUtilDecode.a(dis1)+unit1);
		}
		if(dis1<=0)this.mapNaviView.service_station.setVisibility(View.GONE);

		if(serviceFacilityInfos.length>1){
			this.mapNaviView.toll_station.setVisibility(View.VISIBLE);
			int type2=serviceFacilityInfos[1].getType();
			int dis2=serviceFacilityInfos[1].getRemainDist();
			if(dis2<0)dis2=0;
			String unit2=dis2>1000?"公里":"米";
			if(type2==0){
				//服务区
				this.mapNaviView.toll_station.setBackground(service_background);
				this.mapNaviView.toll_station_name.setText(NaviUtilDecode.a(dis2)+unit2);
				this.mapNaviView.toll_station_dis.setText(NaviUtilDecode.a(dis2)+unit2);
			}else{
				//收费站
				this.mapNaviView.toll_station.setBackground(toll_background);
				this.mapNaviView.toll_station_name.setText(NaviUtilDecode.a(dis2)+unit2);
				this.mapNaviView.toll_station_dis.setText(NaviUtilDecode.a(dis2)+unit2);

			}
			if(dis1<=0){
				this.mapNaviView.service_station_name.setText(serviceFacilityInfos[1].getName());
//				if(type2==0)
//					this.mapNaviView.toll_station_name.setText("服务区");
//				else
//					this.mapNaviView.toll_station_name.setText("收费站");
				}
			if(dis2<=0)this.mapNaviView.toll_station.setVisibility(View.GONE);
		}else this.mapNaviView.toll_station.setVisibility(View.GONE);

	}

	private void setEmulatorNaviUI(int naviMode){
		if(naviMode== Navi.GPSNaviMode){
			mapNaviView.gps_sum_text_port.setVisibility(View.VISIBLE);
			mapNaviView.gps_sum_text_land.setVisibility(View.VISIBLE);
			return;
		}else {
			this.mapNaviView.port_emulator_speed.setVisibility(View.VISIBLE);
			this.mapNaviView.remainingTimeAtBottomBarTV.setVisibility(View.GONE);
			this.mapNaviView.remainingDisAtBottomBarTV.setVisibility(View.GONE);
			this.mapNaviView.dis_text_down.setVisibility(View.GONE);
			this.mapNaviView.port_emulator_stop.setVisibility(View.VISIBLE);
			this.mapNaviView.naviSettingView.setVisibility(View.GONE);
			this.mapNaviView.ln_car_speed.setVisibility(View.GONE);
		}
	}
	public void setShowRouteTrafficLigth(float zoomlevel){
		if(routeOverLay!=null)
			this.routeOverLay.setShowTrafficLigth(zoomlevel);
	}
	public void setShowRouteCamera(float zoomlevel){
		if(cameraList==null||cameraList.size()==0)return;
		boolean isShow=false;
		if(zoomlevel>15f)isShow=true;
		for(Marker marker:cameraList){
			marker.setVisible(isShow);
		}
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

}