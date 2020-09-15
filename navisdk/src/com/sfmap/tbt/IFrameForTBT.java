package com.sfmap.tbt;

/**
 * @brief TBT使用的外部依赖接口
 * TBT调用此接口通知TBT使用者消息，或向使用者查询信息
 */

public interface IFrameForTBT {
	/**
	 * @brief HTTP请求
	 *
	 * TBT调用此接口进行HTTP网络请求，此接口为异步接口，函数调用必须立即返回。
	 * 实现者在外部子线程中完成请求，得到请求结果后调用TBT的receiveNetData将请求结果传入TBT，
	 * 并调用TBT的setNetRequestState告知请求结果状态：成功、或是超时失败、或是其他状态。
	 *
	 * @param moduleID
	 * int 模块号：1 请求路径，2 实施交通信息，3 前方路况播报，4 情报看板，5 整体路况概览，6 路口扩大图 8 ETA统计模块 101 偏航数据回传
	 * @param connectID int 连接ID，请求到数据后用此ID将数据传给TBT
	 * @param type int 0为Post方式，1为Get方式
	 * @param url String 请求的URL串
	 * @param head String HTTP头，默认为空
	 * @param data Post方式的Data数据，默认为空
	 * @param dataLength int Post方式的Data数据长度，默认为0
	 */
	public abstract void requestHttp(
			int moduleID,
			int connectID,
			int type,
			String url,
			String head,
			byte[] data,
			int dataLength
	);

	/**
	 * @brief 通知导航信息更新
	 *
	 * @details 包括当前所在位置信息、导航段信息、电子眼信息、服务区信息等
	 *
	 * @param dgNaviInfo 导航信息
	 * @see DGNaviInfo
	 */
	public abstract void updateNaviInfo( DGNaviInfo  dgNaviInfo );

	/**
	 * @brief 显示路口扩大图
	 *
	 * @details 扩大图有PNG与BMP两种格式，BMP格式通常为矢量扩大图栅格化后得到
	 *
	 * @param picFormat int 图片格式，1 为PNG图片，2 为BMP图片
	 * @param picBuf1
	 * 图片数据1，当图片格式为PNG图片时，此数据为PNG格式的背景数据，若图片格式为BMP时此数据为整个的扩大图的BMP数据
	 * @param picBuf2
	 * 图片数据2，当图片格式为PNG图片时，此数据为PNG格式的箭头数据，若图片格式为BMP时此数据为NULL
	 * @param picSize1
	 * 图片数据大小1，当图片格式为PNG图片时，此大小为pPicBuf1的数据大小，若图片格式为BMP时此数据为扩大图的宽度
	 * @param picSize2
	 * 图片数据大小2，当图片格式为PNG图片时，此大小为pPicBuf2的数据大小，若图片格式为BMP时此数据为扩大图的高度
	 */
	public abstract void showCross(
			int picFormat,
			byte[] picBuf1,
			byte[] picBuf2,
			int picSize1,
			int picSize2
	);

	/**
	 * @brief 隐藏路口扩大图
	 *
	 * @details 路过相应路口，进入下一导航段后隐藏
	 */
	public abstract void hideCross();

	/**
	 * @brief 显示车道信息
	 *
	 * @details 通知外部显示车道信息
	 * @param laneBackInfo 车道背景信息数组, 数组大小为8
	 * @param laneSelectInfo 车道选择信息数组, 数组大小为8
	 */
	public abstract void showLaneInfo(
			byte[] laneBackInfo,
			byte[] laneSelectInfo
	);

	/**
	 * @brief 隐藏车道信息
	 *
	 * @details 路过车道信息所在link，进入下一link后隐藏
	 */
	public abstract void hideLaneInfo();

	/**
	 * @brief 播报字符串
	 *
	 * @details 使用TTS播报一个字符串，此函数调用后应马上返回。
	 * @param soundType 语音类型：
	 ** 1 导航播报
	 ** 2 前方路况播报（含事件播报、情报板播报）
	 ** 4 周边路况播报
	 ** 8 通过提示音播报

	 * @param soundStr 要播报的字符串，Unicode编码
	 */
	public abstract void playNaviSound(
			int soundType,
			String soundStr
	);

	/**
	 * @brief 停止模拟导航
	 *
	 * @details 模拟导航结束后通知Frame以便更新UI界面等
	 */
	public abstract void endEmulatorNavi();

	/**
	 * @brief 通知到达途经点或目的地
	 *
	 * @details 当GPS导航到达途径点或目的地时调用此接口通知Frame
	 * @param wayId int 到达途径点的编号，标号从1开始，如果到达目的地iWayID为0
	 */
	public abstract void arriveWay(
			int wayId
	);

	/**
	 * @brief 通知已经匹配到备选路径
	 *
	 * @details 车辆虽然偏离当前设定的导航路径，但能匹配到其他备选路径上，
	 *
	 * @param routeId 备选路径编号，
	 * @return  1 执行movetoroute，成功响应
	 * 			0 未响应或响应失败
	 */
	public int matchRouteChanged(int routeId);

	/**
	 * @brief 通知GPS导航偏离路径
	 *
	 * @details 当车辆偏离路径后通知Frame，需要重新计算路径； 如通知2 ，客户端勿做语音提示
	 *
	 * @param status  状态。 0：未定义；1：正常行驶时首次偏航；2：处于路径探索状态（偏航后下发的新路径仍然未匹配到路上的偏航）

	 */
	public abstract void offRoute(int status);

	/**
	 * @brief 规避拥堵通知
	 *
	 * 在允许静默算路前提下，TBT在导航路径前方出现拥堵后发出本通知，
	 * 外部可切换导航路径，也可不做响应
	 *
	 * @param routeId 用于规避前方拥堵的新路径编号，
	 * 				  正常情形下，该编号大于0，可在响应时调用TBT的switchNaviRoute切换到新路径导航
	 * @param   savedTime 躲避拥堵路径节省的时间， 单位为秒
	 * @param   preserveParam  保留参数
	 */
	public abstract void rerouteForTMC(
			int routeId, int savedTime, int preserveParam
	);

	/**
	 * @brief 路径销毁
	 *
	 * 停止导航时会删除TBT内部所有路径，此接口通知外部TBT内的路径已经失效，以便外部刷新数据显示。
	 * 外部在收到此消息后，将无法通过TBT获取任何路径相关信息，直至新路径就绪
	 */
	public abstract void routeDestroy();

	/**
	 * @brief 通知当前匹配后的自车位置点
	 *
	 * 外部在收到此消息后，可以使用新的位置点与方向，更新地图中心点与地图角度。
	 * 导航状态下会自动匹配在路上，但偏航或未导航状态位置点不一定在路上
	 * @param carLocation 当前匹配后的点
	 * @see CarLocation
	 */
	public abstract void carLocationChange(
			CarLocation  carLocation
	);

	/**
	 * @brief 通知路径计算状态
	 *
	 * @details 可能的状态有：
	 * *             1   路径计算成功
	 * *             2   网络超时或网络失败
	 * *             3   起点错误
	 * *             4   请求协议非法
	 * *             6   终点错误
	 * *             10  起点找不到道路
	 * *             11  终点找不到道路
	 * *             12  途经点找不到道路
	 * *             other  其他算路错误

	 * @param state 路径计算结果状态
	 */
	public abstract void setRouteRequestState(
			int iModuleID,
			int state
	);

	/**
	 * @brief 通知动态信息改变
	 *
	 * @details 当动态信息更新后会调用此接口，外部如果使用了动态信息，如光柱等，应及时应用新的动态信息，比如刷新光柱显示
	 * @param hour int 更新批次信息，时
	 * @param minute int 更新批次信息，分
	 * @param second int 更新批次信息，秒
	 */
	public abstract void tmcUpdate(int hour, int minute, int second);

	/**
	 * @brief 通知显示情报板
	 *
	 * @details 情报板：一些交通枢纽部位的周边路况展示图
	 * @param panelBuf PNG格式情报板数据
	 */
	public abstract void showTrafficPanel(
			byte[] panelBuf
	);

	/**
	 * @brief 通知隐藏情报板
	 *
	 * @details 路过相应交通枢纽位置后隐藏
	 */
	public abstract void hideTrafficPanel();

	/**
	 * @brief 查询TTS是否正在播报
	 *
	 * @details TBT通过使用此接口查询来修正导航播报时机，返回值是否准确会影响到导航播报的质量。
	若应用层无法获得TTS的状态，返回未知状态
	 * @return int 0 表示未知状态或空闲  1 表示正在播报
	 */
	public abstract int getPlayState();

	/**
	* @brief      锁屏导航提示
	* @details    锁屏状态导航远距离提示点亮屏幕
	* @param      soundStr        通知文字
	* @param      iTurnIcon       拐弯图标，参见DGNaviInfo数据结构中m_iIcon定义
	* @param      iSegRemainLen   当前导航段剩余距离
	* @return     无返回值
	* @see        DGNaviInfo
	*/
	public abstract void lockScreenNaviTips(
			String soundStr,
			int iTurnIcon,
			int iSegRemainLen
	);

	/**
	* @brief      消息通知
	* @details    类型不同，则通知内容的参数不同
	* 通知类型1：iType == 1 收费信息通知
	   iParam1    1表示显示收费信息，0表示隐藏收费信息
	   iParam2    表示收费金额，单位：元
	   strMsg     收费站名称

    * 通知类型2：iType == 2 交通台请求的响应状态通知
    *   iParam1    	请求类型 1 为sdk触发的自动请求 2 为用户手动请求
    *   iParam2    	响应状态 0 正常 1网络请求失败 2网络请求超时 3鉴权失败或是会话超时
    *  						 4 当前处于禁止播报的状态  5 没有可播报路况 6 自动请求时，过滤了重复性质的路况内容

    * 通知类型3：iType == 3 在GPS导航结束时，可以显示统计信息的通知
    *   iParam1    	可以显示与否 1 表示可以显示
    *
    * 通知类型8：iType == 8  特殊语音提示
    *   iParam1    	 提示的类别  1 表示行车提示
    *   iParam2    	 提示的语音编号  1  提示嘟嘟音
    *
	* @param      iType           通知类型
	* @param      iParam1         参数1
	* @param      iParam2         参数2
	* @param      strMsg   		  消息字符串
	* @return     无返回值
	*/
	public abstract void notifyMessage(int iType, int iParam1, int iParam2, String strMsg);

	/**
	* @brief      交通设施信息更新通知
	* @details
	* @param      info  交通设施信息
	* @return     无返回值
	*/
	public abstract void updateTrafficFacility(TrafficFacilityInfo[] info);

	/**
	* @brief      服务区&收费站信息更新通知
	* @details
	* @param      infoArray 服务区&收费站信息数组
	* @return     无返回值
	*/
	public abstract void updateServiceFacility(ServiceFacilityInfo[] infoArray);

	/**
	 *
	 * @param paramArrayOfViewCameraInfo
     */
	public abstract void updateCameraInfo(
			ViewCameraInfo[] paramArrayOfViewCameraInfo);

	/**
	 *
	 * @param paramArrayOfTrackPosition
     */
	public abstract void updateTrack(TrackPosition[] paramArrayOfTrackPosition);

	/**
	 *停止GPS导航
	 *
	 */
	public abstract void endGpsNavi();
}