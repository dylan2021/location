package com.sfmap.api.navi.model;

public class NaviLocationDecode {
	private NaviLocation mMapNaviLocation;
	private float bearing;
	private float speed;
	private long time;
	private int matchStatus;
	private NaviLatLng coord;

	public NaviLocationDecode() {
		this.mMapNaviLocation = new NaviLocation();
	}

	public NaviLocation getMapNaviLocation() {
		return this.mMapNaviLocation;
	}

	public void setMatchStatus(int matchStatus) {
		this.matchStatus = matchStatus;
		this.mMapNaviLocation.setMatchStatus(matchStatus);
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
		this.mMapNaviLocation.setBearing(bearing);
	}

	public void setSpeed(float speed) {
		this.speed = speed;
		this.mMapNaviLocation.setSpeed(speed);
	}

	public void setTime(long time) {
		this.time = time;
		this.mMapNaviLocation.setTime(time);
	}

	public void setCoord(NaviLatLng naviLatLng) {
		this.coord = naviLatLng;
		this.mMapNaviLocation.setCoord(this.coord);
	}
}