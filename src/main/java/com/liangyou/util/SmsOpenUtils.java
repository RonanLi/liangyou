package com.liangyou.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/*****************
* @ClassName: SmsOpenUtils
* @Description: 短信工具类
* @author xx_erongdu
* @date 2013-7-22 下午1:53:11
*
*****************/ 
public class SmsOpenUtils {
	private static Logger logger = Logger.getLogger(SmsOpenUtils.class);
	/**
	 * 短信发送
	 * @param url
	 * @param username
	 * @param password
	 * @param mobile
	 * @param content
	 * @return 发送响应结果
	 */
	public static String sendSms(String name, String pwd, String dst, String msg){
		   String response = null;
	       String  URL="http://yl.mobsms.net/send/gsend.aspx";
	       PostMethod postMethod = new PostMethod(URL);
	       postMethod.getParams().setContentCharset("GB2312");
	       postMethod.addParameter("name", name);
	       postMethod.addParameter("pwd",pwd);
	       postMethod.addParameter("dst", dst);
	       postMethod.addParameter("msg", msg);
	       
	        HttpClient httpClient = new HttpClient();
	        int statusCode=0;
	       try {
	       	statusCode=httpClient.executeMethod(postMethod);
	           if(statusCode== HttpStatus.SC_OK){ 
	          	 response = postMethod.getResponseBodyAsString();
	           }else {
	               System.out.println("=====statusCode===="+statusCode);
				}
	       } catch (Exception e) {
	           e.printStackTrace();
	       }finally{
	       	postMethod.releaseConnection(); 
	       }
	       System.out.println("响应结果为："+response);
	       logger.info("响应结果为："+response);
		   return response;
	}
	
	public static String convertStreamToString(InputStream is) {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
        StringBuilder sb = new StringBuilder();      
        String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {    
                is.close();
            } catch (IOException e) {
               logger.error(e);
            }
        }  
        return sb.toString();
    }
	
	public static void main(String[] args) {
		SmsOpenUtils.sendSms("zyjr", "zyjr123", "18201020469","恭喜您！");
	}
}
