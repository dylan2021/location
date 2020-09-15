package com.sfmap.library.io;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.sfmap.library.io.imp.AssetStorageImpl;
import com.sfmap.library.io.imp.DynamicStorageImpl;
import com.sfmap.library.io.imp.KeyValueStorageImpl;
import com.sfmap.library.io.imp.SQLiteMapperImpl;
import com.sfmap.library.util.DebugLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储工厂类
 */
public class StorageFactory {
	private Map<Object, Object> cache = new HashMap<Object, Object>();

	private StorageFactory() {
	}

	/**
	 * 实例化StorageFactory对象
	 * @hide
	 */
	public static final StorageFactory INSTANCE = new StorageFactory();

	/**
	 * 获得强类型的SQLite 数据库->对象 映射
	 * @param type			储存对象
	 * @param application	上下文
	 * @param <T>			存储管理
     * @return
     */
	@SuppressWarnings("unchecked")
	public <T> SQLiteMapper<T> getSQLiteStorage(Class<T> type,
												Context application) {
		SQLiteMapper<T> impl = (SQLiteMapper<T>) cache.get(type);
		if (impl == null) {
			synchronized (cache) {
				impl = (SQLiteMapper<T>) cache.get(type);
				if (impl == null) {
					impl = new SQLiteMapperImpl<T>(application, type);
					cache.put(type, impl);
				}
			}
		}
		return impl;
	}

	/**
	 * 获得强类型的keyValue 结构化存储
	 * @param type			储存对象
	 * @param application	上下文
	 * @param <T>			存储管理
     * @return
     */
	@SuppressWarnings("unchecked")
	public <T extends KeyValueStorage<?>> T getKeyValueStorage(Class<T> type,
															   Context application) {
		if (!type.isInterface()) {
			DebugLog.error("KvStroage  必须从接口创建！" + type);
		}
		T impl = (T) cache.get(type);
		if (impl == null) {
			synchronized (cache) {
				impl = (T) cache.get(type);
				if (impl == null) {
					String key;
					try {
						//sp->初始化存储名称
						KeyValueStorage.StorageKey field = type
							.getAnnotation(KeyValueStorage.StorageKey.class);
						key = field == null ? type.getName() : (String) field
							.name();
					} catch (Exception e) {
						key = type.getName();
					}
					impl = KeyValueStorageImpl.buildKVStroage(type, application, key);
					cache.put(type, impl);
					buildUpgrade(impl, type, application, key);
				}
			}
		}
		return impl;
	}

	private static final String KEY_VERSION = "#version";

	private static <T extends KeyValueStorage<?>> void buildUpgrade(T impl,
	                                                                final Class<T> type, Context context, String name) {
		final SharedPreferences sp = context.getSharedPreferences(name,
			Activity.MODE_PRIVATE);

		Class<?>[] classes = type.getDeclaredClasses();
		if (classes == null || classes.length <= 0) {
			return;
		}

		Class<?> clazz = null;
		for (Class<?> class1 : classes) {
			if (KeyValueStorage.Upgrade.class.isAssignableFrom(class1)) {
				clazz = class1;
				break;
			}
		}

		if (clazz == null) {
			return;
		}

		boolean isCreate = false;
		int oldVersion = sp.getInt(KEY_VERSION, -1);
		if (oldVersion == -1) {
			isCreate = true;
			oldVersion = 1;
		} else {
			isCreate = false;
		}

		int newVersion = oldVersion;

		KeyValueStorage.StorageKey field = type
			.getAnnotation(KeyValueStorage.StorageKey.class);

		if (field != null) {
			newVersion = field.version();
		}

		try {
			@SuppressWarnings("unchecked")
			KeyValueStorage.Upgrade<T> upgrade = (KeyValueStorage.Upgrade<T>) clazz.newInstance();

			if (isCreate) {
				upgrade.onCreate(impl);
			} else {
				if (newVersion > oldVersion) {
					upgrade.onUpgrade(impl, oldVersion, newVersion);
					// store the new version
					Editor editor = sp.edit();
					editor.putInt(KEY_VERSION, newVersion);
					editor.commit();
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取强类型assetStorage		结构化存储
	 * @param type				储存对象
	 * @param application		上下文
	 * @param <T>				存储管理
     * @return
     */
	@SuppressWarnings("unchecked")
	public <T extends KeyValueStorage.AssetStorage<?>> T getAssetStorage(Class<T> type,
																		 Context application) {
		if (!type.isInterface()) {
			DebugLog.error("AssetStorage  必须从接口创建！" + type);
		}
		T impl = (T) cache.get(type);
		if (impl == null) {
			synchronized (cache) {
				impl = (T) cache.get(type);
				if (impl == null) {
					String key = null;
					try {
						KeyValueStorage.StorageKey field = type
							.getAnnotation(KeyValueStorage.StorageKey.class);
						key = field == null ? null : field.name();
					} catch (Exception e) {
					}
					if (key == null) {
						DebugLog.error(type + "的Asset文件路径必须存在!");
						return null;
					}
					impl = AssetStorageImpl.buildAssetStorage(type,
						application, key);
					cache.put(type, impl);
				}
			}
		}
		return impl;
	}

	/**
	 * 弱类型的KeyValye 存储 ，只用于和Html5 做 数据交互 数据存储时做了简单的混淆处理。
	 * @param key			  存储key
	 * @param application	  上下文
     * @return
     */
	public WebStorage getLocalStorage(String key, Application application) {

		WebStorage impl = (WebStorage) cache
			.get(key);
		if (impl == null) {
			synchronized (cache) {
				impl = (WebStorage) cache.get(key);
				if (impl == null) {
					impl = new DynamicStorageImpl(key);
					cache.put(key, impl);
				}
			}
		}
		return impl;
	}
}

