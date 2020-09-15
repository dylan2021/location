package com.sfmap.library.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateTimeUtil {

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;

	/**
	 * 比较两个时间之差
	 * @param currentTime	当前时间
	 * @param oldTime		旧时间
     * @return 单位 分钟前,小时前,xx月xx日
     */
    public static String formatTime(long currentTime, long oldTime) {
		String strInterval = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(oldTime);
		long interval = currentTime - oldTime;

		if (interval <= MINUTE) {
			strInterval = "1分钟前";
		} else if (interval > MINUTE && interval <= HOUR) {
			strInterval = (interval / MINUTE) + "分钟前";
		} else if (interval >= HOUR && interval <= HOUR * 4) {
			strInterval = (interval / HOUR) + "小时前";
		} else if (interval > HOUR * 4) {
			// strInterval="很久以前";
			// strInterval = (interval / HOUR) + "小时前";
			strInterval = (calendar.get(Calendar.MONTH) + 1) + "月"
				+ (calendar.get(Calendar.DAY_OF_MONTH)) + "日";
		}
		return strInterval;
    }

	/**
	 * 获取当前时间
	 * @return 格式:yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTimeStamp() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return sdf.format(dt);
    }
	/**
	 * 获取当前时间
	 * @return 格式:yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTimeStampDetail() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(dt);
    }

	/**
	 * 根据格式返回当前时间
	 * @return 返回指定格式的时间
	 */
	public static String getTimeStampFormat(String format) {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(dt);
	}

    /**
     * 判断结束日期和开始日期的差是否大于等于一周
     * @param lastUpdateTime 最后更新时间
     * @return				 true大于一周否者反之
     */
    public static boolean isOutOf1Week(long lastUpdateTime) {
		long days = (System.currentTimeMillis() - lastUpdateTime)
			/ (1000 * 60 * 60 * 24);
		if (days >= 7) {
			return true;
		} else {
			return false;
		}
    }

    /**
     * 判断结束日期和开始日期的差是否大于等于一天
     * @param lastUpdateTime	最后更新时间
     * @return					true大于一天否者反之
     */
    public static boolean isOutOf1Day(long lastUpdateTime) {
		long days = (System.currentTimeMillis() - lastUpdateTime)
			/ (1000 * 60 * 60 * 24);
		if (days >= 1) {
			return true;
		} else {
			return false;
		}
    }

    /**
     * 获取日期和时间的字符串格式如下 yyyy-MM-dd HH:mm:ss.SSS 2014-02-25 11:03:25:798
     * @param date	Date的时间
     * @return		字符串格式的时间
     */
    public static String getDateTimeString(Date date) {
		SimpleDateFormat sFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
		return sFormat.format(date);
    }


    /**
     * 通过字符串获取发车间隔描述 如0510 5小时10分钟发一躺
     * @param s		时间字符串
     * @return		时间描述
     */
    public static String getInterval4String(String s) {
		if (s.length() != 4) {
			s = "";
		} else {
			String strHour = s.substring(0, 2);
			String strMin = s.substring(2, 4);

			int hour = Integer.parseInt(strHour);
			int min = Integer.parseInt(strMin);

			StringBuilder sbTime = new StringBuilder();
			if (hour != 0) {
			sbTime.append(hour + "小时");
			}
			if (min != 0) {
			sbTime.append(min + "分钟");
			}
			if (sbTime.length() != 0) {
			}
			s = sbTime.toString();
			if (s.length() != 0) {
			s = "发车间隔:" + s;
			}
		}
		return s;
    }

    private static String[] month = new String[] { "Jan.", "Feb.", "Mar.", "Apr.",
			"May", "Jun", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec." };

	/**
	 * 判断两个时间是否为同一时间
	 * @param dateTime1		时间1
	 * @param dateTime2		时间2
	 * @return				true是同一时间否者反之
	 */
    public static boolean isSameDay(String dateTime1, String dateTime2) {
		Date date1 = getDateTime(dateTime1);
		Date date2 = getDateTime(dateTime2);
		if (date1 == null || date2 == null) {
			return false;
		}
		if (date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前时间的Date
	 * @param dateTime		时间字符串
	 * @return				Date时间
     */
    public static Date getDateTime(String dateTime) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(dateTime);
		}
		 catch (ParseException e) {
			 e.printStackTrace();
		 }
		return date;
    }

    /**
     * 时间描述
     * @param mill	时间字符串
     * @return 格式:分钟后，小时后
     */
    public static String getHourString(long mill) {
		String string = "";
		int min = (int) (mill / 60);
		if (min < 60) {
			string = min + "分钟后";
		} else {
			string = min / 60 + "小时后";
		}
		return string;
    }

    /**
     * 根据线路长度取步行时间(步行速度按每秒钟一米计算)
     * @param length	距离
     * @return			时间描述
     */
    public static String getOnFootTimeStr(int length) {
		if (length <= 0) {
			return "";
		}
		int hour = length / 3600;
		// int minute = (n % 3600) / 60 +1;
		// bingchuan.gong 20130428 modify
		int minute = (length % 3600) / 60;
		int seconds = (length % 3600) % 60;
		String strHour = "";
		String strMinute = "";
		if (hour > 0) {
			strHour = hour + "小时";
		}
		if (seconds > 50) { // 如果超过50秒则分钟加1
			minute++;
		}
		if (minute > 0) {
			strMinute = minute + "分钟";
		} else {
			strMinute = "1分钟";
		}
		return strHour + strMinute;
    }

    /**
     * 获取时间字符串
     * 
     * @param second	时间大小
     * @return			时间描述
     */
    public static String getTimeStr(int second) {
		int minute = second / 60;
		String restTime = "";
		if (minute < 60) {
			if (minute == 0) {
			restTime = "<1分钟";
			} else {
			restTime = minute + "分钟";
			}
		} else {
			int hour = minute / 60;
			restTime = hour + "小时";
			minute = minute % 60;
			if (minute > 0) {
			restTime = restTime + minute + "分钟";
			}
		}
		return restTime;
    }
}
