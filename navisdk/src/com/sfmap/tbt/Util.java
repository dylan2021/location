package com.sfmap.tbt;

/**
 * 原类名：an
 */
class Util {
	static String a(String paramString) {
		if (paramString == null)
			return null;

		String str1 = EncryptDecode.b(paramString.getBytes());
		String str2 = (char) (str1.length() % 26 + 65) + str1;

		return str2;
	}

	static String b(String paramString) {
		if (paramString.length() < 2)
			return "";

		return EncryptDecode.a(paramString.substring(1));
	}
}