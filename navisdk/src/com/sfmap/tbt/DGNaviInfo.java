package com.sfmap.tbt;

/********************************************************************
 * 导航段转向图标定义如下： 1 // 自车图标 2 // 左转图标 3 // 右转图标 4 // 左前方图标 5 // 右前方图标 6 // 左后方图标 7
 * // 右后方图标 8 // 左转掉头图标 9 // 直行图标 10 // 到达途经点图标 11 // 进入环岛图标 12 // 驶出环岛图标 13 //
 * 到达服务区图标 14 // 到达收费站图标 15 // 到达目的地图标Serializable 16 // 进入隧道图标
 ********************************************************************/
public class DGNaviInfo {
	public int m_Type;
	// ! 当前道路名称
	public String m_CurRoadName;
	// ! 下条道路名称
	public String m_NextRoadName;
	// ! 距离最近服务区的距离（单位米），若为-1则说明距离下一个服务区还远，或是路上没有服务区
	public int m_SAPADist;
	// ! 服务区类型　0 高速路服务区　1 其他服务区
	public int m_SAPAType;
	// ! 距离最近电子眼距离（单位米），若为-1则说明距离下一个电子眼还远，或是路上没有电子眼
	public int m_CameraDist;
	// ! 电子眼类型，0 测试摄像头，1为监控摄像头
	public int m_CameraType;
	// ! 电子眼限速，若无限速信息则为0
	public int m_CameraSpeed;
	// ! 电子眼在路径上的编号, 总是指下一个将要路过的电子眼的编号，若为-1则路上没有电子眼
	public int m_CameraIndex;
	// ! 导航段转向图标
	public int m_Icon;
	// ! 路径剩余距离（单位米）
	public int m_RouteRemainDis;
	// ! 路径剩余时间（单位秒）
	public int m_RouteRemainTime;
	// ! 当前导航段剩余距离（单位米）
	public int m_SegRemainDis;
	// ! 当前导航段剩余时间（单位秒）
	public int m_SegRemainTime;
	// ! 自车方向（单位度），以正北为基准，顺时针增加
	public int m_CarDirection;
	// ! 自车经度
	public double m_Longitude;
	// ! 自车纬度
	public double m_Latitude;
	// ! 当前道路速度限制（单位公里每小时）
	public int m_LimitedSpeed;
	// ! 当前自车所在segment段，从0开始
	public int m_CurSegNum;
	// ! 当前自车所在Link，从0开始
	public int m_CurLinkNum;
	// ! 当前位置的前一个形状点号，从0开始
	public int m_CurPointNum;

	public void releaseResource() {
		m_CurRoadName = null;
		m_NextRoadName = null;
		m_SAPADist = -1;
		m_CameraDist = -1;
		m_RouteRemainTime = 0;
		m_CameraType = -1;
		m_CameraIndex = -1;
		m_SegRemainDis = -1;
		m_SegRemainTime = 0;
		m_RouteRemainDis = 0;
		m_Icon = 0;
		m_Longitude = 0;
		m_Latitude = 0;
		m_CameraSpeed = 0;
		m_LimitedSpeed = 0;
		m_CurSegNum = 0;
		m_CurLinkNum = 0;
		m_CurPointNum = 0;
		m_SAPAType = 1;
	}
}