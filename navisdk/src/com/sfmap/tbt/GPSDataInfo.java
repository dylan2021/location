package com.sfmap.tbt;

/**
 * brief      GPS信息结构体
 */
public class GPSDataInfo {
	public double lon; // !< 经度, 单位度 (正值为东经, 负值为西经)
	public double lat; // !< 纬度, 单位度 (正值为北纬, 负值为南纬)
	public short speed; // !< 速度, 单位千米/时
	public short angle; // !< 方向角, 单位度
	public short year; // !< GPS(BJ)时间－－年
	public short month; // !< GPS(BJ)时间－－月
	public short day; // !< GPS(BJ)时间－－日
	public short hour; // !< GPS(BJ)时间－－时
	public short minute; // !< GPS(BJ)时间－－分
	public short second; // !< GPS(BJ)时间－－秒
}