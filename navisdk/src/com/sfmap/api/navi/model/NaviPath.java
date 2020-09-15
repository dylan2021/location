package com.sfmap.api.navi.model;

import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.tbt.AvoidJamArea;
import com.sfmap.tbt.GroupSegment;
import com.sfmap.tbt.JamInfo;
import com.sfmap.tbt.RouteIncident;

import java.util.List;

/**
 * 导航路线描述信息类。
 */
public class NaviPath {

	/**
	 * 途径点Index数组。
	 */
	public int[] wayPointIndex;
	private int allLength;
	private int strategy;
	private int allTime;
	private int stepsCount;
	private List<NaviStep> mSteps;
	private List<NaviLatLng> list;
	private NaviLatLng startPoi;
	private NaviLatLng endPoi;
	private List<NaviLatLng> wayPoi;
	private int tollCost;
	private NaviLatLng center;
	private LatLngBounds bounds;
	private int byPassLinitedRoad;
	private int diffToTMCRoute;
	private GroupSegment[] groupSegmentList;
	private JamInfo[] jamInfoList;
	private AvoidJamArea avoidJamArea;
	private RouteIncident[] routeIncident;
	/**
	 * 构造函数。
	 */
	public NaviPath() {
		this.wayPointIndex = null;
		this.tollCost = 0;
	}

	/**
	 * 获取途经点index数组。
	 * @return 途经点index数组
	 */
	public int[] getWayPointIndex() {
		return this.wayPointIndex;
	}

	/**
	 * 获取当前路径的途经点坐标（仅支持驾车模式下获取）。
	 * @return 返回途经点坐标。
	 */
	public List<NaviLatLng> getWayPoint() {
		return this.wayPoi;
	}

	/**
	 * 设置当前路径的途经点坐标（仅支持驾车模式)
	 * @param paramList 途经点坐标集合。
     */
	public void setWayPoint(List<NaviLatLng> paramList) {
		this.wayPoi = paramList;
	}

	/**
	 * 获取当前路线方案的起点坐标。
	 * @return 返回起点坐标。
	 */
	public NaviLatLng getStartPoint() {
		return this.startPoi;
	}

	/**
	 * 设置当前路线方案的起点坐标
	 * @param paramNaviLatLng 起点坐标。
     */
	public  void setStartPoint(NaviLatLng paramNaviLatLng) {
		this.startPoi = paramNaviLatLng;
	}

	/**
	 * 获取当前路线方案的终点坐标。
	 * @return 返回终点坐标。
	 */
	public NaviLatLng getEndPoint() {
		return this.endPoi;
	}

	/**
	 * 设置当前路线方案的终点坐标
	 * @param paramNaviLatLng 终点坐标。
     */
	 public void setEndPoint(NaviLatLng paramNaviLatLng) {
		this.endPoi = paramNaviLatLng;
	}

	/**
	 * 返回当前导航路线的中心点。
	 * @return 返回当前导航路线的中心点，即导航路径的最小外接矩形对角线的交点。
	 */
	public NaviLatLng getCenterForPath() {
		return this.center;
	}


	void setCenter(NaviLatLng paramNaviLatLng) {
		this.center = paramNaviLatLng;
	}

	/**
	 * 获取该导航路线最小坐标点和最大坐标点围成的矩形区域。 该方法需要在地图载入成功后使用。返回的LatLngBounds用法见地图包。
	 * @return 获取的一个矩形区域。LatLngBounds的用法参见地图包，可以获取矩形左下和右上点的坐标。
	 */
	public LatLngBounds getBoundsForPath() {
		return this.bounds;
	}


	void setBounds(LatLngBounds paramLatLngBounds) {
		this.bounds = paramLatLngBounds;
	}

	/**
	 * 返回当前导航路线上分段NaviStep
	 * @return 返回当前导航路线上分段NaviStep
	 */
	public List<NaviStep> getSteps() {
		return this.mSteps;
	}


	void setSteps(List<NaviStep> paramList) {
		this.mSteps = paramList;
	}

	/**
	 * 返回当前导航路线的所有坐标点。
	 * @return 返回当前导航路线的所有坐标点。
	 */
	public List<NaviLatLng> getCoordList() {
		return this.list;
	}


	void setList(List<NaviLatLng> paramList) {
		this.list = paramList;
	}


	NaviStep getStep(int paramInt) {
		return null;
	}

	/**
	 * 返回当前导航路线的总长度。
	 * @return 返回当前导航方案的总长度。如果获取成功则返回路径长度，单位为米，否则返回-1。
	 */
	public int getAllLength() {
		return this.allLength;
	}


	void setAllLength(int paramInt) {
		this.allLength = paramInt;
	}

	/**
	 * 返回当前导航路线的路径计算策略。
	 * @return 返回当前导航路线的路径计算策略。
	 */
	public int getStrategy() {
		return this.strategy;
	}


	void setStrategy(int paramInt) {
		this.strategy = paramInt;
	}

	/**
	 * 返回当前导航路径所需的时间，单位为秒。
	 * @return 如果获取成功则返回当前导航路径所需时间，单位为秒；否则返回-1。
	 */
	public int getAllTime() {
		return this.allTime;
	}


	void setAllTime(int paramInt) {
		this.allTime = paramInt;
	}

	/**
	 * 返回当前导航路线上分段的总数。
	 * @return 返回当前导航路线上分段的总数。
	 */
	public int getStepsCount() {
		return this.stepsCount;
	}


	void setStepsCount(int paramInt) {
		this.stepsCount = paramInt;
	}

	/**
	 * 获取导航段的花费金额，单位元。
	 * @return 获取导航段的花费金额，单位元。
	 */
	public int getTollCost() {
		return this.tollCost;
	}


	void setTollCost(int tollcost) {
		this.tollCost = tollcost;
	}

	/**
	 * 获取当前路段是否已规避限行路段
	 * @return 已规避则返回1，反之返回0
     */
	public int getByPassLinitedRoad() {
		return byPassLinitedRoad;
	}

	 void setByPassLinitedRoad(int byPassLinitedRoad) {
		this.byPassLinitedRoad = byPassLinitedRoad;
	}

	/**
	 * 获取相比当前路径，是否存在更省时间的tmc路径，能节省多少时间
	 * @return 存在更好的tmc路径则返回节省多少时间(分钟)，反之返回0
     */
	public int getDiffToTMCRoute() {
		return diffToTMCRoute;
	}

	 void setDiffToTMCRoute(int diffToTMCRoute) {
		this.diffToTMCRoute = diffToTMCRoute;
	}

	/**
	 * 获得整条路径的聚合段信息列表
	 * 列表数目为聚合段个数。
	 * @return 聚合段信息列表，失败返回NULL
     */
	public GroupSegment[] getGroupSegmentList() {
		return groupSegmentList;
	}

	 void setGroupSegmentList(GroupSegment[] groupSegmentList) {
		this.groupSegmentList = groupSegmentList;
	}

	/**
	 * 获得路径的拥堵点信息
	 * @return 拥堵点JamInfo数组，如果没有则返回NULL
     */
	public JamInfo[] getJamInfoList() {
		return jamInfoList;
	}

	 void setJamInfoList(JamInfo[] jamInfoList) {
		this.jamInfoList = jamInfoList;
	}

	/**
	 * 	获得选中路径上的拥堵区域信息
	 * @return 	路径上的拥堵区域信息
     */
	public AvoidJamArea getAvoidJamArea() {
		return avoidJamArea;
	}

	 void setAvoidJamArea(AvoidJamArea avoidJamArea) {
		this.avoidJamArea = avoidJamArea;
	}

	/**
	 * 	获得选中路径上的事件信息
	 * @return	路径上的事件信息数组，如果没有返回NULL
	 *
     */
	public RouteIncident[] getRouteIncident() {
		return routeIncident;
	}

	 void setRouteIncident(RouteIncident[] routeIncident) {
		this.routeIncident = routeIncident;
	}
}