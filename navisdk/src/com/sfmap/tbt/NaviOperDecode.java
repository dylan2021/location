package com.sfmap.tbt;

import com.sfmap.tbt.AvoidJamArea;
import com.sfmap.tbt.GroupSegment;
import com.sfmap.tbt.JamInfo;
import com.sfmap.tbt.NaviAction;
import com.sfmap.tbt.RouteIncident;

/**
 * 导航操作抽象基类
 */
public abstract interface NaviOperDecode {

	public abstract int startEmulatorNavi();

	public abstract int startGPSNavi();

	public abstract void pauseNavi();

	public abstract void resumeNavi();

	public abstract void stopNavi();

	public abstract int playNaviManual();

	public abstract int reroute(int paramInt1, int paramInt2);

	public abstract int selectRoute(int paramInt);

	public abstract int getRouteLength();

	public abstract int getRouteTime();

	public abstract int getSegNum();

	public abstract int getSegLength(int paramInt);

	public abstract int getSegTime(int paramInt);

	public abstract int getSegLinkNum(int paramInt);

	public abstract double[] getSegCoor(int segIndex);

	public abstract int[] getAllRouteID();

	public abstract String getLinkRoadName(int paramInt1, int paramInt2);

	public abstract double[] getLinkCoor(int segIndex, int linkIndex);

	public abstract int getLinkLength(int paramInt1, int paramInt2);

	public abstract int getLinkTime(int paramInt1, int paramInt2);

	public abstract void setEmulatorSpeed(int paramInt);

	public abstract int getRouteStrategy();

	public abstract int getSegChargeLength(int paramInt);

	public abstract int getSegTollCost(int paramInt);

	public abstract int getLinkRoadClass(int paramInt1, int paramInt2);

	public abstract int getLinkFormWay(int paramInt1, int paramInt2);

	public abstract int haveTrafficLights(int paramInt1, int paramInt2);

	public abstract void manualRefreshTMC();

	public abstract int playTrafficRadioManual(int paramInt);
	public abstract int getBypassLimitedRoad();
	public abstract int getDiffToTMCRoute();
	public abstract GroupSegment[] getGroupSegmentList();
	public abstract JamInfo[] getJamInfoList();
	public abstract NaviAction getSegNaviAction(int segIndex);
	public abstract int getSegTurnIcon(int segIndex);
	public abstract AvoidJamArea getAvoidJamArea();
	public abstract RouteIncident[] getRouteIncident();
	public abstract RestrictionArea[] getRestrictionInfo();

//	public abstract void setLocalOfflineDataPath(String pwPath);
//	public abstract OfflineDataElem[] GetOfflineDataListServer(int pnListSize);
//	public abstract OfflineDataElem[] GetOfflineDataListLocal(int pnListSize);
//	public abstract int LoadOfflineData(String dataID,String pwFilter);
//	public abstract void DebugReRoute(String strURL,String strSendContont);
}