package com.sfmap.library.container.action;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *	点击事件注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	/**
	 * 返回一个id
	 * @return
     */
	int[] value();

	/**
	 * 返回一个name
	 * @return
     */
	String name() default "";

	/**
	 * 返回一个事件类型,默认是OnClickListener事件
	 * @return
     */
	Class<?> type() default View.OnClickListener.class;

	/**
	 * 返回父id
	 * @return
     */
	int[] parent() default 0;

	/**
	 * layout 注解事件
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PageInject {
		/**
		 * 返回layout id
		 * @return
         */
		int value();

		/**
		 * 返回layout name
		 * @return
         */
		String name() default "";
	}

}
