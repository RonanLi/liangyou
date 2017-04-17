package com.liangyou.sendPhone;

import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.liangyou.context.Global;

/**
 * 此版本使用document 对象封装XML，解决发送短信内容包涵特殊字符而出现无法解析，如 短信为：“你好，<%&*&*&><<<>fds测试短信”
 * 东方贷
 */

public class Hulianphone {
	public static Logger logger = Logger.getLogger(Hulianphone.class);

	// ############################此部分参数需要修改############################
	public static String userName = Global.getValue("sendsms_account"); // 用户名
	public static String password = Global.getValue("sendsms_password"); // 密码
	public static String sign = ""; // 短信内容
	public static String msgid = "";
	public static String subcode = "";
	public static String sendtime = "";
	// public static String url="http://3tong.net/http/sms/Submit"; //无限通使用地址
	public static String url = "http://3tong.net/http/sms/Submit";

	// ############################此部分参数需要修改############################
	// MD5加密函数
	public static String MD5Encode(String sourceString) {
		String resultString = null;
		try {
			resultString = new String(sourceString);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byte2hexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
		}
		return resultString;
	}

	public static final String byte2hexString(byte[] bytes) {
		StringBuffer bf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10) {
				bf.append("0");
			}
			bf.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return bf.toString();
	}

	// 发送短信
	/**
	 * 发送短信方法使用document 对象方法封装XML字符串
	 */
	public static String send(String phone, String content) {
		try {
			// String url = "http://3tong.net/http/sms/Submit";
			Map<String, String> params = new LinkedHashMap<String, String>();
			Hulianphone docXml = new Hulianphone();
			/*
			 * String message =
			 * "<?xml version='1.0' encoding='UTF-8'?><message>" + "<account>" +
			 * userName + "</account>" + "<password>" + MD5Encode(password) +
			 * "</password>" + "<msgid></msgid><phones>" + phone +
			 * "</phones><content>" + content + "</content>" + "<sign>"+sign+
			 * "</sign><subcode></subcode><sendtime></sendtime></message>";
			 */
			String message = docXml.DocXml(userName, MD5Encode(password),
					msgid, phone, content, sign, subcode, sendtime);
			logger.info("发送短信-->" + message);
			params.put("message", message);

			String resp = doPost(url, params);
			logger.info("短信发送返回信息--->" + resp);
			return "短信发送成功";
		} catch (Exception e) {
			logger.info("短信发送异常--->" + e.getMessage());
			return "短信发送失败";
		}

	}

	// 状态报告
	private static void getReport() {
		// String url = "http://3tong.net/http/sms/Submit";
		Map<String, String> params = new LinkedHashMap<String, String>();
		String message = "<?xml version='1.0' encoding='UTF-8'?><message>"
				+ "<account>" + userName + "</account>" + "<password>"
				+ MD5Encode(password) + "</password>"
				+ "<msgid></msgid><phone>18221177661</phone></message>";
		params.put("message", message);

		String resp = doPost(url, params);
		System.out.println(resp);
	}

	// 查询余额
	private static void getBalance() {
		// String url = "http://3tong.net/http/sms/Submit";
		Map<String, String> params = new LinkedHashMap<String, String>();
		String message = "<?xml version='1.0' encoding='UTF-8'?><message><account>"
				+ userName
				+ "</account>"
				+ "<password>"
				+ MD5Encode(password)
				+ "</password></message>";
		params.put("message", message);

		String resp = doPost(url, params);
		System.out.println(resp);
	}

	// 获取上行
	private static void getSms() {

		Map<String, String> params = new LinkedHashMap<String, String>();
		String message = "<?xml version='1.0' encoding='UTF-8'?><message><account>"
				+ userName
				+ "</account>"
				+ "<password>"
				+ MD5Encode(password)
				+ "</password></message>";
		params.put("message", message);

		String resp = doPost(url, params);
		System.out.println(resp);
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	private static String doPost(String url, Map<String, String> params) {
		String response = null;
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 设置Post数据
		if (!params.isEmpty()) {
			int i = 0;
			NameValuePair[] data = new NameValuePair[params.size()];
			for (Entry<String, String> entry : params.entrySet()) {
				data[i] = new NameValuePair(entry.getKey(), entry.getValue());
				i++;
			}

			postMethod.setRequestBody(data);

		}
		try {
			client.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		return response;
	}

	/**
	 * 使用document 对象封装XML
	 * 
	 * @param userName
	 * @param pwd
	 * @param id
	 * @param phone
	 * @param contents
	 * @param sign
	 * @param subcode
	 * @param sendtime
	 * @return
	 */
	public String DocXml(String userName, String pwd, String msgid,
			String phone, String contents, String sign, String subcode,
			String sendtime) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("UTF-8");
		Element message = doc.addElement("response");
		Element account = message.addElement("account");
		account.setText(userName);
		Element password = message.addElement("password");
		password.setText(pwd);
		Element msgid1 = message.addElement("msgid");
		msgid1.setText(msgid);
		Element phones = message.addElement("phones");
		phones.setText(phone);
		Element content = message.addElement("content");
		content.setText(contents);
		Element sign1 = message.addElement("sign");
		sign1.setText(sign);
		Element subcode1 = message.addElement("subcode");
		subcode1.setText(subcode);
		Element sendtime1 = message.addElement("sendtime");
		sendtime1.setText(sendtime);
		return message.asXML();
		// System.out.println(message.asXML());

	}
}
