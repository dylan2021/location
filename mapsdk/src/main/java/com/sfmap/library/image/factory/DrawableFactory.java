package com.sfmap.library.image.factory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.sfmap.plugin.IMPluginManager;

public interface DrawableFactory {
	/**
	 * 图片资源转化
	 * @param source	旧图片资源
	 * @return			新的图片资源
     */
	public Bitmap parseResource(Bitmap source);

	/**
	 * 视频资源转化
	 * @param source	旧的动态图资源
	 * @return			新的动态图资源
     */
	public Movie parseResource(Movie source);

	/**
	 * 返回正在使用的资源
	 * @return	一个Drawable对象
     */
	public Drawable getLoadingDrawable();

	/**
	 * 创建一个Drawable对象
	 * @param bitmap	一个Bitmap对象
	 * @return			一个的Drawable对象
     */
	public Drawable createDrawable(Bitmap bitmap);

	/**
	 * 创建一个Drawable对象
	 * @param movie		一个Movie对象
	 * @return			一个的Drawable对象
     */
	public Drawable createDrawable(Movie movie);

	/**
	 * 返回Movie大小
	 * @param movie		一个Movie对象
	 * @return			Movie对象的大小
     */
	public int getSize(Movie movie);

	/**
	 * 返回Bitmap大小
	 * @param bitmap	一个bitmap对象
	 * @return			bitmap对象的大小
     */
	public int getSize(Bitmap bitmap);

	/**
	 * @deprecated
	 */
	public class DefaultDrawableFactory implements DrawableFactory {

		@Override
		public Bitmap parseResource(Bitmap bitmap) {
			return bitmap;
		}

		@Override
		public Movie parseResource(Movie movie) {
			return movie;
		}

		@Override
		public Drawable createDrawable(Bitmap bitmap) {
			return new SafeBitmapDrawable(bitmap);
		}

		@Override
		public Drawable createDrawable(Movie movie) {
			return new MovieDrawable(movie);
		}

		public Drawable getLoadingDrawable() {
			return null;
		}

		public int getSize(Bitmap bm) {
			if (bm != null) {
				return bm.getRowBytes() * bm.getHeight();
			}
			return 0;
		}

		public int getSize(Movie m) {
			if (m != null) {
				return m.height() * m.width() * 3;
			}
			return 0;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o instanceof DefaultDrawableFactory;
		}
	}

	/**
	 * @deprecated
	 */
	static class SafeBitmapDrawable extends BitmapDrawable {

		public SafeBitmapDrawable(Bitmap bitmap) {
			super(IMPluginManager.getApplication().getResources(), bitmap);
		}

		public void draw(Canvas canvas) {
			try {
				super.draw(canvas);
			} catch (Exception e) {
			}
		}
	}

}
