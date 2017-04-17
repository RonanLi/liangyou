package com.liangyou.quartz.batchRecharge;

import com.liangyou.domain.AccountRecharge;
import com.liangyou.model.APIModel.RechargeModel;

/**
 * add by lijing 
 * 批量充值业务
 */
public class BatchReachargeBean {
	private String type;
	private AccountRecharge accountRecharge;
	private RechargeModel rechargeModel;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public AccountRecharge getAccountRecharge() {
		return accountRecharge;
	}
	public void setAccountRecharge(AccountRecharge accountRecharge) {
		this.accountRecharge = accountRecharge;
	}
	public RechargeModel getRechargeModel() {
		return rechargeModel;
	}
	public void setRechargeModel(RechargeModel rechargeModel) {
		this.rechargeModel = rechargeModel;
	}
	
}
