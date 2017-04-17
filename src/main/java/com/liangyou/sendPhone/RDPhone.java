package com.liangyou.sendPhone;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.liangyou.context.Global;
import com.liangyou.util.SmsOpenUtils;

/**
 * @author wujing 
 * @version 创建时间：2013-12-30 下午5:08:50
 * 类说明：使用融都本公司短信工具类
 */
public class RDPhone {
	
	
	/**
	 * @param phone：手机号码
	 * @param content：内容
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String  sentPhone(String phone,String content) throws UnsupportedEncodingException{
		 String sendsms_account = Global.getValue("sendsms_account");  //发送短信账户
		 String sendsms_password = Global.getValue("sendsms_password");  //发送短信密码
		 String sendResult;
		try {
			sendResult = SmsOpenUtils.sendSms(sendsms_account, sendsms_password, phone, content);
			if(sendResult.startsWith("num=1")){  //modify by lijing 返回码是这个了
				sendResult = "短信发送成功";
			}
			return sendResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
