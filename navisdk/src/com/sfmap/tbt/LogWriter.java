package com.sfmap.tbt;

import android.content.Context;
import android.os.Looper;

import com.sfmap.tbt.util.AppInfo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * 原类名：y
 */
abstract class LogWriter {
	private ProductInfoDecode a;

	static LogWriter a(int paramInt) {
		Object localObject = null;
		switch (paramInt) {
		case 0:
			localObject = new CrashLogWriter();
			break;
		case 1:
			localObject = new ExceptionLogWriter();
			break;
		case 2:
			localObject = new ANRWriterDecode();
			break;
		default:
			return null;
		}
		return ((LogWriter) localObject);
	}

	void a(Context paramContext, Throwable paramThrowable, String paramString1,
			String paramString2) {
		List<ProductInfoDecode> localList = a(paramContext);
		if ((localList == null) || (localList.size() == 0)) {
			return;
		}

		String str1 = a(paramThrowable);
		if ((str1 == null) || ("".equals(str1))) {
			return;
		}

		for (ProductInfoDecode localp : localList) {
			String[] arrayOfString = localp.f();
			if (a(arrayOfString, str1)) {
				a(localp);
				String str2 = c();
				String str3 = a(paramContext, localp);
				String str4 = b(paramContext);
				String str5 = b(paramThrowable);
				if ((str5 == null) || ("".equals(str5)))
					return;

				int i = a();
				StringBuilder localStringBuilder = new StringBuilder();
				if (paramString1 != null) {
					localStringBuilder.append("class:").append(paramString1);
				}

				if (paramString2 != null) {
					localStringBuilder.append(" method:").append(paramString2)
							.append("$").append("<br/>");
				}

				localStringBuilder.append(str1);
				String str6 = a(str1);
				String str7 = a(str4, str3, str2, i, str5,
						localStringBuilder.toString());

				if ((str7 == null) || ("".equals(str7)))
					return;

				String str8 = a(paramContext, str7);
				String str9 = b();

				synchronized (Looper.getMainLooper()) {
					LogDBOperation localai = new LogDBOperation(paramContext);
					boolean bool = a(paramContext, str6, str9, str8, localai);

					a(localai, localp.a(), str6, i, bool);
				}
			}
		}
	}

	protected void a(ProductInfoDecode paramp) {
		this.a = paramp;
	}

	private List<ProductInfoDecode> a(Context paramContext) {
		List localList = null;
		try {
			synchronized (Looper.getMainLooper()) {
				SDKDBDecode localal = new SDKDBDecode(paramContext);
				localList = localal.a();
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return localList;
	}

	private void a(LogDBOperation paramai, String paramString1, String paramString2,
				   int paramInt, boolean paramBoolean) {
		LogInfo localak = new LogInfo();

		localak.a(0);

		localak.b(paramString1);
		localak.a(paramString2);
		paramai.a(localak, paramInt);
	}

	protected abstract int a();

	protected abstract String a(String paramString);

	protected abstract FileOperationListener a(LogDBOperation paramai);

	protected abstract String b();

	private String a(String paramString1, String paramString2,
			String paramString3, int paramInt, String paramString4,
			String paramString5) {
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer.append(paramString2).append(",")
				.append("\"timestamp\":\"");
		localStringBuffer.append(paramString3);
		localStringBuffer.append("\",\"et\":\"");
		localStringBuffer.append(paramInt);
		localStringBuffer.append("\",\"classname\":\"");
		localStringBuffer.append(paramString4);
		localStringBuffer.append("\",");
		localStringBuffer.append("\"detail\":\"");
		localStringBuffer.append(paramString5);
		localStringBuffer.append("\"");

		return localStringBuffer.toString();
	}

	private String a(Context paramContext, String paramString) {
		String str = null;
		try {
			byte[] arrayOfByte = paramString.getBytes("UTF-8");
			str = CInfoDecode.a(paramContext, arrayOfByte);
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			localUnsupportedEncodingException.printStackTrace();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return str;
	}

	private String c() {
		String str = null;

		Date localDate = new Date();
		str = z.a(localDate.getTime());

		return str;
	}

	protected String a(Throwable paramThrowable) {
		String str = null;
		try {
			str = z.a(paramThrowable);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return str;
	}

	private String b(Throwable paramThrowable) {
		return paramThrowable.toString();
	}

	private String a(Context paramContext, ProductInfoDecode paramp) {
		String str = CInfoDecode.a(paramContext, paramp);
		return str;
	}

	private String b(Context paramContext) {
		String str = AppInfo.getSystemAk(paramContext);
		return str;
	}

	private boolean a(Context paramContext, String paramString1,
			String paramString2, String paramString3, LogDBOperation paramai) {
		OutputStream localOutputStream = null;
		DiskLruCache localao = null;
		try {
			int k;
			StringBuilder localStringBuilder = new StringBuilder();
			String str1 = paramContext.getFilesDir().getAbsolutePath();
			localStringBuilder.append(str1);
			localStringBuilder.append(Log.a);
			localStringBuilder.append(paramString2);
			String str2 = localStringBuilder.toString();
			File localFile = new File(str2);
			if ((!(localFile.exists())) && (!(localFile.mkdirs()))) {
				// int i = 0; jsr 136; return i;
				return false;
			}

			localao = DiskLruCache.a(localFile, 1, 1, 20480L);

			localao.a(a(paramai));
			DiskLruCache.b localb = localao.a(paramString1);

			if (localb != null) {
				// int j = 0; jsr 91; return j;
				return false;
			}

			byte[] arrayOfByte = paramString3.getBytes("UTF-8");
			DiskLruCache.a locala = localao.b(paramString1);
			localOutputStream = locala.a(0);
			localOutputStream.write(arrayOfByte);
			locala.a();
			localao.b();

			return true;
			// return 1;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		} catch (Throwable localThrowable1) {
			localThrowable1.printStackTrace();
		} finally {
			if (localOutputStream != null)
				try {
					localOutputStream.close();
				} catch (Throwable localThrowable2) {
					localThrowable2.printStackTrace();
				}

			if ((localao != null) && (!(localao.a())))
				try {
					localao.close();
				} catch (Throwable localThrowable3) {
					localThrowable3.printStackTrace();
				}
		}

		return false;
	}
	protected boolean a(String[] paramArrayOfString, String paramString)
	{
		boolean bool = false;
		try
		{
			if ((paramArrayOfString == null) || (paramString == null)) {
				return bool;
			}
			for (String str : paramArrayOfString) {
				if (paramString.indexOf(str) != -1) {
					return true;
				}
			}
		}
		catch (Throwable localThrowable)
		{
			localThrowable.printStackTrace();
		}
		return bool;
	}

}