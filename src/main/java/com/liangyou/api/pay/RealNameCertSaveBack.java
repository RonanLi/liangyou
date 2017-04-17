package com.liangyou.api.pay;
/**
 * 实名认证保存
 * @author Administrator
 *
 */
public class RealNameCertSaveBack extends PayModel {
	
	private String status;
	private String userId;
	private String msg;
	private String notifyTime;
	private String signType;
	private String sign;
	private String authNo;
	
	private String[] paramNames = new String[]{"status","userId","msg","notifyTime","signType","authNo"};

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getAuthNo() {
		return authNo;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}
	
	
}
