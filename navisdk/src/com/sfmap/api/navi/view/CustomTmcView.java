package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sfmap.api.navi.model.NaviTrafficStatus;

import com.sfmap.tbt.ResourcesUtil;

import java.util.List;

/**
 * 蚯蚓线view
 */
public class CustomTmcView extends ImageView {
	int left;
	int right;
	int progressBarHeight;
	Paint paint;
	Bitmap displayingBitmap;
	Bitmap tmcBarBitmapPortrait;
	Bitmap tmcBarBitmapLandscape;
	private List<NaviTrafficStatus> mTmcSections;
	private int tmcBarTopMargin = 30;
	private Bitmap rawBitmap;
	private int totalDis = 0;
	private RectF colorRectF;
	private int drawTmcBarBgX;
	private int drawTmcBarBgY;
	private int tmcBarBgWidth;
	private int tmcBarBgHeight = 0;
	public CustomTmcView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		initResource();
	}

	public CustomTmcView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		initResource();
	}

	public CustomTmcView(Context paramContext) {
		super(paramContext);
		initResource();
	}

	public Bitmap getDisplayingBitmap() {
		return this.displayingBitmap;
	}

	private void initResource() {
		this.rawBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), ResourcesUtil.navigation_light_bg);
		this.tmcBarBitmapPortrait = this.rawBitmap;

		this.left = (this.tmcBarBitmapPortrait.getWidth() * 20 / 100);

		this.right = (this.tmcBarBitmapPortrait.getWidth() * 78 / 100);

		this.progressBarHeight = (int) (this.tmcBarBitmapPortrait.getHeight() * 0.8100000000000001D);

		this.tmcBarBgWidth = this.tmcBarBitmapPortrait.getWidth();

		this.tmcBarBgHeight = this.tmcBarBitmapPortrait.getHeight();

		this.paint = new Paint();

		if (Build.VERSION.SDK_INT >= 11)
			this.tmcBarTopMargin = (Math.abs(this.progressBarHeight	- this.tmcBarBitmapPortrait.getHeight()) / 4 - (int) (this.progressBarHeight * 0.017D));
		else {
			this.tmcBarTopMargin = (Math.abs(this.progressBarHeight	- this.tmcBarBitmapPortrait.getHeight()) / 4 - 3);
		}

		setTmcBarHeightWhenLandscape(0.6666666666666666D);
		this.displayingBitmap = this.tmcBarBitmapPortrait;

		this.colorRectF = new RectF();
	}

	public void onConfigurationChanged(boolean paramBoolean) {
		if (paramBoolean)
			this.displayingBitmap = this.tmcBarBitmapLandscape;
		else
			this.displayingBitmap = this.tmcBarBitmapPortrait;

		setProgressBarSize(paramBoolean);
	}

	private void setProgressBarSize(boolean paramBoolean) {
		this.progressBarHeight = (int) (this.displayingBitmap.getHeight() * 0.8100000000000001D);
		this.tmcBarBgWidth = this.displayingBitmap.getWidth();
		this.tmcBarBgHeight = this.displayingBitmap.getHeight();
		if (!(paramBoolean))
			if (Build.VERSION.SDK_INT >= 11)
				this.tmcBarTopMargin = (Math.abs(this.progressBarHeight	- this.displayingBitmap.getHeight()) / 4 - (int) (this.progressBarHeight * 0.017D));
			else
				this.tmcBarTopMargin = (Math.abs(this.progressBarHeight	- this.displayingBitmap.getHeight()) / 4 - 4);

		else
			this.tmcBarTopMargin = (Math.abs(this.progressBarHeight	- this.displayingBitmap.getHeight()) / 4 - (int) (this.progressBarHeight * 0.017D));
	}

	public void update(List<NaviTrafficStatus> paramList, int totalDistance) {
		this.mTmcSections = paramList;
		this.totalDis = totalDistance;

		Bitmap localBitmap = produceFinalBitmap();
		if (localBitmap != null)
			setImageBitmap(produceFinalBitmap());
	}

	Bitmap produceFinalBitmap() {
		if (this.mTmcSections == null)
			return null;
		Bitmap localBitmap = Bitmap.createBitmap(this.displayingBitmap.getWidth(),this.displayingBitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas localCanvas = new Canvas(localBitmap);

		this.paint.setStyle(Paint.Style.FILL);
		float distance = this.totalDis;
		for (int i = 0; i < this.mTmcSections.size(); ++i) {
			NaviTrafficStatus trafficStatus = (NaviTrafficStatus) this.mTmcSections.get(i);
			switch (trafficStatus.getStatus()) {
			case 1:
				this.paint.setColor(Color.parseColor("#6db24f"));
				break;
			case 2:
				this.paint.setColor(Color.parseColor("#f1c060"));
				break;
			case 3:
				this.paint.setColor(Color.parseColor("#ce4839"));
//				this.paint.setColor(Color.argb(255, 255, 93, 91));
				break;
			case 4:
				this.paint.setColor(Color.parseColor("#8e2b21"));
//				this.paint.setColor(Color.argb(255, 179, 17, 15));
//				this.paint.setColor(Color.argb(255, 179, 17, 15));
				break;
			default:
				this.paint.setColor(Color.parseColor("#6db24f"));/**/
//				this.paint.setColor(Color.argb(255, 26, 166, 239));
//				this.paint.setColor(Color.argb(255, 26, 166, 239));
			}

			if (distance - trafficStatus.getLength() > 0.0F) {
				this.colorRectF.set(this.left, this.progressBarHeight * (distance - trafficStatus.getLength())/ this.totalDis + this.tmcBarTopMargin, this.right,	this.progressBarHeight * distance / this.totalDis	+ this.tmcBarTopMargin);
			} else {
				this.colorRectF.set(this.left, this.tmcBarTopMargin,this.right, this.progressBarHeight * distance / this.totalDis	+ this.tmcBarTopMargin);
			}

			if (i == this.mTmcSections.size() - 1) {
				this.colorRectF.set(this.left, this.tmcBarTopMargin,this.right, this.progressBarHeight * distance / this.totalDis	+ this.tmcBarTopMargin);
			}

			localCanvas.drawRect(this.colorRectF, this.paint);
			distance -= trafficStatus.getLength();
		}
		this.paint.setColor(-65536);//Color.WHITE

		localCanvas.drawBitmap(this.displayingBitmap, 0.0F, 0.0F, null);
		return localBitmap;
	}

	public void setTmcBarPosition(int paramInt1, int paramInt2, int paramInt3,	int paramInt4, boolean paramBoolean) {
		setTmcBarHeightWhenLandscape(0.6666666666666666D * paramInt2/ paramInt3);
		setTmcBarHeightWhenPortrait(1.0D * paramInt2 / paramInt3);

		paramInt4 = paramInt4 * paramInt2 / paramInt3;

		onConfigurationChanged(paramBoolean);
		if (paramBoolean) {
			this.drawTmcBarBgX = Math.abs(paramInt1	- (int) (1.3D * this.tmcBarBgWidth));
			this.drawTmcBarBgY = ((paramInt2 - (this.tmcBarBgHeight / 2)) * 6 / 10);
		} else {
			this.drawTmcBarBgX = Math.abs(paramInt1	- (int) (1.3D * this.tmcBarBgWidth));
			this.drawTmcBarBgY = (int) (paramInt2 - (1.5D * paramInt4) - this.tmcBarBgHeight);
		}
	}

	public void setTmcBarHeightWhenLandscape(double paramDouble) {
		if (paramDouble > 1.0D)
			paramDouble = 1.0D;
		else if (paramDouble < 0.1D)
			paramDouble = 0.1D;

		this.tmcBarBitmapLandscape = Bitmap.createScaledBitmap(this.rawBitmap,this.rawBitmap.getWidth(),(int) (this.rawBitmap.getHeight() * paramDouble), true);
	}

	public void setTmcBarHeightWhenPortrait(double paramDouble) {
		if (paramDouble > 1.0D)
			paramDouble = 1.0D;
		else if (paramDouble < 0.1D)
			paramDouble = 0.1D;

		this.tmcBarBitmapPortrait = Bitmap.createScaledBitmap(this.rawBitmap,this.rawBitmap.getWidth(),	(int) (this.rawBitmap.getHeight() * paramDouble), true);

		this.displayingBitmap = this.tmcBarBitmapPortrait;
		setProgressBarSize(false);
	}

	public int getTmcBarBgPosX() {
		return this.drawTmcBarBgX;
	}

	public int getTmcBarBgPosY() {
		return this.drawTmcBarBgY;
	}

	public int getTmcBarBgWidth() {
		return this.tmcBarBgWidth;
	}

	public int getTmcBarBgHeight() {
		return this.tmcBarBgHeight;
	}
}