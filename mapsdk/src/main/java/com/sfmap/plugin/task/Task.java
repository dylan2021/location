package com.sfmap.plugin.task;

import com.sfmap.plugin.task.pool.Priority;

/**
 *
 * @date: 2014/11/11
 */
public abstract class Task<ResultType> {

	protected abstract ResultType doBackground() throws Exception;

	protected abstract void onFinished(ResultType result);

	protected abstract void onError(Throwable ex, boolean isCallbackError);

	protected void onStart() {
	}

	protected void onUpdate() {
	}

	protected void onCancelled(CancelledException cex) {
	}

	public void cancel() {
		this.state = State.Cancelled;
	}

	/*package*/ State state;

	public final State getState() {
		return state;
	}

	public Priority getPriority() {
		return Priority.DEFAULT;
	}

	public static class CancelledException extends RuntimeException {
		public CancelledException(String detailMessage) {
			super(detailMessage);
		}
	}

	public static enum State {
		Started, Running, Finished, Cancelled, Error
	}
}
