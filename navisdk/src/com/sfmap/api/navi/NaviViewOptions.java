package com.sfmap.api.navi;

import android.graphics.Bitmap;
import com.sfmap.tbt.NaviUtilDecode;

/**
 * 导航视图参数类。
 */
public class NaviViewOptions {
	/**
	 * 默认主题颜色（数值：0）。
	 */
	public static final int DEFAULT_COLOR_TOPIC = 0;

	/**
	 * 经典蓝色主题（数值：1）。
	 */
	public static final int BLUE_COLOR_TOPIC = 1;

	/**
	 * 可爱粉色主题（数值：2）。
	 */
	public static final int PINK_COLOR_TOPIC = 2;

//	/**
//	 * 简约白色主题（数值：3）。
//	 */
//	public static final int WHITE_COLOR_TOPIC = 3;

	/**
	 * HUD正向显示（数值：1）。
	 */
	public static final int HUD_NORMAL_SHOW = 1;

	/**
	 * HUD镜像显示（数值：2）。
	 */
	public static final int HUD_MIRROR_SHOW = 2;

	private boolean autoDrawRoute;
	private boolean isCrossDisplayEnabled;
	private boolean isCrossDisplayShow;
	private boolean isLaneInfoShow;
	private int zoom;
	private int tilt;
	private boolean isCompassEnabled;
	private boolean isTrafficBarEnabled;
	private boolean isTrafficLayerEnabled;
	private boolean isRouteListButtonShow;
	private boolean isNaviNight;
	private Bitmap startBitmap;
	private Bitmap endBitmap;
	private Bitmap wayBitmap;
	private Bitmap monitorBitmap;
	private boolean isCameraEnabled;
	private boolean isScreenAlwaysBright;
	private boolean isTrafficInfoUpdateEnabled;
	private boolean isCameraInfoUpdateEnabled;
	private boolean isReCalculateRouteForYaw;
	private boolean isReCalculateRouteForTrafficJam;
	private boolean isSettingMenuEnabled;
	private int themeColor;
	private boolean isTrafficLine;
	private int leaderLineColor;
	private boolean isLayoutVisible;
	private boolean isZoomLayoutVisible;
	private long lockMapDelayed;
	private boolean isAutoChangeZoom;
	private double mapCenter_X;
	private double mapCenter_Y;
	private boolean isModeNaviUP;

	/**
	 * 构造函数。
	 */
	public NaviViewOptions() {
		this.autoDrawRoute = true;
		this.isCrossDisplayEnabled = true;
		this.isCrossDisplayShow = true;
		this.isLaneInfoShow = true;
		this.zoom = 18;
		this.tilt = 45;
		this.isCompassEnabled = true;
		this.isTrafficBarEnabled = true;
		this.isTrafficLayerEnabled = true;
		this.isRouteListButtonShow = true;
		this.isNaviNight = false;

		this.isCameraEnabled = true;
		this.isScreenAlwaysBright = true;
		this.isTrafficInfoUpdateEnabled = true;
		this.isCameraInfoUpdateEnabled = true;
		this.isReCalculateRouteForYaw = true;
		this.isReCalculateRouteForTrafficJam = false;
		this.isSettingMenuEnabled = false;
		this.themeColor = 0;
		this.isTrafficLine = true;

		this.leaderLineColor = -1;
		this.isLayoutVisible = true;
		this.isZoomLayoutVisible = true;
		this.lockMapDelayed = 5000L;
		this.isAutoChangeZoom = false;
		this.mapCenter_X = 0.5D;
		this.mapCenter_Y = 0.6666666666666666D;
		this.isModeNaviUP=true;

	}

	public void setIsShowNaviUpMode(boolean enable){
		this.isModeNaviUP=enable;
	}
	public boolean isShowNaviUpMode(){return this.isModeNaviUP;}
	/**
	 * 返回指南针图标是否显示。
	 * @return 返回true，代表显示指南针；返回false，代表不显示指南针。
	 */
	public boolean isCompassEnabled() {
		return this.isCompassEnabled;
	}

	/**
	 * 设置指南针图标否在导航界面显示，默认显示。true，显示；false，隐藏。
	 * @param enabled  - true，显示；false，隐藏。
	 */
	public void setCompassEnabled(Boolean enabled) {
		this.isCompassEnabled = enabled.booleanValue();
	}

	/**
	 * 返回路况光柱条是否显示（只适用于驾车导航，需要联网）。路况光柱颜色：红—拥堵；黄—缓行；绿—畅通；灰—未知。
	 * @return 返回true，代表显示路况光柱条；false，代表隐藏。默认为true，代表显示路况光柱条。
	 */
	public boolean isTrafficBarEnabled() {
		return this.isTrafficBarEnabled;
	}

	/**
	 * 设置路况光柱条是否显示（只适用于驾车导航，需要联网）。
	 * @param enabled  - 路况光柱条是否在导航界面显示，默认为true，代表显示路况光柱条。true，显示；false，隐藏。
	 */
	public void setTrafficBarEnabled(Boolean enabled) {
		this.isTrafficBarEnabled = enabled.booleanValue();
	}

	/**
	 * 返回[实时交通图层开关按钮]是否显示（只适用于驾车导航，需要联网）
	 * @return 返回实时交通图层开关按钮是否显示。默认为true，代表显示实时交通图层。true，显示；false，隐藏。
	 */
	public boolean isTrafficLayerEnabled() {
		return this.isTrafficLayerEnabled;
	}

	/**
	 * 设置[实时交通图层开关按钮]是否显示（只适用于驾车导航，需要联网）。
	 * @param enabled  - 实时实时交通图层开关按钮是否在导航界面显示，默认为true，代表显示实时交通图层。true，显示；false，隐藏。
	 */
	public void setTrafficLayerEnabled(Boolean enabled) {
		this.isTrafficLayerEnabled = enabled.booleanValue();
	}

	/**
	 * 返回当前界面是否显示路线全览按钮
	 * @return 返回全览按钮是否在导航界面显示，默认显示。true，显示；false，隐藏。
	 */
	public boolean isRouteListButtonShow() {
		return this.isRouteListButtonShow;
	}

	/**
	 * 返回全览按钮是否在导航界面显示，默认显示。true，显示；false，隐藏。
	 * @param isShow  - 设置全览按钮是否在导航界面显示，默认显示。true，显示；false，隐藏。
	 */
	public void setRouteListButtonShow(boolean isShow) {
		this.isRouteListButtonShow = isShow;
	}

	/**
	 * 返回导航界面是否显示黑夜模式。
	 * @return 导航界面是否显示黑夜模式。true代表显示；false代表不显示。
	 */
	public boolean isNaviNight() {
		return this.isNaviNight;
	}

	/**
	 * 设置导航界面是否显示黑夜模式。
	 * @param isNight  - 导航界面是否显示黑夜模式。true代表显示；false代表不显示。
	 */
	public void setNaviNight(boolean isNight) {
		this.isNaviNight = isNight;
	}

	/**
	 * 设置起点位图，须在画路前设置。
	 * @param startBitmap - 起点位图
	 */
	public void setStartPointBitmap(Bitmap startBitmap) {
		this.startBitmap = startBitmap;
	}

	Bitmap getStartMarker() {
		return this.startBitmap;
	}

	/**
	 * 设置终点位图，须在画路前设置。
	 * @param endBitmap  - 终点位图
	 */
	public void setEndPointBitmap(Bitmap endBitmap) {
		this.endBitmap = endBitmap;
	}

	Bitmap getEndMarker() {
		return this.endBitmap;
	}

	/**
	 * 设置途经点位图，须在画路前设置。
	 * @param wayPointBitmap  - 途经点位图
	 */
	public void setWayPointBitmap(Bitmap wayPointBitmap) {
		this.wayBitmap = wayPointBitmap;
	}

	Bitmap getWayMarker() {
		return this.wayBitmap;
	}

	/**
	 * 设置摄像头监控图标（只适用于驾车导航）。
	 * @param icon 摄像头图标。
	 */
	public void setMonitorCameraBitmap(Bitmap icon) {
		this.monitorBitmap = icon;
	}

	Bitmap getMonitorMarker() {
		return this.monitorBitmap;
	}

	/**
	 * 返回摄像头图标是否显示。
	 * @return  摄像头图标是否显示。
	 */
	public boolean isMonitorCameraEnabled() {
		return this.isCameraEnabled;
	}

	/**
	 * 设置摄像头监控图标是否显示（只适用于驾车导航）。
	 * @param enabled - 摄像头图标是否在导航界面显示，默认显示。true，显示；false，隐藏。
	 */
	public void setMonitorCameraEnabled(Boolean enabled) {
		this.isCameraEnabled = enabled.booleanValue();
	}

	/**
	 * 返回导航状态下屏幕是否一直开启。
	 * @return 返回导航状态下屏幕是否一直开启。true，开启；false，不开启。
	 */
	public boolean isScreenAlwaysBright() {
		return this.isScreenAlwaysBright;
	}

	/**
	 * 设置导航状态下屏幕是否一直开启。
	 * @param enabled - 设置屏幕是否一直开启，默认不开启。true，开启；false，不开启。
	 */
	public void setScreenAlwaysBright(boolean enabled) {
		this.isScreenAlwaysBright = enabled;
	}

//	/**
//	 * 返回交通播报是否打开（只适用于驾车导航，需要联网）。
//	 * @return 返回交通播报是否打开（只适用于驾车导航，需要联网）。true，开启；false，不开启。
//	 */
//	public boolean isTrafficInfoUpdateEnabled() {
//		return this.isTrafficInfoUpdateEnabled;
//	}
//
//	/**
//	 * 设置交通播报是否打开（只适用于驾车导航，需要联网）。
//	 * @param enabled - 设置交通播报是否打开，默认开启。true，开启；false，不开启。
//	 */
//	public void setTrafficInfoUpdateEnabled(boolean enabled) {
//		this.isTrafficInfoUpdateEnabled = enabled;
//	}

	/**
	 * 返回摄像头播报是否打开（只适用于驾车导航）。
	 * @return 摄像头播报是否打开（只适用于驾车导航）。true，开启；false，不开启。
	 */
	public boolean isCameraInfoUpdateEnabled() {
		return this.isCameraInfoUpdateEnabled;
	}

	/**
	 * 设置摄像头播报是否打开（只适用于驾车导航）。
	 * @param enabled - 设置播报是否打开，默认开启。true，开启；false，不开启。
	 */
	public void setCameraInfoUpdateEnabled(boolean enabled) {
		this.isCameraInfoUpdateEnabled = enabled;
	}

	/**
	 * 返回偏航的情况，是否设置重算路径。
	 * @return 返回偏航的情况，是否设置重算路径。true，开启；false，不开启。
	 */
	public boolean isReCalculateRouteForYaw() {
		return this.isReCalculateRouteForYaw;
	}

	/**
	 * 偏航时是否重新计算路径(计算路径需要联网）。
	 * @param enabled  - 当用户偏离规划路线时，是否需要重新计算路径。默认为true。true，重新计算；false，不计算。
	 */
	public void setReCalculateRouteForYaw(Boolean enabled) {
		this.isReCalculateRouteForYaw = enabled.booleanValue();
	}

//	/**
//	 * 返回前方拥堵时是否重新计算路径（只适用于驾车导航，需要联网）。
//	 * @return 返回前方拥堵时是否重新计算路径（只适用于驾车导航，需要联网）。true，开启；false，不开启。
//	 */
//	public boolean isReCalculateRouteForTrafficJam() {
//		return this.isReCalculateRouteForTrafficJam;
//	}
//
//	/**
//	 * 前方拥堵时是否重新计算路径（只适用于驾车导航，需要联网）。
//	 * @param enabled  - 前方拥堵时是否重新计算路径。true代表重新规划路径；false代表不重新计算。
//	 */
//	public void setReCalculateRouteForTrafficJam(Boolean enabled) {
//		this.isReCalculateRouteForTrafficJam = enabled.booleanValue();
//	}

	/**
	 * 返回导航界面菜单按钮是否显示。
	 * @return 返回导航界面菜单按钮是否显示。true，开启；false，不开启。
	 */
	public boolean isSettingMenuEnabled() {
		return this.isSettingMenuEnabled;
	}

	/**
	 * 设置菜单按钮是否在导航界面显示。
	 * @param enabled - 菜单按钮是否在导航界面显示。true代表显示，false代表不显示。
	 */
	public void setSettingMenuEnabled(Boolean enabled) {
		this.isSettingMenuEnabled = enabled.booleanValue();
	}

	/**
	 * 返回导航界面颜色主题的类型。
	 * @return 返回导航界面颜色主题的类型。具体颜色主题类型请参见相关常量。
	 */
	public int getNaviViewTopic() {
		return this.themeColor;
	}

	/**
	 * 设置导航界面的颜色主题。
	 * @param color  - 导航界面的颜色主题,包含黑色、蓝色、粉色和白色。默认为黑色。
	 */
	public void setNaviViewTopic(int color) {
		this.themeColor = color;
	}

	/**
	 * 返回是否绘制显示交通路况的线路（彩虹线），拥堵-红色，畅通-绿色，缓慢-黄色，未知-蓝色。
	 * @return 返回 true 表示绘制；返回 false 表示未绘制.
	 */
	public boolean isTrafficLine() {
		return this.isTrafficLine;
	}

//	/**
//	 * 设置是否绘制显示交通路况的线路（彩虹线），拥堵-红色，畅通-绿色，缓慢-黄色，未知-蓝色。默认不绘制彩虹线。
//	 * @param enabled - 设置为true，绘制彩虹线；反之不绘制。
//	 */
	public void setTrafficLine(boolean enabled) {
		this.isTrafficLine = enabled;
	}

	int getLeaderLineColor() {
		return this.leaderLineColor;
	}

	/**
	 * 返回是否绘制牵引线。
	 * @return 返回 true 表示绘制；返回 false 表示未绘制。
	 */
	public boolean isLeaderLineEnabled() {
		return (this.leaderLineColor != -1);
	}

	/**
	 * 设置是否绘制牵引线（当前位置到目的地的指引线）。默认不绘制牵引线。
	 * @param color - 设置牵引线颜色，为ARGB格式。不显示牵引线时，颜色设置为-1即可。
	 */
	public void setLeaderLineEnabled(int color) {
		this.leaderLineColor = color;
	}

	/**
	 * 获取当前缩放控件是否显示。
	 * @return true导航界面显示，false导航界面不显示。
	 */
	public boolean isZoomLayoutVisible() {
		return this.isZoomLayoutVisible;
	}
	/**
	 * 获取当前导航界面是否显示。
	 * @return true导航界面显示，false导航界面不显示。
	 */
	public boolean isLayoutVisible() {
		return this.isLayoutVisible;
	}
	/**
	 * 设置导航界面UI是否显示。
	 * @param layoutVisible  - true导航界面显示，false导航界面不显示。
	 */
	public void setLayoutVisible(boolean layoutVisible) {
		this.isLayoutVisible = layoutVisible;
	}
/**
	 * 设置导航缩放控件是否显示。
	 * @param zoomLayoutVisible  - true显示，false不显示。
	 */
	public void setZoomLayoutVisible(boolean zoomLayoutVisible) {
		this.isZoomLayoutVisible = zoomLayoutVisible;
	}

	/**
	 * 获取锁定地图延迟毫秒数。
	 * @return 延迟毫秒。
	 */
	public long getLockMapDelayed() {
		return this.lockMapDelayed;
	}

	/**
	 * 设置锁定地图延迟毫秒数。
	 * @param lockMapDelayed  - 延迟毫秒。
	 */
	public void setLockMapDelayed(long lockMapDelayed) {
		this.lockMapDelayed = lockMapDelayed;
	}

	/**
	 * 是否自动画路，默认为True，此时当算路成功后会立即自动画路。
	 * @return true, 自动画路;false，不自动画路
	 */
	public boolean isAutoDrawRoute() {
		return this.autoDrawRoute;
	}

	/**
	 * 设置是否自动画路。
	 * @param autoDrawRoute  - true, 自动画路；false，不自动画路。
	 */
	public void setAutoDrawRoute(boolean autoDrawRoute) {
		this.autoDrawRoute = autoDrawRoute;
	}

	/**
	 * 是否开启路口放大图功能。
	 * @return true，开启；false，不开启
	 */
	public boolean isCrossDisplayEnabled() {
		return this.isCrossDisplayEnabled;
	}

	/**
	 * 设置是否开启路口放大图功能。
	 * @param enabled - true，开启；false，不开启。
	 */
	public void setCrossDisplayEnabled(boolean enabled) {
		NaviUtilDecode.crossDisplayMode = (enabled) ? 2 : 0;
		if (!(enabled))
			this.isCrossDisplayShow = false;
	}

	/**
	 * 是否显示路口放大图。
	 * @return true，显示；false，不显示。
	 */
	public boolean isCrossDisplayShow() {
		return this.isCrossDisplayShow;
	}

	/**
	 * 设置是否显示路口放大图。
	 * @param isCrossDisplayShow - true，显示；false，不显示
	 */
	public void setCrossDisplayShow(boolean isCrossDisplayShow) {
		if (NaviUtilDecode.crossDisplayMode != 0)
			this.isCrossDisplayShow = isCrossDisplayShow;
	}

	/**
	 * 是否显示车道线信息view。
	 * @return true，显示；false，不显示。
	 */
	public boolean isLaneInfoShow() {
		return this.isLaneInfoShow;
	}

	/**
	 * 设置是否显示车道线信息view。
	 * @param isLaneInfoShow - true，显示；false，不显示。
	 */
	public void setLaneInfoShow(boolean isLaneInfoShow) {
		this.isLaneInfoShow = isLaneInfoShow;
	}

	/**
	 * 获取缩放等级。
	 * @return 缩放等级。
	 */
	public int getZoom() {
		return this.zoom;
	}

	/**
	 * 设置缩放等级。
	 * @param zoom - 缩放等级
	 */
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	/**
	 * 获取倾角。
	 * @return 倾角。
	 */
	public int getTilt() {
		return this.tilt;
	}

	/**
	 * 设置倾角。
	 * @param tilt - 倾角
	 */
	public void setTilt(int tilt) {
		this.tilt = tilt;
	}

	/**
	 * 是否自动改变缩放等级。
	 * @return true, 自动改变；false，不自动改变。
	 */
	public boolean isAutoChangeZoom() {
		return this.isAutoChangeZoom;
	}

	/**
	 * 设置是否自动改变缩放等级。
	 * @param isAutoChangeZoom - true,自动改变；false，不自动改变。
	 */
	public void setAutoChangeZoom(boolean isAutoChangeZoom) {
		this.isAutoChangeZoom = isAutoChangeZoom;
	}

	/**
	 * 设置自车位置锁定在屏幕中的位置。
	 * @param centerX - 0-1 在x轴的位置，百分比
	 * @param centerY - 0-1 在y轴的位置，百分比
	 */
	public void setPointToCenter(double centerX, double centerY) {
		this.mapCenter_X = centerX;
		this.mapCenter_Y = centerY;
	}

	/**
	 * 自车位置锁定在x轴的位置，范围：0-1。
	 * @return 自车位置锁定在x轴的位置。
	 */
	public double getMapCenterX() {
		return this.mapCenter_X;
	}

	/**
	 * 自车位置锁定在y轴的位置，范围：0-1。
	 * @return 自车位置锁定在y轴的位置。
	 */
	public double getMapCenterY() {
		return this.mapCenter_Y;
	}
}