package com.sfmap.api.navi.model;

import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.tbt.AvoidJamArea;
import com.sfmap.tbt.GroupSegment;
import com.sfmap.tbt.JamInfo;
import com.sfmap.tbt.RouteIncident;

import java.util.List;

public class NaviResultDecode {
	public NaviPath naviPath;
	public NaviLatLng center;
	public LatLngBounds bounds;
	private int routeLength;
	private int trategy;
	private int f;
	private int segNum;
	private List<NaviStep> steps;
	private List<NaviLatLng> coordList;
	private NaviLatLng startPoint;
	private NaviLatLng endPoint;
	private List<NaviLatLng> wayPoints;
	private NaviLatLng m;
	private NaviLatLng n;
	private int tollCost;
	private int byPassLinitedRoad;
	private int diffToTMCRoute;
	private GroupSegment[] groupSegmentList;
	private JamInfo[] jamInfoList;
	private AvoidJamArea avoidJamArea;
	private RouteIncident[] routeIncident;
	public NaviResultDecode() {
		this.naviPath = new NaviPath();

		this.m = new NaviLatLng(0.0D, 0.0D);
		this.n = new NaviLatLng(2147483647.0D, 2147483647.0D);

		this.tollCost = 0;
	}

	public List<NaviLatLng> getWayPoints() {
		return this.wayPoints;
	}

	public void setWayPoints(List<NaviLatLng> paramList) {
		this.wayPoints = paramList;
		this.naviPath.setWayPoint(paramList);
	}

	public void setStartPoint(NaviLatLng startPoint) {
		this.startPoint = startPoint;
		this.naviPath.setStartPoint(startPoint);
	}

	public void setEndPoint(NaviLatLng endPoint) {
		this.endPoint = endPoint;
		this.naviPath.setEndPoint(endPoint);
	}

	public NaviLatLng b() {
		return this.m;
	}

	public NaviLatLng c() {
		return this.n;
	}

	public void setCenter(NaviLatLng center) {
		this.center = center;
		this.naviPath.setCenter(center);
	}

	public void setBounds(LatLngBounds latLonBounds) {
		this.bounds = latLonBounds;
		this.naviPath.setBounds(latLonBounds);
	}

	public void setSteps(List<NaviStep> steps) {
		this.steps = steps;
		this.naviPath.setSteps(steps);
	}

	public void setCoordList(List<NaviLatLng> coordList) {
		this.coordList = coordList;
		this.naviPath.setList(coordList);
	}

	public void setRouteLength(int routeLength) {
		this.routeLength = routeLength;
		this.naviPath.setAllLength(routeLength);
	}

	public void setStrategy(int strategy) {
		this.trategy = strategy;
		this.naviPath.setStrategy(strategy);
	}

	public void setRouteTime(int routeTime) {
		this.f = routeTime;
		this.naviPath.setAllTime(routeTime);
	}

	public void setSegNum(int segNum) {
		this.segNum = segNum;
		this.naviPath.setStepsCount(segNum);
	}

	public void setTollCost(int tollCost) {
		this.tollCost = tollCost;
		this.naviPath.setTollCost(this.tollCost);
	}

	public void setByPassLinitedRoad(int byPassLinitedRoad) {
		this.byPassLinitedRoad = byPassLinitedRoad;
		this.naviPath.setByPassLinitedRoad(byPassLinitedRoad);
	}

	public void setDiffToTMCRoute(int diffToTMCRoute) {
		this.diffToTMCRoute = diffToTMCRoute;
		this.naviPath.setDiffToTMCRoute(diffToTMCRoute);
	}

	public void setGroupSegmentList(GroupSegment[] groupSegmentList) {
		this.groupSegmentList = groupSegmentList;
		this.naviPath.setGroupSegmentList(groupSegmentList);
	}

	public void setJamInfoList(JamInfo[] jamInfoList) {
		this.jamInfoList = jamInfoList;
		this.naviPath.setJamInfoList(jamInfoList);
	}

	public void setAvoidJamArea(AvoidJamArea avoidJamArea) {
		this.avoidJamArea = avoidJamArea;
		this.naviPath.setAvoidJamArea(avoidJamArea);
	}

	public void setRouteIncident(RouteIncident[] routeIncident) {
		this.routeIncident = routeIncident;
		this.naviPath.setRouteIncident(routeIncident);
	}
}