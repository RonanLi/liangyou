package com.liangyou.api.pay;

import java.io.Serializable;

/**
 * TGPROJECT-362
 * @author wujing
 *无卡代扣签约
 */
public class SignmanyBank extends PayModel implements Serializable{
	
	private static final long serialVersionUID = -3811395929295917611L;

	private String userId;  // 会员号
	
	private String returnUrl;  //页面跳转同步通知
	
	private String errorNotifyUrl;  //请求出错时跳转路径
	
	private String unionBusinessNo;   //银联商户号号，默认：easy_trade-yxyt
	
	 private String[] paramNames=new String[]{"service","partnerId","orderNo", "sign","signType",
			 						"returnUrl","errorNotifyUrl","userId","unionBusinessNo"};

	//一下为接收回调参数
	 
	 private String notifyTime;  //通知发送时间
	 
	private  String cardNo;  //银行卡号
	
	private String certNo;  //证件号
	
	private String name;	//姓名
	
	private String bankShort;	//银行简称
	
	private String bankName;	//银行全称
	
	private String  isSuccess;	 //是否成功
	
	private String message;  //消息

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getErrorNotifyUrl() {
		return errorNotifyUrl;
	}

	public void setErrorNotifyUrl(String errorNotifyUrl) {
		this.errorNotifyUrl = errorNotifyUrl;
	}

	public String getUnionBusinessNo() {
		return unionBusinessNo;
	}

	public void setUnionBusinessNo(String unionBusinessNo) {
		this.unionBusinessNo = unionBusinessNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBankShort() {
		return bankShort;
	}

	public void setBankShort(String bankShort) {
		this.bankShort = bankShort;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	

}
