package com.sfmap.api.navi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sfmap.api.navi.model.NaviLaneInfo;
import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.api.navi.model.NaviLocation;
import com.sfmap.api.navi.model.NaviTrafficFacilityInfo;
import com.sfmap.api.navi.model.NaviServiceFacilityInfo;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.view.DriveWayView;
import com.sfmap.api.navi.view.HudMirrorImage;
import com.sfmap.api.navi.view.NaviRoadEnlargeView;
import com.sfmap.tbt.ResourcesUtil;
import com.sfmap.tbt.NaviUtilDecode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * HUD（Head Up Display）界面，方便用户夜间行车，避免长时间将注意力放于手机屏幕上。仅支持设备全屏显示。
 * 当用户点击HUD界面，会出现HUD设置栏（包括：返回按钮和镜像按钮）。
 */
public class HudView extends FrameLayout implements View.OnClickListener,
		View.OnTouchListener, NaviListener {

	private static final int[] hud_imgActions = {
			ResourcesUtil.back_13,
			ResourcesUtil.hud_action_1,
			ResourcesUtil.hud_action_1,
			ResourcesUtil.hud_action_2,
			ResourcesUtil.hud_action_3,
			ResourcesUtil.hud_action_4,
			ResourcesUtil.hud_action_5,
			ResourcesUtil.hud_action_6,
			ResourcesUtil.hud_action_7,
			ResourcesUtil.hud_action_8,
			ResourcesUtil.hud_action_9,
			ResourcesUtil.hud_action_10,
			ResourcesUtil.hud_action_11,
			ResourcesUtil.hud_action_12,
			ResourcesUtil.hud_action_13,
			ResourcesUtil.hud_action_14,
			ResourcesUtil.hud_action_15,
			ResourcesUtil.hud_action_16,
			ResourcesUtil.hud_action_17,
			ResourcesUtil.hud_action_18,
			ResourcesUtil.hud_action_19,
			ResourcesUtil.hud_action_20};
	public static final int[] ID = {
			ResourcesUtil.back_13,
			ResourcesUtil.action_1,
			ResourcesUtil.action_1,
			ResourcesUtil.action_2,
			ResourcesUtil.action_3,
			ResourcesUtil.action_4,
			ResourcesUtil.action_5,
			ResourcesUtil.action_6,
			ResourcesUtil.action_7,
			ResourcesUtil.action_8,
			ResourcesUtil.action_9,
			ResourcesUtil.action_10,
			ResourcesUtil.action_11,
			ResourcesUtil.action_12,
			ResourcesUtil.action_13,
			ResourcesUtil.action_14,
			ResourcesUtil.action_15,
			ResourcesUtil.action_16,
			ResourcesUtil.action_17,
			ResourcesUtil.action_18,
			ResourcesUtil.action_19,
			ResourcesUtil.action_20};
	public static final int[] camera_type={
			ResourcesUtil.peccancy_line,
			ResourcesUtil.camera,
			ResourcesUtil.traffic_light,
			ResourcesUtil.peccancy_line,
			ResourcesUtil.bus_line,
			ResourcesUtil.contingency_line
	};

	private HudViewListener mMapHudVewListener;
	private boolean isLandscape = false;
	private Context mContext;
	private View frameLayout;
	private Navi mNavi;
	private int mWidth = 480;
	private int mHeight = 800;
	private int hudMode = 1;
	private boolean isHudMenu = true;
	private Handler disappearHudHandler = new Handler();
	private android.widget.TextView restDistanceTime;
	private String distanceTimeTextStr;
	private android.widget.TextView limitSpeedTextView;
	private CheckBox mMirrorModeCheckBox;
	private HudMirrorImage logoHudMirrorImage;
	private android.widget.TextView nextRoadNameText;
	private android.widget.TextView restDistanceText;
	private ImageView roadsignimg;
	private android.widget.TextView nextRoadDistanceText;
	private android.widget.TextView eye_speed_dis_text;
	private android.widget.TextView DistanceUnitText;
	private android.widget.TextView nextActionText;
	private android.widget.TextView left_speed_text;
	private ImageView left_eye_camera_img;
	private android.widget.TextView left_eye_dis_text;
	private android.widget.TextView right_eye_dis_text;
	private ImageView title_btn_goback;
	private View mHudMirrorTitle;
	private View left_eye_camera_layout;
	private View top_layout;
	int curNaviMode = Navi.EmulatorNaviMode; //当前导航模式，是模拟导航还是真实导航
	private View crossImage_layout;
	private View bottom_info_layout;
	private View mid_info_layout;
	private View waitGpsHUDLy;
	private View eys_img;
	private View camera_layout;
	private LinearLayout bottom_dis_time_layout;
	private NaviRoadEnlargeView enlarge_road_layout;
	private ProgressBar cross_progress;
	private boolean isflash;
	private DriveWayView driveWayView;
	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();
	private Runnable disappearHudTitleRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				loadHideHudTitleAnimation();
			}
			catch(Throwable v0) {
				v0.printStackTrace();
			}
		}
	};

	private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton paramCompoundButton,
									 boolean paramBoolean) {
			try {
				if(logoHudMirrorImage == null) {
					return;
				}

				hudMode = paramBoolean ? 2 : 1;
				setCheckBoxAndMirrorImageState(paramBoolean);
				removeCallbacks();
				disappearHudHandler.postDelayed(disappearHudTitleRunnable, 2000);
			}
			catch(Throwable v0) {
				v0.printStackTrace();
			}
		}
	};
	private String nextRoadNameTextStr;
	private String restDistanceTextStr;
	private SpannableString nextRoadDisTextSpannableStr = null;
	private int resId;

	private Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==1){
				roadsignimg.setVisibility(roadsignimg.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
				if(isflash)handler.sendEmptyMessageDelayed(1,500);
			}
			super.handleMessage(msg);
		}
	};
	private String distanceUnitTextStr;
	private String disUnitTextStr;
	private String nextActionTextStr;
	private boolean isShowCross;
	private int lastID=50;
	private NaviCross crossImg;
	private boolean isShowEyeCamera;
	private NaviInfo naviInfoTemp;
	private PowerManager.WakeLock wakeLock;

	public HudView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		try {
			init(context);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	public HudView(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			init(context);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	public HudView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 返回HUD显示模式。目前有正常、镜像两种显示模式。
	 * @return 返回1表示正向模式，2表示镜像模式。
	 */
	public int getHudViewMode() {
		return this.hudMode;
	}

	/**
	 * 设置HUD显示模式，目前仅支持设备全屏显示。
	 * @param mode - HUD显示模式. 包括：{@link NaviViewOptions} 中的 HUD_NORMAL_SHOW (HUD正向显示）和  {@link NaviViewOptions} 中的 HUD_MIRROR_SHOW（HUD镜像显示）。
	 */
	public void setHudViewMode(int mode) {
		try {
			this.hudMode = mode;
			setCheckBoxAndMirrorImageState(this.hudMode == 2);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 返回 HUD设置栏是否显示。
	 * @return 返回true，显示；false，不显示。
	 */
	public boolean getHudMenuEnabled() {
		return this.isHudMenu;
	}

	/**
	 * HUD界面是否显示设置栏。
	 * 设置栏包含返回按钮和镜像按钮。用户点击HUD界面，显示设置栏，5秒未有操作，自动隐藏。
	 * @param enabled - 设置为 true，当用户点击屏幕时，会显示HUD设置栏，5秒未有操作，进行隐藏；设置为false，将不会显示用户的状态栏。
	 */
	public void setHudMenuEnabled(Boolean enabled) {
		this.isHudMenu = enabled.booleanValue();
	}

	/**
	 * 创建事件响应
	 * @param bundle bundle信息
	 */
	public final void onCreate(Bundle bundle) {
	}

	/**
	 * 界面恢复事件处理
	 */
	public final void onResume() {
		PowerManager pm= (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"HUD");
		if(true){
			wakeLock.acquire();}

	}

	/**
	 * 界面暂停事件处理
	 */
	public final void onPause() {
		if(wakeLock!=null)
			wakeLock.release();
	}

	/**
	 * onDestroy 销毁该HudView
	 */
	public final void onDestroy() {
		try {
			if (this.logoHudMirrorImage != null)
				this.logoHudMirrorImage.recycleMirrorBitmap();
			if(this.listener!=null){
//				this.mNavi.setGpsStatusListener(null);
				this.mNavi.removeGpsStatusListener(listener);
				this.listener=null;
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 保存信息
	 * @param bundle bundle 对象
	 */
	public final void onSaveInstanceState(Bundle bundle) {
	}

	/**
	 * 设置HudViewListener监听
	 * @param listener - HudViewListener监听
	 */
	public void setHudViewListener(HudViewListener listener) {
		this.mMapHudVewListener = listener;
	}

	private void init(Context paramContext) {
		try {
			this.mContext = paramContext;
			this.mNavi = Navi.getInstance(paramContext);
			this.mNavi.setGpsStatusListener(listener);
			this.isLandscape = isLandscape();
			if (this.isLandscape) {
				this.frameLayout = ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_hudlayout_land,	null);
			} else {
				this.frameLayout = ResourcesUtil.getViewByID( (Activity)getContext(), ResourcesUtil.sdk_hudlayout,	null);
			}

			addView(this.frameLayout);
			initResolution();
			initWidget();
			this.mNavi.addNaviListener(this);
			onNaviInfoUpdate(this.mNavi.getNaviInfo());
			//默认为镜像模式
			hudMode=2;
			setCheckBoxAndMirrorImageState(true);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	private void initResolution() {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		WindowManager localWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		this.mWidth = localDisplayMetrics.widthPixels;
		this.mHeight = localDisplayMetrics.heightPixels;
	}

	/**
	 * 界面触摸事件处理，不需要应用层处理、调用
	 * @param motionEvent 触摸事件
	 * @return 处理是否成功
	 */
	public boolean onTouchHudMirrorEvent(MotionEvent motionEvent) {
		try {
			if (!(this.isHudMenu))
				return true;

			loadShowHudTitleAnimation();
			removeCallbacks();
			if(!isShowCross)
				this.disappearHudHandler.postDelayed(this.disappearHudTitleRunnable, 2000L);
			else
				hideCross();
			return true;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return true;
	}

	private void loadShowHudTitleAnimation() {
		if ((this.mHudMirrorTitle != null)&& (this.mHudMirrorTitle.getVisibility() == View.GONE)) {
			Animation localAnimation = ResourcesUtil.getAnimationByXML(this.mContext, ResourcesUtil.animation_bottom_in);//进入动画

			this.mHudMirrorTitle.setVisibility(View.VISIBLE);
			this.mHudMirrorTitle.startAnimation(localAnimation);
		}
	}

	private void initWidget() {

		this.top_layout = this.frameLayout.findViewById(ResourcesUtil.top_layout);
		this.crossImage_layout = this.frameLayout.findViewById(ResourcesUtil.crossImage_layout);
		this.bottom_info_layout = this.frameLayout.findViewById(ResourcesUtil.bottom_info_layout);
		this.mid_info_layout = this.frameLayout.findViewById(ResourcesUtil.mid_info_layout);
		this.waitGpsHUDLy = this.frameLayout.findViewById(ResourcesUtil.waitGpsHUDLy);
		this.eys_img = this.frameLayout.findViewById(ResourcesUtil.eys_img);
		this.camera_layout = this.frameLayout.findViewById(ResourcesUtil.camera_layout);
		this.bottom_dis_time_layout = (LinearLayout) this.frameLayout.findViewById (ResourcesUtil.bottom_dis_time_layout);
		this.enlarge_road_layout = ((NaviRoadEnlargeView) this.frameLayout.findViewById(ResourcesUtil.hud_enlarge_road_layout));//放大图
		this.cross_progress = ((ProgressBar) this.frameLayout.findViewById(ResourcesUtil.hud_cross_progress));//进度条
		this.driveWayView = ((DriveWayView) this.frameLayout.findViewById(ResourcesUtil.hud_driveWayViewInNaviView));//车道
		this.driveWayView.setHudMode(true);
		this.eye_speed_dis_text = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.hud_eye_speed_dis_text));
		this.DistanceUnitText = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.hud_DistanceUnitText));
		this.nextActionText = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.hud_nextActionText));

		this.left_speed_text = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.left_speed_text));
		this.left_eye_camera_img = (ImageView) this.frameLayout.findViewById(ResourcesUtil.left_eye_camera_img);
		this.left_eye_dis_text = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.left_eye_dis_text));
		this.right_eye_dis_text = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.right_eye_dis_text));
		this.left_eye_camera_layout = this.frameLayout.findViewById(ResourcesUtil.left_eye_camera_layout);



		this.mHudMirrorTitle = this.frameLayout.findViewById(ResourcesUtil.hudmirrortitle);//2131427344
		this.logoHudMirrorImage = ((HudMirrorImage) this.frameLayout.findViewById(ResourcesUtil.hudMirrosImage));//2131427337

		this.mMirrorModeCheckBox = ((CheckBox) this.frameLayout	.findViewById(ResourcesUtil.save));//2131427336
		this.nextRoadNameText = ((android.widget.TextView) this.frameLayout	.findViewById(ResourcesUtil.nextRoadNameText));//2131427338

		this.restDistanceText = ((android.widget.TextView) this.frameLayout	.findViewById(ResourcesUtil.restDistanceText));//2131427341

		this.roadsignimg = ((ImageView) this.frameLayout.findViewById(ResourcesUtil.roadsignimg));//2131427339
		this.nextRoadDistanceText = ((android.widget.TextView) this.frameLayout	.findViewById(ResourcesUtil.nextRoadDistanceText));//2131427340

		this.title_btn_goback = (ImageView) this.frameLayout.findViewById(ResourcesUtil.title_btn_goback);//2131427334
		this.restDistanceTime = ((android.widget.TextView) this.frameLayout	.findViewById(ResourcesUtil.restDistanceTime));//2131427342

		this.limitSpeedTextView = ((android.widget.TextView) this.frameLayout.findViewById(ResourcesUtil.limitSpeedTextView));//2131427343
		getScreenInfo();
		setWidgetListener();
		updateHudWidgetContent();
	}

	private void getScreenInfo() {
		if (this.logoHudMirrorImage == null)
			return;
		this.logoHudMirrorImage.mWidth = this.mWidth;
		this.logoHudMirrorImage.mHeight = (this.mHeight - 50);
	}

	private void updateHudWidgetContent() {
		if (this.nextRoadNameText != null)
			this.nextRoadNameText.setText(this.nextRoadNameTextStr);

		if (this.nextRoadDistanceText != null)
			this.nextRoadDistanceText.setText(this.nextRoadDisTextSpannableStr);

		if (this.restDistanceText != null) {
			this.restDistanceText.setText(this.restDistanceTextStr);
		}

		if (this.restDistanceTime != null) {
			this.restDistanceTime.setText(this.distanceTimeTextStr);
		}
		if(this.DistanceUnitText!=null){
			this.DistanceUnitText.setText(disUnitTextStr);
		}
		if(this.nextActionText!=null){
			this.nextActionText.setText(nextActionTextStr);
		}

	}

	private void setWidgetListener() {
		if (this.logoHudMirrorImage != null) {
			this.logoHudMirrorImage.setMapHudView(this);
			setOnTouchListener(this);
		}

		if (this.mMirrorModeCheckBox != null) {
			this.mMirrorModeCheckBox.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
		}

		if (this.title_btn_goback != null)
			this.title_btn_goback.setOnClickListener(this);
	}

	private void removeCallbacks() {
		if ((this.disappearHudHandler != null) && (this.disappearHudTitleRunnable != null))
			this.disappearHudHandler.removeCallbacks(this.disappearHudTitleRunnable);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setCheckBoxAndMirrorImageState(boolean paramBoolean) {
		if (this.mMirrorModeCheckBox != null) {
			this.mMirrorModeCheckBox.setChecked(paramBoolean);
		}

		if (this.logoHudMirrorImage != null) {
			if(paramBoolean){
				AnimatorSet animatorSet = new AnimatorSet();//组合动画
				ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoHudMirrorImage, "scaleX", -1f, 1f);
				ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoHudMirrorImage, "scaleY", 1f, -1f);
				animatorSet.setDuration(100);
				animatorSet.setInterpolator(new DecelerateInterpolator());
				animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
				animatorSet.start();
			}else{
				AnimatorSet animatorSet = new AnimatorSet();//组合动画
				ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoHudMirrorImage, "scaleX", -1f, 1f);
				ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoHudMirrorImage, "scaleY", -1f, 1f);
				animatorSet.setDuration(100);
				animatorSet.setInterpolator(new DecelerateInterpolator());
				animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
				animatorSet.start();
			}
		}



	}

	private void loadHideHudTitleAnimation() {
		//
		if ((this.mHudMirrorTitle != null)	&& (this.mHudMirrorTitle.getVisibility() == View.VISIBLE)) {
			Animation localAnimation = ResourcesUtil.getAnimationByXML(this.mContext, ResourcesUtil.animation_bottom_out);//2130968577,隐藏动画

			localAnimation.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation paramAnimation) {
				}

				public void onAnimationRepeat(Animation paramAnimation) {
				}

				public void onAnimationEnd(Animation paramAnimation) {
					HudView.this.mHudMirrorTitle.setVisibility(View.GONE);
				}

			});
			this.mHudMirrorTitle.startAnimation(localAnimation);
		}
	}

	private boolean isHudMirror() {
		return (this.hudMode == 2);
	}

	protected void onConfigurationChanged(Configuration paramConfiguration) {
		try {
			if (this.logoHudMirrorImage != null) {
				this.logoHudMirrorImage.recycleMirrorBitmap();
				this.logoHudMirrorImage = null;
			}
			removeAllViews();

			if (isLandscape()) {
				this.frameLayout = ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_hudlayout_land,	null);
			} else {
				this.frameLayout = ResourcesUtil.getViewByID((Activity) getContext(), ResourcesUtil.sdk_hudlayout, null);
			}
			addView(this.frameLayout);
			initResolution();
			initWidget();
			getScreenInfo();
			isflash=false;
			handler.removeMessages(1);
			onNaviInfoUpdate(naviInfoTemp);
			setCheckBoxAndMirrorImageState(isHudMirror());
			if(isShowCross)showCross(crossImg);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		super.onConfigurationChanged(paramConfiguration);
	}

	private void updateHudUI(NaviInfo paramNaviInfo) {
		try {
			if (paramNaviInfo == null)
				return;
			this.nextRoadNameTextStr = paramNaviInfo.m_NextRoadName;
			this.distanceUnitTextStr = paramNaviInfo.getPathRetainDistance()>1000?"公里":"米";
			this.disUnitTextStr = paramNaviInfo.m_SegRemainDis>1000?"公里后":"米后";
			this.restDistanceTextStr= NaviUtilDecode.a(paramNaviInfo.getPathRetainDistance())+distanceUnitTextStr;
			this.nextRoadDisTextSpannableStr = switchStrFromMeter(paramNaviInfo.m_SegRemainDis);
			this.nextActionTextStr = "目的地".equals(paramNaviInfo.getNextRoadName())?" 到达":" 进入";
			if(paramNaviInfo.getPathRetainDistance()==0){
				distanceUnitTextStr="现在";
				nextRoadDisTextSpannableStr=new SpannableString("");
			}
			String localObject2= NaviUtilDecode.getTimeFormatString(paramNaviInfo.m_RouteRemainTime);
			this.distanceTimeTextStr=Html.fromHtml(NaviUtilDecode.a(localObject2, "ffffff", "ffffff")).toString();
			updateHudWidgetContent();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}
	private void changeLandEyelayout(boolean istrue){
		if(istrue){
			RelativeLayout.LayoutParams eye_params= (RelativeLayout.LayoutParams) left_eye_camera_layout.getLayoutParams();
			eye_params.setMargins(dp2px(10),dp2px(100),0,0);
			left_eye_camera_layout.setLayoutParams(eye_params);
			RelativeLayout.LayoutParams speed_params= (RelativeLayout.LayoutParams) camera_layout.getLayoutParams();
			speed_params.setMargins(0,dp2px(100),dp2px(10),0);
			camera_layout.setLayoutParams(speed_params);

		}else {
			RelativeLayout.LayoutParams eye_params = (RelativeLayout.LayoutParams) left_eye_camera_layout.getLayoutParams();
			eye_params.setMargins(dp2px(30), dp2px(20), 0, 0);
			left_eye_camera_layout.setLayoutParams(eye_params);
			RelativeLayout.LayoutParams speed_params = (RelativeLayout.LayoutParams) camera_layout.getLayoutParams();
			speed_params.setMargins(0, dp2px(20), dp2px(30), 0);
			camera_layout.setLayoutParams(speed_params);
		}
	}
	private SpannableString switchStrFromMeter(int paramInt) {
		if (paramInt >= 1000) {
			float f = paramInt / 1000.0F;
			f = Math.round(f * 10.0F) / 10.0F;
			return getSpanableString(this.mContext, f + "","");
		}
		return getSpanableString(this.mContext, paramInt + "","");
	}

	private SpannableString getSpanableString(Context paramContext,
											  String paramString1, String paramString2) {
		SpannableString localSpannableString = new SpannableString(paramString1	+ paramString2);

		int i = NaviUtilDecode.a(paramContext, 40);
		int j = NaviUtilDecode.a(paramContext, 30);

		int k = 33;
		int l = 0;
		int i1 = paramString1.length();

		localSpannableString.setSpan(new AbsoluteSizeSpan(i), l, i1, k);
		localSpannableString.setSpan(new ForegroundColorSpan(-1), l, i1, k);

		l = i1;
		i1 += paramString2.length();
		localSpannableString.setSpan(new AbsoluteSizeSpan(j), l, i1, k);
		localSpannableString.setSpan(new ForegroundColorSpan(-1), l, i1, k);

		return localSpannableString;
	}

	/**
	 * 点击事件处理， 不需要应用层调用
	 * @param view 视图对象
	 */
	public void onClick(View view) {
		try {
			if ((this.title_btn_goback == view)	&& (this.mMapHudVewListener != null))
				this.mMapHudVewListener.onHudViewCancel();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	/**
	 * 触摸事件处理，不需要应用层调用
	 * @param view view对象
	 * @param motionEvent 信息内容
	 * @return 事件处理结果
	 */
	public boolean onTouch(View view, MotionEvent motionEvent) {
		try {
			return onTouchHudMirrorEvent(motionEvent);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return false;
	}

	public void onInitNaviFailure() {
	}

	public void onInitNaviSuccess() {
	}

	public void onStartNavi(int paramInt) {
		this.curNaviMode = paramInt;
	}

	@Override
	public void onTrafficStatusUpdate() {

	}

	public void onLocationChange(NaviLocation naviLocation) {
	}

	public void onGetNavigationText(int paramInt, String paramString) {
	}

	public void onEndEmulatorNavi() {
	}

	public void onArriveDestination() {
	}

	public void onCalculateRouteSuccess() {
	}

	public void onCalculateRouteFailure(int paramInt) {
	}

	public void onReCalculateRouteForYaw() {
	}

	@Override
	public void onReCalculateRouteForTrafficJam() {

	}


	public void onArrivedWayPoint(int wayID) {
	}

	public void onGpsOpenStatus(boolean enabled) {
	}

	public void onNaviInfoUpdate(NaviInfo naviInfo) {
		try {

			updateHudUI(naviInfo);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		//更新放大图进度条
		if(naviInfo!=null&&naviInfo.getCurStepRetainDistance()<=300&&isShowCross){
			int index=(300-naviInfo.getCurStepRetainDistance())*100/300;
			this.cross_progress.setProgress(index);
		}

		updataActionImg(naviInfo);
		naviInfoTemp = naviInfo;
	}


	private void updataActionImg(NaviInfo naviInfo){
		if(naviInfo==null)return;
//		if(naviInfo.m_Icon == 0){ //转向图标异常情况传0过来，隐藏之前显示的图标，跳过此次图标显示
//			this.roadsignimg.setVisibility(View.GONE);
//			return;
//		}
		this.resId = naviInfo.m_Icon;
		Drawable localDrawable = ResourcesUtil.getResources().getDrawable(hud_imgActions[this.resId]);
		Drawable light_img = ResourcesUtil.getResources().getDrawable(ID[this.resId]);

		if(naviInfo!=null&&naviInfo.getPathRetainDistance()<=1||naviInfo!=null&&naviInfo.getCurStepRetainDistance()>50)
		{
			isflash=false;
			handler.removeMessages(1);
			this.roadsignimg.setBackgroundDrawable((Drawable) localDrawable);
			if(this.roadsignimg.getVisibility()!=View.VISIBLE)
				this.roadsignimg.setVisibility(View.VISIBLE);
		}else{
			if(!isflash){
				isflash=true;
				this.roadsignimg.setBackgroundDrawable((Drawable) light_img);
				handler.sendEmptyMessageDelayed(1,500);
			}
			if(lastID!=resId&&lastID!=50){
				isflash=true;
				handler.removeMessages(1);
				this.roadsignimg.setBackgroundDrawable((Drawable) light_img);
				handler.sendEmptyMessageDelayed(1,500);
			}
		}
		lastID = resId;
	}
	public void onUpdateTrafficFacility(NaviTrafficFacilityInfo[] trafficFacilityInfo) {
		updataCameraUI(trafficFacilityInfo);
	}

	void updataCameraUI(NaviTrafficFacilityInfo[] trafficFacilityInfo){
		//更新电子眼
		if(trafficFacilityInfo.length<2){//1个电子眼
			this.left_speed_text.setVisibility(View.GONE);
			this.left_eye_camera_img.setVisibility(View.GONE);
			this.left_eye_dis_text.setVisibility(View.GONE);
			this.right_eye_dis_text.setVisibility(View.GONE);
			int type1=trafficFacilityInfo[0].getBroardcastType();
			if((type1 >= 1) && (type1 <= 5)){
				//违章
				isShowEyeCamera = true;
				this.limitSpeedTextView.setText("");
				this.limitSpeedTextView.setVisibility(View.VISIBLE);
				this.eye_speed_dis_text.setVisibility(View.VISIBLE);
				this.eye_speed_dis_text.setText(trafficFacilityInfo[0].getDistance()+"米");
				Drawable drawable = ResourcesUtil.getResources().getDrawable(camera_type[type1]);
				this.eys_img.setBackgroundDrawable(drawable);
				this.eys_img.setVisibility(View.VISIBLE);
				if(isLandscape()&&isShowCross){
					changeLandEyelayout(true);
				}
			}else if ((type1 == 0)) {
				//限速
				this.limitSpeedTextView.setVisibility(View.VISIBLE);
				this.eye_speed_dis_text.setVisibility(View.VISIBLE);
				if(trafficFacilityInfo[0].getLimitSpeed() > 0)
				this.limitSpeedTextView.setText(""	+ trafficFacilityInfo[0].getLimitSpeed());
				else
					this.limitSpeedTextView.setText("限速");
				this.eye_speed_dis_text.setText(trafficFacilityInfo[0].getDistance()+"米");
				eys_img.setVisibility(View.INVISIBLE);
				if(isLandscape()&&isShowCross){
					changeLandEyelayout(true);
				}
				isShowEyeCamera=true;
			} else  {

				this.limitSpeedTextView.setVisibility(View.INVISIBLE);
				this.eye_speed_dis_text.setVisibility(View.INVISIBLE);
				this.eys_img.setVisibility(View.INVISIBLE);
				isShowEyeCamera=false;
				if(isLandscape()){
					changeLandEyelayout(false);
				}
			}
		}
		else if(trafficFacilityInfo.length==2){//多个电子眼
			eye_speed_dis_text.setVisibility(View.GONE);
			int type1=trafficFacilityInfo[0].getBroardcastType();
			if((type1 >= 1) && (type1<= 5)){
				isShowEyeCamera = true;
				//违章
				this.left_speed_text.setText("");
				this.left_speed_text.setVisibility(View.VISIBLE);
				Drawable drawable = ResourcesUtil.getResources().getDrawable(camera_type[type1]);
				this.left_eye_camera_img.setBackgroundDrawable(drawable);
				this.left_eye_camera_img.setVisibility(View.VISIBLE);
				this.left_eye_dis_text.setVisibility(View.VISIBLE);
				this.left_eye_dis_text.setText("距离"+trafficFacilityInfo[0].getDistance()+"米");
				if(isLandscape()&&isShowCross){
					changeLandEyelayout(true);
				}
			}else if (type1 == 0) {
				//限速
				this.left_speed_text.setVisibility(View.VISIBLE);
				this.left_eye_dis_text.setVisibility(View.VISIBLE);
				if(trafficFacilityInfo[0].getLimitSpeed() > 0)
				this.left_speed_text.setText(""	+ trafficFacilityInfo[0].getLimitSpeed());
				else
					this.left_speed_text.setText("限速");
				this.left_eye_dis_text.setText("距离"+trafficFacilityInfo[0].getDistance()+"米");
				left_eye_camera_img.setVisibility(View.INVISIBLE);
				if(isLandscape()&&isShowCross){
					changeLandEyelayout(true);
				}
				isShowEyeCamera=true;
			} else  {
				this.left_speed_text.setVisibility(View.INVISIBLE);
				this.left_eye_dis_text.setVisibility(View.INVISIBLE);
				this.left_eye_camera_img.setVisibility(View.INVISIBLE);
				isShowEyeCamera=false;
				if(isLandscape()){
					changeLandEyelayout(false);
				}
			}
			int type2=trafficFacilityInfo[1].getBroardcastType();
			if((type2 >= 1) && (type2 <= 5)){
				isShowEyeCamera = true;
				this.limitSpeedTextView.setText("");
				this.limitSpeedTextView.setVisibility(View.VISIBLE);
				this.right_eye_dis_text.setVisibility(View.VISIBLE);
				this.right_eye_dis_text.setText("距离"+trafficFacilityInfo[1].getDistance()+"米");
				Drawable drawable = ResourcesUtil.getResources().getDrawable(camera_type[type2]);
				this.eys_img.setBackgroundDrawable(drawable);
				this.eys_img.setVisibility(View.VISIBLE);
				if(isLandscape()&&isShowCross){
					changeLandEyelayout(true);
				}
			}else if (type2 == 0) {
				this.limitSpeedTextView.setVisibility(View.VISIBLE);
				this.right_eye_dis_text.setVisibility(View.VISIBLE);
				if(trafficFacilityInfo[1].getLimitSpeed() > 0)
					this.left_speed_text.setText(""	+ trafficFacilityInfo[1].getLimitSpeed());
				else
					this.left_speed_text.setText("限速");
				this.right_eye_dis_text.setText("距离"+trafficFacilityInfo[1].getDistance()+"米");
				if(isLandscape()&&isShowCross){
					changeLandEyelayout(true);
				}
				isShowEyeCamera=true;
			} else  {
				this.limitSpeedTextView.setVisibility(View.INVISIBLE);
				this.right_eye_dis_text.setVisibility(View.INVISIBLE);
				this.eys_img.setVisibility(View.INVISIBLE);
				isShowEyeCamera=false;
				if(isLandscape()){
					changeLandEyelayout(false);
				}
			}
		}
	}

	boolean isLandscape() {
		return ((((Activity) getContext()).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) || (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE));
	}
	public void showCross(NaviCross naviCross) {
		if(naviCross==null)return;
		isShowCross = true;
		crossImg = naviCross;
		this.cross_progress.setProgress(0);
		this.crossImage_layout.setVisibility(View.VISIBLE);
		this.enlarge_road_layout.setBitMapIntoView(naviCross);
		if(isLandscape()) {
			bottom_info_layout.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(dp2px(210), ViewGroup.LayoutParams.MATCH_PARENT);
			mid_info_layout.setLayoutParams(params);
			LayoutParams par= (LayoutParams) driveWayView.getLayoutParams();
			par.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
			driveWayView.setLayoutParams(par);
			if(isShowEyeCamera)changeLandEyelayout(true);
		}else{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mid_info_layout.getLayoutParams();
			params.setMargins(0, dp2px(30), 0, dp2px(30));
			this.mid_info_layout.setLayoutParams(params);
		}
	}

	public void hideCross() {
		isShowCross=false;
		crossImg=null;
		this.crossImage_layout.setVisibility(View.GONE);
		this.enlarge_road_layout.recycleResource();
		changeLandEyelayout(false);
		if(isLandscape()){
			bottom_info_layout.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(dp2px(210),dp2px(50),0,0);
			mid_info_layout.setLayoutParams(params);
			LayoutParams par= (LayoutParams) driveWayView.getLayoutParams();
			par.gravity=Gravity.CENTER_HORIZONTAL;
			driveWayView.setLayoutParams(par);

		}else{
			RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mid_info_layout.getLayoutParams();
			params.setMargins(0,dp2px(30),0,dp2px(100));
			this.mid_info_layout.setLayoutParams(params);
		}
	}

	public void showLaneInfo(NaviLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
		if ((laneBackgroundInfo != null) && (laneRecommendedInfo != null)	&& (this.driveWayView != null)) {
			this.driveWayView.loadDriveWayBitmap(laneBackgroundInfo, laneRecommendedInfo);
			this.driveWayView.setVisibility(View.VISIBLE);
		}
	}

	public void hideLaneInfo() {
		if (this.driveWayView != null) {
			this.driveWayView.setVisibility(View.INVISIBLE);
			this.driveWayView.recycleResource();
		}
	}
	private int dp2px(int paramInt) {
		Context localContext = getContext();
		if (paramInt == 0)
			return 0;

		if (localContext == null)
			return paramInt;
		try {
			float f = TypedValue.applyDimension(1, paramInt, localContext.getResources().getDisplayMetrics());

			return (int) f;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return paramInt;
	}
	public void onCalculateMultipleRoutesSuccess(int[] routeIds) {
	}



	@Override
	public void updateServiceFacility(NaviServiceFacilityInfo[] serviceFacilityInfos) {

	}
	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
				// 第一次定位
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					break;
				// 卫星状态改变
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					// 获取当前状态
					LocationManager lm= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
					GpsStatus gpsStatus = mNavi.getGpsStatus();
					if(gpsStatus==null)return;
					numSatelliteList.clear();
					int maxSatellites = gpsStatus.getMaxSatellites();
					Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
					int count = 0;//gps数量
					while (iters.hasNext() && count <= maxSatellites) {
						GpsSatellite s = iters.next();
						numSatelliteList.add(s);
						count++;
					}
					Drawable offGps= ResourcesUtil.getResources().getDrawable(ResourcesUtil.gps_img_red);
					offGps.setBounds(0, 0, offGps.getMinimumWidth(), offGps.getMinimumHeight());
					Drawable onGps= ResourcesUtil.getResources().getDrawable(ResourcesUtil.gps_img_white);
					onGps.setBounds(0, 0, onGps.getMinimumWidth(), onGps.getMinimumHeight());
					if(numSatelliteList.size()>0) {
						if( waitGpsHUDLy.getVisibility()==View.VISIBLE) waitGpsHUDLy.setVisibility(View.GONE);
					}else{
						if(curNaviMode== Navi.GPSNaviMode){
							waitGpsHUDLy.setVisibility(View.VISIBLE);
						}
					}
					break;
				// 定位启动
				case GpsStatus.GPS_EVENT_STARTED:
					break;
				// 定位结束
				case GpsStatus.GPS_EVENT_STOPPED:
					break;
			}
		};
	};


}