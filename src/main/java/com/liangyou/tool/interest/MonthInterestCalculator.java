package com.liangyou.tool.interest;

import java.util.ArrayList;
import java.util.List;

import com.liangyou.util.NumberUtils;

/**
 * 等额还息计算工具类
 * 
 * @author fuxingxing
 * @date 2012-9-7 下午2:45:46
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class MonthInterestCalculator implements InterestCalculator{
	private double account;
	private double apr;
	private int period;
	private double moneyPerMonth;
	private String eachString;
	private List monthList;
	private double remainRepayment;
	
	public MonthInterestCalculator() {
		this(0.0,0.0,0);
	}
	
	public MonthInterestCalculator(double account,double apr, int period) {
		super();
		this.account = NumberUtils.format2(account);
		this.apr = apr/12;
		this.period = period;
		monthList=new ArrayList();
	}
	public MonthInterestCalculator(double account,double apr, int period, double remainRepayment) {
		super();
		this.account = NumberUtils.format2(account);
		this.apr = apr/12;
		this.period = period;
		monthList=new ArrayList();
		this.remainRepayment = remainRepayment;
	}
	
	@Override
	public double getAccount() {
		return this.account;
	}
	
	@Override
	public double getTotalAccount() {
		if(remainRepayment>0){
			return remainRepayment;
		}else{
			return NumberUtils.format2(account*apr*period+account);
		}
	}

	@Override
	public List each(){
		//总共需要还款金额
		double totalRemain= NumberUtils.format2(account*apr*period+account);
		//每期还款后剩余金额
		double remain= NumberUtils.format2(totalRemain-account*apr);
		//每期需要还款中的本金
		double accountPerMon=0.0;
		//每期需要还款中的利息
		double interest=0.0;
		//已经计算过的所有的本金
		double totalPreInterest=0.0;
		//用于控制台输出的字符串
		StringBuffer sb=new StringBuffer("");
		sb.append("Total Money:"+totalRemain);
		sb.append("\n");
		sb.append("Month Apr:"+apr);
		sb.append("\n");
		
		//循环计算accountPerMon、interest、totalRemain
		for(int i=0;i<period;i++){	
			//计算每月需要支付的利息
			interest=NumberUtils.format2(account*apr);
			moneyPerMonth= NumberUtils.format2(interest);
			//用于计算利息的剩余金额
			remain= NumberUtils.format2(totalRemain-moneyPerMonth);
			//计算每月需要还款中的本金
			accountPerMon=0;
			//实际支付的金额扣除本月已经支付的金额
			totalRemain= NumberUtils.format2(totalRemain-moneyPerMonth);
			if(i==period-1){ //最后一期的利息 = 总 - 前边之和
				interest = getTotalAccount() - totalPreInterest - account;
				accountPerMon=account;
				totalRemain=0;
			}else{
				totalPreInterest+=interest;
			}
			
			MonthInterest mi=new MonthInterest( NumberUtils.format2(accountPerMon),  
					NumberUtils.format2(interest),
					NumberUtils.format2(totalRemain));
			monthList.add(mi);
			
			sb.append("每月还钱:"+moneyPerMonth+" 月还款本金："+mi.getAccountPerMon()
					+" 利息："+mi.getInterest()+"  余额:"+mi.getTotalRemain());
			sb.append("\n");
		}
		eachString=sb.toString();
		return monthList;
	}
	
	@Override
	public String toString() {
		return this.eachString;
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

	public List getMonthList() {
		return monthList;
	}

	public void setMonthList(List monthList) {
		this.monthList = monthList;
	}

	public String eachDay() {
		//总共需要还款金额
		double totalRemain=account*(apr/12/30)*period+account;
		System.out.println(totalRemain);
		StringBuffer sb=new StringBuffer("");
		sb.append("Total Money:"+totalRemain);
		sb.append("\n");
		sb.append("Month Apr:"+apr);
		sb.append("\n");
		return "借款额度为："+account+"  到期需偿还"+totalRemain;
	}
	
	
}
