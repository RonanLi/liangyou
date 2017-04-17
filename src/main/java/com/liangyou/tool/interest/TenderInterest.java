package com.liangyou.tool.interest;

//动态计算利息的类
public class TenderInterest {
	

	private Double account;
	private Double lilv;
	private int isDay;//0天标1月标
	private int timas;
	private int time_limit_day;
	private String TenderType;//获得利息方式
	
	public TenderInterest() {
		// TODO Auto-generated constructor stub
	}
	
	public Double getAccount() {
		return account;
	}
	public void setAccount(Double account) {
		this.account = account;
	}
	public Double getLilv() {
		return lilv;
	}
	public void setLilv(Double lilv) {
		this.lilv = lilv;
	}
	
	
	public int getIsDay() {
		return isDay;
	}

	public void setIsDay(int isDay) {
		this.isDay = isDay;
	}

	public int getTimas() {
		return timas;
	}
	public void setTimas(int timas) {
		this.timas = timas;
	}
	public int getTime_limit_day() {
		return time_limit_day;
	}
	public void setTime_limit_day(int time_limit_day) {
		this.time_limit_day = time_limit_day;
	}
	public String getTenderType() {
		return TenderType;
	}
	public void setTenderType(String tenderType) {
		TenderType = tenderType;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
