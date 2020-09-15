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

import android.view.View;


import com.sfmap.library.container.action.Action;
import com.sfmap.library.util.DebugLog;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ListenerManager {

	private static final int MAX_CLICK_INTERVAL = 1000 / 3;
	private static long lastActionTimestamp = 0;

	private ListenerManager() {
	}

	public static void addEventMethod(
		ViewFinder finder,
		ViewInjectInfo info,
		Action action,
		Object handler,
		Method method) {

		try {
			View view = finder.findViewByInfo(info);
			if (view != null) {
				Class<?> listenerType = action.type();
				String listenerSetter = "set" + listenerType.getSimpleName();

				DynamicHandler dynamicHandler = new DynamicHandler(handler);
				dynamicHandler.setMethod(method, action);
				Object listener;
				if (listenerType == View.OnClickListener.class) {
					listener = dynamicHandler;
				} else {
					listener = Proxy.newProxyInstance(
						listenerType.getClassLoader(),
						new Class<?>[]{listenerType}, dynamicHandler);
				}
				Method setEventListenerMethod = view.getClass().getMethod(
					listenerSetter, listenerType);
				setEventListenerMethod.invoke(view, listener);
			}
		} catch (Throwable e) {
			DebugLog.debug(e.getMessage(), e);
		}
	}

	public static class DynamicHandler implements InvocationHandler,
		View.OnClickListener {
		private WeakReference<Object> handlerRef;
		private Method methodImpl;
		private int pageId;
		private int buttonId;
		private boolean isView = false;

		public DynamicHandler(Object handler) {
			this.handlerRef = new WeakReference<Object>(handler);
			Class<?> pageType = handler.getClass();
			pageId = getPageId(pageType);
		}

		@Override
		public void onClick(View v) {
			invoke(v);
		}

		public void setMethod(Method method,Action action) {
			this.methodImpl = method;
			buttonId = getButtonId(action);
			int[] values = action.value();
			isView = values != null && values.length > 0 && values[0] > 0;
		}

		@Override
		public Object invoke(Object proxy, Method method0, Object[] args)
			throws Throwable {
			return invoke(args);
		}

		private Object invoke(Object... args) {
			long currTime = System.currentTimeMillis();
			if (isView && currTime - lastActionTimestamp < MAX_CLICK_INTERVAL) {
				lastActionTimestamp = currTime;
				DebugLog.warn("Action time span less than (1/3)s");
				return null;
			} else {
				lastActionTimestamp = currTime;
			}

			Object handler = handlerRef.get();
			if (handler != null && methodImpl != null) {
				Object result;
				try {
					result = methodImpl.invoke(handler, args);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				return result;
			}
			return null;
		}

	}

	public static int getButtonId(Action action) {
		String name = getButtonName(action);
		if (name.length() > 0) {
			int result = getHashCode(name);
			return result < 0 ? -result : result;
		} else {
			return 0;
		}
	}

	public static String getButtonName(Action action) {
		return action.name();
	}

	/**
	 * 利用RSHash算法, 但从前后同时向中间计算字符串的hash值.
	 *
	 * @param str
	 * @return
	 */
	private static int getHashCode(String str) {
		final int b = 378551;
		int a = 63689;
		long hash = 0;
		int end = str.length() - 1;
		int start = 0;
		while (start < end) {
			hash = hash * a + str.charAt((end - start) % 2 == 0 ? start++ : end--);
			a *= b;
		}
		/*
		boolean odd = end % 2 == 1;
	
		for (int i = 0; i < end / 2; i++) {
			hash = hash * a + str.charAt(i);
			a *= b;
			hash = hash * a + str.charAt(end - 1 - i);
			a *= b;
		}
	
		if (odd) {
			hash = hash * a + str.charAt(end / 2);
		}
		 */
		return (int) hash & 0x7FFFFFFF | 0x8000;
	}

	public static int getPageId(Class<?> pageCls) {
		String name = getPageName(pageCls);
		if (name.length() > 0) {
			int result = getHashCode(name);
			return result < 0 ? -result : result;
		} else {
			return 0;
		}
	}

	public static String getPageName(Class<?> pageCls) {
		Action.PageInject page = pageCls.getAnnotation(Action.PageInject.class);
		if (page != null) {
			return page.name();
		} else {
			return "";
		}
	}
}
