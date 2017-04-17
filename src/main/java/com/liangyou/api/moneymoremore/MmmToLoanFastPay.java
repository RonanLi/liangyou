package com.liangyou.api.moneymoremore;

import com.liangyou.context.Global;

/**双乾 认证、提现银行卡绑定、代扣授权三合一接口
 * //v1.8.0.4  TGPROJECT-382 wsl 2014-08-05 start
 * //v1.8.0.4  TGPROJECT-382 wsl 2014-08-05 end
 * @author wsl
 *
 */
public class MmmToLoanFastPay extends MmmModel{
	private String action;//1.用户认证2.提现银行卡绑定3.代扣授权4.取消代扣授权
	private String cardNo;//银行卡号
	private String cardType = "";//银行卡类型   0.借记卡  1.信用卡，双乾返回不能赋值
	private String bankCode;//银行id
	private String branchBankName;//开户行名称
	private String province;//开户行省份
	private String city;//开户行城市
	private String withholdBeginDate;//代扣开始日期 格式：yyyyMMdd 20140101
	private String withholdEndDate;//代扣结束日期
	private String singleWithholdLimit;//单笔代扣限额
	private String totalWithholdLimit;//代扣总限额
	
	private String randomTimeStamp;//随机时间戳
	private String remark1;//自定义备注
	private String remark2;//自定义备注
	private String remark3;//自定义备注
	
	private String returnURL = Global.getString("weburl")+"/public/mmm/loanfastpayReturn.html";// 页面回调地址
	private String notifyURL = Global.getString("weburl")+"/public/mmm/loanfastpayNotify.html";// 后台回调地址
	
	//提交参数列表
	private String[] commitParamNames = new String[] {"MoneymoremoreId","PlatformMoneymoremore","Action",
			"CardNo","WithholdBeginDate","WithholdEndDate","SingleWithholdLimit","TotalWithholdLimit",
			"RandomTimeStamp","Remark1","Remark2","Remark3","ReturnURL","NotifyURL"};
	//验证签名返回参数列表
	private String[] returnParamNames = new String[] {"MoneymoremoreId","PlatformMoneymoremore","Action",
			"CardType","BankCode","CardNo","BranchBankName","Province","City","WithholdBeginDate",
			"WithholdEndDate","SingleWithholdLimit","TotalWithholdLimit","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
	
	public MmmToLoanFastPay(int operation) {
		super(operation);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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

	public String getWithholdBeginDate() {
		return withholdBeginDate;
	}

	public void setWithholdBeginDate(String withholdBeginDate) {
		this.withholdBeginDate = withholdBeginDate;
	}

	public String getWithholdEndDate() {
		return withholdEndDate;
	}

	public void setWithholdEndDate(String withholdEndDate) {
		this.withholdEndDate = withholdEndDate;
	}

	public String getSingleWithholdLimit() {
		return singleWithholdLimit;
	}

	public void setSingleWithholdLimit(String singleWithholdLimit) {
		this.singleWithholdLimit = singleWithholdLimit;
	}

	public String getTotalWithholdLimit() {
		return totalWithholdLimit;
	}

	public void setTotalWithholdLimit(String totalWithholdLimit) {
		this.totalWithholdLimit = totalWithholdLimit;
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
