package com.sfmap.library.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Movie;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;

import com.sfmap.library.Callback;
import com.sfmap.library.util.DebugLog;
import com.sfmap.plugin.IMPluginManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public final class ImageUtil {
	private static final int IMAGE_HEADER_BUFFER = 1024 * 16;
	public static final boolean GC_IGNORED_BITMAP = Build.VERSION.SDK_INT <= 10;
	private static SparseIntArray bitmapRefMap = new SparseIntArray();
	private static final Config COLOR_TYPE = Config.ARGB_8888;
	@SuppressWarnings("unused")
	private static Pattern URL_PATTERN = Pattern.compile(
		"^(?:https?|ftp|file)\\:\\/", Pattern.CASE_INSENSITIVE);
	private static int maxSide;

	private ImageUtil() {
	}

	public static Bitmap createBitmap(String path) {
		return createBitmap(path, -1, -1, Callback.CachePolicyCallback.CachePolicy.Any, null, null);
	}

	public static Bitmap createBitmap(String path, int maxWidth, int maxHeight,
									  Callback.CachePolicyCallback.CachePolicy policy, Callback.Cancelable cancelable, Options options) {
		return (Bitmap) loadMedia(path, maxWidth, maxHeight, cancelable,
			policy, options, true);
	}

	public static Bitmap createBitmap(InputStream in, int maxWidth,
									  int maxHeight, Callback.CachePolicyCallback.CachePolicy policy, Callback.Cancelable cancelable,
									  Options options) {
		return (Bitmap) loadMedia(in, maxWidth, maxHeight, cancelable, policy,
			options, true);
	}

	/**
	 * return Bitmap or Movie
	 *
	 * @param resource
	 * @param maxWidth
	 * @param maxHeight
	 * @param policy
	 * @param options
	 * @return
	 */
	public static Object createMediaContent(Object resource, int maxWidth,
											int maxHeight, Callback.CachePolicyCallback.CachePolicy policy, Callback.Cancelable cancelable,
											Options options) {
		return loadMedia(resource, maxWidth, maxHeight, cancelable, policy,
			options, false);
	}

	/**
	 * @param resource  String path, InputStream
	 * @param maxWidth
	 * @param maxHeight
	 * @param policy
	 * @return
	 */
	private static Object loadMedia(Object resource, int maxWidth,
									int maxHeight, Callback.Cancelable cancelable, Callback.CachePolicyCallback.CachePolicy policy,
									Options options, boolean ignoreMovie) {
		Object bm = null;
		try {
			if (options == null) {
				options = new Options();
				options.inPreferredConfig = COLOR_TYPE;
			}
			Object in = null;
			try {
				in = requireSource(resource, cancelable, policy);
				boolean isMovie;
				if (in instanceof byte[]) {
					isMovie = checkAndInitOptions(in, maxWidth, maxHeight,
						options, ignoreMovie);
				} else if (in instanceof InputStream) {
					BufferedInputStream buf = new BufferedInputStream(
						(InputStream) in, IMAGE_HEADER_BUFFER);
					buf.mark(1024);
					isMovie = checkAndInitOptions(buf, maxWidth, maxHeight,
						options, ignoreMovie);
					if (options.outWidth <= 0) {
						return null;
					}
					try {
						buf.reset();
						in = buf;
					} catch (IOException e) {
						DebugLog.error("read over:" + e);
						toEOFAndClose(buf);
						in = requireSource(resource, cancelable, policy);
					}
				} else {
					return null;
				}

				bm = decodeStream(in, options, isMovie);
			} catch (Throwable e) {
				DebugLog.warn(e);
				options.inSampleSize++;
				toEOFAndClose(in);
				in = requireSource(resource, cancelable, policy);
				bm = decodeStream(in, options, false);
			} finally {
				toEOFAndClose(in);
			}
		} catch (Throwable e) {
			DebugLog.warn(e);
			return null;
		}
		return bm;
	}

	private static Object requireSource(Object source, Callback.Cancelable cancelable,
	                                    Callback.CachePolicyCallback.CachePolicy policy) throws IOException {
		if (source instanceof String) {
			String path = (String) source;
			source = new FileInputStream(path);
		} else if (source instanceof File) {
			source = new FileInputStream((File) source);
		}
		return source;
	}

	private static final Object decodeStream(Object in, Options opts,
	                                         boolean isMovie) {
		if (in instanceof InputStream) {
			if (isMovie) {
				return Movie.decodeStream((InputStream) in);
			}
			return BitmapFactory.decodeStream((InputStream) in, null, opts);
		} else if (in instanceof byte[]) {
			byte[] data = (byte[]) in;
			if (isMovie) {
				return Movie.decodeByteArray(data, 0, data.length);
			}
			return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} else {
			throw new IllegalArgumentException("byte[] and InputStream only!!");
		}
	}

	private static final void toEOFAndClose(Object data) {
		try {
			if (data instanceof InputStream) {
				InputStream in = (InputStream) data;
				in.read();
				in.close();
			}
		} catch (Exception e2) {
		}
	}

	private static int getMaxSide() {
		if (maxSide <= 0) {
			DisplayMetrics dm = IMPluginManager.getApplication().getResources().getDisplayMetrics();
			maxSide = Math.max(dm.widthPixels, dm.heightPixels);
		}
		return maxSide;
	}

	private static boolean checkAndInitOptions(Object in, int maxWidth,
	                                           int maxHeight, Options options, boolean ignoreMovie)
		throws IOException {
		options.inJustDecodeBounds = true;
		try {
			if (!ignoreMovie) {
				InputStream buf;
				if (in instanceof byte[]) {
					buf = new ByteArrayInputStream((byte[]) in);
				} else {
					buf = (BufferedInputStream) in;
				}
				buf.mark(IMAGE_HEADER_BUFFER);
				GifDecoder gd = new GifDecoder(buf);
				int width = gd.getWidth();
				if (width > 0) {// match
					if (options.inSampleSize <= 0) {
						options.inSampleSize = computeSampleSize(maxWidth, maxHeight,
							width, gd.getHeight());
					}
					return gd.isAnimate();
				} else {
					buf.reset();//GIF 解析失败，尝试用android 系统自己的图像解析处理。
				}
			}
			// long t1 = System.nanoTime();
			if (options.inSampleSize <= 0) {
				decodeStream(in, options, false);
				// log.info("parse bound time:" + (System.nanoTime() - t1));
				options.inSampleSize = computeSampleSize(maxWidth, maxHeight,
					options.outWidth, options.outHeight);
			}

			return false;
		} finally {

			options.inJustDecodeBounds = false;
			// 是否允许回收数据
			options.inInputShareable = in instanceof byte[];
			options.inPurgeable = true;
		}

	}

	private static int computeSampleSize(int maxWidth, int maxHeight,
	                                     final int width, final int height) {
		if (width * height < 200 * 200) {
			return 0;
		}

		if (maxWidth <= 0) {
			maxWidth = getMaxSide();
		}
		if (maxHeight <= 0) {
			maxHeight = getMaxSide();
		}
		final float scale;
		if (width > maxWidth || height > maxHeight) { // 防止内存溢出
			scale = Math.max((float) width / maxWidth, (float) height / maxHeight);
		} else {
			scale = 1;
		}
		return (int) Math.ceil(scale);
	}

	/**
	 * @param bitmap
	 * @hide
	 */
	public static void retain(Bitmap bitmap) {
		// hash 冲突无碍
		int group = bitmap.hashCode();
		synchronized (bitmapRefMap) {
			int count = bitmapRefMap.get(group);
			bitmapRefMap.put(group, count + 1);
		}
		DebugLog.info("bitmap size:" + bitmapRefMap.size());
	}

	/**
	 * @param bitmap
	 * @hide
	 */
	public static void release(Bitmap bitmap) {
		// hash 冲突无碍
		int group = bitmap.hashCode();
		synchronized (bitmapRefMap) {
			int index = bitmapRefMap.indexOfKey(group);
			if (index >= 0) {
				int count = bitmapRefMap.valueAt(index);
				if (count > 1) {
					bitmapRefMap.put(group, count - 1);
					return;
				}
				bitmapRefMap.removeAt(index);
			}
		}
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
