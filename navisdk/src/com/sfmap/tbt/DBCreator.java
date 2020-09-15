package com.sfmap.tbt;

import android.database.sqlite.SQLiteDatabase;

/**
 * 原类名:aa
 */
public abstract interface DBCreator {
	public abstract void a(SQLiteDatabase paramSQLiteDatabase);

	public abstract void a(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2);

	public abstract String a();

	public abstract int b();
}