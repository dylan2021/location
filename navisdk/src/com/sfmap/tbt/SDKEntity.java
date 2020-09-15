package com.sfmap.tbt;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 原类名：am
 */
public class SDKEntity implements SQlEntity<ProductInfoDecode> {
	private static String a = LogDBCreator.f;
	private static String b = LogDBCreator.g;
	private static String c = LogDBCreator.k;
	private static String d = LogDBCreator.h;
	private static String e = LogDBCreator.i;
	private static String f = LogDBCreator.j;
	private ProductInfoDecode g;

	public SDKEntity() {
		this.g = null;
	}

	public ContentValues a() {
		ContentValues localContentValues = null;
		try {
			if (this.g == null)
				return null;

			localContentValues = new ContentValues();
			localContentValues.put(a, Util.a(this.g.a()));

			localContentValues.put(b, Util.a(this.g.b()));

			localContentValues.put(c, Boolean.valueOf(this.g.e()));
			localContentValues.put(d, Util.a(this.g.c()));

			localContentValues.put(f, Util.a(this.g.d()));

			localContentValues.put(e, Util.a(a(this.g.f())));

			return localContentValues;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return localContentValues;
	}

	public ProductInfoDecode b(Cursor paramCursor) {
		ProductInfoDecode productInfo = null;
		try {
			String str1 = Util.b(paramCursor.getString(1));
			String str2 = Util.b(paramCursor.getString(2));
			String str3 = Util.b(paramCursor.getString(3));
			String str4 = Util.b(paramCursor.getString(4));
			String[] arrayOfString = a(str4);
			String str5 = Util.b(paramCursor.getString(5));
			int i = paramCursor.getInt(6);
			boolean bool = false;
			if (i == 0)
				bool = false;
			else
				bool = true;

			productInfo = new ProductInfoDecode.PInnerA(str1, str2, str3).a(bool).a(str5).a(arrayOfString)
					.a();

			return productInfo;
		} catch (OperExceptionDecode localh) {
			localh.printStackTrace();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return productInfo;
	}

	public String b() {
		return LogDBCreator.a;
	}

	private String[] a(String paramString) {
		try {
			String[] arrayOfString = paramString.split(";");
			return arrayOfString;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return null;
	}

	private String a(String[] paramArrayOfString) {
		try {
			if (paramArrayOfString == null)
				return null;

			StringBuilder localStringBuilder = new StringBuilder();
			for (String str : paramArrayOfString)
				localStringBuilder.append(str).append(";");

			return localStringBuilder.toString();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return null;
	}

	public static String c() {
		return c + "=1";
	}

	// not exist before
	@Override
	public ProductInfoDecode a(Cursor paramCursor) {
		// TODO Auto-generated method stub
		return null;
	}
}