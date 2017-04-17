package com.liangyou.tool.interest;

import java.util.List;

public interface InterestCalculator {
	
	public int TYPE_DAY_END=1;
	public int TYPE_MONTH_END=2;
	public int TYPE_MONTH_EQUAL=3;
	public int TYPE_MONTH_INTEREST=4;
	
	/**
	 * 计算每期还款信息
	 * @return
	 */
	public List each();
	
	public double getMoneyPerMonth();
	
	public int getPeriod();
	
	public List getMonthList();
	
	public double getTotalAccount();
	public double getAccount();
	
	public String eachDay();
}
