package com.sfmap.tbt;

import android.content.Context;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/**
 * 原类名:q
 */
public class Utils {
	public static String a(Throwable paramThrowable) {
		StringWriter localStringWriter = null;
		PrintWriter localPrintWriter = null;
		try {
			String str2;
			localStringWriter = new StringWriter();
			localPrintWriter = new PrintWriter(localStringWriter);
			paramThrowable.printStackTrace(localPrintWriter);
			Throwable localThrowable1 = paramThrowable.getCause();

			while (localThrowable1 != null) {
				localThrowable1.printStackTrace(localPrintWriter);
				localThrowable1 = localThrowable1.getCause();
			}
			String str1 = localStringWriter.toString();
			return str1;
		} catch (Throwable localThrowable2) {
			localThrowable2.printStackTrace();
		} finally {
			if (localStringWriter != null)
				try {
					localStringWriter.close();
				} catch (Throwable localThrowable3) {
					localThrowable3.printStackTrace();
				}

			if (localPrintWriter != null)
				try {
					localPrintWriter.close();
				} catch (Throwable localThrowable4) {
					localThrowable4.printStackTrace();
				}
		}

		return null;
	}

	public static String sortParameter(String paramString) {
		try {
			if (paramString == null)
				return null;

			String[] arrayOfString = paramString.split("&");
			Arrays.sort(arrayOfString);
			StringBuffer localStringBuffer = new StringBuffer();
			for (String str : arrayOfString) {
				localStringBuffer.append(str);
				localStringBuffer.append("&");
			}

			// ??? = localStringBuffer.toString();
			String valuedecode = localStringBuffer.toString();
			if (valuedecode.length() > 1) {
				valuedecode = valuedecode.subSequence(0,
						valuedecode.length() - 1).toString();
				return valuedecode;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}

		return ((String) paramString);
	}

	public static byte[] a(byte[] paramArrayOfByte) {
		try {
			return e(paramArrayOfByte);
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return new byte[0];
	}

	static PublicKey a(Context paramContext) throws CertificateException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			NullPointerException, IOException {
		byte[] arrayOfByte = { 48, -126, 2, -98, 48, -126, 2, 7, -96, 3, 2, 1,
				2, 2, 9, 0, -99, 15, 119, 58, 44, -19, -105, -40, 48, 13, 6, 9,
				42, -122, 72, -122, -9, 13, 1, 1, 5, 5, 0, 48, 104, 49, 11, 48,
				9, 6, 3, 85, 4, 6, 19, 2, 67, 78, 49, 19, 48, 17, 6, 3, 85, 4,
				8, 12, 10, 83, 111, 109, 101, 45, 83, 116, 97, 116, 101, 49,
				16, 48, 14, 6, 3, 85, 4, 7, 12, 7, 66, 101, 105, 106, 105, 110,
				103, 49, 17, 48, 15, 6, 3, 85, 4, 10, 12, 8, 65, 117, 116, 111,
				110, 97, 118, 105, 49, 31, 48, 29, 6, 3, 85, 4, 3, 12, 22, 99,
				111, 109, 46, 97, 117, 116, 111, 110, 97, 118, 105, 46, 97,
				112, 105, 115, 101, 114, 118, 101, 114, 48, 30, 23, 13, 49, 51,
				48, 56, 49, 53, 48, 55, 53, 54, 53, 53, 90, 23, 13, 50, 51, 48,
				56, 49, 51, 48, 55, 53, 54, 53, 53, 90, 48, 104, 49, 11, 48, 9,
				6, 3, 85, 4, 6, 19, 2, 67, 78, 49, 19, 48, 17, 6, 3, 85, 4, 8,
				12, 10, 83, 111, 109, 101, 45, 83, 116, 97, 116, 101, 49, 16,
				48, 14, 6, 3, 85, 4, 7, 12, 7, 66, 101, 105, 106, 105, 110,
				103, 49, 17, 48, 15, 6, 3, 85, 4, 10, 12, 8, 65, 117, 116, 111,
				110, 97, 118, 105, 49, 31, 48, 29, 6, 3, 85, 4, 3, 12, 22, 99,
				111, 109, 46, 97, 117, 116, 111, 110, 97, 118, 105, 46, 97,
				112, 105, 115, 101, 114, 118, 101, 114, 48, -127, -97, 48, 13,
				6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115,
				0, 48, -127, -119, 2, -127, -127, 0, -15, -27, -128, -56, 118,
				-59, 62, -127, 79, 125, -36, 121, 0, 63, -125, -30, 118, 5,
				-85, -121, 91, 39, 90, 123, 72, -126, -83, -41, -45, -77, -42,
				-120, -81, 23, -2, -121, -29, 123, -7, 22, -114, -20, -25, 74,
				67, -43, 65, 124, -7, 11, -72, 38, -123, 16, -58, 80, 32, 58,
				-33, 14, 11, 36, 60, 13, -121, 100, 105, -32, 123, -31, 114,
				-101, -41, 12, 100, 33, -120, 63, 126, -123, 48, 55, 80, -116,
				28, -10, 125, 59, -41, -95, -126, 118, -70, 43, -127, 9, 93,
				-100, 81, -19, -114, -41, 85, -103, -37, -116, 118, 72, 86,
				125, -43, -92, -11, 63, 69, -38, -10, -65, 126, -53, -115, 60,
				62, -86, -80, 1, 39, 19, 2, 3, 1, 0, 1, -93, 80, 48, 78, 48,
				29, 6, 3, 85, 29, 14, 4, 22, 4, 20, -29, 63, 48, -79, -113,
				-13, 26, 85, 22, -27, 93, -5, 122, -103, -109, 14, -18, 6, -13,
				-109, 48, 31, 6, 3, 85, 29, 35, 4, 24, 48, 22, -128, 20, -29,
				63, 48, -79, -113, -13, 26, 85, 22, -27, 93, -5, 122, -103,
				-109, 14, -18, 6, -13, -109, 48, 12, 6, 3, 85, 29, 19, 4, 5,
				48, 3, 1, 1, -1, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1,
				1, 5, 5, 0, 3, -127, -127, 0, -32, -74, 55, -125, -58, -128,
				15, -62, 100, -60, 3, -86, 81, 112, -61, -56, -69, -126, 8, 99,
				-100, -38, -108, -56, -122, 125, 19, -64, -61, 90, 85, -47, -8,
				-123, -103, 105, 77, -32, -65, -62, -28, 67, -28, -78, 116,
				-49, 120, -2, 33, 13, 47, 46, -5, -112, 3, -101, -125, -115,
				92, -124, 58, 80, 107, -67, 82, 6, -63, 39, -90, -1, 85, -58,
				82, -115, 119, 13, -4, -32, 0, -98, 100, -41, 94, -75, 75,
				-103, 126, -80, 85, 40, -27, 60, 105, 28, -27, -21, -15, -98,
				103, -88, -109, 35, -119, -27, -26, -122, 113, 63, 35, -33, 70,
				23, 33, -23, 66, 108, 56, 112, 46, -85, -123, -123, 33, 118,
				27, 96, -7, -103 };

		ByteArrayInputStream localByteArrayInputStream = null;
		Certificate localCertificate = null;
		KeyFactory localKeyFactory = null;
		try {
			localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);

			CertificateFactory localCertificateFactory = CertificateFactory
					.getInstance("X.509");
			localKeyFactory = KeyFactory.getInstance("RSA");
			localCertificate = localCertificateFactory
					.generateCertificate(localByteArrayInputStream);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		} finally {
			if (localByteArrayInputStream != null)
				localByteArrayInputStream.close();
		}

		if ((localCertificate == null) || (localKeyFactory == null)) {
			return null;
		}
		X509EncodedKeySpec localX509EncodedKeySpec = new X509EncodedKeySpec(
				localCertificate.getPublicKey().getEncoded());

		return localKeyFactory.generatePublic(localX509EncodedKeySpec);
	}

	static String b(byte[] paramArrayOfByte) {
		try {
			return d(paramArrayOfByte);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return null;
	}

	static String c(byte[] paramArrayOfByte) {
		try {
			return d(paramArrayOfByte);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return null;
	}

	private static String d(byte[] paramArrayOfByte) {
		StringBuilder localStringBuilder = new StringBuilder();
		if (paramArrayOfByte == null)
			return null;

		for (int i = 0; i < paramArrayOfByte.length; ++i) {
			String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
			if (str.length() == 1)
				str = '0' + str;

			localStringBuilder.append(str);
		}
		return localStringBuilder.toString();
	}

	private static byte[] e(byte[] paramArrayOfByte) throws IOException,
			Throwable {
		byte[] arrayOfByte = null;
		ByteArrayOutputStream localByteArrayOutputStream = null;
		GZIPOutputStream localGZIPOutputStream = null;

		if (paramArrayOfByte == null)
			return null;
		try {
			localByteArrayOutputStream = new ByteArrayOutputStream();
			localGZIPOutputStream = new GZIPOutputStream(
					localByteArrayOutputStream);
			localGZIPOutputStream.write(paramArrayOfByte);
			localGZIPOutputStream.finish();

			arrayOfByte = localByteArrayOutputStream.toByteArray();
		} catch (IOException localIOException) {
		} catch (Throwable localThrowable1) {
		} finally {
			if (localGZIPOutputStream != null)
				try {
					localGZIPOutputStream.close();
				} catch (Throwable localThrowable2) {
					throw localThrowable2;
				}

			if (localByteArrayOutputStream != null)
				try {
					localByteArrayOutputStream.close();
				} catch (Throwable localThrowable3) {
					throw localThrowable3;
				}

		}

		return arrayOfByte;
	}
}