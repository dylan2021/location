package com.sfmap.plugin.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.sfmap.plugin.task.pool.Priority;
import com.sfmap.plugin.task.pool.PriorityRunnable;

import java.util.concurrent.Executor;

/*package*/ final class TaskProxy<ResultType> extends Task<ResultType> {

	private final Task<ResultType> task;
	private final Executor executor;
	/*package*/ static final InternalHandler sHandler = new InternalHandler();

	private ResultType result;
	private Throwable exception;
	private CancelledException cancelledException;

	protected TaskProxy(Task<ResultType> task, Executor executor) {
		this.task = task;
		this.executor = executor;
	}

	@Override
	protected ResultType doBackground() throws Exception {
		this.state = State.Started;
		this.onStart();
		PriorityRunnable runnable = new PriorityRunnable(
			task.getPriority(),
			new Runnable() {
				@Override
				public void run() {
					try {
						TaskProxy.this.state = State.Running;
						result = task.doBackground();
						if (state == State.Cancelled) { // 没有在doBackground过程中取消成功
							throw new CancelledException("");
						}
						TaskProxy.this.state = State.Finished;
						TaskProxy.this.onFinished(result);
					} catch (CancelledException cex) {
						TaskProxy.this.state = State.Cancelled;
						TaskProxy.this.onCancelled(cex);
					} catch (Throwable ex) {
						TaskProxy.this.state = State.Error;
						TaskProxy.this.onError(ex, false);
					}
				}
			});
		this.executor.execute(runnable);
		return null;
	}

	@Override
	protected void onFinished(ResultType result) {
		sHandler.obtainMessage(MSG_WHAT_ON_FINISH, this).sendToTarget();
	}

	@Override
	protected void onError(Throwable ex, boolean isCallbackError) {
		exception = ex;
		sHandler.obtainMessage(MSG_WHAT_ON_ERROR, this).sendToTarget();
	}

	@Override
	protected void onStart() {
		sHandler.obtainMessage(MSG_WHAT_ON_START, this).sendToTarget();
	}

	@Override
	protected void onUpdate() {
		sHandler.obtainMessage(MSG_WHAT_ON_UPDATE, this).sendToTarget();
	}

	@Override
	protected void onCancelled(CancelledException cex) {
		cancelledException = cex;
		sHandler.obtainMessage(MSG_WHAT_ON_CANCEL, this).sendToTarget();
	}

	@Override
	public void cancel() {
		task.cancel();
	}

	@Override
	public Priority getPriority() {
		return task.getPriority();
	}

	// ########################### inner type #############################
	private final static int MSG_WHAT_ON_START = 1;
	private final static int MSG_WHAT_ON_FINISH = 2;
	private final static int MSG_WHAT_ON_ERROR = 3;
	private final static int MSG_WHAT_ON_UPDATE = 4;
	private final static int MSG_WHAT_ON_CANCEL = 5;

	/*package*/ final static class InternalHandler extends Handler {

		private InternalHandler() {
			super(Looper.getMainLooper());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj == null) {
				throw new IllegalArgumentException("msg must not be null");
			}
			TaskProxy taskProxy = null;
			if (msg.obj instanceof TaskProxy) {
				taskProxy = (TaskProxy) msg.obj;
			}
			if (taskProxy == null) {
				throw new RuntimeException("msg.obj not instanceof TaskProxy");
			}

			try {
				switch (msg.what) {
					case MSG_WHAT_ON_START: {
						taskProxy.task.onStart();
						break;
					}
					case MSG_WHAT_ON_FINISH: {
						taskProxy.task.onFinished(taskProxy.result);
						break;
					}
					case MSG_WHAT_ON_ERROR: {
						taskProxy.task.onError(taskProxy.exception, false);
						break;
					}
					case MSG_WHAT_ON_UPDATE: {
						taskProxy.task.onUpdate();
						break;
					}
					case MSG_WHAT_ON_CANCEL: {
						taskProxy.task.onCancelled(taskProxy.cancelledException);
						break;
					}
					default: {
						break;
					}
				}
			} catch (Throwable ex) {
				taskProxy.state = State.Error;
				if (msg.what != MSG_WHAT_ON_ERROR) {
					taskProxy.task.onError(ex, true);
				} else {
					ex.getStackTrace();
				}
			}
		}
	}
}
