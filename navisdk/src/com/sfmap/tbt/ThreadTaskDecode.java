package com.sfmap.tbt;

/**
 * 原类名:be
 */
public abstract class ThreadTaskDecode implements Runnable {
	ThreadTaskListener taskListener;

	public final void run() {
		try {
			if (this.taskListener != null) {
				this.taskListener.taskBegin(this);
			}

			if (Thread.interrupted())
				return;

			a();
			if (Thread.interrupted())
				return;

			if (this.taskListener != null)
				this.taskListener.taskEnd(this);
		} catch (Throwable localThrowable) {
			SDKLogHandler.a(localThrowable, "ThreadTask", "run");
			localThrowable.printStackTrace();
		}
	}

	public abstract void a();

	static abstract interface ThreadTaskListener {
		public abstract void taskBegin(ThreadTaskDecode parambe);

		public abstract void taskEnd(ThreadTaskDecode parambe);
	}
}