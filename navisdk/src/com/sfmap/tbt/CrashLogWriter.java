package com.sfmap.tbt;

import java.util.Date;

/**
 * 原类名：v
 */
class CrashLogWriter extends LogWriter {
	private a a;

	protected int a() {
		return 0;
	}

	protected String a(String paramString) {
		String str1 = null;

		String str2 = z.a(new Date().getTime());
		String str3 = paramString + str2;
		str1 = MD5DECODE.b(str3);

		return str1;
	}

	protected String b() {
		return Log.c;
	}

	protected FileOperationListener a(LogDBOperation paramai) {
		try {
			if (this.a == null)
				this.a = new a(paramai);

		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return this.a;
	}

	class a implements FileOperationListener {
		private LogDBOperation b;

		a(LogDBOperation paramai) {
			this.b = paramai;
		}

		public void a(String paramString) {
			try {
				this.b.a(paramString, CrashLogWriter.this.a());
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
		}
	}
}