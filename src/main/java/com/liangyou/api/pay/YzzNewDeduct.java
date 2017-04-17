package com.liangyou.api.pay;


/**
 * 代扣充值
 * @author Administrator
 *
 */
public class YzzNewDeduct extends PayModel{
	
	//请求参数
//	private String returnUrl;
	private String notifyUrl;
	private String userId;
	private String bankProvName;//银行卡所在省
	private String bankCityName;//银行卡所在市
	private String bankAccountNo;//银行卡号
	private String money;//代扣金额
	private String bankCode;//银行简称
	
	private String[] paramNames = new String[]{"service","partnerId","signType","sign","orderNo","notifyUrl",
			"userId","bankProvName","bankCityName","bankAccountNo","money","bankCode"};
	
	//返回参数
	private String success;
	private String orderNo;
	private String resultCode;
	private String resultMessage;
	private String depositId;
	private String channelId;
	private String isSuccess;
	private String outBizNo;
	private String amount;
	private String amountIn;
	private String bankName;
	private String rawAddTime;
	private String message;
	private String notifyTime;
	private String payNo;
	
//	public String getReturnUrl() {
//		return returnUrl;
//	}
//	public void setReturnUrl(String returnUrl) {
//		this.returnUrl = returnUrl;
//	}
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
	public String getBankProvName() {
		return bankProvName;
	}
	public void setBankProvName(String bankProvName) {
		this.bankProvName = bankProvName;
	}
	public String getBankCityName() {
		return bankCityName;
	}
	public void setBankCityName(String bankCityName) {
		this.bankCityName = bankCityName;
	}
	public String getBankAccountNo() {
		return bankAccountNo;
	}
	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public String getDepositId() {
		return depositId;
	}
	public void setDepositId(String depositId) {
		this.depositId = depositId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getOutBizNo() {
		return outBizNo;
	}
	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAmountIn() {
		return amountIn;
	}
	public void setAmountIn(String amountIn) {
		this.amountIn = amountIn;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getRawAddTime() {
		return rawAddTime;
	}
	public void setRawAddTime(String rawAddTime) {
		this.rawAddTime = rawAddTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getNotifyTime() {
		return notifyTime;
	}
	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
}	
