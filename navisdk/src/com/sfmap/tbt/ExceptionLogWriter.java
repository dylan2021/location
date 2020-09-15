package com.sfmap.tbt;

/**
 * 原类名：w
 */
class ExceptionLogWriter extends LogWriter {
	private a a;

	protected int a() {
		return 1;
	}

	protected String a(String paramString) {
		String str = MD5DECODE.b(paramString);

		return str;
	}

	protected String b() {
		return Log.b;
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
				this.b.a(paramString, ExceptionLogWriter.this.a());
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
		}
	}
}