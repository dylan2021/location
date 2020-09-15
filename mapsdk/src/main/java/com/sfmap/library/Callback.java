package com.sfmap.library;

import android.app.Dialog;

import com.sfmap.library.http.HttpAsyncTask;
import com.sfmap.library.http.cache.HttpCacheEntry;
import com.sfmap.library.task.Priority;
import java.net.URLConnection;
import java.util.concurrent.Executor;

/**
 * 通用回掉接口
 *
 * @param <ResultType>
 */
public interface Callback<ResultType> {
	/**
	 * 在调用时所在的线程执行回调处理（比如事件发起的调用是在ui线程，回调就可以直接操作ui元素）
	 *
	 * @param result 经过自动数据转换后的对象， 如，我们要求一个JavaBean，他会自动构造该对象，
	 *               并且递归初始化他的属性（从json属性值自动转换类型并赋值），支持范型
	 */
	public void callback(ResultType result);

	/**
	 * 异常回调
	 *
	 * @param ex			一个Throwable对象
	 * @param callbackError 是否是发生在callback函数中的异常
	 * @return
	 */
	public void error(Throwable ex, boolean callbackError);

	/**
	 * 取消任务接口，比如 发起一个http请求， 会返回一个该对象， 然后我们可以在任意地点尝试取消这个请求。
	 *
	 */
	public interface Cancelable {
		/**
		 * 取消任务
		 */
		void cancel();

		/**
		 * 判断任务是否被取消
		 * @return	true为取消否者反之
         */
		boolean isCancelled();
	}

	/**
	 * 允许后台执行预处理的回调任务。
	 * 一般回掉函数会抛回主线程执行，但是如果我们需要做长时间的耗时操作，就可以用这个借口，吧耗时操作放在prepare中执行。
	 *
	 * @param <RawType>		范型,解析类型
	 * @param <ResultType>	范型,结果类型
	 */
	public interface PrepareCallback<RawType, ResultType> extends
		Callback<ResultType> {
		/**
		 * 在后台线程执行耗时的数据处理。这个过程在后台执行，不在ui线程执行，不能操作ui元素
		 * @param rawData	解析类型
		 * @return			结果类型
		 */
		public ResultType prepare(RawType rawData);
	}

	/**
	 * 缓存双回调接口， 用该接口访问网络的时候，
	 * 为保证内容的快速展现，先直接获取缓存，回调一次callback，然后再从网络获取，结束后回调update(如果内容物变化，参数为null)
	 *
	 * @param <ResultType>
	 */
	public interface CacheCallback<ResultType> extends Callback<ResultType> {
		/**
		 * 获取缓存数据后立即回掉，如果没有有效缓存，cacheData 而我null
		 *
		 * @param cacheData
		 * @return 返回是否信任缓存，如果信任，不在发送网络请求，直接调用callback(null).
		 */
		public boolean cache(ResultType cacheData, HttpCacheEntry cacheEntry);

		/**
		 * 当网络请求返回后回调，如果数据相对缓存数据没有变化，则newData为空，否则为新数据
		 *
		 * @param newData 更新数据（如果数据相对缓存没有变化，该方法依然被调用，但是其值为null）
		 */
		public void callback(ResultType newData);
	}

	/**
	 * 自定义Dialog
	 */
	public interface CustomDlgCallback{
		/**
		 * 回调一个自定义Dialog提示,通过此方法可以回调给上层一个任务对象。
		 * @param task	一个HttpAsyncTask类型的任务
         * @return	一个Dilaog
         */
		Dialog onLoadDlg(HttpAsyncTask<?> task);
	}

	/**
	 * 支持进度的回调接口
	 */
	public interface ProgressCallback extends CancelledCallback {
		/**
		 *	开始方法
		 */
		void onStart();

		/**
		 * 进度回调方法
		 *
		 * @param total	下载总大小
		 * @param current	当前大小
		 */
		void onLoading(long total, long current);

		/**
		 * 下载文件时文件保存的路径和文件名
		 * @return	存储路径
		 */
		String getSavePath();
	}

	/**
	 * 任务取消回调接口
	 */
	public interface CancelledCallback {
		/**
		 * 任务取消方法
		 */
		void onCancelled();
	}

	/**
	 * 添加缓存策略
	 */
	public interface CachePolicyCallback {
		/**
		 * 网络请求的缓存策略
		 */
		public enum CachePolicy {
			/**
			 * 只允许缓存
			 */
			CacheOnly,
			/**
			 * 只允许网络请求
			 */
			NetworkOnly,
			/**
			 * 缓存或者网络请求
			 */
			Any
		}

		/**
		 * 返回缓存策略
		 * @return	一个CachePolicy枚举类型
		 */
		public CachePolicy getCachePolicy();

		/**
		 * 返回获取自定义cache key
		 * @return	返回一个String类型的key,默认返回url
		 */
		public String getCacheKey();
	}

	/**
	 * 为请求设置独立的线程池
	 */
	public interface RequestExecutor {
		/**
		 * 返回线程池
		 * @return	一个线程池对象
         */
		Executor getExecutor();
	}

	/**
	 * 优先级控制接口
	 */
	public interface RequestPriority {
		/**
		 * 返回任务的优先级
		 * @return	一个优先级对象
         */
		Priority getPriority();
	}

	/**
	 * 响应监听接口
	 */
	public interface ResponseListener {
		/**
		 *	回调响应方法,改方法返回http请求的connection参数
		 * @param connection	一个URLConnection对象
         */
		void onResponse(URLConnection connection);
	}

	/**
	 * 页面跳转和接口绑定,可用于跳转页面任务的操作。
	 */
	public interface BindPage {
		/**
		 * 返回当前page对象
		 * @return 返回一个Page类型
         */
		Page getPage();
	}

	/**
	 * 图片大小接口
	 */
	public interface ImageSize {
		/**
		 * 返回图片的宽度
		 * @return	图片宽度
         */
		int getWidth();

		/**
		 * 返回图片的高度
		 * @return	图片高度
         */
		int getHeight();
	}

}
