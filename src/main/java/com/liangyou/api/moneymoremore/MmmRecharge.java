package com.liangyou.api.moneymoremore;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.liangyou.context.Global;

/**
 * 用户充值
 * 
 * @author Qinjun
 */
public class MmmRecharge extends MmmModel {

	private String rechargeMoneymoremore;//要充值的账号的钱多多标识
	private String amount;// 充值金额

	private String rechargeType; // 充值方式3，汇款充值

	private String feeType; // 手续费类型

	private String fee; // 手续费

	private String feePlatform; // 平台承担手续费

	private String returnURL = Global.getString("weburl") + "/public/mmm/rechargeReturn.html";// 页面回调地址
	private String notifyURL = Global.getString("weburl") + "/public/mmm/rechargeNotify.html";// 后台回调地址
	// 提交参数列表
	private String[] commitParamNames = new String[] { "rechargeMoneymoremore", "platformMoneymoremore", "orderNo", "amount", "rechargeType", "feeType", "returnURL", "notifyURL" };
	// 验证签名返回参数列表
	// private String[] returnParamNames = new String[] {"rechargeMoneymoremore","platformMoneymoremore","loanNo","orderNo","amount","resultCode"};
	private String[] returnParamNames = new String[] { "rechargeMoneymoremore", "platformMoneymoremore", "loanNo", "orderNo", "amount", "fee", "feePlatform", "rechargeType", "feeType", "resultCode" };

	public MmmRecharge(int operation) {
		super(operation);
	}

	public String getRechargeMoneymoremore() {
		return rechargeMoneymoremore;
	}

	public void setRechargeMoneymoremore(String rechargeMoneymoremore) {
		this.rechargeMoneymoremore = rechargeMoneymoremore;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
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

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getFeePlatform() {
		return feePlatform;
	}

	public void setFeePlatform(String feePlatform) {
		this.feePlatform = feePlatform;
	}

	public String getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	
	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString (this);
	}

}
