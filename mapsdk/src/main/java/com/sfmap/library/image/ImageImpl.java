package com.sfmap.library.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.sfmap.library.Callback;
import com.sfmap.library.http.NetworkImpl;
import com.sfmap.library.http.cache.HttpCacheEntry;
import com.sfmap.library.http.utils.LruMemoryCache;
import com.sfmap.library.image.factory.DrawableFactory;
import com.sfmap.library.task.Priority;
import com.sfmap.library.util.DebugLog;
import com.sfmap.plugin.task.pool.PriorityExecutor;
import com.sfmap.plugin.IMPluginManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ImageImpl implements ImageLoader {

	private boolean pauseTask = false;
	private boolean cancelAllTack = false;
	private final Object pauseTaskLock = new Object();

	private static int maxCapacity = 4 * 1024 * 1024;// 4M
	private static PriorityExecutor executor = new PriorityExecutor(3);
	private static DrawableFactory DEFAULT_FACTORY = new DrawableFactory.DefaultDrawableFactory();
	/**
	 * 返回Image实体单例
	 */
	public static final ImageImpl INSTANCE = new ImageImpl();

	private ImageImpl() {
	}

	/**
	 * @deprecated
	 */
	private final static LruMemoryCache<CacheKey, CacheInfo> mMemoryCache = new LruMemoryCache<CacheKey, CacheInfo>(
		maxCapacity) {
		/**
		 * Measure item size in bytes rather than units which is more practical
		 * for a bitmap cache
		 */
		@Override
		protected int sizeOf(CacheKey key, CacheInfo cacheInfo) {
			if (cacheInfo == null)
				return 0;
			return cacheInfo.size;
		}

		@Override
		protected void removeLike(CacheKey key) {
			List<CacheKey> rmList = new ArrayList<CacheKey>(3);
			for (CacheKey k : map.keySet()) {
				if (key.url.equals(k.url)) {
					rmList.add(k);
				}
			}
			for (CacheKey k : rmList) {
				CacheInfo info = map.remove(k);
				keyExpiryMap.remove(k);
				size -= info.size;
			}
		}
	};
	/**
	 * @deprecated
	 */
	@Override
	public void bind(final ImageView view, final String url,
	                 DrawableFactory factory, final int fallbackResource,
	                 final Callback<Drawable> callback) {
		if (view == null) {
			bind(new ImageView(IMPluginManager.getApplication()), url, factory,
				fallbackResource, callback);
			return;
		}
		if (url == null || url.length() == 0) {
			DebugLog.fatal("invalid view(" + view + ") or url(" + url + ")");
			if (fallbackResource > 0) {
				view.setImageResource(fallbackResource);
			}
			return;
		}
		if (factory == null) {
			factory = DEFAULT_FACTORY;
		}
		if (!bitmapLoadTaskExist(view, url)) {
			new ImageLoaderCallback(view, url, factory, callback,
				fallbackResource).doLoad();
		}
	}

	/**
	 * @deprecated
	 */
	private void updateImageDrawable(ImageView view, Drawable drawable) {
		if (drawable instanceof AsyncDrawable) {
			Drawable baseDrawable = ((AsyncDrawable) drawable)
				.getBaseDrawable();
			if (baseDrawable == null && view.getDrawable() != null) {
				((AsyncDrawable) drawable).setBaseDrawable(view.getDrawable());
			}
		}
		view.setImageDrawable(drawable);
	}

	/**
	 * @deprecated
	 */
	@Override
	public boolean supportPause() {
		return true;
	}

	/**
	 * @deprecated
	 */
	@Override
	public boolean supportResume() {
		return true;
	}

	/**
	 * @deprecated
	 */
	@Override
	public boolean supportCancel() {
		return true;
	}

	/**
	 * @deprecated
	 */
	@Override
	public boolean isPaused() {
		return pauseTask;
	}

	/**
	 * @deprecated
	 */
	@Override
	public boolean isCancelled() {
		return cancelAllTack;
	}

	/**
	 * @deprecated
	 */
	@Override
	public void resume() {
		pauseTask = false;
		synchronized (pauseTaskLock) {
			pauseTaskLock.notifyAll();
		}
	}

	/**
	 * @deprecated
	 */
	@Override
	public void pause() {
		pauseTask = true;
		// flushCache(); // flush disk cache
	}

	/**
	 * @deprecated
	 */
	@Override
	public void cancel() {
		pauseTask = true;
		cancelAllTack = true;
		synchronized (pauseTaskLock) {
			pauseTaskLock.notifyAll();
		}
	}

	/**
	 * @deprecated
	 */
	private static ImageLoaderCallback getImageLoaderCallback(ImageView view) {
		if (view != null) {
			final Drawable drawable = view.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getImageLoaderCallback();
			}
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	private static <T extends View> boolean bitmapLoadTaskExist(ImageView view,
	                                                            String url) {
		final ImageLoaderCallback oldLoadTask = getImageLoaderCallback(view);

		if (oldLoadTask != null) {
			final String oldUrl = oldLoadTask.url;
			if (TextUtils.isEmpty(oldUrl) || !oldUrl.equals(url)) {
				oldLoadTask.cancel();
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @deprecated
	 */
	class ImageLoaderCallback implements
		Callback.CacheCallback<Drawable>,
		Callback.CachePolicyCallback,
			Callback.PrepareCallback<File, Drawable>,
		Callback.ProgressCallback,
		Callback.RequestExecutor {
		WeakReference<ImageView> viewRef;
		final String url;
		DrawableFactory factory;
		Callback<Drawable> callback;
		ProgressCallback progressCallback;
		int fallbackResourceId;
		Cancelable networkCallback;
		BitmapSize bitmapSize;
		CacheKey cacheKey;
		boolean cacheSuccess = false;
		boolean callbacked;

		/**
		 * @deprecated
		 */
		ImageLoaderCallback(ImageView view, String url,
		                    DrawableFactory factory, Callback<Drawable> callback, int resId) {
			int width = -1;
			int height = -1;
			if (callback instanceof ImageSize) {
				ImageSize imageSize = (ImageSize) callback;
				width = imageSize.getWidth();
				height = imageSize.getHeight();
			}
			bitmapSize = BitmapCommonUtils.optimizeMaxSizeByView(view, width, height);
			updateImageDrawable(view,
				new AsyncDrawable(factory.getLoadingDrawable(), this));
			this.viewRef = new WeakReference<ImageView>(view);
			this.factory = factory == null ? DEFAULT_FACTORY : factory;
			this.url = url;
			this.callback = callback;
			if (callback instanceof ProgressCallback) {
				this.progressCallback = (ProgressCallback) callback;
			}
			this.fallbackResourceId = resId;
			this.cacheKey = new CacheKey(url, bitmapSize, factory);
		}

		/**
		 * @deprecated
		 */
		private boolean useMemCache() {
			ImageView view = getTargetImageView();
			if (view == null) {
				return false;
			}

			// debug
			//if (DebugLog.isDebug()) {
			//	android.util.Log.d("IMG_CACHE", mMemoryCache.toString());
			//}
			if (cacheKey.url.startsWith("http")) {
				CacheInfo info = mMemoryCache.get(cacheKey);
				if (info != null && info.cache != null) {
					info.attach(view);
					this.onLoading(info.size, info.size);
					if (this.callback instanceof CacheCallback) {
						if (!((CacheCallback<Drawable>) this.callback).cache(info.cache, null)) {
							return false;
						}
					}
					this.callback(info.cache);
					return true;
				}
			}
			return false;
		}

		/**
		 * @deprecated
		 */
		void doLoad() {
			if (!useMemCache()) {
				networkCallback = NetworkImpl.getInstance().get(
					this, url, null, Priority.BG_LOW);
			}
		}

		/**
		 * @deprecated
		 */
		@Override
		public Drawable prepare(File file) {
			synchronized (pauseTaskLock) {
				while (pauseTask
					&& (networkCallback != null && !networkCallback.isCancelled())) {
					try {
						pauseTaskLock.wait();
						if (cancelAllTack) {
							return null;
						}
					} catch (Throwable e) {
					}
				}
			}

			ImageView view = getTargetImageView();
			if (view == null) {
				return null;
			}

			Drawable result = null;
			Object data = null;
			if (file != null && file.exists()) {
				data = ImageUtil.createMediaContent(file,
					bitmapSize.getWidth(), bitmapSize.getHeight(),
					CachePolicy.Any, networkCallback, null);
				if (data == null) {
					try {
						file.delete();
					} catch (Throwable ignore) {
					}
				}
			}
			if (data != null) {
				result = convert2Drawable(data);
			} else if (!networkCallback.isCancelled()) {
				byte[] in = null;
				try {
					in = NetworkImpl.getInstance().loadBytes(url, null);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				if (in != null) {
					data = ImageUtil.createMediaContent(in,
						bitmapSize.getWidth(), bitmapSize.getHeight(),
						CachePolicy.Any, networkCallback, null);
					if (data != null) {
						result = convert2Drawable(data);
					}
				}
			}
			return result;
		}

		/**
		 * @deprecated
		 */
		public Drawable convert2Drawable(Object rawData) {
			ImageView view = getTargetImageView();
			if (view == null) {
				return null;
			}

			Drawable drawable = null;
			if (rawData instanceof Bitmap) {
				Bitmap bitmap = factory.parseResource((Bitmap) rawData);
				drawable = factory.createDrawable(bitmap);
				if (drawable != null) {
					if (drawable instanceof BitmapDrawable) {
						bitmap = ((BitmapDrawable) drawable).getBitmap();
					}
					mMemoryCache.put(cacheKey, new CacheInfo(view, drawable, factory.getSize(bitmap)));
				}
			} else if (rawData instanceof Movie) {
				Movie movie = factory.parseResource((Movie) rawData);
				drawable = factory.createDrawable(movie);
				if (drawable != null) {
					mMemoryCache.put(cacheKey, new CacheInfo(view, drawable, factory.getSize(movie)));
				}
			}
			return drawable;
		}

		/**
		 * @deprecated
		 */
		public void cancel() {
			if (networkCallback != null) {
				if (!networkCallback.isCancelled()) {
					networkCallback.cancel();
				}
			}
		}

		/**
		 * @deprecated
		 */
		@Override
		public void callback(Drawable drawable) {
			if (callbacked && drawable == null) {
				return;
			}

			if (callback != null && !cacheSuccess) {
				callback.callback(drawable);
			}

			ImageView view = getTargetImageView();
			if (view == null) {
				return;
			}

			if (drawable == null) {
				useFallbackResource();
			} else {
				updateImageDrawable(view, drawable);
			}
			callbacked = true;
		}

		/**
		 * @deprecated
		 */
		@Override
		public boolean cache(Drawable cacheImage, HttpCacheEntry cacheEntry) {
			if (cacheImage == null) {
				return false;
			}
			if (this.callback instanceof CacheCallback) {
				if (!((CacheCallback<Drawable>) this.callback).cache(cacheImage, cacheEntry)) {
					return false;
				} else {
					cacheSuccess = true;
				}
			}

			this.callback(cacheImage);
			return true;
		}

		/**
		 * @deprecated
		 */
		private void useFallbackResource() {
			ImageView view = getTargetImageView();
			if (view == null) {
				return;
			}

			if (!callbacked && fallbackResourceId >= 0) {
				updateImageDrawable(view, null);
				view.setImageResource(fallbackResourceId);
			}
		}

		/**
		 * @deprecated
		 */
		public void error(Throwable ex, boolean callbackError) {
			if (callbacked) {
				return;
			}
			if (callback != null) {
				callback.error(ex, callbackError);
			} else {
				ex.printStackTrace();
			}
			useFallbackResource();
		}

		/**
		 * @deprecated
		 */
		public ImageView getTargetImageView() {
			ImageView result = null;
			final ImageView container = viewRef.get();
			final ImageLoaderCallback bitmapWorkerTask = getImageLoaderCallback(container);

			if (this == bitmapWorkerTask) {
				result = container;
			}

			if (result == null) {
				ImageLoaderCallback.this.cancel();
			}

			return result;
		}

		/**
		 * @deprecated
		 */
		@Override
		public void onStart() {

		}

		/**
		 * @deprecated
		 */
		@Override
		public void onLoading(long total, long current) {
			if (progressCallback != null) {
				progressCallback.onLoading(total, current);
			}
		}

		@Override
		public void onCancelled() {
			if (progressCallback != null) {
				progressCallback.onCancelled();
			}
		}

		/**
		 * @deprecated
		 */
		@Override
		public String getSavePath() {
			return null;
		}

		/**
		 * @deprecated
		 */
		@Override
		public Executor getExecutor() {
			return executor;
		}

		/**
		 * @deprecated
		 */
		@Override
		public CachePolicy getCachePolicy() {
			return this.url.startsWith("http") ? CachePolicy.Any : CachePolicy.NetworkOnly;
		}

		/**
		 * @deprecated
		 */
		@Override
		public String getCacheKey() {
			return this.url;
		}
	}

	private static class CacheKey {
		final String url;
		final BitmapSize size;
		final DrawableFactory factory;

		/**
		 * @deprecated
		 */
		private CacheKey(String url, BitmapSize size, DrawableFactory factory) {
			this.url = url;
			this.size = size;
			this.factory = factory;
		}

		/**
		 * @deprecated
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			CacheKey cacheKey = (CacheKey) o;

			if (!url.equals(cacheKey.url))
				return false;
			if (!factory.equals(cacheKey.factory))
				return false;
			if (!size.equals(cacheKey.size))
				return false;

			return true;
		}

		/**
		 * @deprecated
		 */
		@Override
		public int hashCode() {
			int result = url.hashCode();
			result = 31 * result + size.hashCode();
			result = 31 * result + factory.hashCode();
			return result;
		}

		/**
		 * @deprecated
		 */
		@Override
		public String toString() {
			return url;
		}
	}

	private static class CacheInfo {
		final int size;
		final Drawable cache;
		int activityMask;

		/**
		 * @deprecated
		 */
		CacheInfo(ImageView view, Drawable drawable, int size) {
			this.cache = drawable;
			this.activityMask = Math.abs(System.identityHashCode(view.getContext()));
			this.size = size;
		}

		/**
		 * @deprecated
		 */
		@SuppressWarnings("unused")
		boolean maybeContains(Context activity) {
			int hash = Math.abs(System.identityHashCode(activity));
			return hash == (hash & activityMask);
		}

		/**
		 * @deprecated
		 */
		void attach(ImageView view) {
			int hash = Math.abs(System.identityHashCode(view.getContext()));
			this.activityMask |= hash;

		}
	}

	/**
	 * @deprecated
	 */
	@Override
	public boolean isStopped() {
		return false;
	}

}
