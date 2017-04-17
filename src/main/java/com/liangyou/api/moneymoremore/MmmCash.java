package com.liangyou.api.moneymoremore;

import com.liangyou.context.Global;

/**
 * 用户取现
 * 
 * @author Qinjun
 */
public class MmmCash extends MmmModel {

	private String withdrawMoneymoremore;//要提现的账号的乾多多标识
	private String amount;// 提现金额
	private String feePercent;//平台承担的手续费比例
	private String fee;//平台承担的手续费金额
	private String freeLimit;//平台扣除的免费提现额
	private String cardNo;//银行卡号
	private String cardType = "0";//银行卡类型   0.借记卡  1.信用卡
	private String bankCode;//银行id
	private String branchBankName;//开户行名称
	private String province;//开户行省份
	private String city;//开户行城市
	
	private String returnURL = Global.getString("weburl")+"/public/mmm/cashReturn.html";// 页面回调地址
	private String notifyURL = Global.getString("weburl")+"/public/mmm/cashNotify.html";// 后台回调地址
	
	//v1.8.0.4_u1 TGPROJECT-292 lx start
	private String randomTimeStamp;//随机时间戳
	private String remark1;//自定义备注
	private String remark2;//自定义备注
	private String remark3;//自定义备注
	private String feeMax;//用户承担的最高手续费
	private String feeWithdraws;//用户实际承担的手续费金额
	
	

	public String getFeeMax() {
		return feeMax;
	}

	public void setFeeMax(String feeMax) {
		this.feeMax = feeMax;
	}

	public String getFeeWithdraws() {
		return feeWithdraws;
	}

	public void setFeeWithdraws(String feeWithdraws) {
		this.feeWithdraws = feeWithdraws;
	}

	public String getRandomTimeStamp() {
		return randomTimeStamp;
	}

	public void setRandomTimeStamp(String randomTimeStamp) {
		this.randomTimeStamp = randomTimeStamp;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getRemark3() {
		return remark3;
	}

	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}

	//提交参数列表
	private String[] commitParamNames = new String[] {"WithdrawMoneymoremore","PlatformMoneymoremore","OrderNo","Amount","FeePercent","FeeMax","CardNo",
					"CardType","BankCode","BranchBankName","Province","City","RandomTimeStamp","Remark1","Remark2","Remark3","ReturnURL","NotifyURL"};
	//验证签名返回参数列表
	private String[] returnParamNames = new String[] {"WithdrawMoneymoremore","PlatformMoneymoremore","LoanNo","OrderNo","Amount",
					"FeeMax","FeeWithdraws","FeePercent","Fee","FreeLimit","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
	public MmmCash(int operation) {
		super(operation);
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getFreeLimit() {
		return freeLimit;
	}

	public void setFreeLimit(String freeLimit) {
		this.freeLimit = freeLimit;
	}

	public String getWithdrawMoneymoremore() {
		return withdrawMoneymoremore;
	}

	public void setWithdrawMoneymoremore(String withdrawMoneymoremore) {
		this.withdrawMoneymoremore = withdrawMoneymoremore;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFeePercent() {
		return feePercent;
	}

	public void setFeePercent(String feePercent) {
		this.feePercent = feePercent;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBranchBankName() {
		return branchBankName;
	}

	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getNotifyURL() {
		return notifyURL;
	}

	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}

	public String[] getCommitParamNames() {
		return commitParamNames;
	}

	public void setCommitParamNames(String[] commitParamNames) {
		this.commitParamNames = commitParamNames;
	}

	public String[] getReturnParamNames() {
		return returnParamNames;
	}

	public void setReturnParamNames(String[] returnParamNames) {
		this.returnParamNames = returnParamNames;
	}


	

}
