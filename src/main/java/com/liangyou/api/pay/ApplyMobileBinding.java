package com.liangyou.api.pay;


/**
 * 设置银行卡为默认绑定
 * @author Administrator
 *
 */
public class ApplyMobileBinding extends PayModel {
	
	private String mobile ;
	private String userId;
	private String[] paramNames = new String[]{"service","partnerId",
	                 "signType","sign","orderNo","mobile","userId"};
	
	/*{"message":"设置手机绑定","resultCode":"EXECUTE_SUCCESS","success":true}
	 */
	private String message;
	private String resultCode;
	private String success;
	//构造器
	public ApplyMobileBinding(){
		super();
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
