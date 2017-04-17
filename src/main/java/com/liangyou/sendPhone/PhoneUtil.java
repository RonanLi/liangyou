package com.liangyou.sendPhone;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

/**
 * @author wujing 
 * @version 创建时间：2013-12-30 下午5:21:06
 * 类说明:根据每个平台不同的短信接口提供商，调用不同的短信平台发送短信的接口
 * 调用接口时，必须要先知道平台短信接口的代号，不然会导致在发送短信时，从其他平台的短信通道中发出
 * 目前已经使用的短信通道：1、融都默认短信通道；2、国控小薇公司使用短信通道
 */
public class PhoneUtil {
	 static Logger logger = Logger.getLogger(PhoneUtil.class);
	
	/**
	 * @param type：短信接口类型
	 * @param phone：手机号
	 * @param content：短信内容
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String  sentPhone(int type,String phone,String content) throws UnsupportedEncodingException{
		String result = "";
		switch (type) {
		case 1://短信通道
			result = RDPhone.sentPhone(phone, content);
			return result;
		default:
			return "";
		}
	}
}
