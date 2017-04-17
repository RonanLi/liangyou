package com.liangyou.api.pay;
	/**
	 * 无卡代扣签约
	 * @author Administrator
	 *
	 */
public class DeductSign extends PayModel {
	
	private String userId;
	private String notifyUrl;
	private String returnUrl;
	 private String[] paramNames=new String[]{"service","partnerId","signType",
	         "sign","orderNo","userId","notifyUrl","returnUrl"};
	 
	 //一下为接收回调参数
	 private String cardNo;
	 private String message;
	 private String isSuccess;
	 private String notifyTime;
	 
	 
	 
	 public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}

	public DeductSign(){
		 super();
	 }

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
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

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	 

}
