package com.liangyou.tool.interest;

import java.util.ArrayList;
import java.util.List;

import com.liangyou.util.NumberUtils;

public class EndInterestCalculator implements InterestCalculator {

	private double account;
	private double apr;
	private int period;
	private double moneyPerMonth;
	private String eachString;
	private List monthList;
	private double remainRepayment;
	
	public EndInterestCalculator() {
		this(0.0,0.0,0);
	}
	
	public EndInterestCalculator(double account,double apr, int day) {
		super();
		this.account = NumberUtils.format2(account);
		this.apr =apr*day/360;
		this.period = 1;
		monthList=new ArrayList();
	}
	
	public EndInterestCalculator(double account,double apr, int day, double remainRepayment) {
		super();
		this.account = NumberUtils.format2(account);
		this.apr =apr*day/360;
		this.period = 1;
		monthList=new ArrayList();
		this.remainRepayment = remainRepayment;
	}
	
	public EndInterestCalculator(double account,double apr, int period,int type) {
		super();
		this.account = NumberUtils.format2(account);
		if(type==InterestCalculator.TYPE_DAY_END){
			this.apr = apr*period/360;
		}else{
			this.apr = apr*period/12;
		}
		this.period = 1;
		monthList=new ArrayList();
	}
	
	public EndInterestCalculator(double account,double apr, int period,int type, double remainRepayment) {
		super();
		this.account = NumberUtils.format2(account);
		if(type==InterestCalculator.TYPE_DAY_END){
			this.apr = apr*period/360;
		}else{
			this.apr = apr*period/12;
		}
		this.period = 1;
		monthList=new ArrayList();
		this.remainRepayment = remainRepayment;
	}

	@Override
	public double getTotalAccount() {
		if(remainRepayment>0){
			return remainRepayment;
		}else{
			moneyPerMonth=NumberUtils.format2(account*(1+apr));
			return moneyPerMonth;
		}
	}

	@Override
	public List each() {
		double interest= NumberUtils.format2(getTotalAccount()-getAccount());
		StringBuffer sb=new StringBuffer("");
		sb.append("Total Money:"+account);
		sb.append("\n");
		sb.append("Month Apr:"+apr);
		sb.append("\n");
		sb.append("Month Money:"+moneyPerMonth);
		sb.append("\n");
		//每期需要还款中的本金
		double accountPerMon= NumberUtils.format2(getTotalAccount() - interest); 
		MonthInterest mi=new MonthInterest( accountPerMon,  interest,0);
		monthList.add(mi);
		
		sb.append("每期还钱:"+moneyPerMonth+" 期还款本金："+mi.getAccountPerMon()
				+" 利息："+mi.getInterest()+"  余额:"+mi.getTotalRemain());
		sb.append("\n");
		eachString=sb.toString();
		
		return monthList;
	}

	public double getAccount() {
		return account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public double getApr() {
		return apr;
	}

	public void setApr(double apr) {
		this.apr = apr;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public double getMoneyPerMonth() {
		return moneyPerMonth;
	}

	public void setMoneyPerMonth(double moneyPerMonth) {
		this.moneyPerMonth = moneyPerMonth;
	}

	public String getEachString() {
		return eachString;
	}

	public void setEachString(String eachString) {
		this.eachString = eachString;
	}

	public List getMonthList() {
		return monthList;
	}

	public void setMonthList(List monthList) {
		this.monthList = monthList;
	}

	@Override
	public String eachDay() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
