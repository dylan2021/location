package com.sfmap.tbt;

/**
 * brief 定义服务区，收费站等设施信息结构体
 */

public class ServiceFacilityInfo {
	public int remainDist; // /< 到当前车位的剩余距离（单位米）
	public int type; // /< 设施类型 0表示服务区 1表示收费站
	public String name; // /< 名称，Unicode编码方式
	public double lon; // /< 设施所在地经度
	public double lat; // /< 设施所在地纬度

	public ServiceFacilityInfo() {
		remainDist = -1;
		type = 0;
		name = "";
		lon = 0;
		lat = 0;
	}
}