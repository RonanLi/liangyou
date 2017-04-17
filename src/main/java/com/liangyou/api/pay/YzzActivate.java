package com.liangyou.api.pay;
/**
 * 业务参数-激活
 * @author Administrator
 */
public class YzzActivate extends PayModel {

	private String userId;
	private String  notifyUrl;
	private String  returnUrl;
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","userId","notifyUrl","returnUrl"};

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
}
