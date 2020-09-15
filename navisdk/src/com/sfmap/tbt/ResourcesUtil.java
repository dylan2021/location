package com.sfmap.tbt;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.sfmap.navi.R;
import com.sfmap.tbt.util.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * 原类名:g
 */
public class ResourcesUtil {
	private static AssetManager assetManager = null;
	private static Resources resources = null;
	private static Resources c = null;
	private static boolean d = true;
	private static Context context;
	private static String name = "naviSDK";
	private static String code = "";
	private static String png = ".png";
	private static String jar = ".jar";
	private static String jarPath = name + code + jar;
	private static String pngPath = name + code + png;
	private static String filePath = Environment.getExternalStorageDirectory() + "/";
	private static String jarFile = filePath + jarPath;
	private static Resources.Theme n = null;
	private static Resources.Theme o = null;
	private static Field p = null;
	private static Field q = null;
	private static Activity activity = null;


	public static boolean init(Context paramContext) {
		context = paramContext;
		if (!(d))
			return true;
		if(isBuildMode){
			if (!(openOutPutStream(paramContext)))
				return false;
			assetManager = setAssetPath(jarFile);//设置资源文件路径
			resources = getResources(paramContext, assetManager);
		}else{
			resources = getResources();
		}
		return true;
	}

	public static Resources getResources() {
		if (resources == null)
			return context.getResources();
		return resources;
	}

	private static Resources getResources(Context paramContext, AssetManager paramAssetManager) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localDisplayMetrics.setToDefaults();
		Configuration localConfiguration = paramContext.getResources().getConfiguration();
		Resources localResources = new Resources(paramAssetManager,	localDisplayMetrics, localConfiguration);
		return localResources;
	}

	private static AssetManager setAssetPath(String paramString) {
		AssetManager localAssetManager = null;
		try {
			Class localClass = Class.forName("android.content.res.AssetManager");
			Constructor localConstructor = localClass.getConstructor((Class[]) null);
			localAssetManager = (AssetManager) localConstructor	.newInstance((Object[]) null);

			Method localMethod = localClass.getDeclaredMethod("addAssetPath",new Class[] { String.class });

			localMethod.invoke(localAssetManager, new Object[] { paramString });
		} catch (Throwable localThrowable) {
		}
		return localAssetManager;
	}

	private static boolean openOutPutStream(Context paramContext) {
		setJarPath(paramContext);
		InputStream localInputStream = null;
		OutputStream localOutputStream = null;
		try {
			localInputStream = paramContext.getResources().getAssets().open(pngPath);
			if (a(localInputStream)) {
				return true;
			}
			clearSDcard();
			localOutputStream = writeToFile(localInputStream, localOutputStream);
		} catch (Exception localException) {
			localException.printStackTrace();
			return false;
		} finally {
			try {
				if (localInputStream != null)
					localInputStream.close();
				if (localOutputStream != null)
					localOutputStream.close();
			} catch (IOException localIOException4) {
				localIOException4.printStackTrace();
			}
		}
		return true;
	}

	private static OutputStream writeToFile(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
		File localFile = new File(filePath, jarPath);
		paramOutputStream = new FileOutputStream(localFile);
		byte[] arrayOfByte = new byte[1024];

		int i1;
		while ((i1 = paramInputStream.read(arrayOfByte)) > 0) {
			paramOutputStream.write(arrayOfByte, 0, i1);
		}
		return paramOutputStream;
	}

	private static boolean a(InputStream paramInputStream) throws IOException {
		File localFile = new File(jarFile);
		long l1 = localFile.length();
		int i1 = paramInputStream.available();
		if ((localFile.exists()) && (l1 == i1)) {
			paramInputStream.close();
			return true;
		}
		return false;
	}


	private static void clearSDcard() {
		File localFile1 = new File(filePath);
		File[] arrayOfFile1 = localFile1.listFiles(new a());
		if ((arrayOfFile1 != null) && (arrayOfFile1.length > 0))
			for (File localFile2 : arrayOfFile1)
				localFile2.delete();
	}

	private static void setJarPath(Context paramContext) {
		filePath = paramContext.getFilesDir().getAbsolutePath();
		jarFile = filePath + "/" + jarPath;
	}

	public static View getViewByID(Activity paramActivity, int paramInt, ViewGroup paramViewGroup) {
		View localView = null;
		if(isBuildMode) {
			XmlResourceParser localXmlResourceParser = getResources().getXml(paramInt);

			if (!(d)) {
				localView = LayoutInflater.from(paramActivity).inflate(localXmlResourceParser, paramViewGroup);
				return localView;
			}
			try {
				boolean bool = a(paramActivity);
				localView = LayoutInflater.from(paramActivity).inflate(localXmlResourceParser, paramViewGroup);
				if (bool)
					b(paramActivity);
			} catch (Throwable localThrowable) {
				localThrowable.printStackTrace();
			} finally {
				localXmlResourceParser.close();
			}
		}else{
			localView=View.inflate(paramActivity,paramInt,paramViewGroup);}

		return localView;
	}

	public static boolean a(Activity paramActivity) {
		if (c != null)
			return false;
		try {
			if (p == null)
				p = h();

			if (q == null)
				q = i();

			if (n == null)
				n = g();

//			Context localContext = paramActivity.getBaseContext();
			Context context = Utils.getContext(paramActivity);
			c = (Resources) q.get(context);
			o = (Resources.Theme) p.get(paramActivity);

			q.set(context, resources);
			p.set(paramActivity, n);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			return false;
		}

		return true;
	}

	private static Resources.Theme g() {
		if (n == null) {
			if (assetManager == null)
				assetManager = setAssetPath(jarFile);

			if (resources == null) {
				resources = getResources(activity, assetManager);
			}

			n = resources.newTheme();
			int i1 = a("com.android.internal.R.style.Theme");
			n.applyStyle(i1, true);
		}
		return n;
	}

	private static Field h() {
		try {
			Class localClass = Class
					.forName("android.view.ContextThemeWrapper");
			p = localClass.getDeclaredField("mTheme");
			p.setAccessible(true);
		} catch (Throwable localThrowable) {
		}
		return p;
	}

	private static Field i() {
		try {
			Class localClass = Class.forName("android.app.ContextImpl");
			q = localClass.getDeclaredField("mResources");
			q.setAccessible(true);
		} catch (Throwable localThrowable) {
		}
		return q;
	}

	public static int a(String paramString) {
		int i1 = -1;
		try {
			int i2 = paramString.indexOf(".R.");
			String str1 = paramString.substring(0, i2 + 2);
			int i3 = paramString.lastIndexOf(".");
			String str2 = paramString.substring(i3 + 1, paramString.length());
			paramString = paramString.substring(0, i3);
			String str3 = paramString.substring(
					paramString.lastIndexOf(".") + 1, paramString.length());
			String str4 = str1 + "$" + str3;

			Class localClass = Class.forName(str4);
			i1 = localClass.getDeclaredField(str2).getInt(null);
		} catch (Throwable localThrowable) {
		}
		return i1;
	}

	public static void b(Activity paramActivity) {
		if (c == null)
			return;
		try {
			Context context = Utils.getContext(paramActivity);
			q.set(context, c);
			p.set(paramActivity, o);
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		} finally {
			c = null;
		}
	}

	public static Animation getAnimationByXML(Context paramContext, int paramInt)
			throws Resources.NotFoundException {
		Resources.NotFoundException localNotFoundException = new Resources.NotFoundException();
		XmlResourceParser localXmlResourceParser = null;
		try {
			localXmlResourceParser = getResources().getAnimation(paramInt);
			Animation localAnimation1 = a(paramContext, localXmlResourceParser);

			Animation localAnimation2 = localAnimation1;

			return localAnimation2;
		} catch (XmlPullParserException localXmlPullParserException) {
			throw localNotFoundException;
		} catch (IOException localIOException) {
			throw localNotFoundException;
		} finally {
			if (localXmlResourceParser != null)
				localXmlResourceParser.close();
		}

	}

	private static Animation a(Context paramContext,
							   XmlPullParser paramXmlPullParser) throws XmlPullParserException,
			IOException {
		return a(paramContext, paramXmlPullParser, null,Xml.asAttributeSet(paramXmlPullParser));
	}

	private static Animation a(Context paramContext,XmlPullParser paramXmlPullParser, AnimationSet paramAnimationSet,AttributeSet paramAttributeSet) throws XmlPullParserException,	IOException {
		Object localObject ;
		int i1 = paramXmlPullParser.getDepth();
		int i2;
		while ((((i2 = paramXmlPullParser.next()) != 3) || (paramXmlPullParser.getDepth() > i1)) && (i2 != 1)) {
			if (i2 == 2) {
				break;
			}
		}
		String str = paramXmlPullParser.getName();

		if ("set".equals(str)) {
			localObject = new AnimationSet(paramContext, paramAttributeSet);
			a(paramContext, paramXmlPullParser, (AnimationSet) localObject,
					paramAttributeSet);
		} else if ("alpha".equals(str)) {
			localObject = new AlphaAnimation(paramContext, paramAttributeSet);
		} else if ("scale".equals(str)) {
			localObject = new ScaleAnimation(paramContext, paramAttributeSet);
		} else if ("rotate".equals(str)) {
			localObject = new RotateAnimation(paramContext, paramAttributeSet);
		} else if ("translate".equals(str)) {
			localObject = new TranslateAnimation(paramContext,
					paramAttributeSet);
		} else {
			throw new RuntimeException("Unknown animation name: "
					+ paramXmlPullParser.getName());
		}

		if (paramAnimationSet != null)
			paramAnimationSet.addAnimation((Animation) localObject);
		return ((Animation) localObject);
	}

	static class a implements FilenameFilter {
		public boolean accept(File paramFile, String paramString) {
			String str = code + jar;
			return ((paramString.startsWith(name)) && (!(paramString.endsWith(str))));
		}
	}

	private static final boolean isBuildMode = false;//是否开启编译后的模式，需把所有资源id引用全部改为int值
///*
	public static final int sdk_road_enlarge_layout = R.layout.sdk_road_enlarge_layout;
	public static final int sdk_hudlayout_land=R.layout.sdk_hudlayout_land;
	public static final int sdk_hudlayout=R.layout.sdk_hudlayout;
	public static final int sdk_navi_cross_layout=R.layout.sdk_navi_cross_layout;
	public static final int sdk_land_navi_info_layout=R.layout.sdk_land_navi_info_layout;
	public static final int animation_bottom_in =R.anim.animation_bottom_in;
	public static final int animation_bottom_out =R.anim.animation_bottom_out;
	public static final int progress_background=R.color.progress_background;
	public static final int naviRoadEnlargeView=R.id.naviRoadEnlargeView;
	public static final int hudmirrortitle=R.id.hudmirrortitle;
	public static final int hudMirrosImage =R.id.HudMirrosImage;
	public static final int save=R.id.save;
	public static final int nextRoadNameText=R.id.nextRoadNameText;
	public static final int restDistanceText=R.id.restDistanceText;
	public static final int roadsignimg=R.id.roadsignimg;
	public static final int nextRoadDistanceText=R.id.nextRoadDistanceText;
	public static final int title_btn_goback=R.id.title_btn_goback;
	public static final int restDistanceTime=R.id.restDistanceTime;
	public static final int limitSpeedTextView=R.id.limitSpeedTextView;
	public static final int sdk_navi_fragment=R.layout.sdk_navi_fragment;
	public static final int map=R.id.map;
	public static final int enlarge_road_layout=R.id.enlarge_road_layout;
	public static final int directionView=R.id.directionView;
	public static final int directionView_horizontal=R.id.directionView_horizontal;
	public static final int navi_container=R.id.navi_container;
	public static final int driveWayViewInNaviView=R.id.driveWayViewInNaviView;
	public static final int port_limitSpeedText =R.id.port_limitSpeedText;
	public static final int port_electronic_eye_img =R.id.port_electronic_eye_img;
	public static final int port_roadsign =R.id.port_roadsign;
	public static final int port_nextRoadSignDisText =R.id.port_nextRoadSignDisText;
	public static final int port_nextRoadNameText =R.id.port_nextRoadNameText;
	public static final int port_exit_text =R.id.port_exit_text;
	public static final int port_restDistance =R.id.port_restDistance;
	public static final int port_distanceTimeText =R.id.port_distanceTimeText;
	public static final int port_emulator_speed =R.id.port_emulator_speed;
	public static final int port_reset_navi_car_layout =R.id.port_reset_navi_car_layout;
	public static final int port_show_naving_info =R.id.port_show_naving_info;
	public static final int route_tmc=R.id.route_tmc;
	public static final int mode_up=R.id.mode_up;
	public static final int navigation_down_line=R.id.navigation_down_line;
	public static final int navi_back_line=R.id.navi_back_line;
	public static final int browser_navi_setting =R.id.browser_navi_setting;
	public static final int port_emulator_stop =R.id.tv_emulator_stop;
	public static final int navi_exit_image_land=R.id.navi_exit_image_land;
	public static final int navi_setting_image_land=R.id.navi_setting_image_land;
	public static final int browser_navi_back =R.id.browser_navi_back;
	public static final int port_leftwidget =R.id.port_leftwidget;
	public static final int port_cur_road_name_view =R.id.port_cur_road_name_view;
	public static final int navi_widget_footer_linearlayout=R.id.navi_widget_footer_linearlayout;
	public static final int port_curRoadName =R.id.port_curRoadName;
	public static final int navigation_go_on=R.id.navigation_go_on;
	public static final int btn_zoom_out =R.id.btn_zoom_out;
	public static final int btn_zoom_in =R.id.btn_zoom_in;
	public static final int btn_preview =R.id.btn_preview;
	public static final int relativeLayout1=R.id.relativeLayout1;
	public static final int roadsign_layout=R.id.roadsign_layout;
	public static final int roadname_layout=R.id.roadname_layout;
	public static final int go_on_navi=R.id.go_on_navi;
	public static final int top_navi_info=R.id.top_navi_info;
	public static final int top_navi_cross_land=R.id.top_navi_cross_land;
	public static final int land_navi_info_include=R.id.land_navi_info_include;
	public static final int waitGpsLandLy=R.id.waitGpsLandLy;
	public static final int land_info_layout=R.id.land_info_layout;
	public static final int land_port_nextRoadNameText =R.id.land_port_nextRoadNameText;
	public static final int land_port_exit_text =R.id.land_port_exit_text;
	public static final int land_navi_dis_unit_text=R.id.land_navi_dis_unit_text;
	public static final int land_navi_next_action_text=R.id.land_navi_next_action_text;
	public static final int land_port_roadsign =R.id.land_port_roadsign;
	public static final int land_port_nextRoadSignDisText =R.id.land_port_nextRoadSignDisText;
	public static final int land_port_restDistance =R.id.land_port_restDistance;
	public static final int land_port_distanceTimeText =R.id.land_port_distanceTimeText;
	public static final int land_dis_and_time_layout=R.id.land_dis_and_time_layout;
	public static final int zoom_view_land=R.id.zoom_view_land;
	public static final int footbar_view_land=R.id.footbar_view_land;
	public static final int gps_sum_text_land=R.id.gps_sum_text_land;
	public static final int gps_sum_text_port=R.id.gps_sum_text_port;
	public static final int tv_car_speed=R.id.tv_car_speed;
	public static final int ln_car_speed=R.id.ln_car_speed;
	public static final int btn_preview_land =R.id.btn_preview_land;
	public static final int zoom_out_land =R.id.zoom_out_land;
	public static final int zoom_in_land =R.id.zoom_in_land;
	public static final int layout_zoom =R.id.layout_zoom;
	public static final int eye_speed_dis_text=R.id.eye_speed_dis_text;
	public static final int toll_station=R.id.toll_station;
	public static final int toll_station_name=R.id.toll_station_name;
	public static final int toll_station_dis=R.id.toll_station_dis;
	public static final int service_station=R.id.service_station;
	public static final int service_station_name=R.id.service_station_name;
	public static final int service_station_dis=R.id.service_station_dis;
	public static final int cross_progress=R.id.cross_progress;
	public static final int zoom_view =R.id.zoom_view;
	public static final int car_way_tmc_layout=R.id.car_way_tmc_layout;
	public static final int top_navi_info_layout=R.id.top_navi_info_layout;
	public static final int waitGpsPortLy=R.id.waitGpsPortLy;
	public static final int top_layout=R.id.top_layout;
	public static final int port_next_name_layout=R.id.port_next_name_layout;
	public static final int crossImage_layout=R.id.crossImage_layout;
	public static final int navi_dis_unit_text=R.id.navi_dis_unit_text;
	public static final int bottom_info_layout=R.id.bottom_info_layout;
	public static final int navi_next_action_text=R.id.navi_next_action_text;
	public static final int mid_info_layout=R.id.mid_info_layout;
	public static final int waitGpsHUDLy=R.id.waitGpsHUDLy;
	public static final int eys_img=R.id.eys_img;
	public static final int camera_layout=R.id.camera_layout;
	public static final int bottom_dis_time_layout=R.id.bottom_dis_time_layout;
	public static final int hud_enlarge_road_layout=R.id.hud_enlarge_road_layout;
	public static final int hud_cross_progress=R.id.hud_cross_progress;
	public static final int hud_driveWayViewInNaviView=R.id.hud_driveWayViewInNaviView;
	public static final int hud_eye_speed_dis_text=R.id.hud_eye_speed_dis_text;
	public static final int hud_DistanceUnitText=R.id.hud_DistanceUnitText;
	public static final int hud_nextActionText=R.id.hud_nextActionText;
	public static final int baseView=R.id.baseView;
	public static final int navi_tmc_cursor=R.id.navi_tmc_cursor;
	public static final int tmc_view_container=R.id.tmc_view_container;
	public static final int tmc_layout=R.id.tmc_layout;
	public static final int left_speed_text=R.id.left_speed_text;
	public static final int left_eye_camera_img=R.id.left_eye_camera_img;
	public static final int left_eye_dis_text=R.id.left_eye_dis_text;
	public static final int right_eye_dis_text=R.id.right_eye_dis_text;
	public static final int left_eye_camera_layout=R.id.left_eye_camera_layout;
	public static final int switch_img = R.id.switch_img;
	public static final int switch_img_relayout = R.id.switch_img_relayout;
	public static final int land_dis_text_down = R.id.land_dis_text_down;
//	public static final int time_text_down = R.id.time_text_down;
	public static final int dis_text_down = R.id.dis_text_down;
	public static final int top_line_view = R.id.top_line_view;
	public static final int mapview_relativeLayout = R.id.mapview_relativeLayout;

	public static final int navigation_light_bg=R.drawable.navigation_light_bg; //customTmcView
	public static final int navi_over_data_yaw_icon=R.drawable.navi_over_data_yaw_icon; //DirectionView
	public static final int cameraicon =R.drawable.cameraicon;
	public static final int front_0 =R.drawable.front_0;
	public static final int front_1 =R.drawable.front_1;
	public static final int front_2 =R.drawable.front_2;
	public static final int front_3 =R.drawable.front_3;
	public static final int front_4 =R.drawable.front_4;
	public static final int front_22 =R.drawable.front_22;
	public static final int front_25 =R.drawable.front_25;
	public static final int front_16 =R.drawable.front_16;
	public static final int front_17 =R.drawable.front_17;
	public static final int front_14 =R.drawable.front_14;
	public static final int front_15 =R.drawable.front_15;
	public static final int front_5 =R.drawable.front_5;
	public static final int front_6 =R.drawable.front_6;
	public static final int front_7 =R.drawable.front_7;
	public static final int front_8 =R.drawable.front_8;
	public static final int front_9 =R.drawable.front_9;
	public static final int front_10 =R.drawable.front_10;
	public static final int front_11 =R.drawable.front_11;
	public static final int front_12 =R.drawable.front_12;
	public static final int front_13 =R.drawable.front_13;
	public static final int front_19 =R.drawable.front_19;
	public static final int front_18 =R.drawable.front_18;
	public static final int front_21 =R.drawable.front_21;
	public static final int front_20 =R.drawable.front_20;
	public static final int front_23 =R.drawable.front_23;
	public static final int front_24 =R.drawable.front_24;
	public static final int back_0 =R.drawable.back_0;
	public static final int back_1 =R.drawable.back_1;
	public static final int back_2 =R.drawable.back_2;
	public static final int back_3 =R.drawable.back_3;
	public static final int back_4 =R.drawable.back_4;
	public static final int back_5 =R.drawable.back_5;
	public static final int back_6 =R.drawable.back_6;
	public static final int back_7 =R.drawable.back_7;
	public static final int back_8 =R.drawable.back_8;
	public static final int back_9 =R.drawable.back_9;
	public static final int back_10 =R.drawable.back_10;
	public static final int back_11 =R.drawable.back_11;
	public static final int back_12 =R.drawable.back_12;
	public static final int back_13 =R.drawable.back_13;
	public static final int back_14 =R.drawable.back_14;
	public static final int back_25 =R.drawable.back_25;
	public static final int caricon=R.drawable.caricon;
	public static final int caricon_disable=R.drawable.caricon_disable;
	public static final int navi_direction=R.drawable.navi_direction;
	public static final int startpoint=R.drawable.startpoint;
	public static final int waypoint=R.drawable.waypoint;
	public static final int endpoint=R.drawable.endpoint;
	public static final int map_traffic=R.drawable.map_traffic;
	public static final int map_traffic_hl=R.drawable.map_traffic_hl;
	public static final int navi_footer_bg_white=R.drawable.navi_footer_bg_white;
	public static final int draw_navigation_down_line=R.drawable.navigation_down_line;
	public static final int navigation_close_black=R.drawable.navigation_close_black;
	public static final int hud_action_1 =R.drawable.hud_action_1;
	public static final int hud_action_2 =R.drawable.hud_action_2;
	public static final int hud_action_3 =R.drawable.hud_action_3;
	public static final int hud_action_4 =R.drawable.hud_action_4;
	public static final int hud_action_5 =R.drawable.hud_action_5;
	public static final int hud_action_6 =R.drawable.hud_action_6;
	public static final int hud_action_7 =R.drawable.hud_action_7;
	public static final int hud_action_8 =R.drawable.hud_action_8;
	public static final int hud_action_9 =R.drawable.hud_action_9;
	public static final int hud_action_10 =R.drawable.hud_action_10;
	public static final int hud_action_11 =R.drawable.hud_action_11;
	public static final int hud_action_12 =R.drawable.hud_action_12;
	public static final int hud_action_13 =R.drawable.hud_action_13;
	public static final int hud_action_14 =R.drawable.hud_action_14;
	public static final int hud_action_15 =R.drawable.hud_action_15;
	public static final int hud_action_16 =R.drawable.hud_action_16;
	public static final int hud_action_17 =R.drawable.hud_action_17;
	public static final int hud_action_18 =R.drawable.hud_action_18;
	public static final int hud_action_19 =R.drawable.hud_action_19;
	public static final int hud_action_20 =R.drawable.hud_action_20;
	public static final int hud_action_21 =R.drawable.hud_action_21;
	public static final int hud_action_22 =R.drawable.hud_action_22;
	public static final int hud_action_23 =R.drawable.hud_action_23;
	public static final int hud_action_24 =R.drawable.hud_action_24;
	public static final int hud_action_25 =R.drawable.hud_action_25;
	public static final int action_1 =R.drawable.action_1;
	public static final int action_2 =R.drawable.action_2;
	public static final int action_3 =R.drawable.action_3;
	public static final int action_4 =R.drawable.action_4;
	public static final int action_5 =R.drawable.action_5;
	public static final int action_6 =R.drawable.action_6;
	public static final int action_7 =R.drawable.action_7;
	public static final int action_8 =R.drawable.action_8;
	public static final int action_9 =R.drawable.action_9;
	public static final int action_10 =R.drawable.action_10;
	public static final int action_11 =R.drawable.action_11;
	public static final int action_12 =R.drawable.action_12;
	public static final int action_13 =R.drawable.action_13;
	public static final int action_14 =R.drawable.action_14;
	public static final int action_15 =R.drawable.action_15;
	public static final int action_16 =R.drawable.action_16;
	public static final int action_17 =R.drawable.action_17;
	public static final int action_18 =R.drawable.action_18;
	public static final int action_19 =R.drawable.action_19;
	public static final int action_20 =R.drawable.action_20;
	public static final int action_21 =R.drawable.action_21;
	public static final int action_22 =R.drawable.action_22;
	public static final int action_23 =R.drawable.action_23;
	public static final int action_24 =R.drawable.action_24;
	public static final int action_25 =R.drawable.action_25;
	public static final int drive_way_line=R.drawable.drive_way_line;
	public static final int default_navi_land_bg_normal=R.drawable.default_navi_land_bg_normal;
	public static final int speed_5_right=R.drawable.speed_5_right;
	public static final int speed_10_right=R.drawable.speed_10_right;
	public static final int speed_20_right=R.drawable.speed_20_right;
	public static final int speed_30_right=R.drawable.speed_30_right;
	public static final int speed_40_right=R.drawable.speed_40_right;
	public static final int speed_50_right=R.drawable.speed_50_right;
	public static final int speed_60_right=R.drawable.speed_60_right;
	public static final int speed_70_right=R.drawable.speed_70_right;
	public static final int speed_80_right=R.drawable.speed_80_right;
	public static final int speed_90_right=R.drawable.speed_90_right;
	public static final int speed_100_right=R.drawable.speed_100_right;
	public static final int speed_110_right=R.drawable.speed_110_right;
	public static final int speed_120_right=R.drawable.speed_120_right;
	public static final int eye_navi_line=R.drawable.eye_navi_line;
	public static final int bus_line_camera=R.drawable.bus_line_camera;
	public static final int contingency_line_camera=R.drawable.contingency_line_camera;
	public static final int traffic_lights_camera=R.drawable.traffic_lights_camera;
	public static final int gps_img_white=R.drawable.gps_img_white;
	public static final int gps_img_red=R.drawable.gps_img_red;
	public static final int service_station_img=R.drawable.service_station;
	public static final int toll_station_img=R.drawable.toll_station;
	public static final int hud_front_0 =R.drawable.hud_front_0;
	public static final int hud_front_1 =R.drawable.hud_front_1;
	public static final int hud_front_2 =R.drawable.hud_front_2;
	public static final int hud_front_3 =R.drawable.hud_front_3;
	public static final int hud_front_4 =R.drawable.hud_front_4;
	public static final int hud_front_22 =R.drawable.hud_front_22;
	public static final int hud_front_16 =R.drawable.hud_front_16;
	public static final int hud_front_17 =R.drawable.hud_front_17;
	public static final int hud_front_14 =R.drawable.hud_front_14;
	public static final int hud_front_15 =R.drawable.hud_front_15;
	public static final int hud_front_5 =R.drawable.hud_front_5;
	public static final int hud_front_6 =R.drawable.hud_front_6;
	public static final int hud_front_7 =R.drawable.hud_front_7;
	public static final int hud_front_8 =R.drawable.hud_front_8;
	public static final int hud_front_9 =R.drawable.hud_front_9;
	public static final int hud_front_10 =R.drawable.hud_front_10;
	public static final int hud_front_11 =R.drawable.hud_front_11;
	public static final int hud_front_12 =R.drawable.hud_front_12;
	public static final int hud_front_13 =R.drawable.hud_front_13;
	public static final int hud_front_19 =R.drawable.hud_front_19;
	public static final int hud_front_18 =R.drawable.hud_front_18;
	public static final int hud_front_21 =R.drawable.hud_front_21;
	public static final int hud_front_20 =R.drawable.hud_front_20;
	public static final int hud_front_23 =R.drawable.hud_front_23;
	public static final int hud_front_24 =R.drawable.hud_front_24;
	public static final int hud_back_0 =R.drawable.hud_back_0;
	public static final int hud_back_1 =R.drawable.hud_back_1;
	public static final int hud_back_2 =R.drawable.hud_back_2;
	public static final int hud_back_3 =R.drawable.hud_back_3;
	public static final int hud_back_4 =R.drawable.hud_back_4;
	public static final int hud_back_5 =R.drawable.hud_back_5;
	public static final int hud_back_6 =R.drawable.hud_back_6;
	public static final int hud_back_7 =R.drawable.hud_back_7;
	public static final int hud_back_8 =R.drawable.hud_back_8;
	public static final int hud_back_9 =R.drawable.hud_back_9;
	public static final int hud_back_10 =R.drawable.hud_back_10;
	public static final int hud_back_11 =R.drawable.hud_back_11;
	public static final int hud_back_12 =R.drawable.hud_back_12;
	public static final int hud_back_13 =R.drawable.hud_back_13;
	public static final int hud_back_14 =R.drawable.hud_back_14;
	public static final int speed_5_left=R.drawable.speed_5_left;
	public static final int speed_10_left=R.drawable.speed_10_left;
	public static final int speed_20_left=R.drawable.speed_20_left;
	public static final int speed_30_left=R.drawable.speed_30_left;
	public static final int speed_40_left=R.drawable.speed_40_left;
	public static final int speed_50_left=R.drawable.speed_50_left;
	public static final int speed_60_left=R.drawable.speed_60_left;
	public static final int speed_70_left=R.drawable.speed_70_left;
	public static final int speed_80_left=R.drawable.speed_80_left;
	public static final int speed_90_left=R.drawable.speed_90_left;
	public static final int speed_100_left=R.drawable.speed_100_left;
	public static final int speed_110_left=R.drawable.speed_110_left;
	public static final int speed_120_left=R.drawable.speed_120_left;
	public static final int eye_navi_line_left=R.drawable.eye_navi_line_left;
	public static final int bus_line_camera_left=R.drawable.bus_line_camera_left;
	public static final int contingency_line_camera_left=R.drawable.contingency_line_camera_left;
	public static final int traffic_lights_camera_left=R.drawable.traffic_lights_camera_left;
	public static final int hud_driveway_line=R.drawable.hud_driveway_line;
	public static final int default_navi_land_bg_hud=R.drawable.default_navi_land_bg_hud;
	public static final int navi_popup=R.drawable.navi_popup;
	public static final int bus_line=R.drawable.bus_line;
	public static final int camera=R.drawable.camera;
	public static final int contingency_line=R.drawable.contingency_line;
	public static final int peccancy_line=R.drawable.peccancy_line;
	public static final int traffic_light=R.drawable.traffic_light;
	public static final int high_bridge_bottom=R.drawable.high_bridge_bottom;
	public static final int high_bridge_above=R.drawable.high_bridge_above;
	public static final int road_main=R.drawable.road_main;
	public static final int road_auxiliary=R.drawable.road_auxiliary;
	public static final int speed_text_left=R.drawable.speed_text_left;
	public static final int speed_text=R.drawable.speed_text;
	public static final int camera_camera_left=R.drawable.camera_camera_left;
	public static final int camera_camera=R.drawable.camera_camera;
	public static final int navi_up=R.drawable.navi_up;
	public static final int navi_north=R.drawable.navi_north;

//*/

	/*
	//layout
	public static final int sdk_hudlayout = 0x7f03000a;
	public static final int sdk_hudlayout_land = 0x7f03000b;
	public static final int sdk_land_navi_info_layout = 0x7f03000c;
	public static final int sdk_navi_cross_layout = 0x7f03000d;
	public static final int sdk_navi_fragment = 0x7f03000e;
	public static final int sdk_road_enlarge_layout = 0x7f03000f;

	//drawable
	public static final int action_1 = 0x7f020000;
	public static final int action_10 = 0x7f020001;
	public static final int action_11 = 0x7f020002;
	public static final int action_12 = 0x7f020003;
	public static final int action_13 = 0x7f020004;
	public static final int action_14 = 0x7f020005;
	public static final int action_15 = 0x7f020006;
	public static final int action_16 = 0x7f020007;
	public static final int action_17 = 0x7f020008;
	public static final int action_18 = 0x7f020009;
	public static final int action_19 = 0x7f02000a;
	public static final int action_2 = 0x7f02000b;
	public static final int action_20 = 0x7f02000c;
	public static final int action_3 = 0x7f02000d;
	public static final int action_4 = 0x7f02000e;
	public static final int action_5 = 0x7f02000f;
	public static final int action_6 = 0x7f020010;
	public static final int action_7 = 0x7f020011;
	public static final int action_8 = 0x7f020012;
	public static final int action_9 = 0x7f020013;
	public static final int allview_land_light = 0x7f020014;
	public static final int allview_land_normal = 0x7f020015;
	public static final int back_0 = 0x7f020016;
	public static final int back_1 = 0x7f020017;
	public static final int back_10 = 0x7f020018;
	public static final int back_11 = 0x7f020019;
	public static final int back_12 = 0x7f02001a;
	public static final int back_13 = 0x7f02001b;
	public static final int back_14 = 0x7f02001c;
	public static final int back_2 = 0x7f02001d;
	public static final int back_3 = 0x7f02001e;
	public static final int back_4 = 0x7f02001f;
	public static final int back_5 = 0x7f020020;
	public static final int back_6 = 0x7f020021;
	public static final int back_7 = 0x7f020022;
	public static final int back_8 = 0x7f020023;
	public static final int back_9 = 0x7f020024;
	public static final int back_btn_bg_selector = 0x7f020025;
	public static final int bg = 0x7f020026;
	public static final int border = 0x7f020027;
	public static final int bus_line = 0x7f020028;
	public static final int bus_line_camera = 0x7f020029;
	public static final int bus_line_camera_left = 0x7f02002a;
	public static final int camera = 0x7f02002b;
	public static final int camera_camera = 0x7f02002c;
	public static final int camera_camera_left = 0x7f02002d;
	public static final int cameraicon = 0x7f02002e;
	public static final int car = 0x7f02002f;
	public static final int caricon = 0x7f020030;
	public static final int checkbox_selector = 0x7f020031;
	public static final int common_bar_bg = 0x7f020032;
	public static final int contingency_line = 0x7f020033;
	public static final int contingency_line_camera = 0x7f020034;
	public static final int contingency_line_camera_left = 0x7f020035;
	public static final int custtexture = 0x7f020036;
	public static final int default_navi_land_bg_hud = 0x7f020037;
	public static final int default_navi_land_bg_normal = 0x7f020038;
	public static final int direction_btn_bg = 0x7f020039;
	public static final int direction_btn_bg_hl = 0x7f02003a;
	public static final int drive_way_line = 0x7f02003b;
	public static final int electronic_eye = 0x7f02003c;
	public static final int endpoint = 0x7f02003d;
	public static final int exit = 0x7f02003e;
	public static final int eye_navi_line = 0x7f02003f;
	public static final int eye_navi_line_left = 0x7f020040;
	public static final int front_0 = 0x7f020041;
	public static final int front_1 = 0x7f020042;
	public static final int front_10 = 0x7f020043;
	public static final int front_11 = 0x7f020044;
	public static final int front_12 = 0x7f020045;
	public static final int front_13 = 0x7f020046;
	public static final int front_14 = 0x7f020047;
	public static final int front_15 = 0x7f020048;
	public static final int front_16 = 0x7f020049;
	public static final int front_17 = 0x7f02004a;
	public static final int front_18 = 0x7f02004b;
	public static final int front_19 = 0x7f02004c;
	public static final int front_2 = 0x7f02004d;
	public static final int front_20 = 0x7f02004e;
	public static final int front_21 = 0x7f02004f;
	public static final int front_22 = 0x7f020050;
	public static final int front_23 = 0x7f020051;
	public static final int front_24 = 0x7f020052;
	public static final int front_3 = 0x7f020053;
	public static final int front_4 = 0x7f020054;
	public static final int front_5 = 0x7f020055;
	public static final int front_6 = 0x7f020056;
	public static final int front_7 = 0x7f020057;
	public static final int front_8 = 0x7f020058;
	public static final int front_9 = 0x7f020059;
	//public static final int go_on_navi = 0x7f02005a;
	public static final int gps_img_red = 0x7f02005b;
	public static final int gps_img_white = 0x7f02005c;
	public static final int high_bridge_above = 0x7f02005d;
	public static final int high_bridge_bottom = 0x7f02005e;
	public static final int hud_action_1 = 0x7f02005f;
	public static final int hud_action_10 = 0x7f020060;
	public static final int hud_action_11 = 0x7f020061;
	public static final int hud_action_12 = 0x7f020062;
	public static final int hud_action_13 = 0x7f020063;
	public static final int hud_action_14 = 0x7f020064;
	public static final int hud_action_15 = 0x7f020065;
	public static final int hud_action_16 = 0x7f020066;
	public static final int hud_action_17 = 0x7f020067;
	public static final int hud_action_18 = 0x7f020068;
	public static final int hud_action_19 = 0x7f020069;
	public static final int hud_action_2 = 0x7f02006a;
	public static final int hud_action_20 = 0x7f02006b;
	public static final int hud_action_3 = 0x7f02006c;
	public static final int hud_action_4 = 0x7f02006d;
	public static final int hud_action_5 = 0x7f02006e;
	public static final int hud_action_6 = 0x7f02006f;
	public static final int hud_action_7 = 0x7f020070;
	public static final int hud_action_8 = 0x7f020071;
	public static final int hud_action_9 = 0x7f020072;
	public static final int hud_back_0 = 0x7f020073;
	public static final int hud_back_1 = 0x7f020074;
	public static final int hud_back_10 = 0x7f020075;
	public static final int hud_back_11 = 0x7f020076;
	public static final int hud_back_12 = 0x7f020077;
	public static final int hud_back_13 = 0x7f020078;
	public static final int hud_back_14 = 0x7f020079;
	public static final int hud_back_2 = 0x7f02007a;
	public static final int hud_back_3 = 0x7f02007b;
	public static final int hud_back_4 = 0x7f02007c;
	public static final int hud_back_5 = 0x7f02007d;
	public static final int hud_back_6 = 0x7f02007e;
	public static final int hud_back_7 = 0x7f02007f;
	public static final int hud_back_8 = 0x7f020080;
	public static final int hud_back_9 = 0x7f020081;
	public static final int hud_camera_distance = 0x7f020082;
	public static final int hud_cross_close_normal = 0x7f020083;
	public static final int hud_driveway_line = 0x7f020084;
	public static final int hud_front_0 = 0x7f020085;
	public static final int hud_front_1 = 0x7f020086;
	public static final int hud_front_10 = 0x7f020087;
	public static final int hud_front_11 = 0x7f020088;
	public static final int hud_front_12 = 0x7f020089;
	public static final int hud_front_13 = 0x7f02008a;
	public static final int hud_front_14 = 0x7f02008b;
	public static final int hud_front_15 = 0x7f02008c;
	public static final int hud_front_16 = 0x7f02008d;
	public static final int hud_front_17 = 0x7f02008e;
	public static final int hud_front_18 = 0x7f02008f;
	public static final int hud_front_19 = 0x7f020090;
	public static final int hud_front_2 = 0x7f020091;
	public static final int hud_front_20 = 0x7f020092;
	public static final int hud_front_21 = 0x7f020093;
	public static final int hud_front_22 = 0x7f020094;
	public static final int hud_front_23 = 0x7f020095;
	public static final int hud_front_24 = 0x7f020096;
	public static final int hud_front_3 = 0x7f020097;
	public static final int hud_front_4 = 0x7f020098;
	public static final int hud_front_5 = 0x7f020099;
	public static final int hud_front_6 = 0x7f02009a;
	public static final int hud_front_7 = 0x7f02009b;
	public static final int hud_front_8 = 0x7f02009c;
	public static final int hud_front_9 = 0x7f02009d;

	public static final int hud_navi_distance = 0x7f02009e;
	public static final int hud_navi_distancetime = 0x7f02009f;

	public static final int hud_speed_bg = 0x7f0200a0;
	public static final int ic_launcher = 0x7f0200a1;
	public static final int land_navi_zoomax_disabled = 0x7f0200a2;
	public static final int land_navi_zoomax_normal = 0x7f0200a3;
	public static final int land_navi_zoomin_disabled = 0x7f0200a4;
	public static final int land_navi_zoomin_normal = 0x7f0200a5;

	public static final int map_traffic = 0x7f0200a6;
	public static final int map_traffic_hl = 0x7f0200a7;
	public static final int marker1 = 0x7f0200a8;
	public static final int marker2 = 0x7f0200a9;
	public static final int marker_1 = 0x7f0200aa;
	public static final int nav_gps_progress_bar = 0x7f0200ae;

	public static final int nav_gps_progressbar_style = 0x7f0200af;
	public static final int navi_allview_light = 0x7f0200b0;
	public static final int navi_allview_normal = 0x7f0200b1;
	public static final int navi_direction = 0x7f0200b2;
	public static final int navi_footer_bg_white = 0x7f0200b3;
	public static final int navi_gps_progressbar = 0x7f0200b4;
	public static final int navi_north = 0x7f0200b5;
	public static final int navi_over_data_yaw_icon = 0x7f0200b6;
	public static final int navi_popup = 0x7f0200b7;
	public static final int navi_title_bg = 0x7f0200b8;
	public static final int navi_up = 0x7f0200b9;
	public static final int navi_view_zoomin_light = 0x7f0200ba;
	public static final int navi_view_zoomin_normal = 0x7f0200bb;
	public static final int navi_view_zoomout_light = 0x7f0200bc;
	public static final int navi_view_zoomout_normal = 0x7f0200bd;
	public static final int navigation_close_black = 0x7f0200be;
	//public static final int navigation_down_line = 0x7f0200bf;
	public static final int navigation_light_bg = 0x7f0200c0;
	public static final int navigation_tmcbar_cursor = 0x7f0200c1;
	public static final int peccancy_line = 0x7f0200c2;
	public static final int pouse = 0x7f0200c3;
	public static final int pouse_land = 0x7f0200c4;
	public static final int prefer_setting_btn_off = 0x7f0200c5;
	public static final int prefer_setting_btn_on = 0x7f0200c6;
	public static final int pressednooutborder = 0x7f0200c7;
	public static final int preview_land_selector = 0x7f0200c8;
	public static final int preview_selector = 0x7f0200c9;
	public static final int progress_bar_layer = 0x7f0200ca;
	public static final int road_auxiliary = 0x7f0200cb;
	public static final int road_main = 0x7f0200cc;
	public static final int service_station_img = 0x7f0200cd;
	public static final int set = 0x7f0200ce;
	public static final int set_land = 0x7f0200cf;
	public static final int speed_100_left = 0x7f0200d0;
	public static final int speed_100_right = 0x7f0200d1;
	public static final int speed_10_left = 0x7f0200d2;
	public static final int speed_10_right = 0x7f0200d3;
	public static final int speed_110_left = 0x7f0200d4;
	public static final int speed_110_right = 0x7f0200d5;
	public static final int speed_120_left = 0x7f0200d6;
	public static final int speed_120_right = 0x7f0200d7;
	public static final int speed_20_left = 0x7f0200d8;
	public static final int speed_20_right = 0x7f0200d9;
	public static final int speed_30_left = 0x7f0200da;
	public static final int speed_30_right = 0x7f0200db;
	public static final int speed_40_left = 0x7f0200dc;
	public static final int speed_40_right = 0x7f0200dd;
	public static final int speed_50_left = 0x7f0200de;
	public static final int speed_50_right = 0x7f0200df;
	public static final int speed_5_left = 0x7f0200e0;
	public static final int speed_5_right = 0x7f0200e1;
	public static final int speed_60_left = 0x7f0200e2;
	public static final int speed_60_right = 0x7f0200e3;
	public static final int speed_70_left = 0x7f0200e4;
	public static final int speed_70_right = 0x7f0200e5;
	public static final int speed_80_left = 0x7f0200e6;
	public static final int speed_80_right = 0x7f0200e7;
	public static final int speed_90_left = 0x7f0200e8;
	public static final int speed_90_right = 0x7f0200e9;
	public static final int speed_bg = 0x7f0200ea;
	public static final int speed_dis_backgroud = 0x7f0200eb;
	public static final int speed_text = 0x7f0200ec;
	public static final int speed_text_left = 0x7f0200ed;
	public static final int startborder = 0x7f0200ee;
	public static final int startpoint = 0x7f0200ef;
	public static final int toll_station_img = 0x7f0200f0;
	public static final int traffic_light = 0x7f0200f1;
	public static final int traffic_lights_camera = 0x7f0200f2;
	public static final int traffic_lights_camera_left = 0x7f0200f3;
	public static final int waypoint = 0x7f0200f4;
	public static final int whiteborder = 0x7f0200f5;
	public static final int whitedownborder = 0x7f0200f6;
	public static final int whitenooutborder = 0x7f0200f7;
	public static final int zoomin_land_selector = 0x7f0200f8;
	public static final int zoomin_selector = 0x7f0200f9;
	public static final int zoomout_land_selector = 0x7f0200fa;
	public static final int zoomout_selector = 0x7f0200fb;

	//id
	public static final int hudMirrosImage = 0x7f0a0019;
	public static final int baseView = 0x7f0a0068;
	public static final int bottom_dis_time_layout = 0x7f0a002f;
	public static final int bottom_info_layout = 0x7f0a0020;
	public static final int bottom_land_lind_view = 0x7f0a007e;
	public static final int browser_navi_back = 0x7f0a0080;
	public static final int browser_navi_setting = 0x7f0a008c;
	public static final int btn_preview = 0x7f0a006e;
	public static final int btn_preview_land = 0x7f0a007b;
	public static final int btn_zoom_in = 0x7f0a006f;
	public static final int btn_zoom_out = 0x7f0a0070;
	public static final int camera_layout = 0x7f0a0029;
	public static final int car_tmc_layout = 0x7f0a005e;
	public static final int car_way_tmc_layout = 0x7f0a005d;
	public static final int crossImage_layout = 0x7f0a001b;
	public static final int cross_progress = 0x7f0a0054;
	public static final int directionView = 0x7f0a0062;
	public static final int directionView_horizontal = 0x7f0a005f;
	public static final int dis_text_down = 0x7f0a0085;
	public static final int driveWayViewInNaviView = 0x7f0a0066;
	public static final int enlarge_road_layout = 0x7f0a0053;
	public static final int eye_speed_dis_text = 0x7f0a0065;
	public static final int eys_img = 0x7f0a002b;
	public static final int footbar_view_land = 0x7f0a0077;
	public static final int go_on_navi = 0x7f0a0041;
	public static final int gps_sum_text_land = 0x7f0a003f;
	public static final int gps_sum_text_port = 0x7f0a0052;
	public static final int hdu_nextActionText = 0x7f0a0037;
	public static final int hud_DistanceUnitText = 0x7f0a0022;
	public static final int hud_cross_progress = 0x7f0a001d;
	public static final int hud_driveWayViewInNaviView = 0x7f0a001e;
	public static final int hud_enlarge_road_layout = 0x7f0a001c;
	public static final int hud_eye_speed_dis_text = 0x7f0a0026;
	public static final int hud_nextActionText = 0x7f0a002d;
	public static final int hudmirrortitle = 0x7f0a0033;
	public static final int land_cross_layout = 0x7f0a0056;
	public static final int land_dis_and_time_layout = 0x7f0a0042;
	public static final int land_dis_text_down = 0x7f0a0043;
	public static final int land_info_layout = 0x7f0a0038;
	public static final int land_navi_dis_unit_text = 0x7f0a003a;
	public static final int land_navi_info_include = 0x7f0a0057;
	public static final int land_navi_next_action_text = 0x7f0a003c;
	public static final int land_port_distanceTimeText = 0x7f0a0045;
	public static final int land_port_nextRoadNameText = 0x7f0a003d;
	public static final int land_port_nextRoadSignDisText = 0x7f0a0039;
	public static final int land_port_restDistance = 0x7f0a0044;
	public static final int land_port_roadsign = 0x7f0a003b;
	public static final int land_port_show_naving_info = 0x7f0a0040;
	public static final int left_eye_camera_img = 0x7f0a0025;
	public static final int left_eye_camera_layout = 0x7f0a0023;
	public static final int left_eye_dis_text = 0x7f0a0027;
	public static final int left_speed_text = 0x7f0a0024;
	public static final int limitSpeedTextView = 0x7f0a002a;
	public static final int map = 0x7f0a005b;
	public static final int mapview_relativeLayout = 0x7f0a0059;
	public static final int mid_info_layout = 0x7f0a001f;
	public static final int mode_up = 0x7f0a0061;
	public static final int naviRoadEnlargeView = 0x7f0a008d;
	public static final int navi_back_line = 0x7f0a0081;
	public static final int navi_container = 0x7f0a0055;
	public static final int navi_dis_unit_text = 0x7f0a004d;
	public static final int navi_exit_image_land = 0x7f0a0078;
	public static final int navi_next_action_text = 0x7f0a004e;
	public static final int navi_setting_image_land = 0x7f0a007d;
	public static final int navi_tmc_cursor = 0x7f0a006a;
	public static final int navi_widget_footer_linearlayout = 0x7f0a007f;
	public static final int navigation_down_line = 0x7f0a008b;
	public static final int navigation_go_on = 0x7f0a008a;
	public static final int nextRoadDistanceText = 0x7f0a0021;
	public static final int nextRoadNameText = 0x7f0a002e;
	public static final int port_curRoadName = 0x7f0a0088;
	public static final int port_cur_road_name_view = 0x7f0a0087;
	public static final int port_distanceTimeText = 0x7f0a0084;
	public static final int port_electronic_eye_img = 0x7f0a0063;
	public static final int port_leftwidget = 0x7f0a0048;
	public static final int port_limitSpeedText = 0x7f0a0064;
	public static final int port_nextRoadNameText = 0x7f0a0050;
	public static final int port_nextRoadSignDisText = 0x7f0a004c;
	public static final int port_next_name_layout = 0x7f0a004f;
	public static final int port_reset_navi_car_layout = 0x7f0a0089;
	public static final int port_restDistance = 0x7f0a0083;
	public static final int port_roadsign = 0x7f0a0049;
	public static final int port_show_naving_info = 0x7f0a0082;
	public static final int relativeLayout1 = 0x7f0a005a;
	public static final int restDistanceText = 0x7f0a0030;
	public static final int restDistanceTime = 0x7f0a0031;
	public static final int right_eye_dis_text = 0x7f0a002c;
	public static final int roadname_layout = 0x7f0a004b;
	public static final int roadsign_layout = 0x7f0a0047;
	public static final int roadsignimg = 0x7f0a0028;
	public static final int route_tmc = 0x7f0a0060;
	public static final int save = 0x7f0a0036;
	public static final int service_station = 0x7f0a0074;
	public static final int service_station_dis = 0x7f0a0076;
	public static final int service_station_name = 0x7f0a0075;
	public static final int switch_img = 0x7f0a006c;
	public static final int switch_img_relayout = 0x7f0a006b;
	public static final int time_text_down = 0x7f0a0086;
	public static final int title_btn_goback = 0x7f0a0034;
	public static final int title_name_text = 0x7f0a0035;
	public static final int tmc_layout = 0x7f0a0067;
	public static final int tmc_view_container = 0x7f0a0069;
	public static final int toll_station = 0x7f0a0071;
	public static final int toll_station_dis = 0x7f0a0073;
	public static final int toll_station_name = 0x7f0a0072;
	public static final int top_layout = 0x7f0a001a;
	public static final int top_line_view = 0x7f0a004a;
	public static final int top_navi_cross_land = 0x7f0a0058;
	public static final int top_navi_info = 0x7f0a005c;
	public static final int top_navi_info_layout = 0x7f0a0046;
	public static final int waitGpsHUDLy = 0x7f0a0032;
	public static final int waitGpsLandLy = 0x7f0a003e;
	public static final int waitGpsPortLy = 0x7f0a0051;
	public static final int zoom_in_land = 0x7f0a007a;
	public static final int zoom_out_land = 0x7f0a007c;
	public static final int zoom_view = 0x7f0a006d;
	public static final int zoom_view_land = 0x7f0a0079;

	//anim
	public static final int animation_bottom_in = 0x7f040000;
	public static final int animation_bottom_out = 0x7f040001;
	public static final int cycle_interpolator = 0x7f040002;
	*/

/*
    public static final int animation_bottom_in = 2130968576;
    public static final int animation_bottom_out = 2130968577;

    public static final int black = 2131034112;
    public static final int darkyellow = 2131034113;
    public static final int grey = 2131034114;
    public static final int progress_background = 2131034115;
    public static final int white = 2131034116;

	public static final int font_12 = 2131099648;
    public static final int font_20 = 2131099649;
    public static final int font_24 = 2131099650;
    public static final int hud_left_width = 2131099651;

    public static final int action_1 = 2130837504;
    public static final int action_10 = 2130837505;
    public static final int action_11 = 2130837506;
    public static final int action_12 = 2130837507;
    public static final int action_13 = 2130837508;
    public static final int action_14 = 2130837509;
    public static final int action_15 = 2130837510;
    public static final int action_16 = 2130837511;
    public static final int action_17 = 2130837512;
    public static final int action_18 = 2130837513;
    public static final int action_19 = 2130837514;
    public static final int action_2 = 2130837515;
    public static final int action_20 = 2130837516;
    public static final int action_3 = 2130837517;
    public static final int action_4 = 2130837518;
    public static final int action_5 = 2130837519;
    public static final int action_6 = 2130837520;
    public static final int action_7 = 2130837521;
    public static final int action_8 = 2130837522;
    public static final int action_9 = 2130837523;

    public static final int back_0 = 2130837526;
    public static final int back_1 = 2130837527;
    public static final int back_10 = 2130837528;
    public static final int back_11 = 2130837529;
    public static final int back_12 = 2130837530;
    public static final int back_13 = 2130837531;
    public static final int back_14 = 2130837532;
    public static final int back_2 = 2130837533;
    public static final int back_3 = 2130837534;
    public static final int back_4 = 2130837535;
    public static final int back_5 = 2130837536;
    public static final int back_6 = 2130837537;
    public static final int back_7 = 2130837538;
    public static final int back_8 = 2130837539;
    public static final int back_9 = 2130837540;
    public static final int back_btn_bg_selector = 2130837541;
    public static final int bg = 2130837542;

    public static final int bus_line = 2130837544;
    public static final int bus_line_camera = 2130837545;
    public static final int bus_line_camera_left = 2130837546;
    public static final int camera = 2130837547;
    public static final int camera_camera = 2130837548;
    public static final int camera_camera_left = 2130837549;
    public static final int cameraicon = 2130837550;

    public static final int caricon = 2130837552;
    public static final int checkbox_selector = 2130837553;

    public static final int contingency_line = 2130837555;
    public static final int contingency_line_camera = 2130837556;
    public static final int contingency_line_camera_left = 2130837557;

    public static final int default_navi_land_bg_hud = 2130837559;
    public static final int default_navi_land_bg_normal = 2130837560;

    public static final int drive_way_line = 2130837563;
    public static final int electronic_eye = 2130837564;
    public static final int endpoint = 2130837565;
    public static final int exit = 2130837566;
    public static final int eye_navi_line = 2130837567;
    public static final int eye_navi_line_left = 2130837568;
    public static final int front_0 = 2130837569;
    public static final int front_1 = 2130837570;
    public static final int front_10 = 2130837571;
    public static final int front_11 = 2130837572;
    public static final int front_12 = 2130837573;
    public static final int front_13 = 2130837574;
    public static final int front_14 = 2130837575;
    public static final int front_15 = 2130837576;
    public static final int front_16 = 2130837577;
    public static final int front_17 = 2130837578;
    public static final int front_18 = 2130837579;
    public static final int front_19 = 2130837580;
    public static final int front_2 = 2130837581;
    public static final int front_20 = 2130837582;
    public static final int front_21 = 2130837583;
    public static final int front_22 = 2130837584;
    public static final int front_23 = 2130837585;
    public static final int front_24 = 2130837586;
    public static final int front_3 = 2130837587;
    public static final int front_4 = 2130837588;
    public static final int front_5 = 2130837589;
    public static final int front_6 = 2130837590;
    public static final int front_7 = 2130837591;
    public static final int front_8 = 2130837592;
    public static final int front_9 = 2130837593;

    public static final int gps_img_red = 2130837595;
    public static final int gps_img_white = 2130837596;
    public static final int high_bridge_above = 2130837597;
    public static final int high_bridge_bottom = 2130837598;
    public static final int hud_action_1 = 2130837599;
    public static final int hud_action_10 = 2130837600;
    public static final int hud_action_11 = 2130837601;
    public static final int hud_action_12 = 2130837602;
    public static final int hud_action_13 = 2130837603;
    public static final int hud_action_14 = 2130837604;
    public static final int hud_action_15 = 2130837605;
    public static final int hud_action_16 = 2130837606;
    public static final int hud_action_17 = 2130837607;
    public static final int hud_action_18 = 2130837608;
    public static final int hud_action_19 = 2130837609;
    public static final int hud_action_2 = 2130837610;
    public static final int hud_action_20 = 2130837611;
    public static final int hud_action_3 = 2130837612;
    public static final int hud_action_4 = 2130837613;
    public static final int hud_action_5 = 2130837614;
    public static final int hud_action_6 = 2130837615;
    public static final int hud_action_7 = 2130837616;
    public static final int hud_action_8 = 2130837617;
    public static final int hud_action_9 = 2130837618;
    public static final int hud_back_0 = 2130837619;
    public static final int hud_back_1 = 2130837620;
    public static final int hud_back_10 = 2130837621;
    public static final int hud_back_11 = 2130837622;
    public static final int hud_back_12 = 2130837623;
    public static final int hud_back_13 = 2130837624;
    public static final int hud_back_14 = 2130837625;
    public static final int hud_back_2 = 2130837626;
    public static final int hud_back_3 = 2130837627;
    public static final int hud_back_4 = 2130837628;
    public static final int hud_back_5 = 2130837629;
    public static final int hud_back_6 = 2130837630;
    public static final int hud_back_7 = 2130837631;
    public static final int hud_back_8 = 2130837632;
    public static final int hud_back_9 = 2130837633;
    public static final int hud_camera_distance = 2130837634;
    public static final int hud_cross_close_normal = 2130837635;
    public static final int hud_driveway_line = 2130837636;
    public static final int hud_front_0 = 2130837637;
    public static final int hud_front_1 = 2130837638;
    public static final int hud_front_10 = 2130837639;
    public static final int hud_front_11 = 2130837640;
    public static final int hud_front_12 = 2130837641;
    public static final int hud_front_13 = 2130837642;
    public static final int hud_front_14 = 2130837643;
    public static final int hud_front_15 = 2130837644;
    public static final int hud_front_16 = 2130837645;
    public static final int hud_front_17 = 2130837646;
    public static final int hud_front_18 = 2130837647;
    public static final int hud_front_19 = 2130837648;
    public static final int hud_front_2 = 2130837649;
    public static final int hud_front_20 = 2130837650;
    public static final int hud_front_21 = 2130837651;
    public static final int hud_front_22 = 2130837652;
    public static final int hud_front_23 = 2130837653;
    public static final int hud_front_24 = 2130837654;
    public static final int hud_front_3 = 2130837655;
    public static final int hud_front_4 = 2130837656;
    public static final int hud_front_5 = 2130837657;
    public static final int hud_front_6 = 2130837658;
    public static final int hud_front_7 = 2130837659;
    public static final int hud_front_8 = 2130837660;
    public static final int hud_front_9 = 2130837661;

    public static final int hud_speed_bg = 2130837664;
    public static final int ic_launcher = 2130837665;

    public static final int map_traffic = 2130837670;
    public static final int map_traffic_hl = 2130837671;

    public static final int nav_gps_progressbar_style = 0x7f0200af;
    public static final int navi_direction = 2130837685;
    public static final int navi_north = 2130837688;
    public static final int navi_over_data_yaw_icon = 2130837689;
    public static final int navi_popup = 2130837690;
    public static final int navi_up = 2130837692;
    public static final int navigation_close_black = 2130837697;
    public static final int draw_navigation_down_line = 2130837698;
    public static final int navigation_light_bg = 2130837699;
    public static final int peccancy_line = 2130837701;
    public static final int progress_bar_layer = 2130837708;
    public static final int road_auxiliary = 2130837709;
    public static final int road_main = 2130837710;
    public static final int service_station_img = 2130837711;
    public static final int set = 2130837712;
    public static final int speed_100_left = 2130837714;
    public static final int speed_100_right = 2130837715;
    public static final int speed_10_left = 2130837716;
    public static final int speed_10_right = 2130837717;
    public static final int speed_110_left = 2130837718;
    public static final int speed_110_right = 2130837719;
    public static final int speed_120_left = 2130837720;
    public static final int speed_120_right = 2130837721;
    public static final int speed_20_left = 2130837722;
    public static final int speed_20_right = 2130837723;
    public static final int speed_30_left = 2130837724;
    public static final int speed_30_right = 2130837725;
    public static final int speed_40_left = 2130837726;
    public static final int speed_40_right = 2130837727;
    public static final int speed_50_left = 2130837728;
    public static final int speed_50_right = 2130837729;
    public static final int speed_5_left = 2130837730;
    public static final int speed_5_right = 2130837731;
    public static final int speed_60_left = 2130837732;
    public static final int speed_60_right = 2130837733;
    public static final int speed_70_left = 2130837734;
    public static final int speed_70_right = 2130837735;
    public static final int speed_80_left = 2130837736;
    public static final int speed_80_right = 2130837737;
    public static final int speed_90_left = 2130837738;
    public static final int speed_90_right = 2130837739;
    public static final int speed_text = 2130837742;
    public static final int speed_text_left = 2130837743;
    public static final int startpoint = 2130837745;
    public static final int toll_station_img = 2130837746;
    public static final int traffic_light = 2130837747;
    public static final int traffic_lights_camera = 2130837748;
    public static final int traffic_lights_camera_left = 2130837749;
    public static final int waypoint = 2130837750;
    public static final int hudMirrosImage = 2131361817;
    public static final int baseView = 2131361896;
    public static final int bottom_dis_time_layout = 2131361839;
    public static final int bottom_info_layout = 2131361824;
    public static final int browser_navi_back = 2131361920;
    public static final int browser_navi_setting = 2131361932;
    public static final int btn_preview = 2131361902;
    public static final int btn_preview_land = 2131361915;
    public static final int btn_zoom_in = 2131361903;
    public static final int btn_zoom_out = 2131361904;
    public static final int camera_layout = 2131361833;
    public static final int car_way_tmc_layout = 2131361885;
    public static final int crossImage_layout = 2131361819;
    public static final int cross_progress = 2131361876;
    public static final int directionView = 2131361890;
    public static final int directionView_horizontal = 2131361887;
    public static final int dis_text_down = 2131361925;
    public static final int driveWayViewInNaviView = 2131361894;
    public static final int enlarge_road_layout = 2131361875;
    public static final int eye_speed_dis_text = 2131361893;
    public static final int eys_img = 2131361835;
    public static final int footbar_view_land = 2131361911;
    public static final int go_on_navi = 2131361857;
    public static final int gps_sum_text_land = 2131361855;
    public static final int gps_sum_text_port = 2131361874;
    public static final int hud_DistanceUnitText = 2131361826;
    public static final int hud_cross_progress = 2131361821;
    public static final int hud_driveWayViewInNaviView = 2131361822;
    public static final int hud_enlarge_road_layout = 2131361820;
    public static final int hud_eye_speed_dis_text = 2131361830;
    public static final int hud_nextActionText = 2131361837;
    public static final int hudmirrortitle = 2131361843;
    public static final int land_dis_and_time_layout = 2131361858;
    public static final int land_dis_text_down = 2131361859;
    public static final int land_info_layout = 2131361848;
    public static final int land_navi_dis_unit_text = 2131361850;
    public static final int land_navi_info_include = 2131361879;
    public static final int land_navi_next_action_text = 2131361852;
    public static final int land_port_distanceTimeText = 2131361861;
    public static final int land_port_nextRoadNameText = 2131361853;
    public static final int land_port_nextRoadSignDisText = 2131361849;
    public static final int land_port_restDistance = 2131361860;
    public static final int land_port_roadsign = 2131361851;
    public static final int left_eye_camera_img = 2131361829;
    public static final int left_eye_camera_layout = 2131361827;
    public static final int left_eye_dis_text = 2131361831;
    public static final int left_speed_text = 2131361828;
    public static final int limitSpeedTextView = 2131361834;
    public static final int map = 2131361883;
    public static final int mapview_relativeLayout = 2131361881;
    public static final int mid_info_layout = 2131361823;
    public static final int mode_up = 2131361889;
    public static final int naviRoadEnlargeView = 2131361933;
    public static final int navi_back_line = 2131361921;
    public static final int navi_container = 2131361877;
    public static final int navi_dis_unit_text = 2131361869;
    public static final int navi_exit_image_land = 2131361912;
    public static final int navi_next_action_text = 2131361870;
    public static final int navi_setting_image_land = 2131361917;
    public static final int navi_tmc_cursor = 2131361898;
    public static final int navi_widget_footer_linearlayout = 2131361919;
    public static final int navigation_down_line = 2131361931;
    public static final int navigation_go_on = 2131361930;
    public static final int nextRoadDistanceText = 2131361825;
    public static final int nextRoadNameText = 2131361838;
    public static final int port_curRoadName = 2131361928;
    public static final int port_cur_road_name_view = 2131361927;
    public static final int port_distanceTimeText = 2131361924;
    public static final int port_electronic_eye_img = 2131361891;
    public static final int port_leftwidget = 2131361864;
    public static final int port_limitSpeedText = 2131361892;
    public static final int port_nextRoadNameText = 2131361872;
    public static final int port_nextRoadSignDisText = 2131361868;
    public static final int port_next_name_layout = 2131361871;
    public static final int port_reset_navi_car_layout = 2131361929;
    public static final int port_restDistance = 2131361923;
    public static final int port_roadsign = 2131361865;
    public static final int port_show_naving_info = 2131361922;
    public static final int relativeLayout1 = 2131361882;
    public static final int restDistanceText = 2131361840;
    public static final int restDistanceTime = 2131361841;
    public static final int right_eye_dis_text = 2131361836;
    public static final int roadname_layout = 2131361867;
    public static final int roadsign_layout = 2131361863;
    public static final int roadsignimg = 2131361832;
    public static final int route_tmc = 2131361888;
    public static final int save = 2131361846;
    public static final int service_station = 2131361908;
    public static final int service_station_dis = 2131361910;
    public static final int service_station_name = 2131361909;
    public static final int switch_img = 2131361900;
    public static final int switch_img_relayout = 2131361899;
    public static final int time_text_down = 2131361926;
    public static final int title_btn_goback = 2131361844;
    public static final int title_name_text = 2131361845;
    public static final int tmc_layout = 2131361895;
    public static final int tmc_view_container = 2131361897;
    public static final int toll_station = 2131361905;
    public static final int toll_station_dis = 2131361907;
    public static final int toll_station_name = 2131361906;
    public static final int top_layout = 2131361818;
    public static final int top_line_view = 2131361866;
    public static final int top_navi_cross_land = 2131361880;
    public static final int top_navi_info = 2131361884;
    public static final int top_navi_info_layout = 2131361862;
    public static final int waitGpsHUDLy = 2131361842;
    public static final int waitGpsLandLy = 2131361854;
    public static final int waitGpsPortLy = 2131361873;
    public static final int zoom_in_land = 2131361914;
    public static final int zoom_out_land = 2131361916;
    public static final int zoom_view = 2131361901;
    public static final int zoom_view_land = 2131361913;
    public static final int sdk_hudlayout = 2130903050;
    public static final int sdk_hudlayout_land = 2130903051;
    public static final int sdk_land_navi_info_layout = 2130903052;
    public static final int sdk_navi_cross_layout = 2130903053;
    public static final int sdk_navi_fragment = 2130903054;
    public static final int sdk_road_enlarge_layout = 2130903055;

*/
}