package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.sfmap.api.navi.HudView;

public class HudMirrorImage extends RelativeLayout {
	public int mWidth = 480;
	public int mHeight = 800;
	private HudView mHudView;
	private boolean mIsMirror = false;
	private Bitmap hudMirrorBitmap;
	private Canvas hudMirrorCanvas;
	private Paint hudMirrorPaint;
	private Matrix hudMirrorMatrix;

	public HudMirrorImage(Context paramContext,
						  AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		invalidate();
	}

	protected void dispatchDraw(Canvas paramCanvas) {
		if (this.mIsMirror) {
			if (this.hudMirrorBitmap == null) {
				this.hudMirrorBitmap = Bitmap.createBitmap(this.mWidth,
						this.mHeight, Bitmap.Config.RGB_565);
			}

			if (this.hudMirrorCanvas == null)
				this.hudMirrorCanvas = new Canvas(this.hudMirrorBitmap);

			if (this.hudMirrorPaint == null) {
				this.hudMirrorPaint = new Paint();
			}

			if (this.hudMirrorMatrix == null)
				this.hudMirrorMatrix = new Matrix();

			this.hudMirrorPaint.setAntiAlias(true);
			this.hudMirrorCanvas.drawColor(-16777216);
			super.dispatchDraw(this.hudMirrorCanvas);
			this.hudMirrorMatrix.setScale(1.0F, -1.0F);
			this.hudMirrorMatrix.postTranslate(0.0F, this.mHeight);
			paramCanvas.drawBitmap(this.hudMirrorBitmap, this.hudMirrorMatrix,
					this.hudMirrorPaint);
		} else {
			paramCanvas.drawColor(-16777216);
			super.dispatchDraw(paramCanvas);
		}
	}

	public void recycleMirrorBitmap() {
		if (this.hudMirrorBitmap != null) {
			this.hudMirrorBitmap.recycle();
			this.hudMirrorBitmap = null;
		}
		this.hudMirrorCanvas = null;
		this.hudMirrorMatrix = null;
		this.hudMirrorPaint = null;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		try {
			if (this.mHudView != null)
				this.mHudView.onTouchHudMirrorEvent(paramMotionEvent);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}

		return true;
	}

	public void setMapHudView(HudView paramMapHudView) {
		this.mHudView = paramMapHudView;
	}

	public boolean getMirrorState() {
		return this.mIsMirror;
	}

	public void setMirrorState(boolean paramBoolean) {
		this.mIsMirror = paramBoolean;
	}
}