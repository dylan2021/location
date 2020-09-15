package com.sfmap.library.io.imp;

import android.annotation.SuppressLint;
import android.content.Context;


import com.sfmap.library.io.KeyValueStorage;
import com.sfmap.library.io.KeyValueStorage.OldKey;
import com.sfmap.library.util.DebugLog;

import org.xidea.el.Invocable;
import org.xidea.el.impl.ReflectUtil;
import org.xidea.el.json.JSONDecoder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AssetStorageImpl {

	private static JSONDecoder JSON_DECODER = new JSONDecoder(false);

	private static final Object[] EMPTY_OBJECTS = new Object[0];

	@SuppressWarnings("unchecked")
	public static <T extends KeyValueStorage.AssetStorage<?>> T buildAssetStorage(
		final Class<T> type, Context context, String path) {
		if (context != null) {
			final HashMap<String, String> rawData = new HashMap<String, String>();
			try {
				InputStream is = context.getAssets().open(path);
				BufferedReader ir = new BufferedReader(new InputStreamReader(
						is, "UTF-8"));
				String len = null;
				while ((len = ir.readLine()) != null) {
					if (len.startsWith("#") || len.length() <= 0) {
						continue;
					}
					String[] temp = len.split("=");
					if (temp != null && temp.length >= 2) {
						String key = temp[0].substring(1).trim();
						String value = temp[1].trim();
						if (temp.length > 2) {
							for (int i = 2; i < temp.length; i++) {
								value += "=" + temp[i].trim();
							}
						}
						rawData.put(key, value);
					}
				}
				ir.close();
			} catch (Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append("get asset storage ").append(path).append(" error!");
				DebugLog.error(sb.toString());
				return null;
			}
			final Map<String, String> oldKeyMap = buildOldKeyMapping(type);
			return (T) Proxy.newProxyInstance(type.getClassLoader(),
					new Class[] { type }, new InvocationHandler() {
						HashMap<String, Invocable> impls = new HashMap<String, Invocable>();

						public Object invoke(Object proxy, Method method,
								Object[] args) throws Throwable {
							if (args == null) {
								args = EMPTY_OBJECTS;
							}
							Invocable ivc = impls.get(method.getName());
							if (ivc == null) {
								ivc = buildInvokable(type, method, oldKeyMap,
									rawData);
							}
							return ivc == null ? null : ivc.invoke(proxy, args);
						}
					});
		}
		return null;
	}

	private static Invocable buildInvokable(final Class<?> type, Method method,
	                                        Map<String, String> oldMapping, final Map<String, String> rawData) {

		Type returnType = method.getGenericReturnType();
		String name = method.getName();

		Class<?>[] paramsTypes = method.getParameterTypes();
		String key = name.replaceAll("^(?:get|is)([A-Z])", "$1");
		String oldKey = oldMapping.get(key);

		// 首选的是注释里的Key
		if (rawData.containsKey(oldKey)) {
			key = oldKey;
		}
		
		// asset只支持获取，即get或is
		switch (name.charAt(0)) {
		case 'g':// (name.startsWith("get")) {
			if (paramsTypes.length == 0) {
				return buildGetter(rawData, method, returnType, paramsTypes,
						key);
			}
			break;
		case 'i':// }else if (name.startsWith("is")) {
			if (returnType == Boolean.class || returnType == Boolean.TYPE) {
				name = Character.toLowerCase(name.charAt(2))
						+ name.substring(3);
				return buildGetter(rawData, method, returnType, paramsTypes,
						key);
			}
			break;
		}
		
		if (name.equals("toString") && paramsTypes.length == 0) {
			return new Invocable() {
				String label = type.getName();
				public Object invoke(Object thiz, Object... args)
						throws Exception {
					return label;
				}
			};//
		}
		return new Invocable() {
			@Override
			public Object invoke(Object thiz, Object... args) throws Exception {
				throw new UnsupportedOperationException(
						"您调用了 Asset KeyValue 存储实现不支持的的方法。");
			}
		};
	}

	private static Invocable buildGetter(final Map<String, String> rawData,
			final Method method, final Type type, Class<?>[] paramsTypes,
			final String name) {

		final Type valueType = toWrapperType(type);
		return new Invocable() {

			@SuppressLint("DefaultLocale")
			public Object invoke(Object thiz, Object... args) throws Exception {

				boolean isContains = rawData.containsKey(name);
				String raw = rawData.get(name);
				if (valueType == Boolean.class) {
					return isContains ? Boolean.valueOf(raw.toLowerCase())
							: false;
				}
				if (valueType == String.class) {
					return isContains ? raw : "";
				}
				if (valueType == Integer.class) {
					return isContains ? parseInt(raw) : 0;
				}
				if (valueType == Float.class) {
					return isContains ? parseFloat(raw) : 0;
				}
				if (valueType == Long.class) {
					return isContains ? parseLong(raw) : 0;
				}
				if (valueType == Double.class) {
					return isContains ? parseDouble(raw) : 0;
				}
				if (valueType instanceof Class
						&& Enum.class.isAssignableFrom((Class<?>) valueType)) {
					int iv = isContains ? parseInt(raw) : 0;
					return iv < 0 ? null : ReflectUtil.getEnum(iv,
							(Class<?>) valueType);
				}

				if (raw != null && raw.length() > 0) {
					try {
						return JSON_DECODER.decodeObject(raw, valueType);
					} catch (Exception ex) {
						DebugLog.error(ex);
					}
				}
				return null;
			}
		};

	}

	private static Type toWrapperType(Type type) {
		if (type instanceof Class<?>) {
			type = ReflectUtil.toWrapper((Class<?>) type);
		}
		return type;
	}

	private static Map<String, String> buildOldKeyMapping(Class<?> type) {
		Map<String, String> mapping = new HashMap<String, String>();
		for (Method m : type.getMethods()) {
			OldKey an = m.getAnnotation(OldKey.class);
			if (an != null) {
				String name = m.getName();
				String key = name.replaceAll("^(?:get|is)([A-Z])", "$1");
				String oldKey = an.value();
				String existKey = mapping.put(key, oldKey);
				if (existKey != null) {
					DebugLog.error("conflict assets stroage key: " + existKey
							+ " replaced by " + oldKey);
				}
			}
		}
		return mapping;
	}

	private static int parseInt(String str) {
		int value = 0;
		try {
			value = Integer.parseInt(str);
		} catch (Exception e) {
			DebugLog.warn(e);
		}
		return value;
	}

	private static long parseLong(String str) {
		long value = 0;
		try {
			value = Long.parseLong(str);
		} catch (Exception e) {
			DebugLog.warn(e);
		}
		return value;
	}

	private static float parseFloat(String str) {
		float value = 0;
		try {
			value = Float.parseFloat(str);
		} catch (Exception e) {
			DebugLog.warn(e);
		}
		return value;
	}

	private static double parseDouble(String str) {
		double value = 0;
		try {
			value = Double.parseDouble(str);
		} catch (Exception e) {
			DebugLog.warn(e);
		}
		return value;
	}

}
