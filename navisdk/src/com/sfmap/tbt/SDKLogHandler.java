package com.sfmap.tbt;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 原类名：s
 */
public class SDKLogHandler extends BasicLogHandler implements Thread.UncaughtExceptionHandler {
	private Context d;
	private static ExecutorService e;

	static synchronized ExecutorService a() {
		try {
			if ((e == null) || (e.isShutdown()))
				e = Executors.newSingleThreadExecutor();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return e;
	}

	public static synchronized SDKLogHandler b() {
		return ((SDKLogHandler) a);
	}

	public static void a(Throwable paramThrowable, String paramString1,
			String paramString2) {
		if (a != null)
			a.a(paramThrowable, 1, paramString1, paramString2);
	}

	public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
		if (paramThrowable == null)
			return;

		a(paramThrowable, 0, null, null);

		if (this.b != null)
			this.b.uncaughtException(paramThread, paramThrowable);
	}

	public void b(Throwable paramThrowable, String paramString1,
			String paramString2) {
		try {
			if (paramThrowable == null) {
				return;
			}

			a(paramThrowable, 1, paramString1, paramString2);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	protected void a(Throwable paramThrowable, int paramInt,
			String paramString1, String paramString2) {
		Log.a(this.d, paramThrowable, paramInt, paramString1, paramString2);
	}
}