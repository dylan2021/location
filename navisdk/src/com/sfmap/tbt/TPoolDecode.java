package com.sfmap.tbt;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public final class TPoolDecode {
	private static TPoolDecode instance = null;
	private ExecutorService b;
	private ConcurrentHashMap<ThreadTaskDecode, Future<?>> c = new ConcurrentHashMap();
	private ThreadTaskDecode.ThreadTaskListener taskListener = new ThreadTaskDecode.ThreadTaskListener() {

		@Override
		public void taskBegin(ThreadTaskDecode parambe) {

		}

		@Override
		public void taskEnd(ThreadTaskDecode parambe) {
			removeQueue(parambe, false);
		}

	};

	public static synchronized TPoolDecode getInstance(int paramInt) {
		if (instance == null)
			instance = new TPoolDecode(paramInt);

		return instance;
	}

	private TPoolDecode(int paramInt) {
		try {
			this.b = Executors.newFixedThreadPool(paramInt);
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "TPool", "ThreadPool");
			localThrowable.printStackTrace();
		}
	}

	public void addTask(ThreadTaskDecode parambe) throws OperExceptionDecode {
		try {
			if (contain(parambe))
				return;

			if ((this.b == null) || (this.b.isShutdown()))
				return;

			parambe.taskListener = this.taskListener;
			Future localFuture = null;
			try {
				localFuture = this.b.submit(parambe);
			} catch (RejectedExecutionException localRejectedExecutionException) {
				return;
			}
			if (localFuture == null)
				return;

			addQueue(parambe, localFuture);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			SDKLogHandler.a(localThrowable, "TPool", "addTask");
			throw new OperExceptionDecode("thread pool has exception");
		}
	}

	public static synchronized void onDestroy() {
		try {
			if (instance != null) {
				instance.destroy();
				instance = null;
			}
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "TPool", "onDestroy");
			localThrowable.printStackTrace();
		}
	}

	private void destroy() {
		try {
			Iterator localIterator = this.c.entrySet().iterator();
			while (localIterator.hasNext()) {
				Map.Entry localEntry = (Map.Entry) localIterator.next();

				ThreadTaskDecode localbe = (ThreadTaskDecode) localEntry.getKey();
				Future localFuture = (Future) this.c.get(localbe);
				try {
					if (localFuture != null)
						localFuture.cancel(true);
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
			this.c.clear();
			this.b.shutdown();
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "TPool", "destroy");
			localThrowable.printStackTrace();
		}
	}

	private synchronized boolean contain(ThreadTaskDecode parambe) {
		boolean bool = false;
		try {
			bool = this.c.containsKey(parambe);
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "TPool", "contain");
			localThrowable.printStackTrace();
		}
		return bool;
	}

	private synchronized void addQueue(ThreadTaskDecode parambe, Future<?> paramFuture) {
		try {
			this.c.put(parambe, paramFuture);
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "TPool", "addQueue");
			localThrowable.printStackTrace();
		}
	}

	private synchronized void removeQueue(ThreadTaskDecode parambe, boolean paramBoolean) {
		try {
			Future localFuture = (Future) this.c.remove(parambe);
			if ((paramBoolean) && (localFuture != null))
				localFuture.cancel(true);
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "TPool", "removeQueue");
			localThrowable.printStackTrace();
		}
	}
}