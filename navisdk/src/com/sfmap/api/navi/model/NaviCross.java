package com.sfmap.api.navi.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * 路口放大图类
 */
public class NaviCross {
	//原始图片数据
	private byte[] mData;
	//图片宽度
	private int mWidth;
	//图片高度
	private int mHeight;
	//位图数据
	private Bitmap mBitmap;
	//图片格式 1为PNG,2为BMP
	private int mPicFormat;

	/**
	 * 构造函数。
	 * @param picFormat - 路口放大图图片格式，1 为栅格PNG图片，2 为矢量BMP图片
	 * @param width - 图片宽度
	 * @param height - 图片高度
	 * @param data - 图片数据字节数组
	 */
	public NaviCross(int picFormat, int width, int height, byte[] data) {
		this.mPicFormat = picFormat;
		this.mHeight = height;
		this.mWidth = width;
		this.mData = data;

		if (this.mData.length != 0)
			this.mBitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
		this.mData = null;
	}
	/**
	 * 构造函数。
	 * @param picFormat - 路口放大图图片格式，1 为栅格PNG图片，2 为矢量BMP图片
	 * @param width - 图片宽度
	 * @param height - 图片高度
	 * @param backgroud - 背景图片数据字节数组
	 * @param data - 箭头图片数据字节数组
	 */
	public NaviCross(int picFormat, int width, int height, byte[] backgroud, byte[] data) {
		this.mPicFormat = picFormat;
		this.mHeight = height;
		this.mWidth = width;

		this.mBitmap=mergeBitmap(BitmapFactory.decodeByteArray(backgroud, 0,backgroud.length),BitmapFactory.decodeByteArray(data, 0,data.length));
	}

	/**
	 * 获取该路口放大图的bitmap。
	 * @return 该路口放大图的bitmap
	 */
	public Bitmap getBitmap() {
		return this.mBitmap;
	}

	/**
	 * 获取图片格式。
	 * @return int 图片格式，1 为栅格PNG图片，2 为矢量BMP图片
	 */
	public int getPicFormat() {
		return this.mPicFormat;
	}

	/**
	 * 获取图片宽度。
	 * @return 该图片宽度
	 */
	public int getWidth() {
		return this.mWidth;
	}

	/**
	 * 获取图片高度。
	 * @return 该图片高度
	 */
	public int getHeight() {
		return this.mHeight;
	}

	private Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
		if(firstBitmap == null || secondBitmap == null){
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),firstBitmap.getConfig());
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(firstBitmap, new Matrix(), null);
		if(null != secondBitmap){
			canvas.drawBitmap(secondBitmap, 0, 0, null);
		}
		return bitmap;
	}
}