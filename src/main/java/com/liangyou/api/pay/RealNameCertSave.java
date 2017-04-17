package com.liangyou.api.pay;
/**
 * 实名认证保存
 * @author Administrator
 *
 */
public class RealNameCertSave extends PayModel {
	
	private String coreCustomerUserId;
	private String nickname;
	private String certType;
	private String cardtype;
	private String cardid;
	private String cardpic;
	private String cardpic1;
	private String source;
	private String cardoff;
	private String notifyUrl;
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","coreCustomerUserId","nickname","certType","cardtype",
	         "cardid","cardpic","cardpic1","source","orderNo","cardoff","notifyUrl"};
	
	//返回信息封装
	private String message;
	private String resultCode;
	private String success;
	
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
	public String getCoreCustomerUserId() {
		return coreCustomerUserId;
	}
	public void setCoreCustomerUserId(String coreCustomerUserId) {
		this.coreCustomerUserId = coreCustomerUserId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	public String getCardpic() {
		return cardpic;
	}
	public void setCardpic(String cardpic) {
		this.cardpic = cardpic;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getCardpic1() {
		return cardpic1;
	}
	public void setCardpic1(String cardpic1) {
		this.cardpic1 = cardpic1;
	}
	public String getCardoff() {
		return cardoff;
	}
	public void setCardoff(String cardoff) {
		this.cardoff = cardoff;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
}
