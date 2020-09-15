package com.sfmap.tbt;

import java.text.SimpleDateFormat;
import java.util.Date;

public class z {
	static String a(Throwable paramThrowable) {
		String str1 = Utils.a(paramThrowable);
		if (str1 != null) {
			String str2 = str1.replaceAll("\n", "<br/>");
			return str2;
		}
		return null;
	}

	public static String a(long paramLong) {
		String str = null;
		try {
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
					"yyyyMMdd HH:mm:ss:SSS");
			Date localDate = new Date(paramLong);
			str = localSimpleDateFormat.format(localDate);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}

		return str;
	}
}