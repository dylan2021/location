package com.sfmap.api.navi.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.ResourcesUtil;

/**
 * 路口放大图view类。
 */
public class NaviRoadEnlargeView extends RelativeLayout {
	private Bitmap rawRoadEnlargeBitmap = null;
	private View enlargeLayout;
	private ImageView enlargedRoadImageView;

	public NaviRoadEnlargeView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init();
	}

	public NaviRoadEnlargeView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public NaviRoadEnlargeView(Context paramContext) {
		super(paramContext);
		init();
	}

	private void init() {
		this.enlargeLayout = ResourcesUtil.getViewByID((Activity)getContext(), ResourcesUtil.sdk_road_enlarge_layout, null);

		addView(this.enlargeLayout);

		findView();
	}

	private void findView() {
		try {
			this.enlargedRoadImageView = ((ImageView) this.enlargeLayout.findViewById(ResourcesUtil.naviRoadEnlargeView));//2131427333

		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void setBitMapIntoView(NaviCross paramMapNaviCross) {
		this.rawRoadEnlargeBitmap = paramMapNaviCross.getBitmap();
		this.enlargedRoadImageView.setImageBitmap(this.rawRoadEnlargeBitmap);
	}

	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);

	}

	public void recycleResource() {

		if (this.rawRoadEnlargeBitmap != null) {
//			this.rawRoadEnlargeBitmap.recycle();
			this.rawRoadEnlargeBitmap = null;
		}

	}
}