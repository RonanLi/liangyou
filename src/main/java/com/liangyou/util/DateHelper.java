/*
 * Created on 2005-3-24
 */
package com.liangyou.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author chenbh
 */
public class DateHelper {
	private static Logger logger = Logger.getLogger(DateHelper.class);
	/**
	 * 取得当前日期
	 * @return Date 当前日期
	 */
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}
	
	/**
	 * 返回当前日期对应的默认格式的字符串
	 * @return String 当前日期对应的字符串
	 */
	public static String getCurrentStringDate() {
		return convertDate2String(getCurrentDate(), DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 返回当前日期对应的指定格式的字符串
	 * @param dateFormat - 日期格式
	 * @return String 当前日期对应的字符串
	 */
	public static String getCurrentStringDate(String dateFormat) {
		return convertDate2String(getCurrentDate(), dateFormat);
	}
	
	/**
	 * 将日期转换成指定格式的字符串
	 * @param date - 要转换的日期
	 * @param dateFormat - 日期格式
	 * @return String 日期对应的字符串
	 */
	public static String convertDate2String(Date date, String dateFormat) {
		SimpleDateFormat sdf = null;
		if(dateFormat != null && !dateFormat.equals("")) {
			try {
				sdf = new SimpleDateFormat(dateFormat);
			} catch(Exception e) {
				logger.error(e);
				sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			}
		} else {
			sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);			
		}
		return sdf.format(date);
	}	
	
	/**
	 * 将字符串转换成日期
	 * @param stringDate - 要转换的字符串格式的日期
	 * @return Date 字符串对应的日期
	 */
	public static Date convertString2Date(String stringDate) {
		return convertString2Date(stringDate, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 将字符串转换成日期
	 * @param stringDate - 要转换的字符串格式的日期
	 * @param dateFormat - 要转换的字符串对应的日期格式
	 * @return Date 字符串对应的日期
	 */
	public static Date convertString2Date(String stringDate, String dateFormat) {
		SimpleDateFormat sdf = null;
		if(dateFormat != null && !dateFormat.equals("")) {
			try {
				sdf = new SimpleDateFormat(dateFormat);
			} catch(Exception e) {
				logger.error(e);
				sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			}
		} else {
			sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);			
		}
		try {
			return sdf.parse(stringDate);		
		} catch(ParseException e) {
			logger.error(e);
			return new Date(System.currentTimeMillis());
		}
	}
	
	/**
	 * 将一种格式的日期字符串转换成默认格式的日期字符串
	 * @param oldDate - 要格式化的日期字符串
	 * @param oldFormat - 要格式化的日期的格式
	 * @return String 格式化后的日期字符串
	 */
	public static String formatStringDate(String oldStringDate, String oldFormat) {
		return convertDate2String(convertString2Date(oldStringDate, oldFormat), DEFAULT_DATE_FORMAT); 
	}
	
	/**
	 * 将一种格式的日期字符串转换成另一种格式的日期字符串
	 * @param oldDate - 要格式化的日期字符串
	 * @param oldFormat - 要格式化的日期的格式
	 * @param newFormat - 格式化后的日期的格式
	 * @return String 格式化后的日期字符串
	 */
	public static String formatStringDate(String oldStringDate, String oldFormat, String newFormat) {
		return convertDate2String(convertString2Date(oldStringDate, oldFormat), newFormat);
	}
	
	/**
	 * 根据年份和月份判断该月有几天
	 * @param year	-	年份
	 * @param month	- 月份
	 * @return int
	 */
	public static int days(int year, int month) {
		int total = 30;
		switch (month) {
			case 1	:
			case 3	:
			case 5	:
			case 7	:
			case 8	:
			case 10	:
			case 12	:
				total = 31;	break;
			case 2	:
				if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
					total = 29;
				else
					total = 28;
				break;
			default : break;
		}
		return total;
	}
	
	/**
	 * 根据年份和月份判断该月的第一天是星期几
	 * @param year	- 年份
	 * @param month	- 月份
	 * @return int
	 */
	@SuppressWarnings("deprecation")
	public static int firstDayOfWeek(int year, int month) {
		Date firstDate = new Date(year - 1900, month - 1, 1);
		return firstDate.getDay();
	}
	
	/**
	 * 根据年份和月份判断该月的�?后一天是星期几
	 * @param year	- 年份
	 * @param month	- 月份
	 * @return int
	 */	
	@SuppressWarnings("deprecation")
	public static int lastDayOfWeek(int year, int month) {
		Date lastDate = new Date(year - 1900, month - 1, days(year, month));
		return lastDate.getDay();
	}
	
	/**
	 * 获取当前年份
	 * @return int
	 */
	@SuppressWarnings("deprecation")
	public static int getCurrentYear() {
		return getCurrentDate().getYear() + 1900;
	}	
	
	/**
	 * 获取当前月份(1-12)
	 * @return int
	 */
	@SuppressWarnings("deprecation")
	public static int getCurrentMonth() {
		return getCurrentDate().getMonth() + 1;
	}
	
	/**
	 * 获取给定日期的下一个月的日期，返回格式为yyyy-MM-dd
	 * @param dateStr - 给定日期
	 * @param format - 给定日期的格式
	 * @return String
	 */
	@SuppressWarnings("deprecation")
	public static String getNextMonth(String stringDate, String format) {
		Date date = convertString2Date(stringDate, format);
		int year = date.getYear() + 1900;
		int month = date.getMonth() + 1;
		if(month == 12) {
			year = year +1;
			month = 1;
		} else {
			month = month + 1;
		}
		return year + "-" + (month < 10 ? "0" : "") + month + "-01";
	}	
	
	/**
	 * 获取给定日期的下�?个月的日期，返回格式为yyyy-MM-dd
	 * @param dateStr - 给定日期
	 * @return String
	 */
	public static String getNextMonth(String stringDate) {
		return getNextMonth(stringDate, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 获取给定日期的前
	 * @param stringDate - 给定日期
	 * @param format - 给定日期的格式
	 * @return String
	 */
	@SuppressWarnings("deprecation")
	public static String getBeforDate(String stringDate, String format) {
		Date date = convertString2Date(stringDate, format);
		int year = date.getYear() + 1900;
		int month = date.getMonth() + 1;
		int day = date.getDate();
		if(day == 1) {
			if(month == 1) {
				return (year - 1) + "-12-31";
			} else {
				month = month - 1;
				day = days(year, month);
				return year + "-" + (month < 10 ? "0" : "") + month + "-" + day;
			}
		} else {
			return year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 11 ? "0" : "") + (day - 1);
		}
	}
	
	
	/**
	 * 获取给定日期的前�?�?
	 * @param stringDate - 给定日期
	 * @return String
	 */
	public static String getBeforDate(String stringDate) {
		return getBeforDate(stringDate, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 返回当前时间对应的字符串
	 * @return String
	 */
	public static String getCurrentStringTimeMillis(){
		return String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * 获取给定时间对应的字符串
	 * @param date - 给定日期
	 * @return String
	 */
	public static String convertDateToStringTimeMillis(Date date){
		return String.valueOf(date.getTime());
	}
	
	/**
	 * 获取给定字符串对应的时间
	 * @param stringTimeMills - 给定字符串
	 * @return String
	 */
	public static Date convertStringTimeMillisToDate(String stringTimeMills){
		return new Date(Long.valueOf(stringTimeMills).longValue());
		
	}
	
	
	
	
	
	/**
	 * 封装了时间段查询
	 * 如传进来 2007-09-09 �?2007-09-09
	 * 返回 2007-09-09 00:00:00 �?2007-09-09 23:59:59
	 * 说明,如果两个参数都不为null,则同�?
	 * 如果都为null,则默认为前后�?星期
	 * */
	public static String[] getQueryDate(String startdate,String enddate){
		String []s=new String[2];
		if(!StrHelper.getString(startdate).trim().equals("") &&(!StrHelper.getString(enddate).trim().equals(""))){
			s[0]=startdate+" 00:00:00";
			s[1]=enddate+" 23:59:59";
		}else if(!StrHelper.getString(startdate).trim().equals("") &&(StrHelper.getString(enddate).trim().equals(""))){ 
			s[0]=nowLongFormat().substring(0,10)+" 00:00:00";
			s[1]=s[0].substring(0,10)+" 23:59:59";
		}else if(StrHelper.getString(startdate).trim().equals("") &&(StrHelper.getString(enddate).trim().equals(""))){//都为�?
			s[1]=nowLongFormat().substring(0,10)+" 23:59:59";
			s[0]=afterNDay(-7).substring(0,10)+" 00:00:00";
		}else{
			s[0]=s[1]=null;
		}
		
		return s;
	}

	public static String[] getQueryCurrentDate(String startdate,String enddate,int after){
		String []s=new String[2];
		if(!StrHelper.getString(startdate).trim().equals("") &&(!StrHelper.getString(enddate).trim().equals(""))){
			s[0]=startdate;
			s[1]=enddate;
		}else if(StrHelper.getString(startdate).trim().equals("") &&(StrHelper.getString(enddate).trim().equals(""))){//都为�?
			s[0]=s[1]=afterNDay(after).substring(0,10);
		}else{
			s[0]=s[1]=null;
		}
		
		return s;
	}
	
	public static String[] getQueryDate(String startdate,String enddate,int after){
		String []s=new String[2];
		if(!StrHelper.getString(startdate).trim().equals("") &&(!StrHelper.getString(enddate).trim().equals(""))){
			s[0]=startdate;
			s[1]=enddate;
		}else if(StrHelper.getString(startdate).trim().equals("") &&(StrHelper.getString(enddate).trim().equals(""))){//都为�?
			s[0]=afterNDay(after-30).substring(0,10);
			s[1]=afterNDay(after).substring(0,10);
		}else{
			s[0]=s[1]=null;
		}
		return s;
	}
	public static String nowLongFormat() {
		SimpleDateFormat sdf = null;
			try {
				sdf = new SimpleDateFormat(LONG_DATE_FORMAT);
			} catch(Exception e) {
			}
		return sdf.format(new Date());
	}
	
	public static String shortDateFormat(Date date) {
		if(date == null)
			return "";
		SimpleDateFormat sdf = null;
			try {
				sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			} catch(Exception e) {
			}
		return sdf.format(date);
	}
	
//	当前日期前几天或者后几天的日�?

	 public static String afterNDay(int n){
	        Calendar c=Calendar.getInstance();
	        DateFormat df=new SimpleDateFormat(LONG_DATE_FORMAT);
	        c.setTime(new Date());
	        c.add(Calendar.DATE,n);
	        Date d2=c.getTime();
	        String s=df.format(d2);
	        return s;
	    }

	/**
	 * 默认的日期格�?
	 */
	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	public static String MIDDLE_DATE_FORMAT = "yyyy-MM-dd HH:mm";

	public static String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String NONE_LONG_DATE="00-00-00 00:00:00";

	public static Date convertString2LongDate(String stringDate) {
		return convertString2Date(stringDate, LONG_DATE_FORMAT);
	}
	
	
	/**
	 * 用于处理excel导出
	 * hzc EDIT 2007-12-25
	 * */
	public static String convert2String(String obj) {
		SimpleDateFormat sdf = null;
		if(obj != null && !obj.equals("")) {
			if(obj.trim().length()<=10){
				try {
					sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
				} catch(Exception e) {}
			}else{
				try {
					sdf = new SimpleDateFormat(LONG_DATE_FORMAT);
				} catch(Exception e) {}
			}
		} else {
			sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);			
		}
		try {
			return sdf.format(sdf.parse(obj));
		} catch(ParseException pe) {
			return "";
		}
	}
	
	public static boolean isDate(String obj){
		if(obj != null && !obj.equals("")){
			if(obj.indexOf("~")>-1) return false;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
				//if(obj.length()==7&&obj.indexOf("-")==4 && Integer.parseInt(obj.substring(0,4))>1900 ) return true;
				sdf.parse(obj);
				return true;
			} catch(Exception e) {
				return false;
			}
		}else return false;
	}
}
