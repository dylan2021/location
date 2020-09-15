package com.sfmap.tbt;

public class TrackPosition {
	public double m_dLongitude;
	public double m_dLatitude;
	public int m_iCarDir;
	public int m_iSegId;
	public int m_iPointId;

	public TrackPosition() {
		this.m_dLongitude = 0.0D;
		this.m_dLatitude = 0.0D;
		this.m_iCarDir = 0;
		this.m_iSegId = 0;
		this.m_iPointId = 0;
	}
}