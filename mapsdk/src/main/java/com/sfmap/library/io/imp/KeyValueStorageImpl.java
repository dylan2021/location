package com.sfmap.library.io.imp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;

import com.sfmap.library.io.KeyValueStorage;
import com.sfmap.library.io.KeyValueStorage.DefaultValue;
import com.sfmap.library.io.KeyValueStorage.OldKey;
import com.sfmap.library.util.DebugLog;

import org.xidea.el.Invocable;
import org.xidea.el.impl.ReflectUtil;
import org.xidea.el.json.JSONDecoder;
import org.xidea.el.json.JSONEncoder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@DefaultValue
public class KeyValueStorageImpl {
	private static final JSONDecoder JSON_DECODER = new JSONDecoder(false);
	private static final DefaultValue DEFAULT_VALUE = KeyValueStorageImpl.class
			.getAnnotation(DefaultValue.class);

	private static final Object[] EMPTY_OBJECTS = new Object[0];

	@SuppressWarnings("unchecked")
	public static <T extends KeyValueStorage<?>> T buildKVStroage(final Class<T> type,
			Context context, String name) {
		final SharedPreferences preferences = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
		//key 新变量名称，value 旧变量名称
		final Map<String,String> oldMapping = buildOldKeyMapping(type);
		return (T) Proxy.newProxyInstance(type.getClassLoader(),
				new Class[] { type }, new InvocationHandler() {
					HashMap<String, Invocable> impls = new HashMap<String, Invocable>();
					private Editor[] editorHolder = new Editor[1];

					@Override
					public Object invoke(Object thiz, Method method,
							Object[] args) throws Throwable {
						// log.timeStart();
						if (args == null) {
							args = EMPTY_OBJECTS;
						}
						String name = method.getName();
						Invocable inv = impls.get(name);
						if (inv == null) {
							inv = KeyValueStorageImpl.buildInvocable(type,
									method, oldMapping,preferences, editorHolder);
							// System.err.println("create:"+method+"@"+System.identityHashCode(method)+(null==inv));
							if (inv == null) {
								return null;
							} else {
								impls.put(name, inv);
							}
						}
						Object value = inv.invoke(thiz, args);
						// log.timeEnd("keyValue."+method+value);
						return value;
					}
				});
	}

	private static Map<String,String> buildOldKeyMapping(Class<?> type){
	    Map<String,String> mapping = new HashMap<String, String>();
	    for(Method m : type.getMethods()){
		OldKey an = m.getAnnotation(OldKey.class);
			if(an != null){
				String name = m.getName();
				String key = getKeyFromMethodName(name);
				String oldKey = an.value();
				String existKey = mapping.put(key,oldKey);
				if(existKey != null){
					DebugLog.error("conflict stroage key: "+existKey+" replaced by "+oldKey);
				}
			}
	    }
	    return mapping;
	}

	public static Invocable buildInvocable(final Class<?> type, Method method, Map<String,String>oldMapping,
			final SharedPreferences sharedPreferences,
			final Editor[] editorHolder) {
		Type returnType = method.getGenericReturnType();
		String name = method.getName();

		Class<?>[] paramsTypes = method.getParameterTypes();

		// long t1 = System.nanoTime();
		String key = getKeyFromMethodName(name);
		if (!name.equals(key)) {
			String oldKey = oldMapping.get(key);
			if(oldKey != null){
			    key = oldKey;
			}else{
			    key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
			}
			
			switch (name.charAt(0)) {
			case 'g':// (name.startsWith("get")) {
				if (paramsTypes.length == 0) {
					return buildGetter(sharedPreferences, method, returnType,
							paramsTypes, key);
				}
				break;
			case 'i':// }else if (name.startsWith("is")) {
				if (returnType == Boolean.class || returnType == Boolean.TYPE) {
					name = Character.toLowerCase(name.charAt(2))
							+ name.substring(3);
					return buildGetter(sharedPreferences, method, returnType,
							paramsTypes, key);
				}
				break;
			default:// case 's':// } else if (name.startsWith("set")
				if (paramsTypes.length == 1) {
					return buildSetter(sharedPreferences, editorHolder,
							returnType != Void.TYPE, paramsTypes[0], key);
				}
			}
		} else if (name.equals("toString") && paramsTypes.length == 0) {
			return new Invocable() {
				String label = type.getName() + "&"
						+ sharedPreferences.toString();

				@Override
				public Object invoke(Object thiz, Object... args)
						throws Exception {
					return label;
				}
			};//
		}
		else if(name.equals("reset") && paramsTypes.length == 0){// reset()
			return buildReset(sharedPreferences, oldMapping, 
					editorHolder, returnType != Void.TYPE, type);
			
		}else if (method.getDeclaringClass() == KeyValueStorage.class) {
			Invocable inv = buildStorage3Invocable(sharedPreferences,
					editorHolder, returnType, paramsTypes);
			if (inv != null) {
				return inv;
			}
		}

		return new Invocable() {
			@Override
			public Object invoke(Object thiz, Object... args) throws Exception {
				throw new UnsupportedOperationException(
						"您调用了 KeyValue 存储实现不支持的的方法。");
			}
		};
	}

	private static Invocable buildReset(final SharedPreferences preferences,
			final Map<String, String> oldMapping, final Editor[] editorHolder,
			final boolean returnThis,
			final Class<?> type) {
		
		return new Invocable() {
			
			@Override
			public Object invoke(Object thiz, Object... args) throws Exception {
				Editor editor = editorHolder[0];
				boolean localEditor = editor == null;
				int version = preferences.getInt("#version", 1);
				if(localEditor){
					editor = preferences.edit();
				}
				editor.clear();
				editor.putInt("#version", version);
				if(localEditor){
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
						editor.commit();
					}else {
						editor.apply();
					}
				}
				return returnThis ? thiz : null;
			}
		};
	}
	
	private static Invocable buildSetter(final SharedPreferences preferences,
			final Editor[] editorHolder, final boolean returnThis,
			Class<?> type, final String name) {
		final Type valueType = toWrapperType(type);
		return new Invocable() {
			@Override
			public Object invoke(Object thiz, Object... args) throws Exception {
				Editor editor = editorHolder[0];
				boolean selfTransaction = editor == null;
				if (selfTransaction) {
					editor = preferences.edit();
				}
				Object value = args[0];
				if (valueType == Boolean.class) {
					editor.putBoolean(name, (Boolean) value);
				} else if (valueType == String.class) {
					editor.putString(name, (String) value);
				} else if (valueType == Integer.class) {
					editor.putInt(name, (Integer) value);
				} else if (valueType == Float.class) {
					editor.putFloat(name, (Float) value);
				} else if (valueType == Long.class) {
					editor.putLong(name, (Long) value);
				} else if (valueType == Double.class) {
					editor.putFloat(name, ((Double) value).floatValue());
				} else if (valueType instanceof Class
						&& Enum.class.isAssignableFrom((Class<?>) valueType)) {
					if (value == null) {
						editor.remove(name);
					} else {
						editor.putInt(name, ((Enum<?>) value).ordinal());
					}
				} else {
					editor.putString(name, JSONEncoder.encode(value));
				}
				if (selfTransaction) {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
						editor.commit();
					}else {
						editor.apply();
					}
				}
				if (returnThis) {
					return thiz;
				} else {
					return null;
				}
			}
		};
	}

	private static Invocable buildGetter(final SharedPreferences preferences,
			final Method method, final Type type, Class<?>[] paramsTypes,
			final String name) {
		DefaultValue dv = null;
		if (paramsTypes.length == 0) {
			dv = method.getAnnotation(DefaultValue.class);
			if (dv == null) {
				dv = DEFAULT_VALUE;
			}
		}
		final Type valueType = toWrapperType(type);
		final DefaultValue defaultValue = dv;
		return new Invocable() {
			private Object sharedDefaultValue;
			private boolean hasSharedValue;

			@Override
			public Object invoke(Object thiz, Object... args) throws Exception {
				if (valueType == SharedPreferences.class) {
					return preferences;
				}
				 if (!preferences.contains(name)) {
					return defaultValue(args);
				}
				if (valueType == Boolean.class) {
					return preferences.getBoolean(name, false);
				} else if (valueType == String.class) {
					return preferences.getString(name, null);
				} else if (valueType == Integer.class) {
					return preferences.getInt(name, 0);
				} else if (valueType == Float.class) {
					return preferences.getFloat(name, 0);
				} else if (valueType == Long.class) {
					return preferences.getLong(name, 0);
				} else if (valueType == Double.class) {
					return Double.valueOf(preferences.getFloat(name, 0));
				} else if (valueType instanceof Class
						&& Enum.class.isAssignableFrom((Class<?>) valueType)) {
					int iv = preferences.getInt(name, -1);
					return iv < 0 ? null : ReflectUtil.getEnum(iv,
							(Class<?>) valueType);
				} else {
					String raw = preferences.getString(name, "");
					if (raw != null && raw.length() > 0) {
						try {
							return JSON_DECODER.decodeObject(raw, valueType);
						} catch (Exception ex) {
							DebugLog.error(ex);
						}
					}
					return null;
				}
			}

			private Object defaultValue(Object[] args) {
				if (!hasSharedValue) {
					if (defaultValue == null) {
						return args[0];
					}
					double value = defaultValue.value();
					if (valueType instanceof Class
							&& Enum.class
									.isAssignableFrom((Class<?>) valueType)) {
						sharedDefaultValue = defaultValue == null ? null
								: ReflectUtil.getEnum((int) value,
										(Class<?>) valueType);
					} else {
						String jsonValue = defaultValue.jsonValue();
						if(jsonValue.length() == 0 ){
						    if(valueType == String.class){
							jsonValue = "\"\"";//防止空指针
						    }else{
							jsonValue = "null";
						    }
						}
						if ( "null".equals(jsonValue)) {
							if (valueType == Boolean.class) {
								sharedDefaultValue = value != 0;
							} else if (valueType == Integer.class) {
							    	sharedDefaultValue = (int) value;
							} else if (valueType == Long.class) {
								sharedDefaultValue = (long) value;
							} else if (valueType == Float.class) {
								sharedDefaultValue = (float) value;
							} else if (valueType == Double.class) {
								sharedDefaultValue = (double) value;
							}
						} else {
							Object obj = JSON_DECODER.decodeObject(jsonValue,
									valueType);
							if (valueType == String.class || obj == null
									|| obj instanceof Number
									|| obj instanceof Boolean) {
								sharedDefaultValue = obj;
							} else {
								return obj;
							}
							
						}
					}
					this.hasSharedValue = true;
				}
				return sharedDefaultValue;
			}
		};
	}

	private static Type toWrapperType(Type rt) {
		if (rt instanceof Class<?>) {
			rt = ReflectUtil.toWrapper((Class<?>) rt);
		}
		return rt;
	}

	private static Invocable buildStorage3Invocable(final SharedPreferences sp,
			final Editor[] editorHolder, final Type rt, final Class<?>[] ps) {
		return new Invocable() {
			@Override
			public Object invoke(Object thiz, Object... args) throws Exception {
				if (rt == SharedPreferences.class) {// getSharedPrefrences
					return sp;
				} else if (rt == Void.TYPE) {// ("commit".equals(name))
					if (editorHolder[0] != null) {
						editorHolder[0].commit();
						editorHolder[0] = null;
					}
					return null;
				} else {// if( T "beginTransaction".equals(name))
					if (editorHolder[0] == null) {
						editorHolder[0] = sp.edit();
					}
					return null;
				}
			}
		};
	}

	/**
	 * 根据方法名称,提取属性名称
	 * 
	 * @param methodName
	 * @return
	 */
	public static String getKeyFromMethodName(String methodName) {
	    String key = methodName;
	    
	    if (!TextUtils.isEmpty(methodName)) {
		key = methodName.replaceAll("^(?:get|set|is)([A-Z])", "$1");
		
		//属性第一个字母必须小写
		if (!TextUtils.isEmpty(key)) {
		   key = toLowerCaseFirstOne(key);
		}
	    }

	    return key;
	}

	// 首字母转小写
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
		    return s;
		} else {
		    return (new StringBuilder())
			    .append(Character.toLowerCase(s.charAt(0)))
			    .append(s.substring(1)).toString();
		}
	    }

	// 首字母转大写
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0))) {
		    return s;
		} else {
		    return (new StringBuilder())
			    .append(Character.toUpperCase(s.charAt(0)))
			    .append(s.substring(1)).toString();
		}
	    }
}
