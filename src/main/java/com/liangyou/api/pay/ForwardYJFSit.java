package com.liangyou.api.pay;
/**
 * 业务参数-激活
 * @author Administrator
 */
public class ForwardYJFSit extends PayModel {

	private String userId;
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","userId"};

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

}
