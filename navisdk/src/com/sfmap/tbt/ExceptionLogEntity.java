package com.sfmap.tbt;

import android.database.Cursor;

/**
 * 原类名：ag
 */
class ExceptionLogEntity extends LogEntity {
	public String b() {
		return LogDBCreator.c;
	}

	// not exist before
	@Override
	public LogInfo a(Cursor paramCursor) {
		return null;
	}
}