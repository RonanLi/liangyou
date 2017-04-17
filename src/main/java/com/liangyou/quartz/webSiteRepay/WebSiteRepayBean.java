package com.liangyou.quartz.webSiteRepay;

import com.liangyou.model.BorrowParam;
/**
 * 1、还款给网站业务处理类
 * 2、充值有红包奖励，也放到此处处理【垫付业务不是很多】
 * @author zxc
 *
 */
public class WebSiteRepayBean {
	
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private long repaymentId; 
	
	private BorrowParam param;
	
	/**
	 * 用户充值奖励红包的id集合
	 */
	private String[] accountRechargeIds;
	
	public long getRepaymentId() {
		return repaymentId;
	}
	public void setRepaymentId(long repaymentId) {
		this.repaymentId = repaymentId;
	}
	public BorrowParam getParam() {
		return param;
	}
	public void setParam(BorrowParam param) {
		this.param = param;
	}
	public String[] getAccountRechargeIds() {
		return accountRechargeIds;
	}
	public void setAccountRechargeIds(String[] accountRechargeIds) {
		this.accountRechargeIds = accountRechargeIds;
	}

}
