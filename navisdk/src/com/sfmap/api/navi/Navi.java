package com.sfmap.api.navi;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import com.sfmap.api.maps.MapView;
import com.sfmap.api.navi.model.NaviCamera;
import com.sfmap.api.navi.enums.PathPlanningStrategy;
import com.sfmap.api.navi.model.NaviGuide;
import com.sfmap.api.navi.model.NaviPath;
import com.sfmap.api.navi.model.NaviTrafficStatus;
import com.sfmap.api.navi.model.NaviAvoidBound;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.tbt.BaseTask;
import com.sfmap.tbt.GPSDataInfo;
import com.sfmap.tbt.LinkStatus;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.RestrictionArea;
import com.sfmap.tbt.TBTControlDecode;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tbt.util.NaviViewUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * 导航对外控制类
 * @version 1.1.0
 */
public class Navi {
	private String TAG = Navi.class.getSimpleName();
	/**
	 * 导航模式 1 驾车实时导航
	 */
	public static int GPSNaviMode = 1;

	/**
	 * 导航模式 2 驾车模拟导航
	 */
	public static int EmulatorNaviMode = 2;

	/**
	 * 导航模式车速 60低速 120中速 300高速
	 */
	public static int EmulatorSpeedLow = 60;
	public static int EmulatorSpeedMid = 120;
	public static int EmulatorSpeedHigh = 300;

	/**
	 * 导航对外控制类实例
	 */
	private static Navi instance;

	/**
	 * Android 上下文
	 */
	private Context mContext;

	/**
	 *
	 */
	private TBTControlDecode tbtControl;

	/**
	 *
	 */
	private NaviSettingDecode naviSetting;
	private  MapView mapView;
	private boolean custonMapviewEnable;


	/**
	 * 构造函数。
	 * @param context - Android 上下文
	 */
	private Navi(Context context) {
		this.mContext = context.getApplicationContext();
		this.naviSetting = new NaviSettingDecode(this.mContext, this.tbtControl);
		this.tbtControl = TBTControlDecode.getInstance(this.mContext);
		this.tbtControl.init();
	}

	/**
	 *
	 * @return 导航规划总时间
	 */
	public int getRouteTime(){
		return tbtControl.getTBT().getRouteTime();
	}

	/**
	 * 获得 Navi 导航对象单例对象。
	 * @param context - Android 上下文
	 * @return 导航控制对象
	 */
	public static synchronized Navi getInstance(Context context) {
		try {
			if (instance == null)
				instance = new Navi(context);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			NaviUtilDecode.a(throwable);
		}
		return instance;
	}
	/**
	 * 设置本地鉴权文件路径
	 * @param path 鉴权文件路径
	 */
	public  void setAuthFilePath(String path){
		if(this.tbtControl!=null)
			this.tbtControl.setAuthFilePath(path);
	}

	/**
	 * 获得导航SDK的版本信息
	 * @return 导航SDK的版本号
	 */
	public static String getVersion() {

		return "1.4.0";
	}

	/**
	 * 释放导航对象资源
	 * 退出时调用此接口释放导航资源，在调用此接口后不能再调用Navi类里的其它接口。
 	 */
	public synchronized void destroy() {
		try {
			instance = null;
			this.naviSetting.releaseWakeLock();
			if (this.tbtControl != null)
				this.tbtControl.releaseResource();
			this.tbtControl = null;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}


	/**
	 * 返回导航路线的总长度
	 *
	 * @author bingchuan.gong
	 * @return int
	 */
	public int getRouteLength() {
		if (this.tbtControl != null)
			return tbtControl.getTBT().getRouteLength();
		return 0;
	}

	/**
	 * 开始导航。实时导航GPS未开启时，会自动打开GPS定位功能。模拟导航则不需要使用定位功能。
	 * @param naviType - 导航类型，1 ： 实时导航；2 :模拟导航。
	 * @return 返回启动成功或者失败。true是成功，false是失败。
	 */
	public boolean startNavi(int naviType) {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.startNavi(naviType);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 暂停导航，包含实时导航和模拟导航。
	 */
	public void pauseNavi() {
		try {
			if (this.tbtControl != null)
				this.tbtControl.pauseNavi();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 停止导航，包含实时导航和模拟导航。
	 */
	public void stopNavi() {
		try {
			if (this.tbtControl != null)
				this.tbtControl.stopNavi();
			LogUtil.d(TAG,"stopNavi()");
			this.custonMapviewEnable=false;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 设置gps状态监听器
	 * @param listener 状态监听器。
     */
	public void setGpsStatusListener( GpsStatus.Listener listener){
		if (this.tbtControl != null)
		this.tbtControl.setGpsStatusListener(listener);
	}

	/**
	 * gps状态监听移除
	 * @param listener 状态监听器。
     */
	public void removeGpsStatusListener( GpsStatus.Listener listener){
		if (this.tbtControl != null)
		this.tbtControl.removeGpsStatusListener(listener);
	}

	/**
	 * 获取当前GPS状态信息
	 * @return GPS状态信息。
     */
	public GpsStatus getGpsStatus(){
		if(tbtControl==null)return null;
		return  tbtControl.getGpsStatus();
	}


	/**
	 * 继续导航，包含实时导航和模拟导航。
	 */
	public void resumeNavi() {
		try {
			if (this.tbtControl != null)
				this.tbtControl.resumeNavi();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

//	/**
	 //	 * 手动刷新路况信息，调用后会立即触发一次光柱信息请求，只有驾车导航才会有效。
	 //	 */
//	public void refreshTrafficStatuses() {
//		try {
//			if (this.tbtControl != null)
//				this.tbtControl.manualRefreshTMC();
//		} catch (Throwable localThrowable) {
//			localThrowable.printStackTrace();
//			NaviUtilDecode.a(localThrowable);
//		}
//	}

//	/**
//	 * 触发一次导航播报信息。
//	 * @return 返回是否请求成功。true是成功，false是失败。
//	 */
//	public boolean readNaviInfo() {
//		try {
//			if (this.tbtControl != null)
//				return this.tbtControl.playNaviManual();
//		} catch (Throwable localThrowable) {
//			localThrowable.printStackTrace();
//			NaviUtilDecode.a(localThrowable);
//		}
//		return false;
//	}

//	/**
//	 * 触发一次前方路况播报。
//	 * @param frontDistance - 要播报的前方路径距离，0为SDK默认距离，-1为整条路径，一般情况下前方路况播报设置为0。
//	 * @return 返回请求是否成功。true是成功，false是失败。
//	 */
//	public boolean readTrafficInfo(int frontDistance) {
//		try {
//			if (this.tbtControl != null)
//				return this.tbtControl.playTrafficRadioManual(frontDistance);
//		} catch (Throwable localThrowable) {
//			localThrowable.printStackTrace();
//			NaviUtilDecode.a(localThrowable);
//		}
//		return false;
//	}

	/**
	 * 计算驾车路径(包含起点)。
	 * @param from - 指定的导航起点。支持多个起点，起点列表的尾点为实际导航起点，其他坐标点为辅助信息，带有方向性，可有效避免算路到马路的另一侧；
	 * @param to - 指定的导航终点。支持一个终点。
	 * @param wayPoints - 途经点，同时支持最多16个途经点的路径规划；
	 * @param strategy - 驾车路径规划的计算策略，请参见 {@link PathPlanningStrategy }。
	 * @param isLocal - 是否为本地算路,true 本地算路,false 网络算路
	 * @return 返回请求是否成功true，成功；false，失败。说明：返回true，只表示路径计算方法执行，但是否返回规划的路线,请参见 {@link NaviListener } 的回调。
	 */
	public boolean calculateDriveRoute(List<NaviLatLng> from, List<NaviLatLng> to, List<NaviLatLng> wayPoints, int strategy, boolean isLocal) {
		try {
			if (this.tbtControl != null)
			setPathCount(3);
			return this.tbtControl.calculateDriveRoute(from, to, wayPoints, strategy,isLocal,null,null);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 计算驾车路径(不带起点，起点默认为当前位置)。调用此接口前需要调用StartGPS()设置GPS位置，获取算路的起点。
	 * @param to - 指定的导航终点。支持一个终点。
	 * @param wayPoints - 途经点，同时支持最多16个途经点的路径规划；
	 * @param strategy - 驾车路径规划的计算策略，请参见 {@link PathPlanningStrategy }。
	 * @param isLocal - 是否为本地算路,true 本地算路,false 网络算路
	 * @return 返回请求是否成功true，成功；false，失败。说明：返回true，只表示路径计算方法执行，但是否返回规划的路线,请参见 {@link NaviListener } 的回调。
	 */
	public boolean calculateDriveRoute(List<NaviLatLng> to,	List<NaviLatLng> wayPoints, int strategy,boolean isLocal) {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.calculateDriveRoute(to, wayPoints, strategy,isLocal);//@param isLocal - 是否为本地算路
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 计算驾车路径(带避让道路)。
	 * @param from - 指定的导航起点。支持多个起点，起点列表的尾点为实际导航起点，其他坐标点为辅助信息，带有方向性，可有效避免算路到马路的另一侧；
	 * @param to - 指定的导航终点。支持一个终点。
	 * @param wayPoints - 途经点，同时支持最多16个途经点的路径规划；
	 * @param strategy - 驾车路径规划的计算策略，请参见 {@link PathPlanningStrategy }。
	 * @param avoidRouteName - 要避让的道路名称。
	 * @return 返回请求是否成功true，成功；false，失败。说明：返回true，只表示路径计算方法执行，但是否返回规划的路线,请参见 {@link NaviListener } 的回调。
	 */
	public boolean calculateDriveRouteWithAvoidRoute(List<NaviLatLng> from,	List<NaviLatLng> to, List<NaviLatLng> wayPoints, int strategy,String avoidRouteName){
		try {
			if (this.tbtControl != null){
				return this.tbtControl.calculateDriveRoute(from, to, wayPoints, strategy,false, URLEncoder.encode(avoidRouteName, "UTF-8"),null);}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}
	/**
	 * 计算驾车路径(带避让区域)。
	 * @param from - 指定的导航起点。支持多个起点，起点列表的尾点为实际导航起点，其他坐标点为辅助信息，带有方向性，可有效避免算路到马路的另一侧；
	 * @param to - 指定的导航终点。支持一个终点。
	 * @param wayPoints - 途经点，同时支持最多16个途经点的路径规划；
	 * @param strategy - 驾车路径规划的计算策略，请参见 {@link PathPlanningStrategy }。
	 * @param naviAvoidBounds - 要避让的区域集合
	 * @return 返回请求是否成功true，成功；false，失败。说明：返回true，只表示路径计算方法执行，但是否返回规划的路线,请参见 {@link NaviListener } 的回调。
	 */

	public boolean calculateDriveRouteWithAvoidBound(List<NaviLatLng> from, List<NaviLatLng> to, List<NaviLatLng> wayPoints, int strategy, List<NaviAvoidBound> naviAvoidBounds){
		try {
			if (this.tbtControl != null)
				return this.tbtControl.calculateDriveRoute(from, to, wayPoints, strategy,false,null,naviAvoidBounds);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 手动播报导航语音
	 * 	如果正在播报导航信息，则返回失败。
	 * @return true 成功，flase 失败
     */
	public boolean playNaviSound(){
		if(this.tbtControl!=null)
			return  this.tbtControl.playNaviManual();
		return  false;
	}

	/**
	 * 导航过程中重新规划路线（起点为当前位置，途经点、终点位置不变）。
	 * @param strategy - 指定的驾车策略常量值。
	 * @return 返回请求是否成功。true，成功；false，失败。
	 */
	public boolean reCalculateRoute(int strategy) {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.reCalculateRoute(strategy);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 获取某一范围内的路况光柱信息，仅在驾车导航模式下有效。
	 * @param startPos - 光柱范围在路径中的起始位置。
	 * @param distance - 光柱范围的距离。
	 * @return 返回该范围内路况信息数组，用于绘制光柱。
	 */
	public List<NaviTrafficStatus> getTrafficStatuses(int startPos, int distance) {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getTrafficStatuses(startPos, distance);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return null;
	}

	/**
	 * 获取某段路况的信息
	 * @param stepIndex - 大路段索引
	 * @param linkIndex — 小路段索引
	 * @param iTrafficIndex -路况段索引
     * @return 返回该路况的具体路况信息
     */

	public LinkStatus getLinkStatus(int stepIndex, int linkIndex, int iTrafficIndex){
	if(this.tbtControl!=null){
	return this.tbtControl.getTBT().getLinkTrafficStatus(stepIndex,linkIndex,iTrafficIndex);
	}
		return null;
}

	/**
	 * 获取某小段路径上的路况数量
	 * @param stepIndex -大路段索引
	 * @param linkIndex -小路段索引
     * @return 返回该路段的路况数量
     */
	public int getLinkStatusCount(int stepIndex,int linkIndex){
		if(this.tbtControl!=null)
			return this.tbtControl.getTBT().getLinkTrafficStatusCount(stepIndex,linkIndex);
		return 0;
	}

	/**
	 * 获取当前规划的路线方案 获取当前计算出的路线。
	 * @return 当前规划的路线信息。
	 */
	public NaviPath getNaviPath() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getNaviPath();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return null;
	}

	/**
	 * 获取当前规划的限行数据。
	 * @return 当前规划的限行数据。
	 */
	public RestrictionArea[] getRestrictionInfo() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getRestrictionInfo();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return null;
	}

	/**
	 * 获取当前路径的routeId
	 * @return 当前路径的routeId
	 */
	public String GetRouteUID(int type) {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.GetRouteUID(type);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return null;
	}

	/**
	 * 获取路径规划错误信息
	 * @return 路径规划错误信息
	 */
	public String getDecodeRouteError() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getDecodeRouteError();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return null;
	}
	/**
	 * 获取当前规划的高速金额。
	 * @return 当前规划的高速金额。
	 */
	public int getSegTollCost() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getSegTollCost();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return 0;
	}

	/**
	 * 获取计算的多路径的结果
	 * @return 多路径的结果
	 */
	public HashMap<Integer, NaviPath> getNaviPaths() {
		HashMap<Integer, NaviPath> localHashMap = null;
		try {
			if (this.tbtControl != null)
				localHashMap = this.tbtControl.getNaviPaths();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return localHashMap;
	}

	/**
	 * 获取路段概览
	 * @return 路段概览信息集合
	 */
	public List<NaviGuide> getNaviGuideList() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getNaviGuideList();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return null;
	}

	NaviSettingDecode getNaviSetting() {
		return this.naviSetting;
	}

	/**
	 * 设置模拟导航的速度。
	 * @param speed - 模拟导航的速度，单位：km/h。驾车导航默认速度为60km/h。速度范围为 5 - 150 之间。
	 */
	public void setEmulatorNaviSpeed(int speed) {
		//速度范围判断处理
		if(speed < 5)
			speed = 5;

//		if(speed > 150)
//			speed = 150;

		//设置到底层
		try {
			if (this.tbtControl != null)
				this.tbtControl.setEmulatorNaviSpeed(speed);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

//	/**
//	 * 设置TTS语音播报每播报一个字需要的时间。 根据播报一个字的时间和运行的速度，可以更改语音播报的触发时机。
//	 * @param time - 每个字的播放时间，单位为毫秒。驾车默认设置为280毫秒，步行默认设置为220毫秒。
//	 */
//	public void setTimeForOneWord(int time) {
//		try {
//			if (this.tbtControl != null)
//				this.tbtControl.setTimeForOneWord(time);
//		} catch (Throwable localThrowable) {
//			localThrowable.printStackTrace();
//			NaviUtilDecode.a(localThrowable);
//		}
//	}

	/**
	 * 添加导航事件回调监听。
	 * @param naviListener - 导航回调监听
	 */
	public void addNaviListener(NaviListener naviListener) {
		try {
			if (this.tbtControl != null)
				this.tbtControl.setMapNaviListener(naviListener);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 移除导航对象的监听。
	 * @param naviListener - 监听listener。
	 */
	public void removeNaviListener(NaviListener naviListener) {
		try {
			if (this.tbtControl != null)
				this.tbtControl.removeMapNaviListener(naviListener);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}



//	/**
//	 * 计算步行路径(不带起点，默认为当前位置)。
//	 * @param to  - 导航终点。调用此接口前需要调用StartGPS()设置GPS位置，获取算路的起点。
//	 * @return 请求是否成功true，成功；false，失败。说明：返回true，只表示路径计算方法执行，但是否返回规划的路线，请参见NaviListener的回调。
//	 */
//	public boolean calculateWalkRoute(NaviLatLng to) {
//		try {
//			if (this.tbtControl != null)
//				return this.tbtControl.calculateWalkRoute(to);
//		} catch (Throwable localThrowable) {
//			localThrowable.printStackTrace();
//			NaviUtilDecode.a(localThrowable);
//		}
//		return false;
//	}

//	/**
//	 * 计算步行路径(包含起点)。
//	 * @param from - 导航起点。
//	 * @param to  - 导航终点。
//	 * @return 请求是否成功true，成功；false，失败。说明：返回true，只表示路径计算方法执行，但是否返回规划的路线，请参见NaviListener的回调。
//	 */
//	public boolean calculateWalkRoute(NaviLatLng from,	NaviLatLng to) {
//		try {
//			if (this.tbtControl != null)
//				return this.tbtControl.calculateWalkRoute(from, to);
//		} catch (Throwable localThrowable) {
//			localThrowable.printStackTrace();
//			NaviUtilDecode.a(localThrowable);
//		}
//		return false;
//	}

	/**
	 * 设置是否偏航重算
	 * @param isReroute - 是否偏航重算
	 */
	public void setReCalculateRouteForYaw(boolean isReroute) {
		try {
			if (this.tbtControl != null)
				this.tbtControl.setReCalculateRouteForYaw(isReroute);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 是否拥堵重算
	 * @param isReroute - 是否拥堵重算
	 */
	public void setReCalculateRouteForTrafficJam(boolean isReroute) {
		try {
			if (this.tbtControl != null)
				this.tbtControl.setReCalculateRouteForTrafficJam(isReroute);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	/**
	 * 获取当前导航引导模式
	 * @return 当前的引导模式， 0 代表驾车， 1代表步行
	 */
	int getEngineType() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.getEngineType();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return -1;
	}

	/**
	 * 返回当前导航的状态，是模拟导航还是真实导航
	 * @return 1 代表驾车真实导航， 2 代表驾车模拟导航
	 */
	int getNaviModel() {
		try {
			if (this.tbtControl != null)
				return this.tbtControl.naviModel;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return -1;
	}

	/**
	 * 返回当前导航的状态，是模拟导航还是真实导航
	 * @return 1 代表驾车真实导航， 2 代表驾车模拟导航
	 */
	 int getNaviType() {
		if (this.tbtControl != null)
			return this.tbtControl.getNaviType();

		return 0;
	}


	/**
	 * 获得当前导航的指引信息
	 * @return 当前车位的指引信息
	 */
	NaviInfo getNaviInfo() {
		if (this.tbtControl != null)
			return this.tbtControl.getNaviInfo();

		return null;
	}

//	/**
//	 * 设置智能播报类型
//	 * @param detectedMode ：只播报电子眼 2：直播报特殊路段 3:播报电子眼和特殊路段, 参见AimLessMode
//	 */
//	public void setDetectedMode(int detectedMode) {
//		if (this.tbtControl != null)
//			this.tbtControl.setDetectedMode(detectedMode);
//	}

	/**
	 * 设置建立连接超时时间，单位毫秒级，最低3000，默认5000.
	 * @param connectionTimeOut - 超时时间
	 */
	public void setConnectionTimeout(int connectionTimeOut) {
		if (connectionTimeOut < 3000)
			connectionTimeOut = 3000;

		BaseTask.connectionTimeOut = connectionTimeOut;
	}

	/**
	 * 设置服务器返回超时时间，单位毫秒级，最低3000，默认5000.
	 * @param soTimeOut - 超时时间
	 */
	public void setSoTimeout(int soTimeOut) {
		if (soTimeOut < 3000)
			soTimeOut = 3000;

		BaseTask.soTimeout = soTimeOut;
	}

	/**
	 * 选择路线ID , 开始模拟/真实导航前，一定要选择路线。
	 * @param id - 路线ID
	 * @return 是否选择此路线成功
	 */
	public boolean selectRouteId(int id) {
		try {
			if (this.tbtControl != null)
				return (this.tbtControl.selectRouteId(id) != -1);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 启动GPS定位, 无参数。
	 * 用户可以手动启动GPS，如果没有启动GPS， 在驾车导航启动时（startNavi）会自动启动。默认时间间隔设置为1000ms，变化距离为0。
	 * @return GPS启动是否成功。true代表成功，false代表失败。
	 */
	public boolean startGPS() {
		try {
			if (this.tbtControl != null) {
				this.tbtControl.startGPS();
				return true;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 启动GPS定位, 带距离和时间参数。
	 * 用户可以手动启动GPS，如果没有启动GPS，在驾车导航启动时（startNavi）会自动启动。默认启动导航，时间间隔设置为1000ms，变化距离为0。
	 * @param time - 位置更新的时间间隔, 单位：毫秒。
	 * @param dis - 位置更新的距离间隔,单位：米。
	 * @return GPS启动是否成功。true代表成功，false代表失败。
	 */
	public boolean startGPS(long time, int dis) {
		try {
			if (this.tbtControl != null) {
				this.tbtControl.startGPS(time, dis);
				return true;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 停止GPS定位。
	 * @return 是否停止GPS成功。true，成功；false，失败。
	 */
	public boolean stopGPS() {
		try {
			if (this.tbtControl != null) {
				this.tbtControl.stopGPS();
				return true;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
		return false;
	}

	/**
	 * 获取GPS是否准备就绪
	 * @return 准备就绪，返回true；否则，返回false
	 */
	public boolean isGpsReady() {
		if (this.tbtControl != null)
			return this.tbtControl.isGPSReady();

		return false;
	}

	/**
	 * 设置是否记录GPS NMEA数据, 数据将以追加的方式填写到文件中。
	 * @param enable - 是否允许。
	 * @param nmeaFileName - 保存的文件名。如果 enable 参数传false, 此参数可以传空。
	 * @return true 代表设置成功, false代表设置失败。
     */
	public boolean saveGpsNmea(boolean enable, String nmeaFileName){
		if(this.tbtControl != null && (enable && null != nmeaFileName) )
			return this.tbtControl.saveGpsNmea(enable, nmeaFileName);
		return false;
	}

	/**
	 * 是否允许记录GPS NMEA数据。
	 * @return true 代表记录NMEA数据, false 代表不记录NMEA数据。
     */
	public boolean isSaveGpsNmea(){
		if(this.tbtControl != null)
			return this.tbtControl.isSaveGpsNmea();

		return false;
	}

	/**
	 * 设置定位监听器
	 * @param listener 定位监听器
     */
	public void setLocationListener(LocationListener listener){
		if(this.tbtControl!=null)
			tbtControl.setLocationListener(listener);
	}
	/**
	 * 向SDK添加GPS信息。使用本功能前建议禁止SDK内部的GPS监听。
	 * @param offsetFlag - 是否偏转标识，1无偏转，2有偏转
	 * @param location - GPS位置信息。
	 */
	public void setGPSInfo(int offsetFlag, Location location){
		if(this.tbtControl != null)
		{
			tbtControl.setGpsInfo(offsetFlag, location);
		}
	}

	/**
	 * 获取当前导航实际的行驶里程
	 * @return 实际行驶里程。
     */
	public int getNaivStaticInfo(){
		if(tbtControl!=null)
			return tbtControl.getTBT().getNaviStaticInfo().m_nDrivenDist;
		return 0;
	}

	/**
	 * 获取路径上所有电子眼
	 * @return 电子眼数据。
	 */
	public NaviCamera[] getAllCamera(){
		if(this.tbtControl!=null)
			return NaviCamera.getAllCameraForArray(this.tbtControl.getTBT().getAllCamera());
		return null;
	}

	/**
	 * 是否可以切换平行路
	 * @param segmentIndex 导航段索引
	 * @param linkIndex 导航段内的link索引。
	 * @return 当前导航路段的状态
	 * 0：不能切换；
	 * 1：当前在高架下，希望算到高架上去
	 * 2：当前在高架上，希望算到高架下去！
	 * 3：当前在辅路上，希望算到主路上去！
	 * 4：当前在主路上，希望算到辅路上去！
     */
	public int isSwitchParalleRoad(int segmentIndex,int linkIndex){
		if(tbtControl!=null)
			return this.tbtControl.isSwithRoute(segmentIndex,linkIndex);
		return 0;
	}

	/**
	 * 平行路切换
	 */
	public void switchParalleRoad(){
		if(this.tbtControl!=null)
			this.tbtControl.swithRoute();
	}

	/**
	 *  设置离线算路数据路径,路径需要是数据Top目录的上层目录的绝对路径
	 * @param path 离线数据路径
	 */
	public void setLocalDataPath(String path){
		if(tbtControl!=null){
			this.tbtControl.setLocalDataPath(path);
		}
	}

	/**
	 * 设置导航视图主题风格配置文件路径,请在实例化导航视图控件之前设置
	 * <pre>@配置示例:
	naviMainText    = #34a4e4      //主导航指引文字
	naviActionText  = #aed543      //导航动作
	naviDisAndTime  = #aef343      //距离和时间
	bottomText      = #9430ac 	  //底部剩余距离和剩余时间文字
	dividingLine    = #e3aed3       //分隔线
	topInfoBackgroud  = #93da3d     //顶部信息背景
	bottomInfoBackgroud = #0a4d36   //底部信息背景
	 </pre>
	 * @param path 配置文件绝对路径
	 *
     */
	public void setStyleFilePath(String path){
		NaviViewUtil.loadStyleConfigByAsync(path);
	}

	/**
	 * 设置导航地图实例, 此接口需要在实例化NaviView之前调用,方可生效。
	 * 注:此方法只在以地图为底层,导航为上层的不同fragment切换时有效
	 * @param view MapView 实例
     */
	@Deprecated
	public void setMapView(MapView view){
		this.mapView=view;
	}

	/**
	 * 获取自定义地图实例
	 * @return 自定义设置的MapView实例
     */
	@Deprecated
	public MapView getMapView(){
		return this.mapView;
	}


	/**
	 * 将路径Push到TBT中
	 * 如果正在导航，调用此方法将停止导航，并删除现有路径，成功后与正常算路成功后一样进行导航与数据访问。
	 * @param calcType  路径类型
	 * @param flag      路径附加要求flag，默认为0，可以是多个标志位的组合(0x01 代表不走高速)
	 * @param data      路径二进制数据
	 * @param length    数据长度
	 * @return 0失败，1成功
	 */
	public int pushRouteData(int calcType, int flag, byte[] data, int length){
		if(this.tbtControl!=null){
			return this.tbtControl.getTBT().pushRouteData( calcType, flag, data, length);
		}
		return 0;
	}

	/**
	 * 设置导航sdk算路服务版本。
	 * @param number 协议版本
     */
	public void setRouteProtocol(int number) {
		if (this.tbtControl != null)
		{
			this.tbtControl.setRouteProtocol(number);
		}
	}

	/**
	 * 获取TBT中所有路径ID
	 * @return 路径ID数组。
     */
	public int[] getAllRouteID(){
		if(this.tbtControl!=null){
			return tbtControl.getAllRouteID();
		}
		return null;
	}
	public GPSDataInfo[] getRecentGPS(int nDistThres, int nSpeedThres, int nCount){
		if(this.tbtControl!=null)
			return this.tbtControl.getTBT().getRecentGPS(nDistThres,nSpeedThres,nCount);
		return null;
	}

	/**
	 * 设置车牌号码
	 * @param strPlate
	 */
	public void setPlate(String strPlate){
		if(null == strPlate){
			strPlate="";
		}else if(strPlate.length() >  9){
			strPlate = strPlate.substring(0,9);
		}
		String plate = "";
		try {
			plate = URLEncoder.encode(strPlate,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.tbtControl.getTBT().setParam("CarPlate",plate);
	}

	/**
	 * 设置车辆类型
	 * 1-小车，默认，
	 * 4-拖挂车，
	 * 5-微型货车，（车长≤3.5m且总质量≤1.8T）
	 * 6-轻型货车，（3.5m＜车长＜6m且1.8T＜总质量＜4.5T）
	 * 7-中型货车，（车长≥6m且4.5T≤总质量＜12T）
	 * 8-重型货车，（总质量≥12T）
	 * 9-危险品运输车
	 * 	默认取1：小车
	 * @param type
	 */
	public void setVehicle(int type){
		if(type > 9 || type < 1){
			type = 1;
		}
		this.tbtControl.getTBT().setParam("CarVehicle",String.valueOf(type));
	}

	/**
	 * 设置载重，单位：吨
	 * @param weight
	 */
	public void setWeight(double weight){
		if(weight > 1000 || weight <= 0){
			weight = 0;
		}
		this.tbtControl.getTBT().setParam("CarWeight",String.valueOf(weight));
	}

	/**
	 * 设置高度，单位：米
	 */
	public void setHeight(double height){
		if(height > 100 || height <= 0){
			height = 0;
		}
		this.tbtControl.getTBT().setParam("CarHeight",String.valueOf(height));
	}

	/**
	 * 设置轴重，单位：吨
	 */
	public void setAxleWeight(double weight){
		if(weight > 1000 || weight <= 0){
			weight = 0;
		}
		this.tbtControl.getTBT().setParam("CarAxleWeight",String.valueOf(weight));
	}

	/**
	 * 设置轴数，单位：个
	 */
	public void  setAxleNumber(int axleNumber){
		if(axleNumber > 100 || axleNumber < 0){
			axleNumber = 0;
		}
		this.tbtControl.getTBT().setParam("CarAxleNumber",String.valueOf(axleNumber));
	}

    public void setPathCount(int i) {
		this.tbtControl.getTBT().setParam("pathCount",String.valueOf(i));
    }


	/**
	 * 设置频次路径
	 * @param opt
	 */
	public void setOpt(String opt){
		if(null == opt){
			return;
		}
		this.tbtControl.getTBT().setParam("opt",opt);
	}


}