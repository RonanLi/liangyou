package com.liangyou.api.pay;
/**
 * 提现申请
 * @author Administrator
 *
 */
public class ApplyWithDraw extends PayModel {
	
	
	private String userId;
	//private String  payChannelApi  ;
	private String amount;
	private String bizIdentity;
	private String bizNo;
	private String outBizNo;
	private String bankAccountNo;
	private String bankAccountName;
	private String bankCnapsNo;  //联行号必须有
	private String realName;
	private String provName;
	private String cityName;
	private String bankCode;
	private String applyIsInter;
	private String owner;
	private String cardType;
	private String publicTag;
	private String notifyUrl;
	private String tradeBizProductCode;
	private String tradeMerchantId;
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","userId","amount","bizIdentity","bizNo","outBizNo","bankAccountNo",
	         "bankAccountName","bankCnapsNo","realName","provName","cityName","bankCode","applyIsInter","owner","cardType","publicTag","notifyUrl","tradeBizProductCode",
	         "tradeMerchantId"};
	//封装
	private String resultCode;
	private String message;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBizIdentity() {
		return bizIdentity;
	}
	public void setBizIdentity(String bizIdentity) {
		this.bizIdentity = bizIdentity;
	}
	public String getBizNo() {
		return bizNo;
	}
	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}
	public String getOutBizNo() {
		return outBizNo;
	}
	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}
	public String getBankAccountNo() {
		return bankAccountNo;
	}
	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}
	public String getBankAccountName() {
		return bankAccountName;
	}
	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}
	public String getBankCnapsNo() {
		return bankCnapsNo;
	}
	public void setBankCnapsNo(String bankCnapsNo) {
		this.bankCnapsNo = bankCnapsNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getProvName() {
		return provName;
	}
	public void setProvName(String provName) {
		this.provName = provName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTradeBizProductCode() {
		return tradeBizProductCode;
	}
	public void setTradeBizProductCode(String tradeBizProductCode) {
		this.tradeBizProductCode = tradeBizProductCode;
	}
	public String getApplyIsInter() {
		return applyIsInter;
	}
	public void setApplyIsInter(String applyIsInter) {
		this.applyIsInter = applyIsInter;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getPublicTag() {
		return publicTag;
	}
	public void setPublicTag(String publicTag) {
		this.publicTag = publicTag;
	}
	public String getTradeMerchantId() {
		return tradeMerchantId;
	}
	public void setTradeMerchantId(String tradeMerchantId) {
		this.tradeMerchantId = tradeMerchantId;
	}
	
	
}
