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

package com.sfmap.library.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 执行顺序:
 * onPreExecute (UI线程)<br>
 * doInBackground(后台线程)<br>
 * onPostExecute(UI线程)<br>
 * <p>
 * 在doInBackground中调用publishProgress(Progress...)会post到UI线程执行onProgressUpdate(Progress...)
 */
public abstract class PriorityAsyncTask<Params, Progress, Result> implements TaskHandler {

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private static final InternalHandler sHandler = new InternalHandler();

    public static final Executor sDefaultExecutor = new PriorityExecutor();
    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile boolean mExecuteInvoked = false;

    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();

    private Priority priority = Priority.DEFAULT;

    /**
     * 获取任务类型
     *
     * @return
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * 设置异步任务类型
     *
     * @param priority 异步任务类型
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * 构造器,必须在UI线程构建
     */
    public PriorityAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //noinspection unchecked
                return postResult(doInBackground(mParams));
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {

                } catch (ExecutionException e) {
                    throw new RuntimeException(
                            "An error occured while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    private void postResultIfNotInvoked(Result result) {
        boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        @SuppressWarnings("unchecked")
        Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }


    /**
     * 这个方法在后台运行,参数由 {@link #execute}回调传入。
     * 这个方法可以调用 {@link #publishProgress}去更新主线程
     *
     * @param params 任务参数
     * @return 返回结果(类型取决于task定义)
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * 运行在UI线程在 {@link #doInBackground}.之前
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    }


    /**
     * 运行在UI线程在{@link #doInBackground}之后.result就是{@link #doInBackground}的返回值.
     * 任务取消时候这个方法不执行
     *
     * @param result {@link #doInBackground}返回值
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    protected void onPostExecute(Result result) {
    }

    /**
     * 运行在UI线程,调用{@link #publishProgress}触发。
     * 参数由{@link #publishProgress}.传入
     *
     * @param values 这值表明进度
     * @see #publishProgress
     * @see #doInBackground
     */
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * 运行在UI线程执行{@link #cancel(boolean)}之后并且{@link #doInBackground(Object[])}完成
     * 执行{@link #onCancelled()},可以终止任务.如果未自己实，不调<code>super.onCancelled(result)</code>
     *
     * @param result 执行{@link #doInBackground(Object[])},可能为null
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }

    /**
     * 判断是否任务取消,应用可以通过覆盖{@link #onCancelled(Object)}。
     * 这个方法在默认的{@link #onCancelled(Object)}.被调用
     * 在运行{@link #cancel(boolean)}之后或者{@link #doInBackground(Object[])}完成触发
     *
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }

    /**
     * 如果返回true任务正常取消。
     * 如果调用{@link #cancel(boolean)},在{@link #doInBackground(Object[])}结束之后检查
     *
     * @return <tt>true</tt> 任务被取消
     * @see #cancel(boolean)
     */
    @Override
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * true 如果线程执行任务会被打断,否者执行的任务会被允许运行完成。
     *
     * @param    mayInterruptIfRunning
     * @return true    允许在线程运行时中断 false	不允许在线程运行时中断
     * @see #isCancelled()
     * @see #onCancelled(Object)
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * @return
     * @deprecated
     */
    @Override
    public boolean supportPause() {
        return false;
    }

    /**
     * @return
     * @deprecated
     */
    @Override
    public boolean supportResume() {
        return false;
    }

    /**
     * @return
     * @deprecated
     */
    @Override
    public boolean supportCancel() {
        return true;
    }

    /**
     * @return
     * @deprecated
     */
    @Override
    public void pause() {
    }

    /**
     * @return
     * @deprecated
     */
    @Override
    public void resume() {
    }

    /**
     * @return
     * @deprecated
     */
    @Override
    public boolean isPaused() {
        return false;
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return The computed result.
     * @throws CancellationException If the computation was cancelled.
     * @throws ExecutionException    If the computation threw an exception.
     * @throws InterruptedException                       If the current thread was interrupted
     *                                                    while waiting.
     */
    /**
     * 返回执行完成的结果。
     *
     * @return
     * @throws InterruptedException 计算被取消
     * @throws ExecutionException   计算抛出一个异常如果当前线程被打断
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }


    /**
     * 在指定的时间内会等待任务执行，超时则抛异常
     *
     * @param timeout 超时时间
     * @param unit    时间类型
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * 执行任务
     *
     * @param params 任务参数
     * @throws IllegalStateException If execute has invoked.
     * @return 任务实例
     * @see #executeOnExecutor(Executor, Object[])
     * @see #execute(Runnable)
     */
    public final PriorityAsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    /**
     * 执行任务
     *
     * @param exec   是否需要创建线程池
     * @param params 任务参数
     * @throws IllegalStateException If execute has invoked.
     * @return 任务实例
     * @see #execute(Object[])
     */
    public final PriorityAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
                                                                               Params... params) {
        if (mExecuteInvoked) {
            throw new IllegalStateException("Cannot execute task: the task is already executed.");
        }

        mExecuteInvoked = true;

        onPreExecute();

        mWorker.mParams = params;
        exec.execute(new PriorityRunnable(priority, mFuture));
        return this;
    }

    /**
     * 执行runnable任务
     *
     * @param runnable runnable实例
     * @see #execute(Object[])
     * @see #executeOnExecutor(Executor, Object[])
     */
    public static void execute(Runnable runnable) {
        execute(runnable, Priority.DEFAULT);
    }

    /**
     * 执行runnable任务
     *
     * @param runnable runnable实例
     * @param priority 任务类型
     */
    public static void execute(Runnable runnable, Priority priority) {
        sDefaultExecutor.execute(new PriorityRunnable(priority, runnable));
    }

    /**
     * 执行 {@link #doInBackground}时候需要更新UI可以调用{@link #onProgressUpdate}
     * 每次调用触发{@link #onProgressUpdate}方法在UI线程
     * {@link #onProgressUpdate} 如果任务被取消，改方法同样被需要。
     *
     * @param values 更新UI的进度
     * @see #onProgressUpdate
     * @see #doInBackground
     */
    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS,
                    new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
    }

    private static class InternalHandler extends Handler {

        private InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    @SuppressWarnings("rawtypes")
    private static class AsyncTaskResult<Data> {
        final PriorityAsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(PriorityAsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }
}
