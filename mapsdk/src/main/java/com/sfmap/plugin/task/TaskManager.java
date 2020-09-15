package com.sfmap.plugin.task;

import android.util.Log;
import com.sfmap.plugin.task.pool.PriorityExecutor;

import java.util.concurrent.Executor;

/**
 *
 * @date: 2014/11/12
 */
public class TaskManager {

	public static final PriorityExecutor sDefaultExecutor = new PriorityExecutor();

	private TaskManager() {
	}

	/**
	 * run task
	 *
	 * @param task
	 * @param <T>
	 * @return
	 */
	public static <T> Task<T> start(Task<T> task) {
		return start(task, sDefaultExecutor);
	}

	/**
	 * run task use custom executor
	 *
	 * @param task
	 * @param executor
	 * @param <T>
	 * @return
	 */
	public static <T> Task<T> start(Task<T> task, Executor executor) {
		TaskProxy<T> proxy = null;
		if (task instanceof TaskProxy) {
			proxy = (TaskProxy<T>) task;
		} else {
			proxy = new TaskProxy<T>(task, executor);
		}
		try {
			proxy.doBackground();
		} catch (Throwable ex) {
			Log.e("never happened", ex.getMessage(), ex);
		}
		return proxy;
	}

	/**
	 * run in UI thread
	 *
	 * @param runnable
	 */
	public static void post(Runnable runnable) {
		TaskProxy.sHandler.post(runnable);
	}

	/**
	 * run in background thread
	 *
	 * @param runnable
	 */
	public static void run(Runnable runnable) {
		sDefaultExecutor.execute(runnable);
	}
}
