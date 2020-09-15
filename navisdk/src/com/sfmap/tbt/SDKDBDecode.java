package com.sfmap.tbt;

import android.content.Context;
import java.util.List;

public class SDKDBDecode {
	private DataBaseDecode a;
	private Context b;

	public SDKDBDecode(Context paramContext) {
		this.b = paramContext;
		this.a = a(this.b);
	}

	private DataBaseDecode a(Context paramContext) {
		DataBaseDecode localab = null;
		try {
			localab = new DataBaseDecode(paramContext, LogDBCreator.c());
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "SDKDB", "getDB");
			localThrowable.printStackTrace();
		}
		return localab;
	}

	public List<ProductInfoDecode> a() {
		List localList = null;
		try {
			SDKEntity localam = new SDKEntity();
			String str = SDKEntity.c();
			localList = this.a.c(str, localam);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return localList;
	}
}