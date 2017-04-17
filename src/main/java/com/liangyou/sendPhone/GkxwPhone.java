package com.liangyou.sendPhone;
import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.shcm.send.DataApi;
import com.shcm.send.OpenApi;


/**
 * @author wujing
 *@date 2013-12-30 下午5:07:34 
 *说明：国控小微平台短信发送器
 */
public class GkxwPhone
{
	private static Logger logger = Logger.getLogger(GkxwPhone.class);
	private static String sOpenUrl = "http://smsapi.c123.cn/OpenPlatform/OpenApi";
	private static String sDataUrl = "http://smsapi.c123.cn/DataPlatform/DataApi";
	
	/*// 接口帐号
	private static final String account = "1001@500738900001";
	// 接口密钥
	private static final String authkey = "EC14E4FF2315D5F1DB5A458A1F09263C";*/
	// 通道组编号
	private static final int cgid = 687;
	// 默认使用的签名编号(未指定签名编号时传此值到服务器)
	private static final int csid = 0;
	static {
		String account=Global.getValue("sendsms_account");
		String authkey=Global.getValue("sendsms_password");
		// 发送参数
		OpenApi.initialzeAccount(sOpenUrl, account, authkey, cgid, csid);
		// 状态及回复参数
		DataApi.initialzeAccount(sDataUrl, account, authkey);
	}
	
	
	/**
	 * 发送短信接口
	 * @param phone
	 * @param content
	 */
	public static String send(String phone,String content){
		try {
			
			String sSend = new String(content.getBytes(), "UTF-8");
			int nRet = OpenApi.sendOnce(phone, sSend, 0, 0, null);
			
			if (nRet >0) {
				logger.info("手机号码："+phone+",内容："+content+",发送成功"+"发送状态"+nRet);
				return "短信发送成功";
			}else{
				logger.info("手机号码："+phone+",内容："+content+"，发送失败"+"失败状态"+nRet);
				return "失败";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "失败";
	}
	
	/**
	 * 更改秘钥
	 */
	public static void updateAuthKey(){
		String sAuthKey = OpenApi.updateKey();
		if(!sAuthKey.isEmpty())
		{
			logger.debug("已成功更新密钥,新接口密钥为: " + sAuthKey);
		}
	}
	
	/**
	 * 短信接口余额查询接口
	 * @return
	 */
	public static double findBalance(){
		// 取帐户余额
		double dReamin = OpenApi.getBalance();
		System.out.println("短信接口可用余额: " + dReamin);
		return dReamin;
	}
	
	public static void main(String[] args) throws Exception
	{
	String s = send("15858272881", "JAVA接口测试内容");
	logger.info(s);
	findBalance();
	}
}
