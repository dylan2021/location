package com.sfmap.library.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sfmap.library.Callback;
import com.sfmap.library.util.DebugLog;

import org.xidea.el.impl.ReflectUtil;
import org.xidea.el.json.JSONDecoder;
import org.xidea.el.json.JSONEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public abstract class SQLiteMapperSupport<T> implements SQLiteMapper<T> {
	private SQLiteOpenHelper helper;
	private String tableName;
	private String primaryField;
	private ArrayList<Field> fields = new ArrayList<Field>();
	private HashMap<String, Field> fieldMap = new HashMap<String, Field>();
	private Class<T> type;
	private String autoField;

	private Object lock = new Object();

	public SQLiteMapperSupport(Context context, Class<T> c) {
		initFields(c);
		int version = initVersion(c);
		this.helper = new SQLiteOpenHelper(context, this.tableName, null,
				version) {
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				if (oldVersion < newVersion) {
					Method[] ms = type.getDeclaredMethods();
					if (ms != null) {
						for (Method m : ms) {
							Class<?>[] parameterTypes = m.getParameterTypes();
							if (Modifier.isStatic(m.getModifiers())
									&& parameterTypes.length == 2
									&& parameterTypes[0] == SQLiteDatabase.class
									&& parameterTypes[1] == String.class) {
								SQLiteUpdate u = m
										.getAnnotation(SQLiteUpdate.class);
								if (u != null && u.value() == oldVersion) {
									try {
										m.invoke(null, db, tableName);
										return;
									} catch (Exception e) {
										DebugLog.warn(e);
									}
									break;
								}
							}
						}
					}
					// update by default;
					db.execSQL("DROP TABLE IF EXISTS  " + tableName);
					db.execSQL(getCreateSQL());
				}
			}

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                if (oldVersion > newVersion) {
                    Method[] ms = type.getDeclaredMethods();
                    if (ms != null) {
                        for (Method m : ms) {
                            Class<?>[] parameterTypes = m.getParameterTypes();
                            if (Modifier.isStatic(m.getModifiers())
                                    && parameterTypes.length == 2
                                    && parameterTypes[0] == SQLiteDatabase.class
                                    && parameterTypes[1] == String.class) {
                                SQLiteUpdate u = m
                                        .getAnnotation(SQLiteUpdate.class);
                                if (u != null && u.value() == oldVersion) {
                                    try {
                                        m.invoke(null, db, tableName);
                                        return;
                                    } catch (Exception e) {
                                        DebugLog.warn(e);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    // update by default;
                    db.execSQL("DROP TABLE IF EXISTS  " + tableName);
                    db.execSQL(getCreateSQL());
                }
            }

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(getCreateSQL());
			}
		};
	}

	private void initFields(Class<T> c) {
		for (Field f : c.getDeclaredFields()) {
			SQLiteProperty prop = f.getAnnotation(SQLiteProperty.class);
			if (prop != null) {
				fields.add(f);
				String fn = f.getName();
				fieldMap.put(fn, f);
				if (this.primaryField == null
						&& prop.value().indexOf("PRIMARY") >= 0) {
					if (prop.value().indexOf("AUTOINCREMENT") >= 0) {
						this.autoField = fn;
					}
					this.primaryField = fn;
				}
				f.setAccessible(true);
			}

		}
	}

	private int initVersion(Class<T> c) {
		this.type = c;
		final SQLiteEntry entry = c.getAnnotation(SQLiteEntry.class);
		if (entry == null) {
			this.tableName = c.getSimpleName();
			return 1;
		} else {
			String name = entry.name();
			this.tableName = name.length() == 0 ? c.getSimpleName() : name;
			return entry.version();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidea.android.internal.ObjectMapper#get(java.lang.Object)
	 */
	@Override
	public T get(Object id) {
		return getByKey(this.primaryField, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidea.android.internal.ObjectMapper#getByKey(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public T getByKey(String field, Object value) {
		synchronized (lock) {
			try {
				SQLiteDatabase db = this.helper.getReadableDatabase();
				try {
					db.beginTransaction();
					Cursor cursor = db.query(tableName, null, field + "=?",
							new String[] { String.valueOf(toArg(value)) },
							null, null, null, null);
					try {
						if (cursor.moveToFirst()) {
							return toObject(cursor);
						}
					} finally {
						cursor.close();
					}
				} finally {
					db.endTransaction();
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidea.android.internal.ObjectMapper#query(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public List<T> query(String where, Object... selectionArgs) {
		synchronized (lock) {
			SQLiteDatabase db = this.helper.getReadableDatabase();
			try {
				db.beginTransaction();
				String[] args = toQueryParams(selectionArgs);
				Cursor cursor = db.query(tableName, null, where, args, null,
						null, null);
				try {
					ArrayList<T> result = new ArrayList<T>(cursor.getCount());
					while (cursor.moveToNext()) {
						result.add(toObject(cursor));
					}
					return result;
				} finally {
					cursor.close();
				}
			} finally {
				db.endTransaction();
				db.close();
			}
		}
	}

	private String[] toQueryParams(Object... selectionArgs) {
		if (selectionArgs == null) {
			return null;
		}
		String[] args = new String[selectionArgs.length];
		for (int i = 0; i < args.length; i++) {
			Object value = selectionArgs[i];
			args[i] = String.valueOf(toArg(value));
		}
		return args;
	}

	@SuppressWarnings("rawtypes")
	private Object toArg(Object value) {
		return value instanceof Enum ? ((Enum) value).ordinal() : value;
	}

	public void execSQL(String sql, Object... bindArgs) {
		synchronized (lock) {
			SQLiteDatabase db = this.helper.getReadableDatabase();
			try {
				db.beginTransaction();
				try {
					for (int i = 0; i < bindArgs.length; i++) {
						bindArgs[i] = toArg(bindArgs[i]);
					}
					db.execSQL(sql, bindArgs);
				} finally {
					db.endTransaction();
				}
			} finally {
				db.close();
			}
		}
	}

	public void querySQL(Callback<Cursor> callback, String sql,
			Object... bindArgs) {
		synchronized (lock) {
			SQLiteDatabase db = this.helper.getReadableDatabase();
			try {
				db.beginTransaction();
				Cursor cursor = db.rawQuery(sql, toQueryParams(bindArgs));
				try {
					callback.callback(cursor);
					db.setTransactionSuccessful();
				} finally {
					cursor.close();
				}
			} finally {
				db.endTransaction();
				db.close();
			}
		}
	}

	@Override
	public int count() {
		return count(null);
	}
	
	@Override
	public int count(String where, Object... selectionArgs) {
		synchronized (lock) {
			SQLiteDatabase db = this.helper.getReadableDatabase();
			try {
				db.beginTransaction();
				List<String> aList = new ArrayList<String>();
				if(selectionArgs != null){
					for(Object obj : selectionArgs){
						if(obj != null){
							aList.add(String.valueOf(obj));
						}
					}
				}
				String[] args = null;
				if(aList.size() > 0){
					 args = new String[aList.size()];
					 aList.toArray(args);
				}
				Cursor cursor = db.query(tableName, new String[]{"count(*)"}, where, args, null, null, null);
				try {
					int result = 0;
					if (cursor.moveToNext()) {
						result = cursor.getInt(0);
					}
					return result;
				} finally {
					cursor.close();
				}
			} finally {
				db.endTransaction();
				db.close();
			}
		}
	}

	@Override
	public T save(T t) {
		ContentValues values = buildContent(t);
		return save(t, values);
	}

	private T save(T t, ContentValues values) {
		synchronized (lock) {
			SQLiteDatabase db = this.helper.getWritableDatabase();
			try {
				if (autoField != null) {
					values.remove(autoField);
				}
				try {
					db.beginTransaction();
					long id = -1;
					id = db.insert(tableName, null, values);
					if (autoField != null) {
						ReflectUtil.setValue(t, autoField, id);
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}

			} finally {
				db.close();
			}
		}
		return t;
	}

	@Override
	public boolean remove(Object id) {
		if (id == null) {
			return false;
		}

		synchronized (lock) {
			SQLiteDatabase db = this.helper.getWritableDatabase();
			try {
				db.beginTransaction();
				try {
					int c = db.delete(tableName, this.primaryField + "=?",
							new String[] { String.valueOf(id) });
					db.setTransactionSuccessful();
					return c > 0;
				} finally {
					db.endTransaction();
				}
			} finally {
				db.close();
			}
		}

	}

	@Override
	public boolean update(ContentValues contents) {
		synchronized (lock) {
			SQLiteDatabase db = this.helper.getWritableDatabase();
			try {
				db.beginTransaction();
				try {
					int c = db.update(tableName, contents, this.primaryField
							+ "=?", new String[] { String.valueOf(contents
							.get(this.primaryField)) });
					db.setTransactionSuccessful();
					return c > 0;
				} finally {
					db.endTransaction();
				}
			} finally {
				db.close();
			}
		}
	}

	@Override
	public boolean update(T t) {
		ContentValues values = buildContent(t);
		return this.update(values);
	}

	public T saveOrUpdate(T t) {
		ContentValues values = buildContent(t);
		boolean existed = this.update(values);
		if (!existed) {
			this.save(t, values);
		}
		return t;
	}

	/****/

	@SuppressWarnings("rawtypes")
	private ContentValues buildContent(T t) {
		ContentValues values = new ContentValues();
		for (Field f : fields) {
			String name = f.getName();
			Object value = null;
			try {
				value = f.get(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			Class<?> fieldType = ReflectUtil.toWrapper(f.getType());
			if (value == null) {

			} else if (String.class == fieldType) {
				values.put(name, (String) value);
			} else if (URL.class == fieldType || URI.class == fieldType) {
				values.put(name, String.valueOf(value));
			} else if (Integer.class.isAssignableFrom(fieldType)
					|| Byte.class.isAssignableFrom(fieldType)
					|| Short.class.isAssignableFrom(fieldType)) {
				values.put(name, ((Number) value).intValue());
			} else if (Float.class.isAssignableFrom(fieldType)) {
				values.put(name, (Float) value);
			} else if (Double.class.isAssignableFrom(fieldType)) {
				values.put(name, (Double) value);
			} else if (Long.class.isAssignableFrom(fieldType)) {
				values.put(name, (Long) value);
			} else if (Boolean.class.isAssignableFrom(fieldType)) {
				values.put(name, ((Boolean) value) ? 1 : 0);
			} else if (Date.class.isAssignableFrom(fieldType)) {
				values.put(name, ((Date) value).getTime());
			} else if (byte[].class.isAssignableFrom(fieldType)) {
				values.put(name, (byte[]) value);
			} else if (Enum.class.isAssignableFrom(fieldType)) {
				values.put(name, ((Enum) value).ordinal());
			} else {
				values.put(name, JSONEncoder.encode(value));
			}

		}
		return values;
	}

	@SuppressWarnings("static-access")
	private T toObject(Cursor cursor) {
		T o;
		try {
			o = type.newInstance();
		} catch (InstantiationException e) {
			DebugLog.error("对象创建失败:" + type, e);
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			DebugLog.error("对象创建失败:" + type, e);
			throw new RuntimeException(e);
		}
		for (Field field : this.fields) {
			String name = field.getName();
			int i = cursor.getColumnIndex(name);
			if (i < 0) {
				DebugLog.warn("缺少属性:" + type + "#" + name);
				continue;
			}
			Class<?> fieldType = ReflectUtil.toWrapper(field.getType());
			Object value = null;

			if (String.class == fieldType) {
				value = cursor.getString(i);
			} else if (URL.class == fieldType) {
				try {
					value = new URL(cursor.getString(i));
				} catch (MalformedURLException e) {
					DebugLog.warn("无效URL:" + type, e);
				}
			} else if (URI.class == fieldType) {
				value = URI.create(cursor.getString(i));
			} else if (Number.class.isAssignableFrom(fieldType)) {
				if (Float.class == fieldType) {
					value = cursor.getFloat(i);
				} else if (Double.class == fieldType) {
					value = cursor.getDouble(i);
				} else if (Long.class == fieldType) {
					value = cursor.getLong(i);
				} else {
					value = cursor.getInt(i);
				}
			} else if (Boolean.class == fieldType) {
				value = cursor.getInt(i) != 0;
			} else if (Date.class == fieldType) {
				value = new Date(cursor.getLong(i));
			} else if (byte[].class == fieldType) {
				value = cursor.getBlob(i);
			} else if (Enum.class.isAssignableFrom(fieldType)) {
				int ordi = cursor.getInt(i);
				value = ReflectUtil.getEnum(ordi, fieldType);
			} else {
				String text = cursor.getString(i);
				try {
					if (text != null) {
						value = new JSONDecoder(false).decode(text, fieldType);
					}
				} catch (Exception e) {
					DebugLog.error("数据转换失败:" + type, e);
				}
			}
			ReflectUtil.setValue(o, name, value);
		}
		return o;
	}

	private String getCreateSQL() {
		StringBuilder buf = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
				.append(tableName).append('(');
		StringBuilder index = new StringBuilder();
		for (Field f : fields) {
			SQLiteProperty prop = f.getAnnotation(SQLiteProperty.class);
			Class<?> type = ReflectUtil.toWrapper(f.getType());
			String name = f.getName();
			buf.append(name).append(' ');
			if (prop.index()) {
				if (index.length() > 0) {
					index.append(',');
				}
				index.append(name);
			}
			if (String.class == type) {
				buf.append("TEXT");
			} else if (Integer.class.isAssignableFrom(type)
					|| Long.class.isAssignableFrom(type)
					|| Byte.class.isAssignableFrom(type)
					|| Date.class.isAssignableFrom(type)
					|| Short.class.isAssignableFrom(type)
					|| Boolean.class.isAssignableFrom(type)
					|| Enum.class.isAssignableFrom(type)) {
				buf.append("INTEGER");
			} else if (Float.class.isAssignableFrom(type)
					|| Double.class.isAssignableFrom(type)) {
				buf.append("REAL");
			} else if (byte[].class.isAssignableFrom(type)) {
				buf.append("BLOB");
			} else {
				buf.append("TEXT");
			}
			buf.append(' ').append(prop.value());
			buf.append(',');
		}
		buf.setCharAt(buf.length() - 1, ')');
		if (index.length() > 0) {
			buf.append("; CREATE INDEX mapper_index ON ").append(tableName)
					.append('(').append(index).append(')');
		}
		buf.append("; ");
		// System.out.println(buf);
		return buf.toString();
	}
}
