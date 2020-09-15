package com.sfmap.tbt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5DECODE {
	public static String getMD5(String paramString) {
		if (paramString == null)
			return null;

		byte[] arrayOfByte = getMd5Bytes(paramString);
		return Utils.b(arrayOfByte);
	}

	public static String b(String paramString) {
		byte[] arrayOfByte = d(paramString);
		return Utils.c(arrayOfByte);
	}

	public static byte[] getMd5Bytes(String paramString) {
		try {
			return e(paramString);
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			BasicLogHandler.a(localNoSuchAlgorithmException, "MD5", "getMd5Bytes");
			localNoSuchAlgorithmException.printStackTrace();
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			BasicLogHandler.a(localUnsupportedEncodingException, "MD5", "getMd5Bytes");
			localUnsupportedEncodingException.printStackTrace();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "MD5", "getMd5Bytes");
			localThrowable.printStackTrace();
		}
		return new byte[0];
	}

	private static byte[] d(String paramString) {
		try {
			return e(paramString);
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			localNoSuchAlgorithmException.printStackTrace();
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			localUnsupportedEncodingException.printStackTrace();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return new byte[0];
	}

	private static byte[] e(String paramString)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest localMessageDigest = null;
		byte[] arrayOfByte = null;

		if (paramString == null)
			return null;

		localMessageDigest = MessageDigest.getInstance("MD5");
		localMessageDigest.update(paramString.getBytes("utf-8"));
		arrayOfByte = localMessageDigest.digest();
		return arrayOfByte;
	}
}