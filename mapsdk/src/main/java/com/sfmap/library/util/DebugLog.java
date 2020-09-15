package com.sfmap.library.util;


import android.util.Log;

/**
 * 调试
 */
public class DebugLog {

	private static Boolean released = null;
	private static final int releaseFinger = 13686;// debug 6717

	private static int logLevel = Log.VERBOSE;

	private static String generateTag(StackTraceElement caller) {
		//String tag = "%s.%s(L:%d)";
		String callerClazzName = caller.getClassName();
		String tag = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		///tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		//tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
		return tag.length() > 23 ? tag.substring(0, 20) + "..." : tag;
	}

	public static void trace(Object msg, Throwable th) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.VERBOSE, tag, msg, th);
	}

	public static void trace(Object msg) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.VERBOSE, tag, msg, null);
	}

	public static void debug(Object msg, Throwable th) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.DEBUG, tag, msg, th);
	}

	public static void debug(Object msg) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.DEBUG, tag, msg, null);
	}

	public static void info(Object msg, Throwable th) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.INFO, tag, msg, th);
	}

	public static void info(Object msg) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.INFO, tag, msg, null);
	}

	public static void warn(Object msg, Throwable th) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.WARN, tag, msg, th);
	}

	public static void warn(Object msg) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.WARN, tag, msg, null);
	}

	public static void error(Object msg, Throwable th) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.ERROR, tag, msg, th);
	}

	public static void error(Object msg) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.ERROR, tag, msg, null);
	}

	public static void fatal(Object msg, Throwable th) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.ASSERT, tag, msg, th);
	}

	public static void fatal(Object msg) {
		//if (!isDebug()) return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		log(Log.ASSERT, tag, msg, null);
	}

	public static void timeStart() {
		latestStart = System.nanoTime();
	}

	public static void timeEnd(String label) {
		long start = System.nanoTime();
		long offset = start - latestStart;
		latestStart = start;
		System.out.println(formatTime(label, offset));
	}

	private static String formatTime(String label, long offset) {
		if (offset > NS_PER_S) {
			return "time used @" + label + ":" + (offset / (float) NS_PER_S)
				+ " s";
		} else if (offset > NS_PER_MS) {
			return "time used @" + label + ":" + (offset / (float) NS_PER_MS)
				+ " ms";
		}
		return "time used @" + label + ":" + offset + " ns";
	}

	private static long latestStart = -1;
	private static final int NS_PER_MS = 1000000;
	private static final int NS_PER_S = 1000000000;


	private static void log(int level, String tag, Object msg, Throwable th) {
		//if (!isDebug()) return;
		if (level < logLevel) return;
		try {
			if (Log.isLoggable(tag, level) || true) {
				String text;
				if (th == null) {
					if (msg instanceof Throwable) {
						text = String.valueOf(msg) + '\n'
							+ Log.getStackTraceString((Throwable) msg);
					} else {
						text = String.valueOf(msg);
					}
				} else {
					text = String.valueOf(msg) + '\n' + Log.getStackTraceString(th);
				}
				Log.println(level, tag, text);
				// System.out.println(tag + ':' + text);

			}
		} catch (Throwable e) {
			System.out.println(tag + "\t" + msg + "\t" + th);
		}
	}

	private static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}


}
