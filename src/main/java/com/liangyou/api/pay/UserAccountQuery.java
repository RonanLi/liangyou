package com.liangyou.api.pay;
/**
 * 用户信息查询
 * @author Administrator
 */
public class UserAccountQuery extends PayModel {

	private String userId;
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","userId"};
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	//返回参数设置
	private String freezeAmount;
	private String certifyStatus;
	private String runtimeStatus;
	private String licenseValidTime;
	private String currency;
	private String balance;
	private String resultCode;
	private String userName;
	private String creditBalance;
	private String paymodel;
	private String status;
	private String titleName;
	private String accountNo;
	private String resultMessage;
	private String email;
	private String accountTitleId;
	private String accountAlias;
	private String registerFrom;
	private String userStatus;
	private String accountType;
	private String creditAmount;
	private String realName; 
	private String systemAmount;
	private String channelId;
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAccountTitleId() {
		return accountTitleId;
	}
	public void setAccountTitleId(String accountTitleId) {
		this.accountTitleId = accountTitleId;
	}
	public String getAccountAlias() {
		return accountAlias;
	}
	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}
	public String getRegisterFrom() {
		return registerFrom;
	}
	public void setRegisterFrom(String registerFrom) {
		this.registerFrom = registerFrom;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getSystemAmount() {
		return systemAmount;
	}
	public void setSystemAmount(String systemAmount) {
		this.systemAmount = systemAmount;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public UserAccountQuery(){
		super();
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFreezeAmount() {
		return freezeAmount;
	}
	public void setFreezeAmount(String freezeAmount) {
		this.freezeAmount = freezeAmount;
	}
	public String getCertifyStatus() {
		return certifyStatus;
	}
	public void setCertifyStatus(String certifyStatus) {
		this.certifyStatus = certifyStatus;
	}
	public String getRuntimeStatus() {
		return runtimeStatus;
	}
	public void setRuntimeStatus(String runtimeStatus) {
		this.runtimeStatus = runtimeStatus;
	}
	public String getLicenseValidTime() {
		return licenseValidTime;
	}
	public void setLicenseValidTime(String licenseValidTime) {
		this.licenseValidTime = licenseValidTime;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreditBalance() {
		return creditBalance;
	}
	public void setCreditBalance(String creditBalance) {
		this.creditBalance = creditBalance;
	}
	public String getPaymodel() {
		return paymodel;
	}
	public void setPaymodel(String paymodel) {
		this.paymodel = paymodel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	
	
}
