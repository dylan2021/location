package com.sfmap.tbt;

import android.content.Context;

import java.util.HashMap;

/**
 * 原来类名: a
 */
public class BaseTask extends ThreadTaskDecode {
	public static int connectionTimeOut = 30000;	//建立连接超时时间，单位毫秒级，最低3000，默认5000.
	public static int soTimeout = 30000; //服务器返回超时时间，单位毫秒级，最低3000，默认5000.
	public TBTControlDecode tbtControl;
	String serverUrl = null;
	int postReq = 0;
	String parameter = null;
	int cmdType = 0;
	int cmdIndex = 0;
	byte[] postValue = null;
	NetMangerDecode netManger = null;
	Context context = null;
	String rh = null; //融合路径参数

	public BaseTask(TBTControlDecode paramh, Context context,
			String serverUrl, int postReq, String parameter,
			int cmdType, int cmdIndex, byte[] postValue, String rh) {
		this.tbtControl = paramh;
		this.serverUrl = serverUrl;
		this.postReq = postReq;
		this.parameter = parameter;
		this.cmdType = cmdType;
		this.cmdIndex = cmdIndex;
		this.postValue = postValue;
		this.netManger = NetMangerDecode.getInstance(false);
		this.context = context;
		this.rh = rh;
	}

	public void a() {
	}

	public byte[] b()throws OperExceptionDecode {
		HashMap localHashMap = new HashMap();
		TBTRequest tbtRequest = new TBTRequest();
		tbtRequest.setConnectTimeOut(connectionTimeOut);
		tbtRequest.setReadTimeOut(soTimeout);
		byte[] arrayOfByte = null;
		try {
			if (this.postReq == 0) { //post request
				localHashMap.clear();
				if (this.parameter != null) {
					String[] arrayOfString1 = this.parameter.split("\n");
					if ((arrayOfString1 != null) && (arrayOfString1.length > 1)) {
						String[] arrayOfString2 = arrayOfString1[0].split(":");
						if ((arrayOfString2 != null)&& (arrayOfString2.length > 1))
							localHashMap.put(arrayOfString2[0],	arrayOfString2[1]);

						String[] arrayOfString3 = arrayOfString1[1].split(":");
						if ((arrayOfString3 != null)&& (arrayOfString3.length > 1))
							localHashMap.put(arrayOfString3[0],	arrayOfString3[1]);

					}
				}

				tbtRequest.setParameterMap(localHashMap);
				tbtRequest.a(this.serverUrl);
				tbtRequest.b(this.rh);
				tbtRequest.setProxy(ProxyUtilDecode.getProxy(this.context));
				tbtRequest.a(this.postValue);
				arrayOfByte = this.netManger.a(tbtRequest);
			} else { //get request
				tbtRequest.a(this.serverUrl);
				tbtRequest.setProxy(ProxyUtilDecode.getProxy(this.context));

				arrayOfByte = this.netManger.c(tbtRequest);
			}

		} catch ( OperExceptionDecode localThrowable) {
			 throw localThrowable;
		}
		return arrayOfByte;
	}
}