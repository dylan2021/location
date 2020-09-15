package com.sfmap.tbt;

import android.content.Context;

import java.net.Proxy;

public class BaseNetManagerDecode {
	private static BaseNetManagerDecode instance;

	public static BaseNetManagerDecode getInstance() {
		if (instance == null)
			instance = new BaseNetManagerDecode();

		return instance;
	}


	public byte[] a(Request paramay) throws OperExceptionDecode {
		ResponseEntity localba = null;
		try {
			localba = getResponseForNet(paramay, false);


		} catch (OperExceptionDecode localh) {
			throw localh;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			BasicLogHandler.a(localThrowable, "BaseNetManager", "makeSyncPostRequest");
			throw new OperExceptionDecode("未知的错误");
		}
		if (localba != null)
			return localba.a;

		return null;
	}

	protected void checkRequest(Request paramay) throws OperExceptionDecode {
		if (paramay == null)
			throw new OperExceptionDecode("requeust is null");

		if ((paramay.getURL() == null) || ("".equals(paramay.getURL())))
			throw new OperExceptionDecode("request url is empty");
	}

	protected ResponseEntity getResponseForNet(Request paramay, boolean paramBoolean) throws OperExceptionDecode {
		ResponseEntity localba = null;

		byte[] arrayOfByte = paramay.d();
		try {
			Proxy localProxy;
			checkRequest(paramay);

			if (paramay.proxy == null) {
				localProxy = null;
			} else {
				localProxy = paramay.proxy;
			}
			HttpUrlUtilDecode localav = new HttpUrlUtilDecode(paramay.connectTimeout, paramay.readTimeout, localProxy, paramBoolean,paramay.getRh());

			if (arrayOfByte == null) {
				localba = localav.doNet(paramay.getURL(), paramay.getAuthHead(), paramay.getHttpHead());
			} else if (arrayOfByte != null) {
				localba = localav.doNet(paramay.getURL(), paramay.getAuthHead(), paramay.getHttpHead(),arrayOfByte);
			}




		} catch (OperExceptionDecode localh) {
			throw localh;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			throw new OperExceptionDecode("未知的错误");
		}




		return localba;
	}


}