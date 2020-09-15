package com.sfmap.library.http;

import android.app.Dialog;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;


import com.sfmap.library.Callback;
import com.sfmap.library.Page;
import com.sfmap.library.http.cache.HttpCache;
import com.sfmap.library.http.cache.HttpCacheEntry;
import com.sfmap.library.http.cache.HttpCacheImpl;
import com.sfmap.library.http.loader.FileLoader;
import com.sfmap.library.http.loader.Loader;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URIRequest;
import com.sfmap.library.task.PriorityAsyncTask;
import com.sfmap.library.util.DebugLog;
import com.sfmap.library.util.DesUtil;
import com.sfmap.plugin.app.IMPageHelper;

import org.xidea.el.impl.ReflectUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * 网络任务主要流程控制
 * <p/>
 * 执行顺序:
 * onPreExecute    (UI线程)  检查mem cache
 * doInBackground (后台线程) 检查磁盘缓存, 发起请求, 接收数据 (错误和过程回调)
 * onPostExecute   (UI线程)  最终结果callback
 * <p/>
 * 在doInBackground中调用publishProgress(Progress...)会post到UI线程执行onProgressUpdate(Progress...)
 * <p/>
 */
public class HttpAsyncTask<ResultType> extends
		PriorityAsyncTask<Void, Object, ResultType> implements
	ProgressCallbackHandler {
	private String TAG = HttpAsyncTask.class.getSimpleName();
	private int rate = 1000;// 默认每秒更新一次进度
	private State state = State.WAITING;
	private Callback<ResultType> callback;
	private Callback.PrepareCallback prepareCallback;
	private Callback.ProgressCallback progressCallback;
	private Callback.CancelledCallback cancelledCallback;
	private Callback.CacheCallback<ResultType> cacheCallback;
	private Callback.CustomDlgCallback customDlgCallback;
	private Callback.Cancelable cancelable;

	private URIRequest request;
	private Object rawCache = null;
	private ResultType cacheResult;
	private Type loadType;
	private Type resultType;
	private final HttpRetryHandler retryHandler = new HttpRetryHandler();

	private static final HttpCache sCache = HttpCacheImpl.getInstance();
	private boolean supportCache;
	private HttpCacheEntry cacheEntry;
	private Callback.CachePolicyCallback.CachePolicy cachePolicy;
	private boolean trustCache;


	private Dialog msgDialog = null;

	private final static Callback fakeCallback = new Callback() {
		@Override
		public void callback(Object result) {

		}

		@Override
		public void error(Throwable ex, boolean isCallbackError) {

		}
	};

	public HttpAsyncTask(final String url, HttpMethod method, RequestParams params,
	                     Callback<ResultType> callback) {

		if (callback == null) {
			Log.w("HttpLog", "callback is null");
			callback = fakeCallback;
		}

		//绑定当前页回调-->是否需要绑定页面
		if (callback instanceof Callback.BindPage) {
			Page page = ((Callback.BindPage) callback).getPage();
			if (page != null) {
				page.setPageStateListener(new Page.PageStateListener() {
					@Override
					public void onPageHidden() {
						try {
							if (cancelable != null) {
								cancelable.cancel();
							}
							HttpAsyncTask.this.cancel();
						} catch (Throwable ignored) {
						}
					}
				});
			}
		}

		Class<?> clazz = callback.getClass();
		// 初始化callback和loadType
		{
			this.callback = callback;
			if (callback instanceof Callback.PrepareCallback) {
				this.prepareCallback = (Callback.PrepareCallback) callback;
				// RawType
				loadType = ReflectUtil.getParameterizedType(clazz, Callback.PrepareCallback.class, 0);
				resultType = ReflectUtil.getParameterizedType(clazz, Callback.PrepareCallback.class, 1);
			} else {
				if (callback instanceof Callback.CacheCallback) {
					// ResultType
					loadType = callback == fakeCallback ? null :
						ReflectUtil.getParameterizedType(clazz, Callback.CacheCallback.class, 0);
				} else {
					// ResultType
					loadType = callback == fakeCallback ? null :
						ReflectUtil.getParameterizedType(clazz, Callback.class, 0);
				}
				resultType = loadType;
			}

			if (loadType instanceof ParameterizedType) {
				loadType = ((ParameterizedType) loadType).getRawType();
			} else if (loadType instanceof TypeVariable) {
				throw new IllegalArgumentException(
					"not support callback type"
						+ callback.getClass().getCanonicalName());
			}

			if (resultType instanceof ParameterizedType) {
				resultType = ((ParameterizedType) resultType).getRawType();
			}

			if (callback instanceof Callback.ProgressCallback) {
				this.progressCallback = (Callback.ProgressCallback) callback;
			}
			if (callback instanceof Callback.CancelledCallback) {
				this.cancelledCallback = (Callback.CancelledCallback) callback;
			}
			if (callback instanceof Callback.Cancelable) {
				this.cancelable = (Callback.Cancelable) callback;
			}
			if (callback instanceof Callback.CacheCallback) {
				this.cacheCallback = (Callback.CacheCallback<ResultType>) callback;
			}
			if (callback instanceof Callback.CustomDlgCallback) {
				this.customDlgCallback = (Callback.CustomDlgCallback) callback;
			}
		}

		// 初始化request
		{
			try {
				Log.e("请求地址",url);
				this.request = new URIRequest(url, method, params, (Class<?>) loadType, this, callback.getClass().getClassLoader());
			} catch (Throwable ex) {
				this.state = State.FAILURE;
				try {
					this.callback.error(ex, false);
				} catch (Throwable error) {
//					if (DebugLog.isDebug()) {
//						Log.e("HttpLog", "init request error", error);
//					}
				}
				this.cancel();
				return;
			}
		}

		// 初始化缓存支持等
		 {
			if (params != null) {
				this.cachePolicy = params.getCachePolicy();
				this.retryHandler.setMaxRetryCount(params.getMaxRetryCount());
			} else {
				this.cachePolicy = Callback.CachePolicyCallback.CachePolicy.Any;
			}
			// supportCache?
			if (!(callback instanceof Callback.CacheCallback)) {
				this.supportCache = false;
			} else {
				this.supportCache = sCache.supportCache(this.request);
			}
		}
	}

	/**
	 * 检查内存缓存
	 */
	private void checkMemoryCache() {
		if (supportCache) {  //false 进度条显示问题
			Object result = sCache.getMemCache(request);
			if (result != null) {
				cacheEntry = sCache.getHttpCacheEntry(request);
				if (cacheEntry != null) {
					try {
						cacheEntry.isMemCache = true;
						Log.i("caohai","有缓存数据");
						this.trustCache = cacheCallback.cache((ResultType) result, cacheEntry);
					} catch (Throwable ex) {
						this.trustCache = false;
						this.publishProgress(UPDATE_FAILURE, ex);
					} finally {
						cacheEntry.isMemCache = false;
					}
				}
				if (this.trustCache || cachePolicy == Callback.CachePolicyCallback.CachePolicy.CacheOnly) {
					this.state = State.SUCCESS;
				}
			}
		}
	}

	/**
	 * 将内存或磁盘缓存转换为最终结果
	 *
	 * @param rawCache
	 * @return
	 */
	private ResultType rawCache2Result(Object rawCache) {
		if (rawCache == null || cacheResult != null) return cacheResult;

		if (rawCache instanceof String) {
			rawCache = new ByteArrayInputStream(((String) rawCache).getBytes());
		}

		if (rawCache instanceof InputStream) {
			try {
				Object temp = request.getLoader().load((InputStream) rawCache);
				if (prepareCallback != null) {
					temp = prepareCallback.prepare(temp);
				}
				cacheResult = (ResultType) temp;
			} catch (Throwable e) {
				DebugLog.debug(e.getMessage());
			}
		} else {
			try {
				if (prepareCallback != null) {
					rawCache = prepareCallback.prepare(rawCache);
				}
				cacheResult = (ResultType) rawCache;
			} catch (Throwable e) {
				DebugLog.debug(e.getMessage());
			}
		}
		return cacheResult;
	}

	/**
	 * 外部调用HttpAsyncTask的execute等方法执行任务是会立即调用.
	 * 但是doInBackground不一定会立即执行.
	 */
	@Override
	protected void onPreExecute() {
		// 同步检查内存缓存
//		checkMemoryCache();
//
		//获取LoadingMessage
		//将控制dialog放在fragment里面
//		if (!this.trustCache) {
//			try {
//				if (NetworkImpl.getInstance().isInternetConnected()) {
//					if(customDlgCallback != null){
////						msgDialog = customDlgCallback.onLoadDlg(this);
////						msgDialog.show();
//					}
//				}
//
//			} catch (Throwable ex) {
//				Log.d("HttpAsyncTask",
//					"find callback method error:" +
//						callback.getClass().getCanonicalName());
//			}
//		}
	}

	/**
	 * http任务的磁盘缓存支持和异步逻辑控制
	 *
	 * @param voids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ResultType doInBackground(Void... voids) {
		if (this.state == State.SUCCESS || isCallbackCancelled() || this.state == State.CANCELLED) return null;

		ResultType result = null;
		this.publishProgress(UPDATE_STARTED);

		if (supportCache && cacheEntry == null) {
			cacheEntry = sCache.getHttpCacheEntry(request);
		}

		// 如果支持缓存且内存缓存不存在, 尝试加载磁盘缓存.
		if (supportCache && cacheEntry != null && cacheResult == null) {
			try {
				@SuppressWarnings("rawtypes")
				Loader loader = request.getLoader();
				if (loader instanceof FileLoader) {
					rawCache = sCache.getDiskCacheFile(cacheEntry);
				} else {
					rawCache = sCache.getDiskCache(cacheEntry);
				}
				if (rawCache != null && cacheCallback != null) {
					this.publishProgress(UPDATE_CACHE, rawCache2Result(rawCache));
				}
			} catch (Throwable e) {
				DebugLog.debug(e.getMessage());
				rawCache = null;
			}
		}

		if (this.state == State.FAILURE) {
			return null;
		}

		// 缓存策略为只加载缓存的话直接跳过网络加载
		if (trustCache) {
			return rawCache2Result(rawCache);
		}

		if (cachePolicy == Callback.CachePolicyCallback.CachePolicy.CacheOnly) {
			return rawCache2Result(rawCache);
		}

		if (isCallbackCancelled() || this.state == State.CANCELLED) {
			return rawCache2Result(rawCache);
		}

		// 从网络获取数据
		boolean retry = true;
		int retriedCount = 0;
		Throwable exception = null;
		while (retry) {
			if (isCallbackCancelled() || this.state == State.CANCELLED) {
				return rawCache2Result(rawCache);
			}

			// 发送请求
			try {
				sending = true;

				// cache支持
				if (supportCache && cacheEntry != null && rawCache != null) {
					sCache.addHeaders(cacheEntry, request);
				}
				// 断点续下支持
				RequestParams params = request.getParams();
				if (params != null && params.isAutoResume()) {
					String saveFilePath = params.getSaveFilePath();
					if (!TextUtils.isEmpty(saveFilePath)) {
						File file = new File(saveFilePath);
						if (file.exists()) {
							params.setHeader("RANGE", "bytes=" + file.length()
								+ "-");
						}
					}
				}

				if (NetworkImpl.globalRequestCallback != null) {
					NetworkImpl.globalRequestCallback.callback(null);
				}
				request.sendRequest();
			} catch (Throwable e) {
				request.error(e);
				request.close();
				exception = e;
				retry = retryHandler.retryRequest(e, ++retriedCount, request);
				continue;
			}

			// 如果缓存有效, 跳过下载过程.
			if (supportCache && cacheEntry != null && rawCache != null) {
				boolean useCache = sCache.useCache(cacheEntry, request);
				if (useCache) {
					return rawCache2Result(rawCache);
				} else {
					// 需要服务端支持
					/*try {
						sCache.removeCache(cacheEntry);
					} catch (IOException e) {
						DebugLog.debug(e.getMessage());
					}*/
				}
			}

			if (isCallbackCancelled() || this.state == State.CANCELLED) {
				return rawCache2Result(rawCache);
			}

			// 接收数据
			try {
				sending = false;
				if (callback instanceof Callback.ResponseListener) {
					((Callback.ResponseListener) callback).onResponse(request.getConnection());
				}

				Object rawResult = request.loadResult();

				String resultString = new String((byte [])rawResult);
				String errMsgDecrypted = "";
				try {
					errMsgDecrypted = new DesUtil().decrypt(resultString.trim(), "UTF-8");
					Log.d(TAG,errMsgDecrypted);
				} catch (IOException e) {
					e.printStackTrace();
				}catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
				Log.d(TAG,resultString);
				rawResult = errMsgDecrypted.getBytes();
				if (prepareCallback != null) {
					result = (ResultType) prepareCallback.prepare(rawResult);
				} else {
					result = (ResultType) rawResult;
				}
				if (supportCache && result != null) {
					sCache.saveCache(request, request.getLoader().getRawCache(), result);
				}
				exception = null;
				break;
			} catch (Throwable e) {
				request.error(e);
				request.close();
				exception = e;
				retry = retryHandler.retryRequest(e, ++retriedCount, request);
			}
		}

		if (result == null && exception != null) {
			this.publishProgress(UPDATE_FAILURE, exception);
		}

		request.close();
		return result;
	}

	/**
	 * doInBackground过程中的通知(在UI线程执行)
	 *
	 * @param values The values indicating progress.
	 */
	@Override
	protected void onProgressUpdate(Object... values) {
		if (isCallbackCancelled()) {
			cancel();
			return;
		}

		if (this.state == State.CANCELLED || values == null || values.length == 0) {
			return;
		}
		try {
			switch ((Integer) values[0]) {
				case UPDATE_STARTED:
					this.state = State.STARTED;
					if (progressCallback != null) {
						progressCallback.onStart();
					}
					break;
				case UPDATE_SENDING:
					this.state = State.SENDING;
					if (progressCallback != null) {
						progressCallback.onLoading(
							Long.valueOf(String.valueOf(values[1])),
							Long.valueOf(String.valueOf(values[2])));
					}
					break;
				case UPDATE_LOADING:
					this.state = State.LOADING;
					if (progressCallback != null) {
						progressCallback.onLoading(
							Long.valueOf(String.valueOf(values[1])),
							Long.valueOf(String.valueOf(values[2])));
					}
					break;
				case UPDATE_FAILURE:
					this.state = State.FAILURE;
					try {
						callback.error((Throwable) values[1], false);
					} catch (Throwable error) {
						DebugLog.error(error.getMessage(), error);
					}
					break;
				case UPDATE_CACHE:
					if (cacheCallback != null) {
						try {
							this.trustCache = cacheCallback.cache((ResultType) values[1], cacheEntry);
						} catch (Throwable ex) {
							this.trustCache = false;
							//DebugLog.error(ex.getMessage(), ex);
							onProgressUpdate(UPDATE_FAILURE, ex);
						}
					}
					break;
				default:
					break;
			}
		} catch (Throwable ex) {
			try {
				callback.error(ex, true);
			} catch (Throwable error) {
				DebugLog.error(error.getMessage(), error);
			}
		}
	}

	/**
	 * 任务取消时调用
	 */
	@Override
	protected void onCancelled() {
		if (cancelledCallback != null && !isCallbackCancelled()) {
			cancelledCallback.onCancelled();
		}
		if (msgDialog != null) {
			msgDialog.dismiss();
			msgDialog = null;
		}
	}


	/**
	 * 任务结束时调用
	 *
	 * @param result The result of the operation computed by {@link #doInBackground}.
	 */
	@Override
	protected void onPostExecute(ResultType result) {
		if (msgDialog != null) {
			msgDialog.dismiss();
			msgDialog = null;
		}

		synchronized (replaceCallbackLock) {
			callbackInvoked = true;
		}

		if (isCallbackCancelled() || this.state == State.FAILURE) {
			cancel();
			return;
		}

		if (this.state == State.SUCCESS || this.state == State.CANCELLED) {
			return;
		}

		try {
			if (cacheCallback != null) {
				if (this.trustCache && result != null && cacheResult != null && result.equals(cacheResult)) {
					result = null;
				}
				cacheCallback.callback(result);
			} else {
				callback.callback(result == null ? cacheResult : result);
			}
			this.state = State.SUCCESS;
		} catch (Throwable ex) {
			this.state = State.FAILURE;
			try {
				callback.error(ex, true);
			} catch (Throwable error) {
				DebugLog.error(error.getMessage(), error);
			}
		} finally {
		}
	}

	private final Object replaceCallbackLock = new Object();
	private volatile boolean callbackInvoked = false;

	/**
	 * @deprecated
	 * @param callback
	 * @return
     */
	public boolean replaceCallback(Callback<ResultType> callback) {
		synchronized (replaceCallbackLock) {
			if (!callbackInvoked) {
				this.callback = callback;
				if (callback instanceof Callback.CacheCallback) {
					this.cacheCallback = (Callback.CacheCallback<ResultType>) callback;
				}
				return true;
			}
		}
		return false;
	}

	private long lastUpdateTime;
	private volatile boolean sending = true;
	private final static int UPDATE_STARTED = 0;
	private final static int UPDATE_SENDING = 1;
	private final static int UPDATE_LOADING = 2;
	private final static int UPDATE_FAILURE = 3;
	private final static int UPDATE_CACHE = 4;

	/**
	 * @deprecated
	 * @param total
	 * @param current
	 * @param forceUpdateUI 是否强制刷新UI
	 * @return 是否继续执行
	 */
	@Override
	public boolean updateProgress(long total, long current,
	                              boolean forceUpdateUI) {
		if (isCallbackCancelled()) {
			return false;
		}

		if (progressCallback != null && this.state != State.CANCELLED) {
			if (forceUpdateUI) {
				this.publishProgress(sending ? UPDATE_SENDING : UPDATE_LOADING,
					total, current);
			} else {
				long currTime = SystemClock.uptimeMillis();
				if (currTime - lastUpdateTime >= this.rate) {
					lastUpdateTime = currTime;
					this.publishProgress(sending ? UPDATE_SENDING
						: UPDATE_LOADING, total, current);
				}
			}
		}
		return this.state != State.CANCELLED;
	}

	@Override
	final public void cancel() {
		if (msgDialog != null) {
			msgDialog.dismiss();
			msgDialog = null;
		}

		synchronized (replaceCallbackLock) {
			callbackInvoked = true;
		}

		this.state = State.CANCELLED;

		if (request != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						request.close();
					} catch (Throwable e) {
					}
				}
			}).start();
		}
		if (!this.isCancelled()) {
			try {
				this.cancel(true);
			} catch (Throwable e) {
			}
		}
	}

	private boolean isCallbackCancelled() {
		return cancelable != null && cancelable.isCancelled();
	}

	private Object eventSource;

	/**
	 * @deprecated
	 * @param eventSource
     */
	public void setEventSource(Object eventSource) {
		this.eventSource = eventSource;
	}

	/**
	 * 判断任务是否停止
	 * @return	true停止否者反之
     */
	@Override
	public boolean isStopped() {
		return this.state == State.CANCELLED || this.state == State.SUCCESS || this.state == State.FAILURE;
	}

	/**
	 * @deprecated
	 */
	public enum State {
		WAITING(0), STARTED(1), SENDING(2), LOADING(3), CANCELLED(4), SUCCESS(5), FAILURE(
			6);
		private int value = 0;

		State(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

		public static State valueOf(int value) {
			switch (value) {
				case 0:
					return WAITING;
				case 1:
					return STARTED;
				case 2:
					return SENDING;
				case 3:
					return LOADING;
				case 4:
					return CANCELLED;
				case 5:
					return SUCCESS;
				case 6:
					return FAILURE;
				default:
					return FAILURE;
			}
		}
	}

	/**
	 * @deprecated
	 * @return
     */
	@Override
	public String toString() {
		return request == null ? "request is null" : request.toString();
	}

	public static void evictAll(){
		HttpCache cache = HttpCacheImpl.getInstance();
		cache.evictAll();
	}

}
