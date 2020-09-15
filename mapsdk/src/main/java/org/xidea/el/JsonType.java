package org.xidea.el;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @date: 2015/01/15
 */
public class JsonType implements ParameterizedType {

	private Type[] argsTypes;
	private Type rawType;

	public JsonType(Type rawType, Type... argsTypes) {
		this.argsTypes = argsTypes;
		this.rawType = rawType;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return argsTypes;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}
}
