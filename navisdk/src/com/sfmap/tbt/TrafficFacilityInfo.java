package com.sfmap.tbt;

public class TrafficFacilityInfo {
	protected double longitude;
	protected double latitude;
	protected int broadcastType;
	protected int distance;
	protected int limitSpeed;


	public TrafficFacilityInfo() {
		this.longitude = -1.0D;
		this.latitude = -1.0D;
		this.broadcastType = -1;
		this.distance = 0;
		this.limitSpeed = 0;
	}

	public int getLimitSpeed() {
		return this.limitSpeed;
	}

	public void setLimitSpeed(int paramInt) {
		this.limitSpeed = paramInt;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double paramDouble) {
		this.longitude = paramDouble;
	}

	public int getDistance() {
		return this.distance;
	}

	public void setDistance(int paramInt) {
		this.distance = paramInt;
	}

	public int getBroardcastType() {
		return this.broadcastType;
	}

	public void setBroardcastType(int paramInt) {
		this.broadcastType = paramInt;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double paramDouble) {
		this.latitude = paramDouble;
	}
}