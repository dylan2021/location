package com.sfmap.api.navi;

/**
 * 导航视图事件监听。
 */
public abstract interface NaviViewListener {
	/**
	 * 界面右下角功能设置按钮的回调接口。
	 */
	public abstract void onNaviSetting();

	/**
	 * 导航页面左下角返回按钮点击后弹出的 "退出导航对话框" 中选择 "确定" 后的回调接口。
	 */
	public abstract void onNaviCancel();

	/**
	 * 导航页面左下角返回按钮的回调接口
	 * @return false-由SDK主动弹出"退出导航"对话框，true-SDK不主动弹出"退出导航对话框"，由用户自定义
	 */
	public abstract boolean onNaviBackClick();

	/**
	 * 导航界面地图状态的回调。
	 * @param isLock - 地图状态，0:车头朝上状态；1:非锁车状态,即车标可以任意显示在地图区域内。
	 */
	public abstract void onNaviMapMode(int isLock);

	/**
	 * 界面左上角转向操作的点击回调。
	 */
	public abstract void onNaviTurnClick();

	/**
	 * 界面下一道路名称的点击回调。
	 */
	public abstract void onNextRoadClick();

	/**
	 * 界面全览按钮的点击回调。
	 */
	public abstract void onScanViewButtonClick();

	/**
	 * 是否锁定地图的回调。
	 * @param isLock true 代表锁定， false代表不锁定
	 */
	public abstract void onLockMap(boolean isLock);

	/**
	 * 开启避免拥堵后,拥堵时的回调
	 * @return false -由SDK主动弹出"切换路径"对话框,true-SDK不主动弹出"切换路径"对话框,由用户自定义
     */
	public abstract boolean onReRouteForTraffic();
}