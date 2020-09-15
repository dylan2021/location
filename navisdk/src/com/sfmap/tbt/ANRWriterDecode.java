package com.sfmap.tbt;

class ANRWriterDecode extends LogWriter {
	private String[] a;
	private int b;
	private boolean c;
	private int d;
	private a e;

	ANRWriterDecode() {
		this.a = new String[10];
		this.b = 0;
		this.c = false;
		this.d = 0;
	}

	protected int a() {
		return 2;
	}

	protected String b() {
		return Log.d;
	}

	protected String a(String paramString) {
		String str = MD5DECODE.b(paramString);
		return str;
	}

	protected FileOperationListener a(LogDBOperation paramai) {
		try {
			if (this.e == null)
				this.e = new a(paramai);
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "ANRWriter", "getListener");
			localThrowable.printStackTrace();
		}

		return this.e;
	}

	private class a implements FileOperationListener {
		private LogDBOperation b;

		private a(LogDBOperation paramai) {
			this.b = paramai;
		}

		public void a(String paramString) {
			try {
				this.b.a(paramString, ANRWriterDecode.this.a());
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			}
		}
	}
}