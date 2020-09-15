package com.sfmap.tbt;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.sfmap.tbt.util.AppInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * 客户端信息
 */
public class CInfoDecode {
	public static String rsaInfo(Context paramContext, ProductInfoDecode paramp,
			Map<String, String> paramMap) {
		String str1 = null;
		try {
			String str2 = c(paramContext, paramp, paramMap);
			if ("".equals(str2)) {
				return null;
			}
			byte[] arrayOfByte = str2.getBytes("utf-8");
			str1 = aESData(paramContext, arrayOfByte);
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			BasicLogHandler.a(localUnsupportedEncodingException, "CInfo", "rsaInfo");
			localUnsupportedEncodingException.printStackTrace();
		}

		return str1;
	}

	public static String initXInfo(Context paramContext, ProductInfoDecode paramp,
			Map<String, String> paramMap) {
		try {
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			String str1 = DeviceInfoDecode.n(paramContext);

			a(localByteArrayOutputStream, str1);

			a(localByteArrayOutputStream, "");
			String str2 = DeviceInfoDecode.a(paramContext);
			if (str2 == null) {
				str2 = "";
			}

			a(localByteArrayOutputStream, str2);

			String str3 = AppInfo.getPackageName(paramContext);

			a(localByteArrayOutputStream, str3);

			String str4 = Build.MODEL;

			a(localByteArrayOutputStream, str4);

			String str5 = Build.MANUFACTURER;

			a(localByteArrayOutputStream, str5);

			String str6 = Build.DEVICE;

			a(localByteArrayOutputStream, str6);

			String str7 = AppInfo.getApplicationName(paramContext);

			a(localByteArrayOutputStream, str7);

			String str8 = AppInfo.getApplicationVersion(paramContext);

			a(localByteArrayOutputStream, str8);

			String str9 = String.valueOf(Build.VERSION.SDK_INT);

			a(localByteArrayOutputStream, str9);

			String str10 = DeviceInfoDecode.o(paramContext);

			a(localByteArrayOutputStream, str10);

			String str11 = DeviceInfoDecode.m(paramContext);

			a(localByteArrayOutputStream, str11);

			String str12 = DeviceInfoDecode.j(paramContext) + "";

			a(localByteArrayOutputStream, str12);

			String str13 = DeviceInfoDecode.h(paramContext) + "";

			a(localByteArrayOutputStream, str13);

			String str14 = DeviceInfoDecode.p(paramContext);

			a(localByteArrayOutputStream, str14);

			String str15 = DeviceInfoDecode.f(paramContext);
			a(localByteArrayOutputStream, str15);

			a(localByteArrayOutputStream, "");

			a(localByteArrayOutputStream, "");

			a(localByteArrayOutputStream, "");

			a(localByteArrayOutputStream, "");

			byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();

			byte[] arrayOfByte2 = Utils.a(arrayOfByte1);
			PublicKey localPublicKey = null;

			localPublicKey = Utils.a(paramContext);

			byte[] arrayOfByte3 = new byte[117];

			System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 0, 117);

			byte[] arrayOfByte4 = EncryptDecode.a(arrayOfByte3, localPublicKey);

			byte[] arrayOfByte5 = null;

			if (arrayOfByte2.length > 117) {
				arrayOfByte5 = new byte[128 + arrayOfByte2.length - 117];

				System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, 128);

				System.arraycopy(arrayOfByte2, 117, arrayOfByte5, 128,
						arrayOfByte2.length - 117);
			} else {
				arrayOfByte5 = new byte[128];
				System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, 128);
			}

			String str16 = EncryptDecode.encodeBase64(arrayOfByte5);

			return str16;
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "CInfo", "InitXInfo");
			localThrowable.printStackTrace();
		}
		return null;
	}

	private static String c(Context paramContext, byte[] paramArrayOfByte)
			throws InvalidKeyException, IOException, InvalidKeySpecException,
			NoSuchPaddingException, NoSuchAlgorithmException,
			IllegalBlockSizeException, BadPaddingException,
			CertificateException {
		KeyGenerator localKeyGenerator = null;
		localKeyGenerator = KeyGenerator.getInstance("AES");
		if (localKeyGenerator == null) {
			return null;
		}
		localKeyGenerator.init(256);
		SecretKey localSecretKey = localKeyGenerator.generateKey();
		byte[] arrayOfByte1 = localSecretKey.getEncoded();

		PublicKey localPublicKey = null;

		byte[] arrayOfByte2 = null;
		byte[] arrayOfByte3 = null;
		byte[] arrayOfByte4 = null;

		localPublicKey = Utils.a(paramContext);

		if (localPublicKey == null) {
			return null;
		}
		arrayOfByte2 = EncryptDecode.a(arrayOfByte1, localPublicKey);

		arrayOfByte3 = EncryptDecode.aesEncrypt(arrayOfByte1, paramArrayOfByte);

		arrayOfByte4 = new byte[arrayOfByte2.length + arrayOfByte3.length];

		System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 0, arrayOfByte2.length);

		System.arraycopy(arrayOfByte3, 0, arrayOfByte4, arrayOfByte2.length,
				arrayOfByte3.length);

		byte[] arrayOfByte5 = Utils.a(arrayOfByte4);

		if (arrayOfByte5 != null) {
			return EncryptDecode.encodeBase64(arrayOfByte5);
		}

		return "";
	}

	public static String a(Context paramContext, byte[] paramArrayOfByte) {
		try {
			return c(paramContext, paramArrayOfByte);
		} catch (InvalidKeyException localInvalidKeyException) {
			localInvalidKeyException.printStackTrace();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			localNoSuchAlgorithmException.printStackTrace();
		} catch (NoSuchPaddingException localNoSuchPaddingException) {
			localNoSuchPaddingException.printStackTrace();
		} catch (IllegalBlockSizeException localIllegalBlockSizeException) {
			localIllegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException localBadPaddingException) {
			localBadPaddingException.printStackTrace();
		} catch (InvalidKeySpecException localInvalidKeySpecException) {
			localInvalidKeySpecException.printStackTrace();
		} catch (CertificateException localCertificateException) {
			localCertificateException.printStackTrace();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return "";
	}

	public static String aESData(Context paramContext, byte[] paramArrayOfByte) {
		try {
			return c(paramContext, paramArrayOfByte);
		} catch (InvalidKeyException localInvalidKeyException) {
			BasicLogHandler.a(localInvalidKeyException, "CInfo", "AESData");
			localInvalidKeyException.printStackTrace();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			BasicLogHandler.a(localNoSuchAlgorithmException, "CInfo", "AESData");
			localNoSuchAlgorithmException.printStackTrace();
		} catch (NoSuchPaddingException localNoSuchPaddingException) {
			BasicLogHandler.a(localNoSuchPaddingException, "CInfo", "AESData");
			localNoSuchPaddingException.printStackTrace();
		} catch (IllegalBlockSizeException localIllegalBlockSizeException) {
			BasicLogHandler.a(localIllegalBlockSizeException, "CInfo", "AESData");
			localIllegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException localBadPaddingException) {
			BasicLogHandler.a(localBadPaddingException, "CInfo", "AESData");
			localBadPaddingException.printStackTrace();
		} catch (InvalidKeySpecException localInvalidKeySpecException) {
			BasicLogHandler.a(localInvalidKeySpecException, "CInfo", "AESData");
			localInvalidKeySpecException.printStackTrace();
		} catch (CertificateException localCertificateException) {
			BasicLogHandler.a(localCertificateException, "CInfo", "AESData");
			localCertificateException.printStackTrace();
		} catch (IOException localIOException) {
			BasicLogHandler.a(localIOException, "CInfo", "AESData");
			localIOException.printStackTrace();
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "CInfo", "AESData");
			localThrowable.printStackTrace();
		}
		return "";
	}

	public static String a(Context paramContext, ProductInfoDecode paramp) {
		StringBuilder localStringBuilder = new StringBuilder();
		try {
			String str = DeviceInfoDecode.r(paramContext);
			localStringBuilder.append("\"sim\":\"").append(str)
					.append("\",\"sdkversion\":\"").append(paramp.sdkversion)
					.append("\",\"product\":\"").append(paramp.product)
					.append("\",\"ed\":\"").append(paramp.d())
					.append("\",\"nt\":\"").append(DeviceInfoDecode.i(paramContext))
					.append("\",\"np\":\"").append(DeviceInfoDecode.q(paramContext))
					.append("\",\"mnc\":\"").append(DeviceInfoDecode.g(paramContext))
					.append("\",\"ant\":\"").append(DeviceInfoDecode.k(paramContext))
					.append("\"");
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}

		return localStringBuilder.toString();
	}

	private static String c(Context paramContext, ProductInfoDecode paramp,
			Map<String, String> paramMap) {
		StringBuilder localStringBuilder = new StringBuilder();
		try {
			String str1 = DeviceInfoDecode.n(paramContext);

			localStringBuilder.append("diu=").append(str1);
			localStringBuilder.append("&pkg=").append(AppInfo.getPackageName(paramContext));
			localStringBuilder.append("&model=");
			localStringBuilder.append(Build.MODEL);

			localStringBuilder.append("&manufacture=").append(
					Build.MANUFACTURER);

			localStringBuilder.append("&device=").append(Build.DEVICE);
			localStringBuilder.append("&appname=").append(AppInfo.getApplicationName(paramContext));

			localStringBuilder.append("&appversion=").append(AppInfo.getApplicationVersion(paramContext));

			localStringBuilder.append("&sysversion=");
			localStringBuilder.append(Build.VERSION.SDK_INT);
			String str2 = DeviceInfoDecode.o(paramContext);
			localStringBuilder.append("&sim=").append(str2);
			localStringBuilder.append("&resolution=" + DeviceInfoDecode.m(paramContext));
			localStringBuilder.append("&mac=").append(DeviceInfoDecode.d(paramContext));
			localStringBuilder.append("&wifis=").append(DeviceInfoDecode.c(paramContext));

			localStringBuilder.append("&ant=").append(DeviceInfoDecode.j(paramContext));

			localStringBuilder.append("&nt=");
			localStringBuilder.append(DeviceInfoDecode.h(paramContext));
			String str3 = DeviceInfoDecode.p(paramContext);
			localStringBuilder.append("&np=");
			localStringBuilder.append(str3);
			localStringBuilder.append("&mnc=").append(DeviceInfoDecode.f(paramContext));
			String str4 = DeviceInfoDecode.b(paramContext);
			if (!(TextUtils.isEmpty(str4)))
				localStringBuilder.append("&wifi=").append(str4);

			String str5 = DeviceInfoDecode.a(paramContext);
			if (str5 == null) {
				str5 = "";
			}

			localStringBuilder.append("&bts=").append(DeviceInfoDecode.e(paramContext));
			localStringBuilder.append("&tid=").append(str5);

			if (paramMap != null) {
				for (Map.Entry localEntry : paramMap.entrySet()) {
					localStringBuilder.append("&")
							.append((String) localEntry.getKey()).append("=")
							.append((String) localEntry.getValue());
				}

			}

		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "CInfo", "InitXInfo");
			localThrowable.printStackTrace();
		}

		return localStringBuilder.toString();
	}

	private static void a(ByteArrayOutputStream paramByteArrayOutputStream,
			String paramString) {
		if (!(TextUtils.isEmpty(paramString))) {
			int i = paramString.getBytes().length;
			byte b = 0;

			if (i > 255) {
				b = -1;
			} else {
				b = (byte) paramString.getBytes().length;
			}

			writeField(paramByteArrayOutputStream, b, paramString.getBytes());
		} else {
			// a(paramByteArrayOutputStream, 0, new byte[0]);
			writeField(paramByteArrayOutputStream, (byte) 0, null);
		}
	}

	private static void writeField(ByteArrayOutputStream paramByteArrayOutputStream,
			byte paramByte, byte[] paramArrayOfByte) {
		try {
			paramByteArrayOutputStream.write(new byte[] { paramByte });
			if ((((paramByte > 0) ? 1 : 0) & (((paramByte & 0xFF) < 255) ? 1
					: 0)) != 0) {
				paramByteArrayOutputStream.write(paramArrayOfByte);
			} else if ((paramByte & 0xFF) == 255)
				paramByteArrayOutputStream.write(paramArrayOfByte, 0, 255);
		} catch (IOException localIOException) {
			BasicLogHandler.a(localIOException, "CInfo", "writeField");
			localIOException.printStackTrace();
		}
	}

	public static String getTS() {
		String str1 = null;
		try {
			str1 = String.valueOf(System.currentTimeMillis());
			String str2 = "1";
			int i = str1.length();

			str1 = str1.substring(0, i - 2) + str2 + str1.substring(i - 1);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "CInfo", "getTS");
			localThrowable.printStackTrace();
		}
		return str1;
	}

	public static String Scode(Context paramContext, String paramString1,
			String paramString2) {
		String str1 = null;
		try {
			String str2 = AppInfo.getSHA1AndPackage(paramContext);

			str1 = MD5DECODE.getMD5(str2 + ":"
					+ paramString1.substring(0, paramString1.length() - 3)
					+ ":" + paramString2);
		} catch (Throwable localThrowable) {
			BasicLogHandler.a(localThrowable, "CInfo", "Scode");
			localThrowable.printStackTrace();
		}
		return str1;
	}
}