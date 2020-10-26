package com.sunfintech.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * sunlink日期工具类
 * @author wuhuan 2018-08-08
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	/**
	 * 日期格式
	 */
	public static String HHmmssSSS = "HHmmssSSS";
	public static String pattern_d = "yyyy-MM-dd";
	public static String pattern_t = "HH:mm:ss";
	public static String pattern_t_nodivide = "HHmmSS";
	public static String pattern_full = "yyyy-MM-dd HH:mm:ss";
	public static String pattern_full_S = "yyyy-MM-dd HH:mm:ss.S";
	public static String pattern_full_divide = "yyyy/MM/dd HH:mm:ss";
	public static String pattern_divide = "yyyy/MM/dd";
	public static String pattern_full_divide_S = "yyyy/MM/dd HH:mm:ss.S";
	public static String pattern_yyyy = "yyyy";
	public static String pattern_yyyyMM = "yyyyMM";
	public static String pattern_yyyyMMdd = "yyyyMMdd";
	public static String pattern_yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static String pattern_yyyyMMddHHmmssS = "yyyyMMddHHmmssS";

	public static SimpleDateFormat shortDateFormat = new SimpleDateFormat(pattern_d);
	public static SimpleDateFormat shortTimeFormat = new SimpleDateFormat(pattern_t);
	public static SimpleDateFormat shortTimeNoDivideFormat = new SimpleDateFormat(pattern_t_nodivide);
	public static SimpleDateFormat fullDateFormat = new SimpleDateFormat(pattern_full);
	public static SimpleDateFormat yyyyDateFormat = new SimpleDateFormat(pattern_yyyy);
	public static SimpleDateFormat yyyyMMDateFormat = new SimpleDateFormat(pattern_yyyyMM);
	public static SimpleDateFormat yyyyMMddDateFormat = new SimpleDateFormat(pattern_yyyyMMdd);
	public static SimpleDateFormat HHmmssSSSFormat = new SimpleDateFormat(HHmmssSSS);
	public static SimpleDateFormat yyyyMMddHHmmssFormat = new SimpleDateFormat(pattern_yyyyMMddHHmmss);
	public static SimpleDateFormat yyyyMMddHHmmssSFormat = new SimpleDateFormat(pattern_yyyyMMddHHmmssS);
	public static SimpleDateFormat shortDivideFormat = new SimpleDateFormat(pattern_divide);

	/**
	 * Timestamp类型当前时间
	 */
	public static Timestamp now() {
		return getTimestamp(System.currentTimeMillis());
	}

	/**
	 * 返回Timestamp类型指定时间
	 * 
	 * @param currentTime
	 *            毫秒数
	 */
	public static Timestamp getTimestamp(long currentTime) {
		return new java.sql.Timestamp(currentTime);
	}

	/**
	 * Date类型当前时间
	 */
	public static Date dateNow() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}

	/**
	 * 将yyyyMMdd格式的日期字符串转换为Date
	 */
	public static synchronized Date getDateyyyyMMdd(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				return yyyyMMddDateFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 返回yyyy/MM/dd格式的当前时间
	 */
	public static synchronized String getCurrentDateSlash() {
		return shortDivideFormat.format(new Date());
	}

	/**
	 * 将HHmmSS格式的日期字符串转换为Date
	 */
	public static synchronized Date getDateHHmmSS(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				return shortTimeNoDivideFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将yyyyMMddHHmmss格式的日期字符串转换为Date
	 */
	public static synchronized Date getDateyyyyMMddHHmmss(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				return yyyyMMddHHmmssFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将yyyyMM格式的日期字符串转换为Date
	 */
	public static synchronized Date getDateyyyyMM(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				return yyyyMMDateFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将yyyy-MM-dd格式的日期字符串转换为Date
	 */
	public static synchronized Date getDateShort(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				return shortDateFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将yyyy-MM-dd HH:mm:ss格式的日期字符串转换为Date
	 */
	public static synchronized Date getDateFullDateFormat(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				return fullDateFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将yyyy/MM/dd格式的日期字符串转换为Date
	 */
	public static synchronized Date getShortDivideDate(String date) {
		if (StringUtils.isNotBlank(date)) {
			try {
				Date result = shortDivideFormat.parse(date);
				return result;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 当前时间转为HHmmSS的字符串
	 */
	public static synchronized String getShortTimeNoDivide() {
		return shortTimeNoDivideFormat.format(new Date());
	}

	/**
	 * 当前时间转为yyyyMMdd的字符串
	 */
	public static synchronized String getCurrentTimeyyyyMMdd() {
		return yyyyMMddDateFormat.format(new Date());
	}

	/**
	 * 当前时间转为yyyyMM的字符串
	 */
	public static synchronized String dateToyyyyMM() {
		return yyyyMMDateFormat.format(new Date());
	}

	/**
	 * 当前时间转为yyyy的字符串
	 */
	public static synchronized String getCurrentTimeyyyy() {
		return yyyyDateFormat.format(new Date());
	}

	/**
	 * 当前时间转为yyyy-MM-dd HH:mm:ss的字符串
	 */
	public static synchronized String getCurrentTimefullDateFormat() {
		return fullDateFormat.format(new Date());
	}

	/**
	 * 当前时间转为HHmmssSSS的字符串
	 */
	public static synchronized String getHHmmssSSSFormatDateFormat() {
		return HHmmssSSSFormat.format(new Date());
	}

	/**
	 * 当前时间转为yyyyMMddHHmmss的字符串(精确到秒)
	 */
	public static synchronized String getyyyyMMddHHmmssFormatDateFormat() {
		return yyyyMMddHHmmssFormat.format(new Date());
	}

	/**
	 * 当前时间转为yyyyMMddHHmmssS的字符串(精确到毫秒)
	 */
	public static synchronized String getTimestampFormatDateFormat() {
		return yyyyMMddHHmmssSFormat.format(new Date());
	}

	/**
	 * 将所给日期转换为时间(yyyy-MM-dd)字符串
	 * @param source 要转换的日期对象
	 * @return 时间字符串
	 */
	public static synchronized String dateToShort(Date source) {
		if (source != null) {
			try {
				String result = shortDateFormat.format(source);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将所给日期转换为时间(yyyyMM)字符串
	 * @param source 要转换的日期对象
	 * @return 时间字符串
	 */
	public static synchronized String dateToyyyyMM(Date source) {
		if (source != null) {
			try {
				String result = yyyyMMDateFormat.format(source);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将所给日期转换为时间(yyyyMMdd)字符串
	 * @param source 要转换的日期对象
	 * @return 时间字符串
	 */
	public static synchronized String dateToyyyyMMdd(Date source) {
		if (source != null) {
			try {
				String result = yyyyMMddDateFormat.format(source);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将所给日期转换为时间(yyyy-MM-dd HH:mm:ss)字符串
	 * @param source 要转换的日期对象
	 * @return 时间字符串
	 */
	public static synchronized String dateToFull(Date source) {
		if (source != null) {
			try {
				String result = fullDateFormat.format(source);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 日期加d天m月
	 * @param date 待计算日期
	 * @param m 添加的月数
	 * @param d 添加的天数
	 * @return 加后的日期
	 */
	public static Date addDay(Date date, int m, int d) {
		if (date != null) {
			try {
				Calendar cd = Calendar.getInstance();
				cd.setTime(date);
				cd.add(Calendar.DATE, d);// 添加一天
				cd.add(Calendar.MONTH, m);// 添加一个月
				return cd.getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * get星期几
	 * @param date 待计算的日期
	 * @return 1~7分别表示周一到周日
	 */
	public static int getWeekday(Date date) {
		if (date != null) {
			try {
				Calendar gc = Calendar.getInstance();
				gc.setTime(date);
				int week = gc.get(Calendar.DAY_OF_WEEK);// "","星期日","星期一","星期二","星期三","星期四","星期五","星期六"
				week--;
				if (week == 0) {
					week = 7;
				}
				return week;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * 是否为工作日
	 * @param day 待计算的日期
	 */
	public static Boolean isWorkingDay(Date day) {
		if (day == null) {
			return null;
		}
		try {
			int week = DateUtils.getWeekday(day);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(day);
			// 周末跳过
			if (week > 5) {
				return false;
			}
			// 1.1跳过
			if (calendar.get(Calendar.DAY_OF_WEEK) == 0 && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
				return false;
			}
			// 5.1跳过
			if (calendar.get(Calendar.MONTH) == 4
					&& (calendar.get(Calendar.DAY_OF_MONTH) >= 1 && calendar.get(Calendar.DAY_OF_MONTH) <= 3)) {
				return false;
			}
			// 10.1跳过
			if (calendar.get(Calendar.MONTH) == 9
					&& (calendar.get(Calendar.DAY_OF_MONTH) >= 1 && calendar.get(Calendar.DAY_OF_MONTH) <= 7)) {
				return false;
			}
			Date d = LunarDateUtils.solarTolunar(day);
			calendar.setTime(d);
			if (calendar.get(Calendar.MONTH) == 0
					&& (calendar.get(Calendar.DAY_OF_MONTH) >= 1 && calendar.get(Calendar.DAY_OF_MONTH) <= 7)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 计算-字符串yyyy-MM-dd的日期格式,相差几天, 参数2 - 参数1
	 * @return bdate - smdate
	 */
	public static int daysBetween(String smdate, String bdate) {
		if (!Objects.equals(smdate, bdate)) {
			try {
				long betweenDays = 0;
				if (smdate == null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(shortDateFormat.parse(bdate));
					long time = cal.getTimeInMillis();
					betweenDays = time / (1000 * 3600 * 24);
				} else if (bdate == null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(shortDateFormat.parse(smdate));
					long time = cal.getTimeInMillis();
					betweenDays = time / (1000 * 3600 * 24);
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat(pattern_d);
					Calendar cal = Calendar.getInstance();
					cal.setTime(sdf.parse(smdate));
					long time1 = cal.getTimeInMillis();
					cal.setTime(sdf.parse(bdate));
					long time2 = cal.getTimeInMillis();
					betweenDays = (time2 - time1) / (1000 * 3600 * 24);
				}
				return Integer.parseInt(String.valueOf(betweenDays));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 日期加减n秒
	 */
	public static Date addSecond(Calendar calendar, int second) {
		if (calendar != null) {
			try {
				calendar.add(Calendar.SECOND, second);
				return calendar.getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 判断两个日期是否大于一年
	 * @param start 大于返回true
	 * @param end
	 * @return
	 */
	public static boolean dateLongThanOneYear(Date start, Date end) {
		if (!Objects.equals(start, end)) {
			try {
				start = dateAddYear(start, 1);
				if (start.compareTo(end) < 0) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 日期加n年
	 */
	public static Date dateAddYear(Date date, int n) {
		if (date != null) {
			try {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				// 把日期往后增加一年.整数往后推,负数往前移动
				calendar.add(Calendar.YEAR, n);
				date = calendar.getTime();
				return date;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 日期加n月
	 */
	public static Date dateAddMonth(Date date, int n) {
		if (date != null) {
			try {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				// 把日期往后增加一年.整数往后推,负数往前移动
				calendar.add(Calendar.MONTH, n);
				date = calendar.getTime();
				return date;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 测试
	 */
	public static void main(String[] args) {
		System.out.println(dateToyyyyMMdd(addDay(new Date(), 0, -1)));
	}
}