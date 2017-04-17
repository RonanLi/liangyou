package com.liangyou.model.account;

public class CollectSummary {
	private String collectTime;//最近的待收
	private double collectTotal;//待收总和
	private double collectInterestTotal;//待收利息总和
	private double collectMoney;//最近的待收本息
	private double collectInterest;//最近待收的利息
	private long user_id;
	public String getCollectTime() {
		return collectTime;
	}
	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}
	public double getCollectTotal() {
		return collectTotal;
	}
	public void setCollectTotal(double collectTotal) {
		this.collectTotal = collectTotal;
	}
	public double getCollectInterest() {
		return collectInterest;
	}
	public void setCollectInterest(double collectInterest) {
		this.collectInterest = collectInterest;
	}
	public double getCollectMoney() {
		return collectMoney;
	}
	public void setCollectMoney(double collectMoney) {
		this.collectMoney = collectMoney;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public double getCollectInterestTotal() {
		return collectInterestTotal;
	}
	public void setCollectInterestTotal(double collectInterestTotal) {
		this.collectInterestTotal = collectInterestTotal;
	}
	
}
