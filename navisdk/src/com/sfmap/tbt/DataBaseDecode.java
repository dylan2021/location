package com.sfmap.tbt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBaseDecode {
	private DB a;
	private SQLiteDatabase b;
	private DBCreator c;

	public DataBaseDecode(Context paramContext, DBCreator paramaa) {
		this.a = new DB(paramContext, paramaa.a(), null, paramaa.b(), paramaa);

		this.c = paramaa;
	}

	private SQLiteDatabase a() {
		this.b = this.a.getReadableDatabase();
		return this.b;
	}

	private SQLiteDatabase b() {
		this.b = this.a.getWritableDatabase();
		return this.b;
	}

	public static String a(Map<String, String> paramMap) {
		if (paramMap == null)
			return "";

		StringBuilder localStringBuilder = new StringBuilder();
		int i = 1;
		for (String str : paramMap.keySet()) {
			if (i != 0) {
				localStringBuilder.append(str).append(" = '")
						.append((String) paramMap.get(str)).append("'");
				i = 0;
			} else {
				localStringBuilder.append(" and ").append(str).append(" = '")
						.append((String) paramMap.get(str)).append("'");
			}

		}

		return localStringBuilder.toString();
	}

	public <T> void a(String paramString, SQlEntity<T> paramac) {
		synchronized (this.c) {
			if ((paramac.b() != null) && (paramString != null))
				// break label23;
				return;

			/* label23: */
			if ((this.b != null) && (!(this.b.isReadOnly()))) /* break label48; */
				this.b = b();

			/* label48: */if (this.b != null) /* break label58; */
				return;
			try {
				/* label58: */this.b.delete(paramac.b(), paramString, null);
			} catch (Throwable localThrowable) {
				BasicLogHandler.a(localThrowable, "DataBase", "deleteData");
				localThrowable.printStackTrace();
			} finally {
				if (this.b != null) {
					this.b.close();
					this.b = null;
				}
			}
		}
	}

	public <T> void b(String paramString, SQlEntity<T> paramac) {
		synchronized (this.c) {
			if ((paramac != null) && (paramString != null)
					&& (paramac.b() != null)) /* break label27; */
				return;

			/* label27: */ContentValues localContentValues = paramac.a();
			if (localContentValues != null) /* break label43; */
				return;

			/* label43: */if ((this.b != null) && (!(this.b.isReadOnly()))) /*
																			 * break
																			 * label68
																			 * ;
																			 */
				this.b = b();

			/* label68: */if (this.b != null) /* break label78; */
				return;
			try {
				/* label78: */this.b.update(paramac.b(), localContentValues,
						paramString, null);
			} catch (Throwable localThrowable) {
				BasicLogHandler.a(localThrowable, "DataBase", "updateData");
				localThrowable.printStackTrace();
			} finally {
				if (this.b != null) {
					this.b.close();
					this.b = null;
				}
			}
		}
	}

	public <T> void a(SQlEntity<T> paramac) {
		synchronized (this.c) {
			if ((this.b == null) || (this.b.isReadOnly()))
				this.b = b();

			if (this.b != null) /* break label42; */
				return;
			try {
				/* label42: */a(this.b, paramac);
			} catch (Throwable localThrowable) {
				BasicLogHandler.a(localThrowable, "DataBase", "insertData");
				localThrowable.printStackTrace();
			} finally {
				if (this.b != null) {
					this.b.close();
					this.b = null;
				}
			}
		}
	}

	private <T> void a(SQLiteDatabase paramSQLiteDatabase, SQlEntity<T> paramac) {
		if ((paramac == null) || (paramSQLiteDatabase == null))
			return;

		ContentValues localContentValues = paramac.a();
		if ((localContentValues == null) || (paramac.b() == null))
			return;

		paramSQLiteDatabase.insert(paramac.b(), null, localContentValues);
	}

	public <T> List<T> c(String paramString, SQlEntity<T> paramac) {
		synchronized (this.c) {
			ArrayList localArrayList1 = new ArrayList();
			Cursor localCursor = null;
			if (this.b == null)
				this.b = a();

			if ((this.b != null) && (paramac.b() != null)
					&& (paramString != null)) /* break label59; */
				return localArrayList1;
			try {
				label59: localCursor = this.b.query(paramac.b(), null,
						paramString, null, null, null, null);

				if (localCursor == null) {
					this.b.close();
					this.b = null;
					ArrayList localArrayList2 = localArrayList1;
					try {
						if (localCursor != null)
							localCursor.close();
					} catch (Throwable localThrowable6) {
						BasicLogHandler.a(localThrowable6, "DataBase", "searchListData");

						localThrowable6.printStackTrace();
					}

					return localArrayList2;
				}
				while (localCursor.moveToNext())
					localArrayList1.add(paramac.a(localCursor));
			} catch (Throwable localThrowable5) {
				BasicLogHandler.a(localThrowable5, "DataBase", "searchListData");
				localThrowable5.printStackTrace();
			} finally {
				try {
					if (localCursor != null)
						localCursor.close();
				} catch (Throwable localThrowable8) {
					BasicLogHandler.a(localThrowable8, "DataBase", "searchListData");

					localThrowable8.printStackTrace();
				}
				try {
					if (this.b != null) {
						this.b.close();
						this.b = null;
					}
				} catch (Throwable localThrowable9) {
					BasicLogHandler.a(localThrowable9, "DataBase", "searchListData");

					localThrowable9.printStackTrace();
				}
			}

			return localArrayList1;
		}
	}
}