package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sfmap.tbt.ResourcesUtil;

/*
指南针view
 */
public class DirectionView extends ImageView {
	private Bitmap mDirectionBitmap;
	private Matrix mDirectionMatrix = new Matrix();
	private Paint mPaint;
	private PaintFlagsDrawFilter mPaintFlagsDrawFilter;

	public DirectionView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		initPaint();
	}

	public DirectionView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		initPaint();
	}

	public DirectionView(Context paramContext) {
		super(paramContext);
		initPaint();
	}

	private void initPaint() {
		if (this.mPaint == null) {
			this.mPaint = new Paint();
			this.mPaint.setAntiAlias(true);
			this.mDirectionBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), ResourcesUtil.navi_over_data_yaw_icon);
		}
		this.mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
	}

	public void setRotate(float paramFloat) {
		if (this.mDirectionBitmap == null)
			return;
		this.mDirectionMatrix.setRotate(paramFloat,
				this.mDirectionBitmap.getWidth() / 2.0F,
				this.mDirectionBitmap.getHeight() / 2.0F);

		Bitmap localBitmap = Bitmap.createBitmap(
				this.mDirectionBitmap.getWidth(),
				this.mDirectionBitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas localCanvas = new Canvas(localBitmap);
		localCanvas.setDrawFilter(this.mPaintFlagsDrawFilter);
		if ((this.mDirectionBitmap != null) && (this.mDirectionMatrix != null))
			localCanvas.drawBitmap(this.mDirectionBitmap,
					this.mDirectionMatrix, this.mPaint);

		setImageBitmap(localBitmap);
	}

	public void recycleResource() {
		if (this.mDirectionBitmap != null) {
			this.mDirectionBitmap.recycle();
			this.mDirectionBitmap = null;
		}
	}

	public Bitmap getDirectionBitmap() {
		return this.mDirectionBitmap;
	}

	public void setDirectionBitmap(Bitmap paramBitmap) {
		this.mDirectionBitmap = paramBitmap;
	}
}