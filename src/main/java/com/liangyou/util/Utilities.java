package com.liangyou.util;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 实用工具,包含�?些常用方.
 * 
 * Copyright (c) 2005-2007
 * Commnet Software Tech  All Rights Reserved.
 * @author josson.jin
 */
public class Utilities {

	/**
	 * 将字符串转换为UTF-8解码
	 * @param aValue
	 * @return
	 * @throws Exception
	 */
	public static String decode(String str) {
		return decode(str,"UTF-8");
	}
	
	/**
	 * 对字符串按指定字符集进行解码转换.
	 * @param str
	 * @param charsetName 字符集名称
	 * @return
	 * @throws Exception
	 */
	public static String decode(String str,String charsetName) {
		try {
			if (str == null)
				return null;
			return new String(str.getBytes("ISO-8859-1"), charsetName);
			// �?: 这里的UTF-8, 应视提交页面的编码�?�定, 若提交表单的页面是GBK编码�?,
			// 这里应当设置为GBK. 否则, 仍然无法正常转换成中�?.
		} catch (Exception ex) {
			return str;
		}
	}
	
	/**
	 * 将字符串按UTF-8进行编码.
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		return encode(str,"UTF-8");
	}
	
	/**
	 * 将字符串按指定字符集进行编码.
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static String encode(String str,String charsetName) {
		if (str == null)	return null;
		try {
			return (URLEncoder.encode(str, charsetName));
		}
		catch (Exception ex) {
			return str;
		}
	}
	
	/**
	 * 判断字符串是否为数字函数，正则表达式
	 */
	public static boolean isDigitalChar(String strNumber){
		if(StringUtils.isBlank(strNumber)) return false;
		
		Pattern p = Pattern.compile("[^0-9]");
		Matcher m = p.matcher(strNumber);
		return !m.find();
	}
	
	/**
	 * 对长度不足的字符以指定的字符填充.
	 * @param str
	 * @param len
	 * @param ch 填充字符
	 * @param fillOnLeft true 左边/false 右边填充
	 * @return
	 */
	public static String expandStr(String str, int len, char ch, boolean fillOnLeft){
        int nLen = str.length();
        if(len <= nLen)
            return str;
        String sRet = str;
        for(int i = 0; i < len - nLen; i++)
            sRet = fillOnLeft ? String.valueOf(ch) + String.valueOf(sRet) : String.valueOf(sRet) + String.valueOf(ch);

        return sRet;
    }
	
	/**
	 * 将不存于目的集合中的对象从源集合中移
	 * @param src 源集
	 * @param dest 目的集合
	 * @return
	 */
	public static boolean retainAll(Collection src,Collection dest) {
		boolean modified = false;
		Iterator itr = src.iterator();
		while (itr.hasNext()) {
		    if(dest.contains(itr.next())) {
		    	itr.remove();
			modified = true;
		    }
		}
		return modified;
	}
	
	public static boolean retain(Collection src,Collection dest) {
		boolean modified = false;
		Iterator itr = src.iterator();
		while (itr.hasNext()) {
		    if(!dest.contains(itr.next())) {
		    	itr.remove();
			modified = true;
		    }
		}
		return modified;
	}
	static String ssource = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	static char[] src = ssource.toCharArray();
	
	/**
	 * 产生随机字符串
	 */
    public static String randString (int length){
        char[] buf = new char[length];
        int rnd;
        for(int i=0;i<length;i++){
            rnd = Math.abs(new Random().nextInt()) % src.length;
            buf[i] = src[rnd];
        }
        return new String(buf);
    }
}
