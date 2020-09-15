package com.sfmap.api.navi.model;

public class CarLocation {
	public double m_Longitude; // 自车经度
	public double m_Latitude; // 自车纬度
	public int m_CarDir; // 当前车辆的方向（单位度），以正北为基准，顺时针增加
	public int m_Speed; // 当前自车速度，单位公里每小时
	public int m_MatchStatus; // 匹配状态，0 未匹配到路径上，1 匹配到路径上

//	public long m_timestamp;	// 时间戳（暂以YYYYMMDDhhmmss形式呈现）
//
//	public double m_dOrgLongitude;	// 原始轨迹点坐标（变形后）
//	public double m_dOrgLatitude;	// 原始轨迹点坐标（变形后）
//	public int m_distanceWithRoute;	// 原始轨迹点与道路的最近距离（m）
//	public int m_distanceWithLast;	// 匹配点与上一次匹配点的距离（m）
//
//	public int m_matchRouteIndex;	// 匹配点所在路径索引
//	public int m_matchSegIndex;		// 匹配点所在导航段索引
//	public int m_matchLinkIndex;	// 匹配点所在Link索引
//	public int m_matchSegPointIndex;// 匹配点所在导航段上，上一个形点索引
}