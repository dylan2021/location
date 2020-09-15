package com.sfmap.tbt;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sfmap.api.maps.DesUtil;
import com.sfmap.api.navi.NaviListener;
import com.sfmap.api.navi.enums.PathPlanningErrCode;
import com.sfmap.api.navi.model.CrossImgInfo;
import com.sfmap.api.navi.model.LaneImgInfo;
import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.api.navi.model.NaviLaneInfo;
import com.sfmap.api.navi.model.NaviServiceFacilityInfo;
import com.sfmap.api.navi.model.NaviTrafficFacilityInfo;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.api.navi.model.NaviLocationDecode;
import com.sfmap.api.navi.model.ResultBean;
import com.sfmap.api.navi.model.Top3Result;
import com.sfmap.api.navi.model.YawInfo;
import com.sfmap.tbt.util.AppInfo;
import com.sfmap.tbt.util.AuthManager;
import com.sfmap.tbt.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * 导航底层回调接口类
 */
public class FrameForTBT implements IFrameForTBT {
	private static String TAG = FrameForTBT.class.getSimpleName();
	// 起点错误（坐标不在中国范围内）
	public final int START_POINT_FALSE = 3;
	//解码错误
	public final int END_POINT_FALSE = 6;
	// 请求协议非法
	public final int ILLEGAL_REQUEST = 4;
	//无可通行路径
	public final static int ROUTE_FAIL = 13;
	//编码错误
	public final static int ERROR_CODE_ENCODE = 5;
	//参数错误
	public final static int ERROR_CODE_PARAMETER = 7;
	//策略不支持
	public final static int ERROR_CODE_STRATEGY = 8;
	//起点附近没有道路
	public final static int ERROR_CODE_FROMPOINT =14;
	//终点附近没有道路
	public final static int  ERROR_CODE_TOPOINT =15;
	//起点在双向禁行路上
	public final static int  ERROR_CODE_FROMFORBIDDING=18;
	//终点在双向禁行路上
	public final static int  ERROR_CODE_TOFORBIDDING=19;
	//途经点坐标错误，请重新选择途经点
	public final static int  ERROR_CODE_VIAPOINT=20;
	//途经点在双向禁行路上
	public final static int  ERROR_CODE_VIAFORBIDDING=22;
	//消息常量定义
	private static final int MESSAGE_NAVI_INFO = 0;
	private static final int MESSAGE_TTS_MESSAGE = 1;
	private static final int MESSAGE_FINISH_SIM_NAVI = 2;
	private static final int MESSAGE_ARRIVEWY = 3;
	private static final int MESSAGE_REROUTE_FOR_YAW = 4;
	private static final int MESSAGE_REROUTE_FOR_JAM = 5;
	private static final int MESSAGE_CAR_LOCATION_CHANGE = 6;
	private static final int MESSAGE_TMC_UDPATE = 7;
	private static final int MESSAGE_INIT_SUCCESS = 8;
	private static final int MESSAGE_INIT_FAILURE = 9;
	private static final int MESSAGE_START_NAVI = 10;
	private static final int MESSAGE_CALCULATE_ROUTE_SUCCESS = 11;
	private static final int MESSAGE_CALCULATE_ROUTE_FAILED = 12;
	private static final int MESSAGE_GPS_OPEN_STATUS = 13;
	private static final int MESSAGE_SHOW_LANEINFO = 14;
	private static final int MESSAGE_HIDE_LANEINFO = 15;
	private static final int MESSAGE_TRAFFIC_FACILITY_INFO=16;
	private static final int MESSAGE_SHOW_CROSS = 17;
	private static final int MESSAGE_HIDE_CROSS = 18;
	private static final int MESSAGE_SERVICE_FACILITY_INFO = 19;

	//全局属性缓存
	private Context mContext;
	private List<NaviListener> mNaviListeners;
	private NaviListenerTriggerHandler naviListenerTriggerHandler = new NaviListenerTriggerHandler();

	//当前操作对象的缓存
	NaviInfo naviInfo;

	//电子眼信息
	Camera[] allCameras;

	int errorCode = 0; //算路结果错误码
	private static TBTControlDecode mTbtControl;
	private boolean isReCalculateRouteForYaw = true;
	private boolean isReCalculateRouteForTrafficJam = false;
	private TrafficFacilityInfo[] mTrafficFacilityInfos;
	private NaviCross cross;
	private NaviLaneInfo[] laneInfos;
	private byte[] backgroundInfo;
	private byte[] foregroundInfo;
	private StringBuffer playBuff = new StringBuffer();
	private int soundType = 1;
	private int arrayWayId = -1;
	private NaviLocationDecode naviLocation;
	private int naviType = -1;
	private boolean isGpsOpen = false;
	private int routeCalculatedSize;
	private int[] routeIds;
	private boolean isReRoute=false;
	private NaviServiceFacilityInfo[] mServiceFacilityInfos; //道路设施信息
	private static boolean jyRoute = false;

	public FrameForTBT(Context paramContext, TBTControlDecode tbtControl) {
		this.mContext = paramContext;
		this.mTbtControl = tbtControl;
		this.mNaviListeners = new ArrayList();
	}

	public void setReCalculateRouteForYaw(boolean paramBoolean) {
		this.isReCalculateRouteForYaw = paramBoolean;
	}

	public void setReCalculateRouteForTrafficJam(boolean paramBoolean) {
		this.isReCalculateRouteForTrafficJam = paramBoolean;
	}


	//请求网络
	public void requestHttp(int cmdType, int cmdIndex, int postReq,
							String serverURL, String parameter, byte[] postValue,
							int postValueLen) {
		Log.i(TAG,"requestHttp:serverURL:"+serverURL+" parameter:"+parameter+" cmdType:"+cmdType+" cmdIndex:"+cmdIndex);
////		鉴权
		if(AuthManager.authResult != -1 && AuthManager.authResult != 0)
		{
			this.errorCode = PathPlanningErrCode.INSUFFICIENT_PRIVILEGES;
			this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_CALCULATE_ROUTE_FAILED);
			return;
		}

		try {
			if ((this.mTbtControl != null) && (this.mTbtControl.threadPool != null)) {

				String finalUrl = AppInfo.getRouteCarURL(mContext);
//				if(cmdType==2)	parameter+="\n"+"Content-Type:application/octet-stream";
				if(cmdType==2)	parameter+="\n"+"Content-Type:application/json";
//				android.util.Log.i("url",finalUrl+","+ new String(postValue,"utf-8").toString());
				byte[] validPostValue = new byte[postValueLen];
				System.arraycopy(postValue, 0, validPostValue, 0, postValueLen);
				if(cmdType==2){
					TBTTask locald = new TBTTask(this.mTbtControl, this.mContext, serverURL,
							postReq, parameter, cmdType, cmdIndex,	validPostValue, "sf");
					this.cmdType = cmdType;
					this.cmdIndex = cmdIndex;
					this.mTbtControl.threadPool.addTask(locald);
				}else if(cmdType==7){
					serverURL = AppInfo.getSfReRouteEtaURL(mContext);
					TBTTask locald = new TBTTask(this.mTbtControl, this.mContext, serverURL,
							postReq, parameter, cmdType, cmdIndex,	validPostValue, "jy0");
					this.cmdType = cmdType;
					this.cmdIndex = cmdIndex;
					jy0Task = locald;
					this.mTbtControl.threadPool.addTask(locald);
				}else if(cmdType==1){
					serverURL = AppInfo.getSfRouteEtaURL(mContext);
					TBTTask locald = new TBTTask(this.mTbtControl, this.mContext, serverURL,
							postReq, parameter, cmdType, cmdIndex,	validPostValue, "sf");
					this.cmdType = cmdType;
					this.cmdIndex = cmdIndex;
					this.mTbtControl.threadPool.addTask(locald);

				}else {
					TBTTask locald = new TBTTask(this.mTbtControl, this.mContext, finalUrl,
							postReq, parameter, cmdType, cmdIndex,	validPostValue, "sf");
					this.cmdType = cmdType;
					this.cmdIndex = cmdIndex;
					this.mTbtControl.threadPool.addTask(locald);

//					if(soapContent.contains("strategy=5")){
//						Thread.sleep(500);
//						TBTTask locald1 = new TBTTask(this.mTbtControl, this.mContext, finalUrl,
//								postReq, parameter, cmdType, cmdIndex,	validPostValue, "jy0");
//						this.cmdType = cmdType;
//						this.cmdIndex = cmdIndex;
//						jy0Task = locald1;
//						this.mTbtControl.threadPool.addTask(locald1);
//
//						AppInfo.dealNaviId(this.mContext);
//					}else {
//						jyRoute = false;
//					}
//					LogUtil.d(TAG,"parameter:"+soapContent);
				}

//				TBTTask locald1 = new TBTTask(this.mTbtControl, this.mContext, finalUrl,
//						postReq, parameter, cmdType, cmdIndex,	validPostValue, "jy0");

//				Thread.sleep(100);
//				this.mTbtControl.threadPool.addTask(locald1);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	static TBTTask jy0Task;
	public static void requestJyRoute(){
		if(null != jy0Task){
			try {
				mTbtControl.threadPool.addTask(jy0Task);
			} catch (OperExceptionDecode operExceptionDecode) {
				operExceptionDecode.printStackTrace();
			}
		}
		jy0Task = null;
	}

	public NaviInfo getNaviInfo() {
		return this.naviInfo;
	}

	public void setGpsValid(int paramInt) {
	}

	//更新导航信息
	public void updateNaviInfo(DGNaviInfo dgNaviInfo) {
		try {
			if (dgNaviInfo == null) {
				return;
			}
//			if(dgNaviInfo.m_CurLinkNum <=0 || dgNaviInfo.m_CurSegNum<=0){
//				//过滤掉无效导航信息
//				return;
//			}
			if(dgNaviInfo.m_Latitude <= 0 || dgNaviInfo.m_Longitude <= 0){
				//过滤掉坐标异常
				return;
			}
			Gson gson = new Gson();
			String logs = gson.toJson(dgNaviInfo);
			LogUtil.d(TAG,"updateNaviInfo:"+logs);
			this.naviInfo = new NaviInfo(dgNaviInfo);
			//更新电子眼信息
			if ((dgNaviInfo.m_CameraDist != -1)&& (dgNaviInfo.m_CameraIndex >= 0)) {
				//加载电子眼信息
				if(this.allCameras == null) {
					this.allCameras = this.mTbtControl.getTBT().getAllCamera();
				}
				if ((this.allCameras != null)&& (dgNaviInfo.m_CameraIndex < this.allCameras.length)) {
					NaviLatLng localNaviLatLng = new NaviLatLng();
					localNaviLatLng	.setLatitude(this.allCameras[dgNaviInfo.m_CameraIndex].m_Latitude);
					localNaviLatLng	.setLongitude(this.allCameras[dgNaviInfo.m_CameraIndex].m_Longitude);
					this.naviInfo.setCameraCoord(localNaviLatLng);
					this.naviInfo.setLimitSpeed(this.allCameras[dgNaviInfo.m_CameraIndex].m_CameraSpeed);
					this.naviInfo.setCameraType(this.allCameras[dgNaviInfo.m_CameraIndex].m_CameraType);
				}
			}
			if (this.naviListenerTriggerHandler != null) {
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_NAVI_INFO);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	//显示路口放大图
	public void showCross(int picFormat, final byte[] picBytes, final byte[] paramArrayOfByte2, int width, int height) {
		LogUtil.d(TAG,"showCross"+picFormat);
		try {
			if(picFormat == -1 && picBytes != null && paramArrayOfByte2 != null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						String text3 = "";
						for(int i=0 ;i<picBytes.length;i++){
							text3 += picBytes[i]+"";
						}
						String text4 = "";
						for(int i=0 ;i<paramArrayOfByte2.length;i++){
							text4 += paramArrayOfByte2[i]+"";
						}
						Log.i(TAG,"showCross:text3"+text3+"text4:"+text4);
						final String picName1 = bytesToHex(picBytes);
						final String picName2 = bytesToHex(paramArrayOfByte2);
						String text1 = "#CROSSING_IMG,"+getTimeStampDetail()+","+picName1+"\n";
						String text2 = "#CROSSING_IMG,"+getTimeStampDetail()+","+picName2+"\n";
						LogUtil.d(TAG,"showCross:text1"+text1+"text2:"+text2);
						Request request = new Request() {
							@Override
							public Map<String, String> getAuthHead() {
								return null;
							}

							@Override
							public Map<String, String> getHttpHead() {
								return null;
							}

							@Override
							public String getURL() {
								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName1+"&ak=1aca21cfd4204548bad2fd32dca24b8b&type=background";
//								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName1+"&ak="+AppInfo.getAkKey(mContext)+"&type=background";
								return url;
							}

							@Override
							public String getRh() {
								return null;
							}
						};
						Request request1 = new Request() {
							@Override
							public Map<String, String> getAuthHead() {
								return null;
							}

							@Override
							public Map<String, String> getHttpHead() {
								return null;
							}

							@Override
							public String getURL() {
								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName2+"&ak=1aca21cfd4204548bad2fd32dca24b8b&type=unbackground";
//								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName2+"&ak="+AppInfo.getAkKey(mContext)+"&type=unbackground";
								return url;
							}

							@Override
							public String getRh() {
								return null;
							}
						};
						try {
							byte[] picByte = NetMangerDecode.getInstance(false).c(request);
							byte[] paramArrayOfByte = NetMangerDecode.getInstance(false).c(request1);
							if ( paramArrayOfByte == null && picByte != null) {
								cross = new NaviCross(2, 400, 400, picByte);
							}else if(paramArrayOfByte!=null && picByte != null){
								cross=new NaviCross(2,400,400,picByte,paramArrayOfByte);
							}
							if (naviListenerTriggerHandler != null)
								naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_SHOW_CROSS);
						} catch (OperExceptionDecode operExceptionDecode) {
							operExceptionDecode.printStackTrace();
						}
					}
				}).start();

			}else if(picFormat == -2 && picBytes != null && paramArrayOfByte2 != null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						String text3 = "";
						for(int i=0 ;i<picBytes.length;i++){
							text3 += picBytes[i]+",";
						}
						String text4 = "";
						for(int i=0 ;i<paramArrayOfByte2.length;i++){
							text4 += paramArrayOfByte2[i]+",";
						}
						Log.i(TAG,"showCross:text3"+new String(picBytes)+"text4:"+new String(paramArrayOfByte2));
						final String picName1 = new String(picBytes);
						final String picName2 = new String(paramArrayOfByte2);

						CrossImgInfo crossImgInfo = new CrossImgInfo();
						crossImgInfo.setCrossingId(picName1+","+picName2);
						EventBus.getDefault().postSticky(crossImgInfo);


						Log.i(TAG,"showCross:picName1"+picName1+"picName2:"+picName2);
						Request request = new Request() {
							@Override
							public Map<String, String> getAuthHead() {
								return null;
							}

							@Override
							public Map<String, String> getHttpHead() {
								return null;
							}

							@Override
							public String getURL() {
								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName1+"&ak=1aca21cfd4204548bad2fd32dca24b8b&type=background";
//								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName1+"&ak="+AppInfo.getAkKey(mContext)+"&type=background";
								Log.i(TAG,"showCross:url1"+url);
								return url;
							}

							@Override
							public String getRh() {
								return null;
							}
						};
						Request request1 = new Request() {
							@Override
							public Map<String, String> getAuthHead() {
								return null;
							}

							@Override
							public Map<String, String> getHttpHead() {
								return null;
							}

							@Override
							public String getURL() {
								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName2+"&ak=1aca21cfd4204548bad2fd32dca24b8b&type=unbackground";
//								String url = AppInfo.getSfCrossPicURL(mContext)+"?fileName="+picName2+"&ak="+AppInfo.getAkKey(mContext)+"&type=unbackground";
								Log.i(TAG,"showCross:url2"+url);
								return url;
							}

							@Override
							public String getRh() {
								return null;
							}
						};
						try {
							byte[] picByte = NetMangerDecode.getInstance(false).c(request);
							byte[] paramArrayOfByte = NetMangerDecode.getInstance(false).c(request1);
							if ( paramArrayOfByte == null && picByte != null) {
								cross = new NaviCross(2, 400, 400, picByte);
							}else if(paramArrayOfByte!=null && picByte != null){
								cross=new NaviCross(2,400,400,picByte,paramArrayOfByte);
							}
							if (naviListenerTriggerHandler != null)
								naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_SHOW_CROSS);
						} catch (OperExceptionDecode operExceptionDecode) {
							operExceptionDecode.printStackTrace();
						}
					}
				}).start();

			}else {
				picFormat = 2;
				width = height = 400;
				if ( paramArrayOfByte2==null && picBytes != null) {
					this.cross = new NaviCross(picFormat, width, height, picBytes);
				}else if(paramArrayOfByte2!=null && picBytes != null){
					this.cross=new NaviCross(picFormat,width,height,picBytes,paramArrayOfByte2);
				}
				if (this.naviListenerTriggerHandler != null)
					this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_SHOW_CROSS);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void hideCross() {
		if (this.naviListenerTriggerHandler != null)
			this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_HIDE_CROSS);
	}

	//显示车道线信息
	public void showLaneInfo(byte[] backGroundInfo, byte[] foregroundInfo) {
		if(this.backgroundInfo !=null && this.foregroundInfo != null){
			String text1 = "";
			for(int i=0 ;i<this.backgroundInfo.length;i++){
				text1 += this.backgroundInfo[i]+"";
			}
			String text2 = "";
			for(int i=0 ;i<this.foregroundInfo.length;i++){
				text2 += this.foregroundInfo[i]+"";
			}
			String textBack = "#LANE_IMG,"+getTimeStampDetail()+","+text1+","+text2+"\n";
			LogUtil.d(TAG,textBack);
			LaneImgInfo laneImgInfo = new LaneImgInfo();
			laneImgInfo.setLaneId(text1+","+text2);
			EventBus.getDefault().postSticky(laneImgInfo);
		}


		this.backgroundInfo = backGroundInfo;
		this.foregroundInfo = foregroundInfo;

		this.laneInfos = parseLaneInfoByte(backGroundInfo, foregroundInfo);
		if (this.naviListenerTriggerHandler != null)
			this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_SHOW_LANEINFO);
	}

	private NaviLaneInfo[] parseLaneInfoByte(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
		int i = parseDriveWaySize(paramArrayOfByte1);
		NaviLaneInfo[] arrayOfMapLaneInfo = initLaneInfos(i);
		for (int k = 0; k < i; ++k) {
			int j;
			if (isComplexLane(paramArrayOfByte1[k])) {
				j = complexLaneAlgorithm(paramArrayOfByte1[k],paramArrayOfByte2[k]);
			} else
				j = simpleLaneAlgorithm(paramArrayOfByte1[k],paramArrayOfByte2[k]);
			arrayOfMapLaneInfo[k].setLaneTypeId(j);
		}
		return arrayOfMapLaneInfo;
	}

	private int simpleLaneAlgorithm(byte paramByte1, byte paramByte2) {
		int i;
		if (paramByte2 == 15)
			i = paramByte1 * 16 + paramByte2;
		else
			i = paramByte1 * 16 + paramByte1;
		return i;
	}

	//初始化车道信息
	private NaviLaneInfo[] initLaneInfos(int paramInt) {
		NaviLaneInfo[] arrayOfMapLaneInfo = new NaviLaneInfo[paramInt];
		for (int i = 0; i < arrayOfMapLaneInfo.length; ++i)
			arrayOfMapLaneInfo[i] = new NaviLaneInfo();
		return arrayOfMapLaneInfo;
	}

	private int parseDriveWaySize(byte[] paramArrayOfByte) {
		int i = paramArrayOfByte.length;
		int j = 0;
		for (int k = 0; k < i; ++k)
			if (paramArrayOfByte[k] == 15) {
				j = k;
				break;
			}
		return j;
	}

	private boolean isComplexLane(int paramInt) {
		return ((paramInt == 14) || (paramInt == 2) || (paramInt == 4)
				|| (paramInt == 9) || (paramInt == 10) || (paramInt == 11)
				|| (paramInt == 12) || (paramInt == 6) || (paramInt == 7));
	}

	private int complexLaneAlgorithm(int paramInt1, int paramInt2) {
		int i = paramInt1 * 16 + paramInt2;
		return i;
	}

	public void hideLaneInfo() {
		if (this.naviListenerTriggerHandler != null)
			this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_HIDE_LANEINFO);
	}

	//播报语音
	public void playNaviSound(int soundType, String soundText) {
		Log.i("playNaviSound",soundText);
		try {
			if (this.playBuff != null)
				this.playBuff.delete(0, this.playBuff.length());
			else {
				this.playBuff = new StringBuffer();
			}
			if(soundText != null){
				if(soundText.length() >= 4){
					try {
						this.soundType = Integer.parseInt(soundText.substring(0,4));
					}catch (Exception e){

					}
					this.playBuff.append(soundText.substring(4));
				}
				else{
					this.playBuff.append(soundText);
					this.soundType = 0;
				}

			}
			else
			{
				this.soundType = 0;
			}

			/*
			if (soundType == 8) //原有引擎8代表了提示音
				this.playBuff.append("");
			else {
				this.playBuff.append(soundText);
			}
			this.soundType = soundType; */
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_TTS_MESSAGE);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void endEmulatorNavi() {
		try {
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_FINISH_SIM_NAVI);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void arriveWay(int arrayWayId) {
		try {
			this.arrayWayId = arrayWayId;
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_ARRIVEWY);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void routeDestroy() {
		try {
			if (this.mTbtControl != null)
				this.mTbtControl.clearRouteResult();
			//清空电子眼信息
			this.allCameras = null;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}


	/**
	 * 更新车位
	 * @param paramCarLocation
	 */
	public void carLocationChange(CarLocation paramCarLocation) {
		try {
			if (paramCarLocation == null)
				return;
			if(paramCarLocation.m_Latitude == 0 || paramCarLocation.m_Longitude == 0){
				return;
			}
			AppInfo.setStartX(paramCarLocation.m_Longitude);
			AppInfo.setStartY(paramCarLocation.m_Latitude);
			LogUtil.d(TAG,"#CARLOCATION,"+new Gson().toJson(paramCarLocation)+"\n");
			this.naviLocation = new NaviLocationDecode();
			this.naviLocation.setBearing(paramCarLocation.m_CarDir);
			this.naviLocation.setSpeed(paramCarLocation.m_Speed);
			this.naviLocation.setMatchStatus(paramCarLocation.m_MatchStatus);
			this.naviLocation.setCoord(new NaviLatLng(paramCarLocation.m_Latitude,paramCarLocation.m_Longitude));
			this.naviLocation.setTime(System.currentTimeMillis());
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_CAR_LOCATION_CHANGE);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}
	/*
    算路结果
     */
	public void setRouteRequestState(int iModuleID,int routeResult) {
		try {
			LogUtil.d(TAG,"setRouteRequestState:"+routeResult);
			this.errorCode = routeResult;
			switch (routeResult) {
				case TBT.SUCCEED_STATE:
					int getInfoResult = -1;
					NaviUtilDecode.a(new Object[] { "setRouteRequestState error_code = " + this.errorCode });
					if (this.mTbtControl != null) {
						this.routeIds = this.mTbtControl.getAllRouteID();
						this.routeCalculatedSize = this.mTbtControl.getAllRouteID().length;
						if (this.routeCalculatedSize == 1) {
							getInfoResult = this.mTbtControl.selectRouteId(this.routeIds[0]);
						} else {
							getInfoResult = this.mTbtControl.getRouteInfos(this.routeIds);
						}
						//获得电子眼信息
						this.allCameras = mTbtControl.getTBT().getAllCamera();
					}
					if (this.mNaviListeners != null) {
						if (this.routeCalculatedSize >= 1 && getInfoResult == 1){
							this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_CALCULATE_ROUTE_SUCCESS);
							if(iModuleID==7){
								isReRoute=true;
//								if(mTbtControl.isSwithRoute){
//									String text ="";
//									mTbtControl.isSwithRoute=false;
//									switch (mTbtControl.routeId){
//										case 1:
//											text="已将路径切换到高架上";
//											break;
//										case 2:
//											text="已将路径切换到高架下";
//											break;
//										case 3:
//											text="已将路径切换到主路";
//											break;
//										case 4:
//											text="已将路径切换到辅路";
//											break;
//
//									}
//									if (this.playBuff != null)
//										this.playBuff.delete(0, this.playBuff.length());
//									else {
//										this.playBuff = new StringBuffer();
//									}
//									playBuff.append(text);
//									if (this.naviListenerTriggerHandler != null)
//									this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_TTS_MESSAGE);
//								}


							}
						}
						else {
							this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_CALCULATE_ROUTE_FAILED);
						}
					}
					break;
				default:
					break;
			}

			if (routeResult != 1)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_CALCULATE_ROUTE_FAILED);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}
	/*
    路况更新
     */
	public void tmcUpdate() {
		try {
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_TMC_UDPATE);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void showTrafficPanel(int paramInt, byte[] paramArrayOfByte) {
	}

	public void hideTrafficPanel() {
	}

	public void rerouteForTMC(int paramInt1, int paramInt2, int paramInt3) {
		try {
			if ((this.isReCalculateRouteForTrafficJam)	&& (this.mTbtControl != null) && (paramInt1 != 0)) {
				int i = (this.mTbtControl.b(paramInt1, paramInt3) == 1) ? 1 : 0;
				if (i != 0)
					this.mTbtControl.selectRouteId(paramInt1);

				if (this.naviListenerTriggerHandler != null)
					this.naviListenerTriggerHandler.sendEmptyMessage(11);
				 playNaviSound(0,"发现前方拥堵,已为您重新规划");

				if (this.naviListenerTriggerHandler != null)
					this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_REROUTE_FOR_JAM);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}
	/*
    路况更新
     */
	int index=0;
	public void tmcUpdate(int paramInt1, int paramInt2, int paramInt3) {
		index++;
		if(index==4){
			rerouteForTMC(0,0,0);
		index=0;
		}
		try {
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_TMC_UDPATE);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void showTrafficPanel(byte[] paramArrayOfByte) {
	}

	public int getPlayState() {
		return 0;
	}

	public void lockScreenNaviTips(String paramString, int paramInt1,
								   int paramInt2) {
	}

	public void notifyMessage(int paramInt1, int paramInt2, int paramInt3,
							  String paramString) {
	}

	public void initSuccess() {
		try {
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_INIT_SUCCESS);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void initFailure() {
		try {
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_INIT_FAILURE);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void setMapNaviListener(NaviListener paramMapNaviListener) {
		try {
			if (paramMapNaviListener == null)
				return;

			if ((this.mNaviListeners != null)
					&& (!(this.mNaviListeners
					.contains(paramMapNaviListener))))
				this.mNaviListeners.add(paramMapNaviListener);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void removeNaviListener(NaviListener paramMapNaviListener) {
		try {
			if (this.mNaviListeners != null)
				this.mNaviListeners.remove(paramMapNaviListener);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void destroy() {
		try {
			if (this.mNaviListeners != null) {
				this.mNaviListeners.clear();
				this.mNaviListeners = null;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void onStartNavi(int paramInt) {
		try {
			this.naviType = paramInt;
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_START_NAVI);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void onGpsOpenStatus(boolean paramBoolean) {
		try {
			this.isGpsOpen = paramBoolean;
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_GPS_OPEN_STATUS);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public int matchRouteChanged(int paramInt) {
		return 0;
	}

	public void offRoute(int paramInt) {
		try {
			boolean calResult = true;
			if (this.isReCalculateRouteForYaw) {
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_REROUTE_FOR_YAW);

				NaviUtilDecode.a(new Object[] { "offRoute status" });
				calResult = this.mTbtControl.reCalculateRoute(-1);
			}
			if (paramInt == 2) //???
			{
				return;
			}


			if (!calResult) {
				this.errorCode = 3;
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_CALCULATE_ROUTE_FAILED);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}
	public void updateTrafficFacility(	TrafficFacilityInfo[] paramTrafficFacilityInfo) {
		mTrafficFacilityInfos=null;
//		android.util.Log.i("eyes",paramTrafficFacilityInfo.length+"");
		try {
			if(paramTrafficFacilityInfo.length>0){
				mTrafficFacilityInfos = new NaviTrafficFacilityInfo[paramTrafficFacilityInfo.length];
				for (int i = 0; i < paramTrafficFacilityInfo.length; i++) {
					mTrafficFacilityInfos[i] = new NaviTrafficFacilityInfo(paramTrafficFacilityInfo[i]);
				}
			}
			if (this.naviListenerTriggerHandler != null)
				this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_TRAFFIC_FACILITY_INFO);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void updateServiceFacility(ServiceFacilityInfo[] serviceFacilityInfos) {
		mServiceFacilityInfos = null;
		try {
			if (serviceFacilityInfos.length > 0) {
				mServiceFacilityInfos = new NaviServiceFacilityInfo[serviceFacilityInfos.length];
				for (int i = 0; i < serviceFacilityInfos.length; i++) {
					mServiceFacilityInfos[i] = new NaviServiceFacilityInfo();
					mServiceFacilityInfos[i].setRemainDist(serviceFacilityInfos[i].remainDist);
					mServiceFacilityInfos[i].setType(serviceFacilityInfos[i].type);
					mServiceFacilityInfos[i].setCoords(new NaviLatLng(serviceFacilityInfos[i].lat,serviceFacilityInfos[i].lon));
					mServiceFacilityInfos[i].setName(serviceFacilityInfos[i].name);
				}
				if (this.naviListenerTriggerHandler != null)
					this.naviListenerTriggerHandler.sendEmptyMessage(MESSAGE_SERVICE_FACILITY_INFO);
			}
		}catch(Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}



	}

	public void updateCameraInfo(ViewCameraInfo[] paramArrayOfViewCameraInfo) {
	}

	public void updateTrack(TrackPosition[] paramArrayOfTrackPosition) {
	}

	@Override
	public void endGpsNavi() {
		LogUtil.d(TAG,"EndGPSNavi");
	}

	class NaviListenerTriggerHandler extends Handler {
		public void handleMessage(Message paramMessage) {
			int i;
			try {
				super.handleMessage(paramMessage);
				if (FrameForTBT.this.mNaviListeners == null)
					return;

				switch (paramMessage.what) {
					case MESSAGE_NAVI_INFO:
						for (i = 0; i < mNaviListeners.size(); ++i){
							mNaviListeners.get(i).onNaviInfoUpdate(naviInfo);
						}
						break;

					case MESSAGE_TTS_MESSAGE:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onGetNavigationText(soundType, playBuff.toString());
						break;

					case MESSAGE_FINISH_SIM_NAVI:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onEndEmulatorNavi();
						break;

					case MESSAGE_ARRIVEWY:
						if (arrayWayId >= 0) {
							if (arrayWayId == 0)
								for (i = 0; i < mNaviListeners.size(); ++i)
									mNaviListeners.get(i).onArriveDestination();
							else
								for (i = 0; i < mNaviListeners.size(); ++i)
									mNaviListeners.get(i).onArrivedWayPoint(arrayWayId);
						}
						break;

					case MESSAGE_REROUTE_FOR_YAW://偏航重算
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onReCalculateRouteForYaw();
						break;

					case MESSAGE_REROUTE_FOR_JAM:
						//实时交通重新算路
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onReCalculateRouteForTrafficJam();
						break;

					case MESSAGE_CAR_LOCATION_CHANGE:
						if (naviLocation != null)
							for (i = 0; i < mNaviListeners.size(); ++i) {
								mNaviListeners.get(i).onLocationChange(naviLocation.getMapNaviLocation());
							}
						break;

					case MESSAGE_TMC_UDPATE:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onTrafficStatusUpdate();

						break;
					case MESSAGE_INIT_SUCCESS:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onInitNaviSuccess();
						break;

					case MESSAGE_INIT_FAILURE:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onInitNaviFailure();
						break;

					case MESSAGE_START_NAVI:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onStartNavi(naviType);
						break;

					case MESSAGE_CALCULATE_ROUTE_SUCCESS:
						if (routeCalculatedSize == 1) {
							for (i = 0; i < mNaviListeners.size(); ++i)
								mNaviListeners.get(i).onCalculateRouteSuccess();
							if(isReRoute){
								mTbtControl.startNavi(mTbtControl.getNaviType());
								isReRoute = false;
							}

						}

						if (routeCalculatedSize > 1){
//							if(isReRoute){
//								Toast.makeText(mContext,"手动选择第1条",Toast.LENGTH_LONG).show();
//								mTbtControl.selectRouteId(routeIds[0]);
//								isReRoute = false;
//							}
							for (i = 0; i < mNaviListeners.size(); ++i){
								mNaviListeners.get(i).onCalculateMultipleRoutesSuccess(routeIds);
							}
						}
						break;
					case MESSAGE_CALCULATE_ROUTE_FAILED:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onCalculateRouteFailure(errorCode);
							android.util.Log.e(TAG,"errorCode"+errorCode+" message:"+getErrorCodeDes(errorCode));

						break;
					case MESSAGE_GPS_OPEN_STATUS:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).onGpsOpenStatus(isGpsOpen);

						break;
					case MESSAGE_SHOW_LANEINFO:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).showLaneInfo(laneInfos, backgroundInfo, foregroundInfo);

						break;
					case MESSAGE_HIDE_LANEINFO:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).hideLaneInfo();

						break;
					case MESSAGE_TRAFFIC_FACILITY_INFO:
						if (mTrafficFacilityInfos != null)
							for (i = 0; i < mNaviListeners.size(); ++i) {
								mNaviListeners.get(i).onUpdateTrafficFacility((NaviTrafficFacilityInfo[]) mTrafficFacilityInfos);
							}
						break;
					case MESSAGE_SHOW_CROSS:
						if (cross != null && cross.getBitmap()!=null) {
							for (i = 0; i < mNaviListeners.size(); ++i)
								mNaviListeners.get(i).showCross(cross);
						}
						break;

					case MESSAGE_HIDE_CROSS:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).hideCross();
						break;

					case MESSAGE_SERVICE_FACILITY_INFO:
						for (i = 0; i < mNaviListeners.size(); ++i)
							mNaviListeners.get(i).updateServiceFacility(mServiceFacilityInfos);
						break;

					default:
						break;
				}

			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
				NaviUtilDecode.a(localThrowable);
			}
		}
	}
	public String GetDeviceID(){
		return DeviceIdManager.getDeviceID(mContext);
	}

	/**
	 * 根据错误码索引返回错误码描述
	 *
	 * @param errorCodeIndex
	 * @return
	 */
	private String getErrorCodeDes(int errorCodeIndex) {
		String errorCodeDes = "请求路线失败，请稍后重试";
		switch (errorCodeIndex) {
			case START_POINT_FALSE:
				errorCodeDes = "起点不在规划区域内";
				break;
			case ILLEGAL_REQUEST:
				errorCodeDes = "请求协议非法";
				break;
			case ERROR_CODE_PARAMETER:
				errorCodeDes = "参数错误";
				break;
			case END_POINT_FALSE:
				errorCodeDes = "请求路线失败，请稍后重试";
				break;
			case ERROR_CODE_ENCODE:
				errorCodeDes = "编码错误";
				break;
			case ERROR_CODE_STRATEGY:
				errorCodeDes = "策略不支持，请重新选择策略";
				break;
			case ERROR_CODE_FROMPOINT:
				errorCodeDes = "起点附近没有道路，请重新选择起点";
				break;
			case ERROR_CODE_TOPOINT:
				errorCodeDes = "终点附近没有道路，请重新选择终点";
				break;
			case ERROR_CODE_FROMFORBIDDING:
				errorCodeDes = "起点在双向禁行路上，请重新选择起点";
				break;
			case ERROR_CODE_TOFORBIDDING:
				errorCodeDes = "终点在双向禁行路上，请重新选择终点";
				break;
			case ROUTE_FAIL:
				errorCodeDes = "无可通行路径，请重新选择起终点";
				break;
			case ERROR_CODE_VIAPOINT:
				errorCodeDes = "途经点坐标错误，请重新选择途经点";
				break;
			case ERROR_CODE_VIAFORBIDDING:
				errorCodeDes = "途经点在双向禁行路上，请重新选择途经点";
				break;
			default:
				break;
		}
		LogUtil.d(TAG,"#RQERROR,"+errorCode+errorCodeDes+"\n");
		return errorCodeDes;
	}

	/**
	 * 字节数组转16进制
	 * @param bytes 需要转换的byte数组
	 * @return  转换后的Hex字符串
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for(int i = bytes.length-1; i >= 0; i--) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if(hex.length() < 2){
				sb.append(0);
			}
			sb.append(hex);
		}
//		for(int i = 0; i <bytes.length; i++) {
//			String hex = Integer.toHexString(bytes[i] & 0xFF);
//			if(hex.length() < 2){
//				sb.append(0);
//			}
//			sb.append(hex);
//		}
		return sb.toString();
	}

	public static byte[] data1 = null;
	public static byte[] data2 = null;
	static int cmdType = 0;
	static int cmdIndex = 0;;

	public static void recieveData(byte[] data,int requestType){

		try {
			if(requestType == 2){
				LogUtil.d(TAG,"解密前");
				byte[] res1 =  DesUtil.getInstance().decryptByte(data);
				LogUtil.d(TAG,"解密后");
				if(isGzip(res1)){
					String res = uncompressToString(res1);
					LogUtil.d(TAG,"解压后"+res);
					mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, res.getBytes(), res.getBytes().length);
					LogUtil.d(TAG,"塞给导航库"+res.getBytes().length);
				}else {
					mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, res1, res1.length);
				}



//				String tmp= new String(data);
//				LogUtil.d(TAG,"实时路況解密前："+tmp);
//				String tmp1 = null;
//				try {
//					tmp1 = DesUtil.getInstance().decrypt(tmp,"UTF-8");
//					LogUtil.d(TAG,"实时路況解密后："+tmp1);
//				} catch (IllegalBlockSizeException e) {
//					e.printStackTrace();
//				} catch (BadPaddingException e) {
//					e.printStackTrace();
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//
//				LogUtil.d(TAG,"receiveNetData 路況"+data.length);
//				mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, tmp1.getBytes(), tmp1.getBytes().length);
				return;
			}else if(requestType == 1){
				LogUtil.d(TAG,"解密前");
				byte[] res1 =  DesUtil.getInstance().decryptByte(data);
				LogUtil.d(TAG,"解密后");
				if(isGzip(res1)){
					String res = uncompressToString(res1);
					LogUtil.d(TAG,"解压后"+res);
					mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, res.getBytes(), res.getBytes().length);
					LogUtil.d(TAG,"塞给导航库"+res.getBytes().length);
				}else {
					mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, res1, res1.length);
					LogUtil.d(TAG,"塞给导航库"+new String(res1));
				}

			}else if(requestType == 7){
				byte[] res1 =  DesUtil.getInstance().decryptByte(data);
				boolean iszip = isGzip(res1);
				if(iszip){
					String res = uncompressToString(res1);
					LogUtil.d(TAG,"偏航解密后結果:"+res);
					LogUtil.d(TAG,"receiveNetData 偏航"+res.getBytes().length);
					mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, res.getBytes(), res.getBytes().length);
				}else {
					mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, res1, res1.length);
					LogUtil.d(TAG,"解压缩失败");
				}
			}else {
				LogUtil.d(TAG,"其它接口receiveNetData"+data.length);
				mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, data, data.length);

			}
		}catch (Exception e){
			mTbtControl.tbtControl.receiveNetData(requestType, cmdIndex, data, data.length);
			LogUtil.d(TAG,e.getMessage());
		}

	}

	public static boolean isJson(String content) {
        try {
            if(content.startsWith("{"))
            return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

	public String getTimeStampDetail() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(dt);
	}


	public static String getString(){
		String result = "乏乐久乎乍乁乐丐丐丗乓乐乁乓丄七习丁丘丘丘丘丘丘";
		char[] c = result.toCharArray();
		for(int i=0;i<c.length;i++){
			c[i] = (char)(c[i]^20000);
		}
		return new String(c);
	}

	class Response{
		private String result;

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}

	class Response1{
		private Object result;

		public Object getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}

	public static byte[] uncompress(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			GZIPInputStream ungzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			LogUtil.d(TAG,"gzip uncompress error."+e.getMessage());
		}

		return out.toByteArray();
	}


	/**
	 * Gzip  byte[] 解压成字符串
	 * @param bytes
	 * @return
	 */
	public static String uncompressToString(byte[] bytes) {
		return uncompressToString(bytes, "utf-8");
//		return uncompressToString(bytes, "unicode");
//		return uncompressToString(bytes, "ISO-8859-1");
	}


	/**
	 * Gzip  byte[] 解压成字符串
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public static String uncompressToString(byte[] bytes, String encoding) {
		String content = null;
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			GZIPInputStream ungzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			content =  out.toString(encoding);
		} catch (IOException e) {
			Log.e("gzip compress error.",e.getMessage());
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return content;
	}
	/**
	 * 判断byte[]是否是Gzip格式
	 * @param data
	 * @return
	 */
	public static boolean isGzip(byte[] data) {
		int header = (int)((data[0]<<8) | data[1]&0xFF);
		return header == 0x1f8b;
	}

}