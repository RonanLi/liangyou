package com.liangyou.sendPhone;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.liangyou.context.Global;

/**
 * 
 * 义务贷使用接口类
 *
 */
public class YWDPhone {
	private static Logger logger = Logger.getLogger(YWDPhone.class);
	private static String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";
	
	public static String send(String phone,String content){
		String msg = "";
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(Url); 
			
		//client.getParams().setContentCharset("GBK");		
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

		String sendsms_account = Global.getValue("sendsms_account");  //发送短信账户
		 String sendsms_password = Global.getValue("sendsms_password");  //发送短信密码
		
		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", sendsms_account), 
			    new NameValuePair("password", sendsms_password), 	//密码可以使用明文密码或使用32位MD5加密
			    new NameValuePair("mobile", phone), 
			    new NameValuePair("content", content),
		};
		
		method.setRequestBody(data);		
		
		
		try {
			client.executeMethod(method);	
			
			String SubmitResult =method.getResponseBodyAsString();
					
			//System.out.println(SubmitResult);

			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();


			String code = root.elementText("code");	
			String message = root.elementText("msg");	
			String smsid = root.elementText("smsid");	
			logger.info("返回值:"+code+"-----提交结果描述:"+message+"-------消息ID:"+smsid);
			
			if(!code.equals("2")){
				msg =  message;
			}else{
				msg = "成功";
			}
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug(e.getMessage());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug(e.getMessage());
		}	
		return msg;
	}
	
}
