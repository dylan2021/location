package com.sfmap.library.http.url;

import android.util.Log;

import com.sfmap.library.http.params.ParamEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
public class URLBuilderFactory {

	private URLBuilderFactory() {
	}

	private static class EntityInfo{
		URLBuilder.Path path;
		Map<String, Field> fields;
		
	}
	@SuppressWarnings("rawtypes")
	private final static Map<Class, EntityInfo> entityInfoCache = new HashMap<Class, EntityInfo>();

	public static URLBuilder build(ParamEntity entity, boolean isPost) {

		if (entity == null) {
			throw new IllegalArgumentException("entity must not be null.");
		}

		Class<?> entityCls = entity.getClass();
		EntityInfo info = entityInfoCache.get(entityCls);
		if (info == null) {
			info = new EntityInfo();
			URLBuilder.Path path = entityCls
					.getAnnotation(URLBuilder.Path.class);

			// 检查entity规范
			// 1. 必须要有Path 注解
			if (path == null) {
				throw new IllegalArgumentException(
						"参数entity必需有URLBuilder.Path注解");
			}

			info.path = path;
			info.fields = new HashMap<String, Field>();
			resolveAllFields(entityCls, info.fields);
			entityInfoCache.put(entityCls, info);
		}

		URLBuilder builder = null;
		Class<? extends URLBuilder> type = info.path.builder();
		type = type == null ? info.path.builder() : type;
		try {
			builder = type.newInstance();
			builder.parse(info.path, info.fields, entity, isPost);
		} catch (InstantiationException e) {
			throw new RuntimeException(type.getName() + "必须有空构造方法");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(type.getName() + "必须有public空构造方法");
		}

		return builder;
	}

	@SuppressWarnings("rawtypes")
	private static void resolveAllFields(Class cls,
			Map<String, Field> result) {
		if (cls != null && !cls.equals(Object.class)) {
			Field[] fields = cls.getDeclaredFields();
			if (fields != null) {
				for (Field field : fields) {
					String name = field.getName();
					if (!name.startsWith("this$")) {
						field.setAccessible(true);
						result.put(name, field);
					}
				}
			}
			resolveAllFields(cls.getSuperclass(), result);
		}
	}
}
