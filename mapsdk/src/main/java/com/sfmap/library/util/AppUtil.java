package com.sfmap.library.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * app工具类
 */
public class AppUtil {

    /**
     * 隐藏虚拟键盘
     * @param context	上下文
     * @param layout	一个视图view对象
     */
    public static void hideVirtualKeyboard(Context context, View layout) {
		if (context == null || layout == null) {
			return;
		}

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(layout.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		}
    }

	public static void showInputMethod(final Context context, final View layout,int delay) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		   {
			   public void run()
			   {
				   InputMethodManager mInputMethodManager = (InputMethodManager) context
						   .getSystemService(Context.INPUT_METHOD_SERVICE);
				   mInputMethodManager.showSoftInput(layout, 0);
			   }
		   },
				delay);
	}

	public static void hideInputMethod(Context context, View layout) {
		InputMethodManager mImm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mImm.hideSoftInputFromWindow(layout.getApplicationWindowToken(), 0);
	}

    /**
     * 字符串编码
     * @param str	字符串
     * @return		字符数组
     */
    public static byte[] encode(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		byte[] byteArray = null;
		try {
			String utf = URLEncoder.encode(str, "UTF-8");
			byteArray = utf.getBytes("UTF-8");
			for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = (byte) (byteArray[i] + 16);
			}
		}
		catch (Exception e) {
			CatchExceptionUtil.normalPrintStackTrace(e);
		}

		return byteArray;
    }

	/**
	 * 通过context获取app版本名称
	 * @param context	上下文
	 * @return			字符串
     */
    public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// Get the package information
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context
				.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
			return "";
			}
		}
		catch (Exception ex) {
			CatchExceptionUtil.normalPrintStackTrace(ex);
		}
		return versionName;
    }

	/**
	 * 通过context获取app版本号
	 * @param context	上下文
	 * @return			版本号
     */
    public static int getAppVersionCode(Context context) {
		int versionCode = 0;
			try {
				// Get the package information
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				versionCode = pi.versionCode;
			}
			catch (Exception ex) {
			 CatchExceptionUtil.normalPrintStackTrace(ex);
			}
		return versionCode;
    }

	/**
	 * 判断版本是否为 beta版本
	 * @param context	字符串
	 * @return			true是beta版本否者反之
     */
    public static boolean isBetaVersion(Context context) {
		String vName = getAppVersionName(context);
		return vName.toLowerCase().contains("beta");
    }

	/**
	 *  根据路径查看app是否安装
	 * @param context	上下文
	 * @param uri		uri路径
     * @return			true安装否者反之
     */
	public static boolean isAppInstalled(Context context,String uri) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	/**
	 * data/data目录下获取安装apk的权限
	 * @param file	文件file
	 * @return		权限等级
	 */
	public static int permation(File file) {
		try {
			Process p = Runtime.getRuntime().exec("chmod 777 " + file);
			int status = p.waitFor();
			return status;
		} catch (IOException e) {
			CatchExceptionUtil
					.normalPrintStackTrace(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 获得SD卡总大小
	 *
	 * @param context	上下文
	 * @return			SD卡大小
	 */
	public static String getSDTotalSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 *
	 * @param context	上下文
	 * @return			SD卡剩余容量
	 */
	public static String getSDAvailableSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 获得机身内存总大小
	 *
	 * @param context	上下文
	 * @return			内存大小
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身可用内存
	 *
	 * @param context	上下文
	 * @return			可用内存大小
	 */
	public static String getRomAvailableSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 获取内置SD路径
	 *
	 * @param context	上下文
	 * @return			SD路径
	 */
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getExternalStroragePath(Context context) {
		Class<?>[] parameterTypes = null;
		Object[] objects = null;
		int currentapiVersion = Build.VERSION.SDK_INT;
		if (currentapiVersion >= 12) {// 12 is HONEYCOMB_MR1
			try {
				StorageManager manager = (StorageManager) context
						.getSystemService(Context.STORAGE_SERVICE);
				/************** StorageManager的方法 ***********************/
				Method getVolumeList = StorageManager.class.getMethod(
						"getVolumeList", parameterTypes);
				Method getVolumeState = StorageManager.class.getMethod(
						"getVolumeState", String.class);

				Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
						objects);
				String state = null;
				String path = null;
				Boolean isRemove = false;
				String sdPath = "";
				String innerPath = "";
				String sdState = "";
				String innerState = "";
				String storageDir = null;
				for (Object volume : Volumes) {
					/************** StorageVolume的方法 ***********************/
					Method getPath = volume.getClass().getMethod("getPath",
							parameterTypes);
					Method isRemovable = volume.getClass().getMethod(
							"isRemovable", parameterTypes);
					path = (String) getPath.invoke(volume, objects);

					state = (String) getVolumeState.invoke(manager,
							getPath.invoke(volume, objects));
					isRemove = (Boolean) isRemovable.invoke(volume, objects);

					// 三星S5存储卡分区问题
					if (path.toLowerCase().contains("private")) {
						continue;
					}
					if (isRemove) {
						sdPath = path;
						sdState = state;
						// 如果sd卡路径存在
						if (null != sdPath && null != sdState
								&& sdState.equals(Environment.MEDIA_MOUNTED)) {

							if (currentapiVersion <= 18) {
								storageDir = sdPath;
							} else {
								try {
									File[] files = context
											.getExternalFilesDirs(null);
									if (files != null) {
										if (files.length > 1 && null != files[1])
											storageDir = files[1]
													.getAbsolutePath();
										else
											storageDir = path;
									}
								} catch (Exception ex) {
									// 此处保护java.lang.NoSuchMethodError:
									// android.content.Context.getExternalFilesDirs
									storageDir = sdPath;
								}
							}
							break;
						}
					} else {
						innerPath = path;
						innerState = state;
					}
				}

				// 如果sd卡路径为null,检测内部存储空间
				if (currentapiVersion <= 18) {// 18 is JELLY_BEAN_MR2
					if (null == storageDir && null != innerPath) {
						if (null != innerState
								&& innerState.equals(Environment.MEDIA_MOUNTED)) {
							storageDir = innerPath;
						}
					}
					return storageDir;
				} else {// 4.4以上系统有限内部存储卡
					if (null != innerPath) {
						if (null != innerState
								&& innerState.equals(Environment.MEDIA_MOUNTED)) {
							storageDir = innerPath;
						}
					}
					return storageDir;
				}
			} catch (Exception e) {
			}
		}

		{
			// 得到存储卡路径
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED); // 判断sd卡
			// 或可存储空间是否存在
			if (sdCardExist) {
				sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
				return sdDir.toString();
			}

			return null;
		}
	}

	/**
	 * 检查gps权限
	 * @param context	上下文
	 * @return			true有gps权限否者反之
     */
	public static boolean getGpsStatus(Context context) {
		try {
			LocationManager lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if(lm == null){
				return false;
			}
//			boolean test = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (java.lang.SecurityException e) {
			return false;
		}
	}

	/**
	 * @ note 跳转到指定安装程序的卸载页面
	 * @param activity		一个activity实例
	 * @param strPackageName 指定包名
     */
	@SuppressLint("InlinedApi")
	public static void startUnInstallDetailPage(Activity activity, String strPackageName) {
		// 打开卸载页面
		if (TextUtils.isEmpty(strPackageName)) {
			return;
		}
		try {
			Intent intent = new Intent(
					Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + strPackageName));
			activity.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取Rom版本
	 * @param propName	版本包名
	 * @return	返回一个Rom版本
     */
	public static String getSystemPropertyROMVersion(String propName) {
		// 获取MIUI版本号 getSystemProperty("ro.miui.ui.version.name")
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			Log.e("Utils.getSystemProperty", "Unable to read sysprop "
					+ propName, ex);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Log.e("Utils.getSystemProperty",
							"Exception while closing InputStream", e);
				}
			}
		}
		return line;
	}

	/**
	 * 判断android版本是否为 Froyo
	 * @return	true是否者反之
     */
	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * 判断android版本是否为 GINGERBREAD
	 * @return	true是否者反之
     */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * 判断android版本是否为 HONEYCOMB
	 * @return	true是否者反之
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * 判断android版本是否为 HONEYCOMB_MR1
	 * @return	true是否者反之
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * 判断android版本是否为 JELLY_BEAN
	 * @return	true是否者反之
	 */
	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	/**
	 * 判断android版本是否为 JELLY_BEAN_MR1
	 * @return	true是否者反之
	 */
	public static boolean hasJellyBeamMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	/**
	 * 判断是否是小米系统
	 * @return
	 *        如果硬件是小米手机,但是刷了别的rom,则有可能不能识别
	 */
	public static boolean isXiaomiOs() {
		return Build.BRAND.toLowerCase().contains("xiaomi") ||
				Build.MANUFACTURER.toLowerCase().contains("xiaomi");

	}

	/**
	 * 获取设备的utd id
	 * @param context	上下文
	 * @return			返回utd id
     */
//	public static String getUtdid(Context context) {
//		return com.ta.utdid2.device.UTDevice.getUtdid(context);
//	}

	/**
	 * 返回当前屏幕状态横屏，竖屏
	 * @return boolean true，横屏  false,竖屏
	 */
	public static boolean isOrientationLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

}
