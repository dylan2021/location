package com.sfmap.library.container.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图注解事件
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {
	/**
	 * 返回view id
	 * @return
     */
	int value();

	/**
	 * 返回父view id
	 * @return
     */
	int parent() default 0;
}
