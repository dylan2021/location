package com.sfmap.tbt;

import android.content.Context;
import java.util.List;


/**
 * 原类名：ai
 */
public class LogDBOperation {
	private DataBaseDecode a;

	public LogDBOperation(Context paramContext) {
		this.a = new DataBaseDecode(paramContext, LogDBCreator.c());
	}

	private LogEntity a(int paramInt) {
		Object localObject = null;
		switch (paramInt) {
		case 0:
			localObject = new CrashLogEntity();
			break;
		case 1:
			localObject = new ExceptionLogEntity();
			break;
		case 2:
			localObject = new AnrLogEntity();
			break;
		default:
			return null;
		}
		return ((LogEntity) localObject);
	}

	public void a(String paramString, int paramInt) {
		try {
			b(paramString, paramInt);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	private void b(String paramString, int paramInt) {
		String str = LogEntity.a(paramString);
		LogEntity localaj = a(paramInt);
		this.a.a(str, localaj);
	}

	public void a(LogInfo paramak, int paramInt) {
		try {
			LogEntity localaj = a(paramInt);
			switch (paramInt) {
			case 0:
				a(paramak, localaj);
				break;
			case 1:
				b(paramak, localaj);
				break;
			case 2:
				b(paramak, localaj);
				break;
			default:
				return;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	private void a(LogInfo paramak, LogEntity paramaj) {
		paramaj.a(paramak);
		this.a.a(paramaj);
	}

	private void b(LogInfo paramak, LogEntity paramaj) {
		String str = LogEntity.a(paramak.b());

		List localList = this.a.c(str, paramaj);
		if ((localList == null) || (localList.size() == 0)) {
			paramaj.a(paramak);
			this.a.a(paramaj);
		} else {
			LogInfo localak = (LogInfo) localList.get(0);
			if (paramak.a() == 0) {
				int i = localak.d();
				int j = i + 1;
				localak.b(j);
			} else {
				localak.b(0);
			}
			paramaj.a(localak);
			this.a.b(str, paramaj);
		}
	}
}