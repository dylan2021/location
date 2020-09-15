package com.sfmap.tbt;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.HashMap;

/**
 * 原类名：aj
 */
public abstract class LogEntity implements SQlEntity<LogInfo> {
	private static final String a = LogDBCreator.l;
	private static final String b = LogDBCreator.m;
	private static final String c = LogDBCreator.n;
	private static final String d = LogDBCreator.f;
	private LogInfo e;

	public LogEntity() {
		this.e = null;
	}

	public ContentValues a() {
		ContentValues localContentValues = null;
		try {
			if (this.e == null)
				return null;

			localContentValues = new ContentValues();
			localContentValues.put(a, this.e.b());
			localContentValues.put(b, Integer.valueOf(this.e.a()));
			localContentValues.put(d, Util.a(this.e.c()));
			localContentValues.put(c, Integer.valueOf(this.e.d()));

			return localContentValues;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return localContentValues;
	}

	public LogInfo b(Cursor paramCursor) {
		LogInfo localak = null;
		try {
			if (paramCursor == null) {
				return null;
			}

			String str1 = paramCursor.getString(1);
			int i = paramCursor.getInt(2);
			String str2 = paramCursor.getString(4);
			int j = paramCursor.getInt(3);

			localak = new LogInfo();
			localak.a(str1);
			localak.a(i);
			localak.b(Util.b(str2));
			localak.b(j);
			return localak;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return localak;
	}

	public void a(LogInfo paramak) {
		this.e = paramak;
	}

	public static String a(String paramString) {
		HashMap localHashMap = new HashMap();
		localHashMap.put(a, paramString);
		return DataBaseDecode.a(localHashMap);
	}
}