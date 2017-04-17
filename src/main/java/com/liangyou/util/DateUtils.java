package com.liangyou.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class DateUtils {
	private static Logger logger = Logger.getLogger(DateUtils.class);
	public static String dateStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日 hh:mm");
		String str = format.format(date);
		return str;
	}
	
	public static String dateStr(Date date,String f) {
		SimpleDateFormat format = new SimpleDateFormat(f);
		String str = format.format(date);
		return str;
	}

	public static String dateStr2(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
	}
	public static String newdateStr2(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String str = format.format(date);
		return str;
	}
	
	public static String newdateStr6(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		String str = format.format(date);
		return str;
	}
	
	public static String newdateStr3(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		String str = format.format(date);
		return str;
	}

	public static String dateStr3(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = format.format(date);
		return str;
	}

	public static String dateStr4(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;

	}
	
	public static String dateStr5(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String str = format.format(date);
		return str;

	}
	
	public static Date getDate(String time,String formatStr){
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		try {
			return format.parse(time);
		} catch (ParseException e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 将秒转换成时间
	 * @param times
	 * @return
	 */
	public static Date getDate(String times) {
		long time = Long.parseLong(times);
		return new Date(time*1000);
	}

	public static String dateStr(String times) {
		return dateStr(getDate(times));
	}
	public static String dateStr2(String times) {
		return dateStr2(getDate(times));
	}
	public static String dateStr3(String times) {
		return dateStr3(getDate(times));
	}
	public static String dateStr4(String times) {
		return dateStr4(getDate(times));
	}
	public static long getTime(Date date) {
		return date.getTime() / 1000;
	}

	public static int getDay(Date d){
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * s - 表示 "yyyy-mm-dd" 形式的日期的 String 对象 
	 * @param f
	 * @return
	 */
	public static Date valueOf(String s){
		final int YEAR_LENGTH = 4;
        final int MONTH_LENGTH = 2;
        final int DAY_LENGTH = 2;
        final int MAX_MONTH = 12;
        final int MAX_DAY = 31;
        int firstDash;
        int secondDash;
        Date d = null;

        if (s == null) {
            throw new java.lang.IllegalArgumentException();
        }

        firstDash = s.indexOf('-');
        secondDash = s.indexOf('-', firstDash + 1);
        if ((firstDash > 0) && (secondDash > 0) && (secondDash < s.length()-1)) {
            String yyyy = s.substring(0, firstDash);
            String mm = s.substring(firstDash + 1, secondDash);
            String dd = s.substring(secondDash + 1);
            if (yyyy.length() == YEAR_LENGTH && mm.length() == MONTH_LENGTH &&
                dd.length() == DAY_LENGTH) {
                int year = Integer.parseInt(yyyy);
                int month = Integer.parseInt(mm);
                int day = Integer.parseInt(dd);
                if (month >= 1 && month <= MAX_MONTH) {
                    int maxDays = MAX_DAY;
                    switch (month) {
                        // February determine if a leap year or not
                        case 2:
                            if((year % 4 == 0 && !(year % 100 == 0)) || (year % 400 == 0)) {
                                maxDays = MAX_DAY-2; // leap year so 29 days in February
                            } else {
                                maxDays = MAX_DAY-3; //  not a leap year so 28 days in February 
                            }
                            break;
                        // April, June, Sept, Nov 30 day months
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            maxDays = MAX_DAY-1;
                            break;
                    }
                    if (day >= 1 && day <= maxDays) {
                        Calendar cal=Calendar.getInstance();
                        cal.set(year, month - 1, day,0,0,0);
                        cal.set(Calendar.MILLISECOND, 0);
                        d=cal.getTime();
                    }
                }
            }
        }
        if (d == null) {
            throw new java.lang.IllegalArgumentException();
        }
        return d;
	}
	
	/**
	 * @author lijie
	 * @param Begin
	 * @param end
	 * 传入开始时间 和 结束时间 格式如：2012-09-07
	 * @return
	 * 返回Map  获取相隔多少年 get("YEAR")及为俩个时间年只差，月 天，类推
	 *  Key ：
	 *  YEAR MONTH DAY
	 *  如果开始时间 晚于 结束时间 return null；
	 */
	
	public static Map<String,Integer> getApartTime(String Begin, String end) {
		  String[] temp = Begin.split("-");
	        String[] temp2 = end.split("-");
	        if (temp.length > 1 && temp2.length > 1) {
	            Calendar ends = Calendar.getInstance();
	            Calendar begin = Calendar.getInstance();

	            begin.set(NumberUtils.getInt(temp[0]),
	                    NumberUtils.getInt(temp[1]), NumberUtils.getInt(temp[2]));
	            ends.set(NumberUtils.getInt(temp2[0]),
	                    NumberUtils.getInt(temp2[1]), NumberUtils.getInt(temp2[2]));
	            if (begin.compareTo(ends) <= 0) {
	                Map<String,Integer> map = new HashMap<String, Integer>();
	                ends.add(Calendar.YEAR, -NumberUtils.getInt(temp[0]));
	                ends.add(Calendar.MONTH, -NumberUtils.getInt(temp[1]));
	                ends.add(Calendar.DATE, -NumberUtils.getInt(temp[2]));
	                map.put("YEAR", ends.get(Calendar.YEAR));
	                map.put("MONTH", ends.get(Calendar.MONTH) + 1);
	                map.put("DAY", ends.get(Calendar.DATE));
	                return map;
	            }
	        }
	        return null;
	}
	/**add by lijing 
	 * 传入两个日期 ,计算两个日期相差的天数,精确到毫秒!
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int getIntervalDays(Date begin, Date end) {

	       if (null == begin || null == end) {

	           return -1;

	       }

	       long intervalMilli = end.getTime() - begin.getTime();

	       return (int) (intervalMilli / (24 * 60 * 60 * 1000));

	}
	
	public static Date rollDay(Date d,int day){
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}
	public static Date rollMon(Date d,int mon){
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONTH, mon);
		return cal.getTime();
	}
	public static Date rollYear(Date d,int year){
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.YEAR, year);
		return cal.getTime();
	}
	public static Date rollDate(Date d,int year,int mon,int day){
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, mon);
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}
	
	
	/**
	 * 获取当前时间的秒数字符串
	 * @return
	 */
	public static String getNowTimeStr(){
		String str=Long.toString(System.currentTimeMillis() / 1000);
		return str;
	}
	public static String getTimeStr(Date time){
		long date = time.getTime();
		String str=Long.toString(date / 1000);
		return str;
	}
	public static String rollMonth(String addtime,String time_limit){
		Date t=DateUtils.rollDate(DateUtils.getDate(addtime), 0, NumberUtils.getInt(time_limit),0);
		return t.getTime()/1000+"";
	}
	
	public static String rollDay(String addtime,String time_limit_day){
		Date t=DateUtils.rollDate(DateUtils.getDate(addtime), 0, 0,NumberUtils.getInt(time_limit_day));
		return t.getTime()/1000+"";
	}
	
	public static Date getIntegralTime(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	public static Date getLastIntegralTime(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	public static Date getLastSecIntegralTime(Date d){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(d.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static long getTime(String format){
		long t=0;
		if(StringUtils.isBlank(format)) return t;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = sdf.parse(format);
			t=date.getTime()/1000;
		} catch (ParseException e) {
			logger.error(e);
		}
		return t;
	}
	
	public static Date getDate2(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e);
		}
		return date;

	}
	
	public static Date getDateYYYYMMdd(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e);
		}
		return date;

	}
	
	/**
	 * 获取下一天 默认 格式 yyyy-MM-dd 00:00:00
	 * @param date
	 * @return
	 */
	public static Date getNextDayYYYYMMdd(Date date){
		date = rollDay(date, 1);
		return getDateYYYYMMdd(dateStr2(date));
	}
	//wsl 2014-09-04 start
	/**
	 * 获取当天 默认 格式 yyyy-MM-dd 00:00:00
	 * @param date
	 * @return
	 */
	public static long getDayYYYYMMdd(Date date){
		return getDateYYYYMMdd(dateStr2(date)).getTime()/1000;
	}
	//wsl 2014-09-04 end
	public static String getYYYYMMddHHmmss(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date == null){return null;}
		try {
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception {
/*	    Date date = getDate2("2014-02-17 16:42:54");
	    Date next =  getNextDayYYYYMMdd(date);
	    System.out.println(next);
	    Date date1 = getDate2("2014-02-18 18:20:54");
	    
	    String date2 = dateStr5(new Date());
		System.out.println( (date1.getTime() - next.getTime())/(24*60*60*1000) + 1 );
		System.out.println(date2);*/
		/*System.out.println(valid("2014-02-17 16:42:54"));
		System.out.println(valid("2014-02-17"));*/
	/*	Date d1 = DateUtils.getDate2("2014-08-17 16:42:54");
		System.out.println(d1.compareTo(new Date()));*/
	}
	public static boolean valid(String str) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		try {
			Date date = (Date) formatter.parse(str);
			return str.equals(formatter.format(date));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param str1 时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2 时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static Long getDistanceTime(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		System.out.println("两个时间相差距离多少天多少小时多少分多少秒 ：" + day + "天" + hour + "小时" + min + "分" + sec + "秒");
		return day;
	}
}
