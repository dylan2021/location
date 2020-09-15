package com.sfmap.api.navi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sfmap.api.navi.NaviView;
import com.sfmap.tbt.ResourcesUtil;
import com.sfmap.tbt.util.LogUtil;

/**
 * 车道view类 提示用户当前的车道有几个 且不同车道对应的前进方向为什么（比如：直行+右转；掉头）
 */
public class DriveWayView extends ImageView {
	private final String TAG = this.getClass().getSimpleName();
	private int driveWayWidth = 0;
	private int driveWayHeight = 0;
	private int driveWaySize = 0;
	private int COMMON_VALUE = 10;
	private static int[] driveWayBackgroundId = {
			ResourcesUtil.back_0,
			ResourcesUtil.back_1,
			ResourcesUtil.back_2,
			ResourcesUtil.back_3,
			ResourcesUtil.back_4,
			ResourcesUtil.back_5,
			ResourcesUtil.back_6,
			ResourcesUtil.back_7,
			ResourcesUtil.back_8,
			ResourcesUtil.back_9,
			ResourcesUtil.back_10,
			ResourcesUtil.back_11,
			ResourcesUtil.back_12,
			ResourcesUtil.back_13,
//			ResourcesUtil.back_14,
			ResourcesUtil.back_25};
	private static int[] driveWayForegroundId = {
			ResourcesUtil.front_0,
			ResourcesUtil.front_1,
			ResourcesUtil.back_2,
			ResourcesUtil.front_2,
			ResourcesUtil.back_4,
			ResourcesUtil.front_3,
			ResourcesUtil.back_6,
			ResourcesUtil.back_7,
			ResourcesUtil.front_4,
			ResourcesUtil.back_9,
			ResourcesUtil.back_10,
			ResourcesUtil.back_11,
			ResourcesUtil.back_12,
			ResourcesUtil.front_22,
//			ResourcesUtil.back_14,
			ResourcesUtil.front_25};
	private static int[] hud_driveWayBackgroundId = {
			ResourcesUtil.hud_back_0,
			ResourcesUtil.hud_back_1,
			ResourcesUtil.hud_back_2,
			ResourcesUtil.hud_back_3,
			ResourcesUtil.hud_back_4,
			ResourcesUtil.hud_back_5,
			ResourcesUtil.hud_back_6,
			ResourcesUtil.hud_back_7,
			ResourcesUtil.hud_back_8,
			ResourcesUtil.hud_back_9,
			ResourcesUtil.hud_back_10,
			ResourcesUtil.hud_back_11,
			ResourcesUtil.hud_back_12,
			ResourcesUtil.hud_back_13,
			ResourcesUtil.hud_back_14, };
	private static int[] hud_driveWayForegroundId = {
			ResourcesUtil.hud_front_0,
			ResourcesUtil.hud_front_1,
			ResourcesUtil.hud_back_2,
			ResourcesUtil.hud_front_2,
			ResourcesUtil.hud_back_4,
			ResourcesUtil.hud_front_3,
			ResourcesUtil.hud_back_6,
			ResourcesUtil.hud_back_7,
			ResourcesUtil.hud_front_4,
			ResourcesUtil.hud_back_9,
			ResourcesUtil.hud_back_10,
			ResourcesUtil.hud_back_11,
			ResourcesUtil.hud_back_12,
			ResourcesUtil.hud_front_22,
			ResourcesUtil.hud_back_14};


	private static int[] driveWay={
			ResourcesUtil.front_16,
			ResourcesUtil.front_17,
			ResourcesUtil.front_14,
			ResourcesUtil.front_15,
			ResourcesUtil.front_5,
			ResourcesUtil.front_6,
			ResourcesUtil.front_7,
			ResourcesUtil.front_8,
			ResourcesUtil.front_9,
			ResourcesUtil.front_10,
			ResourcesUtil.front_11,
			ResourcesUtil.front_12,
			ResourcesUtil.front_13,
			ResourcesUtil.front_19,
			ResourcesUtil.front_18,
			ResourcesUtil.front_21,
			ResourcesUtil.front_20,
			ResourcesUtil.front_23,
			ResourcesUtil.front_24,
			ResourcesUtil.front_25

	};
	private static int[] hud_driveWay={
			ResourcesUtil.hud_front_16,
			ResourcesUtil.hud_front_17,
			ResourcesUtil.hud_front_14,
			ResourcesUtil.hud_front_15,
			ResourcesUtil.hud_front_5,
			ResourcesUtil.hud_front_6,
			ResourcesUtil.hud_front_7,
			ResourcesUtil.hud_front_8,
			ResourcesUtil.hud_front_9,
			ResourcesUtil.hud_front_10,
			ResourcesUtil.hud_front_11,
			ResourcesUtil.hud_front_12,
			ResourcesUtil.hud_front_13,
			ResourcesUtil.hud_front_19,
			ResourcesUtil.hud_front_18,
			ResourcesUtil.hud_front_21,
			ResourcesUtil.hud_front_20,
			ResourcesUtil.hud_front_23,
			ResourcesUtil.hud_front_24
	};

	private NaviView mNaviView = null;
	private Bitmap[] driveWayBitMaps = null;
	private Bitmap[] driveWayBitMapBgs = null;
	private int height;
	private int width;
	private boolean isHudMode;
	private int[] driveWay_1 = driveWayForegroundId;
	private int[] driveWay_2 = driveWayBackgroundId;
	private int[] driveWay_3 = driveWay;
	private Bitmap line=BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.drive_way_line);
	private Drawable backGroud_img=ResourcesUtil.getResources().getDrawable(ResourcesUtil.default_navi_land_bg_normal);
	private int i;


	/**
	 * 构造函数。
	 * @param context 上下文。
	 */
	public DriveWayView(Context context) {
		super(context);
	}

	public DriveWayView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public DriveWayView(Context paramContext, AttributeSet paramAttributeSet,
						int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	/**
	 * 返回单车道控件宽度。
	 * @return 单车道控件宽度。
	 */
	public int getDriveWayWidth() {
		return this.driveWayWidth;
	}

	/**
	 * 获得车道数量。
	 * @return 当前车道数量。
	 */
	public int getDriveWaySize() {
		return this.driveWaySize;
	}

	/**
	 * 返回单车道控件高度。
	 * @return 单车道控件高度。
	 */
	public int getDriveWayBgHeight() {
		return this.driveWayHeight;
	}

	/**
	 * 设置导航视图类。
	 * @param NaviView 导航视图类。
	 */
	public void setMapNaviView(NaviView NaviView) {
		this.mNaviView = NaviView;
	}

	/**
	 * 加载车道背景和推荐车道线数据，自动产生对应的车道信息位图。
	 * @param laneBackgroundInfo - 背景数据数组。
	 * @param laneRecommendedInfo - 推荐数据数组。
	 */
	public void loadDriveWayBitmap(byte[] laneBackgroundInfo,byte[] laneRecommendedInfo) {
		this.driveWaySize = parseDriveWaySize(laneBackgroundInfo);
		if (this.driveWaySize == 0)
			return;

		this.driveWayBitMapBgs = new Bitmap[this.driveWaySize];
		this.driveWayBitMaps = new Bitmap[this.driveWaySize];

		int i;
		for (i = 0; i < this.driveWaySize; ++i) {
			this.driveWayBitMapBgs[i] = BitmapFactory.decodeResource(ResourcesUtil.getResources(),	this.driveWay_2[laneBackgroundInfo[i]]);
			if (isComplexLane(laneBackgroundInfo[i])) {
				this.driveWayBitMaps[i] = complexBitmap(laneBackgroundInfo[i], laneRecommendedInfo[i]);
			}
			else if (isThisLaneRecommended(laneRecommendedInfo[i])) {
				this.driveWayBitMaps[i] = BitmapFactory.decodeResource(ResourcesUtil.getResources(),this.driveWay_1[laneRecommendedInfo[i]]);//从数据取图片
			}
			else {
				this.driveWayBitMaps[i] = this.driveWayBitMapBgs[i];
			}

		}

		if (this.driveWayBitMapBgs[(i - 1)] != null) {
			this.driveWayWidth = this.driveWayBitMapBgs[(i - 1)].getWidth();
			this.driveWayHeight = this.driveWayBitMapBgs[(i - 1)].getHeight();
		}

		this.height = this.driveWayHeight;
		this.width = (this.driveWayWidth * this.driveWaySize)+line.getWidth()*(this.driveWaySize-1);

		setImageBitmap(produceFinalBitmap());
		setPadding(10,10,10,10);
		setBackgroundDrawable(backGroud_img);
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
								 int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public void setHudMode(boolean istrue){
		isHudMode = istrue;
		if(istrue)     {
			driveWay_1 = hud_driveWayForegroundId;
			driveWay_2 = hud_driveWayBackgroundId;
			driveWay_3 = hud_driveWay;
			backGroud_img=ResourcesUtil.getResources().getDrawable(ResourcesUtil.default_navi_land_bg_hud);
			line=BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.hud_driveway_line);

		}else{
			driveWay_1 = driveWayForegroundId;
			driveWay_2 = driveWayBackgroundId;
			driveWay_3 = driveWay;
			backGroud_img = ResourcesUtil.getResources().getDrawable(ResourcesUtil.default_navi_land_bg_normal);
			line=BitmapFactory.decodeResource(ResourcesUtil.getResources(),ResourcesUtil.drive_way_line);
		}

	}
	private int parseDriveWaySize(byte[] paramArrayOfByte) {
		int i = paramArrayOfByte.length;
		int j = 0;
		for (int k = 0; k < i; ++k)
			if (paramArrayOfByte[k] == 15) {
				j = k;
				break;
			}

		return j;
	}

	private boolean isThisLaneRecommended(byte paramByte) {
		return (paramByte != 15);
	}

	private boolean isComplexLane(int paramInt) {
		LogUtil.d(TAG,"paramInt"+paramInt);
//		return ((paramInt == 2) || (paramInt == 4)	|| (paramInt == 9) || (paramInt == 10) || (paramInt == 11)	|| (paramInt == 12) || (paramInt == 6) || (paramInt == 7));
		return ((paramInt == 14) || (paramInt == 2) || (paramInt == 4)	|| (paramInt == 9) || (paramInt == 10) || (paramInt == 11)	|| (paramInt == 12) || (paramInt == 6) || (paramInt == 7));
	}
	/*
    生成车道图片
     */
	private Bitmap complexBitmap(int paramInt1, int paramInt2) {
		Bitmap localBitmap = null;

		if (paramInt1 == 10) {
			if (paramInt2 == 0) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[0]);//
			} else if (paramInt2 == 8)
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[1]);//

		} else if (paramInt1 == 9) {
			if (paramInt2 == 0) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[2]);//
			} else if (paramInt2 == 5)
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[3]);//

		} else if (paramInt1 == 2) {
			if (paramInt2 == 0) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[4]);
			} else if (paramInt2 == 1)
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[5]);//

		} else if (paramInt1 == 4) {
			if (paramInt2 == 0) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[6]);//
			} else if (paramInt2 == 3)
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[7]);//

		} else if (paramInt1 == 6) {
			if (paramInt2 == 1) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[8]);//
			} else if (paramInt2 == 3) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[9]);//
			}

		} else if (paramInt1 == 7) {
			if (paramInt2 == 0) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[10]);//
			} else if (paramInt2 == 1) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[11]);//
			} else if (paramInt2 == 3) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[12]);//
			}

		} else if (paramInt1 == 11) {
			if (paramInt2 == 5) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[13]);//
			} else if (paramInt2 == 1) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[14]);////
			}

		} else if (paramInt1 == 12) {
			if (paramInt2 == 8) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[15]);//
			} else if (paramInt2 == 3) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[16]);//
			}

		} else if (paramInt1 == 14) {
			if (paramInt2 == 14) {
				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[19]);//
			} else if (paramInt2 == 15) {
//				localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(), driveWay_3[18]);//
			}

		}

		if (localBitmap == null) {
			localBitmap = BitmapFactory.decodeResource(ResourcesUtil.getResources(),this.driveWay_2[paramInt1]);
		}

		return localBitmap;
	}

	/**
	 * 设置车道视图距离顶端的距离。
	 * @param margin - 距离。
	 */
	public void setDefaultTopMargin(int margin) {
		if (this.mNaviView == null)
			return;

		int i = 0;
		if (this.mNaviView.isOrientationLandscape()) {
			i = margin + this.COMMON_VALUE;
		}
		else {
			i = margin + this.COMMON_VALUE;
		}

		RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
		localLayoutParams.setMargins(0, i, 0, 0);
		setLayoutParams(localLayoutParams);
	}

	/**
	 * 资源回收。
	 */
	public void recycleResource() {
		int i = 0;
		while (i < this.driveWaySize) {
			if (this.driveWayBitMaps[i] != null) {
				this.driveWayBitMaps[i].recycle();
				this.driveWayBitMaps[i] = null;
			}

			if (this.driveWayBitMapBgs[i] != null) {
				this.driveWayBitMapBgs[i].recycle();
				this.driveWayBitMapBgs[i] = null;
			}

			++i;
		}

		this.driveWaySize = 0;
	}

	Bitmap produceFinalBitmap() {
		Bitmap localBitmap = Bitmap.createBitmap(this.width, this.height,Bitmap.Config.ARGB_8888);

		Canvas localCanvas = new Canvas(localBitmap);
		int i = 0;

			while (i < this.driveWaySize) {
				if (i > 0) {
					localCanvas.drawBitmap(line, 0 + i * this.driveWayWidth + (i - 1) * line.getWidth(), 2.0F, null);
				}
				if (this.driveWayBitMaps[i] != null) {
					localCanvas.drawBitmap(this.driveWayBitMaps[i], 0 + i * this.driveWayWidth + i * line.getWidth(), 0.0F, null);
				}
				++i;
			}

		if(i==0){this.setVisibility(View.INVISIBLE);}
		return localBitmap;
	}
}