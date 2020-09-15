package com.sfmap.tbt;

/**
 * 导航操作实现类，和底层交互的接口类
 */

/**
 * @brief TBT接口类
 * 此类定义了TBT SDK的所有功能，请严格按照接口描述使用
 */

public class TBT implements NaviOperDecode {
	public static final int ERROR_STATE_ENV_FAILED = 0;
	public static final int SUCCEED_STATE = 1;
	public static final int ERROR_STATE_PARAM_INVALID = 2;
	public static final int ERROR_STATE_StartPointFalse = 3;
	public static final int ERROR_STATE_IllegalRequest = 4;
	public static final int ERROR_STATE_CallCenterError = 5;
	public static final int ERROR_STATE_EndPointFalse = 6;
	public static final int ERROR_STATE_EncodeFalse = 7;
	public static final int ERROR_STATE_LackPreview = 8;
	public static final int ERROR_STATE_DataBufError = 9;
	public static final int ERROR_STATE_StartNoRoad = 10;
	public static final int ERROR_STATE_EndNoRoad = 11;
	public static final int ERROR_STATE_HalfwayNoRoad = 12;
	public static final int ERROR_STATE_RouteFail = 13;
	public static final int ERROR_STATE_FORBID = 100;
	public static final int OFFLINE_DATAMANAGER = 1;
	public static final int VERSION_GET_OP = 1;
	public static final int DATA_ADD_OP = 2;
	public static final int DATA_UPDAE_OP = 3;
	public static final int DATA_FINISH_OP = 4;
	public static final int DATA_DELETE_OP = 5;

	/**
	 * @brief 初始化TBT
	 *
	 * 在使用TBT接口前必须先调用此接口对TBT进行初始化，初始化成功后才能使用TBT
	 * @param frame TBT依赖的外部接口，TBT通过此接口和外部交互
	 * @param workPath 工作路径，TBT的一些配置文件以及输出信息等资料将保存到此路径下
	 * @param usercode 用户类型编码，从官网申请得到
	 * @param userbatch 用户批次号，默认为0. 从官网申请得到
	 * @param deviceID 设备号，设备号必须唯一，可以是硬件设备号或UUID等方式生成
	 * @return  0 初始化失败，1 初始化成功
	 * @see IFrameForTBT
	 * @warning  deviceID 唯一性类似网卡物理地址一样，每个设备固定且各设备唯一
	 */
	public native int init(
			IFrameForTBT frame,
			String workPath,
			String usercode,
			String userbatch,
			String deviceID
	);

	/**
	 * @brief 销毁TBT
	 *
	 * 系统退出时调用此接口释放TBT资源，在调用此接口后不能再调用TBT的其它接口。
	 *
	 */
	public native void destroy();

	/**
	 * @brief 获得TBT的版本号
	 *
	 * TBT每次发布，版本号都会不同，此信息用于问题调查时的版本确定
	 *
	 * @return TBT的版本号，例如“BASE_2.5.0_12-10-15_1”
	 */
	public native String getVersion();

	/**
	 * @brief 开始模拟导航
	 *
	 * 收到路径计算成功消息后可以通过此接口开始模拟导航，
	 * 如果当前有多路径在开始导航前必须先调用SelectRoute选择使用那条路模拟导航
	 *
	 * @return i 0 失败， 1 成功
	 */
	public native int startEmulatorNavi();

	/**
	 * @brief 开始GPS导航
	 *
	 * 收到路径计算成功消息后可以通过此接口开始GPS导航，
	 * 如果当前有多路径在开始导航前必须先调用SelectRoute选择使用那条路模拟导航
	 * @return i 0 失败， 1 成功
	 */
	public native int startGPSNavi();

	/**
	 * @brief 停止模拟导航
	 *
	 * 调用此接口停止导航，若在开始模拟导航前已经开始了GPS导航，则继续GPS导航
	 *
	 */
	public native void stopEmulatorNavi();

	/**
	 * @brief 暂停导航
	 *
	 * 暂停导航后将不再有导航播报
	 *
	 */
	public native void pauseNavi();

	/**
	 * @brief 恢复导航
	 */
	public native void resumeNavi();

	/**
	 * @brief 结束导航
	 *
	 * 调用该接口后会停止导航并删除TBT内的所有路径。
	 *
	 */
	public native void stopNavi();

	/**
	 * @brief         手动刷新路况信息
	 *
	 * @details       调用后会立即触发一次光柱信息请求，结果返回后，TBT会通知外部更新光柱
	 * @return        无返回值
	 */
	public native void manualRefreshTMC();

	/**
	 * @brief 手动播报导航提示
	 *
	 * 如果正在播报导航信息，则返回失败。
	 *
	 * @return i 0失败，1成功
	 */
	public native int playNaviManual();

	/**
	 * @brief 手动播报前方路况
	 *
	 * 此接口需要向服务器请求最新的前方路况，不一定请求成功，失败会有相应的语音提示
	 *
	 * @param frontDistance i 要播报的前方路径距离，0为SDK默认距离，-1为整条路径 ，
	 * 一般的前方路况播报设置为0就可以了
	 * @return i 0失败，1成功
	 */
	public native int playTrafficRadioManual(
			int frontDistance
	);

	/**
	 * @brief 创建一个光柱
	 * @param startPos int 光柱范围在路径中的起始位置
	 * @param distance int 光柱范围长度
	 * @return 成功返回光柱数据列表，@see TMCBarItem 结构,失败返回NULL
	 */
	public native TmcBarItem[] createTmcBar(
			int startPos,
			int distance
	);

	/**
	 * @brief 设置GPS信息
	 *
	 * TBT初始化成功后，只要GPS有效就可以调用此接口将GPS设置给TBT，
	 * 直到TBT被销毁，不论是否有路径，是否开始导航
	 *
	 * @param offsetFlag i 是否偏转标识，1无偏转，2有偏转
	 * @param longitude d 经度
	 * @param latitude d 纬度
	 * @param speed d 速度（单位km/h）
	 * @param direction d 方向，单位度，以正北为基准，顺时针增加
	 * @param year i 年
	 * @param month i 月
	 * @param day i 日
	 * @param hour i 时
	 * @param minute i 分
	 * @param second i 秒
	 * @param type i 定位类型
	 * @param precision i 精度
	 * @warning  速度单位是km/h，注意和原始gps速度单位不同
	 */
	public native void setGPSInfo(
			int offsetFlag,
			double longitude,
			double latitude,
			double speed,
			double direction,
			int year,
			int month,
			int day,
			int hour,
			int minute,
			int second
//			int type,
//			double precision
	);

	/**
	 * @brief 设置自车初始位置
	 *
	 * 在系统初始化时，以及非TBT通知的位置变化时，调用此接口通知TBT自车位置改变
	 *
	 * @param offFlag i 是否有偏移，1 没偏移，2 有偏移(一般直接从GPS接收过来的位置是没偏转的，从TBT得到的位置都是偏转过的)
	 * @param longitude d 自车经度
	 * @param latitude d 自车纬度
	 */
	public native void setCarLocation(
			int offFlag,
			double longitude,
			double latitude,
			float forwardDir
	);

	///@{
	/**
	 * @brief 请求路径，推荐的算路方式，以当前自车位置为起点
	 *
	 * 调用此接口前需要调用过SetGPSInfo设置过GPS位置或调用过SetCarLocation设置过自车当前位置，以便TBT知道算路的起点

	 * 网络算路时，路径类型值定义如下：
	 * *     0   速度优先（推荐道路）
	 * *     1   费用优先（尽量避开收费道路）
	 * *     2   距离优先（距离最短）
	 * *     3   普通路优先（不走快速路，包含高速路）
	 * *     4   考虑实时路况的路线（时间优先）
	 * *     5   多路径（一条速度优先路线，一条考虑实时交通路况路线）
	 * *     12  TMC最快且不走收费道路

	本地算路时（flag需要组合0x100），路径类型值定义如下：
	 * *     0   最优策略
	 * *     1   高速优先， 注意和网络算路同编号策略含义不同
	 * *     2   距离优先
	 * *     3   普通路优先

	 * @param calcType  路径类型
	 * @param flag      附加要求flag，默认为0，可以是多个标志位的组合;
	- 0x01 代表不走高速;
	- 0x04 代表启用备选路径，备选路径一般是3条，但有可能存在相同路径.
	TBT默认排除重复路径，如希望保留重复路径，组合不排除重复路径标志位
	- 0x08 代表不排除重复路径（启用备选路径时）
	- 0x20 代表本次算路不清理前次导航统计结果
	- 0x100 代表请求本地路径

	 * @param endNum    终点个数, 如果有多个点，则为POI多终点算路
	 * @param endPos    终点经纬度序列，多终点模式则第一个点为POI显示坐标，其余为POI出口终点坐标
	 * @param passNum   途径点个数，可以为0，表示没有途径点
	 * @param passPos   途径点经纬度序列，没有途径点时为null
	 * @return 0失败，1成功
	 */
	public native int requestRoute(
			int calcType,
			int flag,
			float forwardDir,
			int endNum,
			double[] endPos,
			int passNum,
			double[] passPos
	);

	/**
	 * @brief 带起点的路径请求
	 *
	 * 此接口一般用于路径浏览，而非导航
	 * 网络算路时，路径类型值定义如下：
	 * *     0   速度优先（推荐道路）
	 * *     1   费用优先（尽量避开收费道路）
	 * *     2   距离优先（距离最短）
	 * *     3   普通路优先（不走快速路，包含高速路）
	 * *     4   考虑实时路况的路线（时间优先）
	 * *     5   多路径（一条速度优先路线，一条考虑实时交通路况路线）
	 * *     12  TMC最快且不走收费道路

	本地算路时（flag需要组合0x100），路径类型值定义如下：
	 * *     0   最优策略
	 * *     1   高速优先， 注意和网络算路同编号策略含义不同
	 * *     2   距离优先
	 * *     3   普通路优先

	 * @param calcType  路径类型
	 * @param flag      附加要求flag，默认为0，可以是多个标志位的组合;
	- 0x01 代表不走高速;
	- 0x02 代表启用POI多起点算路模式
	- 0x04 代表启用备选路径，备选路径一般是3条，但有可能存在相同路径.
	TBT默认排除重复路径，如希望保留重复路径，组合不排除重复路径标志位
	- 0x08 代表不排除重复路径（启用备选路径时）
	- 0x20 代表本次算路不清理前次导航统计结果
	- 0x100 代表请求本地路径

	 * @param startNum  起点个数
	 * @param startPos  起点经纬度序列
	 * @param endNum    终点个数, 如果有多个点，则为POI多终点算路
	 * @param endPos    终点经纬度序列，多终点模式则第一个点为POI显示坐标，其余为POI出口终点坐标
	 * @param passNum   途径点个数，可以为0，表示没有途径点
	 * @param passPos   途径点经纬度序列，没有途径点时为null
	 * @return 0失败，1成功
	 */
	public native int requestRouteWithStart(
			int calcType,
			int flag,
			float forwardDir,
			int startNum,
			double[] startPos,
			int endNum,
			double[] endPos,
			int passNum,
			double[] passPos
	);

	/**
	 * 带避道路的路径请求
	 * @param iCalcType 路径类型
	 * @param iFlag    附加要求flag，默认为0，可以是多个标志位的组合;
	- 0x01 代表不走高速;
	- 0x02 代表启用POI多起点算路模式
	- 0x04 代表启用备选路径，备选路径一般是3条，但有可能存在相同路径.
	TBT默认排除重复路径，如希望保留重复路径，组合不排除重复路径标志位
	- 0x08 代表不排除重复路径（启用备选路径时）
	- 0x20 代表本次算路不清理前次导航统计结果
	- 0x100 代表请求本地路径
	 * @param iStartPosNum 起点个数
	 * @param pdStartPos  起点经纬度序列
	 * @param iEndPosNum  终点个数, 如果有多个点，则为POI多终点算路
	 * @param pdEndPos    终点经纬度序列，多终点模式则第一个点为POI显示坐标，其余为POI出口终点坐标
	 * @param iPassPosNum  途径点个数，可以为0，表示没有途径点
	 * @param pdPassPos   途径点经纬度序列，没有途径点时为null
     * @param pcRoadName 避让道路名称
     * @return 0失败，1成功
     */
	public native int requestRouteWithAvoidRoad(int iCalcType,
												int iFlag,
												float forwardDir,
												int iStartPosNum,
												double[] pdStartPos,
												int iEndPosNum,
												double[] pdEndPos,
												int iPassPosNum,
												double[] pdPassPos,
												String pcRoadName);

	/**
	 * 带避让区域的路径请求
	 * @param iCalcType 路径类型
	 * @param iFlag    附加要求flag，默认为0，可以是多个标志位的组合;
	- 0x01 代表不走高速;
	- 0x02 代表启用POI多起点算路模式
	- 0x04 代表启用备选路径，备选路径一般是3条，但有可能存在相同路径.
	TBT默认排除重复路径，如希望保留重复路径，组合不排除重复路径标志位
	- 0x08 代表不排除重复路径（启用备选路径时）
	- 0x20 代表本次算路不清理前次导航统计结果
	- 0x100 代表请求本地路径
	 * @param iStartPosNum 起点个数
	 * @param pdStartPos  起点经纬度序列
	 * @param iEndPosNum  终点个数, 如果有多个点，则为POI多终点算路
	 * @param pdEndPos    终点经纬度序列，多终点模式则第一个点为POI显示坐标，其余为POI出口终点坐标
	 * @param iPassPosNum  途径点个数，可以为0，表示没有途径点
	 * @param pdPassPos   途径点经纬度序列，没有途径点时为null
	 * @param iAvoidAreaCount  避让区域数量
	 * @param pAvoidArea 避让区域
     * @return
     */
	public native int requestRouteWithAvoidAreas(int iCalcType,
											 int iFlag,
											 float forwardDir,
											 int iStartPosNum,
											 double[] pdStartPos,
											 int iEndPosNum,
											 double[] pdEndPos,
											 int iPassPosNum,
											 double[] pdPassPos,
											 int iAvoidAreaCount,
											 MPolygon[] pAvoidArea);

	/**
	 * @brief 接收网络数据
	 *
	 * 网络请求有数据返回时调用此接口，如没有数据返回，调用setNetRequestState通知请求失败。
	 * @param moduleID i 模块号，必须和TBT请求时传入的模块号一致
	 * @param connectID i 连接号，必须和TBT请求时传入的连接号一致
	 * @param data 下载的数据
	 * @param length 数据长度
	 */
	public native void receiveNetData(
			int moduleID,
			int connectID,
			byte[] data,
			int length
	);

	/**
	 * @brief 接收网络数据
	 *
	 * 网络请求有数据返回时调用此接口，如没有数据返回，调用setNetRequestState通知请求失败。
	 * @param moduleID i 模块号，必须和TBT请求时传入的模块号一致
	 * @param connectID i 连接号，必须和TBT请求时传入的连接号一致
	 * @param data1 下载的数据1
	 * @param length1 数据1长度
	 * @param data2 下载的数据2
	 * @param length2 数据2长度
	 */
	public native boolean fuseNewData(
			int moduleID,
			int connectID,
			byte[] data1,
			int length1,
			byte[] data2,
			int length2
	);

	/**
	 * @brief 设置网络请求状态
	 *
	 * 使用者通过此接口通知TBT网络请求结果，无论成败都需调用此接口通知TBT，
	 * TBT在收到此消息后才认为一次HTTP请求结束，应确保每一次HTTP网络请求都有回调本函数。

	 * 请求结果状态有如下可能：
	 * *  1 请求成功
	 * *  2 请求失败
	 * *  3 请求超时
	 * *  4 用户手动结束请求
	 *
	 * @param moduleID i 模块号，必须和TBT请求时传入的模块号一致
	 * @param connectID i 连接号，必须和TBT请求时传入的连接号一致
	 * @param netState 请求结果状态
	 */
	public native void setNetRequestState(
			int moduleID,
			int connectID,
			int netState
	);


	public native int setPassPoint(double passPos,int passPosNum);
	public native int setEndPoints(double endPos);



	/**
	 * @brief 重新计算路径
	 *
	 * 当TBT通知偏离导航路径时可回调本函数，也支持用户手动重算路。

	 * @param calcType i 路径类型
	 * * -1   沿用重算前路径策略
	 * * 0    速度优先（推荐道路）
	 * * 1    费用优先（尽量避开收费道路）
	 * * 2    距离优先（距离最短）
	 * * 3    普通路优先（不走快速路，包含高速路）
	 * * 4    考虑实时路况的路线（时间优先）

	 * @param  flag 路径定制flag，默认为0，可以是多个标志位的组合
	- flag含义参见RequestRoute

	 * @return 0失败，1成功
	 */
	public native int reroute(int calcType, int flag);

	/**
	 * @brief 切换到新的静默算路结果路径
	 *
	 * 若切换前已经开始GPS导航，切换后将自动开始GPS导航
	 *
	 * @param routeID
	 * @return i 0失败，1成功
	 */
	public native int switchNaviRoute(int routeID);

	/**
	 * @brief 切换平行路
	 *
	 * 此函数只有在GPS导航开始后才能使用，用来将路径的起点切换到当前导航路径平行的其它路径上，
	 * 例如当前路径在主路上，调用此接口将把路径切换到辅路上，
	 * 如果当前道路周围没有平行道路，则路径不变，切换成功后将自动开始导航
	 */
//	public native void switchParallelRoad();
	public native void switchParallelRoad(int startPointFlag);

	public native int getLinkAttributeInfo(int iSegIndex, int iLinkIndex);

	/**
	 * @brief 将路径Push到TBT中
	 *
	 * 如果正在导航，调用此方法将停止导航，并删除现有路径，成功后与正常算路成功后一样进行导航与数据访问
	 *
	 * @param calcType  路径类型
	 * @param flag      路径附加要求flag，默认为0，可以是多个标志位的组合(0x01 代表不走高速)
	 * @param data      路径二进制数据
	 * @param length    数据长度
	 * @return 0失败，1成功
	 * @warning flag 定义同requestRoute中的flag，可能与TBT外部算路自用的flag不同，切勿混淆，生搬套用。
	 */
	public native int pushRouteData(int calcType, int flag, byte[] data, int length);
	///@}

	///@{
	/**
	 * @brief 获得TBT中的所有路径的ID
	 *
	 * 此接口一般在路径计算成功后，或PushRouteData成功后用来获得路径的ID，以便之后选择并操作路径
	 *
	 * @return 路径ID的列表，每一个路径ID是一个Int的数值
	 */
	public native int[] getAllRouteID();

	/**
	 * @brief 选择路径
	 *
	 * 在路径计算完成，或PushRouteData完成后，导航或访问路径数据前必须调用此接口选择路径
	 *
	 * @param routeID i 要选择道路的ID
	 * @return i 失败返回-1，成功返回选择被选中道路的ID
	 */
	public native int selectRoute(
			int routeID
	);

	/**
	 * @brief 获得当前路径的策略
	 *
	 * @return 成功返回路径策略，否则返回-1
	 */
	public native int getRouteStrategy();

	/**
	 * @brief 获得当前路径的长度
	 *
	 * @return 成功返回路径长度，单位：米，否则返回-1
	 */
	public native int getRouteLength();

	/**
	 * @brief 获得当前路径的旅行时间
	 * @return 成功返回路径的旅行时间，单位：秒，否则返回-1
	 */
	public native int getRouteTime();

	/**
	 * @brief 获得当前路径的导航段个数
	 * @return 成功返回导航段个数，否则返回-1
	 */
	public native int getSegNum();

	/**
	 * @brief 获得一个导航段的长度
	 *
	 * @param segIndex int 导航段编号，从0开始编号
	 * @return int 成功返回导航段的长度，单位：米。否则返回-1
	 */
	public native int getSegLength(
			int segIndex
	);

	/**
	 * @brief 获得导航段的旅行时间
	 * @param segIndex int 导航段编号，从0开始编号
	 * @return 成功返回导航段的旅行时间，单位：秒。否则返回-1
	 */
	public native int getSegTime(
			int segIndex
	);

	/**
	 * @brief 获得一个导航段中Link的数量
	 * @param segIndex int 导航段编号，从0开始编号
	 * @return int 当iSegIndex小于总导航个数时返回Link个数，否则返回-1
	 */
	public native int getSegLinkNum(
			int segIndex
	);

	/**
	 * @brief 获得一个导航段的收费路长度
	 *
	 * @param segIndex int 导航段编号，从0开始编号
	 * @return int 成功返回导航段中收费道路的长度，单位：米。否则返回-1
	 */
	public native int getSegChargeLength(
			int segIndex
	);

	/**
	 * @brief 获得一个导航段的收费金额
	 *
	 * @param segIndex int 导航段编号，从0开始编号
	 * @return int 成功返回导航段中收费金额，单位：元。否则返回-1
	 */
	public native int getSegTollCost(
			int segIndex
	);

	/**
	 * @brief 获得一个导航段的形状点
	 * @param segIndex int 导航段编号，从0开始编号
	 * @return 返回形状点列表
	 */
	public native double[] getSegCoor(
			int segIndex
	);

	/**
	 * @brief 获得一个Link的道路名称
	 *
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return 返回Link的名称
	 */
	public native String getLinkRoadName(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link的形状坐标点
	 *
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return 返回Link的形状点序列
	 */
	public native double[] getLinkCoor(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link的长度（单位米）
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return int 成功返回Link的长度，否则返回-1
	 */
	public native int getLinkLength(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link的旅行时间（单位秒）
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return int 成功返回Link的旅行时间，否则返回-1
	 */
	public native int getLinkTime(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link的FormWay

	 * FormWay定义如下
	 * * 1    主路
	 * * 2    路口内部道路
	 * * 3    JCT道路
	 * * 4    环岛
	 * * 5    服务区
	 * * 6    匝道
	 * * 7    辅路
	 * * 8    匝道与JCT
	 * * 9    出口
	 * * 10   入口
	 * * 11   A类右转专用道
	 * * 12   B类右转专用道
	 * * 13   A类左转专用道
	 * * 14   B类左转专用道
	 * * 15   普通道路
	 * * 16   左右转专用道
	 * * 56   服务区与匝道
	 * * 53   服务区与JCT
	 * * 58   服务区与匝道以及JCT

	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return int 成功返回Link的FormWay信息，否则返回-1
	 */
	public native int getLinkFormWay(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link的RoadClass

	 * RoadClass定义如下
	 * * 0  高速公路
	 * * 1  国道
	 * * 2  省道
	 * * 3  县道
	 * * 4  乡公路
	 * * 5  县乡村内部道路
	 * * 6  主要大街、城市快速道
	 * * 7  主要道路
	 * * 8  次要道路
	 * * 9  普通道路
	 * * 10 非导航道路

	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return int 成功返回Link的RoadClass信息，否则返回-1
	 */
	public native int getLinkRoadClass(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link的LinkType
	 * LinkType定义如下
	 * * 0  普通道路
	 * * 1  航道
	 * * 2  隧道
	 * * 3  桥梁

	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return int 成功返回Link的LinkType信息，否则返回-1
	 */
	public native int getLinkType(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得link是否有岔路
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return {int} 0 无岔路 1 有岔路
	 */
	public native int getLinkIsBranched(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得对应link是否存在出口或入口的状态
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return {int} 0 普通link 1 带有出口或入口
	 */
	public native int getLinkIOFlag(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得link所在的城市编码
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return {int} 城市编码，有效编码是个6位数，获取失败返回-1。
	 */
	public native int getLinkCityCode(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个Link上是否有红绿灯
	 *
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int Link编号，从0开始编号
	 * @return int 0：无红绿灯，1：有红绿灯，失败返回-1
	 */
	public native int haveTrafficLights(
			int segIndex,
			int linkIndex
	);

	/**
	 * @brief 获得一个LocationCode的状态
	 * @param segIndex int 导航段编号，从0开始编号
	 * @param linkIndex int 要查询的Link编号，从0开始编号
	 * @return 成功返回Link信息，否则返回null
	 * @see LinkStatus
	 */
	public native LinkStatus getLinkTrafficStatus(
			int segIndex,
			int linkIndex,
			int iTrafficIndex
	);

	/**
	 *  获得一个Link的不同路况数量
	 * @param iSegIndex int 导航段编号,从0开始编号
	 * @param iLinkIndex int 要查询的Link编号,从0开始编号
     * @return 成功返回Link路况数量,否则返回0
     */
	public native int getLinkTrafficStatusCount(int iSegIndex,
												int iLinkIndex);

	/**
	 * 获得整条路径的聚合段信息列表
	 * 列表数目为聚合段个数。
	 * @return 聚合段信息列表，失败返回NULL
	 * @see GroupSegment
	 */
	public native GroupSegment[] getGroupSegmentList();

	/**
	 * @brief 获得行程Guide列表
	 *
	 * 列表数目为导航段的个数。
	 *
	 @return 行程列表
	 @see NaviGuideItem
	 */
	public native NaviGuideItem[] getNaviGuideList();

	/**
	 * @brief 获得整条路径的电子眼列表
	 *
	 @return 返回电子眼列表，失败返回NULL
	 @see Camera
	 */
	public native Camera[] getAllCamera();

	/**
	 * @brief 获得起点位置后，指定数目的服务区列表
	 *
	 * @param nStartPos int 起始位置，相对于路径起点的距离，为0表示获取全路径
	 * @param nNeedNum int 想要获取的服务区数目，为0表示剩余部分的全部
	 @return 返回服务区列表，失败返回NULL
	 @see Camera
	 */
	public native RestAreaInfo[] getRestAreas(int nStartPos, int nNeedNum);

	/**
	 * @brief 获取最近一次导航的统计信息
	 *
	 * @warning 只能获取已经完成的导航数据, 即使处在新导航中，也只能取到前一次导航统计信息
	 @return 统计信息，失败返回NULL
	 @see NaviStaticInfo
	 */
	public native NaviStaticInfo getNaviStaticInfo();

	///@}

	///@{
	/**
	 * @brief 打开动态交通
	 *
	 * 需要动态交通信息相关功能时需要调用此接口打开交通信息功能，默认动态交通信息是关闭的，
	 * 打开后会每2分钟请求一次新的交通信息来更新交通信息
	 */
	public  native void openTMC();

	/**
	 * @brief 关闭动态交通
	 *
	 * 关闭动态交通信息后使用交通信息的相关信息的功能将无法正常使用，关闭后不再进行TMC信息请求
	 */
	public  native void closeTMC();

	/**
	 * @brief 打开移动交通台功能
	 *
	 * 打开此功能后将有整体路况概览与前方路况播报功能，打开此功能后TBT会基本1分钟请求
	 * 一次前方路况（此时间间隔不是固定的）
	 */
	public  native void openTrafficRadio();

	/**
	 * @brief 关闭移动交通台功能
	 *
	 * 关闭交通台后将没有路况播报功能
	 */
	public  native void closeTrafficRadio();

	/**
	 * @brief 打开情报板功能
	 *
	 * 在打开了移动交通台的时候，可以通过此接口打开情报板功能，默认为关闭状态，
	 * 打开状态下当车辆前方有情报板时，请求网络下载当前位置的情报板，并通知Frame显示
	 */
	public native void openTrafficPanel();

	/**
	 * @brief 关闭情报板功能
	 */
	public native void closeTrafficPanel();

	/**
	 * @brief 打开电子眼播报
	 *
	 * 初始电子眼播报是打开的，当电子眼播报打开时，电子眼数据中有限速信息时，同时会播报限速信息
	 */
	public native void openCamera();

	/**
	 * @brief 关闭电子眼播报
	 */
	public native void closeCamera();

	/**
	 * @brief 设置模拟导航的速度
	 *
	 * 需要设置模拟导航速度时调用
	 *
	 * @param emulatorSpeed i 模拟导航速度，单位km/h
	 */
	public native void setEmulatorSpeed(
			int emulatorSpeed
	);

	/**
	 * @brief 设置TTS每播报一个字需要的时间
	 *
	 * TBT会用这个值来计算播报语音，为了获得更好的播报效果，在导航前必须调用此接口来设置比较精确的值
	 *
	 * @param useTime i 播报每个字需要的时间，单位为毫秒
	 */
	public native void setTimeForOneWord(
			int useTime
	);

	/**
	 * @brief 设置TMC拥堵重算策略

	 * * 1 满足拥堵条件， 当前路况耗时比算路时要长，自动算路后新路比旧路节省时间则通知
	 * * 2 满足拥堵条件， 不考虑时间，自动算路后有不同则通知

	 * @param strategy int 重算策略
	 * @return void
	 * @see   requestRoute
	 */
	public native void setTMCRerouteStrategy(int strategy);

	/**
	 * @brief 设置路口放大图显示模式
	 * @param mode int 显示模式: 0 不显示, 1 栅格, 2 矢量
	 * @return void
	 */
	public native void setCrossDisplayMode(int mode);

	/**
	 * @brief      定制参数设置，用以接受客户相关的定制信息

	 * @details    企业用户需要设置所授权的用户名，密码等信息，方能正常使用TBT所用服务
	例如 tbt.setParam("userid", "XX");
	tbt.setParam("userpwd", "XXX");

	 * @param      strName String  参数名称
	 * @param      strVal  String  参数值
	 * @return     是否设定成功
	 */
	public native int setParam(String strName, String strVal);

	/**
	 @brief          获得最近几个符合规则的gps点
	 @details
	 0.  第0个点取距当前时间最近的点，即最新gps点
	 1.	第1个点取距离最新的GPS点nDistThres米外的点；
	 2.	第2个点取距离上个点nDistThres外的点（以此类推，取count个点）；
	 3.	如果只有一个点满足距离要求，返回已有gps序列；
	 4.	如果没有任何点满足距离要求，则做速度判断：按时间倒退顺序取count个速度大于等于nSpeedThres的点；
	 5.	如果只有一个点满足速度要求，返回已有gps序列；
	 6.	如果无任何点满足距离及速度要求，第1个点取距离最新GPS点最远的点。

	 @param[in]      nDistThres 距离阈值，默认取30m
	 @param[in]      nSpeedThres 速度阈值，默认取4km/h
	 @param[in]  	nCount  希望取得点数，注意实际取到的点数可能小于它或者取点失败
	 @return         取到的gps数据数组
	 @warning        返回数组的长度有可能不足指定个数，甚至为空（没有gps积累时）
	 */
	public native GPSDataInfo[] getRecentGPS(int nDistThres, int nSpeedThres, int nCount);

	///@}

	/*!
	 * \brief  相比当前路径，是否存在更省时间的tmc路径，能节省多少时间
	 * \return 存在更好的tmc路径则返回节省多少时间(分钟)，反之返回0
	 */
	public native int getDiffToTMCRoute();

	/*!
	 * \brief  当前路径是否已规避限行路段
	 * \return 已规避则返回1，反之返回0
	 */
	public native int getBypassLimitedRoad();

	/*!
	 * \brief  获得导航段主要收费道路名称
	 * \param[in] segIndex 导航段编号，从0开始编号
	 * \return 主要收费道路名称，失败返回NULL
	 */
	public native String getSegTollPathName(int segIndex);

	/*!
	 * \brief  获得目的地在相对于道路的位置关系
	 * \return 位置关系 0 在路上 1 在道路左侧 2 在道路右侧  -1 失败
	 */
	public native int getDestDirection();

	/*!
	 * \brief  获得路径起点位置，路径数据的首点
	 * \return 起点位置
	 */
	public native GeoPoint getRouteStartPos();

	/*!
	 * \brief  获得路径终点位置，路径数据的尾点
	 * \return 终点位置
	 */
	public native GeoPoint getRouteEndPos();

	/*!
	 * \brief  获得路径途径点位置，各途径点所在导航段的尾点
	 * \return 途径点对象数组 ，失败返回NULL
	 */
	public native GeoPoint[] getPassPoints();

	/*!
	 * \brief  获得导航段的导航动作
	 * \param[in]  segIndex 导航段编号，从0开始编号
	 * \return 导航动作对象NaviAction
	*/
	public native int getSegTurnIcon(int segIndex);
	/*!
	 * \brief  获得导航段的导航动作
	 * \param[in]  segIndex 导航段编号，从0开始编号
	 * \return 导航动作对象NaviAction
	*/
	public native NaviAction getSegNaviAction(int segIndex);

	/*!
	 * \brief          多路径匹配时切换导航路径
	 * \details        若切换前已经开始GPS导航，切换后将自动开始GPS导航
	 * \param[in]      routeID {int}. 要切换的导航路径
	 * \return         {int}. 0失败，1成功
	 */
	public native int moveToRoute(int routeID);

	/*!
	 * \brief  获得躲避拥堵方案的旅行时间
	 * \details  单位：秒
	 * \return 旅行时间
	 */
	public native int getTMCRouteTime();

	/*!
	 * \brief  获得路径的拥堵点信息
	 * \details
	 * \return 拥堵点JamInfo数组，如果没有则返回NULL
	 */
	public native JamInfo[] getJamInfoList();

	/*!
	 * \brief	控制非导航状态下播报类型
	 * \details 根据输入的flag值控制非导航状态下的播报类型
	   \param   flag 播报类型
		**   0x1表示电子眼关闭/开启.
		**   0x2表示特殊道路（交通设施）关闭/开启.

        2种类型可以任意组合,
        flag=0，表示都关闭；
        flag=0x1，表示只开启电子眼；
        flag=0x2，表示只开启特殊道路设施；
        flag=0x3,表示同时开启电子眼和特殊道路设施
    \return         {void}. 无返回值
	 */
	public native void setDetectedMode(int flag);

	/*!
	 * \brief	设置客户端网络类型
	 * \details 0：无网络、1：wifi、2：运营商数据、3：表示其他
	 * return
	 */
	public native void setClientNetType(int type);

	/*!
	 * \brief	获得选中路径上的拥堵区域信息
	 * \details
	 * return 	路径上的拥堵区域信息
	 */
	public native AvoidJamArea getAvoidJamArea();

	/*!
	 * \brief	获得选中路径上的事件信息
	 * \details
	 * return	路径上的事件信息数组，如果没有返回NULL
	 */
	public native RouteIncident[] getRouteIncident();
	/*!
	 * \brief	设置导航路径途径点
	 */
	public native int setPassPoints(double[] passPos,int passNum );
	/*!
	 * \brief	设置导航路径途径点
	 */
	public native int setEndPoint(double[] endPos );
	/*!
	 * \brief	设置导航路径途径点
	 */
	public native int setCalcType(int type);

	public native RestrictionArea[] getRestrictionInfo();

	public native String GetRouteUID(int type);

	public native String getDecodeTmcError();

	public native String getDecodeRouteError();




//
//	/**
//	 *
//	 * @param type
//	 * @return
//	 */




//	/**
//	 \brief     指定本地离线导航数据存储位置
//	 \param     pwPath 存储位置
//	 \return
//	 */
//	public native  void setLocalOfflineDataPath(String pwPath);
//
//	/**
//	 \brief     获取【服务端/本地】离线数据城市（全国基础数据、省市列表）列表
//	 \return		列表指针
//	 */
//	public native  OfflineDataElem[] GetOfflineDataListServer();
//	public native  OfflineDataElem[] GetOfflineDataListLocal();

	static {
		System.loadLibrary("naviCore");
	}
}