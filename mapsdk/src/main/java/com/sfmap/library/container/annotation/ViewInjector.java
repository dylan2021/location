/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sfmap.library.container.annotation;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;


import com.sfmap.library.container.action.Action;
import com.sfmap.library.container.action.ViewInject;
import com.sfmap.library.util.DebugLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注解辅助类
 */
public class ViewInjector {

	private ViewInjector() {

	}

	/**
	 * 初始化
	 * @param view 一个view视图
     */
	public static void inject(View view) {
		injectObject(view, new ViewFinder(view));
	}

	/**
	 * 初始化
	 * @param activity	一个Activity实例
     */
	public static void inject(Activity activity) {
		injectObject(activity, new ViewFinder(activity));
	}

	/**
	 * 初始化
	 * @param dialog	一个Dialog实例
     */
	public static void inject(Dialog dialog) {
		injectObject(dialog, new ViewFinder(dialog));
	}

	/**
	 * 初始化	第一个参数需要包含第二个参数
	 * @param handler	Obj视图类型
	 * @param view		一个View类型
     */
	public static void inject(Object handler, View view) {
		injectObject(handler, new ViewFinder(view));
	}

	/**
	 *  初始化	第一个参数需要包含第二个参数
	 * @param handler	Obj视图类型
	 * @param activity	一个Activity类型
     */
	public static void inject(Object handler, Activity activity) {
		injectObject(handler, new ViewFinder(activity));
	}

	/**
	 * 初始化	第一个参数需要包含第二个参数
	 * @param handler	Obj视图类型
	 * @param dialog	一个Dialog类型
     */
	public static void inject(Object handler, Dialog dialog) {
		injectObject(handler, new ViewFinder(dialog));
	}

	private static void injectObject(Object handler, ViewFinder finder) {
		Class<?> handlerType = handler.getClass();
		// inject view
		Field[] fields = handlerType.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				if (viewInject != null) {
					try {
						View view = finder.findViewById(viewInject.value(), viewInject.parent());
						if (view != null) {
							field.setAccessible(true);
							field.set(handler, view);
						}
					} catch (Throwable e) {
						DebugLog.error(e.getMessage(), e);
					}
				}
			}
		}

		// inject event
		Method[] methods = handlerType.getDeclaredMethods();
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				Action action = method.getAnnotation(Action.class);
				if (action != null) {
					method.setAccessible(true);

					//int[] parentIds = action.parent();
					int[] ids = action.value();
					int[] parentIds = new int[ids.length];
					int[] temp = action.parent();
					for (int i = 0; i < parentIds.length && i < temp.length; i++) {
						parentIds[i] = temp[i];
					}

					for (int i = 0; i < ids.length; i++) {
						ViewInjectInfo info = new ViewInjectInfo();
						info.value = ids[i];
						info.parentId = parentIds[i];
						ListenerManager.addEventMethod(finder, info, action, handler, method);
					}
				}
			}
		}
	}

}
