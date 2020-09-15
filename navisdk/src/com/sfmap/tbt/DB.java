package com.sfmap.tbt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 原类名：af
 */
public class DB extends SQLiteOpenHelper {
	private DBCreator a;

	public DB(Context paramContext, String paramString,
			  SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt,
			  DBCreator paramaa) {
		super(paramContext, paramString, paramCursorFactory, paramInt);
		this.a = paramaa;
	}

	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		this.a.a(paramSQLiteDatabase);
	}

	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2) {
		this.a.a(paramSQLiteDatabase, paramInt1, paramInt2);
	}
}