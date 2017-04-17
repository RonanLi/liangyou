package com.liangyou.api.pay;

/**
 * 接口： 业务参数-短信发动验证服务
 * @author Administrator
 */
public class SmsCaptcha extends PayModel {
	
	private String userId ;
	private String smsContext;
	 
	private String[] paramNames = new String[]{"service","partnerId",
	                 "signType","sign","orderNo","userId","smsContext"};
	
	private String message;
	private String resultCode;
	private String checkCodeUniqueId;
	private String notifyTime;

	
	public SmsCaptcha() {
		super();
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSmsContext() {
		return smsContext;
	}

	public void setSmsContext(String smsContext) {
		this.smsContext = smsContext;
	}

	public String getCheckCodeUniqueId() {
		return checkCodeUniqueId;
	}

	public void setCheckCodeUniqueId(String checkCodeUniqueId) {
		this.checkCodeUniqueId = checkCodeUniqueId;
	}

	public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}
	
}
