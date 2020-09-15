package com.sfmap.tbt;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * 原类名：x
 */
public class Log
{
	static final String a = "/a/";
	static final String b = "b";
	static final String c = "c";
	static final String d = "d";

	static void a(final Context paramContext, final Throwable paramThrowable, int paramInt, final String paramString1, final String paramString2)
	{
		try
		{
			ExecutorService localExecutorService = SDKLogHandler.a();
			if ((localExecutorService == null) || (localExecutorService.isShutdown())) {
				return;
			}
//			localExecutorService.submit(new Runnable()
//			{
//				public void run()
//				{
//					try
//					{
//						y localy = y.a(x.this.a);
//						if (localy == null) {
//							return;
//						}
//						localy.a(paramContext, paramThrowable, paramString1, paramString2);
//					}
//					catch (Throwable localThrowable)
//					{
//						localThrowable.printStackTrace();
//					}
//				}
//			});
		}
		catch (RejectedExecutionException localRejectedExecutionException) {}catch (Throwable localThrowable)
		{
			localThrowable.printStackTrace();
		}
	}
}
