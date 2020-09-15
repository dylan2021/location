package com.sfmap.tbt;

public class ProductInfoDecode {
	String sdkversion;
	String b;
	String product;
	private boolean d = true;
	private String e = "standard";
	private String[] f = null;

	private ProductInfoDecode(PInnerA parama) {
		this.sdkversion = parama.sdkversion;
		this.product = parama.product;
		this.b = parama.c;
		this.d = parama.d;
		this.e = parama.e;
		this.f = parama.f;
	}

	public String a() {
		return this.product;
	}

	public String b() {
		return this.sdkversion;
	}

	public String c() {
		return this.b;
	}

	public String d() {
		return this.e;
	}

	public boolean e() {
		return this.d;
	}

	public String[] f() {
		return ((String[]) this.f.clone());
	}

	public static class PInnerA {
		private String sdkversion;
		private String product;
		private String c;
		private boolean d = true;
		private String e = "standard";
		private String[] f = null;

		public PInnerA(String paramString1, String paramString2, String paramString3) {
			this.sdkversion = paramString2;
			this.c = paramString3;
			this.product = paramString1;
		}

		public PInnerA a(boolean paramBoolean) {
			this.d = paramBoolean;
			return this;
		}

		public PInnerA a(String paramString) {
			this.e = paramString;
			return this;
		}

		public PInnerA a(String[] paramArrayOfString) {
			this.f = ((String[]) paramArrayOfString.clone());
			return this;
		}

		public ProductInfoDecode a() throws OperExceptionDecode {
			if (this.f == null)
				throw new OperExceptionDecode("sdk packages is null");

			return new ProductInfoDecode(this);
		}
	}
}