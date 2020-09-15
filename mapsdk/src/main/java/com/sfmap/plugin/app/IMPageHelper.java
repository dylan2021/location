package com.sfmap.plugin.app;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.IMPluginManager;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.core.ctx.ContextProxy;
import com.sfmap.plugin.exception.StartActivityException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @date: 2014/11/24
 */
public class IMPageHelper extends Activity {
	private IMPageHelper() {
	}

	// 页面信息
	public final static String PAGE_INFO_CMD = "getPageInfo";
	public final static String PAGE_INFO_KEY = "pageInfo";
	public final static String PAGE_CLASS_KEY = "pageClass";

	// action和约定
	public final static String ACTION_PREFIX = "sfmap."; // 插件的action前缀
	public final static String ACTION_HOST_ACTIVITY = "action.plugin.Activity";
	public final static String HOST_ACTIVITY = "com.sfmap.plugin.app.IMActivity";

	// 记录目标Activity
	private final static String TARGET_ACTIVITY_PREF = "TARGET_ACTIVITY_PREF";
	private final static String TARGET_ACTIVITY_CLASS_KEY = "TARGET_ACTIVITY_CLASS_KEY";

	private static WeakReference<Activity> lastActivityRef = null;
	private static final HashMap<String, IntentRedirector> intentRedirectorMap = new HashMap<String, IntentRedirector>(1);

	/**
	 * 返回最近一次打开的插件activity
	 *
	 * @return
	 */
	public static Activity getLastActivity() {
		return lastActivityRef == null ? null : lastActivityRef.get();
	}

	public static void setLastActivity(Activity lastActivity) {
		lastActivityRef = new WeakReference<Activity>(lastActivity);
	}

	public static void putIntentRedirector(String key, IntentRedirector redirector) {
		synchronized (intentRedirectorMap) {
			intentRedirectorMap.put(key, redirector);
		}
	}

	public static int getThemeResId(Activity activity, ContextProxy contextProxy) {
		HashMap<String, ActivityInfo> pageMap = contextProxy.getPlugin().getConfig().getPageMap();
		if (pageMap != null) {
			ActivityInfo info = pageMap.get(activity.getClass().getName());
			if (info != null) {
				return info.theme;
			}
		}
		return 0;
	}

	public static void initTitleBar(Activity activity, ContextProxy contextProxy) {
		HashMap<String, ActivityInfo> pageMap = contextProxy.getPlugin().getConfig().getPageMap();
		if (pageMap != null) {
			ActivityInfo info = pageMap.get(activity.getClass().getName());
			if (info != null) {
				boolean hasActionBar = false;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					ActionBar actionBar = activity.getActionBar();
					if (actionBar != null) {
						hasActionBar = true;
						if (info.labelRes > 0) {
							actionBar.setTitle(info.labelRes);
						} else if (info.nonLocalizedLabel != null && info.nonLocalizedLabel.length() > 0) {
							activity.setTitle(info.nonLocalizedLabel);
						}
						if (info.icon > 0) {
							actionBar.setIcon(info.icon);
						}
					}
				}

				if (!hasActionBar) {
					if (info.labelRes > 0) {
						activity.setTitle(info.labelRes);
					} else if (info.nonLocalizedLabel != null && info.nonLocalizedLabel.length() > 0) {
						activity.setTitle(info.nonLocalizedLabel);
					}
				}
			}
		}
	}

	private static Class<?> targetActivityClass;

	public static Class<?> getTargetActivityClass() throws ClassNotFoundException {
		if (targetActivityClass == null) {
			SharedPreferences pref = IMPluginManager.getApplication().getSharedPreferences(TARGET_ACTIVITY_PREF, 0);
			String targetActivityClassName = pref.getString(TARGET_ACTIVITY_CLASS_KEY, null);
			if (targetActivityClassName != null) {
				targetActivityClass = Class.forName(targetActivityClassName);
			}
		}
		return targetActivityClass;
	}

	@SuppressLint("NewApi")
	private static void setTargetActivityClass(Class<?> targetActivityClass) {
		IMPageHelper.targetActivityClass = targetActivityClass;
		SharedPreferences pref = IMPluginManager.getApplication().getSharedPreferences(TARGET_ACTIVITY_PREF, 0);
		SharedPreferences.Editor editor = pref.edit().putString(TARGET_ACTIVITY_CLASS_KEY, targetActivityClass.getName());
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public static void startActivity(final Intent intent, final Runnable callSuper) {

		synchronized (intentRedirectorMap) {
			if (intentRedirectorMap.size() > 0) {
				for (IntentRedirector redirector : intentRedirectorMap.values()) {
					redirector.redirect(intent);
				}
			}
		}

		String targetAction = intent.getAction();
		if (targetAction != null && targetAction.startsWith(IMPageHelper.ACTION_PREFIX)) {
			intent.setAction(IMPageHelper.ACTION_HOST_ACTIVITY);
			IMPluginMsg msg = new IMPluginMsg(intent.getPackage(), IMPageHelper.PAGE_INFO_CMD);
			msg.put("action", targetAction);
			intent.setPackage(IMPluginManager.getApplication().getPackageName());
			IMPluginManager.sendMsg(msg, new MsgCallback() {
				@Override
				public void callback(Map<String, Object> result) {
					Class<?> activityClass = (Class<?>) result.get(IMPageHelper.PAGE_CLASS_KEY);
					if (activityClass != null) {
						IMPageHelper.setTargetActivityClass(activityClass);
						callSuper.run();
					}
				}

				@Override
				public void error(Throwable ex, boolean isCallbackError) {
					throw new StartActivityException(intent.toString(), ex);
				}
			});
		} else {
			callSuper.run();
		}
	}

	public interface IntentRedirector {
		void redirect(Intent intent);
	}
}
