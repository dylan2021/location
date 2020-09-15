package com.sfmap.tbt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.net.Proxy;

public class NetMangerDecode extends BaseNetManagerDecode {
	private static NetMangerDecode instance;
	private TPoolDecode b;
	private Handler c;

	public static NetMangerDecode getInstance(boolean paramBoolean) {
		return getInstance(paramBoolean, 5);
	}

	private static synchronized NetMangerDecode getInstance(boolean paramBoolean, int paramInt) {
		try {
			if (instance == null) {
				instance = new NetMangerDecode(paramBoolean, paramInt);

			} else if ((paramBoolean) && (instance.b == null))
				instance.b = TPoolDecode.getInstance(paramInt);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return instance;
	}

	private NetMangerDecode(boolean paramBoolean, int paramInt) {
		try {
			if (paramBoolean) {
				this.b = TPoolDecode.getInstance(paramInt);
			}
			if (Looper.myLooper() == null)
				// this.c = new a(Looper.getMainLooper(), null);
				this.c = new a(Looper.getMainLooper());
			else
				this.c = new a();
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "NetManger", "NetManger1");
			localThrowable.printStackTrace();
		}
	}

	public byte[] a(Request paramay) throws OperExceptionDecode {
		ResponseEntity localba = null;

		try {
			localba = getResponseForNet(paramay, false);

		} catch (OperExceptionDecode localh) {
			throw localh;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			SDKLogHandler.b().b(localThrowable, "NetManager", "makeSyncPostRequest");
			throw new OperExceptionDecode("未知的错误");
		}
		if (localba != null)
			return localba.a;

		return null;
	}

	public byte[] c(Request paramay) throws OperExceptionDecode {
		ResponseEntity localba = null;
		try {
			localba = getHttpResultForGet(paramay, false);
		} catch (OperExceptionDecode localh) {
			throw localh;
		} catch (Throwable localThrowable) {
			throw new OperExceptionDecode("未知的错误");
		}
		if (localba != null)
			return localba.a;

		return null;
	}

	public ResponseEntity getHttpResultForGet(Request paramay, boolean paramBoolean) throws OperExceptionDecode {
		ResponseEntity localba = null;
		try {
			Proxy localProxy;
			checkRequest(paramay);

			if (paramay.proxy == null) {
				localProxy = null;
			} else {
				localProxy = paramay.proxy;
			}
			HttpUrlUtilDecode localav = new HttpUrlUtilDecode(paramay.connectTimeout, paramay.readTimeout, localProxy, paramBoolean, paramay.getRh());

			localba = localav.doGet(paramay.getURL(), paramay.getAuthHead(), paramay.getHttpHead());
		} catch (OperExceptionDecode localh) {
			throw localh;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			throw new OperExceptionDecode("未知的错误");
		}

		return localba;
	}

	private void a(OperExceptionDecode paramh, Response paramaz) {
		ResponseMessageEntity localbc = new ResponseMessageEntity();
		localbc.a = paramh;
		localbc.b = paramaz;
		Message localMessage = Message.obtain();
		localMessage.obj = localbc;
		localMessage.what = 1;
		this.c.sendMessage(localMessage);
	}

	private void a(ResponseEntity paramba, Response paramaz) {
		paramaz.a(paramba.b, paramba.a);
		ResponseMessageEntity localbc = new ResponseMessageEntity();
		localbc.b = paramaz;
		Message localMessage = Message.obtain();
		localMessage.obj = localbc;
		localMessage.what = 0;
		this.c.sendMessage(localMessage);
	}

	static class a extends Handler {
		private a(Looper paramLooper) {
			super(paramLooper);
		}

		public a() {
		}

		public void handleMessage(Message paramMessage) {
			try {
				int i = paramMessage.what;
				switch (i) {
					case 0:
						ResponseMessageEntity localbc1 = (ResponseMessageEntity) paramMessage.obj;
						Response localaz1 = localbc1.b;
						localaz1.a();
						break;
					case 1:
						ResponseMessageEntity localbc2 = (ResponseMessageEntity) paramMessage.obj;
						Response localaz2 = localbc2.b;
						localaz2.a(localbc2.a);
						break;
					default:
						return;
				}
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
		}
	}

}