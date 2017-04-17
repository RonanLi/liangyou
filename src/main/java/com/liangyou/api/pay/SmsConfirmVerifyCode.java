package com.liangyou.api.pay;


/**
 * 业务参数-验证验证码服务
 * @author Administrator
 *
 */
public class SmsConfirmVerifyCode extends PayModel {
	
	private String checkCodeUniqueId ;
	private String checkCodeString;
	private String[] paramNames = new String[]{"service","partnerId",
	                 "signType","sign","orderNo","checkCodeUniqueId","checkCodeString"};
	
	private String message;
	private String resultCode;
	
	public SmsConfirmVerifyCode() {
		super();
	}
	public String getCheckCodeUniqueId() {
		return checkCodeUniqueId;
	}
	public void setCheckCodeUniqueId(String checkCodeUniqueId) {
		this.checkCodeUniqueId = checkCodeUniqueId;
	}
	public String getCheckCodeString() {
		return checkCodeString;
	}
	public void setCheckCodeString(String checkCodeString) {
		this.checkCodeString = checkCodeString;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	
}
