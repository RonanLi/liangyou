package com.liangyou.tool.interest;
/**
 * 每月还利息，到期一次性还本金的还款方式计算器
 * @author fuxingxing
 * @date 2012-12-22 下午3:45:16
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class MonthInterest {
	//本金
	private double accountPerMon; 
	//利息
	private double interest;
	//剩余借款
	private double totalRemain;
	
	public MonthInterest() {
		super();
	}
	
	public MonthInterest(double accountPerMon, double interest,
			double totalRemain) {
		super();
		this.accountPerMon = accountPerMon;
		this.interest = interest;
		this.totalRemain = totalRemain;
	}
	public double getAccountPerMon() {
		return accountPerMon;
	}
	public void setAccountPerMon(double accountPerMon) {
		this.accountPerMon = accountPerMon;
	}
	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}
	public double getTotalRemain() {
		return totalRemain;
	}
	public void setTotalRemain(double totalRemain) {
		this.totalRemain = totalRemain;
	}

}
