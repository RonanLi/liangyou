package com.liangyou.api.pay;
/**
 * 代扣充值接口
 * @author Administrator
 *
 */
public class DeductDepositApply extends PayModel {
	
	private String  deductType;
	private String  certType;
	private String  certNo;
	private String  userId;
	private String  amount;
	private String  bizIdentity;
	private String  bizNo;
	private String  outBizNo;
	private String  payChannelApi;
	private String  tradeBizProductCode;
	private String  bankAccountNo;   //银行卡号
	private String  bankAccountName;  //开户名
	private String  provName; // 省
	private String  cityName;  //市
	private String  owner;  // 渠道所属来源
	private String  bankCode;   //银行简写
	private String  publicTag;   //对公对
	private String  extFlag;  //  渠道特殊标号
	private String  subOwner; //产品子集
	private String  notifyUrl;  // 异步通知地址
	
	
	private String[] paramNames = new String[]{"service","partnerId",
			         "signType","sign","orderNo","deductType","certType","certNo","userId","amount",
			         "bizIdentity","bizNo","outBizNo","tradeBizProductCode","bankAccountNo","bankAccountName",
			         "notifyUrl","owner","bankCode","publicTag","extFlag","subOwner","provName","cityName"};
	//下面字段 保存返回的信息。
	private String status;  //指令状态
	private String rawAddTime; //添加时间
	private String bankName;      // 充值银行
	private String payAmount;  //应付额
	private String payAmountIn;  //应付实收额
	private String depositId; // 充值流水
	private Object success;   
	private String resultMessage;
	

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getDeductType() {
		return deductType;
	}

	public void setDeductType(String deductType) {
		this.deductType = deductType;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

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

	public String getPayChannelApi() {
		return payChannelApi;
	}

	public void setPayChannelApi(String payChannelApi) {
		this.payChannelApi = payChannelApi;
	}

	public String getTradeBizProductCode() {
		return tradeBizProductCode;
	}

	public void setTradeBizProductCode(String tradeBizProductCode) {
		this.tradeBizProductCode = tradeBizProductCode;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRawAddTime() {
		return rawAddTime;
	}

	public void setRawAddTime(String rawAddTime) {
		this.rawAddTime = rawAddTime;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getPayAmountIn() {
		return payAmountIn;
	}

	public void setPayAmountIn(String payAmountIn) {
		this.payAmountIn = payAmountIn;
	}

	public String getDepositId() {
		return depositId;
	}

	public void setDepositId(String depositId) {
		this.depositId = depositId;
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

	

	public Object getSuccess() {
		return success;
	}

	public void setSuccess(Object success) {
		this.success = success;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getPublicTag() {
		return publicTag;
	}

	public void setPublicTag(String publicTag) {
		this.publicTag = publicTag;
	}

	public String getExtFlag() {
		return extFlag;
	}

	public void setExtFlag(String extFlag) {
		this.extFlag = extFlag;
	}

	public String getSubOwner() {
		return subOwner;
	}

	public void setSubOwner(String subOwner) {
		this.subOwner = subOwner;
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
	
	

}
