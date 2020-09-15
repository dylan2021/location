package com.sfmap.tbt;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecode {
	private static final char[] a = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '+', '/' };
	private static final byte[] b = new byte[128];

	static byte[] aesEncrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
		try {
			return b(paramArrayOfByte1, paramArrayOfByte2);
		} catch (InvalidKeyException localInvalidKeyException) {
			BasicLogHandler.a(localInvalidKeyException, "Encrypt", "aesEncrypt");
			localInvalidKeyException.printStackTrace();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			BasicLogHandler.a(localNoSuchAlgorithmException, "Encrypt", "aesEncrypt");
			localNoSuchAlgorithmException.printStackTrace();
		} catch (NoSuchPaddingException localNoSuchPaddingException) {
			BasicLogHandler.a(localNoSuchPaddingException, "Encrypt", "aesEncrypt");
			localNoSuchPaddingException.printStackTrace();
		} catch (IllegalBlockSizeException localIllegalBlockSizeException) {
			BasicLogHandler.a(localIllegalBlockSizeException, "Encrypt", "aesEncrypt");
			localIllegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException localBadPaddingException) {
			BasicLogHandler.a(localBadPaddingException, "Encrypt", "aesEncrypt");
			localBadPaddingException.printStackTrace();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "Encrypt", "aesEncrypt");
			localThrowable.printStackTrace();
		}
		return null;
	}

	private static byte[] b(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] arrayOfByte1 = null;

		String str1 = "16,16,18,77,15,911,121,77,121,911,38,77,911,99,86,67,611,96,48,77,84,911,38,67,021,301,86,67,611,98,48,77,511,77,48,97,511,58,48,97,511,84,501,87,511,96,48,77,221,911,38,77,121,37,86,67,25,301,86,67,021,96,86,67,021,701,86,67,35,56,86,67,611,37,221,87";

		StringBuffer localStringBuffer = new StringBuffer(str1);
		String str2 = localStringBuffer.reverse().toString();
		String[] arrayOfString1 = str2.split(",");
		byte[] arrayOfByte2 = new byte[arrayOfString1.length];
		for (int i = 0; i < arrayOfString1.length; ++i) {
			arrayOfByte2[i] = Byte.parseByte(arrayOfString1[i]);
		}

		byte[] arrayOfByte3 = b(new String(arrayOfByte2));
		String str3 = new String(arrayOfByte3);
		localStringBuffer = new StringBuffer(str3);
		String str4 = localStringBuffer.reverse().toString();

		String[] arrayOfString2 = str4.split(",");
		byte[] arrayOfByte4 = new byte[arrayOfString2.length];
		for (int j = 0; j < arrayOfString2.length; ++j) {
			arrayOfByte4[j] = Byte.parseByte(arrayOfString2[j]);
		}

		IvParameterSpec localIvParameterSpec = new IvParameterSpec(arrayOfByte4);
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1,
				"AES");
		Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		try {
			localCipher.init(1, localSecretKeySpec, localIvParameterSpec);
		} catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
			localInvalidAlgorithmParameterException.printStackTrace();
		}

		arrayOfByte1 = localCipher.doFinal(paramArrayOfByte2);

		return arrayOfByte1;
	}

	static byte[] a(byte[] paramArrayOfByte, Key paramKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		localCipher.init(1, paramKey);
		return localCipher.doFinal(paramArrayOfByte);
	}

	public static String a(String paramString) {
		return new String(b(paramString));
	}

	public static byte[] b(String paramString) {
		if (paramString == null) {
			return new byte[0];
		}

		byte[] arrayOfByte = paramString.getBytes();
		int i = arrayOfByte.length;
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(
				i);
		int j = 0;

		while (j < i) {
			int k;
			int l;
			int i1;
			int i2;
			do
				k = b[arrayOfByte[(j++)]];
			while ((j < i) && (k == -1));
			if (k == -1) {
				break;
			}

			do {
				l = b[arrayOfByte[(j++)]];
			} while ((j < i) && (l == -1));
			if (l == -1)
				break;

			localByteArrayOutputStream.write(k << 2 | (l & 0x30) >>> 4);
			do {
				i1 = arrayOfByte[(j++)];
				if (i1 == 61)
					return localByteArrayOutputStream.toByteArray();

				i1 = b[i1];
			} while ((j < i) && (i1 == -1));
			if (i1 == -1)
				break;

			localByteArrayOutputStream
					.write((l & 0xF) << 4 | (i1 & 0x3C) >>> 2);
			do {
				i2 = arrayOfByte[(j++)];
				if (i2 == 61)
					return localByteArrayOutputStream.toByteArray();

				i2 = b[i2];
			} while ((j < i) && (i2 == -1));
			if (i2 == -1)
				break;

			localByteArrayOutputStream.write((i1 & 0x3) << 6 | i2);
		}
		return localByteArrayOutputStream.toByteArray();
	}

	private static String c(byte[] paramArrayOfByte) {
		StringBuffer localStringBuffer = new StringBuffer();
		int i = paramArrayOfByte.length;
		int j = 0;

		while (j < i) {
			int k = paramArrayOfByte[(j++)] & 0xFF;

			if (j == i) {
				localStringBuffer.append(a[(k >>> 2)]);
				localStringBuffer.append(a[((k & 0x3) << 4)]);
				localStringBuffer.append("==");
				break;
			}
			int l = paramArrayOfByte[(j++)] & 0xFF;
			if (j == i) {
				localStringBuffer.append(a[(k >>> 2)]);
				localStringBuffer
						.append(a[((k & 0x3) << 4 | (l & 0xF0) >>> 4)]);

				localStringBuffer.append(a[((l & 0xF) << 2)]);
				localStringBuffer.append("=");
				break;
			}
			int i1 = paramArrayOfByte[(j++)] & 0xFF;
			localStringBuffer.append(a[(k >>> 2)]);
			localStringBuffer.append(a[((k & 0x3) << 4 | (l & 0xF0) >>> 4)]);

			localStringBuffer.append(a[((l & 0xF) << 2 | (i1 & 0xC0) >>> 6)]);

			localStringBuffer.append(a[(i1 & 0x3F)]);
		}

		return localStringBuffer.toString();
	}

	public static String encodeBase64(byte[] paramArrayOfByte) {
		try {
			return c(paramArrayOfByte);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "Encrypt", "encodeBase64");
			localThrowable.printStackTrace();
		}
		return null;
	}

	public static String b(byte[] paramArrayOfByte) {
		try {
			return c(paramArrayOfByte);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return null;
	}

	static {
		for (int i = 0; i < 128; ++i)
			b[i] = -1;

		for (int i = 65; i <= 90; ++i)
			b[i] = (byte) (i - 65);

		for (int i = 97; i <= 122; ++i)
			b[i] = (byte) (i - 97 + 26);

		for (int i = 48; i <= 57; ++i)
			b[i] = (byte) (i - 48 + 52);

		b[43] = 62;
		b[47] = 63;
	}
}