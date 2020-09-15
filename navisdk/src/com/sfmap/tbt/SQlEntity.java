package com.sfmap.tbt;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 原类名：ac
 */
public  interface SQlEntity<T> {
	 ContentValues a();

	 T a(Cursor paramCursor);

	 String b();
}