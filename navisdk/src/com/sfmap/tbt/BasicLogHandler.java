package com.sfmap.tbt;
/**
 * 原类名：u
 */
public class BasicLogHandler {
	protected static BasicLogHandler a;
	protected Thread.UncaughtExceptionHandler b;
	protected boolean c;

	public BasicLogHandler() {
		this.c = true;
	}

	public static void a(Throwable paramThrowable, String paramString1,
			String paramString2) {
		if (a != null)
			a.a(paramThrowable, 1, paramString1, paramString2);
	}

	protected void a(Throwable paramThrowable, int paramInt,
			String paramString1, String paramString2) {
	}
}