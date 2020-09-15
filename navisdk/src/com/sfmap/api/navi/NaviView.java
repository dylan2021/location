package com.sfmap.api.navi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdate;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.api.navi.view.DirectionView;
import com.sfmap.api.navi.view.DriveWayView;
import com.sfmap.api.navi.view.NaviRoadEnlargeView;
import com.sfmap.api.navi.view.TmcViewManager;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.ResourcesUtil;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tbt.util.NaviViewUtil;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 导航视图类
 */
public class NaviView extends FrameLayout implements View.OnClickListener,
		MapController.OnCameraChangeListener, MapController.OnMapLoadedListener,
		MapController.OnMapTouchListener {
	private final String TAG = NaviView.class.getSimpleName();
	/**
	 * 锁车模式（车头向上模式）
	 */
	public static int CAR_UP_MODE = 0;

	/**
	 * 地图正北向上模式
	 */
	public static int NORTH_UP_MODE = 1;


	private int ENLARGED_ROAD_WIDTH_PIXEL_WHEN_LANDSCAPE = dp2px(300);
	private int ENLARGED_ROAD_HEIGHT_PIXEL_WHEN_PORTRAIT = dp2px(250);
	private double point_center_x = 0.0D;
	private double point_center_y = 0.0D;
	float curMapZoomLenvl = 17.0F;
	int curMapTilt = 60;
	NaviRoadEnlargeView enlargedRoadView;
	View naviLayout = null;
	ImageView roadSignIV;
	TextView nextRoadDisBelowRoadSignTV;
	TextView nextRoadNameAtTopBarTV;
	TextView port_exit_text;
	FrameLayout keepOnNavigationLayout;
	FrameLayout showNaviInfoBottomBarLayout;
	RelativeLayout roadSignLayout;
	LinearLayout topNaviLayout;
	RelativeLayout NaviLayoutport;
	RelativeLayout NaviLayoutland;
	RelativeLayout land_navi_info_include;
	LinearLayout waitGpsPortLy;
	LinearLayout port_next_name_layout;
	LinearLayout roadNameLayout;
	LinearLayout zoomLayout;
	ProgressBar cross_progress;
	LinearLayout upLeftCornerNaviLayout;
	FrameLayout arriveEndBottomBarLayout;
	LinearLayout footerLinearLayout;
	TextView curRoadNameTV;
	TextView navigationGoOnTV;
	DirectionView directionView;
	DirectionView directionHorizontalView;
	ImageView routeTMCOnOffView;
	ImageView mode_up;
	Drawable trafficOpen;
	NaviCross crossBitmap;
	Drawable trafficClose;
	Drawable naviMode_car;
	Drawable naviMode_north;
	TextView remainingDisAtBottomBarTV;
	TextView remainingTimeAtBottomBarTV;
	TextView port_emulator_speed;
	TextView naviBackView;
	ImageView dividingLineLeftOfMenu;
	ImageView dividingLineRightOfBack;
	TextView naviSettingView;
	TextView port_emulator_stop;
	ImageView navi_setting_image_land;
	ImageView navi_exit_image_land;
	Button zoomOutBtnland;
	ImageView zoomOutBtn;
	Button zoomInBtnland;
	ImageView zoomInBtn;
	ConstraintLayout layout_zoom;
	CheckBox routeOverviewView;
	CheckBox routeOverviewViewLand;
	DriveWayView driveWayView;
	RelativeLayout footerLayout;
	RelativeLayout tmc_layout;
	RelativeLayout containerRelativeLayout;
	TextView speedCamera;
	TextView eye_speed_dis_text;
	TextView navi_dis_unit_text;
	TextView toll_station_name;
	TextView toll_station_dis;
	TextView service_station_name;
	TextView service_station_dis;
	TextView navi_next_action_text;
	TextView gps_sum_text_port;
	TextView gps_sum_text_land;
	TextView tv_car_speed;
	LinearLayout ln_car_speed;
	LinearLayout monitorCamera;
	int nWidth = 480;
	int nHeight = 800;
	boolean isLandscape = false;
	int mapHeight = 0;
	int mapWidth = 0;
	boolean isAllRoute = false;
	boolean isArrivedEnd = false;
	boolean isLayOutVisible = true;
	boolean isShowRoadEnlarge = false;
	boolean isDestroy = false;
	private NaviViewOptions mMapNaviViewOptions = null;
	private MapView mapView;
	private Navi mMapNavi;
	private NaviUIController uiController;
	private MapController mMap;
	private RelativeLayout mapviewLayout;
	private MapViewListenerTriggerHandler mMapViewListenerTriggerHandler;
	private long lockMapDelayed = 5000L;
	private boolean isCarLock = true;
	private int roadSignClickCount = 0;
	private NaviViewListener mNaviViewListener;
	private boolean isZoomVisible = true;
	private boolean isCrossDisplayShow = true;
	private boolean isAutoChangeZoom = false;
	private int currentNaviMode = 0;
	private boolean isFrist=true;
	View waitGpsLandLy;
	View land_info_layout;
	TextView land_next_name;
	TextView land_port_exit_text;
	TextView land_unit;
	TextView land_action;
	ImageView land_turn_image;
	TextView land_dis;
	TextView land_restDis;
	TextView land_time;

	View go_on_navi;
	View land_dis_and_time_layout;
	TextView land_dis_text_down;
	TextView dis_text_down;
//	TextView time_text_down;
	View zoom_view_land;
	View footbar_view_land;
	View car_way_tmc_layout;
	ImageView top_line_view;
	View toll_station;
	View service_station;
	View switch_img_relayout;
	TmcViewManager tmcManager;
	 ImageView switch_img;
	boolean northUpMode=false;
//	boolean firstReLoad=true; //解决由重新绘制路线偶发的anr
	private CameraPosition saveCameraPosition;
	private ViewGroup.LayoutParams saviParams;

	public NaviView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public NaviView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NaviView(Context context) {
		super(context);
		init();
	}

	/**
	 * 根据给定的视图参数构造一个导航视图类对象。
	 * @param context - 上下文
	 * @param options - 视图构造参数
	 */
	public NaviView(Context context, NaviViewOptions options) {
		super(context);
		this.mMapNaviViewOptions = options;
		init();
	}

	/**
	 * 返回导航视图设置的NaviViewOptions对象。
	 * @return 显示属性对象。
	 */
	public NaviViewOptions getViewOptions() {
		return this.mMapNaviViewOptions;
	}

	/**
	 * 设置导航视图界面对象（NaviViewOptions）。
	 * @param options - 指定的导航视图界面对象（NaviViewOptions）。
	 */
	public void setViewOptions(NaviViewOptions options) {
		this.mMapNaviViewOptions = options;

		checkViewOptions();
		LMapChangeCamera();
	}

	/**
	 * 获取地图控制类。
	 * @return 地图控制类对象。
	 */
	public MapController getMap() {
		return this.mMap;
	}

	/**
	 * 获得地图当前缩放级别。
	 * @return 地图缩放级别。
	 */
	public float getCurrentZoom(){
		return curMapZoomLenvl;
	}

	/**
	 * 获得地图当前倾斜角度。
	 * @return 地图倾斜角度。
	 */
	public int getCurrentTilt(){
		return curMapTilt;
	}

	/**
	 * 自车位置锁定在x轴的位置，范围：0-1。
	 * @return  自车位置锁定在x轴的位置，从左至右.
	 */
	public double getAnchorX(){
		return point_center_x;
	}

	/**
	 * 自车位置锁定在y轴的位置，范围：0-1。
	 * @return 自车位置锁定在y轴的位置，从上至下。
	 */
	public double getAnchorY(){
		return point_center_y;
	}

	/**
	 * 是否自动去进行缩放变化。
	 * @return 是否自动去进行缩放变化。
	 */
	public boolean isAutoChangeZoom() {
		return this.isAutoChangeZoom;
	}

	/**
	 * 与Activity onCreate同步
	 * @param bundle - bundle
	 */
	public final void onCreate(Bundle bundle) {
		try {
			if(this.mMapNavi.getMapView()==null) {
				this.mapView.onCreate(bundle);
			}
			this.mMap = this.mapView.getMap();

			this.mMap.getUiSettings().setZoomControlsEnabled(false);
			this.mMap.setTrafficEnabled(true);

			LMapChangeCamera();//初始化地图视角，位置

//			checkViewOptions();
			initListener();
			buildScreenInfo();
			setConfigurationChanged(isLandscape());
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 与Activity onResume同步
	 */
	public final void onResume() {
		try {
			if(this.mMapNavi.getMapView()==null)
			this.mapView.onResume();
			buildScreenInfo();

            this.mMap.setOnCameraChangeListener(this);
            this.mMap.setOnMapTouchListener(this);

			this.uiController.a();
			this.uiController.setNaviPath(this.mMapNavi.getNaviPath());

			LMapChangeCamera();//初始化地图视角，位置

		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 与Activity onPause同步
	 */
	public final void onPause() {
		try {
			if(this.mMapNavi.getMapView()==null)
			this.mapView.onPause();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 与Activity onDestroy同步
	 */
	public final void onDestroy() {
		try {
			this.isDestroy = true;
			if(this.uiController != null){
				this.mMapNavi.removeNaviListener(this.uiController);
				this.uiController.onArriveDestination();
			}


			if(this.mMapNavi.getMapView()==null) {
				this.mapView.onDestroy();
			}
			else{
					setMapLayoutParams(	0,0, 0, 0);
					this.mMap.setOnMapTouchListener(null);
			}
			if(this.mNaviViewListener !=null)
				this.mNaviViewListener =null;
			if(this.uiController != null){
				this.uiController.destroy();
				this.uiController=null;
			}

			ensureResourceRecycled();
			removeAllViews();
			this.mMapViewListenerTriggerHandler	.removeCallbacksAndMessages(null);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 与Activity onSaveInstanceState同步。
	 * @param bundle - bundle
	 */
	public final void onSaveInstanceState(Bundle bundle) {
		try {
			this.mapView.onSaveInstanceState(bundle);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 展示全览：成功算路获得路径之后，可将地图缩放到完全展示该路径。
	 */
	public void displayOverview() {
		setCarLock(false, false,false);
		this.uiController.b();
	}

	/**
	 * 获取导航模式。
	 * @return 导航模式 0 - CAR_UP_MODE 1 - NORTH_UP_MODE。
	 */
	public int getNaviMode(){
		return currentNaviMode;
	}

	/**
	 * 返回当前导航的状态，是模拟导航还是真实导航
	 * @return 1 代表驾车真实导航， 2 代表驾车模拟导航
	 */
	public int getNaviType(){
		return mMapNavi.getNaviModel();
	}

	/**
	 * 设置导航模式。
	 * @param naviMode - 导航模式 0 - CAR_UP_MODE 1 - NORTH_UP_MODE。
	 */
	public void setNaviMode(int naviMode){
		if( CAR_UP_MODE == naviMode ){
			setCarLock(true,true,false);
			this.currentNaviMode = CAR_UP_MODE;
		}
		else if( NORTH_UP_MODE  == naviMode){
			setCarLock(true, true,true);
			this.currentNaviMode = NORTH_UP_MODE;
		}
	}

	/**
	 * 设置导航界面按钮的回调监听。
	 * @param naviViewListener  - 导航界面按钮的回调监听。
	 */
	public void setMapNaviViewListener(NaviViewListener naviViewListener) {
		this.mNaviViewListener = naviViewListener;
	}

	/**
	 * 当前是否显示了路口放大图。
	 * @return 是否显示路口放大图
	 */
	public boolean isShowRoadEnlarge() {
		return this.isShowRoadEnlarge;
	}

	/**
	 * 返回当前屏幕状态横屏，竖屏。
	 * @return true，横屏 false,竖屏。
	 */
	public boolean isOrientationLandscape() {
		return this.isLandscape;
	}



	// ------------------------------------------------------------------
	//如下PUBLIC方法不用向外暴露

	/**
	 * 地图加载完成后的回调实现。
	 */
	public void onMapLoaded() {
		try {
			this.mapHeight = this.mapView.getHeight();
			this.mapWidth = this.mapView.getWidth();
			ENLARGED_ROAD_HEIGHT_PIXEL_WHEN_PORTRAIT = this.mapHeight * 4 / 8;
			ENLARGED_ROAD_WIDTH_PIXEL_WHEN_LANDSCAPE = this.mapHeight * 5 / 8;
			if ((this.mapHeight != 0) && (this.mapWidth != 0)) {
				this.mMap.setPointToCenter(	(int) (this.mapWidth * point_center_x),	(int) (this.mapHeight * point_center_y));
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 地图可视范围改变过程中回调的默认实现。
	 * @param position - 可视范围最终改变的CameraPosition 对象。
	 */
	public void onCameraChange(CameraPosition position) {
		try {
			if(null == uiController){
				return;
			}
			uiController.setShowRouteTrafficLigth(position.zoom);
			uiController.setShowRouteCamera(position.zoom);
			if ((position.bearing == 0.0F)|| (position.bearing == 360.0F)) {
				this.directionView.setVisibility(View.INVISIBLE);
				this.directionHorizontalView.setVisibility(View.INVISIBLE);
			} else if (this.mMapNaviViewOptions.isCompassEnabled()) {
				if (this.isLandscape)
					this.directionHorizontalView.setVisibility(View.VISIBLE);
				else {
					this.directionView.setVisibility(View.VISIBLE);
				}
			}

			this.directionView.setRotate(360.0F - position.bearing);
			this.directionHorizontalView.setRotate(360.0F - position.bearing);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 地图可视范围改变完成之后（例如拖动、fling、缩放）回调的默认实现。
	 * @param position - 可视范围最终改变的CameraPosition 对象。
	 */
	public void onCameraChangeFinish(CameraPosition position) {
		setZoomBtnEnable(position);
	}

	/**
	 * 地图触摸事件实现。
	 * @param motionEvent － 事件对象。
	 */
	public void onTouch(MotionEvent motionEvent) {
		try {
//			if (isDestroy)
//				return;
			if (this.enlargedRoadView.getVisibility() == View.VISIBLE)
				hideCross();
			restoreNormalLayout();
			this.mMapViewListenerTriggerHandler.sendEmptyMessage(4);
			this.mMapViewListenerTriggerHandler.removeMessages(0);
			this.mMapViewListenerTriggerHandler.sendEmptyMessageDelayed(0, this.lockMapDelayed);
			//caohai 2018.7.24 修复主界面跳转导航界面-点击地图线路丢失bug （解决办法为延时加载一次路径，临时解决办法，bug原因还未定位）
//			if(firstReLoad){
//				this.mMapViewListenerTriggerHandler.sendEmptyMessageDelayed(10, 300);
//				firstReLoad = false;
//			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 点击事件实现。
	 * @param view － 视图对象
	 */
	public void onClick(View view) {
		try {
			if(view==port_emulator_speed){
				uiController.switchEmulatorSpeed();
			}
			if(view==port_emulator_stop){
				uiController.switchEmulatorStop();
			}
			if(view==switch_img_relayout)
				uiController.switchRoute();

			CameraUpdate localCameraUpdate;
			if (keepOnNavigationLayout == view||view==go_on_navi){//继续导航点击事件 2131427382
				this.mMapViewListenerTriggerHandler.sendEmptyMessage(0);
			}

			if (routeTMCOnOffView == view) {//交通路况开关点击事件 2131427366
				boolean bool = this.mMap.isTrafficEnabled();
				if (bool)
					this.routeTMCOnOffView.setBackgroundDrawable(this.trafficClose);
				else
					this.routeTMCOnOffView.setBackgroundDrawable(this.trafficOpen);

				this.mMap.setTrafficEnabled(!(bool));
//				if (this.uiController != null)
//					this.uiController.e(!(bool));//TODO   路线routeOverlay的路况信息暂时无服务
			}
			if(mode_up==view){
				if(this.getNaviMode()==NORTH_UP_MODE){
					this.mode_up.setBackgroundDrawable(this.naviMode_car);
					setNaviMode(CAR_UP_MODE);
				}else{
					this.northUpMode=true;
					this.mode_up.setBackgroundDrawable(this.naviMode_north);
					setNaviMode(NORTH_UP_MODE);
				}
			}

			if (routeOverviewView== view||view.getId()==routeOverviewViewLand.getId()) {//全览  2131427372
				this.uiController.a();
				if (this.isAllRoute) {
					this.routeOverviewView.setChecked(false);
					this.mMapViewListenerTriggerHandler.sendEmptyMessage(0);
				} else {
					this.mMapViewListenerTriggerHandler.sendEmptyMessage(4);
					this.curMapZoomLenvl = this.mMap.getCameraPosition().zoom;
					this.uiController.b();
					this.isAllRoute = true;
				}

				if (this.mNaviViewListener != null)
					this.mNaviViewListener.onScanViewButtonClick();

			}

			if ( ResourcesUtil.browser_navi_setting == view.getId()||view.getId()==ResourcesUtil.navi_setting_image_land)//2131427385 菜单点击
			{
				this.mMapViewListenerTriggerHandler.sendEmptyMessage(1);
			}

			if (( ResourcesUtil.browser_navi_back == view.getId())||view.getId()==ResourcesUtil.navi_exit_image_land){
				if (this.mNaviViewListener == null||!this.mNaviViewListener.onNaviBackClick()){
					this.mMapViewListenerTriggerHandler.sendEmptyMessage(2);
				}else if(this.mNaviViewListener.onNaviBackClick()){
					this.mMapViewListenerTriggerHandler.sendEmptyMessage(3);
				}
			}

			if (( ResourcesUtil.port_roadsign == view.getId())&& (this.mNaviViewListener != null)) {//2131427356 道路转向
				this.mNaviViewListener.onNaviTurnClick();
			}

			if (this.directionView.equals(view)) {
				localCameraUpdate = CameraUpdateFactory.changeBearing(0.0F);
				this.mMap.animateCamera(localCameraUpdate);
				this.mMapViewListenerTriggerHandler.sendEmptyMessage(4);
				this.mMapViewListenerTriggerHandler.removeMessages(0);
				this.mMapViewListenerTriggerHandler.sendEmptyMessageDelayed(0,	this.lockMapDelayed);
			}
			if (this.directionHorizontalView.equals(view)) {
				localCameraUpdate = CameraUpdateFactory.changeBearing(0.0F);
				this.mMap.animateCamera(localCameraUpdate);
				this.mMapViewListenerTriggerHandler.sendEmptyMessage(4);
				this.mMapViewListenerTriggerHandler.removeMessages(0);
				this.mMapViewListenerTriggerHandler.sendEmptyMessageDelayed(0,	this.lockMapDelayed);
			}
			if (this.zoomInBtn.equals(view)||view.equals(this.zoomInBtnland)) {
				if (this.currentNaviMode == CAR_UP_MODE) {
					setCarLock(false);
					this.curMapZoomLenvl += 1.0F;
				}
				this.mMap.animateCamera(CameraUpdateFactory.zoomIn());
			}
			if (this.zoomOutBtn.equals(view)||view.equals(this.zoomOutBtnland)) {
				if (this.currentNaviMode == CAR_UP_MODE) {
					setCarLock(false);
					this.curMapZoomLenvl -= 1.0F;
				}
				this.mMap.animateCamera(CameraUpdateFactory.zoomOut());
			}

			if (this.roadSignIV.equals(view)) {
				this.roadSignClickCount += 1;
				if (this.roadSignClickCount > 2) {
					this.roadSignClickCount = 0;
					showDebugInfo();
				}
			}

			if(view==port_next_name_layout||view==land_next_name){
				if (mNaviViewListener != null)
					mNaviViewListener.onNextRoadClick();
			}



		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}






	// -------------------------------------------
	//如下是私有方法
	private void init() {
		try {
			if (this.mMapNaviViewOptions == null){
				this.mMapNaviViewOptions = new NaviViewOptions();}

			this.mMapNavi = Navi.getInstance(getContext());
			this.naviLayout =ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_navi_fragment,null);
			addView(this.naviLayout);
			 mapviewLayout= (RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.mapview_relativeLayout);
			MapView naviMap = ((MapView) this.naviLayout.findViewById(ResourcesUtil.map));
			if(this.mMapNavi.getMapView()!=null){
				this.mapView=this.mMapNavi.getMapView();
				naviMap.setVisibility(View.GONE);
			}else{
				naviMap.setVisibility(View.VISIBLE);
				this.mapView=naviMap;
			}
			NaviLayoutport = (RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.top_navi_info);
			NaviLayoutport.addView(ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_navi_cross_layout,null));
			NaviLayoutland = (RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.top_navi_cross_land);
			NaviLayoutland.addView(ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_navi_cross_layout,null));
			land_navi_info_include = (RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.land_navi_info_include);
			land_navi_info_include.addView(ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_land_navi_info_layout,null));
			NaviUtilDecode.a(getContext());
			this.isLandscape = isLandscape();
			findView();
			if (this.uiController == null) {
				this.uiController = new NaviUIController(getContext(), this.mapView, this, mNaviViewListener);
			}

			this.mMapViewListenerTriggerHandler = new MapViewListenerTriggerHandler(this);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	boolean isLandscape() {
		return ((((Activity) getContext()).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) || (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE));
	}

	private void buildScreenInfo() {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		WindowManager localWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		this.nWidth = localDisplayMetrics.widthPixels;
		this.nHeight = localDisplayMetrics.heightPixels;
	}

	private void initListener() {
		try {
			this.mMap.setOnMapLoadedListener(this);
			this.mMap.setOnCameraChangeListener(this);
			this.mMap.setOnMapTouchListener(this);
			this.mMapNavi.addNaviListener(this.uiController);
			this.keepOnNavigationLayout.setOnClickListener(this);
			this.go_on_navi.setOnClickListener(this);
			this.port_emulator_speed.setOnClickListener(this);
			this.routeTMCOnOffView.setOnClickListener(this);
			this.mode_up.setOnClickListener(this);
			this.naviSettingView.setOnClickListener(this);
			this.port_emulator_stop.setOnClickListener(this);
			this.navi_setting_image_land.setOnClickListener(this);
			this.naviBackView.setOnClickListener(this);
			this.navi_exit_image_land.setOnClickListener(this);
			this.directionView.setOnClickListener(this);
			this.roadSignIV.setOnClickListener(this);
			this.directionHorizontalView.setOnClickListener(this);
			this.routeOverviewView.setOnClickListener(this);
			this.routeOverviewViewLand.setOnClickListener(this);
			this.zoomOutBtnland.setOnClickListener(this);
			this.zoomOutBtn.setOnClickListener(this);
			this.curRoadNameTV.setOnClickListener(this);
			this.zoomInBtn.setOnClickListener(this);
			this.zoomInBtnland.setOnClickListener(this);
			this.port_next_name_layout.setOnClickListener(this);
			this.land_next_name.setOnClickListener(this);
			this.switch_img_relayout.setOnClickListener(this);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	private void findView() {
		try {

			if(isLandscape()){
				NaviLayoutport.setVisibility(View.GONE);
				initTitleView(NaviLayoutland);
			}else {
				NaviLayoutland.setVisibility(View.GONE);
				NaviLayoutport.setVisibility(View.VISIBLE);
				initTitleView(NaviLayoutport);
			}


			//横屏Gps
			this.waitGpsLandLy = this.land_navi_info_include.findViewById(ResourcesUtil.waitGpsLandLy);
			//横屏正常信息根布局
			this.land_info_layout = this.land_navi_info_include.findViewById(ResourcesUtil.land_info_layout);
			//横屏下个路名
			this.land_next_name = (TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_port_nextRoadNameText);
			this.land_port_exit_text = (TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_port_exit_text);
			//横屏距离单位
			this.land_unit = (TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_navi_dis_unit_text);
			//横屏进入或到达动作
			this.land_action = (TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_navi_next_action_text);
			//横屏指引图片
			this.land_turn_image = (ImageView) this.land_navi_info_include.findViewById(ResourcesUtil.land_port_roadsign);
			//横屏下段路距离
			this.land_dis = (TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_port_nextRoadSignDisText);
			//横屏底部距离
			this.land_restDis = ((TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_port_restDistance));
			//横屏底部时间
			this.land_time = ((TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_port_distanceTimeText));
			//横屏继续导航
			this.go_on_navi = this.land_navi_info_include.findViewById(ResourcesUtil.go_on_navi);
			//横屏距离和时间布局
			this.land_dis_and_time_layout = this.land_navi_info_include.findViewById(ResourcesUtil.land_dis_and_time_layout);
			this.land_dis_text_down = (TextView) this.land_navi_info_include.findViewById(ResourcesUtil.land_dis_text_down);

			//横屏缩放布局
			this.zoom_view_land = this.naviLayout.findViewById(ResourcesUtil.zoom_view_land);
			this.footbar_view_land = this.naviLayout.findViewById(ResourcesUtil.footbar_view_land);
			this.car_way_tmc_layout = this.naviLayout.findViewById(ResourcesUtil.car_way_tmc_layout);

			this.gps_sum_text_land = ((TextView) naviLayout.findViewById(ResourcesUtil.gps_sum_text_land));
			this.tv_car_speed = ((TextView) naviLayout.findViewById(ResourcesUtil.tv_car_speed));
			this.ln_car_speed = ((LinearLayout) naviLayout.findViewById(ResourcesUtil.ln_car_speed));

			this.routeOverviewViewLand = ((CheckBox) this.naviLayout.findViewById(ResourcesUtil.btn_preview_land));
			this.zoomOutBtnland = ((Button) this.naviLayout	.findViewById(ResourcesUtil.zoom_out_land));
			this.zoomInBtnland = ((Button) this.naviLayout.findViewById(ResourcesUtil.zoom_in_land));
			this.layout_zoom = ((ConstraintLayout) this.naviLayout.findViewById(ResourcesUtil.layout_zoom));
			this.eye_speed_dis_text = ((TextView) this.naviLayout.findViewById(ResourcesUtil.eye_speed_dis_text));


			this.toll_station = this.naviLayout.findViewById(ResourcesUtil.toll_station);
			this.toll_station_name = ((TextView) this.naviLayout.findViewById(ResourcesUtil.toll_station_name));
			this.toll_station_dis = ((TextView) this.naviLayout.findViewById(ResourcesUtil.toll_station_dis));
			this.service_station =this.naviLayout.findViewById(ResourcesUtil.service_station);
			this.service_station_name = ((TextView) this.naviLayout.findViewById(ResourcesUtil.service_station_name));
			this.service_station_dis = ((TextView) this.naviLayout.findViewById(ResourcesUtil.service_station_dis));


			this.directionView = ((DirectionView) this.naviLayout.findViewById(ResourcesUtil.directionView));//指南针
			this.directionHorizontalView = ((DirectionView) this.naviLayout.findViewById(ResourcesUtil.directionView_horizontal));//横向指南针
			this.containerRelativeLayout = ((RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.navi_container));//相对布局
			this.driveWayView = ((DriveWayView) this.naviLayout.findViewById(ResourcesUtil.driveWayViewInNaviView));//车道
			this.driveWayView.setMapNaviView(this);
			this.speedCamera = ((TextView) this.naviLayout.findViewById(ResourcesUtil.port_limitSpeedText));//限速

			this.monitorCamera = ((LinearLayout) this.naviLayout.findViewById(ResourcesUtil.port_electronic_eye_img));//电子眼
			this.remainingDisAtBottomBarTV = ((TextView) this.naviLayout.findViewById(ResourcesUtil.port_restDistance));//底部距离
			this.remainingTimeAtBottomBarTV = ((TextView) this.naviLayout.findViewById(ResourcesUtil.port_distanceTimeText));//底部时间
			this.port_emulator_speed = ((TextView) this.naviLayout.findViewById(ResourcesUtil.port_emulator_speed));//底部时间
			this.keepOnNavigationLayout = ((FrameLayout) this.naviLayout.findViewById(ResourcesUtil.port_reset_navi_car_layout));//继续导航
			this.showNaviInfoBottomBarLayout = ((FrameLayout) this.naviLayout.findViewById(ResourcesUtil.port_show_naving_info));//底部显示导航信息
			this.routeTMCOnOffView = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.route_tmc));//路况开关
			this.routeTMCOnOffView.setVisibility(View.GONE);
			this.mode_up = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.mode_up));//车头朝北切换
			this.dividingLineLeftOfMenu = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.navigation_down_line));//菜单左边的线
			this.dividingLineRightOfBack = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.navi_back_line));//返回右边的线
			this.naviSettingView = ((TextView) this.naviLayout.findViewById(ResourcesUtil.browser_navi_setting));//菜单设置
			this.port_emulator_stop = ((TextView) this.naviLayout.findViewById(ResourcesUtil.port_emulator_stop));//菜单设置
			this.navi_setting_image_land = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.navi_setting_image_land));//菜单设置
			this.navi_exit_image_land = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.navi_exit_image_land));//关闭导航
			this.naviBackView = ((TextView) this.naviLayout.findViewById(ResourcesUtil.browser_navi_back));//关闭导航
			this.arriveEndBottomBarLayout = ((FrameLayout) this.naviLayout.findViewById(ResourcesUtil.port_cur_road_name_view));//底部到目的地
			this.footerLinearLayout = ((LinearLayout) this.naviLayout.findViewById(ResourcesUtil.navi_widget_footer_linearlayout));//底部layout
			this.curRoadNameTV = ((TextView) this.naviLayout.findViewById(ResourcesUtil.port_curRoadName));//底部到目的地
			this.navigationGoOnTV = ((TextView) this.naviLayout.findViewById(ResourcesUtil.navigation_go_on));//继续导航
//			this.time_text_down = ((TextView) this.naviLayout.findViewById(ResourcesUtil.time_text_down));
			this.dis_text_down = ((TextView) this.naviLayout.findViewById(ResourcesUtil.dis_text_down));
			this.zoomOutBtn = ((ImageView) this.naviLayout	.findViewById(ResourcesUtil.btn_zoom_out));//缩小
			this.zoomInBtn = ((ImageView) this.naviLayout.findViewById(ResourcesUtil.btn_zoom_in));//放大
			this.routeOverviewView = ((CheckBox) this.naviLayout.findViewById(ResourcesUtil.btn_preview));//全览
			this.trafficOpen = ResourcesUtil.getResources().getDrawable(ResourcesUtil.map_traffic_hl);//2130837583
			this.trafficClose = ResourcesUtil.getResources().getDrawable(ResourcesUtil.map_traffic);//2130837582
			this.naviMode_car = ResourcesUtil.getResources().getDrawable(ResourcesUtil.navi_up);
			this.naviMode_north = ResourcesUtil.getResources().getDrawable(ResourcesUtil.navi_north);
			this.footerLayout = ((RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.relativeLayout1));//底部栏的父布局
			this.zoomLayout = ((LinearLayout) naviLayout.findViewById(ResourcesUtil.zoom_view));//缩放父布局
			this.tmc_layout = ((RelativeLayout) this.naviLayout.findViewById(ResourcesUtil.tmc_layout));
			this.tmcManager=new TmcViewManager();
			this.tmcManager.initView(getContext(),tmc_layout);
            footerLayout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
			this.switch_img = (ImageView) this.naviLayout.findViewById(ResourcesUtil.switch_img);
			this.switch_img.setVisibility(View.GONE);
			this.switch_img_relayout = this.naviLayout.findViewById(ResourcesUtil.switch_img_relayout);

		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}


	private void initTitleView(View view){

		this.cross_progress = ((ProgressBar) view.findViewById(ResourcesUtil.cross_progress));
		this.topNaviLayout = ((LinearLayout) view.findViewById(ResourcesUtil.top_navi_info_layout));//顶部信息
		this.waitGpsPortLy = ((LinearLayout) view.findViewById(ResourcesUtil.waitGpsPortLy));//未gps定位布局
		this.enlargedRoadView = ((NaviRoadEnlargeView) view.findViewById(ResourcesUtil.enlarge_road_layout));//放大图
		this.port_next_name_layout = ((LinearLayout) view.findViewById(ResourcesUtil.port_next_name_layout));//竖屏时下个路名根布局
		this.navi_dis_unit_text = ((TextView) view.findViewById(ResourcesUtil.navi_dis_unit_text));//距离单位
		this.navi_next_action_text = ((TextView) view.findViewById(ResourcesUtil.navi_next_action_text));//下个路名前动作
		this.gps_sum_text_port = ((TextView) view.findViewById(ResourcesUtil.gps_sum_text_port));//下个路名前动作
		this.top_line_view = (ImageView) view.findViewById(ResourcesUtil.top_line_view);
		this.roadSignIV = ((ImageView) view.findViewById(ResourcesUtil.port_roadsign));//转向标志
		this.nextRoadNameAtTopBarTV = ((TextView) view.findViewById(ResourcesUtil.port_nextRoadNameText));//顶部路名
		this.port_exit_text = ((TextView) view.findViewById(ResourcesUtil.port_exit_text));//顶部路名
		this.nextRoadDisBelowRoadSignTV = ((TextView) view.findViewById(ResourcesUtil.port_nextRoadSignDisText));//下段路距离
		this.upLeftCornerNaviLayout = ((LinearLayout) view.findViewById(ResourcesUtil.port_leftwidget));//左上layout
		this.roadSignLayout = ((RelativeLayout) view.findViewById(ResourcesUtil.roadsign_layout));//左上父布局
		this.roadNameLayout = ((LinearLayout) view.findViewById(ResourcesUtil.roadname_layout));//顶部父布局

	}
	protected void onConfigurationChanged(Configuration paramConfiguration) {
		super.onConfigurationChanged(paramConfiguration);
		try {
			buildScreenInfo();
			this.isLandscape = isLandscape();
			setConfigurationChanged(this.isLandscape);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	public  void setConfigurationChanged(boolean paramBoolean) {

		if(paramBoolean){
			if(isLayOutVisible)land_navi_info_include.setVisibility(View.VISIBLE);
			NaviLayoutport.setVisibility(View.GONE);
			initTitleView(NaviLayoutland);
		}else {
			land_navi_info_include.setVisibility(View.GONE);
			NaviLayoutland.setVisibility(View.GONE);
			if(isLayOutVisible)NaviLayoutport.setVisibility(View.VISIBLE);
			initTitleView(NaviLayoutport);
		}
		restoreNormalLayout();
		checkViewOptions();

		this.uiController.updataNaviInfoAgain();
		if (crossBitmap!=null) {
			setEnlargedRoadLayout();
		}
		if(!isFrist)setCarLock(this.isCarLock);else isFrist=false;
		if(tmc_layout.getVisibility()==View.VISIBLE)updateTmcLayout();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	 void updateTmcLayout() {

		if (tmc_layout == null) {
			return;
		}
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tmc_layout.getLayoutParams();
		if (isLandscape()) {
			tmc_layout.setScaleX(1.1f);
			tmc_layout.setScaleY(1.1f);
				lp.bottomMargin = dp2px(120);
		} else {
			if(isShowRoadEnlarge&&crossBitmap!=null) {
				tmc_layout.setScaleX(0.9f);
				tmc_layout.setScaleY(0.9f);
				lp.bottomMargin = dp2px(60);
			}else {
				tmc_layout.setScaleX(1.3f);
				tmc_layout.setScaleY(1.3f);
					lp.bottomMargin = dp2px(130);
			}
		}
		tmc_layout.setLayoutParams(lp);
	}
	/*
    初始始化布局设置
     */
	void checkViewOptions() {
		try {
			point_center_x = this.mMapNaviViewOptions.getMapCenterX();
			point_center_y = this.mMapNaviViewOptions.getMapCenterY();

			if(this.mMapNaviViewOptions.isLayoutVisible()){
				setLayOutVisible(true);
				if(this.mMapNaviViewOptions.isZoomLayoutVisible()){
					if(isLandscape())
					this.zoom_view_land.setVisibility(View.VISIBLE);
					else
						setZoomVisible(true);
				}else{
					setZoomVisible(false);
					this.zoom_view_land.setVisibility(View.GONE);
				}
				if(isLandscape()){
					this.footerLayout.setVisibility(View.GONE);
					this.footbar_view_land.setVisibility(View.VISIBLE);

				}else{
					this.footerLayout.setVisibility(View.VISIBLE);
					this.footbar_view_land.setVisibility(View.GONE);
				}
			}else{
				setLayOutVisible(false);
				this.NaviLayoutland.setVisibility(View.GONE);
				this.land_navi_info_include.setVisibility(View.GONE);
			}


			this.lockMapDelayed = this.mMapNaviViewOptions.getLockMapDelayed();
			this.isAutoChangeZoom = this.mMapNaviViewOptions.isAutoChangeZoom();
			this.isCrossDisplayShow = this.mMapNaviViewOptions.isCrossDisplayShow();


			if (!(this.mMapNaviViewOptions.isRouteListButtonShow())) {
				this.routeOverviewView.setVisibility(View.GONE);
				if(this.isLandscape)routeOverviewViewLand.setVisibility(View.GONE);
			} else if (!(this.isCarLock)) {
				this.routeOverviewView.setVisibility(View.VISIBLE);
				if(this.isLandscape)routeOverviewViewLand.setVisibility(View.VISIBLE);
			}


			if(this.mMapNaviViewOptions.isShowNaviUpMode()){
				this.mode_up.setVisibility(View.VISIBLE);
			}else
			this.mode_up.setVisibility(View.GONE);
			if (this.mMapNaviViewOptions.isNaviNight())
				this.mMap.setMapType(MapController.MAP_TYPE_NIGHT);
			else {
				this.mMap.setMapType(MapController.MAP_TYPE_NAVI);
			}
//				this.directionHorizontalView.setVisibility(this.mMapNaviViewOptions.isCompassEnabled()?View.VISIBLE:View.GONE);
						if (!(this.mMapNaviViewOptions.isCompassEnabled())) {
				this.directionView.setVisibility(View.INVISIBLE);
				this.directionHorizontalView.setVisibility(View.INVISIBLE);
			} else if (this.isLandscape) {
				this.directionView.setVisibility(View.INVISIBLE);
				this.directionHorizontalView.setVisibility(View.VISIBLE);
			} else {
				this.directionView.setVisibility(View.VISIBLE);
				this.directionHorizontalView.setVisibility(View.INVISIBLE);
			}
			this.uiController.setShowTrafficLine(this.mMapNaviViewOptions.isTrafficLine());
			this.uiController.setShowTrafficLayerEnable(this.mMapNaviViewOptions.isTrafficLayerEnabled());
			this.uiController.setLineColor(this.mMapNaviViewOptions.getLeaderLineColor());
			this.uiController.setAutoDrawRoute(this.mMapNaviViewOptions.isAutoDrawRoute());
			this.uiController.setLaneShow(this.mMapNaviViewOptions.isLaneInfoShow());
			this.uiController.setShowCross(this.mMapNaviViewOptions.isCrossDisplayShow());
			Bitmap localBitmap1 = this.mMapNaviViewOptions.getStartMarker();
			Bitmap localBitmap2 = this.mMapNaviViewOptions.getEndMarker();
			Bitmap localBitmap3 = this.mMapNaviViewOptions.getWayMarker();
			Bitmap localBitmap4 = this.mMapNaviViewOptions.getMonitorMarker();

			this.uiController.setStartPointBitmap(localBitmap1);
			this.uiController.setEndPointBitmap(localBitmap2);
			this.uiController.setWayPointBitmap(localBitmap3);
//			this.uiController.setCameraBitmap(localBitmap4);
			this.uiController.isMonitorEnable(this.mMapNaviViewOptions.isMonitorCameraEnabled());

			this.mMapNavi.getNaviSetting().enableScreenAlwaysBright(this.mMapNaviViewOptions.isScreenAlwaysBright());
			//暂时不支持
			//this.mMapNavi.getNaviSetting().enableTrafficInfoUpdate(this.mMapNaviViewOptions.isTrafficInfoUpdateEnabled());
			this.mMapNavi.getNaviSetting().enableTrafficInfoUpdate(false);

			this.mMapNavi.getNaviSetting().enabelCameraInfoUpdate(this.mMapNaviViewOptions.isCameraInfoUpdateEnabled());
			this.mMapNavi.getNaviSetting().enableMonitorCamera(this.mMapNaviViewOptions.isMonitorCameraEnabled());
//			boolean bool = this.mMapNaviViewOptions.isSettingMenuEnabled();
			this.mMapNavi.setReCalculateRouteForYaw(this.mMapNaviViewOptions.isReCalculateRouteForYaw());
			if (this.mMap.isTrafficEnabled()){
				this.routeTMCOnOffView.setBackgroundDrawable(this.trafficOpen);
			}
			//暂时不支持
			//this.mMapNavi.setReCalculateRouteForTrafficJam(this.mMapNaviViewOptions.isReCalculateRouteForTrafficJam());
//			if (bool) {
//				this.dividingLineLeftOfMenu.setVisibility(View.VISIBLE);
//				this.naviSettingView.setVisibility(View.VISIBLE);
//			} else {
//				this.dividingLineLeftOfMenu.setVisibility(View.GONE);
//				this.naviSettingView.setVisibility(View.GONE);
//			}
//			String bottom_text_color="#000000";
			String bottom_text_color="#e1022b";
			int viewTopic = this.mMapNaviViewOptions.getNaviViewTopic();//主题，黰认0
//			this.naviBackView.setImageDrawable(ResourcesUtil.getResources().getDrawable(ResourcesUtil.navigation_close_black));//返回图标，2130837644
			this.curRoadNameTV.setTextColor(Color.parseColor(bottom_text_color));//度部目的地字体颜色
			this.navigationGoOnTV.setTextColor(Color.parseColor(bottom_text_color));//继续导航字体颜色
			this.uiController.setFontColor(bottom_text_color, bottom_text_color);//底部距离，时间字体颜色
			int default_color=Color.parseColor("#303441");
			int blue_color=Color.parseColor("#3A77D1");
			int pink_color=Color.parseColor("#EC3A8A");

			switch (viewTopic) {
				case NaviViewOptions.DEFAULT_COLOR_TOPIC:
					this.topNaviLayout.setBackgroundColor(default_color);
					this.land_info_layout.setBackgroundColor(default_color);
					this.waitGpsPortLy.setBackgroundColor(default_color);
					this.waitGpsLandLy.setBackgroundColor(default_color);
					this.nextRoadDisBelowRoadSignTV.setTextColor(-1);//标识下部字体颜色
					this.nextRoadNameAtTopBarTV.setTextColor(-1);//顶部街道字体颜色
					this.navi_dis_unit_text.setTextColor(-1);
					break;
				case NaviViewOptions.BLUE_COLOR_TOPIC:
					this.topNaviLayout.setBackgroundColor(blue_color);
					this.land_info_layout.setBackgroundColor(blue_color);
					this.waitGpsPortLy.setBackgroundColor(blue_color);
					this.waitGpsLandLy.setBackgroundColor(blue_color);
					this.nextRoadDisBelowRoadSignTV.setTextColor(-1);
					this.nextRoadNameAtTopBarTV.setTextColor(-1);
					this.navi_dis_unit_text.setTextColor(-1);
					break;
				case NaviViewOptions.PINK_COLOR_TOPIC:
					this.topNaviLayout.setBackgroundColor(pink_color);
					this.land_info_layout.setBackgroundColor(pink_color);
					this.waitGpsPortLy.setBackgroundColor(pink_color);
					this.waitGpsLandLy.setBackgroundColor(pink_color);
					this.nextRoadDisBelowRoadSignTV.setTextColor(-1);
					this.nextRoadNameAtTopBarTV.setTextColor(-1);
					this.navi_dis_unit_text.setTextColor(-1);
					break;
//				case NaviViewOptions.WHITE_COLOR_TOPIC:
//					this.topNaviLayout.setBackgroundColor(Color.parseColor("#F8F8F8"));
//					this.land_info_layout.setBackgroundColor(Color.parseColor("#F8F8F8"));
//					this.waitGpsPortLy.setBackgroundColor(Color.parseColor("#F8F8F8"));
//					this.waitGpsLandLy.setBackgroundColor(Color.parseColor("#F8F8F8"));
//					this.nextRoadDisBelowRoadSignTV.setTextColor(-16777216);
//					this.nextRoadNameAtTopBarTV.setTextColor(-16777216);
//					this.navi_dis_unit_text.setTextColor(-16777216);
//					break;
			}

			setCustomStyle();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}



	int dp2px(int paramInt) {
		Context localContext = getContext();
		if (paramInt == 0)
			return 0;

		if (localContext == null)
			return paramInt;
		try {
			float f = TypedValue.applyDimension(1, paramInt, localContext.getResources().getDisplayMetrics());

			return (int) f;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return paramInt;
	}

	private int px2dp(float paramFloat) {
		Context localContext = getContext();
		if (paramFloat == 0.0F)
			return 0;

		if (localContext == null)
			return (int) paramFloat;

		float f = localContext.getResources().getDisplayMetrics().density;
		return (int) (paramFloat / f + 0.5F);
	}



	private void ensureResourceRecycled() {
		if (this.enlargedRoadView != null) {
			this.enlargedRoadView.setVisibility(View.GONE);
			this.enlargedRoadView.recycleResource();
			this.enlargedRoadView=null;
		}

		if (this.driveWayView != null) {
			this.driveWayView.setVisibility(View.GONE);
			this.driveWayView.recycleResource();
			this.driveWayView=null;
		}
				if (this.directionHorizontalView != null) {
			this.directionHorizontalView.setVisibility(View.GONE);
			this.directionHorizontalView.recycleResource();
					directionHorizontalView=null;
		}

		if (this.directionView != null) {
			this.directionView.setVisibility(View.INVISIBLE);
			this.directionView.recycleResource();
			directionView=null;
		}

	}



	private void LMapChangeCamera() {
		this.curMapZoomLenvl = this.mMapNaviViewOptions.getZoom();
		this.curMapTilt = this.mMapNaviViewOptions.getTilt();
		LogUtil.d(TAG,"LMapChangeCamera curMapZoomLenvl"+curMapZoomLenvl);
		CameraPosition localCameraPosition = new CameraPosition(this.mMap.getCameraPosition().target, this.curMapZoomLenvl,	this.curMapTilt, 0.0F);
		CameraUpdate localCameraUpdate = CameraUpdateFactory.newCameraPosition(localCameraPosition);
		this.mMap.moveCamera(localCameraUpdate);
	}



	void showCross(NaviCross paramMapNaviCross) {
		if (!(this.isCrossDisplayShow))
			return;
		crossBitmap=paramMapNaviCross;
		this.isShowRoadEnlarge = true;
		this.cross_progress.setProgress(0);
		setEnlargedRoadLayout();
		requestLayout();
	}

	void hideCross() {
		if (!(this.isCrossDisplayShow))
			return;

		this.isShowRoadEnlarge = false;
		crossBitmap=null;
		restoreNormalLayout();

		this.enlargedRoadView.setVisibility(View.GONE);
		this.enlargedRoadView.recycleResource();
		requestLayout();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setEnlargedRoadLayout() {
		RelativeLayout.LayoutParams localLayoutParams;
		if (this.isLandscape) {

			this.NaviLayoutland.setVisibility(View.VISIBLE);
			this.enlargedRoadView.setVisibility(View.VISIBLE);
			localLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -1);
			this.enlargedRoadView.setLayoutParams(localLayoutParams);
			if(this.mMapNavi.getMapView()!=null)
				setMapLayoutParams(300,0, 0, 0);
		} else {
			this.NaviLayoutland.setVisibility(View.GONE);
			this.enlargedRoadView.setVisibility(View.VISIBLE);
			localLayoutParams = new RelativeLayout.LayoutParams(-1,	dp2px(250));
			this.enlargedRoadView.setLayoutParams(localLayoutParams);
			setMapLayoutParams(	0,250, 0, 0);

			RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) driveWayView.getLayoutParams();
			params.setMargins(0,-(driveWayView.getHeight()),0,0);
			driveWayView.setLayoutParams(params);
		}
		if(isShowRoadEnlarge&&crossBitmap!=null){
			enlargedRoadView.setBitMapIntoView(crossBitmap);
			if(isLandscape)this.NaviLayoutland.setVisibility(View.VISIBLE);
			this.cross_progress.setVisibility(View.VISIBLE);
		}
		updateTmcLayout();

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void restoreNormalLayout() {
		RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(0, 0);
		this.enlargedRoadView.setLayoutParams(localLayoutParams);
		if(isLandscape)this.NaviLayoutland.setVisibility(View.GONE);
		this.cross_progress.setVisibility(View.GONE);
		int top=0;
		int left=0;
		if(!this.isLandscape&&isLayOutVisible==true){
			top=100;}
		if(this.mMapNavi.getMapView()!=null&&this.isLandscape())
			left=200;
		setMapLayoutParams(left, top, 0, 0);
		RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) driveWayView.getLayoutParams();
		params.setMargins(left,dp2px(10),0,0);
		driveWayView.setLayoutParams(params);
		if(!isLandscape()&&tmc_layout!=null){
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tmc_layout.getLayoutParams();
			tmc_layout.setScaleX(1.3f);
			tmc_layout.setScaleY(1.3f);
			lp.bottomMargin = dp2px( 100);
			if(switch_img.getVisibility()==View.VISIBLE)
				lp.bottomMargin = dp2px( 130);
			tmc_layout.setLayoutParams(lp);}
	}




	private void showDialog() {
		try {
			new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("确定退出导航?").setPositiveButton("确定",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface paramDialogInterface,	int paramInt) {
					paramDialogInterface.cancel();
					NaviView.this.mMapNavi.stopNavi();
					NaviView.this.mMapViewListenerTriggerHandler.sendEmptyMessage(3);
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



	void setCarLock(boolean isCarLock) {
		setCarLock(isCarLock, true,false);
	}

	void setCarLock(boolean isCarLock, boolean paramBoolean2,boolean northUpMode) {
		try {
			if ((this.mNaviViewListener != null)&& (this.isCarLock != isCarLock)&& (!(this.isArrivedEnd))) {
				this.mNaviViewListener.onLockMap(isCarLock);
			}

			if (this.isLandscape) {
				if(isLayOutVisible)this.footbar_view_land.setVisibility(View.VISIBLE);
				this.footerLayout.setVisibility(View.GONE);
				zoomLayout.setVisibility(View.GONE);
				if (!isCarLock) {
					if(!this.isArrivedEnd)this.go_on_navi.setVisibility(View.VISIBLE);
					this.land_dis_and_time_layout.setVisibility(View.GONE);
					if(isArrivedEnd)routeOverviewViewLand.setVisibility(View.GONE);
					if(this.mMapNaviViewOptions.isZoomLayoutVisible())this.zoom_view_land.setVisibility(View.VISIBLE);

				}else{
					this.go_on_navi.setVisibility(View.GONE);
					this.zoom_view_land.setVisibility(View.GONE);
					if(!this.isArrivedEnd)this.land_dis_and_time_layout.setVisibility(View.VISIBLE);}
			}
			else {
				if(this.mMapNaviViewOptions.isZoomLayoutVisible())zoomLayout.setVisibility(View.VISIBLE);
				this.footerLayout.setVisibility(View.VISIBLE);
				this.footbar_view_land.setVisibility(View.GONE);
				if(!this.isArrivedEnd){
					if(mMapNavi.getNaviType() == Navi.GPSNaviMode){
						this.keepOnNavigationLayout.setVisibility((isCarLock) ? View.GONE : View.VISIBLE);
						this.showNaviInfoBottomBarLayout.setVisibility((isCarLock) ? View.VISIBLE: View.GONE);
					}
					if (this.mMapNaviViewOptions.isRouteListButtonShow())
						this.routeOverviewView.setVisibility((!(isCarLock)) ? View.VISIBLE : View.GONE);
				}
//				this.zoomOutBtn.setVisibility((!(isCarLock)) ? View.VISIBLE : View.GONE);
//				this.zoomInBtn.setVisibility((!(isCarLock)) ? View.VISIBLE : View.GONE);
				this.layout_zoom.setVisibility((!(isCarLock)) ? View.VISIBLE : View.GONE);
				if (this.mMapNaviViewOptions.isRouteListButtonShow())
					this.routeOverviewView.setVisibility((!(isCarLock)) ? View.VISIBLE : View.GONE);
			}


			if (this.isArrivedEnd)
				return;

			this.isCarLock = isCarLock;
			if (this.mNaviViewListener != null)
				if (isCarLock)
					this.mNaviViewListener.onNaviMapMode(0);
				else
					this.mNaviViewListener.onNaviMapMode(1);

			this.mMapViewListenerTriggerHandler.removeMessages(0);
			if (isCarLock) {
				this.isAllRoute = false;
				LogUtil.d(TAG,"setCarLock curMapZoomLenvl"+curMapZoomLenvl);
				CameraUpdate localCameraUpdate = CameraUpdateFactory.zoomTo(this.curMapZoomLenvl);
				this.mMap.moveCamera(localCameraUpdate);
				this.routeTMCOnOffView.setVisibility(View.GONE);
				this.mode_up.setVisibility(View.GONE);
			} else {
				restoreNormalLayout();
				if(this.mMapNaviViewOptions.isTrafficLayerEnabled())
				{this.routeTMCOnOffView.setVisibility(View.GONE);
//				{this.routeTMCOnOffView.setVisibility(View.VISIBLE);//caohai 注释掉

				}
				if(this.mMapNaviViewOptions.isShowNaviUpMode())this.mode_up.setVisibility(View.VISIBLE);
				if (paramBoolean2)
					this.mMapViewListenerTriggerHandler.sendEmptyMessageDelayed(0, this.lockMapDelayed);
			}
			this.uiController.setCarLock(isCarLock,northUpMode);
			updateTmcLayout();

			//不支持TMCBAR
			if ((this.mMapNaviViewOptions.isTrafficBarEnabled())&& (this.mMapNavi.getEngineType() == 0)&&(this.isLayOutVisible))
				this.tmc_layout.setVisibility((isCarLock) ? View.VISIBLE : View.GONE);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		requestLayout();

	}



	private void showDebugInfo() {
		if (this.uiController != null)
			this.uiController.c();
	}



	void arrivedEnd() {
		this.isArrivedEnd = true;
		this.arriveEndBottomBarLayout.setVisibility(View.VISIBLE);
		this.keepOnNavigationLayout.setVisibility(View.GONE);
		this.nextRoadDisBelowRoadSignTV.setVisibility(View.GONE);
		this.navi_dis_unit_text.setVisibility(View.GONE);
		this.land_unit.setVisibility(View.INVISIBLE);
		this.land_dis.setVisibility(View.INVISIBLE);
		this.land_dis_and_time_layout.setVisibility(View.INVISIBLE);
		this.showNaviInfoBottomBarLayout.setVisibility(View.GONE);
		this.tmc_layout.setVisibility(View.GONE);
		this.routeTMCOnOffView.setVisibility(View.GONE);
		this.mode_up.setVisibility(View.GONE);
		this.nextRoadNameAtTopBarTV.setVisibility(View.GONE);
		this.routeOverviewView.setVisibility(View.GONE);
	}

	void initLayout() {
		this.arriveEndBottomBarLayout.setVisibility(View.GONE);
		this.dividingLineLeftOfMenu.setVisibility(View.VISIBLE);
		this.naviSettingView.setVisibility(View.VISIBLE);
		this.upLeftCornerNaviLayout.setVisibility(View.VISIBLE);
		this.keepOnNavigationLayout.setVisibility(View.GONE);
		this.nextRoadNameAtTopBarTV.setVisibility(View.VISIBLE);
		this.showNaviInfoBottomBarLayout.setVisibility(View.VISIBLE);
		this.routeTMCOnOffView.setVisibility(View.GONE); //caohai注释
		this.mode_up.setVisibility(View.VISIBLE);
//		this.zoomOutBtn.setVisibility(View.GONE);
//		this.zoomInBtn.setVisibility(View.GONE);
		this.layout_zoom.setVisibility(View.GONE);
		this.routeOverviewView.setVisibility(View.GONE);
		setConfigurationChanged(this.isLandscape);
	}



	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,	int paramInt3, int paramInt4) {
		if (this.isDestroy)
			return;
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		try {
			if (ifSizeChanged()) {
				setCustomizedLockCenter();

				if (this.driveWayView != null) {
					this.driveWayView.invalidate();
				}

			}
		} catch (Throwable localThrowable) {
		}
	}

	private boolean ifSizeChanged() {
		return ((this.mapHeight != this.mapView.getHeight()) || (this.mapWidth != this.mapView		.getWidth()));
	}

	private void setCustomizedLockCenter() {
		this.mapHeight = this.mapView.getHeight();
		this.mapWidth = this.mapView.getWidth();
		if ((this.mapHeight != 0) && (this.mapWidth != 0)) {
			this.mMap.setPointToCenter((int) (this.mapWidth * point_center_x),	(int) (this.mapHeight * point_center_y));
		}

		this.uiController.e();
	}

//	private void addTMCChartLayoutToMap() {
//		if (this.customTmcView == null)
//			this.customTmcView = new CustomTmcView(getContext());
//
//		this.containerRelativeLayout.removeView(this.customTmcView);
//		RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
//
//		Bitmap localBitmap = this.customTmcView.getDisplayingBitmap();
//		int i = localBitmap.getHeight();
//
//		int j = 0;
//		int k = 0;
//		if (isOrientationLandscape())
//			j = this.customTmcView.getTmcBarBgHeight() * 2 / 3 - i >> 1;
//		else
//			j = this.customTmcView.getTmcBarBgHeight() - i >> 1;
//
//		j += this.customTmcView.getTmcBarBgPosY();
//		k = this.customTmcView.getTmcBarBgPosX();
//		localLayoutParams.setMargins(k, j, 0, 0);
//		this.containerRelativeLayout.addView(this.customTmcView,localLayoutParams);
//		this.containerRelativeLayout.addView(this.customTmcView);
//	}


	private void setMapLayoutParams(int paramInt1, int paramInt2,int paramInt3, int paramInt4) {
		if ((!(this.isLayOutVisible)) && (paramInt4 != 0)) {
			return;
		}
		RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) this.mapView.getLayoutParams();
		localLayoutParams.setMargins(dp2px(paramInt1), dp2px(paramInt2),dp2px(paramInt3), dp2px(paramInt4));
		this.mapView.setLayoutParams(localLayoutParams);
	}



	/*
	显示或隐藏布局
	 */
	private void setLayOutVisible(boolean paramBoolean) {
		this.isLayOutVisible = paramBoolean;

		if (!(this.isLayOutVisible)) {
			this.footerLinearLayout.setVisibility(View.GONE);
			this.zoomLayout.setVisibility(View.GONE);
			this.topNaviLayout.setVisibility(View.GONE);
			this.gps_sum_text_port.setVisibility(View.GONE);
			this.roadSignLayout.setVisibility(View.GONE);
			this.roadNameLayout.setVisibility(View.GONE);
			this.car_way_tmc_layout.setVisibility(View.GONE);
			this.tmc_layout.setVisibility(View.GONE);
			this.zoom_view_land.setVisibility(View.GONE);
			this.land_info_layout.setVisibility(View.GONE);
		} else {
			this.footerLinearLayout.setVisibility(View.VISIBLE);
			if(isArrivedEnd)
				showNaviInfoBottomBarLayout.setVisibility(View.GONE);
			if(isLandscape()){
				this.zoom_view_land.setVisibility(View.VISIBLE);
				this.land_info_layout.setVisibility(View.VISIBLE);
			}else{
				this.zoomLayout.setVisibility(View.VISIBLE);
			}
			this.topNaviLayout.setVisibility(View.VISIBLE);
			this.roadSignLayout.setVisibility(View.VISIBLE);
			this.roadNameLayout.setVisibility(View.VISIBLE);
			this.car_way_tmc_layout.setVisibility(View.VISIBLE);
		}
		restoreNormalLayout();
	}

	private void setZoomVisible(boolean paramBoolean) {
		this.isZoomVisible = paramBoolean;
		if (!(this.isZoomVisible))
			this.zoomLayout.setVisibility(View.INVISIBLE);
		else
			this.zoomLayout.setVisibility(View.VISIBLE);
	}

	 class MapViewListenerTriggerHandler extends Handler {
		private WeakReference<NaviView> mNaviViewRef;

		MapViewListenerTriggerHandler(NaviView paramMapNaviView) {
			try {
				this.mNaviViewRef = new WeakReference(paramMapNaviView);
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
		}

		public void handleMessage(Message paramMessage) {
			NaviView naviView = (NaviView) this.mNaviViewRef.get();
			if (naviView == null)
				return;
			try {
				switch (paramMessage.what) {
					case 0:
						naviView.setNaviMode(naviView.getNaviMode());//恢复朝向的锁车
						break;
					case 1:
						if (naviView.mNaviViewListener != null)
							naviView.mNaviViewListener.onNaviSetting();
						break;
					case 2:
						naviView.showDialog();
						break;
					case 3:
						if (naviView.mNaviViewListener != null)
							naviView.mNaviViewListener.onNaviCancel();
						break;
					case 4:
						naviView.setCarLock(false);
						break;
					//caohai 2018.7.24 修复主界面跳转导航界面-点击地图线路丢失bug （解决办法为重新加载一次路径，临时解决办法，bug原因还未定位）
					case 10:
//						uiController.reLoadNaviPath();
						break;
					default:
						break;
				}
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
				NaviUtilDecode.a(localThrowable);
			}
		}
	}

	private void titleColor(int  color){
		//距离
		nextRoadDisBelowRoadSignTV.setTextColor(color);
		land_dis.setTextColor(color);
		//单位
		land_unit.setTextColor(color);
		navi_dis_unit_text.setTextColor(color);
		//下个路名
		land_next_name.setTextColor(color);
		nextRoadNameAtTopBarTV.setTextColor(color);

}
	private void naviActionColor(int color){
		land_action.setTextColor(color);
		navi_next_action_text.setTextColor(color);
	}

	private void disAndTime_top(String color){
		land_restDis.setTextColor(Color.parseColor(color));
		land_time.setTextColor(Color.parseColor(color));
		this.uiController.setFontColor(color, color);//底部距离，时间字体颜色
	}
	private void disAndTime_down(int color){
		land_dis_text_down.setTextColor(color);
		dis_text_down.setTextColor(color);
//		time_text_down.setTextColor(color);
	}

	private void lineViewColor(int color){
		dividingLineRightOfBack.setBackgroundColor(color);
		dividingLineLeftOfMenu.setBackgroundColor(color);
		top_line_view.setBackgroundColor(color);
	}
	private void topInfoBackgroud(int color){
		topNaviLayout.setBackgroundColor(color);
		waitGpsPortLy.setBackgroundColor(color);
		land_info_layout.setBackgroundColor(color);
		waitGpsLandLy.setBackgroundColor(color);
	}
	private void bottomInfoBackgroud(int color){
		footerLinearLayout.setBackgroundColor(color);
	}

	private void setCustomStyle() {
			Map<String,String> configMap= NaviViewUtil.config_style;
		if(configMap!=null)
		for(Map.Entry<String,String> entry:configMap.entrySet()){
				String key=entry.getKey();
				String values=entry.getValue();
			if(key==null||"".equals(key)||values==null||"".equals(values))return;
			if("naviMainText".equals(key)){
				//前方道路和距离
				if(values.startsWith("#")&&(values.length()==7||values.length()==9))
				titleColor(Color.parseColor(values));
			}else if("naviActionText".equals(key)){
				//动作
				if(values.startsWith("#")&&(values.length()==7||values.length()==9))
					naviActionColor(Color.parseColor(values));
			}else if("naviDisAndTime".equals(key)){
				//底部具体的时间和距离信息
				if(values.startsWith("#")&&(values.length()==7||values.length()==9))
					disAndTime_top(values);
			}else if("bottomText".equals(key)){
				//底部时间和距离文字
				if(values.startsWith("#")&&(values.length()==7||values.length()==9)){
					disAndTime_down(Color.parseColor(values));
				}
			} else if ("dividingLine".equals(key)) {
				//分隔线
				if(values.startsWith("#")&&(values.length()==7||values.length()==9))
					lineViewColor(Color.parseColor(values));
			}else if("topInfoBackgroud".equals(key)){
				//顶部信息栏背景
				if(values.startsWith("#")&&(values.length()==7||values.length()==9))
					topInfoBackgroud(Color.parseColor(values));
			}else if("bottomInfoBackgroud".equals(key)){
				//底部信息栏背景
				if(values.startsWith("#")&&(values.length()==7||values.length()==9))
					bottomInfoBackgroud(Color.parseColor(values));
			} else if ("".equals(key)) {

			}

		}
	}

	private void setZoomBtnEnable(CameraPosition position){
		if(position.zoom==this.mMap.getMaxZoomLevel()){
			zoomInBtn.setEnabled(false);
		}else {
			zoomInBtn.setEnabled(true);
		}
		if(position.zoom==this.mMap.getMinZoomLevel()){
			zoomOutBtn.setEnabled(false);
		}else {
			zoomOutBtn.setEnabled(true);
		}
	}
}